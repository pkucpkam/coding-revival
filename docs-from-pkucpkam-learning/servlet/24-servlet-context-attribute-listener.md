# 24-servlet-context-attribute-listener

# Document: `javax.servlet.ServletContextAttributeListener`

* **Package:** `javax.servlet`
* **Interface:** `public interface ServletContextAttributeListener extends java.util.EventListener`
* **All Superinterfaces:** `java.util.EventListener`
* **Since:** Servlet 2.3
* **See Also:** `javax.servlet.ServletContextAttributeEvent`, `javax.servlet.ServletContext`

---

## 1. Tổng quan (Overview)

Interface `ServletContextAttributeListener` nhận các thông báo về sự thay đổi danh sách thuộc tính (attributes) trong `ServletContext` của một ứng dụng web (Web Application).

Để nhận được các sự kiện thông báo này, lớp triển khai bắt buộc phải được cấu hình trong deployment descriptor (`web.xml`) của ứng dụng web hoặc khai báo bằng annotation `@WebListener`.

Khi một thuộc tính phạm vi **Application Scope** (`ServletContext`) được **thêm mới**, **xóa bỏ**, hoặc **thay thế (ghi đè)**, Servlet Container sẽ tự động gọi các phương thức tương ứng trong listener này để xử lý logic (ví dụ: ghi log audit, cập nhật bộ đệm hệ thống toàn cục, đồng bộ cấu hình).

---

## 2. Tóm tắt Phương thức (Method Summary)

| Kiểu trả về | Phương thức | Mô tả |
| --- | --- | --- |
| `void` | `attributeAdded(ServletContextAttributeEvent scab)` | Thông báo rằng một thuộc tính mới vừa được thêm vào `ServletContext`. |
| `void` | `attributeRemoved(ServletContextAttributeEvent scab)` | Thông báo rằng một thuộc tính hiện có vừa bị xóa khỏi `ServletContext`. |
| `void` | `attributeReplaced(ServletContextAttributeEvent scab)` | Thông báo rằng một thuộc tính trong `ServletContext` vừa bị thay thế/ghi đè. |

---

## 3. Chi tiết Tất cả các phương thức (Detail)

---

### 1. `attributeAdded(ServletContextAttributeEvent scab)`

```java
public void attributeAdded(ServletContextAttributeEvent scab)

```

* **Mô tả:** Được Servlet Container gọi **sau khi** một thuộc tính mới được thêm thành công vào `ServletContext` thông qua phương thức `context.setAttribute(name, value)`.
* **Parameters:** `scab` - đối tượng `ServletContextAttributeEvent` chứa thông tin về context, tên thuộc tính (`getName()`) và giá trị thuộc tính vừa được thêm vào (`getValue()`).

---

### 2. `attributeRemoved(ServletContextAttributeEvent scab)`

```java
public void attributeRemoved(ServletContextAttributeEvent scab)

```

* **Mô tả:** Được Servlet Container gọi **sau khi** một thuộc tính bị xóa khỏi `ServletContext` thông qua phương thức `context.removeAttribute(name)`.
* **Parameters:** `scab` - đối tượng `ServletContextAttributeEvent` chứa thông tin về context, tên thuộc tính bị xóa (`getName()`) và giá trị thuộc tính vừa bị xóa (`getValue()`).

---

### 3. `attributeReplaced(ServletContextAttributeEvent scab)`

```java
public void attributeReplaced(ServletContextAttributeEvent scab)

```

* **Mô tả:** Được Servlet Container gọi **sau khi** một thuộc tính trong `ServletContext` bị thay thế/ghi đè bằng một giá trị mới thông qua phương thức `context.setAttribute(name, newValue)`.
* **Parameters:** `scab` - đối tượng `ServletContextAttributeEvent` chứa thông tin về context, tên thuộc tính (`getName()`), và **giá trị CŨ** của thuộc tính trước khi bị thay thế (`getValue()`).

---

## 4. Ví dụ triển khai mã nguồn thực tế (Code Examples)

### Ví dụ 1: Khai báo bằng Annotation `@WebListener` để theo dõi biến động Application Cache

Tạo một Listener để lắng nghe và ghi log theo thời gian thực mỗi khi có sự thay đổi dữ liệu cấu hình/cache ở phạm vi toàn ứng dụng.

```java
import javax.servlet.ServletContextAttributeEvent;
import javax.servlet.ServletContextAttributeListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class GlobalConfigListener implements ServletContextAttributeListener {

    @Override
    public void attributeAdded(ServletContextAttributeEvent scab) {
        String attrName = scab.getName();
        Object attrValue = scab.getValue();

        System.out.println("[GLOBAL ATTR ADDED]");
        System.out.println("  - Name: " + attrName);
        System.out.println("  - Value: " + attrValue);
    }

    @Override
    public void attributeRemoved(ServletContextAttributeEvent scab) {
        String attrName = scab.getName();
        Object removedValue = scab.getValue();

        System.out.println("[GLOBAL ATTR REMOVED]");
        System.out.println("  - Name: " + attrName);
        System.out.println("  - Removed Value: " + removedValue);
    }

    @Override
    public void attributeReplaced(ServletContextAttributeEvent scab) {
        String attrName = scab.getName();
        // scab.getValue() trả về giá trị CŨ trước khi ghi đè
        Object oldValue = scab.getValue(); 
        // Lấy giá trị MỚI vừa được cập nhật trực tiếp từ ServletContext
        Object newValue = scab.getServletContext().getAttribute(attrName);

        System.out.println("[GLOBAL ATTR REPLACED]");
        System.out.println("  - Name: " + attrName);
        System.out.println("  - Old Value: " + oldValue);
        System.out.println("  - New Value: " + newValue);
    }
}

```

---

### Ví dụ 2: Khai báo qua file `web.xml`

Nếu dự án của bạn sử dụng cấu hình file `web.xml` thay vì Annotation:

#### Cấu hình trong `web.xml`:

```xml
<web-app xmlns="http://java.sun.com/xml/ns/javaee" version="2.5">
    <listener>
        <listener-class>com.example.listener.AppAttributeAuditListener</listener-class>
    </listener>
</web-app>

```

#### Lớp Java triển khai (`AppAttributeAuditListener.java`):

```java
package com.example.listener;

import javax.servlet.ServletContextAttributeEvent;
import javax.servlet.ServletContextAttributeListener;

public class AppAttributeAuditListener implements ServletContextAttributeListener {

    @Override
    public void attributeAdded(ServletContextAttributeEvent scab) {
        scab.getServletContext().log("Audit: Thêm thuộc tính mới vào Context -> " + scab.getName());
    }

    @Override
    public void attributeRemoved(ServletContextAttributeEvent scab) {
        scab.getServletContext().log("Audit: Xóa thuộc tính khỏi Context -> " + scab.getName());
    }

    @Override
    public void attributeReplaced(ServletContextAttributeEvent scab) {
        scab.getServletContext().log("Audit: Cập nhật giá trị thuộc tính trong Context -> " + scab.getName());
    }
}

```

---