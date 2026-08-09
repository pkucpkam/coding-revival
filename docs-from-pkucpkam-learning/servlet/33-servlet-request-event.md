# 33-servlet-request-event

# Document: `javax.servlet.ServletRequestEvent`

* **Package:** `javax.servlet`
* **Class:** `public class ServletRequestEvent extends java.util.EventObject`
* **Inheritance:** `java.lang.Object` $\rightarrow$ `java.util.EventObject` $\rightarrow$ `javax.servlet.ServletRequestEvent`
* **All Implemented Interfaces:** `java.io.Serializable`
* **Direct Known Subclasses:** `javax.servlet.ServletRequestAttributeEvent`
* **Since:** Servlet 2.4
* **See Also:** `javax.servlet.ServletRequestListener`, `javax.servlet.ServletContext`

---

## 1. Tổng quan (Overview)

`ServletRequestEvent` là lớp sự kiện đại diện cho các thông báo liên quan đến vòng đời (lifecycle) của một `ServletRequest`.

Đối tượng của lớp này được Servlet Container tự động khởi tạo và truyền làm tham số cho các phương thức của **`ServletRequestListener`** mỗi khi:

1. Một Request mới vừa bắt đầu đi vào scope của ứng dụng web (**Khởi tạo Request**).
2. Request hoàn tất xử lý và chuẩn bị rời khỏi scope (**Phá hủy Request**).

> **Lưu ý về Nguồn sự kiện (`source`):** Mặc dù sự kiện phản ánh trạng thái của `ServletRequest`, nguồn (`source`) kế thừa từ `EventObject` của lớp này lại chính là đối tượng **`ServletContext`** của ứng dụng web.

---

## 2. Tóm tắt Field & Constructor (Summary)

### Field Summary

| Field (Inherited) | Mô tả |
| --- | --- |
| `protected Object source` | Đối tượng phát ra sự kiện, kế thừa từ `java.util.EventObject` (ở đây chính là `ServletContext`). |

---

### Constructor Summary

| Constructor | Mô tả |
| --- | --- |
| `ServletRequestEvent(ServletContext sc, ServletRequest request)` | Khởi tạo một đối tượng sự kiện `ServletRequestEvent` từ `ServletContext` và `ServletRequest` tương ứng. |

---

## 3. Tóm tắt Phương thức (Method Summary)

| Kiểu trả về | Phương thức | Mô tả |
| --- | --- | --- |
| `ServletContext` | `getServletContext()` | Trả về đối tượng `ServletContext` của ứng dụng web nơi xảy ra sự kiện. |
| `ServletRequest` | `getServletRequest()` | Trả về đối tượng `ServletRequest` đang có thay đổi về vòng đời. |

---

## 4. Chi tiết Constructor & Tất cả các phương thức (Detail)

---

### Constructor Detail

#### `ServletRequestEvent(ServletContext sc, ServletRequest request)`

```java
public ServletRequestEvent(ServletContext sc, ServletRequest request)

```

* **Mô tả:** Khởi tạo đối tượng `ServletRequestEvent` chứa tham chiếu tới `ServletContext` và `ServletRequest`.
* **Parameters:**
* `sc`: Đối tượng `ServletContext` của ứng dụng web.
* `request`: Đối tượng `ServletRequest` gửi sự kiện này.



---

### Method Detail

#### 1. `getServletRequest()`

```java
public ServletRequest getServletRequest()

```

* **Mô tả:** Trả về đối tượng `ServletRequest` liên quan trực tiếp đến sự kiện khởi tạo hoặc phá hủy này.
* **Returns:** Đối tượng `ServletRequest`.

---

#### 2. `getServletContext()`

```java
public ServletContext getServletContext()

```

* **Mô tả:** Trả về đối tượng `ServletContext` đại diện cho ứng dụng web đang thực thi.
* **Returns:** Đối tượng `ServletContext`.

---

## 5. Ví dụ triển khai mã nguồn thực tế (Code Examples)

### Ví dụ 1: Sử dụng `ServletRequestEvent` để đo thời gian xử lý (Execution Time) của Request

Sử dụng `ServletRequestEvent` trong `ServletRequestListener` để đo chính xác tổng thời gian mà server cần để xử lý trọn vẹn một Request từ client.

```java
import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpServletRequest;

@WebListener
public class RequestExecutionTimerListener implements ServletRequestListener {

    private static final String START_TIME_ATTR = "REQUEST_START_TIME";

    // 1. Khi Request vừa được khởi tạo
    @Override
    public void requestInitialized(ServletRequestEvent sre) {
        ServletRequest request = sre.getServletRequest();
        long startTime = System.currentTimeMillis();
        
        // Lưu thời điểm bắt đầu vào chính Request Scope
        request.setAttribute(START_TIME_ATTR, startTime);

        if (request instanceof HttpServletRequest) {
            HttpServletRequest req = (HttpServletRequest) request;
            System.out.println("[REQUEST START] URI: " + req.getRequestURI() + " | IP: " + req.getRemoteAddr());
        }
    }

    // 2. Khi Request chuẩn bị bị phá hủy (xử lý xong)
    @Override
    public void requestDestroyed(ServletRequestEvent sre) {
        ServletRequest request = sre.getServletRequest();
        Long startTime = (Long) request.getAttribute(START_TIME_ATTR);

        if (startTime != null) {
            long duration = System.currentTimeMillis() - startTime;
            if (request instanceof HttpServletRequest) {
                HttpServletRequest req = (HttpServletRequest) request;
                System.out.println("[REQUEST END] URI: " + req.getRequestURI() + " | Execution Time: " + duration + " ms");
            }
        }
    }
}

```

---

### Ví dụ 2: Lấy thông tin Context và Request từ `ServletRequestEvent`

```java
import javax.servlet.ServletContext;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class RequestContextAuditListener implements ServletRequestListener {

    @Override
    public void requestInitialized(ServletRequestEvent sre) {
        // Lấy thông tin Context
        ServletContext context = sre.getServletContext();
        
        // Lấy thông tin Request
        String clientIp = sre.getServletRequest().getRemoteAddr();

        context.log("Incoming request from IP: " + clientIp + " in App: " + context.getServletContextName());
    }

    @Override
    public void requestDestroyed(ServletRequestEvent sre) {
        // Dọn dẹp tài nguyên nếu cần
    }
}

```

---