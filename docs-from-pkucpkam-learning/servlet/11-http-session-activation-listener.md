# 11-http-session-activation-listener

# Document: `javax.servlet.http.HttpSessionActivationListener`

* **Package:** `javax.servlet.http`
* **Interface:** `public interface HttpSessionActivationListener extends java.util.EventListener`
* **All Superinterfaces:** `java.util.EventListener`
* **Since:** Servlet 2.3
* **See Also:** `HttpSessionEvent`, `HttpSessionAttributeListener`

---

## 1. Tổng quan (Overview)

Các đối tượng được gắn (bound) vào một `HttpSession` có thể lắng nghe các sự kiện từ Servlet Container để nhận thông báo khi session chuẩn bị bị **vô hiệu hóa tạm thời (passivated)** hoặc vừa được **kích hoạt lại (activated)**.

Một Servlet Container thực hiện di chuyển session giữa các máy ảo Java (JVM/VM) trong môi trường phân tán (Cluster/Distributed Container) hoặc thực hiện ghi bền vững session xuống ổ đĩa (Persistence) bắt buộc phải gửi thông báo tới tất cả các thuộc tính (attributes) trong session có triển khai interface `HttpSessionActivationListener`.

---

### Khái niệm Passivation & Activation:

1. **Passivation (Vô hiệu hóa/Đóng đóng băng):** Quá trình chuyển trạng thái session từ bộ nhớ RAM xuống bộ nhớ phụ (như File trên đĩa cứng hoặc CSDL) để tiết kiệm bộ nhớ RAM, hoặc chuẩn bị gửi dữ liệu session qua mạng sang một máy chủ (Node) khác trong cụm Cluster.
2. **Activation (Kích hoạt lại/Giải đóng băng):** Quá trình đọc/tải lại dữ liệu session từ đĩa cứng hoặc từ máy chủ khác quay trở lại bộ nhớ RAM của JVM để tiếp tục phục vụ yêu cầu của người dùng.

---

## 2. Tóm tắt Phương thức (Method Summary)

| Kiểu trả về | Phương thức | Mô tả |
| --- | --- | --- |
| `void` | `sessionWillPassivate(HttpSessionEvent se)` | Được container gọi để thông báo rằng session chuẩn bị bị vô hiệu hóa (passivated). |
| `void` | `sessionDidActivate(HttpSessionEvent se)` | Được container gọi để thông báo rằng session vừa được kích hoạt lại (activated). |

---

## 3. Chi tiết Tất cả các phương thức (Detail)

---

### 1. `sessionWillPassivate(HttpSessionEvent se)`

```java
public void sessionWillPassivate(HttpSessionEvent se)

```

* **Mô tả:** Được container gọi ngay trước khi session bị đóng băng (passivated). Đây là thời điểm để đối tượng dọn dẹp các tài nguyên không thể tuần tự hóa (non-serializable resources) như open files, database connections, socket connection, v.v.
* **Parameters:** `se` - đối tượng `HttpSessionEvent` chứa thông tin về session sắp bị vô hiệu hóa.

---

### 2. `sessionDidActivate(HttpSessionEvent se)`

```java
public void sessionDidActivate(HttpSessionEvent se)

```

* **Mô tả:** Được container gọi ngay sau khi session vừa được kích hoạt lại (activated) trong bộ nhớ RAM. Đây là thời điểm thích hợp để khởi tạo lại hoặc khôi phục các kết nối/tài nguyên đã bị ngắt trước khi session bị passivate.
* **Parameters:** `se` - đối tượng `HttpSessionEvent` chứa thông tin về session vừa được kích hoạt.

---

## 4. Ví dụ triển khai mã nguồn thực tế (Code Examples)

### Ví dụ 1: Quản lý thuộc tính Không thể Tuần tự hóa (Non-Serializable Object Management)

Nhiều đối tượng trong Java (như `Connection`, `InputStream`, `Socket`) không triển khai `Serializable`. Nếu lưu các đối tượng này vào Session trong môi trường Cluster, container sẽ gặp lỗi khi ghi đĩa hoặc gửi qua mạng.

Sử dụng `HttpSessionActivationListener` giúp ta đóng kết nối trước khi Passivate và tạo lại kết nối sau khi Activate.

```java
import javax.servlet.http.HttpSessionActivationListener;
import javax.servlet.http.HttpSessionEvent;
import java.io.Serializable;

public class UserSessionData implements Serializable, HttpSessionActivationListener {

    private static final long serialVersionUID = 1L;

    private String userId;
    private String userName;

    // Thành phần transient không được tự động Serializable
    private transient Object tempResourceConnection;

    public UserSessionData(String userId, String userName) {
        this.userId = userId;
        this.userName = userName;
        this.initConnection();
    }

    private void initConnection() {
        // Giả lập mở một kết nối tạm thời hoặc tải đệm dữ liệu
        this.tempResourceConnection = "Active-Resource-Connection-For-" + userId;
        System.out.println("[INIT] Da khoi tao tai nguyen cho User: " + userId);
    }

    // 1. Trước khi Session bị đóng băng (Passivate) xuống đĩa/mạng
    @Override
    public void sessionWillPassivate(HttpSessionEvent se) {
        System.out.println("[PASSIVATE] Session " + se.getSession().getId() + " chuan bi bi ngung hoat dong (truyensang dia/VM khac).");
        
        // Dọn dẹp các tài nguyên transient để tránh lỗi NotSerializableException
        if (this.tempResourceConnection != null) {
            this.tempResourceConnection = null;
            System.out.println("[PASSIVATE] Da ngat ket noi tai nguyen tam thoi.");
        }
    }

    // 2. Sau khi Session được khôi phục (Activate) trở lại bộ nhớ RAM
    @Override
    public void sessionDidActivate(HttpSessionEvent se) {
        System.out.println("[ACTIVATE] Session " + se.getSession().getId() + " vua duoc kich hoat lai tren JVM.");
        
        // Khôi phục lại các kết nối/tài nguyên transient
        initConnection();
    }

    // Getter & Setter
    public String getUserId() { return userId; }
    public String getUserName() { return userName; }
}

```

---

### Ví dụ 2: Tích hợp vào Servlet để lưu trữ Session trong môi trường Cluster

```java
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/cluster-session-demo")
public class ClusterSessionServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        HttpSession session = req.getSession(true);

        UserSessionData userData = (UserSessionData) session.getAttribute("userData");

        if (userData == null) {
            userData = new UserSessionData("USR_1029", "Nguyen Van A");
            
            // Khi gán đối tượng vào Session, Container sẽ theo dõi sự kiện Activation nếu Session bị di chuyển
            session.setAttribute("userData", userData);
            resp.getWriter().println("Da tao moi UserSessionData va dua vao Session!");
        } else {
            resp.getWriter().println("Welcome back: " + userData.getUserName() + " (ID: " + userData.getUserId() + ")");
        }
    }
}

```

---