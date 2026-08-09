# 20-servlet

# Document: `javax.servlet.Servlet`

* **Package:** `javax.servlet`
* **Interface:** `public interface Servlet`
* **All Known Implementing Classes:** `javax.servlet.GenericServlet`, `javax.servlet.http.HttpServlet`
* **See Also:** `javax.servlet.GenericServlet`, `javax.servlet.http.HttpServlet`

---

## 1. Tổng quan (Overview)

Interface `Servlet` định nghĩa các phương thức cốt lõi mà tất cả các Servlet Java bắt buộc phải triển khai (implement).

Một **Servlet** là một chương trình Java nhỏ chạy bên trong một Web Server/Servlet Container. Servlet tiếp nhận và phản hồi các yêu cầu (requests) từ Web Client, phổ biến nhất là thông qua giao thức HTTP (HyperText Transfer Protocol).

### Triển khai Interface:

Để triển khai interface này trong thực tế, bạn có thể:

1. Viết một Servlet độc lập giao thức bằng cách mở rộng (extend) lớp `javax.servlet.GenericServlet`.
2. Viết một HTTP Servlet chuẩn cho ứng dụng Web bằng cách mở rộng lớp `javax.servlet.http.HttpServlet`.

---

## 2. Vòng đời của Servlet (Servlet Lifecycle)

Interface này định nghĩa 3 phương thức quản lý vòng đời (Lifecycle Methods) được Servlet Container tự động kích hoạt theo đúng trình tự sau:

1. **Khởi tạo (`init`):** Container khởi tạo Servlet instance, sau đó gọi phương thức `init(ServletConfig config)` đúng **1 lần duy nhất** để cấu hình Servlet trước khi nhận request.
2. **Xử lý Dịch vụ (`service`):** Mọi request từ Client gửi đến sẽ được xử lý thông qua phương thức `service(ServletRequest req, ServletResponse res)`. Phương thức này có thể chạy đồng thời trên nhiều luồng (multithreaded).
3. **Phá hủy & Dọn dẹp (`destroy`):** Khi Servlet bị rút khỏi dịch vụ (shutdown server hoặc undeploy app), Container gọi `destroy()` **1 lần duy nhất** để giải phóng tài nguyên. Sau đó, Servlet instance sẽ được thu gom rác (Garbage Collected).

Ngoài ra, interface còn cung cấp 2 phương thức bổ trợ:

* `getServletConfig()`: Lấy đối tượng cấu hình khởi tạo.
* `getServletInfo()`: Trả về thông tin mô tả Servlet (tác giả, phiên bản, bản quyền).

---

## 3. Tóm tắt Phương thức (Method Summary)

| Kiểu trả về | Phương thức | Mô tả |
| --- | --- | --- |
| `void` | `init(ServletConfig config)` | Được Servlet Container gọi **đúng 1 lần** để đưa Servlet vào trạng thái sẵn sàng hoạt động. |
| `void` | `service(ServletRequest req, ServletResponse res)` | Được Container gọi mỗi khi có request từ client gửi tới Servlet. |
| `void` | `destroy()` | Được Container gọi **đúng 1 lần** để thông báo Servlet bị ngừng dịch vụ và dọn dẹp tài nguyên. |
| `ServletConfig` | `getServletConfig()` | Trả về đối tượng `ServletConfig` chứa tham số khởi tạo của Servlet. |
| `String` | `getServletInfo()` | Trả về thông tin mô tả ngắn gọn về Servlet (tác giả, phiên bản, bản quyền). |

---

## 4. Chi tiết Tất cả các phương thức (Detail)

---

### 1. `init`

```java
public void init(ServletConfig config) throws ServletException

```

* **Mô tả:** Được Servlet Container gọi đúng **1 lần duy nhất** ngay sau khi khởi tạo thể hiện (instance) của Servlet. Phương thức này phải hoàn thành thành công trước khi Servlet có thể nhận bất kỳ request nào.
* **Các trường hợp Servlet Container không thể đưa Servlet vào dịch vụ:**
1. Phương thức ném ra một `ServletException`.
2. Phương thức không hoàn thành (return) trong khoảng thời gian quy định của Web Server.


* **Parameters:** `config` - đối tượng `ServletConfig` chứa cấu hình và tham số khởi tạo của Servlet.
* **Throws:** `ServletException` nếu xảy ra lỗi gián đoạn hoạt động bình thường của Servlet.
* **See Also:** `UnavailableException`, `getServletConfig()`

---

### 2. `service`

```java
public void service(ServletRequest req, ServletResponse res) 
             throws ServletException, java.io.IOException

```

* **Mô tả:** Được Servlet Container gọi để cho phép Servlet phản hồi lại yêu cầu từ Client. Phương thức này **chỉ được gọi sau khi** `init()` đã hoàn thành thành công.
* **Lưu ý quan trọng về Đa luồng (Multithreading):**
* Servlets chạy bên trong các Container đa luồng có thể xử lý đồng thời hàng trăm request. Lập trình viên phải chú ý **đồng bộ hóa (synchronize)** khi truy cập vào các tài nguyên dùng chung như: biến instance, biến static/class, file, kết nối CSDL, kết nối mạng.


* **Parameters:**
* `req`: Đối tượng `ServletRequest` chứa dữ liệu request từ Client.
* `res`: Đối tượng `ServletResponse` chứa dữ liệu phản hồi trả về Client.


* **Throws:**
* `ServletException`: Nếu có lỗi cấp độ Servlet xảy ra.
* `java.io.IOException`: Nếu xảy ra lỗi I/O trong quá trình đọc/ghi.



---

### 3. `destroy`

```java
public void destroy()

```

* **Mô tả:** Được Servlet Container gọi để thông báo rằng Servlet sắp bị rút khỏi dịch vụ. Phương thức này **chỉ được gọi một lần** sau khi tất cả các luồng đang thực thi bên trong `service()` đã hoàn tất hoặc sau khi hết thời gian timeout. Sau khi gọi `destroy()`, Container sẽ không bao giờ gọi lại `service()` trên instance này nữa.
* **Mục đích:** Giúp Servlet dọn dẹp các tài nguyên đang lưu giữ (như giải phóng bộ nhớ, đóng file handles, giải phóng connection pool, đóng luồng) và đồng bộ hóa trạng thái bộ nhớ xuống đĩa cứng.

---

### 4. `getServletConfig`

```java
public ServletConfig getServletConfig()

```

* **Mô tả:** Trả về đối tượng `ServletConfig` chứa các tham số khởi tạo và cấu hình ban đầu của Servlet. Đối tượng này chính là đối tượng đã được truyền vào hàm `init(ServletConfig config)`.
* **Returns:** Đối tượng `ServletConfig`.
* **See Also:** `init(ServletConfig)`

---

### 5. `getServletInfo`

```java
public java.lang.String getServletInfo()

```

* **Mô tả:** Trả về thông tin mô tả về Servlet (như tác giả, phiên bản, bản quyền). Chuỗi trả về phải là **Plain Text thuần túy**, không được chứa các thẻ định dạng markup (như HTML, XML, v.v.).
* **Returns:** Chuỗi `String` chứa thông tin về Servlet.

---

## 5. Ví dụ triển khai mã nguồn thực tế (Code Examples)

### Ví dụ 1: Triển khai trực tiếp `javax.servlet.Servlet` (Thuần túy)

Ví dụ dưới đây minh họa việc triển khai trực tiếp interface `Servlet` từ đầu để hiểu rõ toàn bộ 5 phương thức hợp đồng của nó.

```java
import javax.servlet.*;
import java.io.IOException;
import java.io.PrintWriter;

public class RawCustomServlet implements Servlet {

    private ServletConfig config;

    // 1. Lifecycle Method: Khởi tạo
    @Override
    public void init(ServletConfig config) throws ServletException {
        this.config = config;
        System.out.println("[LIFECYCLE] 1. init(): Servlet đã được khởi tạo thành công.");
    }

    // 2. Lifecycle Method: Xử lý Request
    @Override
    public void service(ServletRequest req, ServletResponse res) 
            throws ServletException, IOException {
        
        System.out.println("[LIFECYCLE] 2. service(): Đang xử lý 1 request từ Client.");

        res.setContentType("text/html; charset=UTF-8");
        PrintWriter out = res.getWriter();

        out.println("<html><body>");
        out.println("<h1>Demo Triển Khai Trực Tiếp interface javax.servlet.Servlet</h1>");
        out.println("<p>Servlet Name: " + getServletConfig().getServletName() + "</p>");
        out.println("</body></html>");
    }

    // 3. Lifecycle Method: Phá hủy
    @Override
    public void destroy() {
        System.out.println("[LIFECYCLE] 3. destroy(): Servlet bị dừng và giải phóng tài nguyên.");
    }

    // 4. Getter cho ServletConfig
    @Override
    public ServletConfig getServletConfig() {
        return this.config;
    }

    // 5. Thông tin mô tả Servlet
    @Override
    public String getServletInfo() {
        return "RawCustomServlet - Version 1.0 - Author: Developer";
    }
}

```

---

### Ví dụ 2: Triển khai thực tế chuẩn mực bằng cách Kế thừa `HttpServlet`

Trong các dự án Web thực tế, lập trình viên không triển khai trực tiếp `Servlet` mà sẽ kế thừa `HttpServlet` (lớp đã triển khai sẵn `Servlet` và `GenericServlet`).

```java
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/app-info")
public class SystemInfoServlet extends HttpServlet {

    private String systemVersion;

    // Khởi tạo tùy chỉnh (kế thừa từ GenericServlet/Servlet)
    @Override
    public void init() throws ServletException {
        this.systemVersion = "v2026.1.0";
        log("SystemInfoServlet: Đã nạp cấu hình hệ thống phiên bản " + systemVersion);
    }

    // HttpServlet tự động phân chia service() thành doGet(), doPost(),...
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        resp.setContentType("application/json; charset=UTF-8");
        resp.getWriter().write("{\"system_status\": \"running\", \"version\": \"" + systemVersion + "\"}");
    }

    @Override
    public void destroy() {
        log("SystemInfoServlet: Đã đóng kết nối dọn dẹp bộ nhớ.");
    }

    @Override
    public String getServletInfo() {
        return "System Info API Servlet Core";
    }
}

```