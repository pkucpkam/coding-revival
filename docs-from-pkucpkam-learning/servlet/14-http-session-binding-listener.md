# 14-http-session-binding-listener

# Document: `javax.servlet.http.HttpSessionBindingListener`

* **Package:** `javax.servlet.http`
* **Interface:** `public interface HttpSessionBindingListener extends java.util.EventListener`
* **All Superinterfaces:** `java.util.EventListener`
* **See Also:** `HttpSession`, `HttpSessionBindingEvent`

---

## 1. Tổng quan (Overview)

Interface `HttpSessionBindingListener` giúp một đối tượng Java tự nhận biết và nhận thông báo khi nó được **gắn vào (bound)** hoặc **ngắt khỏi (unbound)** một `HttpSession`. Thông báo được truyền thông qua đối tượng `HttpSessionBindingEvent`.

### Các trường hợp sự kiện xảy ra:

1. Lập trình viên Servlet chủ động gắn/ngắt thuộc tính bằng `session.setAttribute()` hoặc `session.removeAttribute()`.
2. Session bị hủy chủ động bằng phương thức `session.invalidate()`.
3. Session tự động hết hạn do quá thời gian chờ (session timing out).

> **Lưu ý sự khác biệt:** Không giống như `HttpSessionAttributeListener` (phải đăng ký trong `web.xml` hoặc `@WebListener` để lắng nghe mọi thuộc tính), `HttpSessionBindingListener` **không cần đăng ký toàn cục**. Bản thân class dữ liệu/model chỉ cần `implements` interface này, và Servlet Container sẽ tự động gọi phương thức tương ứng ngay khi chính đối tượng đó được đưa vào hoặc rút ra khỏi Session.

---

## 2. Tóm tắt Phương thức (Method Summary)

| Kiểu trả về | Phương thức | Mô tả |
| --- | --- | --- |
| `void` | `valueBound(HttpSessionBindingEvent event)` | Thông báo cho đối tượng biết nó vừa được gán vào một session. |
| `void` | `valueUnbound(HttpSessionBindingEvent event)` | Thông báo cho đối tượng biết nó vừa bị xóa/ngắt kết nối khỏi session. |

---

## 3. Chi tiết Tất cả các phương thức (Detail)

---

### 1. `valueBound(HttpSessionBindingEvent event)`

```java
public void valueBound(HttpSessionBindingEvent event)

```

* **Mô tả:** Được Servlet Container gọi để thông báo cho chính đối tượng này biết nó vừa được liên kết vào một `HttpSession`. Phương thức này chạy ngay sau khi `session.setAttribute(name, object)` hoàn tất.
* **Parameters:** `event` - đối tượng `HttpSessionBindingEvent` chứa thông tin về session, tên thuộc tính (`getName()`) và giá trị (`getValue()`).
* **See Also:** `valueUnbound(HttpSessionBindingEvent)`

---

### 2. `valueUnbound(HttpSessionBindingEvent event)`

```java
public void valueUnbound(HttpSessionBindingEvent event)

```

* **Mô tả:** Được Servlet Container gọi để thông báo cho đối tượng biết nó vừa bị ngắt liên kết khỏi `HttpSession`. Phương thức này chạy khi `removeAttribute()` được gọi, hoặc khi session bị `invalidate()`, hoặc khi session hết hạn (timeout).
* **Parameters:** `event` - đối tượng `HttpSessionBindingEvent` chứa thông tin về session vừa giải phóng đối tượng.
* **See Also:** `valueBound(HttpSessionBindingEvent)`

---

## 4. Ví dụ triển khai mã nguồn thực tế (Code Examples)

### Ví dụ 1: Quản lý đếm số lượng Session của User & Tự động Cleanup tài nguyên

Ví dụ một Model `UserSession` tự quản lý vòng đời của mình khi đưa vào Session.

```java
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

public class UserSession implements HttpSessionBindingListener {

    private String userId;
    private String username;

    public UserSession(String userId, String username) {
        this.userId = userId;
        this.username = username;
    }

    // 1. Được gọi tự động ngay sau khi đối tượng này được lưu vào Session bằng setAttribute()
    @Override
    public void valueBound(HttpSessionBindingEvent event) {
        System.out.println("[BINDING SUCCESS] User " + username + " (ID: " + userId + ") da duoc gán vao Session: " + event.getSession().getId());
        
        // Có thể thực hiện khởi tạo cache cá nhân hoặc ghi log truy cập tại đây
    }

    // 2. Được gọi tự động khi đối tượng bị gỡ khỏi Session (removeAttribute, invalidate, hoặc timeout)
    @Override
    public void valueUnbound(HttpSessionBindingEvent event) {
        System.out.println("[UNBINDING SUCCESS] User " + username + " da bi xoa/ngat ket noi khoi Session: " + event.getSession().getId());
        
        // Dọn dẹp tài nguyên cá nhân của User khi đăng xuất hoặc hết hạn phiên
    }

    public String getUsername() { return username; }
    public String getUserId() { return userId; }
}

```

---

### Ví dụ 2: Tích hợp vào Servlet xử lý Đăng nhập và Đăng xuất

Cách Servlet sử dụng Model triển khai `HttpSessionBindingListener` mà không cần cấu hình thêm Listener ở `web.xml`.

```java
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/auth-session-demo")
public class AuthSessionServlet extends HttpServlet {

    // Đăng nhập: Gán object triển khai HttpSessionBindingListener vao Session
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        String username = req.getParameter("username");
        HttpSession session = req.getSession(true);

        UserSession userModel = new UserSession("USR_999", username);

        // Ngay khi gọi setAttribute, phương thức userModel.valueBound() sẽ tự động kích hoạt
        session.setAttribute("userSession", userModel);

        resp.getWriter().println("Dang nhap thanh cong cho user: " + username);
    }

    // Đăng xuất: Xóa object hoặc Hủy Session
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        HttpSession session = req.getSession(false);
        if (session != null) {
            // Ngay khi gọi invalidate(), phương thức userModel.valueUnbound() sẽ tự động kích hoạt
            session.invalidate();
        }

        resp.getWriter().println("Da dang xuat va huy Session!");
    }
}

```

---