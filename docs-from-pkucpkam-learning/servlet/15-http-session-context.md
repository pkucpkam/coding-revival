# 15-http-session-context

# Document: `javax.servlet.http.HttpSessionContext`

* **Package:** `javax.servlet.http`
* **Interface:** `public interface HttpSessionContext`
* **Deprecated:** Kể từ Java Servlet API 2.1 vì lý do bảo mật, **không có phương thức thay thế**. Interface này bị bãi bỏ và sẽ bị xóa khỏi các phiên bản Servlet API tương lai.
* **See Also:** `HttpSession`, `HttpSessionBindingEvent`, `HttpSessionBindingListener`

---

## 1. Tổng quan (Overview)

Interface `HttpSessionContext` từng được thiết kế để cung cấp khả năng truy cập tới toàn bộ các `HttpSession` đang hoạt động trong Servlet Container thông qua mã định danh `sessionId`.

Tuy nhiên, vì lý do **bảo mật nghiêm trọng** (cho phép một session này đọc/can thiệp vào session của người dùng khác), interface này đã bị **bãi bỏ (Deprecated)** từ phiên bản **Servlet 2.1** và tất cả phương thức bên trong đều bị vô hiệu hóa hoàn toàn (trả về `null` hoặc tập hợp rỗng).

---

## 2. Tóm tắt Phương thức (Method Summary)

| Kiểu trả về | Phương thức | Mô tả |
| --- | --- | --- |
| `Enumeration<String>` | `getIds()` | *(Deprecated)* Trả về danh sách tất cả các Session ID. Bắt buộc phải trả về một `Enumeration` rỗng. |
| `HttpSession` | `getSession(String sessionId)` | *(Deprecated)* Trả về đối tượng `HttpSession` theo ID. Bắt buộc phải trả về `null`. |

---

## 3. Chi tiết Tất cả các phương thức (Detail)

---

### 1. `getSession(String sessionId)`

```java
public HttpSession getSession(String sessionId)

```

* **Mô tả:** *(Deprecated)* Kể từ Java Servlet API 2.1, không có phương thức thay thế.
* **Hành vi bắt buộc:** Để đảm bảo tính bảo mật và tính tương thích ngược, các triển khai của phương thức này **bắt buộc phải trả về `null**`.
* **Parameters:** `sessionId` - chuỗi mã định danh của session.
* **Returns:** Luôn trả về `null`.

---

### 2. `getIds()`

```java
public java.util.Enumeration getIds()

```

* **Mô tả:** *(Deprecated)* Kể từ Java Servlet API 2.1, không có phương thức thay thế.
* **Hành vi bắt buộc:** Để đảm bảo an toàn dữ liệu giữa các phiên làm việc của người dùng, phương thức này **bắt buộc phải trả về một `Enumeration` rỗng**.
* **Returns:** Một đối tượng `Enumeration` rỗng không chứa bất kỳ phần tử nào.

---

## 4. Ví dụ giải thích & Giải pháp thay thế (Alternative Solutions)

### Lý do bị bãi bỏ (Deprecation Reason)

Trước phiên bản Servlet 2.1, lập trình viên có thể dùng `HttpSessionContext` để truy cập vào bất kỳ Session nào của ứng dụng:

```java
// Mã nguồn CŨ/ĐÃ BỊ CẤM (Vấn đề bảo mật):
HttpSessionContext context = session.getSessionContext();
HttpSession otherUserSession = context.getSession("SOME_OTHER_SESSION_ID"); 
// -> Một user có thể chiếm đoạt/đọc dữ liệu session của user khác!

```

---

### Giải pháp hiện đại: Tự quản lý Session bằng `HttpSessionListener`

Nếu ứng dụng của bạn thực sự cần quản lý hoặc theo dõi danh sách các `HttpSession` đang hoạt động (ví dụ: tính năng quản lý danh sách thiết bị đang đăng nhập, kick user khỏi hệ thống), hãy triển khai interface **`HttpSessionListener`** kết hợp với một `Map` lưu trữ Thread-safe.

#### Bước 1: Tạo Listener lưu trữ danh sách Session an toàn

```java
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@WebListener
public class ActiveSessionRegistry implements HttpSessionListener {

    // Bộ lưu trữ Session chủ động do lập trình viên tự kiểm soát (Thread-safe)
    private static final Map<String, HttpSession> activeSessions = new ConcurrentHashMap<>();

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        HttpSession session = se.getSession();
        activeSessions.put(session.getId(), session);
        System.out.println("[SESSION REGISTERED] ID: " + session.getId());
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        HttpSession session = se.getSession();
        activeSessions.remove(session.getId());
        System.out.println("[SESSION UNREGISTERED] ID: " + session.getId());
    }

    // Phương thức kiểm tra hoặc hủy Session của một user cụ thể
    public static HttpSession getSessionById(String sessionId) {
        return activeSessions.get(sessionId);
    }

    public static int getActiveSessionCount() {
        return activeSessions.size();
    }
}

```

#### Bước 2: Sử dụng trong Admin Controller / Servlet

```java
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/admin/session-manager")
public class SessionManagerServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        resp.setContentType("text/html; charset=UTF-8");

        // Lấy số lượng session hoạt động hiện tại thông qua Listener custom
        int totalActive = ActiveSessionRegistry.getActiveSessionCount();
        resp.getWriter().println("<h1>Tổng số phiên đang hoạt động: " + totalActive + "</h1>");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        // Thao tác Force Logout (Kick user) an toàn do Admin thực hiện
        String targetSessionId = req.getParameter("targetSessionId");
        
        HttpSession targetSession = ActiveSessionRegistry.getSessionById(targetSessionId);
        if (targetSession != null) {
            targetSession.invalidate(); // Hủy session của user bị chỉ định
            resp.getWriter().println("Đã hủy thành công phiên: " + targetSessionId);
        } else {
            resp.getWriter().println("Không tìm thấy phiên hoặc phiên đã hết hạn.");
        }
    }
}

```

---