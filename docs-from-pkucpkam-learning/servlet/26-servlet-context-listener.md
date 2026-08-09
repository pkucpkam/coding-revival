# 26-servlet-context-listener

# Document: `javax.servlet.ServletContextListener`

* **Package:** `javax.servlet`
* **Interface:** `public interface ServletContextListener extends java.util.EventListener`
* **All Superinterfaces:** `java.util.EventListener`
* **Since:** Servlet 2.3
* **See Also:** `javax.servlet.ServletContextEvent`, `javax.servlet.ServletContext`

---

## 1. Tổng quan (Overview)

Các lớp triển khai interface `ServletContextListener` sẽ nhận được thông báo về những thay đổi đối với vòng đời của `ServletContext` trong ứng dụng web mà chúng thuộc về.

Để nhận được các sự kiện thông báo này, lớp triển khai bắt buộc phải được cấu hình trong deployment descriptor (`web.xml`) của ứng dụng web hoặc khai báo bằng annotation `@WebListener`.

---

### Thứ tự thực thi quan trọng trong Vòng đời Web Application:

1. **Khởi tạo (`contextInitialized`):** Tất cả các `ServletContextListener` đều nhận được thông báo khởi tạo ngữ cảnh **TRƯỚC KHI** bất kỳ Filter hoặc Servlet nào trong ứng dụng web được khởi tạo.
2. **Phá hủy (`contextDestroyed`):** Tất cả các Filter và Servlet trong ứng dụng web đã được gọi hàm `destroy()` **TRƯỚC KHI** bất kỳ `ServletContextListener` nào nhận được thông báo phá hủy ngữ cảnh.

---

## 2. Tóm tắt Phương thức (Method Summary)

| Kiểu trả về | Phương thức | Mô tả |
| --- | --- | --- |
| `void` | `contextInitialized(ServletContextEvent sce)` | Thông báo rằng quá trình khởi tạo ứng dụng web đang bắt đầu. |
| `void` | `contextDestroyed(ServletContextEvent sce)` | Thông báo rằng ngữ cảnh servlet (`ServletContext`) chuẩn bị tắt/ngừng hoạt động. |

---

## 3. Chi tiết Tất cả các phương thức (Detail)

---

### 1. `contextInitialized(ServletContextEvent sce)`

```java
public void contextInitialized(ServletContextEvent sce)

```

* **Mô tả:** Được Servlet Container gọi để thông báo rằng quá trình khởi tạo ứng dụng web bắt đầu. Tất cả các `ServletContextListener` được thông báo **trước khi** bất kỳ Filter hay Servlet nào được khởi tạo (`init()`).
* **Parameters:** `sce` - đối tượng `ServletContextEvent` chứa thông tin về `ServletContext` vừa khởi tạo.

---

### 2. `contextDestroyed(ServletContextEvent sce)`

```java
public void contextDestroyed(ServletContextEvent sce)

```

* **Mô tả:** Được Servlet Container gọi để thông báo rằng `ServletContext` chuẩn bị bị tắt (shutdown server hoặc undeploy app). Tất cả các Servlet và Filter trong ứng dụng đã được gọi `destroy()` **trước khi** phương thức này được kích hoạt.
* **Parameters:** `sce` - đối tượng `ServletContextEvent` chứa thông tin về `ServletContext` chuẩn bị dừng.

---

## 4. Ví dụ triển khai mã nguồn thực tế (Code Examples)

### Ví dụ 1: Khai báo bằng Annotation `@WebListener` để Khởi tạo Database Connection Pool

Sử dụng `ServletContextListener` để khởi tạo các tài nguyên nặng (như Connection Pool, Redis Client, Background Scheduler) ngay khi ứng dụng web vừa bật lên.

```java
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class AppDatabaseInitializerListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();

        System.out.println("==================================================");
        System.out.println("[APP STARTUP] Bat dau khoi tao Web Application...");
        
        // 1. Đọc cấu hình từ web.xml hoặc biến môi trường
        String dbUrl = context.getInitParameter("dbUrl");
        if (dbUrl == null) {
            dbUrl = "jdbc:mysql://localhost:3306/production_db";
        }

        // 2. Giả lập khởi tạo Database Connection Pool dùng chung
        System.out.println("[APP STARTUP] Dang ket noi CSDL tai: " + dbUrl);
        context.setAttribute("DB_CONNECTION_URL", dbUrl);
        context.setAttribute("APP_STATUS", "READY");

        System.out.println("[APP STARTUP] Da san sang phuc vu Requests!");
        System.out.println("==================================================");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();

        System.out.println("==================================================");
        System.out.println("[APP SHUTDOWN] Web Application dang dung...");

        // Giải phóng tài nguyên toàn cục
        context.removeAttribute("DB_CONNECTION_URL");
        context.removeAttribute("APP_STATUS");

        System.out.println("[APP SHUTDOWN] Da dong tat ca ket noi CSDL an toan.");
        System.out.println("==================================================");
    }
}

```

---

### Ví dụ 2: Khai báo qua file `web.xml`

Trường hợp ứng dụng sử dụng file cấu hình XML truyền thống thay vì `@WebListener`.

#### Cấu hình trong `web.xml`:

```xml
<web-app xmlns="http://java.sun.com/xml/ns/javaee" version="2.5">
    
    <context-param>
        <param-name>logDirectory</param-name>
        <param-value>/var/logs/my-app/</param-value>
    </context-param>

    <listener>
        <listener-class>com.example.listener.LoggingSystemListener</listener-class>
    </listener>

</web-app>

```

#### Lớp Java triển khai (`LoggingSystemListener.java`):

```java
package com.example.listener;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class LoggingSystemListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();
        String logDir = context.getInitParameter("logDirectory");

        System.out.println("[LOG INIT] Dang thiet lap thu muc Log: " + logDir);
        context.log("LoggingSystemListener: He thong Log da duoc kich hoat.");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();
        context.log("LoggingSystemListener: He thong Log dang ngung hoat dong.");
        System.out.println("[LOG SHUTDOWN] Da flush toan bo Log va dong file.");
    }
}

```

---