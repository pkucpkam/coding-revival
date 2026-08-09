# 32-servlet-request-attribute-listener

# Document: `javax.servlet.ServletRequestAttributeListener`

* **Package:** `javax.servlet`
* **Interface:** `public interface ServletRequestAttributeListener extends java.util.EventListener`
* **All Superinterfaces:** `java.util.EventListener`
* **Since:** Servlet 2.4
* **See Also:** `javax.servlet.ServletRequestAttributeEvent`, `javax.servlet.ServletRequest`

---

## 1. Tổng quan (Overview)

Interface `ServletRequestAttributeListener` có thể được triển khai bởi lập trình viên muốn nhận các thông báo về sự thay đổi thuộc tính (attributes) trong phạm vi **Request Scope** (`ServletRequest`).

Các thông báo sự kiện sẽ được tự động phát ra trong suốt thời gian request nằm trong phạm vi (scope) của ứng dụng web mà listener được đăng ký:

* **Bắt đầu vào scope:** Ngay khi request chuẩn bị đi vào Servlet hoặc Filter đầu tiên trong ứng dụng web.
* **Kết thúc scope:** Khi request đi ra khỏi Servlet cuối cùng hoặc Filter đầu tiên trong chuỗi xử lý (chain).

Khi một thuộc tính trong `ServletRequest` được **thêm mới**, **xóa bỏ**, hoặc **thay thế (ghi đè)**, Servlet Container sẽ tự động gọi phương thức tương ứng trong listener này.

---

## 2. Tóm tắt Phương thức (Method Summary)

| Kiểu trả về | Phương thức | Mô tả |
| --- | --- | --- |
| `void` | `attributeAdded(ServletRequestAttributeEvent srae)` | Thông báo rằng một thuộc tính mới vừa được thêm vào `ServletRequest`. |
| `void` | `attributeRemoved(ServletRequestAttributeEvent srae)` | Thông báo rằng một thuộc tính hiện có vừa bị xóa khỏi `ServletRequest`. |
| `void` | `attributeReplaced(ServletRequestAttributeEvent srae)` | Thông báo rằng một thuộc tính trong `ServletRequest` vừa bị thay thế/ghi đè. |

---

## 3. Chi tiết Tất cả các phương thức (Detail)

---

### 1. `attributeAdded(ServletRequestAttributeEvent srae)`

```java
public void attributeAdded(ServletRequestAttributeEvent srae)

```

* **Mô tả:** Được Servlet Container gọi **sau khi** một thuộc tính mới được thêm thành công vào `ServletRequest` thông qua phương thức `request.setAttribute(name, value)`.
* **Parameters:** `srae` - đối tượng `ServletRequestAttributeEvent` chứa thông tin về request, tên thuộc tính (`getName()`) và giá trị thuộc tính vừa gán vào (`getValue()`).

---

### 2. `attributeRemoved(ServletRequestAttributeEvent srae)`

```java
public void attributeRemoved(ServletRequestAttributeEvent srae)

```

* **Mô tả:** Được Servlet Container gọi **sau khi** một thuộc tính bị xóa khỏi `ServletRequest` thông qua phương thức `request.removeAttribute(name)`.
* **Parameters:** `srae` - đối tượng `ServletRequestAttributeEvent` chứa thông tin về request, tên thuộc tính bị xóa (`getName()`) và giá trị thuộc tính vừa bị xóa (`getValue()`).

---

### 3. `attributeReplaced(ServletRequestAttributeEvent srae)`

```java
public void attributeReplaced(ServletRequestAttributeEvent srae)

```

* **Mô tả:** Được Servlet Container gọi **sau khi** một thuộc tính trong `ServletRequest` bị thay thế/ghi đè bằng giá trị mới thông qua phương thức `request.setAttribute(name, newValue)`.
* **Parameters:** `srae` - đối tượng `ServletRequestAttributeEvent` chứa thông tin về request, tên thuộc tính (`getName()`), và **giá trị CŨ** của thuộc tính trước khi bị thay thế (`getValue()`).

---

## 4. Ví dụ triển khai mã nguồn thực tế (Code Examples)

### Ví dụ 1: Sử dụng Annotation `@WebListener` để kiểm vết (Audit Log) dữ liệu Request Scope

Ghi nhận thông vết quá trình chuyển tiếp và đính kèm dữ liệu giữa các Controller/Filter trong cùng một Request lifecycle.

```java
import javax.servlet.ServletRequestAttributeEvent;
import javax.servlet.ServletRequestAttributeListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class RequestAttributeLoggerListener implements ServletRequestAttributeListener {

    @Override
    public void attributeAdded(ServletRequestAttributeEvent srae) {
        String name = srae.getName();
        Object value = srae.getValue();

        System.out.println("[REQUEST ATTR ADDED]");
        System.out.println("  - Name: " + name);
        System.out.println("  - Value: " + value);
    }

    @Override
    public void attributeRemoved(ServletRequestAttributeEvent srae) {
        String name = srae.getName();
        Object removedValue = srae.getValue();

        System.out.println("[REQUEST ATTR REMOVED]");
        System.out.println("  - Name: " + name);
        System.out.println("  - Removed Value: " + removedValue);
    }

    @Override
    public void attributeReplaced(ServletRequestAttributeEvent srae) {
        String name = srae.getName();
        // srae.getValue() trả về giá trị CŨ trước khi ghi đè
        Object oldValue = srae.getValue();
        // Lấy giá trị MỚI vừa cập nhật trực tiếp từ ServletRequest
        Object newValue = srae.getServletRequest().getAttribute(name);

        System.out.println("[REQUEST ATTR REPLACED]");
        System.out.println("  - Name: " + name);
        System.out.println("  - Old Value: " + oldValue);
        System.out.println("  - New Value: " + newValue);
    }
}

```

---

### Ví dụ 2: Khai báo qua file `web.xml`

Nếu dự án của bạn sử dụng cấu hình XML truyền thống thay vì `@WebListener`:

#### Cấu hình trong `web.xml`:

```xml
<web-app xmlns="http://java.sun.com/xml/ns/javaee" version="2.5">
    <listener>
        <listener-class>com.example.listener.RequestDataAuditListener</listener-class>
    </listener>
</web-app>

```

#### Lớp Java triển khai (`RequestDataAuditListener.java`):

```java
package com.example.listener;

import javax.servlet.ServletRequestAttributeEvent;
import javax.servlet.ServletRequestAttributeListener;

public class RequestDataAuditListener implements ServletRequestAttributeListener {

    @Override
    public void attributeAdded(ServletRequestAttributeEvent srae) {
        srae.getServletContext().log("Audit Request: Gán mới thuộc tính -> " + srae.getName());
    }

    @Override
    public void attributeRemoved(ServletRequestAttributeEvent srae) {
        srae.getServletContext().log("Audit Request: Xóa thuộc tính -> " + srae.getName());
    }

    @Override
    public void attributeReplaced(ServletRequestAttributeEvent srae) {
        srae.getServletContext().log("Audit Request: Thay thế thuộc tính -> " + srae.getName());
    }
}

```

---