# 25-servlet-context-event

# Document: `javax.servlet.ServletContextEvent`

* **Package:** `javax.servlet`
* **Class:** `public class ServletContextEvent extends java.util.EventObject`
* **Inheritance:** `java.lang.Object` $\rightarrow$ `java.util.EventObject` $\rightarrow$ `javax.servlet.ServletContextEvent`
* **All Implemented Interfaces:** `java.io.Serializable`
* **Direct Known Subclasses:** `javax.servlet.ServletContextAttributeEvent`
* **Since:** Servlet 2.3
* **See Also:** `javax.servlet.ServletContextListener`, `javax.servlet.ServletContext`

---

## 1. Tổng quan (Overview)

`ServletContextEvent` là lớp sự kiện đại diện cho các thông báo liên quan đến sự thay đổi trạng thái vòng đời của `ServletContext` trong một ứng dụng web (Web Application).

Đối tượng của lớp này được Servlet Container tự động khởi tạo và truyền làm tham số cho các phương thức của **`ServletContextListener`** khi:

1. Ứng dụng web bắt đầu được khởi chạy (**Khởi tạo Context**).
2. Ứng dụng web chuẩn bị bị dừng hoặc bị gỡ bỏ (**Phá hủy Context**).

---

## 2. Tóm tắt Field & Constructor (Summary)

### Field Summary

| Field (Inherited) | Mô tả |
| --- | --- |
| `protected Object source` | Đối tượng phát ra sự kiện, kế thừa từ `java.util.EventObject` (ở đây chính là đối tượng `ServletContext`). |

---

### Constructor Summary

| Constructor | Mô tả |
| --- | --- |
| `ServletContextEvent(ServletContext source)` | Khởi tạo một đối tượng sự kiện `ServletContextEvent` từ `ServletContext` nguồn. |

---

## 3. Tóm tắt Phương thức (Method Summary)

| Kiểu trả về | Phương thức | Mô tả |
| --- | --- | --- |
| `ServletContext` | `getServletContext()` | Trả về đối tượng `ServletContext` phát ra sự kiện. |

---

## 4. Chi tiết Constructor & Tất cả các phương thức (Detail)

---

### Constructor Detail

#### `ServletContextEvent(ServletContext source)`

```java
public ServletContextEvent(ServletContext source)

```

* **Mô tả:** Khởi tạo một đối tượng `ServletContextEvent` từ `ServletContext` truyền vào.
* **Parameters:** `source` - đối tượng `ServletContext` gửi sự kiện này.
* **Throws:** `java.lang.IllegalArgumentException` nếu `source` là `null`.

---

### Method Detail

#### `getServletContext()`

```java
public ServletContext getServletContext()

```

* **Mô tả:** Lấy đối tượng `ServletContext` nơi diễn ra sự kiện thay đổi vòng đời. Phương thức này ép kiểu kết quả của `getSource()` từ `java.util.EventObject` về kiểu `ServletContext`.
* **Returns:** Đối tượng `ServletContext` đã phát ra sự kiện.

---

## 5. Ví dụ triển khai mã nguồn thực tế (Code Examples)

### Ví dụ 1: Sử dụng `ServletContextEvent` trong `ServletContextListener` để khởi tạo kết nối CSDL khi Web App khởi chạy

Đoạn mã dưới đây minh họa việc nhận `ServletContextEvent` khi ứng dụng web bắt đầu hoặc dừng hẳn để quản lý vòng đời của Database Connection Pool.

```java
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class DatabaseInitializerListener implements ServletContextListener {

    // 1. Được gọi khi Web Application bắt đầu khởi chạy
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // Lấy ServletContext từ ServletContextEvent
        ServletContext context = sce.getServletContext();
        
        context.log("[STARTUP] Web Application đang khởi chạy...");
        System.out.println("Tên ứng dụng: " + context.getServletContextName());

        // Giả lập khởi tạo Database Connection Pool
        String dbUrl = context.getInitParameter("dbUrl");
        if (dbUrl == null) {
            dbUrl = "jdbc:mysql://localhost:3306/mydb";
        }
        
        // Lưu Connection Pool hoặc DataSource vào ServletContext (Application Scope)
        context.setAttribute("DB_URL", dbUrl);
        System.out.println("[STARTUP] Đã cấu hình CSDL thành công: " + dbUrl);
    }

    // 2. Được gọi khi Web Application chuẩn bị dừng (Shutdown server hoặc Undeploy)
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();
        
        context.log("[SHUTDOWN] Web Application chuẩn bị dừng...");

        // Giải phóng tài nguyên
        context.removeAttribute("DB_URL");
        System.out.println("[SHUTDOWN] Đã đóng tất cả các kết nối CSDL an toàn.");
    }
}

```

---

### Ví dụ 2: Lấy thông tin cấu hình từ `web.xml` thông qua `ServletContextEvent`

Đọc tham số cấu hình tĩnh phạm vi toàn cục từ file `web.xml` tại thời điểm khởi tạo Web Application.

#### File cấu hình `web.xml`:

```xml
<web-app xmlns="http://java.sun.com/xml/ns/javaee" version="2.5">
    <context-param>
        <param-name>environment</param-name>
        <param-value>PRODUCTION</param-value>
    </context-param>
    
    <listener>
        <listener-class>com.example.listener.AppEnvListener</listener-class>
    </listener>
</web-app>

```

#### Lớp Java triển khai (`AppEnvListener.java`):

```java
package com.example.listener;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class AppEnvListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // Đọc thông tin từ ServletContext thông qua đối tượng sự kiện
        ServletContext context = sce.getServletContext();
        String env = context.getInitParameter("environment");

        System.out.println("-> Môi trường ứng dụng hiện tại: " + env);
        context.setAttribute("CURRENT_ENV", env);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("-> Ứng dụng đã giải phóng tài nguyên môi trường.");
    }
}

```

---