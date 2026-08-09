# 17-http-session-listener

# Document: `javax.servlet.http.HttpSessionListener`

* **Package:** `javax.servlet.http`
* **Interface:** `public interface HttpSessionListener extends java.util.EventListener`
* **All Superinterfaces:** `java.util.EventListener`
* **Since:** Servlet 2.3
* **See Also:** `javax.servlet.http.HttpSessionEvent`

---

## 1. Tổng quan (Overview)

Interface `HttpSessionListener` nhận các thông báo về sự thay đổi danh sách các session đang hoạt động (`active sessions`) trong một ứng dụng web.

Để nhận được các sự kiện thông báo này, lớp triển khai bắt buộc phải được cấu hình trong deployment descriptor (`web.xml`) của ứng dụng web hoặc khai báo bằng annotation `@WebListener`.

---

## 2. Tóm tắt Phương thức (Method Summary)

| Kiểu trả về | Phương thức | Mô tả |
| --- | --- | --- |
| `void` | `sessionCreated(HttpSessionEvent se)` | Thông báo rằng một session vừa được tạo mới. |
| `void` | `sessionDestroyed(HttpSessionEvent se)` | Thông báo rằng một session chuẩn bị bị vô hiệu hóa (invalidated). |

---

## 3. Chi tiết Tất cả các phương thức (Detail)

---

### 1. `sessionCreated(HttpSessionEvent se)`

```java
public void sessionCreated(HttpSessionEvent se)

```

* **Mô tả:** Được Servlet Container gọi để thông báo rằng một `HttpSession` mới đã được tạo thành công trong ứng dụng web.
* **Parameters:** `se` - đối tượng `HttpSessionEvent` chứa thông tin về session mới tạo.

---

### 2. `sessionDestroyed(HttpSessionEvent se)`

```java
public void sessionDestroyed(HttpSessionEvent se)

```

* **Mô tả:** Được Servlet Container gọi để thông báo rằng một `HttpSession` chuẩn bị bị hủy (invalidated), do lập trình viên gọi `session.invalidate()` hoặc do session hết hạn tự động (timeout).
* **Parameters:** `se` - đối tượng `HttpSessionEvent` chứa thông tin về session chuẩn bị bị hủy.

---

## 4. Ví dụ triển khai mã nguồn thực tế (Code Examples)

### Ví dụ 1: Đếm số lượng Session/Người dùng đang truy cập hệ thống (Active Sessions Counter)

Ví dụ triển khai `HttpSessionListener` bằng `@WebListener` để theo dõi và quản lý bộ đếm tổng số lượng session đang hoạt động.

```java
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.concurrent.atomic.AtomicInteger;

@WebListener
public class ActiveSessionCounterListener implements HttpSessionListener {

    // Bộ đếm an toàn trong môi trường đa luồng (Thread-safe)
    private static final AtomicInteger activeSessionCount = new AtomicInteger(0);

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        int count = activeSessionCount.incrementAndGet();
        System.out.println("[SESSION CREATED] ID: " + se.getSession().getId() 
                + " | Tổng active sessions: " + count);
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        int count = activeSessionCount.decrementAndGet();
        System.out.println("[SESSION DESTROYED] ID: " + se.getSession().getId() 
                + " | Tổng active sessions còn lại: " + count);
    }

    // Phương thức tĩnh để các Servlet/JSP gọi đến xem số lượng online
    public static int getActiveSessionCount() {
        return activeSessionCount.get();
    }
}

```

---

### Ví dụ 2: Khai báo qua file `web.xml` và thiết lập thuộc tính mặc định khi tạo Session

Trong trường hợp ứng dụng sử dụng file cấu hình `web.xml` thay vì `@WebListener`.

#### Cấu hình trong `web.xml`:

```xml
<web-app xmlns="http://java.sun.com/xml/ns/javaee" version="2.5">
    <listener>
        <listener-class>com.example.listener.AppSessionInitializerListener</listener-class>
    </listener>
</web-app>

```

#### Lớp Java triển khai (`AppSessionInitializerListener.java`):

```java
package com.example.listener;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

public class AppSessionInitializerListener implements HttpSessionListener {

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        HttpSession session = se.getSession();
        
        // Cấu hình mặc định cho tất cả session khi mới khởi tạo
        session.setMaxInactiveInterval(15 * 60); // Timeout sau 15 phút
        session.setAttribute("cart_items_count", 0);
        
        System.out.println("Đã khởi tạo thuộc tính mặc định cho Session: " + session.getId());
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        HttpSession session = se.getSession();
        
        // Log vết trước khi session bị xóa hẳn khỏi bộ nhớ
        Object user = session.getAttribute("currentUser");
        if (user != null) {
            System.out.println("Session của User " + user + " đã bị hủy.");
        }
    }
}

```