# 12-http-session-attribute-listener

# Document: `javax.servlet.http.HttpSessionAttributeListener`

* **Package:** `javax.servlet.http`
* **Interface:** `public interface HttpSessionAttributeListener extends java.util.EventListener`
* **All Superinterfaces:** `java.util.EventListener`
* **Since:** Servlet 2.3
* **See Also:** `HttpSessionBindingEvent`, `HttpSession`

---

## 1. Tổng quan (Overview)

Interface `HttpSessionAttributeListener` dùng để lắng nghe và nhận các thông báo về việc thay đổi danh sách thuộc tính (attributes) của tất cả các `HttpSession` bên trong ứng dụng web (Web Application) hiện tại.

Khi một thuộc tính được **thêm mới**, **xóa bỏ**, hoặc **thay thế (ghi đè)** trong bất kỳ session nào, Servlet Container sẽ tự động gọi các phương thức tương ứng trong listener này để xử lý logic (ví dụ: ghi log, quản lý phiên người dùng toàn cục, audit trail).

---

## 2. Tóm tắt Phương thức (Method Summary)

| Kiểu trả về | Phương thức | Mô tả |
| --- | --- | --- |
| `void` | `attributeAdded(HttpSessionBindingEvent se)` | Được gọi sau khi một thuộc tính mới được thêm vào session. |
| `void` | `attributeRemoved(HttpSessionBindingEvent se)` | Được gọi sau khi một thuộc tính bị xóa khỏi session. |
| `void` | `attributeReplaced(HttpSessionBindingEvent se)` | Được gọi sau khi một thuộc tính trong session bị thay thế/ghi đè bởi giá trị mới. |

---

## 3. Chi tiết Tất cả các phương thức (Detail)

---

### 1. `attributeAdded(HttpSessionBindingEvent se)`

```java
public void attributeAdded(HttpSessionBindingEvent se)

```

* **Mô tả:** Được Servlet Container gọi **sau khi** một thuộc tính mới được thêm thành công vào session thông qua phương thức `session.setAttribute(name, value)`.
* **Parameters:** `se` - đối tượng `HttpSessionBindingEvent` chứa thông tin về session, tên thuộc tính (`getName()`) và giá trị thuộc tính vừa thêm vào (`getValue()`).

---

### 2. `attributeRemoved(HttpSessionBindingEvent se)`

```java
public void attributeRemoved(HttpSessionBindingEvent se)

```

* **Mô tả:** Được Servlet Container gọi **sau khi** một thuộc tính bị xóa khỏi session thông qua phương thức `session.removeAttribute(name)` hoặc `session.invalidate()`.
* **Parameters:** `se` - đối tượng `HttpSessionBindingEvent` chứa thông tin về session, tên thuộc tính bị xóa (`getName()`) và giá trị thuộc tính vừa bị xóa (`getValue()`).

---

### 3. `attributeReplaced(HttpSessionBindingEvent se)`

```java
public void attributeReplaced(HttpSessionBindingEvent se)

```

* **Mô tả:** Được Servlet Container gọi **sau khi** một thuộc tính trong session bị ghi đè/thay thế bằng giá trị mới thông qua phương thức `session.setAttribute(name, newValue)`.
* **Parameters:** `se` - đối tượng `HttpSessionBindingEvent` chứa thông tin về session, tên thuộc tính (`getName()`), và **giá trị cũ** của thuộc tính trước khi bị thay thế (`getValue()`).

---

## 4. Ví dụ triển khai mã nguồn thực tế (Code Examples)

### Ví dụ 1: Ghi Log Audit các sự kiện thay đổi Session Attribute

Tạo một Listener khai báo qua annotation `@WebListener` để theo dõi các thao tác biến động trong Session của người dùng.

```java
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;

@WebListener
public class SessionAttributeLoggerListener implements HttpSessionAttributeListener {

    @Override
    public void attributeAdded(HttpSessionBindingEvent se) {
        String sessionName = se.getName();
        Object sessionValue = se.getValue();
        String sessionId = se.getSession().getId();

        System.out.println("[SESSION ATTR ADDED] Session ID: " + sessionId 
                + " | Attribute: " + sessionName 
                + " | Value: " + sessionValue);
    }

    @Override
    public void attributeRemoved(HttpSessionBindingEvent se) {
        String sessionName = se.getName();
        Object removedValue = se.getValue();
        String sessionId = se.getSession().getId();

        System.out.println("[SESSION ATTR REMOVED] Session ID: " + sessionId 
                + " | Attribute: " + sessionName 
                + " | Removed Value: " + removedValue);
    }

    @Override
    public void attributeReplaced(HttpSessionBindingEvent se) {
        String sessionName = se.getName();
        Object oldValue = se.getValue(); // Lưu ý: getValue() trả về giá trị CŨ
        Object newValue = se.getSession().getAttribute(sessionName); // Giá trị MỚI
        String sessionId = se.getSession().getId();

        System.out.println("[SESSION ATTR REPLACED] Session ID: " + sessionId 
                + " | Attribute: " + sessionName 
                + " | Old Value: " + oldValue 
                + " -> New Value: " + newValue);
    }
}

```

---

### Ví dụ 2: Quản lý và Giám sát số lượng Người dùng Online trực tiếp

Sử dụng `HttpSessionAttributeListener` để tự động theo dõi danh sách các User đăng nhập và đăng xuất khỏi hệ thống dựa vào attribute `"currentUser"`.

```java
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@WebListener
public class ActiveUserTrackerListener implements HttpSessionAttributeListener {

    // Tập hợp chứa danh sách Username đang Online (Thread-safe)
    private static final Set<String> activeUsers = ConcurrentHashMap.newKeySet();

    @Override
    public void attributeAdded(HttpSessionBindingEvent se) {
        if ("currentUser".equals(se.getName())) {
            String username = (String) se.getValue();
            activeUsers.add(username);
            System.out.println("[ONLINE] User " + username + " đã đăng nhập. Số user online hiện tại: " + activeUsers.size());
        }
    }

    @Override
    public void attributeRemoved(HttpSessionBindingEvent se) {
        if ("currentUser".equals(se.getName())) {
            String username = (String) se.getValue();
            activeUsers.remove(username);
            System.out.println("[OFFLINE] User " + username + " đã đăng xuất/hết hạn session. Số user online còn lại: " + activeUsers.size());
        }
    }

    @Override
    public void attributeReplaced(HttpSessionBindingEvent se) {
        if ("currentUser".equals(se.getName())) {
            String oldUser = (String) se.getValue();
            String newUser = (String) se.getSession().getAttribute("currentUser");
            
            activeUsers.remove(oldUser);
            activeUsers.add(newUser);
            
            System.out.println("[USER SWITCHED] Thay đổi tài khoản đăng nhập từ: " + oldUser + " sang: " + newUser);
        }
    }

    // Phương thức tĩnh hỗ trợ các Controller/Servlet lấy số lượng user online
    public static Set<String> getActiveUsers() {
        return activeUsers;
    }
}

```

---