# 10-http-session

# Document: `javax.servlet.http.HttpSession`

* **Package:** `javax.servlet.http`
* **Interface:** `public interface HttpSession`
* **All Known Implementing Classes:** Custom Session Wrappers / Container-specific Session implementations
* **See Also:** `HttpSessionBindingListener`, `HttpSessionAttributeListener`, `HttpSessionActivationListener`, `HttpSessionContext`

---

## 1. Tổng quan (Overview)

Interface `HttpSession` cung cấp phương thức để xác định một người dùng qua nhiều lần gửi yêu cầu (request) hoặc nhiều lượt truy cập vào một trang Web, đồng thời cho phép lưu trữ thông tin về người dùng đó trong suốt phiên làm việc.

Servlet Container sử dụng interface này để tạo một phiên tương tác (**Session**) giữa HTTP Client và HTTP Server. Session tồn tại trong một khoảng thời gian nhất định qua nhiều kết nối hoặc request. Một session thường tương ứng với một người dùng truy cập trang web nhiều lần. Server có thể duy trì session theo nhiều cách như sử dụng **Cookies** hoặc **URL Rewriting**.

### Chức năng chính:

* **Quản lý thông tin Session:** Xem và thao tác các dữ liệu như *session ID, creation time (thời gian tạo)*, và *last accessed time (thời gian truy cập cuối)*.
* **Ràng buộc đối tượng (Attributes Binding):** Lưu giữ các đối tượng Java (như thông tin User, Giỏ hàng) xuyên suốt nhiều kết nối của một người dùng.
* **Xử lý sự kiện (Events & Listeners):**
* Khi lưu/xóa một đối tượng vào Session, nếu đối tượng đó triển khai `HttpSessionBindingListener`, container sẽ gửi thông báo `valueBound` hoặc `valueUnbound`.
* Trong môi trường phân tán (Distributed Container / Cluster), khi Session được chuyển giữa các JVM, các attribute triển khai `HttpSessionActivationListener` sẽ nhận được thông báo passivate/activate.


* **Client chưa gia nhập Session:** Nếu client tắt Cookie hoặc chưa chấp nhận Session, `isNew()` sẽ trả về `true`. Trong trường hợp client liên tục từ chối session, mỗi request gọi `getSession()` sẽ tạo ra một Session mới với `isNew() = true`.
* **Phạm vi (Scope):** Dữ liệu Session chỉ có phạm vi trong ứng dụng web hiện tại (`ServletContext`). Dữ liệu lưu ở context này không thể truy cập trực tiếp từ context khác.

---

## 2. Tóm tắt Phương thức (Method Summary)

| Kiểu trả về | Phương thức | Mô tả |
| --- | --- | --- |
| `Object` | `getAttribute(String name)` | Trả về đối tượng được lưu với tên chỉ định trong session, hoặc `null` nếu không có. |
| `Enumeration<String>` | `getAttributeNames()` | Trả về một `Enumeration` chứa tất cả các tên thuộc tính (attributes) đang lưu trong session. |
| `long` | `getCreationTime()` | Trả về thời điểm session được tạo (tính bằng ms từ 01/01/1970 GMT). |
| `String` | `getId()` | Trả về chuỗi định danh duy nhất (Session ID) được gán cho session này. |
| `long` | `getLastAccessedTime()` | Trả về thời điểm client gửi request cuối cùng liên kết với session này (ms từ 01/01/1970 GMT). |
| `int` | `getMaxInactiveInterval()` | Trả về khoảng thời gian không hoạt động tối đa (tính bằng **giây**) trước khi session bị hủy. |
| `ServletContext` | `getServletContext()` | Trả về đối tượng `ServletContext` mà session này thuộc về. |
| `HttpSessionContext` | `getSessionContext()` | *(Deprecated)* Đã không còn sử dụng từ Servlet 2.1. |
| `Object` | `getValue(String name)` | *(Deprecated)* Thay thế bằng `getAttribute(String)` từ phiên bản 2.2. |
| `String[]` | `getValueNames()` | *(Deprecated)* Thay thế bằng `getAttributeNames()` từ phiên bản 2.2. |
| `void` | `invalidate()` | Hủy bỏ session hiện tại và giải phóng/ngắt kết nối tất cả các đối tượng lưu trong đó. |
| `boolean` | `isNew()` | Trả về `true` nếu client chưa biết về session hoặc chọn không tham gia session. |
| `void` | `putValue(String name, Object value)` | *(Deprecated)* Thay thế bằng `setAttribute(String, Object)` từ phiên bản 2.2. |
| `void` | `removeAttribute(String name)` | Xóa đối tượng được lưu trữ với tên chỉ định khỏi session. |
| `void` | `removeValue(String name)` | *(Deprecated)* Thay thế bằng `removeAttribute(String)` từ phiên bản 2.2. |
| `void` | `setAttribute(String name, Object value)` | Lưu trữ một đối tượng vào session với tên chỉ định. |
| `void` | `setMaxInactiveInterval(int interval)` | Đặt khoảng thời gian chờ (tính bằng **giây**) giữa các request trước khi container hủy session. |

---

## 3. Chi tiết Tất cả các phương thức (Detail)

---

### 1. `getCreationTime()`

* **Cú pháp:** `public long getCreationTime()`
* **Mô tả:** Trả về thời điểm session được khởi tạo, đo bằng miligiây tính từ midnight 01/01/1970 GMT.
* **Returns:** Số nguyên kiểu `long` biểu diễn thời gian khởi tạo.
* **Throws:** `java.lang.IllegalStateException` nếu phương thức được gọi trên một session đã bị hủy (`invalidated`).

---

### 2. `getId()`

* **Cú pháp:** `public String getId()`
* **Mô tả:** Trả về chuỗi chứa mã định danh duy nhất (Session ID) do Servlet Container cấp phát cho session này.
* **Returns:** Chuỗi `String` chứa Session ID.
* **Throws:** `java.lang.IllegalStateException` nếu session đã bị hủy.

---

### 3. `getLastAccessedTime()`

* **Cú pháp:** `public long getLastAccessedTime()`
* **Mô tả:** Trả về thời điểm client gửi request cuối cùng đính kèm session ID này (tính bằng ms từ 01/01/1970 GMT). Các thao tác xử lý nội bộ trong server (như đọc/ghi attribute) không làm thay đổi mốc thời gian này.
* **Returns:** Số nguyên kiểu `long` biểu diễn thời gian truy cập cuối.
* **Throws:** `java.lang.IllegalStateException` nếu session đã bị hủy.

---

### 4. `getServletContext()`

* **Cú pháp:** `public ServletContext getServletContext()`
* **Mô tả:** Trả về tham chiếu tới đối tượng `ServletContext` đại diện cho ứng dụng web mà session này đang chạy bên trong.
* **Returns:** Đối tượng `ServletContext`.
* **Since:** Servlet 2.3

---

### 5. `setMaxInactiveInterval(int interval)`

* **Cú pháp:** `public void setMaxInactiveInterval(int interval)`
* **Mô tả:** Quy định thời gian tối đa (tính bằng **giây**) giữa các request từ client trước khi Servlet Container tự động hủy session. Nếu truyền vào một số âm (`< 0`), session sẽ không bao giờ bị quá hạn (timeout).
* **Parameters:** `interval` - số giây quy định thời gian sống tối đa không hoạt động.

---

### 6. `getMaxInactiveInterval()`

* **Cú pháp:** `public int getMaxInactiveInterval()`
* **Mô tả:** Trả về thời gian không hoạt động tối đa (tính bằng giây) trước khi session bị tự động hủy. Giá trị âm nghĩa là session không bao giờ hết hạn.
* **Returns:** Số nguyên `int` biểu diễn số giây.
* **See Also:** `setMaxInactiveInterval(int)`

---

### 7. `getSessionContext()`

* **Cú pháp:** `public HttpSessionContext getSessionContext()`
* **Mô tả:** *(Deprecated)* Đã không còn sử dụng từ phiên bản Servlet 2.1 và không có phương thức thay thế.

---

### 8. `getAttribute(String name)`

* **Cú pháp:** `public Object getAttribute(String name)`
* **Mô tả:** Lấy đối tượng đã được lưu trong session theo tên chỉ định. Trả về `null` nếu không tìm thấy.
* **Parameters:** `name` - tên thuộc tính cần lấy.
* **Returns:** Đối tượng `Object` ứng với tên chỉ định, hoặc `null`.
* **Throws:** `java.lang.IllegalStateException` nếu session đã bị hủy.

---

### 9. `getValue(String name)`

* **Mô tả:** *(Deprecated)* Thay thế bằng `getAttribute(String)` kể từ Servlet 2.2.
* **Throws:** `java.lang.IllegalStateException` nếu session đã bị hủy.

---

### 10. `getAttributeNames()`

* **Cú pháp:** `public java.util.Enumeration getAttributeNames()`
* **Mô tả:** Trả về danh sách tất cả các tên thuộc tính đang được gắn vào session dưới dạng `Enumeration` của các chuỗi `String`.
* **Returns:** Đối tượng `Enumeration` chứa tập hợp tên thuộc tính.
* **Throws:** `java.lang.IllegalStateException` nếu session đã bị hủy.

---

### 11. `getValueNames()`

* **Mô tả:** *(Deprecated)* Thay thế bằng `getAttributeNames()` kể từ Servlet 2.2.
* **Throws:** `java.lang.IllegalStateException` nếu session đã bị hủy.

---

### 12. `setAttribute(String name, Object value)`

* **Cú pháp:** `public void setAttribute(String name, Object value)`
* **Mô tả:** Lưu trữ một đối tượng vào session với tên tương ứng. Nếu đã có đối tượng trùng tên, đối tượng cũ sẽ bị ghi đè.
* Nếu `value` triển khai `HttpSessionBindingListener`, container sẽ gọi phương thức `valueBound()`.
* Nếu truyền `value = null`, phương thức có tác dụng tương đương với `removeAttribute(name)`.


* **Parameters:**
* `name`: Tên thuộc tính (không được `null`).
* `value`: Đối tượng cần lưu vào session.


* **Throws:** `java.lang.IllegalStateException` nếu session đã bị hủy.

---

### 13. `putValue(String name, Object value)`

* **Mô tả:** *(Deprecated)* Thay thế bằng `setAttribute(String, Object)` kể từ Servlet 2.2.
* **Throws:** `java.lang.IllegalStateException` nếu session đã bị hủy.

---

### 14. `removeAttribute(String name)`

* **Cú pháp:** `public void removeAttribute(String name)`
* **Mô tả:** Xóa đối tượng lưu trữ theo tên chỉ định khỏi session. Nếu không tìm thấy tên, phương thức không làm gì cả.
* Nếu đối tượng bị xóa triển khai `HttpSessionBindingListener`, container sẽ gọi phương thức `valueUnbound()`.


* **Parameters:** `name` - tên thuộc tính cần xóa.
* **Throws:** `java.lang.IllegalStateException` nếu session đã bị hủy.

---

### 15. `removeValue(String name)`

* **Mô tả:** *(Deprecated)* Thay thế bằng `removeAttribute(String)` kể từ Servlet 2.2.
* **Throws:** `java.lang.IllegalStateException` nếu session đã bị hủy.

---

### 16. `invalidate()`

* **Cú pháp:** `public void invalidate()`
* **Mô tả:** Hủy bỏ session hiện tại lập tức và ngắt liên kết (unbind) tất cả các đối tượng được lưu bên trong.
* **Throws:** `java.lang.IllegalStateException` nếu phương thức được gọi trên một session **đã bị hủy từ trước đó**.

---

### 17. `isNew()`

* **Cú pháp:** `public boolean isNew()`
* **Mô tả:** Trả về `true` nếu client chưa nhận biết được session (chưa gửi cookie chứa session ID lên) hoặc chọn từ chối session.
* **Returns:** `true` nếu server đã tạo session nhưng client chưa tham gia; `false` nếu client đã xác nhận session ID.
* **Throws:** `java.lang.IllegalStateException` nếu session đã bị hủy.

---

## 4. Ví dụ triển khai mã nguồn thực tế (Code Examples)

### Ví dụ 1: Quản lý đăng nhập & Lưu thông tin User trong Session

Xử lý đăng nhập, lưu thông tin User Model vào `HttpSession`, thiết lập thời gian hết hạn sau 30 phút.

```java
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.Serializable;

// Model lưu thông tin User (Nên implement Serializable để hỗ trợ Cluster Session)
class UserDTO implements Serializable {
    private String username;
    private String role;

    public UserDTO(String username, String role) {
        this.username = username;
        this.role = role;
    }

    public String getUsername() { return username; }
    public String getRole() { return role; }
}

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        String userParam = req.getParameter("username");
        String passParam = req.getParameter("password");

        // Kiểm tra thông tin đăng nhập (Giả lập)
        if ("admin".equals(userParam) && "123456".equals(passParam)) {
            
            // 1. Tạo mới hoặc lấy Session hiện tại
            HttpSession session = req.getSession(true);

            // 2. Tạo đối tượng User thông tin
            UserDTO user = new UserDTO("admin", "ADMINISTRATOR");

            // 3. Đưa đối tượng User vào Session với tên "currentUser"
            session.setAttribute("currentUser", user);

            // 4. Thiết lập thời gian hết hạn không hoạt động: 30 phút (30 * 60 = 1800 giây)
            session.setMaxInactiveInterval(30 * 60);

            resp.getWriter().println("Dang nhap thanh cong! Session ID: " + session.getId());
        } else {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Tai khoan hoac mat khau sai!");
        }
    }
}

```

---

### Ví dụ 2: Kiểm tra phiên và Xóa Session khi Đăng xuất (Logout)

Đọc thuộc tính từ Session để xác thực request và xóa hẳn Session khi người dùng chọn Đăng xuất.

```java
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        // Lấy session hiện tại, nếu chưa có thì KHÔNG tạo mới (truyền false)
        HttpSession session = req.getSession(false);

        if (session != null && session.getAttribute("currentUser") != null) {
            UserDTO user = (UserDTO) session.getAttribute("currentUser");

            resp.setContentType("text/html; charset=UTF-8");
            resp.getWriter().println("<h1>Welcome " + user.getUsername() + " (" + user.getRole() + ")</h1>");
            resp.getWriter().println("<p>Session Created At: " + session.getCreationTime() + "</p>");
            resp.getWriter().println("<p>Last Access Time: " + session.getLastAccessedTime() + "</p>");
        } else {
            // Chưa đăng nhập -> Chuyển hướng về trang Login
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        // Thao tác Đăng xuất (Logout)
        HttpSession session = req.getSession(false);
        if (session != null) {
            // Hủy bỏ toàn bộ Session
            session.invalidate();
        }

        resp.sendRedirect(req.getContextPath() + "/login.jsp?message=logout_success");
    }
}

```

---

### Ví dụ 3: Sử dụng `HttpSessionBindingListener` để theo dõi sự kiện liên kết

Tạo một class lắng nghe tự động khi đối tượng được thêm vào hoặc xóa khỏi Session.

```java
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

public class LoggedInUserTracker implements HttpSessionBindingListener {

    private String username;

    public LoggedInUserTracker(String username) {
        this.username = username;
    }

    @Override
    public void valueBound(HttpSessionBindingEvent event) {
        // Được gọi tự động SAU KHI setAttribute() thực thi xong
        System.out.println("[EVENT] User " + username + " da duoc LUU VAK Session: " + event.getSession().getId());
    }

    @Override
    public void valueUnbound(HttpSessionBindingEvent event) {
        // Được gọi tự động SAU KHI removeAttribute() hoặc invalidate() được gọi
        System.out.println("[EVENT] User " + username + " da bi XOA KHOI Session: " + event.getSession().getId());
    }
}

```