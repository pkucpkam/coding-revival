# 09-http-servlet-response

# Document: `javax.servlet.http.HttpServletResponse`

* **Package:** `javax.servlet.http`
* **Interface:** `public interface HttpServletResponse extends ServletResponse`
* **All Superinterfaces:** `javax.servlet.ServletResponse`
* **All Known Implementing Classes:** `javax.servlet.http.HttpServletResponseWrapper`
* **See Also:** `javax.servlet.ServletResponse`

---

## 1. Tổng quan (Overview)

Interface `HttpServletResponse` mở rộng từ `ServletResponse` nhằm cung cấp các tính năng dành riêng cho giao thức HTTP khi gửi phản hồi (response) từ Server về Client. Ví dụ: nó cung cấp các phương thức để làm việc với các HTTP Headers, HTTP Status Codes và Cookies.

Servlet Container sẽ tự động tạo đối tượng `HttpServletResponse` và truyền nó dưới dạng một tham số vào các phương thức xử lý yêu cầu của Servlet (như `doGet`, `doPost`, `doPut`, `doDelete`,...).

---

## 2. Tóm tắt Field & Phương thức (Summary)

### Field Summary (HTTP Status Codes)

Lớp cung cấp danh sách đầy đủ các hằng số trạng thái HTTP (Status Codes):

* **Informational (1xx):** `SC_CONTINUE` (100), `SC_SWITCHING_PROTOCOLS` (101).
* **Successful (2xx):** `SC_OK` (200), `SC_CREATED` (201), `SC_ACCEPTED` (202), `SC_NON_AUTHORITATIVE_INFORMATION` (203), `SC_NO_CONTENT` (204), `SC_RESET_CONTENT` (205), `SC_PARTIAL_CONTENT` (206).
* **Redirection (3xx):** `SC_MULTIPLE_CHOICES` (300), `SC_MOVED_PERMANENTLY` (301), `SC_MOVED_TEMPORARILY` (302), `SC_FOUND` (302), `SC_SEE_OTHER` (303), `SC_NOT_MODIFIED` (304), `SC_USE_PROXY` (305), `SC_TEMPORARY_REDIRECT` (307).
* **Client Error (4xx):** `SC_BAD_REQUEST` (400), `SC_UNAUTHORIZED` (401), `SC_PAYMENT_REQUIRED` (402), `SC_FORBIDDEN` (403), `SC_NOT_FOUND` (404), `SC_METHOD_NOT_ALLOWED` (405), `SC_NOT_ACCEPTABLE` (406), `SC_PROXY_AUTHENTICATION_REQUIRED` (407), `SC_REQUEST_TIMEOUT` (408), `SC_CONFLICT` (409), `SC_GONE` (410), `SC_LENGTH_REQUIRED` (411), `SC_PRECONDITION_FAILED` (412), `SC_REQUEST_ENTITY_TOO_LARGE` (413), `SC_REQUEST_URI_TOO_LONG` (414), `SC_UNSUPPORTED_MEDIA_TYPE` (415), `SC_REQUESTED_RANGE_NOT_SATISFIABLE` (416), `SC_EXPECTATION_FAILED` (417).
* **Server Error (5xx):** `SC_INTERNAL_SERVER_ERROR` (500), `SC_NOT_IMPLEMENTED` (501), `SC_BAD_GATEWAY` (502), `SC_SERVICE_UNAVAILABLE` (503), `SC_GATEWAY_TIMEOUT` (504), `SC_HTTP_VERSION_NOT_SUPPORTED` (505).

---

### Method Summary

| Kiểu trả về | Phương thức | Mô tả |
| --- | --- | --- |
| `void` | `addCookie(Cookie cookie)` | Thêm Cookie chỉ định vào Response Header. |
| `void` | `addDateHeader(String name, long date)` | Thêm một Response Header kiểu thời gian (miligiây). |
| `void` | `addHeader(String name, String value)` | Thêm một Response Header kiểu chuỗi `String`. |
| `void` | `addIntHeader(String name, int value)` | Thêm một Response Header kiểu số nguyên `int`. |
| `boolean` | `containsHeader(String name)` | Kiểm tra xem một Response Header đã được thiết lập hay chưa. |
| `String` | `encodeRedirectUrl(String url)` | *(Deprecated)* Dùng `encodeRedirectURL(String url)` thay thế. |
| `String` | `encodeRedirectURL(String url)` | Mã hóa URL được sử dụng trong phương thức `sendRedirect`. |
| `String` | `encodeUrl(String url)` | *(Deprecated)* Dùng `encodeURL(String url)` thay thế. |
| `String` | `encodeURL(String url)` | Mã hóa URL bằng cách nhúng Session ID vào URL nếu trình duyệt không hỗ trợ Cookie. |
| `void` | `sendError(int sc)` | Gửi phản hồi báo lỗi về Client bằng mã trạng thái chỉ định và xóa buffer. |
| `void` | `sendError(int sc, String msg)` | Gửi phản hồi báo lỗi về Client bằng mã trạng thái và thông điệp mô tả. |
| `void` | `sendRedirect(String location)` | Chuyển hướng (Redirect) tạm thời yêu cầu của Client tới một URL mới. |
| `void` | `setDateHeader(String name, long date)` | Đặt giá trị cho Response Header kiểu thời gian (ghi đè nếu đã tồn tại). |
| `void` | `setHeader(String name, String value)` | Đặt giá trị cho Response Header kiểu `String` (ghi đè nếu đã tồn tại). |
| `void` | `setIntHeader(String name, int value)` | Đặt giá trị cho Response Header kiểu `int` (ghi đè nếu đã tồn tại). |
| `void` | `setStatus(int sc)` | Đặt HTTP Status Code cho phản hồi khi không có lỗi (vd: `200 OK`). |
| `void` | `setStatus(int sc, String sm)` | *(Deprecated)* Đặt Status Code kèm thông điệp (dùng `sendError` hoặc `setStatus(int)` thay thế). |

---

## 3. Chi tiết Hằng số & Tất cả các phương thức (Detail)

---

### Field Detail (Status Codes Constants)

Các hằng số mã trạng thái HTTP dùng làm giá trị tham số cho `setStatus()` hoặc `sendError()`:

* **`SC_CONTINUE`** (`int 100`): Yêu cầu đã nhận, Client có thể tiếp tục gửi body.
* **`SC_SWITCHING_PROTOCOLS`** (`int 101`): Server đang chuyển đổi giao thức theo header `Upgrade`.
* **`SC_OK`** (`int 200`): Yêu cầu đã thành công bình thường.
* **`SC_CREATED`** (`int 201`): Yêu cầu thành công và một tài nguyên mới đã được tạo trên Server.
* **`SC_ACCEPTED`** (`int 202`): Yêu cầu đã được chấp nhận để xử lý nhưng chưa hoàn thành.
* **`SC_NON_AUTHORITATIVE_INFORMATION`** (`int 203`): Thông tin meta trả về không xuất phát trực tiếp từ Server gốc.
* **`SC_NO_CONTENT`** (`int 204`): Yêu cầu thành công nhưng không có nội dung nào trả về trong body.
* **`SC_RESET_CONTENT`** (`int 205`): Khuyên Client nên reset lại view/form đã gửi request.
* **`SC_PARTIAL_CONTENT`** (`int 206`): Server đã xử lý thành công yêu cầu GET một phần dữ liệu (Byte Range).
* **`SC_MULTIPLE_CHOICES`** (`int 300`): Tài nguyên yêu cầu có nhiều đại diện khác nhau.
* **`SC_MOVED_PERMANENTLY`** (`int 301`): Tài nguyên đã được chuyển vĩnh viễn sang URI mới.
* **`SC_MOVED_TEMPORARILY`** (`int 302`): Tài nguyên tạm thời ở địa chỉ khác (đã cũ, nên dùng `SC_FOUND`).
* **`SC_FOUND`** (`int 302`): Tài nguyên tạm thời cư trú ở một URI khác.
* **`SC_SEE_OTHER`** (`int 303`): Phản hồi của request có thể tìm thấy ở một URI khác bằng phương thức GET.
* **`SC_NOT_MODIFIED`** (`int 304`): Yêu cầu Conditional GET nhận thấy tài nguyên không bị thay đổi.
* **`SC_USE_PROXY`** (`int 305`): Tài nguyên bắt buộc phải truy cập qua Proxy chỉ định trong header `Location`.
* **`SC_TEMPORARY_REDIRECT`** (`int 307`): Tài nguyên tạm thời chuyển sang URI khác.
* **`SC_BAD_REQUEST`** (`int 400`): Cú pháp request của Client không hợp lệ.
* **`SC_UNAUTHORIZED`** (`int 401`): Request yêu cầu phải có xác thực HTTP.
* **`SC_PAYMENT_REQUIRED`** (`int 402`): Dành riêng cho tương lai.
* **`SC_FORBIDDEN`** (`int 403`): Server hiểu request nhưng từ chối thực thi (không có quyền truy cập).
* **`SC_NOT_FOUND`** (`int 404`): Không tìm thấy tài nguyên yêu cầu trên Server.
* **`SC_METHOD_NOT_ALLOWED`** (`int 405`): Phương thức HTTP không được hỗ trợ cho tài nguyên này.
* **`SC_NOT_ACCEPTABLE`** (`int 406`): Đặc tính nội dung không tương thích với các header `Accept` của Client.
* **`SC_PROXY_AUTHENTICATION_REQUIRED`** (`int 407`): Client cần phải xác thực với Proxy trước.
* **`SC_REQUEST_TIMEOUT`** (`int 408`): Quá thời gian chờ request từ Client.
* **`SC_CONFLICT`** (`int 409`): Request xung đột với trạng thái hiện tại của tài nguyên.
* **`SC_GONE`** (`int 410`): Tài nguyên không còn tồn tại trên Server và không có địa chỉ chuyển tiếp.
* **`SC_LENGTH_REQUIRED`** (`int 411`): Thiếu header `Content-Length` bắt buộc.
* **`SC_PRECONDITION_FAILED`** (`int 412`): Điều kiện tiên quyết trong header của request bị đánh giá là false.
* **`SC_REQUEST_ENTITY_TOO_LARGE`** (`int 413`): Dữ liệu gửi lên vượt quá khả năng xử lý của Server.
* **`SC_REQUEST_URI_TOO_LONG`** (`int 414`): Request-URI quá dài.
* **`SC_UNSUPPORTED_MEDIA_TYPE`** (`int 415`): Định dạng dữ liệu gửi lên không được hỗ trợ.
* **`SC_REQUESTED_RANGE_NOT_SATISFIABLE`** (`int 416`): Khoảng byte dữ liệu yêu cầu không thể đáp ứng.
* **`SC_EXPECTATION_FAILED`** (`int 417`): Server không thể đáp ứng yêu cầu trong header `Expect`.
* **`SC_INTERNAL_SERVER_ERROR`** (`int 500`): Lỗi nội bộ phía HTTP Server.
* **`SC_NOT_IMPLEMENTED`** (`int 501`): Server không hỗ trợ chức năng để đáp ứng request.
* **`SC_BAD_GATEWAY`** (`int 502`): Gateway/Proxy nhận phản hồi không hợp lệ từ server tuyến trên.
* **`SC_SERVICE_UNAVAILABLE`** (`int 503`): Server tạm thời bị quá tải hoặc đang bảo trì.
* **`SC_GATEWAY_TIMEOUT`** (`int 504`): Gateway/Proxy bị hết thời gian chờ phản hồi từ server tuyến trên.
* **`SC_HTTP_VERSION_NOT_SUPPORTED`** (`int 505`): Server từ chối hỗ trợ phiên bản giao thức HTTP của request.

---

### Method Detail

#### 1. `addCookie(Cookie cookie)`

* **Cú pháp:** `void addCookie(Cookie cookie)`
* **Mô tả:** Thêm một đối tượng `Cookie` vào Response. Có thể gọi phương thức này nhiều lần để thêm nhiều cookie.
* **Parameters:** `cookie` - đối tượng `Cookie` gửi về client.

---

#### 2. `containsHeader(String name)`

* **Cú pháp:** `boolean containsHeader(String name)`
* **Mô tả:** Kiểm tra xem header chỉ định đã được đặt trong response hay chưa.
* **Parameters:** `name` - tên header.
* **Returns:** `true` nếu header đã tồn tại; `false` nếu chưa.

---

#### 3. `encodeURL(String url)`

* **Cú pháp:** `String encodeURL(String url)`
* **Mô tả:** Mã hóa URL chỉ định bằng cách đính kèm Session ID vào URL nếu trình duyệt không hỗ trợ Cookie hoặc nếu URL Rewriting được kích hoạt.
* **Parameters:** `url` - chuỗi URL cần mã hóa.
* **Returns:** URL đã mã hóa nếu cần thiết; hoặc URL gốc nếu không cần mã hóa.

---

#### 4. `encodeRedirectURL(String url)`

* **Cú pháp:** `String encodeRedirectURL(String url)`
* **Mô tả:** Mã hóa URL dành riêng cho phương thức `sendRedirect`. Quy tắc kiểm tra Session ID cho chuyển hướng có thể khác với liên kết thông thường.
* **Parameters:** `url` - chuỗi URL chuyển hướng.
* **Returns:** URL đã mã hóa nếu cần thiết; hoặc URL gốc nếu không.
* **See Also:** `sendRedirect(String)`

---

#### 5. `encodeUrl(String url)` & `encodeRedirectUrl(String url)`

* **Mô tả:** *(Deprecated)* Các phương thức cũ từ phiên bản Servlet 2.1, lần lượt được thay thế bằng `encodeURL` và `encodeRedirectURL`.

---

#### 6. `sendError(int sc, String msg)`

* **Cú pháp:** `void sendError(int sc, String msg) throws java.io.IOException`
* **Mô tả:** Gửi phản hồi báo lỗi về Client với mã trạng thái và thông điệp chi tiết. Phương thức này tự động xóa response buffer và đặt Content-Type thành `"text/html"`. Response sẽ đi vào trạng thái đã committed.
* **Parameters:**
* `sc`: Mã trạng thái lỗi (ví dụ: `SC_NOT_FOUND`, `SC_FORBIDDEN`).
* `msg`: Thông điệp mô tả lỗi.


* **Throws:**
* `java.io.IOException`: Nếu xảy ra lỗi I/O.
* `java.lang.IllegalStateException`: Nếu response đã committed trước đó.



---

#### 7. `sendError(int sc)`

* **Cú pháp:** `void sendError(int sc) throws java.io.IOException`
* **Mô tả:** Tương tự như `sendError(int, String)` nhưng sử dụng thông điệp lỗi mặc định của server tương ứng với Status Code.
* **Parameters:** `sc` - mã trạng thái lỗi.
* **Throws:** `java.io.IOException`, `java.lang.IllegalStateException`.

---

#### 8. `sendRedirect(String location)`

* **Cú pháp:** `void sendRedirect(String location) throws java.io.IOException`
* **Mô tả:** Gửi một HTTP Redirect (mã 302) yêu cầu Client chuyển hướng đến vị trí URL mới chỉ định. Chấp nhận cả URL tuyệt đối và URL tương đối.
* **Parameters:** `location` - đường dẫn URL chuyển hướng.
* **Throws:**
* `java.io.IOException`: Nếu có lỗi I/O.
* `java.lang.IllegalStateException`: Nếu response đã committed.



---

#### 9. `setDateHeader(String name, long date)` & `addDateHeader(String name, long date)`

* **Cú pháp:**
* `void setDateHeader(String name, long date)`
* `void addDateHeader(String name, long date)`


* **Mô tả:**
* `setDateHeader`: Đặt giá trị thời gian (timestamp ms từ Epoch) cho header. Ghi đè nếu header đã tồn tại.
* `addDateHeader`: Bổ sung giá trị thời gian cho header (cho phép một header chứa nhiều giá trị).



---

#### 10. `setHeader(String name, String value)` & `addHeader(String name, String value)`

* **Cú pháp:**
* `void setHeader(String name, String value)`
* `void addHeader(String name, String value)`


* **Mô tả:**
* `setHeader`: Thiết lập giá trị `String` cho header (ghi đè giá trị cũ).
* `addHeader`: Thêm một giá trị `String` mới cho header (hỗ trợ multi-value headers).



---

#### 11. `setIntHeader(String name, int value)` & `addIntHeader(String name, int value)`

* **Cú pháp:**
* `void setIntHeader(String name, int value)`
* `void addIntHeader(String name, int value)`


* **Mô tả:**
* `setIntHeader`: Thiết lập giá trị số nguyên `int` cho header (ghi đè giá trị cũ).
* `addIntHeader`: Thêm một giá trị số nguyên `int` cho header.



---

#### 12. `setStatus(int sc)`

* **Cú pháp:** `void setStatus(int sc)`
* **Mô tả:** Thiết lập Status Code cho phản hồi khi **không có lỗi** (ví dụ: `SC_OK`, `SC_CREATED`, `SC_NO_CONTENT`). Nếu muốn trả về lỗi, nên dùng `sendError()`.
* **Parameters:** `sc` - mã trạng thái HTTP.

---

#### 13. `setStatus(int sc, String sm)`

* **Mô tả:** *(Deprecated)* Đã ngừng sử dụng từ Servlet 2.1 do ý nghĩa không rõ ràng của tham số message. Dùng `setStatus(int)` để đặt mã thành công hoặc `sendError(int, String)` để báo lỗi.

---

## 4. Ví dụ triển khai mã nguồn thực tế (Code Examples)

### Ví dụ 1: Quản lý Header, Status Code và Cookie trong REST API Response

Tạo một Endpoint đăng ký thành công: Trả về **HTTP 201 Created**, thêm Cookie xác thực và cấu hình Header chống Caching.

```java
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/api/register")
public class RegisterServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        // 1. Thiết lập Status Code 201 (Created)
        resp.setStatus(HttpServletResponse.SC_CREATED);

        // 2. Thiết lập Header chống Cache dữ liệu
        resp.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        resp.setDateHeader("Expires", 0);

        // 3. Tạo và trả về Cookie cho Client
        Cookie authCookie = new Cookie("AUTH_TOKEN", "JWT_TOKEN_ABC123");
        authCookie.setMaxAge(24 * 60 * 60); // 1 ngày
        authCookie.setPath("/");
        authCookie.setHttpOnly(true);       // Bảo mật chống XSS
        
        resp.addCookie(authCookie);

        // 4. Định dạng kiểu nội dung trả về
        resp.setContentType("application/json; charset=UTF-8");
        resp.getWriter().write("{\"message\": \"User registered successfully\"}");
    }
}

```

---

### Ví dụ 2: Điều hướng (`sendRedirect`) kết hợp mã hóa URL (`encodeRedirectURL`)

Sử dụng `sendRedirect` để chuyển hướng người dùng sau khi đăng xuất, đảm bảo duy trì Session nếu trình duyệt không bật Cookie.

```java
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/logout-action")
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        // Hủy bỏ Session hiện tại
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        // Tạo URL chuyển hướng và mã hóa URL
        String targetUrl = req.getContextPath() + "/login.jsp?status=logged_out";
        String encodedUrl = resp.encodeRedirectURL(targetUrl);

        // Thực hiện chuyển hướng (HTTP 302)
        resp.sendRedirect(encodedUrl);
    }
}

```

---

### Ví dụ 3: Xử lý Báo lỗi chuẩn HTTP với `sendError`

Trả về các mã lỗi phù hợp (`401 Unauthorized` hoặc `404 Not Found`) khi dữ liệu không hợp lệ.

```java
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/download")
public class DownloadServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        String fileId = req.getParameter("id");
        String authToken = req.getHeader("Authorization");

        // 1. Kiểm tra xác thực -> Báo lỗi 401
        if (authToken == null || !authToken.startsWith("Bearer ")) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Ban can cung cap Token hop le de tai file!");
            return;
        }

        // 2. Kiểm tra tài nguyên -> Báo lỗi 404
        if (fileId == null || !"FILE_100".equals(fileId)) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Tap tin khong ton tai tren he thong!");
            return;
        }

        // 3. Nếu thành công -> Trả về file
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("application/octet-stream");
        resp.setHeader("Content-Disposition", "attachment; filename=\"document.pdf\"");
        resp.getWriter().write("Binary content of document...");
    }
}

```