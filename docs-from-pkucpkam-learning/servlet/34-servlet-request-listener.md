# 34-servlet-request-listener

# Document: `javax.servlet.ServletRequestListener`

* **Package:** `javax.servlet`
* **Interface:** `public interface ServletRequestListener extends java.util.EventListener`
* **All Superinterfaces:** `java.util.EventListener`
* **Since:** Servlet 2.4
* **See Also:** `javax.servlet.ServletRequestEvent`, `javax.servlet.ServletRequest`

---

## 1. Tổng quan (Overview)

Interface `ServletRequestListener` có thể được triển khai bởi lập trình viên muốn nhận thông báo khi các yêu cầu (`ServletRequest`) đi vào hoặc đi ra khỏi phạm vi (scope) của một web component trong ứng dụng web.

### Định nghĩa Phạm vi (Scope):

* **Đi vào phạm vi (`requestInitialized`):** Ngay khi request chuẩn bị đi vào Servlet hoặc Filter đầu tiên trong ứng dụng web.
* **Đi ra khỏi phạm vi (`requestDestroyed`):** Khi request đi ra khỏi Servlet cuối cùng hoặc Filter đầu tiên trong chuỗi xử lý (chain).

Interface này cực kỳ hữu ích cho các tác vụ mang tính chất ngang (cross-cutting concerns) như: ghi vết nhật ký truy cập (Access Logging), tính toán thời gian thực thi (Performance Monitoring), hoặc thiết lập/dọn dẹp dữ liệu ngữ cảnh luồng (ThreadLocal / MDC context).

---

## 2. Tóm tắt Phương thức (Method Summary)

| Kiểu trả về | Phương thức | Mô tả |
| --- | --- | --- |
| `void` | `requestInitialized(ServletRequestEvent sre)` | Thông báo rằng yêu cầu (`request`) chuẩn bị đi vào phạm vi xử lý của ứng dụng web. |
| `void` | `requestDestroyed(ServletRequestEvent sre)` | Thông báo rằng yêu cầu (`request`) chuẩn bị đi ra khỏi phạm vi xử lý của ứng dụng web. |

---

## 3. Chi tiết Tất cả các phương thức (Detail)

---

### 1. `requestInitialized(ServletRequestEvent sre)`

```java
public void requestInitialized(ServletRequestEvent sre)

```

* **Mô tả:** Được Servlet Container gọi khi một `ServletRequest` chuẩn bị bước vào phạm vi của ứng dụng web (trước khi vào bất kỳ Filter hay Servlet nào).
* **Parameters:** `sre` - đối tượng `ServletRequestEvent` chứa tham chiếu tới `ServletContext` và `ServletRequest` tương ứng.

---

### 2. `requestDestroyed(ServletRequestEvent sre)`

```java
public void requestDestroyed(ServletRequestEvent sre)

```

* **Mô tả:** Được Servlet Container gọi khi `ServletRequest` chuẩn bị rời khỏi phạm vi của ứng dụng web (sau khi kết thúc chuỗi Filter và Servlet).
* **Parameters:** `sre` - đối tượng `ServletRequestEvent` chứa tham chiếu tới `ServletContext` và `ServletRequest` tương ứng.

---

## 4. Ví dụ triển khai mã nguồn thực tế (Code Examples)

### Ví dụ 1: Khai báo bằng Annotation `@WebListener` để đo thời gian xử lý và Ghi log Request

Sử dụng `ServletRequestListener` kết hợp với `HttpServletRequest` để đo độ trễ (latency) của từng request từ client.

```java
import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpServletRequest;

@WebListener
public class RequestPerformanceListener implements ServletRequestListener {

    private static final String START_TIME_ATTR = "startTime";

    @Override
    public void requestInitialized(ServletRequestEvent sre) {
        ServletRequest request = sre.getServletRequest();
        long startTime = System.currentTimeMillis();

        // Lưu thời điểm bắt đầu vào Request Scope
        request.setAttribute(START_TIME_ATTR, startTime);

        if (request instanceof HttpServletRequest) {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            System.out.println("[REQUEST START] " + httpRequest.getMethod() 
                    + " " + httpRequest.getRequestURI() 
                    + " | IP: " + httpRequest.getRemoteAddr());
        }
    }

    @Override
    public void requestDestroyed(ServletRequestEvent sre) {
        ServletRequest request = sre.getServletRequest();
        Long startTime = (Long) request.getAttribute(START_TIME_ATTR);

        if (startTime != null) {
            long executionTime = System.currentTimeMillis() - startTime;
            if (request instanceof HttpServletRequest) {
                HttpServletRequest httpRequest = (HttpServletRequest) request;
                System.out.println("[REQUEST END] " + httpRequest.getRequestURI() 
                        + " | Execution Time: " + executionTime + " ms");
            }
        }
    }
}

```

---

### Ví dụ 2: Khai báo qua file `web.xml` để quản lý ThreadLocal Context

Nếu ứng dụng sử dụng cấu hình XML truyền thống và cần thiết lập dữ liệu luồng (`ThreadLocal`) cho từng HTTP request.

#### Cấu hình trong `web.xml`:

```xml
<web-app xmlns="http://java.sun.com/xml/ns/javaee" version="2.5">
    <listener>
        <listener-class>com.example.listener.ThreadLocalCleanListener</listener-class>
    </listener>
</web-app>

```

#### Lớp Java triển khai (`ThreadLocalCleanListener.java`):

```java
package com.example.listener;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;

public class ThreadLocalCleanListener implements ServletRequestListener {

    // ThreadLocal lưu requestId hoặc thông tin người dùng cho luồng hiện tại
    private static final ThreadLocal<String> requestIdHolder = new ThreadLocal<>();

    @Override
    public void requestInitialized(ServletRequestEvent sre) {
        // Tạo unique ID cho mỗi request
        String requestId = java.util.UUID.randomUUID().toString();
        requestIdHolder.set(requestId);
        
        sre.getServletContext().log("Bắt đầu xử lý Request ID: " + requestId);
    }

    @Override
    public void requestDestroyed(ServletRequestEvent sre) {
        sre.getServletContext().log("Hoàn tất xử lý Request ID: " + requestIdHolder.get());
        
        // Bắt buộc phải clear ThreadLocal để tránh rò rỉ bộ nhớ (Memory Leak) trong Thread Pool
        requestIdHolder.remove();
    }

    public static String getRequestId() {
        return requestIdHolder.get();
    }
}

```

---