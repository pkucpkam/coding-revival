# 02-filter

# Document: `javax.servlet.Filter`

* **Package:** `javax.servlet`
* **Interface:** `public interface Filter`
* **All Known Implementing Classes:** `CharacterEncodingFilter`, `CorsFilter`, `CsrfFilter`, v.v.
* **Since:** Servlet 2.3

---

## 1. Tổng quan (Overview)

Một **`Filter`** (Bộ lọc) là một đối tượng thực hiện các tác vụ lọc trên:

1. **Request** tới tài nguyên (Servlet hoặc static content).
2. **Response** từ tài nguyên trả về cho client.
3. Hoặc cả **cả Request và Response**.

---

### Cách thức hoạt động và Các trường hợp sử dụng (Use Cases)

* **Vòng đời & Cấu hình:** Các bộ lọc thực hiện xử lý chính trong phương thức `doFilter`. Mỗi Filter đều truy cập được đối tượng `FilterConfig` để lấy tham số khởi tạo (init parameters) và tham chiếu đến `ServletContext` (ví dụ: dùng để load tài nguyên dùng chung).
* **Khai báo:** Filter được cấu hình trong deployment descriptor (`web.xml`) của ứng dụng web hoặc sử dụng annotation `@WebFilter`.

#### Các trường hợp ứng dụng thực tế phổ biến:

1. **Authentication Filters** (Lọc xác thực người dùng/phân quyền).
2. **Logging and Auditing Filters** (Ghi log và kiểm vết request).
3. **Image conversion Filters** (Chuyển đổi/tối ưu hóa hình ảnh).
4. **Data compression Filters** (Nén dữ liệu trả về, ví dụ: GZIP).
5. **Encryption Filters** (Mã hóa / Giải mã dữ liệu).
6. **Tokenizing Filters** (Xử lý chuỗi/token).
7. **Resource access event Filters** (Bắt sự kiện truy cập tài nguyên).
8. **XSL/T filters** (Biến đổi XML/XSLT).
9. **Mime-type chain Filter** (Lọc và thiết lập MIME-type).

---

## 2. Tóm tắt Phương thức (Method Summary)

| Kiểu trả về | Phương thức | Mô tả |
| --- | --- | --- |
| `void` | `init(FilterConfig filterConfig)` | Được web container gọi đúng 1 lần duy nhất để đưa Filter vào trạng thái sẵn sàng hoạt động. |
| `void` | `doFilter(ServletRequest request, ServletResponse response, FilterChain chain)` | Đọc/thay đổi request và response, quyết định cho phép đi tiếp qua chuỗi `FilterChain` hay chặn lại. |
| `void` | `destroy()` | Được web container gọi khi Filter bị ngừng dịch vụ (shutdown server/undeploy) để giải phóng tài nguyên. |

---

## 3. Chi tiết tất cả các phương thức (Method Detail)

---

### 1. `init`

```java
public void init(FilterConfig filterConfig) throws ServletException

```

* **Mô tả:** Được gọi bởi web container để đánh dấu Filter bắt đầu đi vào hoạt động. Servlet container gọi phương thức `init` **đúng một lần** duy nhất sau khi khởi tạo đối tượng Filter. Phương thức này phải hoàn thành thành công trước khi Filter thực hiện bất kỳ công việc lọc nào.
* **Các trường hợp Web Container không thể đưa Filter vào dịch vụ:**
1. Phương thức quăng ra một `ServletException`.
2. Phương thức không hoàn thành (return) trong khoảng thời gian quy định của web container.


* **Parameters:**
* `filterConfig`: Đối tượng chứa các thông tin cấu hình khởi tạo cho Filter từ `web.xml` hoặc `@WebFilter`.


* **Throws:** `ServletException` nếu có lỗi trong quá trình khởi tạo.

---

### 2. `doFilter`

```java
public void doFilter(ServletRequest request, 
                     ServletResponse response, 
                     FilterChain chain) 
              throws java.io.IOException, ServletException

```

* **Mô tả:** Được container gọi mỗi khi cặp `request/response` đi qua chuỗi bộ lọc do client yêu cầu một tài nguyên ở cuối chuỗi. Tham số `FilterChain` cho phép Filter chuyển tiếp request/response tới thành phần tiếp theo.
* **Quy trình triển khai tiêu chuẩn của phương thức `doFilter`:**
1. **Kiểm tra Request:** Đọc headers, parameters, attribute,...
2. **Bọc Request (tùy chọn):** Wrap đối tượng `request` bằng một Custom Wrapper để can thiệp nội dung/header đầu vào.
3. **Bọc Response (tùy chọn):** Wrap đối tượng `response` bằng một Custom Wrapper để can thiệp nội dung/header đầu ra.
4. **Điều hướng:**
* **Cách 4a:** Gọi đối tượng tiếp theo trong chuỗi qua `chain.doFilter(request, response)`.
* **Cách 4b:** Chặn request (không gọi `chain.doFilter`), trực tiếp trả về lỗi hoặc redirect (ví dụ: chưa đăng nhập).


5. **Xử lý Response sau khi chuỗi chạy xong:** can thiệp/bổ sung header cho response sau khi tài nguyên đích đã thực thi.


* **Throws:** `java.io.IOException`, `ServletException`.

---

### 3. `destroy`

```java
public void destroy()

```

* **Mô tả:** Được web container gọi để thông báo rằng Filter sắp bị ngưng hoạt động. Phương thức này chỉ được gọi khi tất cả các luồng (threads) bên trong `doFilter` của Filter đã thoát ra hoặc hết thời gian timeout. Sau khi gọi `destroy`, container sẽ không gọi `doFilter` trên instance này nữa.
* **Mục đích:** Cho phép Filter giải phóng các tài nguyên đang nắm giữ (ví dụ: kết nối Database, File handles, Thread pools, bộ nhớ đệm) và đồng bộ hóa trạng thái lưu trữ.

---

## 4. Ví dụ triển khai mã nguồn thực tế (Code Examples)

### Ví dụ 1: Authentication & Authorization Filter (Xác thực người dùng)

Lọc tất cả các request gửi tới `/admin/*`. Nếu người dùng chưa đăng nhập, lập tức chuyển hướng về trang `/login`.

```java
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter(urlPatterns = "/admin/*")
public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Khởi tạo tài nguyên hoặc đọc tham số cấu hình nếu cần
        System.out.println("AuthFilter: Khoi tao thanh cong.");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        HttpSession session = httpRequest.getSession(false);
        boolean isLoggedIn = (session != null && session.getAttribute("user") != null);

        if (isLoggedIn) {
            // 4a. Người dùng hợp lệ -> Cho phép đi tiếp tới Filter/Servlet tiếp theo
            chain.doFilter(request, response);
        } else {
            // 4b. Chưa đăng nhập -> Chặn lại và chuyển hướng tới trang đăng nhập
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login.jsp");
        }
    }

    @Override
    public void destroy() {
        // Dọn dẹp tài nguyên
        System.out.println("AuthFilter: Da giai phong.");
    }
}

```

---

### Ví dụ 2: Logging & Performance Filter (Ghi log và đo thời gian xử lý)

Tính toán chính xác thời gian xử lý của mỗi request (từ khi vào Filter cho tới khi Servlet xử lý xong và phản hồi về).

```java
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@WebFilter(urlPatterns = "/*")
public class PerformanceLoggingFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("PerformanceLoggingFilter started.");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest req = (HttpServletRequest) request;
        long startTime = System.currentTimeMillis();

        System.out.println("[REQUEST START] " + req.getMethod() + " " + req.getRequestURI());

        try {
            // Chuyển giao Request cho Servlet xử lý
            chain.doFilter(request, response);
        } finally {
            // Đo thời gian thực thi sau khi Response đã được xử lý xong
            long duration = System.currentTimeMillis() - startTime;
            System.out.println("[REQUEST END] " + req.getRequestURI() + " - Processing Time: " + duration + " ms");
        }
    }

    @Override
    public void destroy() {
        System.out.println("PerformanceLoggingFilter destroyed.");
    }
}

```

---

### Ví dụ 3: Character Encoding Filter (Cấu hình Tiếng Việt UTF-8)

Đảm bảo tất cả các Request đầu vào và Response đầu ra đều ép về chuẩn bảng mã UTF-8.

```java
import javax.servlet.*;
import java.io.IOException;

public class EncodingFilter implements Filter {

    private String encoding = "UTF-8";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String customEncoding = filterConfig.getInitParameter("encoding");
        if (customEncoding != null) {
            this.encoding = customEncoding;
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        // Thiết lập bộ mã ký tự trước khi đọc/ghi dữ liệu
        request.setCharacterEncoding(encoding);
        response.setCharacterEncoding(encoding);
        response.setContentType("text/html; charset=" + encoding);

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}

```