# 08-http-servlet-request-wrapper

# Document: `javax.servlet.http.HttpServletRequestWrapper`

* **Package:** `javax.servlet.http`
* **Class:** `public class HttpServletRequestWrapper extends ServletRequestWrapper implements HttpServletRequest`
* **Inheritance:** `java.lang.Object` $\rightarrow$ `javax.servlet.ServletRequestWrapper` $\rightarrow$ `javax.servlet.http.HttpServletRequestWrapper`
* **All Implemented Interfaces:** `javax.servlet.http.HttpServletRequest`, `javax.servlet.ServletRequest`
* **Since:** Servlet 2.3
* **See Also:** `javax.servlet.http.HttpServletRequest`, `javax.servlet.ServletRequestWrapper`

---

## 1. Tổng quan (Overview)

`HttpServletRequestWrapper` cung cấp một cài đặt tiện lợi cho interface `HttpServletRequest`, cho phép các lập trình viên mở rộng (subclass) để tùy chỉnh (adapt/modify) HTTP Request truyền vào một Servlet.

Lớp này thực thi **Wrapper Pattern** hoặc **Decorator Pattern**. Hành vi mặc định của tất cả các phương thức trong lớp này là **gọi chuyển tiếp (call-through)** đến đối tượng request gốc đã được bọc (wrapped request object).

---

## 2. Tóm tắt Field & Constructor (Summary)

### Field Summary

| Hằng số (Inherited) | Mô tả |
| --- | --- |
| `BASIC_AUTH` | Chuỗi xác định kiểu xác thực HTTP Basic (`"BASIC"`). |
| `CLIENT_CERT_AUTH` | Chuỗi xác định kiểu xác thực Client Certificate (`"CLIENT_CERT"`). |
| `DIGEST_AUTH` | Chuỗi xác định kiểu xác thực HTTP Digest (`"DIGEST"`). |
| `FORM_AUTH` | Chuỗi xác định kiểu xác thực Form-based (`"FORM"`). |

---

### Constructor Summary

| Constructor | Mô tả |
| --- | --- |
| `HttpServletRequestWrapper(HttpServletRequest request)` | Khởi tạo một đối tượng request wrapper bao bọc đối tượng `HttpServletRequest` được truyền vào. |

---

## 3. Tóm tắt Phương thức (Method Summary)

| Kiểu trả về | Phương thức | Mô tả |
| --- | --- | --- |
| `String` | `getAuthType()` | Trả về tên của cơ chế xác thực được sử dụng cho request. |
| `String` | `getContextPath()` | Trả về phần đường dẫn URI chỉ định ngữ cảnh (context) của request. |
| `Cookie[]` | `getCookies()` | Trả về mảng chứa tất cả các Cookie gửi kèm request này. |
| `long` | `getDateHeader(String name)` | Trả về giá trị ngày tháng của header dưới dạng miligiây (tính từ 01/01/1970 GMT). |
| `String` | `getHeader(String name)` | Trả về giá trị chuỗi của một request header theo tên. |
| `Enumeration<String>` | `getHeaderNames()` | Trả về danh sách tất cả các tên header có trong request. |
| `Enumeration<String>` | `getHeaders(String name)` | Trả về tất cả các giá trị của một header cụ thể. |
| `int` | `getIntHeader(String name)` | Trả về giá trị dạng số nguyên (`int`) của một header. |
| `String` | `getMethod()` | Trả về tên của phương thức HTTP (GET, POST, PUT, DELETE,...). |
| `String` | `getPathInfo()` | Trả về thông tin đường dẫn bổ sung nằm sau Servlet path và trước Query string. |
| `String` | `getPathTranslated()` | Trả về đường dẫn thực tế trên ổ đĩa sau khi đã qua chuyển đổi. |
| `String` | `getQueryString()` | Trả về chuỗi truy vấn (query string) nằm sau dấu `?` trong URL. |
| `String` | `getRemoteUser()` | Trả về tên đăng nhập của user gửi request này (nếu đã xác thực). |
| `String` | `getRequestedSessionId()` | Trả về Session ID được gửi từ phía client. |
| `String` | `getRequestURI()` | Trả về phần URL từ tên protocol cho đến trước query string. |
| `StringBuffer` | `getRequestURL()` | Trả về URL đầy đủ được tái cấu trúc lại từ request. |
| `String` | `getServletPath()` | Trả về đường dẫn Servlet xử lý request này. |
| `HttpSession` | `getSession()` | Trả về Session hiện tại liên kết với request, tự tạo mới nếu chưa có. |
| `HttpSession` | `getSession(boolean create)` | Trả về Session hiện tại, tạo mới nếu `create = true` và chưa có Session. |
| `Principal` | `getUserPrincipal()` | Trả về đối tượng `java.security.Principal` chứa thông tin user đã xác thực. |
| `boolean` | `isRequestedSessionIdFromCookie()` | Kiểm tra Session ID có được gửi qua Cookie hay không. |
| `boolean` | `isRequestedSessionIdFromURL()` | Kiểm tra Session ID có được gửi đính kèm trên URL hay không. |
| `boolean` | `isRequestedSessionIdFromUrl()` | *(Deprecated)* Tương tự `isRequestedSessionIdFromURL()`. |
| `boolean` | `isRequestedSessionIdValid()` | Kiểm tra Session ID từ client có còn hợp lệ hay không. |
| `boolean` | `isUserInRole(String role)` | Kiểm tra user đã xác thực có thuộc về một vai trò (role) nhất định không. |

---

## 4. Chi tiết Constructor & Tất cả các phương thức (Detail)

---

### Constructor Detail

#### `HttpServletRequestWrapper(HttpServletRequest request)`

* **Cú pháp:** `public HttpServletRequestWrapper(HttpServletRequest request)`
* **Mô tả:** Khởi tạo một đối tượng request wrapper bao bọc request được truyền vào.
* **Parameters:** `request` - đối tượng `HttpServletRequest` cần bọc.
* **Throws:** `java.lang.IllegalArgumentException` nếu đối tượng `request` truyền vào là `null`.

---

### Method Detail

#### 1. `getAuthType()`

* **Cú pháp:** `public String getAuthType()`
* **Mô tả:** Hành vi mặc định là trả về kết quả của `getAuthType()` trên đối tượng request được bọc.
* **Specified by:** `getAuthType` trong interface `HttpServletRequest`.
* **Returns:** Một trong các hằng số static `BASIC_AUTH`, `FORM_AUTH`, `CLIENT_CERT_AUTH`, `DIGEST_AUTH`, hoặc chuỗi đại diện cho cơ chế xác thực riêng của container; trả về `null` nếu request chưa được xác thực.

---

#### 2. `getCookies()`

* **Cú pháp:** `public Cookie[] getCookies()`
* **Mô tả:** Hành vi mặc định là trả về kết quả của `getCookies()` trên đối tượng request được bọc.
* **Specified by:** `getCookies` trong interface `HttpServletRequest`.
* **Returns:** Mảng tất cả các `Cookie` đính kèm theo request này, hoặc `null` nếu không có cookie nào.

---

#### 3. `getDateHeader(String name)`

* **Cú pháp:** `public long getDateHeader(String name)`
* **Mô tả:** Hành vi mặc định là trả về kết quả của `getDateHeader(String name)` trên đối tượng request được bọc.
* **Specified by:** `getDateHeader` trong interface `HttpServletRequest`.
* **Parameters:** `name` - tên của header.
* **Returns:** Giá trị kiểu `long` biểu diễn thời gian của header tính bằng miligiây từ 01/01/1970 GMT, hoặc `-1` nếu header không tồn tại.

---

#### 4. `getHeader(String name)`

* **Cú pháp:** `public String getHeader(String name)`
* **Mô tả:** Hành vi mặc định là trả về kết quả của `getHeader(String name)` trên đối tượng request được bọc.
* **Specified by:** `getHeader` trong interface `HttpServletRequest`.
* **Parameters:** `name` - tên của header.
* **Returns:** Chuỗi `String` chứa giá trị của header được yêu cầu, hoặc `null` nếu không có header tên đó.

---

#### 5. `getHeaders(String name)`

* **Cú pháp:** `public java.util.Enumeration getHeaders(String name)`
* **Mô tả:** Hành vi mặc định là trả về kết quả của `getHeaders(String name)` trên đối tượng request được bọc.
* **Specified by:** `getHeaders` trong interface `HttpServletRequest`.
* **Parameters:** `name` - tên của header.
* **Returns:** Một `Enumeration` chứa tất cả các giá trị của header đó. Nếu không có header nào, trả về enumeration rỗng. Nếu container không cho phép truy cập, trả về `null`.

---

#### 6. `getHeaderNames()`

* **Cú pháp:** `public java.util.Enumeration getHeaderNames()`
* **Mô tả:** Hành vi mặc định là trả về kết quả của `getHeaderNames()` trên đối tượng request được bọc.
* **Specified by:** `getHeaderNames` trong interface `HttpServletRequest`.
* **Returns:** Một `Enumeration` chứa tất cả các tên header gửi kèm request; nếu không có header nào, trả về enumeration rỗng; nếu container không hỗ trợ, trả về `null`.

---

#### 7. `getIntHeader(String name)`

* **Cú pháp:** `public int getIntHeader(String name)`
* **Mô tả:** Hành vi mặc định là trả về kết quả của `getIntHeader(String name)` trên đối tượng request được bọc.
* **Specified by:** `getIntHeader` trong interface `HttpServletRequest`.
* **Parameters:** `name` - tên của request header.
* **Returns:** Số nguyên `int` biểu diễn giá trị của header, hoặc `-1` nếu request không có header tên này.

---

#### 8. `getMethod()`

* **Cú pháp:** `public String getMethod()`
* **Mô tả:** Hành vi mặc định là trả về kết quả của `getMethod()` trên đối tượng request được bọc.
* **Specified by:** `getMethod` trong interface `HttpServletRequest`.
* **Returns:** Chuỗi `String` chỉ định tên phương thức HTTP được dùng gửi request (ví dụ: `GET`, `POST`, `PUT`, `DELETE`).

---

#### 9. `getPathInfo()`

* **Cú pháp:** `public String getPathInfo()`
* **Mô tả:** Hành vi mặc định là trả về kết quả của `getPathInfo()` trên đối tượng request được bọc.
* **Specified by:** `getPathInfo` trong interface `HttpServletRequest`.
* **Returns:** Chuỗi `String` đã được giải mã do web container xử lý, chứa thông tin đường dẫn bổ sung nằm sau servlet path và trước query string; trả về `null` nếu URL không có đường dẫn bổ sung.

---

#### 10. `getPathTranslated()`

* **Cú pháp:** `public String getPathTranslated()`
* **Mô tả:** Hành vi mặc định là trả về kết quả của `getPathTranslated()` trên đối tượng request được bọc.
* **Specified by:** `getPathTranslated` trong interface `HttpServletRequest`.
* **Returns:** Chuỗi `String` chỉ định đường dẫn thực tế (real path) trên ổ đĩa, hoặc `null` nếu URL không có đường dẫn bổ sung.

---

#### 11. `getContextPath()`

* **Cú pháp:** `public String getContextPath()`
* **Mô tả:** Hành vi mặc định là trả về kết quả của `getContextPath()` trên đối tượng request được bọc.
* **Specified by:** `getContextPath` trong interface `HttpServletRequest`.
* **Returns:** Chuỗi `String` chỉ định phần Request URI xác định ngữ cảnh (context) của ứng dụng web.

---

#### 12. `getQueryString()`

* **Cú pháp:** `public String getQueryString()`
* **Mô tả:** Hành vi mặc định là trả về kết quả của `getQueryString()` trên đối tượng request được bọc.
* **Specified by:** `getQueryString` trong interface `HttpServletRequest`.
* **Returns:** Chuỗi `String` chứa đoạn query string hoặc `null` nếu URL không chứa query string. Giá trị này chưa được decode bởi container.

---

#### 13. `getRemoteUser()`

* **Cú pháp:** `public String getRemoteUser()`
* **Mô tả:** Hành vi mặc định là trả về kết quả của `getRemoteUser()` trên đối tượng request được bọc.
* **Specified by:** `getRemoteUser` trong interface `HttpServletRequest`.
* **Returns:** Chuỗi `String` chỉ định tên đăng nhập của user gửi request này, hoặc `null` nếu chưa xác định được user.

---

#### 14. `isUserInRole(String role)`

* **Cú pháp:** `public boolean isUserInRole(String role)`
* **Mô tả:** Hành vi mặc định là trả về kết quả của `isUserInRole(String role)` trên đối tượng request được bọc.
* **Specified by:** `isUserInRole` trong interface `HttpServletRequest`.
* **Parameters:** `role` - tên vai trò cần kiểm tra.
* **Returns:** `true` nếu user gửi request thuộc vai trò chỉ định; `false` nếu không thuộc hoặc chưa được xác thực.

---

#### 15. `getUserPrincipal()`

* **Cú pháp:** `public java.security.Principal getUserPrincipal()`
* **Mô tả:** Hành vi mặc định là trả về kết quả của `getUserPrincipal()` trên đối tượng request được bọc.
* **Specified by:** `getUserPrincipal` trong interface `HttpServletRequest`.
* **Returns:** Đối tượng `java.security.Principal` chứa tên của user gửi request; trả về `null` nếu chưa xác thực.

---

#### 16. `getRequestedSessionId()`

* **Cú pháp:** `public String getRequestedSessionId()`
* **Mô tả:** Hành vi mặc định là trả về kết quả của `getRequestedSessionId()` trên đối tượng request được bọc.
* **Specified by:** `getRequestedSessionId` trong interface `HttpServletRequest`.
* **Returns:** Chuỗi `String` chứa Session ID do client gửi lên, hoặc `null` nếu request không chứa Session ID.
* **See Also:** `isRequestedSessionIdValid()`

---

#### 17. `getRequestURI()`

* **Cú pháp:** `public String getRequestURI()`
* **Mô tả:** Hành vi mặc định là trả về kết quả của `getRequestURI()` trên đối tượng request được bọc.
* **Specified by:** `getRequestURI` trong interface `HttpServletRequest`.
* **Returns:** Chuỗi `String` chứa đoạn URL từ protocol name cho tới trước query string.

---

#### 18. `getRequestURL()`

* **Cú pháp:** `public StringBuffer getRequestURL()`
* **Mô tả:** Hành vi mặc định là trả về kết quả của `getRequestURL()` trên đối tượng request được bọc.
* **Specified by:** `getRequestURL` trong interface `HttpServletRequest`.
* **Returns:** Đối tượng `StringBuffer` chứa URL tái cấu trúc đầy đủ.

---

#### 19. `getServletPath()`

* **Cú pháp:** `public String getServletPath()`
* **Mô tả:** Hành vi mặc định là trả về kết quả của `getServletPath()` trên đối tượng request được bọc.
* **Specified by:** `getServletPath` trong interface `HttpServletRequest`.
* **Returns:** Chuỗi `String` chứa tên hoặc đường dẫn của Servlet được gọi trong URL; trả về chuỗi rỗng `""` nếu servlet được khớp bằng pattern `"/*"`.

---

#### 20. `getSession(boolean create)`

* **Cú pháp:** `public HttpSession getSession(boolean create)`
* **Mô tả:** Hành vi mặc định là trả về kết quả của `getSession(boolean create)` trên đối tượng request được bọc.
* **Specified by:** `getSession` trong interface `HttpServletRequest`.
* **Parameters:** `create` - `true` để tạo session mới nếu chưa có; `false` để trả về `null` nếu chưa có session hợp lệ.
* **Returns:** Đối tượng `HttpSession` liên kết với request này hoặc `null`.

---

#### 21. `getSession()`

* **Cú pháp:** `public HttpSession getSession()`
* **Mô tả:** Hành vi mặc định là trả về kết quả của `getSession()` trên đối tượng request được bọc. Tương đương với `getSession(true)`.
* **Specified by:** `getSession` trong interface `HttpServletRequest`.
* **Returns:** Đối tượng `HttpSession` liên kết với request này.

---

#### 22. `isRequestedSessionIdValid()`

* **Cú pháp:** `public boolean isRequestedSessionIdValid()`
* **Mô tả:** Hành vi mặc định là trả về kết quả của `isRequestedSessionIdValid()` trên đối tượng request được bọc.
* **Specified by:** `isRequestedSessionIdValid` trong interface `HttpServletRequest`.
* **Returns:** `true` nếu Session ID từ client khớp với một session hợp lệ đang hoạt động; ngược lại trả về `false`.

---

#### 23. `isRequestedSessionIdFromCookie()`

* **Cú pháp:** `public boolean isRequestedSessionIdFromCookie()`
* **Mô tả:** Hành vi mặc định là trả về kết quả của `isRequestedSessionIdFromCookie()` trên đối tượng request được bọc.
* **Specified by:** `isRequestedSessionIdFromCookie` trong interface `HttpServletRequest`.
* **Returns:** `true` nếu Session ID được gửi từ client thông qua Cookie; ngược lại `false`.

---

#### 24. `isRequestedSessionIdFromURL()`

* **Cú pháp:** `public boolean isRequestedSessionIdFromURL()`
* **Mô tả:** Hành vi mặc định là trả về kết quả của `isRequestedSessionIdFromURL()` trên đối tượng request được bọc.
* **Specified by:** `isRequestedSessionIdFromURL` trong interface `HttpServletRequest`.
* **Returns:** `true` nếu Session ID được đính kèm trực tiếp trong đường dẫn URL; ngược lại `false`.

---

#### 25. `isRequestedSessionIdFromUrl()`

* **Cú pháp:** `public boolean isRequestedSessionIdFromUrl()`
* **Mô tả:** *(Deprecated)* Phương thức cũ, giữ lại để tương thích ngược. Hành vi mặc định trả về kết quả của `isRequestedSessionIdFromUrl()` trên đối tượng request được bọc.
* **Specified by:** `isRequestedSessionIdFromUrl` trong interface `HttpServletRequest`.

---

## 5. Ví dụ triển khai mã nguồn thực tế (Code Examples)

### Ví dụ 1: Custom Wrapper thêm/ghi đè HTTP Headers

Do `HttpServletRequest` của Servlet API không cung cấp phương thức `setHeader` hay `addHeader`, lập trình viên phải tạo một subclass kế thừa `HttpServletRequestWrapper` để bổ sung tính năng này.

```java
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.*;

public class CustomHeaderRequestWrapper extends HttpServletRequestWrapper {

    private final Map<String, String> customHeaders;

    public CustomHeaderRequestWrapper(HttpServletRequest request) {
        super(request);
        this.customHeaders = new HashMap<>();
    }

    // Phương thức tùy chỉnh để thêm Header mới
    public void addHeader(String name, String value) {
        this.customHeaders.put(name, value);
    }

    @Override
    public String getHeader(String name) {
        // Kiểm tra xem header có nằm trong danh sách custom không
        if (customHeaders.containsKey(name)) {
            return customHeaders.get(name);
        }
        // Nếu không, gọi tới phương thức gốc của Request
        return super.getHeader(name);
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        Set<String> names = new HashSet<>(customHeaders.keySet());

        // Lấy danh sách tên header từ request gốc và gộp lại
        Enumeration<String> e = super.getHeaderNames();
        while (e.hasMoreElements()) {
            names.add(e.nextElement());
        }

        return Collections.enumeration(names);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        if (customHeaders.containsKey(name)) {
            List<String> values = new ArrayList<>();
            values.add(customHeaders.get(name));
            return Collections.enumeration(values);
        }
        return super.getHeaders(name);
    }
}

```

---

### Ví dụ 2: Tự động Trim khoảng trắng và lọc ký tự độc hại XSS trong Parameter

Sử dụng Wrapper kết hợp với `Filter` để can thiệp vào các hàm `getParameter()` và `getParameterValues()`, tự động làm sạch dữ liệu trước khi chuyển giao request cho Servlet xử lý.

#### Custom Request Wrapper (`SanitizeRequestWrapper.java`):

```java
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class SanitizeRequestWrapper extends HttpServletRequestWrapper {

    public SanitizeRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    @Override
    public String getParameter(String name) {
        String value = super.getParameter(name);
        return sanitize(value);
    }

    @Override
    public String[] getParameterValues(String name) {
        String[] values = super.getParameterValues(name);
        if (values == null) {
            return null;
        }
        String[] cleanValues = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            cleanValues[i] = sanitize(values[i]);
        }
        return cleanValues;
    }

    // Hàm tiện ích loại bỏ khoảng trắng dư thừa và mã hóa ký tự HTML cơ bản
    private String sanitize(String input) {
        if (input == null) {
            return null;
        }
        return input.trim()
                    .replaceAll("<", "&lt;")
                    .replaceAll(">", "&gt;");
    }
}

```

#### Tích hợp Wrapper vào Filter (`XssFilter.java`):

```java
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@WebFilter(urlPatterns = "/*")
public class XssFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        if (request instanceof HttpServletRequest) {
            HttpServletRequest httpRequest = (HttpServletRequest) request;

            // Bọc request gốc bằng Wrapper đã tinh chỉnh
            SanitizeRequestWrapper wrappedRequest = new SanitizeRequestWrapper(httpRequest);

            // Chuyển request đã bọc sang Filter/Servlet tiếp theo
            chain.doFilter(wrappedRequest, response);
        } else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {}
}

```