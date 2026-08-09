# 13-http-session-binding-event

# Document: `javax.servlet.http.HttpSessionBindingEvent`

* **Package:** `javax.servlet.http`
* **Class:** `public class HttpSessionBindingEvent extends HttpSessionEvent`
* **Inheritance:** `java.lang.Object` $\rightarrow$ `java.util.EventObject` $\rightarrow$ `javax.servlet.http.HttpSessionEvent` $\rightarrow$ `javax.servlet.http.HttpSessionBindingEvent`
* **All Implemented Interfaces:** `java.io.Serializable`
* **See Also:** `HttpSession`, `HttpSessionBindingListener`, `HttpSessionAttributeListener`

---

## 1. Tổng quan (Overview)

Các sự kiện thuộc kiểu `HttpSessionBindingEvent` sẽ được gửi tới:

1. Đối tượng triển khai interface **`HttpSessionBindingListener`** khi đối tượng đó được gắn vào (bound) hoặc ngắt khỏi (unbound) một `HttpSession`.
2. Đối tượng triển khai interface **`HttpSessionAttributeListener`** (được cấu hình trong file deployment descriptor hoặc `@WebListener`) khi có bất kỳ thuộc tính nào được gán, xóa bỏ, hoặc thay thế trong session.

Session gắn đối tượng bằng cách gọi phương thức `HttpSession.setAttribute` và ngắt liên kết đối tượng bằng phương thức `HttpSession.removeAttribute`.

---

## 2. Tóm tắt Field & Constructor (Summary)

### Field Summary

| Field (Inherited) | Mô tả |
| --- | --- |
| `protected Object source` | Đối tượng phát ra sự kiện (kế thừa từ `java.util.EventObject`). |

---

### Constructor Summary

| Constructor | Mô tả |
| --- | --- |
| `HttpSessionBindingEvent(HttpSession session, String name)` | Khởi tạo sự kiện thông báo cho một đối tượng biết nó vừa được gắn vào hoặc ngắt khỏi session. |
| `HttpSessionBindingEvent(HttpSession session, String name, Object value)` | Khởi tạo sự kiện kèm theo tên và giá trị của thuộc tính liên quan. |

---

## 3. Tóm tắt Phương thức (Method Summary)

| Kiểu trả về | Phương thức | Mô tả |
| --- | --- | --- |
| `String` | `getName()` | Trả về tên của thuộc tính vừa được gán hoặc ngắt khỏi session. |
| `HttpSession` | `getSession()` | Trả về đối tượng `HttpSession` xảy ra sự thay đổi (ghi đè từ `HttpSessionEvent`). |
| `Object` | `getValue()` | Trả về giá trị của thuộc tính đã thêm, bị xóa, hoặc giá trị cũ trước khi bị thay thế. |

---

## 4. Chi tiết Constructor & Tất cả các phương thức (Detail)

---

### Constructor Detail

#### 1. `HttpSessionBindingEvent(HttpSession session, String name)`

* **Cú pháp:** `public HttpSessionBindingEvent(HttpSession session, String name)`
* **Mô tả:** Khởi tạo sự kiện để thông báo cho đối tượng triển khai `HttpSessionBindingListener` biết nó đã được gán vào hoặc ngắt khỏi session.
* **Parameters:**
* `session`: Đối tượng `HttpSession` làm nguồn phát sự kiện.
* `name`: Tên dùng để gán đối tượng vào session.



---

#### 2. `HttpSessionBindingEvent(HttpSession session, String name, Object value)`

* **Cú pháp:** `public HttpSessionBindingEvent(HttpSession session, String name, Object value)`
* **Mô tả:** Khởi tạo sự kiện kèm đầy đủ thông tin về session, tên thuộc tính và giá trị đối tượng.
* **Parameters:**
* `session`: Đối tượng `HttpSession` xảy ra sự kiện.
* `name`: Tên của thuộc tính.
* `value`: Giá trị tương ứng của thuộc tính.



---

### Method Detail

#### 1. `getSession()`

* **Cú pháp:** `public HttpSession getSession()`
* **Mô tả:** Trả về đối tượng `HttpSession` đã bị thay đổi thuộc tính.
* **Overrides:** `getSession()` trong lớp `HttpSessionEvent`.
* **Returns:** Đối tượng `HttpSession`.

---

#### 2. `getName()`

* **Cú pháp:** `public String getName()`
* **Mô tả:** Trả về chuỗi tên thuộc tính đã được gắn hoặc xóa khỏi session.
* **Returns:** Chuỗi `String` chứa tên thuộc tính.

---

#### 3. `getValue()`

* **Cú pháp:** `public Object getValue()`
* **Mô tả:** Trả về giá trị đối tượng của thuộc tính:
* **Khi thêm mới (added/bound):** Trả về giá trị của thuộc tính vừa được thêm.
* **Khi xóa (removed/unbound):** Trả về giá trị của thuộc tính vừa bị xóa.
* **Khi thay thế (replaced):** Trả về **giá trị cũ** của thuộc tính trước khi bị đè.


* **Returns:** Đối tượng `Object` ứng với giá trị thuộc tính.
* **Since:** Servlet 2.3

---

## 5. Ví dụ triển khai mã nguồn thực tế (Code Examples)

### Ví dụ 1: Sử dụng `HttpSessionBindingEvent` trong `HttpSessionBindingListener`

Tạo một đối tượng UserDTO có thể tự nhận biết khi nào mình được thêm vào hoặc xóa khỏi Session bằng cách đọc `HttpSessionBindingEvent`.

```java
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

public class UserSessionModel implements HttpSessionBindingListener {

    private String userId;
    private String username;

    public UserSessionModel(String userId, String username) {
        this.userId = userId;
        this.username = username;
    }

    // 1. Tự động gọi khi được gắn vào Session (setAttribute)
    @Override
    public void valueBound(HttpSessionBindingEvent event) {
        System.out.println("[BOUND EVENT] User " + username + " (ID: " + userId + ")");
        System.out.println("  - Attribute Name: " + event.getName());
        System.out.println("  - Session ID: " + event.getSession().getId());
    }

    // 2. Tự động gọi khi bị xóa khỏi Session (removeAttribute hoặc invalidate)
    @Override
    public void valueUnbound(HttpSessionBindingEvent event) {
        System.out.println("[UNBOUND EVENT] User " + username + " da bi xoa khoi Session.");
        System.out.println("  - Attribute Name: " + event.getName());
        System.out.println("  - Value bi remove: " + event.getValue());
    }

    public String getUsername() { return username; }
}

```

---

### Ví dụ 2: Sử dụng `HttpSessionBindingEvent` trong `HttpSessionAttributeListener`

Sử dụng `HttpSessionBindingEvent` để lấy thông tin chi tiết (Tên attribute, Giá trị cũ/mới, Session ID) trong một Listener giám sát biến động dữ liệu.

```java
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;

@WebListener
public class AttributeMonitorListener implements HttpSessionAttributeListener {

    @Override
    public void attributeAdded(HttpSessionBindingEvent event) {
        System.out.println("[ADD] Attribute Created:");
        System.out.println("  - Name: " + event.getName());
        System.out.println("  - Value: " + event.getValue());
        System.out.println("  - Target Session: " + event.getSession().getId());
    }

    @Override
    public void attributeRemoved(HttpSessionBindingEvent event) {
        System.out.println("[REMOVE] Attribute Deleted:");
        System.out.println("  - Name: " + event.getName());
        System.out.println("  - Removed Value: " + event.getValue());
    }

    @Override
    public void attributeReplaced(HttpSessionBindingEvent event) {
        System.out.println("[UPDATE] Attribute Value Changed:");
        System.out.println("  - Name: " + event.getName());
        // event.getValue() tra ve gia tri CU trước khi đè
        System.out.println("  - Old Value: " + event.getValue()); 
        // Lấy giá trị MOI trực tiếp từ Session
        Object newValue = event.getSession().getAttribute(event.getName());
        System.out.println("  - New Value: " + newValue);
    }
}

```

---