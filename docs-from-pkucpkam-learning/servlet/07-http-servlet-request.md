# 07-http-servlet-request

# Document: `javax.servlet.http.HttpServletRequest`

* **Package:** `javax.servlet.http`
* **Interface:** `public interface HttpServletRequest extends ServletRequest`
* **All Superinterfaces:** `javax.servlet.ServletRequest`
* **All Known Implementing Classes:** `javax.servlet.http.HttpServletRequestWrapper`
* **See Also:** `javax.servlet.ServletRequest`

---

## 1. Tổng quan (Overview)

Interface `HttpServletRequest` mở rộng từ `ServletResponse` nhằm cung cấp các thông tin chi tiết về yêu cầu HTTP (HTTP Request) gửi từ client tới Servlet.

Servlet Container sẽ tự động khởi tạo đối tượng `HttpServletRequest` và truyền nó dưới dạng tham số vào các phương thức xử lý dịch vụ của Servlet (như `doGet`, `doPost`, `doPut`, `doDelete`,...).

---

## 2. Tóm tắt Field & Phương thức (Summary)

### Field Summary (Authentication Schemes)

Lớp định nghĩa các hằng số nhận diện loại cơ chế xác thực HTTP:

| Hằng số | Giá trị (`String`) | Mô tả |
| --- | --- | --- |
| `BASIC_AUTH` | `"BASIC"` | Định danh chuỗi cho kiểu xác thực HTTP Basic. |
| `FORM_AUTH` | `"FORM"` | Định danh chuỗi cho kiểu xác thực Form-based. |
| `CLIENT_CERT_AUTH` | `"CLIENT_CERT"` | Định danh chuỗi cho kiểu xác thực Client Certificate (SSL/TLS). |
| `DIGEST_AUTH` | `"DIGEST"` | Định danh chuỗi cho kiểu xác thực HTTP Digest. |

---

### Method Summary

| Kiểu trả về | Phương thức | Mô tả |
| --- | --- | --- |
| `String` | `getAuthType()` | Trả về tên cơ chế xác thực dùng để bảo vệ servlet (null nếu chưa xác thực). |
| `String` | `getContextPath()` | Trả về đoạn URI xác định ngữ cảnh (context) của ứng dụng web. |
| `Cookie[]` | `getCookies()` | Trả về mảng chứa tất cả đối tượng `Cookie` mà client gửi kèm request. |
| `long` | `getDateHeader(String name)` | Trả về giá trị của request header dạng mốc thời gian `long` (ms từ Epoch). |
| `String` | `getHeader(String name)` | Trả về giá trị `String` của một request header theo tên. |
| `Enumeration<String>` | `getHeaderNames()` | Trả về danh sách tất cả các tên header có trong request. |
| `Enumeration<String>` | `getHeaders(String name)` | Trả về tất cả các giá trị của một request header cụ thể. |
| `int` | `getIntHeader(String name)` | Trả về giá trị dạng số nguyên `int` của một request header. |
| `String` | `getMethod()` | Trả về tên phương thức HTTP được sử dụng (ví dụ: `GET`, `POST`, `PUT`). |
| `String` | `getPathInfo()` | Trả về đường dẫn bổ sung sau servlet path và trước query string. |
| `String` | `getPathTranslated()` | Trả về đường dẫn thực tế trên đĩa sau khi đã chuyển đổi từ path info. |
| `String` | `getQueryString()` | Trả về chuỗi query string nằm sau đường dẫn trong URL. |
| `String` | `getRemoteUser()` | Trả về tên đăng nhập của user gửi request (nếu đã xác thực). |
| `String` | `getRequestedSessionId()` | Trả về Session ID được gửi từ phía client. |
| `String` | `getRequestURI()` | Trả về đoạn URL từ tên protocol cho tới trước query string. |
| `StringBuffer` | `getRequestURL()` | Tái cấu trúc lại URL đầy đủ mà client dùng để gửi request. |
| `String` | `getServletPath()` | Trả về đoạn đường dẫn URL gọi đến Servlet. |
| `HttpSession` | `getSession()` | Trả về `HttpSession` hiện tại, tự tạo mới nếu chưa có. |
| `HttpSession` | `getSession(boolean create)` | Trả về `HttpSession` hiện tại, tạo mới nếu `create = true` và chưa có. |
| `Principal` | `getUserPrincipal()` | Trả về đối tượng `java.security.Principal` chứa thông tin user đã xác thực. |
| `boolean` | `isRequestedSessionIdFromCookie()` | Kiểm tra Session ID có được gửi qua Cookie hay không. |
| `boolean` | `isRequestedSessionIdFromUrl()` | *(Deprecated)* Dùng `isRequestedSessionIdFromURL()` thay thế. |
| `boolean` | `isRequestedSessionIdFromURL()` | Kiểm tra Session ID có được đính kèm trên URL hay không. |
| `boolean` | `isRequestedSessionIdValid()` | Kiểm tra Session ID từ client có còn hợp lệ hay không. |
| `boolean` | `isUserInRole(String role)` | Kiểm tra user đã xác thực có thuộc về một phân quyền (role) cụ thể không. |

---

## 3. Chi tiết Hằng số & Tất cả các phương thức (Detail)

---

### Field Detail

#### 1. `BASIC_AUTH`

* **Cú pháp:** `public static final String BASIC_AUTH = "BASIC";`
* **Mô tả:** Định danh cho phương thức xác thực Basic.

#### 2. `FORM_AUTH`

* **Cú pháp:** `public static final String FORM_AUTH = "FORM";`
* **Mô tả:** Định danh cho phương thức xác thực Form.

#### 3. `CLIENT_CERT_AUTH`

* **Cú pháp:** `public static final String CLIENT_CERT_AUTH = "CLIENT_CERT";`
* **Mô tả:** Định danh cho phương thức xác thực chứng chỉ Client.

#### 4. `DIGEST_AUTH`

* **Cú pháp:** `public static final String DIGEST_AUTH = "DIGEST";`
* **Mô tả:** Định danh cho phương thức xác thực Digest.

---

### Method Detail

#### 1. `getAuthType()`

* **Cú pháp:** `String getAuthType()`
* **Mô tả:** Trả về tên của scheme xác thực dùng bảo vệ servlet. Trả về `null` nếu request chưa được xác thực. Tương đương biến CGI `AUTH_TYPE`.
* **Returns:** Một trong các hằng số `BASIC_AUTH`, `FORM_AUTH`, `CLIENT_CERT_AUTH`, `DIGEST_AUTH` hoặc `null`.

---

#### 2. `getCookies()`

* **Cú pháp:** `Cookie[] getCookies()`
* **Mô tả:** Trả về mảng chứa tất cả đối tượng `Cookie` gửi kèm request. Trả về `null` nếu không có cookie nào.
* **Returns:** Mảng `Cookie[]` hoặc `null`.

---

#### 3. `getDateHeader(String name)`

* **Cú pháp:** `long getDateHeader(String name)`
* **Mô tả:** Trả về giá trị của header kiểu ngày tháng (như `If-Modified-Since`) dưới dạng số ms tính từ 01/01/1970 GMT. Tên header không phân biệt hoa thường.
* **Parameters:** `name` - tên header.
* **Returns:** Số nguyên kiểu `long` (ms) hoặc `-1` nếu header không tồn tại.
* **Throws:** `IllegalArgumentException` nếu header không thể chuyển đổi sang ngày tháng.

---

#### 4. `getHeader(String name)`

* **Cú pháp:** `String getHeader(String name)`
* **Mô tả:** Trả về giá trị dạng `String` của header chỉ định. Tên không phân biệt hoa thường. Nếu có nhiều header trùng tên, trả về giá trị đầu tiên.
* **Parameters:** `name` - tên header.
* **Returns:** Chuỗi giá trị hoặc `null` nếu không tồn tại.

---

#### 5. `getHeaders(String name)`

* **Cú pháp:** `Enumeration getHeaders(String name)`
* **Mô tả:** Trả về tất cả giá trị của một header chỉ định (dành cho các header có nhiều giá trị như `Accept-Language`).
* **Parameters:** `name` - tên header.
* **Returns:** `Enumeration` chứa các chuỗi giá trị; hoặc enumeration rỗng nếu không có header; hoặc `null` nếu container không hỗ trợ.

---

#### 6. `getHeaderNames()`

* **Cú pháp:** `Enumeration getHeaderNames()`
* **Mô tả:** Trả về tất cả tên các header có trong request.
* **Returns:** `Enumeration` chứa danh sách tên header, enumeration rỗng nếu không có header, hoặc `null` nếu container từ chối truy cập.

---

#### 7. `getIntHeader(String name)`

* **Cú pháp:** `int getIntHeader(String name)`
* **Mô tả:** Trả về giá trị kiểu số nguyên `int` của header.
* **Parameters:** `name` - tên header.
* **Returns:** Số nguyên `int` hoặc `-1` nếu header không tồn tại.
* **Throws:** `NumberFormatException` nếu không thể ép kiểu sang `int`.

---

#### 8. `getMethod()`

* **Cú pháp:** `String getMethod()`
* **Mô tả:** Trả về tên phương thức HTTP được dùng (ví dụ: `GET`, `POST`, `PUT`). Tương đương biến CGI `REQUEST_METHOD`.
* **Returns:** Chuỗi `String` chứa tên phương thức HTTP.

---

#### 9. `getPathInfo()`

* **Cú pháp:** `String getPathInfo()`
* **Mô tả:** Trả về đường dẫn bổ sung nằm sau servlet path và trước query string (bắt đầu bằng dấu `/`). Tương đương biến CGI `PATH_INFO`.
* **Returns:** Chuỗi đã được decode hoặc `null` nếu không có.

---

#### 10. `getPathTranslated()`

* **Cú pháp:** `String getPathTranslated()`
* **Mô tả:** Chuyển đổi đường dẫn ảo trong `getPathInfo()` thành đường dẫn thực tế trên hệ thống file. Tương đương biến CGI `PATH_TRANSLATED`.
* **Returns:** Chuỗi đường dẫn thực tế hoặc `null`.

---

#### 11. `getContextPath()`

* **Cú pháp:** `String getContextPath()`
* **Mô tả:** Trả về phần đường dẫn chỉ định Context của ứng dụng web. Luôn bắt đầu bằng `/` và không kết thúc bằng `/`. Với root context sẽ trả về `""`.
* **Returns:** Chuỗi context path.

---

#### 12. `getQueryString()`

* **Cú pháp:** `String getQueryString()`
* **Mô tả:** Trả về chuỗi query string nằm sau đường dẫn trong URL. Chưa được decode bởi container. Tương đương biến CGI `QUERY_STRING`.
* **Returns:** Chuỗi query string hoặc `null`.

---

#### 13. `getRemoteUser()`

* **Cú pháp:** `String getRemoteUser()`
* **Mô tả:** Trả về tên đăng nhập của user đã xác thực. Tương đương biến CGI `REMOTE_USER`.
* **Returns:** Chuỗi username hoặc `null` nếu chưa đăng nhập.

---

#### 14. `isUserInRole(String role)`

* **Cú pháp:** `boolean isUserInRole(String role)`
* **Mô tả:** Kiểm tra user đã xác thực có thuộc về một phân quyền (role) logic được định nghĩa trong deployment descriptor hay không.
* **Parameters:** `role` - tên role cần kiểm tra.
* **Returns:** `true` nếu user thuộc role; `false` nếu không thuộc hoặc chưa xác thực.

---

#### 15. `getUserPrincipal()`

* **Cú pháp:** `java.security.Principal getUserPrincipal()`
* **Mô tả:** Trả về đối tượng `Principal` chứa thông tin user đã xác thực.
* **Returns:** Đối tượng `Principal` hoặc `null` nếu chưa xác thực.

---

#### 16. `getRequestedSessionId()`

* **Cú pháp:** `String getRequestedSessionId()`
* **Mô tả:** Trả về Session ID do client gửi lên. Có thể không trùng với Session ID hợp lệ hiện tại.
* **Returns:** Chuỗi Session ID hoặc `null` nếu client không gửi.

---

#### 17. `getRequestURI()`

* **Cú pháp:** `String getRequestURI()`
* **Mô tả:** Trả về đoạn URL tính từ tên protocol cho tới trước query string. Không được decode bởi container.
* **Examples:**
* `POST /some/path.html HTTP/1.1` $\rightarrow$ `/some/path.html`
* `GET [http://foo.bar/a.html](http://foo.bar/a.html) HTTP/1.0` $\rightarrow$ `/a.html`
* `HEAD /xyz?a=b HTTP/1.1` $\rightarrow$ `/xyz`


* **Returns:** Chuỗi Request URI.

---

#### 18. `getRequestURL()`

* **Cú pháp:** `StringBuffer getRequestURL()`
* **Mô tả:** Tái cấu trúc lại URL đầy đủ mà client dùng gửi request (bao gồm protocol, server name, port number, server path, nhưng không gồm query string). Trả về `StringBuffer` giúp dễ dàng chỉnh sửa hoặc nối thêm tham số.
* **Returns:** Đối tượng `StringBuffer`.

---

#### 19. `getServletPath()`

* **Cú pháp:** `String getServletPath()`
* **Mô tả:** Trả về phần URL dùng để gọi Servlet. Bắt đầu bằng `/`. Trả về `""` nếu Servlet được gọi bằng pattern `/*`. Tương đương biến CGI `SCRIPT_NAME`.
* **Returns:** Chuỗi servlet path.

---

#### 20. `getSession(boolean create)`

* **Cú pháp:** `HttpSession getSession(boolean create)`
* **Mô tả:** Trả về `HttpSession` hiện tại. Nếu chưa có và `create = true`, tạo một session mới. Nếu `create = false` và chưa có, trả về `null`. Must call before response is committed.
* **Parameters:** `create` - `true` để tạo mới nếu cần; `false` để trả về `null`.
* **Returns:** Đối tượng `HttpSession` hoặc `null`.

---

#### 21. `getSession()`

* **Cú pháp:** `HttpSession getSession()`
* **Mô tả:** Tương đương với `getSession(true)`.
* **Returns:** Đối tượng `HttpSession`.

---

#### 22. `isRequestedSessionIdValid()`

* **Cú pháp:** `boolean isRequestedSessionIdValid()`
* **Mô tả:** Kiểm tra Session ID gửi từ client có hợp lệ trong session context hiện tại không.
* **Returns:** `true` nếu hợp lệ; `false` nếu không.

---

#### 23. `isRequestedSessionIdFromCookie()`

* **Cú pháp:** `boolean isRequestedSessionIdFromCookie()`
* **Mô tả:** Kiểm tra Session ID gửi lên có phải qua Cookie hay không.
* **Returns:** `true` nếu từ Cookie; ngược lại `false`.

---

#### 24. `isRequestedSessionIdFromURL()`

* **Cú pháp:** `boolean isRequestedSessionIdFromURL()`
* **Mô tả:** Kiểm tra Session ID gửi lên có phải đính kèm trên URL hay không.
* **Returns:** `true` nếu từ URL; ngược lại `false`.

---

#### 25. `isRequestedSessionIdFromUrl()`

* **Mô tả:** *(Deprecated)* Thay thế bằng `isRequestedSessionIdFromURL()` từ Servlet API 2.1.

---

## 4. Ví dụ triển khai mã nguồn thực tế (Code Examples)

### Ví dụ 1: Đọc thông tin Header, URL và Client trong Servlet

Xử lý đọc các thông số kĩ thuật từ `HttpServletRequest` để phục vụ logging hoặc kiểm tra thông tin thiết bị client.

```java
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

@WebServlet("/request-info")
public class RequestInfoServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        resp.setContentType("text/html; charset=UTF-8");
        PrintWriter out = resp.getWriter();

        out.println("<h2>Thông tin Request:</h2>");
        out.println("<p><b>HTTP Method:</b> " + req.getMethod() + "</p>");
        out.println("<p><b>Request URI:</b> " + req.getRequestURI() + "</p>");
        out.println("<p><b>Full Request URL:</b> " + req.getRequestURL().toString() + "</p>");
        out.println("<p><b>Context Path:</b> " + req.getContextPath() + "</p>");
        out.println("<p><b>Servlet Path:</b> " + req.getServletPath() + "</p>");
        out.println("<p><b>Query String:</b> " + req.getQueryString() + "</p>");

        // 1. Đọc User-Agent Header
        String userAgent = req.getHeader("User-Agent");
        out.println("<p><b>User-Agent:</b> " + userAgent + "</p>");

        // 2. Duyệt tất cả Headers
        out.println("<h3>Danh sách Headers:</h3><ul>");
        Enumeration<String> headerNames = req.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            out.println("<li><b>" + name + ":</b> " + req.getHeader(name) + "</li>");
        }
        out.println("</ul>");

        // 3. Đọc Cookies gửi lên
        out.println("<h3>Danh sách Cookies:</h3>");
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                out.println("<p>" + c.getName() + " = " + c.getValue() + "</p>");
            }
        } else {
            out.println("<p>Không có Cookie nào được gửi kèm.</p>");
        }
    }
}

```

---

### Ví dụ 2: Kiểm tra Phân quyền người dùng (Security & Roles)

Sử dụng các phương thức `getRemoteUser()`, `isUserInRole()`, và `getUserPrincipal()` để kiểm tra quyền truy cập.

```java
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;

@WebServlet("/admin/dashboard")
public class AdminDashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        resp.setContentType("text/html; charset=UTF-8");

        // 1. Kiểm tra trạng thái đăng nhập
        String remoteUser = req.getRemoteUser();
        Principal principal = req.getUserPrincipal();

        if (remoteUser == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Vui lòng đăng nhập để tiếp tục!");
            return;
        }

        // 2. Kiểm tra Vai trò (Role Check)
        if (req.isUserInRole("ADMIN")) {
            resp.getWriter().println("<h1>Chào mừng Admin: " + principal.getName() + "</h1>");
            resp.getWriter().println("<p>Loại xác thực: " + req.getAuthType() + "</p>");
        } else {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền truy cập vào trang Admin!");
        }
    }
}

```

---