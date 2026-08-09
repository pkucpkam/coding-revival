# 27-servlet-exception

# Document: `javax.servlet.ServletException`

* **Package:** `javax.servlet`
* **Class:** `public class ServletException extends java.lang.Exception`
* **Inheritance:** `java.lang.Object` $\rightarrow$ `java.lang.Throwable` $\rightarrow$ `java.lang.Exception` $\rightarrow$ `javax.servlet.ServletException`
* **All Implemented Interfaces:** `java.io.Serializable`
* **Direct Known Subclasses:** `javax.servlet.UnavailableException`
* **See Also:** `javax.servlet.Servlet`, `javax.servlet.http.HttpServlet`

---

## 1. Tổng quan (Overview)

`ServletException` định nghĩa một ngoại lệ chung mà một Servlet hoặc Filter có thể quăng ra (throw) khi gặp sự cố trong quá trình xử lý yêu cầu hoặc khởi tạo.

Đây là ngoại lệ kiểm tra được (checked exception) cốt lõi của Servlet API. Khi một phương thức trong Servlet (như `init`, `service`, `doGet`, `doPost`) gặp sự cố hệ thống hoặc lỗi nghiệp vụ không thể tự xử lý, nó sẽ quăng ra `ServletException` để Servlet Container (như Apache Tomcat) bắt lấy và chuyển hướng xử lý (ví dụ: hiển thị trang báo lỗi HTTP 500 hoặc trang lỗi tùy chỉnh).

Lớp này cũng hỗ trợ cơ chế **Root Cause Exception Wrapping** (bọc ngoại lệ gốc), cho phép lưu giữ nguyên vẹn thông tin vết lỗi (stack trace) của các ngoại lệ cấp dưới (như `SQLException`, `IOException`, `NullPointerException`).

---

## 2. Tóm tắt Constructor & Phương thức (Summary)

### Constructor Summary

| Constructor | Mô tả |
| --- | --- |
| `ServletException()` | Khởi tạo một đối tượng `ServletException` mới không chứa thông điệp lỗi. |
| `ServletException(String message)` | Khởi tạo một `ServletException` mới với thông điệp mô tả lỗi cụ thể. |
| `ServletException(String message, Throwable rootCause)` | Khởi tạo một `ServletException` kèm thông điệp mô tả và ngoại lệ nguyên nhân gốc (`rootCause`). |
| `ServletException(Throwable rootCause)` | Khởi tạo một `ServletException` chỉ truyền vào ngoại lệ nguyên nhân gốc (`rootCause`). |

---

### Method Summary

| Kiểu trả về | Phương thức | Mô tả |
| --- | --- | --- |
| `Throwable` | `getRootCause()` | Trả về ngoại lệ nguyên nhân gốc (`rootCause`) đã gây ra `ServletException` này. |

---

## 3. Chi tiết Constructor & Tất cả các phương thức (Detail)

---

### Constructor Detail

#### 1. `ServletException()`

```java
public ServletException()

```

* **Mô tả:** Khởi tạo một `ServletException` rỗng không có thông điệp chi tiết.

---

#### 2. `ServletException(String message)`

```java
public ServletException(String message)

```

* **Mô tả:** Khởi tạo một `ServletException` với thông điệp mô tả chỉ định. Thông điệp này có thể được ghi vào file log của server và/hoặc hiển thị cho người dùng.
* **Parameters:** `message` - chuỗi `String` chứa văn bản thông điệp lỗi.

---

#### 3. `ServletException(String message, Throwable rootCause)`

```java
public ServletException(String message, Throwable rootCause)

```

* **Mô tả:** Khởi tạo một `ServletException` khi Servlet cần ném ra ngoại lệ kèm thông điệp mô tả và nguyên nhân gốc (`rootCause`) can thiệp vào hoạt động bình thường của nó.
* **Parameters:**
* `message`: Chuỗi `String` chứa thông điệp lỗi.
* `rootCause`: Đối tượng `Throwable` (như `SQLException`, `IOException`) gây ra sự cố.



---

#### 4. `ServletException(Throwable rootCause)`

```java
public ServletException(Throwable rootCause)

```

* **Mô tả:** Khởi tạo một `ServletException` chỉ truyền vào nguyên nhân gốc (`rootCause`). Thông điệp của ngoại lệ này sẽ dựa trên thông điệp đã được địa phương hóa (`getLocalizedMessage()`) của ngoại lệ gốc.
* **Parameters:** `rootCause` - đối tượng `Throwable` gây ra ngoại lệ.

---

### Method Detail

#### `getRootCause()`

```java
public java.lang.Throwable getRootCause()

```

* **Mô tả:** Trả về ngoại lệ nguyên nhân gốc đã gây ra `ServletException` này (tương tự như `getCause()` trong `java.lang.Throwable`).
* **Returns:** Đối tượng `Throwable` là nguyên nhân gốc, hoặc `null` nếu không có.

---

## 4. Ví dụ triển khai mã nguồn thực tế (Code Examples)

### Ví dụ 1: Bọc ngoại lệ CSDL (`SQLException`) bằng `ServletException` trong Servlet

Khi tương tác với CSDL, lỗi `SQLException` xuất hiện. Ta bắt lỗi đó và bọc lại vào `ServletException` để Servlet Container xử lý.

```java
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/user-detail")
public class UserDetailServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        String userId = req.getParameter("id");

        try {
            // Giả lập truy vấn CSDL có thể phát sinh SQLException
            fetchUserDataFromDatabase(userId);

        } catch (SQLException e) {
            // Ghi log sự cố
            log("Lỗi truy vấn CSDL cho User ID: " + userId, e);

            // Bọc SQLException vào ServletException kèm thông điệp rõ ràng
            throw new ServletException("Lỗi hệ thống khi truy xuất dữ liệu người dùng từ Database", e);
        }
    }

    private void fetchUserDataFromDatabase(String userId) throws SQLException {
        if ("invalid".equals(userId)) {
            throw new SQLException("Connection timeout or invalid SQL syntax");
        }
    }
}

```

---

### Ví dụ 2: Xử lý ngoại lệ tập trung trong Filter với `ServletException`

Sử dụng `ServletException` để chặn request trong `Filter` khi có vi phạm bảo mật hoặc lỗi cấu hình.

```java
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@WebFilter(urlPatterns = "/api/*")
public class ApiValidationFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("ApiValidationFilter initialized.");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String apiKey = httpRequest.getHeader("X-API-KEY");

        if (apiKey == null || apiKey.trim().isEmpty()) {
            // Ném ServletException trực tiếp để Container dừng luồng xử lý
            throw new ServletException("Yêu cầu bị từ chối: Thiếu Header X-API-KEY hợp lệ!");
        }

        try {
            chain.doFilter(request, response);
        } catch (Exception e) {
            // Bắt lỗi không xác định và bọc lại bằng ServletException
            throw new ServletException("Lỗi không xác định xảy ra trong chuỗi Filter execution", e);
        }
    }

    @Override
    public void destroy() {}
}

```

---