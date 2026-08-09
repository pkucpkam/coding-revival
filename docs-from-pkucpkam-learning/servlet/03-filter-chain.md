# 03-filter-chain

# Document: `javax.servlet.FilterChain`

* **Package:** `javax.servlet`
* **Interface:** `public interface FilterChain`
* **Since:** Servlet 2.3
* **See Also:** `javax.servlet.Filter`

---

## 1. Tổng quan (Overview)

`FilterChain` là một đối tượng do **Servlet Container** cung cấp cho lập trình viên, đại diện cho chuỗi gọi (invocation chain) của các bộ lọc (filters) xử lý một request gửi tới tài nguyên (resource).

các bộ lọc (Filters) sử dụng `FilterChain` để:

1. **Chuyển tiếp request/response** đến bộ lọc tiếp theo trong chuỗi.
2. **Kích hoạt tài nguyên đích** (Servlet, file JSP hoặc file tĩnh) ở cuối chuỗi nếu bộ lọc hiện tại là bộ lọc cuối cùng.

---

## 2. Tóm tắt Phương thức (Method Summary)

| Kiểu trả về | Phương thức | Mô tả |
| --- | --- | --- |
| `void` | `doFilter(ServletRequest request, ServletResponse response)` | Thực thi Filter tiếp theo trong chuỗi, hoặc kích hoạt Servlet/tài nguyên ở cuối chuỗi nếu đây là Filter cuối cùng. |

---

## 3. Chi tiết Phương thức (Method Detail)

---

### `doFilter`

```java
public void doFilter(ServletRequest request, ServletResponse response) 
              throws java.io.IOException, ServletException

```

* **Mô tả:** Kích hoạt bộ lọc tiếp theo trong chuỗi bộ lọc. Nếu bộ lọc đang gọi phương thức này là bộ lọc cuối cùng trong chuỗi, phương thức sẽ kích hoạt tài nguyên (Servlet, JSP, v.v.) nằm ở cuối chuỗi.
* **Parameters:**
* `request`: Request truyền tiếp qua chuỗi bộ lọc.
* `response`: Response truyền tiếp qua chuỗi bộ lọc.


* **Throws:**
* `java.io.IOException`: Nếu xảy ra lỗi I/O trong quá trình xử lý.
* `ServletException`: Nếu quá trình lọc gặp lỗi cấp độ Servlet.


* **Since:** Servlet 2.3

---

## 4. Ví dụ triển khai mã nguồn thực tế (Code Examples)

### Ví dụ 1: Cơ chế hoạt động của `FilterChain` trong chuỗi nhiều Filters

Đoạn mã minh họa thứ tự chạy của 2 Filter khi chuyển giao đối tượng `FilterChain`.

```java
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;

// Filter 1: Chạy trước
@WebFilter(urlPatterns = "/*")
public class FirstFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        System.out.println("1. FirstFilter: Truoc khi goi chain.doFilter()");

        // Chuyển tiếp Request/Response sang Filter tiếp theo trong FilterChain
        chain.doFilter(request, response); 

        System.out.println("4. FirstFilter: Sau khi FilterChain xu ly xong (tren duong ve)");
    }

    @Override
    public void destroy() {}
}

```

```java
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;

// Filter 2: Chạy tiếp theo
@WebFilter(urlPatterns = "/*")
public class SecondFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        System.out.println("2. SecondFilter: Truoc khi goi chain.doFilter()");

        // Vì đây la Filter cuoi cung, chain.doFilter() se goi Servlet dich
        chain.doFilter(request, response);

        System.out.println("3. SecondFilter: Sau khi Servlet xu ly xong");
    }

    @Override
    public void destroy() {}
}

```

> **Thứ tự In ra Console:**
> 1. `1. FirstFilter: Truoc khi goi chain.doFilter()`
> 2. `2. SecondFilter: Truoc khi goi chain.doFilter()`
> 3. *(Servlet đích xử lý và trả response)*
> 4. `3. SecondFilter: Sau khi Servlet xu ly xong`
> 5. `4. FirstFilter: Sau khi FilterChain xu ly xong (tren duong ve)`
> 
> 

---

### Ví dụ 2: Sử dụng `FilterChain` với Custom Request Wrapper

Trong nhiều trường hợp, bạn muốn chỉnh sửa tham số Request trước khi chuyển giao cho Filter/Servlet tiếp theo thông qua `FilterChain`.

```java
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;

@WebFilter(urlPatterns = "/*")
public class SanitizeInputFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        // Bọc request gốc bằng một Wrapper tùy chỉnh để trim() các tham số
        HttpServletRequest wrappedRequest = new HttpServletRequestWrapper(httpRequest) {
            @Override
            public String getParameter(String name) {
                String value = super.getParameter(name);
                return (value != null) ? value.trim() : null;
            }
        };

        // Truyền request da duoc boc (wrappedRequest) vao FilterChain
        chain.doFilter(wrappedRequest, response);
    }

    @Override
    public void destroy() {}
}

```

---

### Ví dụ 3: Ngắt chuỗi `FilterChain` (Chặn Request)

Nếu điều kiện không thỏa mãn (ví dụ: thiếu API Key), bộ lọc sẽ **không gọi `chain.doFilter()**`, giúp chặn request không đến được Servlet đích.

```java
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(urlPatterns = "/api/*")
public class ApiKeyFilter implements Filter {

    private static final String VALID_API_KEY = "SECRET_KEY_123";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String apiKey = req.getHeader("X-API-KEY");

        if (VALID_API_KEY.equals(apiKey)) {
            // API Key hop le -> Cho phep tiep tuc qua FilterChain
            chain.doFilter(request, response);
        } else {
            // API Key khong hop le -> KHONG GOI chain.doFilter(), tra ve loi 401 ngay lap tuc
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.getWriter().write("Error 401: Invalid or missing API Key!");
        }
    }

    @Override
    public void destroy() {}
}

```