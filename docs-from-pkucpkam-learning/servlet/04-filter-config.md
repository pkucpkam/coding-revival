# 04-filter-config

# Document: `javax.servlet.FilterConfig`

* **Package:** `javax.servlet`
* **Interface:** `public interface FilterConfig`
* **Since:** Servlet 2.3
* **See Also:** `javax.servlet.Filter`

---

## 1. Tổng quan (Overview)

`FilterConfig` là một đối tượng cấu hình được **Servlet Container** cung cấp cho lập trình viên nhằm truyền thông tin cấu hình vào phương thức `init(FilterConfig filterConfig)` của một `Filter` trong quá trình khởi tạo.

### Vai trò chính của `FilterConfig`:

1. **Lấy tên của Filter:** Đọc tên bộ lọc được khai báo trong file cấu hình (`web.xml`) hoặc annotation `@WebFilter`.
2. **Đọc tham số khởi tạo (Init Parameters):** Truy xuất các cấu hình tĩnh truyền vào Filter khi ứng dụng khởi chạy.
3. **Truy cập `ServletContext`:** Lấy tham chiếu tới môi trường chạy ứng dụng web (dùng để ghi log toàn cục, chia sẻ thuộc tính ứng dụng, đọc tài nguyên trên server).

---

## 2. Tóm tắt Phương thức (Method Summary)

| Kiểu trả về | Phương thức | Mô tả |
| --- | --- | --- |
| `String` | `getFilterName()` | Trả về tên của Filter như được định nghĩa trong deployment descriptor (`web.xml`) hoặc annotation. |
| `String` | `getInitParameter(String name)` | Trả về chuỗi chứa giá trị của tham số khởi tạo theo tên chỉ định, hoặc `null` nếu không tồn tại. |
| `java.util.Enumeration<String>` | `getInitParameterNames()` | Trả về danh sách tên tất cả tham số khởi tạo dưới dạng một `Enumeration` chuỗi. |
| `ServletContext` | `getServletContext()` | Trả về tham chiếu đến đối tượng `ServletContext` của ứng dụng web hiện tại. |

---

## 3. Chi tiết tất cả các phương thức (Method Detail)

---

### 1. `getFilterName`

```java
public java.lang.String getFilterName()

```

* **Mô tả:** Trả về tên đại diện (`filter-name`) của bộ lọc này như được khai báo trong file deployment descriptor (`web.xml`) hoặc thuộc tính `filterName` của `@WebFilter`.
* **Returns:** Một chuỗi `String` chứa tên của Filter.

---

### 2. `getServletContext`

```java
public ServletContext getServletContext()

```

* **Mô tả:** Trả về tham chiếu tới đối tượng `ServletContext` mà caller (Filter) đang thực thi bên trong. Đối tượng này cho phép Filter tương tác trực tiếp với Servlet Container.
* **Returns:** Một đối tượng `ServletContext`.
* **See Also:** `javax.servlet.ServletContext`

---

### 3. `getInitParameter`

```java
public java.lang.String getInitParameter(java.lang.String name)

```

* **Mô tả:** Trả về giá trị của tham số khởi tạo được chỉ định theo tên.
* **Parameters:**
* `name`: Chuỗi `String` chỉ định tên tham số khởi tạo cần lấy.


* **Returns:** Một chuỗi `String` chứa giá trị tham số, hoặc `null` nếu tham số không tồn tại.

---

### 4. `getInitParameterNames`

```java
public java.util.Enumeration getInitParameterNames()

```

* **Mô tả:** Trả về danh sách tất cả các tên tham số khởi tạo của Filter dưới dạng một tập hợp `Enumeration` các đối tượng `String`. Nếu Filter không có tham số khởi tạo nào, phương thức sẽ trả về một `Enumeration` rỗng.
* **Returns:** Đối tượng `Enumeration` chứa tập hợp tên các tham số khởi tạo.

---

## 4. Ví dụ triển khai mã nguồn thực tế (Code Examples)

### Ví dụ 1: Đọc Init Parameters và ServletContext bằng Annotation `@WebFilter`

Minh họa việc cấu hình các tham số khởi tạo ngay trên Annotation và sử dụng `FilterConfig` để lấy dữ liệu trong hàm `init()`.

```java
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import java.io.IOException;

@WebFilter(
    urlPatterns = "/*",
    filterName = "SiteConfigFilter",
    initParams = {
        @WebInitParam(name = "defaultLanguage", value = "vi_VN"),
        @WebInitParam(name = "maxUploadSize", value = "10485760") // 10MB
    }
)
public class SiteConfigFilter implements Filter {

    private String language;
    private long maxUploadSize;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // 1. Lấy tên Filter
        String filterName = filterConfig.getFilterName();

        // 2. Lấy đối tượng ServletContext để log thông tin hệ thống
        ServletContext context = filterConfig.getServletContext();
        context.log("[" + filterName + "] Dang khoi tao Filter...");

        // 3. Đọc tham số khởi tạo cụ thể
        this.language = filterConfig.getInitParameter("defaultLanguage");
        
        String maxAgeStr = filterConfig.getInitParameter("maxUploadSize");
        if (maxAgeStr != null) {
            this.maxUploadSize = Long.parseLong(maxAgeStr);
        }

        context.log("[" + filterName + "] Language=" + language + ", MaxSize=" + maxUploadSize);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        // Gán cấu hình mặc định vào Request Attributes
        request.setAttribute("appLanguage", this.language);
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {}
}

```

---

### Ví dụ 2: Duyệt toàn bộ Init Parameters qua `getInitParameterNames()` (Cấu hình `web.xml`)

Nhiều trường hợp cấu hình Filter qua file `web.xml`, ta muốn duyệt tự động tất cả tham số mà không cần biết trước tên tham số.

#### File cấu hình `web.xml`:

```xml
<filter>
    <filter-name>SecurityHeaderFilter</filter-name>
    <filter-class>com.example.SecurityHeaderFilter</filter-class>
    <init-param>
        <param-name>X-Frame-Options</param-name>
        <param-value>DENY</param-value>
    </init-param>
    <init-param>
        <param-name>X-XSS-Protection</param-name>
        <param-value>1; mode=block</param-value>
    </init-param>
</filter>
<filter-mapping>
    <filter-name>SecurityHeaderFilter</filter-name>
    <url-pattern>/*</url-pattern>
</filter-mapping>

```

#### File Java `SecurityHeaderFilter.java`:

```java
import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class SecurityHeaderFilter implements Filter {

    private final Map<String, String> headersMap = new HashMap<>();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Lấy danh sách tên tất cả tham số khởi tạo
        Enumeration<String> paramNames = filterConfig.getInitParameterNames();

        while (paramNames.hasMoreElements()) {
            String headerName = paramNames.nextElement();
            String headerValue = filterConfig.getInitParameter(headerName);
            
            // Lưu các cặp Header cấu hình từ web.xml vào Map
            headersMap.put(headerName, headerValue);
        }

        System.out.println("Da load " + headersMap.size() + " Security Headers tu web.xml");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Tự động gán tất cả Security Headers đã đọc từ FilterConfig vào HTTP Response
        headersMap.forEach(httpResponse::setHeader);

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        headersMap.clear();
    }
}

```