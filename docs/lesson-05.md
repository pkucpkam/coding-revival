# 🚀 Bài 5 — `HttpServletRequest` & `HttpServletResponse`

Đây là bài mà bro sẽ bắt đầu thật sự “đụng” vào Servlet ở mức thực tế.

Nếu bài trước giúp bro hiểu Servlet chạy như thế nào, thì bài này giúp bro hiểu:

> “Một request đến từ browser thì dữ liệu của nó được đọc như thế nào, và server trả dữ liệu về cho browser bằng cách nào?”

Sau bài này bro sẽ tự tin đọc và dùng các API rất phổ biến:

```java
request.getParameter(...)
request.getHeader(...)
request.getCookies(...)

response.setStatus(...)
response.setHeader(...)
response.setContentType(...)
response.getWriter(...)
```

---

## 1. Hãy tưởng tượng một cuộc trao đổi đơn giản

Đừng nghĩ request/response là những khái niệm trừu tượng.

Hãy hình dung như sau:

```text
Browser như người khách
Server như người bán hàng
Request như câu hỏi của khách
Response như câu trả lời của người bán
```

Ví dụ:

```text
Khách hỏi: "Cho tôi xem sản phẩm A"
Người bán trả: "Đây là sản phẩm A"
```

Trong web, điều đó tương ứng là:

```text
Browser gửi request
Servlet đọc request
Servlet tạo response
Browser nhận response
```

---

## 2. Tổng quan trước khi đi sâu

Browser gửi một request như thế này:

```http
POST /login?redirect=/dashboard HTTP/1.1
Host: localhost:8080
Content-Type: application/x-www-form-urlencoded
Cookie: JSESSIONID=ABC123

username=phuc&password=123
```

Servlet nhận được như sau:

```java
doPost(HttpServletRequest request, HttpServletResponse response)
```

Có thể hiểu đơn giản là:

```text
request = dữ liệu khách gửi tới
response = dữ liệu server trả về
```

Sơ đồ rất dễ nhớ:

```text
Browser
  ↓
HTTP Request
  ↓
Servlet
  ├── đọc dữ liệu từ request
  └── tạo response để gửi về
```

---

## 3. `HttpServletRequest` là gì?

```java
HttpServletRequest request
```

Đây là object chứa thông tin của request hiện tại.

Bạn có thể nghĩ nó như một “thùng dữ liệu” mà browser gửi tới server.

Ví dụ browser gửi:

```http
GET /users?id=10 HTTP/1.1
Host: localhost:8080
Accept: application/json
```

Servlet có thể lấy dữ liệu từ đó bằng:

```java
request.getMethod();
request.getRequestURI();
request.getParameter("id");
request.getHeader("Accept");
```

---

## 4. `HttpServletResponse` là gì?

```java
HttpServletResponse response
```

Đây là object dùng để tạo response gửi về browser.

Bạn có thể nghĩ nó như một “cái hộp thư trả lời” mà server dùng để gửi dữ liệu lại cho client.

Ví dụ:

```java
response.setStatus(200);
response.setContentType("text/plain");
response.getWriter().println("Hello Phuc");
```

---

## 5. `getMethod()` — xem request là GET hay POST

```java
String method = request.getMethod();
```

Ví dụ:

```http
GET /users
```

thì:

```java
"GET"
```

Nếu là:

```http
POST /login
```

thì:

```java
"POST"
```

### Ví dụ đơn giản

```java
if ("POST".equals(request.getMethod())) {
    // xử lý POST
}
```

### Nhưng trong Servlet thường không cần tự kiểm tra

Nếu bạn override:

```java
doGet()
doPost()
```

thì container sẽ tự dispatch request cho đúng method.

```text
GET  → doGet()
POST → doPost()
```

---

## 6. `getRequestURI()` — xem đường dẫn request

Ví dụ browser gọi:

```http
GET /users/123 HTTP/1.1
```

thì:

```java
request.getRequestURI()
```

sẽ trả:

```text
/users/123
```

Nói cách khác, nó cho biết request đang đi tới đường dẫn nào.

---

## 7. `getParameter()` — cách lấy dữ liệu từ URL hoặc form

Đây là API cực kỳ quan trọng trong Servlet.

### 7.1 Lấy từ query string

Ví dụ URL:

```http
GET /users?id=10
```

Servlet:

```java
String id = request.getParameter("id");
```

Kết quả:

```text
"10"
```

### 7.2 Lấy từ form POST

Browser gửi form như sau:

```http
POST /login HTTP/1.1
Content-Type: application/x-www-form-urlencoded

username=phuc&password=123
```

Servlet có thể lấy:

```java
String username = request.getParameter("username");
String password = request.getParameter("password");
```

### 7.3 Một điều người mới hay nhầm

`getParameter()` không chỉ dùng cho GET.

Nó có thể lấy được cả:

- query parameters
- form data

Ví dụ:

```text
GET /login?redirect=/home
```

và:

```text
username=phuc&password=123
```

đều có thể lấy bằng:

```java
request.getParameter("redirect");
request.getParameter("username");
request.getParameter("password");
```

### 7.4 Nếu parameter không tồn tại

Ví dụ:

```http
GET /users
```

Servlet:

```java
String id = request.getParameter("id");
```

Kết quả là:

```java
null
```

Đây là điều quan trọng cần nhớ.

Không phải `""` đâu.

Nếu bạn làm:

```java
Long id = Long.parseLong(request.getParameter("id"));
```

và `id` không tồn tại thì sẽ lỗi.

Vì vậy nên validate trước:

```java
String idParam = request.getParameter("id");

if (idParam == null) {
    // xử lý thiếu parameter
}
```

---

## 8. `getParameterValues()` — khi một tên parameter có nhiều giá trị

Ví dụ URL:

```http
GET /search?tag=java&tag=servlet&tag=jsp
```

Bạn có thể lấy bằng:

```java
String[] tags = request.getParameterValues("tag");
```

Kết quả:

```text
["java", "servlet", "jsp"]
```

Dùng cho các tình huống như checkbox chọn nhiều mục.

---

## 9. `getHeader()` — đọc header của request

Header là metadata của request.

Ví dụ browser gửi:

```http
Accept: application/json
User-Agent: Chrome
Authorization: Bearer abc123
```

Servlet có thể đọc bằng:

```java
String accept = request.getHeader("Accept");
String authorization = request.getHeader("Authorization");
```

Kết quả:

```text
application/json
Bearer abc123
```

### Header và parameter khác nhau

```text
Parameter: dữ liệu của request theo dạng key=value
Header: metadata của request
```

Ví dụ:

```http
GET /users?id=10 HTTP/1.1
Accept: application/json
Authorization: Bearer abc
```

- `id=10` là parameter
- `Accept` và `Authorization` là header

```java
request.getParameter("id");     // lấy parameter
request.getHeader("Accept");    // lấy header
```

---

## 10. `getCookies()` — đọc cookie

Cookie là dữ liệu mà browser gửi lại cho server.

Ví dụ:

```http
Cookie: JSESSIONID=ABC123
```

Servlet đọc được bằng:

```java
Cookie[] cookies = request.getCookies();
```

Sau đó có thể lặp qua từng cookie:

```java
for (Cookie cookie : cookies) {
    System.out.println(cookie.getName());
    System.out.println(cookie.getValue());
}
```

Ví dụ:

```text
JSESSIONID = ABC123
```

---

## 11. `HttpServletResponse` — trả lời lại cho client

Bây giờ đổi chiều.

`HttpServletResponse` là object dùng để tạo response gửi về browser.

Bạn có thể nghĩ đây là “câu trả lời” mà server sẽ gửi lại.

Ví dụ:

```http
HTTP/1.1 200 OK
Content-Type: text/plain

Hello Phuc
```

Servlet tạo response như thế này:

```java
response.setStatus(200);
response.setContentType("text/plain");
response.getWriter().println("Hello Phuc");
```

---

## 12. `setStatus()` — đặt mã trạng thái HTTP

```java
response.setStatus(HttpServletResponse.SC_OK);
```

Đây là HTTP 200.

Nếu không tìm thấy dữ liệu:

```java
response.setStatus(HttpServletResponse.SC_NOT_FOUND);
```

thì là HTTP 404.

### Các mã thường gặp

```text
200 OK
201 Created
400 Bad Request
401 Unauthorized
403 Forbidden
404 Not Found
405 Method Not Allowed
500 Internal Server Error
```

---

## 13. `setContentType()` — nói cho browser biết nội dung là gì

Ví dụ:

```java
response.setContentType("text/plain");
```

Nói rằng response body là plain text.

Nếu là HTML:

```java
response.setContentType("text/html");
```

Nếu là JSON:

```java
response.setContentType("application/json");
```

### Charset cũng quan trọng

Nếu trả tiếng Việt, nên dùng:

```java
response.setContentType("text/plain;charset=UTF-8");
```

hoặc:

```java
response.setCharacterEncoding("UTF-8");
```

Nếu không đúng charset, bạn có thể thấy lỗi font như:

```text
Xin chÃ o PhÃºc
```

---

## 14. `getWriter()` — ghi text vào response body

```java
response.getWriter().println("Hello Phuc");
```

Điều này nghĩa là:
- lấy writer
- ghi text vào body của response
- browser sẽ thấy nội dung đó

Ví dụ:

```java
response.setContentType("text/plain");
response.getWriter().println("Hello Phuc");
```

Response sẽ có dạng:

```http
HTTP/1.1 200 OK
Content-Type: text/plain

Hello Phuc
```

---

## 15. Response có thể trả HTML

Ví dụ:

```java
response.setContentType("text/html");
response.getWriter().println("<h1>Hello Phuc</h1>");
```

Browser sẽ render thành HTML.

Sơ đồ rất đơn giản:

```text
Servlet
  ↓
Viết HTML vào response
  ↓
Browser render ra giao diện
```

---

## 16. `getOutputStream()` — dùng cho binary data

Ngoài `getWriter()` còn có:

```java
response.getOutputStream()
```

### Phân biệt dễ nhớ

```text
getWriter()       → dùng cho text/character
getOutputStream() → dùng cho binary data
```

Ví dụ:
- `getWriter()` dùng cho HTML, JSON, text
- `getOutputStream()` dùng cho file PDF, ảnh, zip

---

## 17. `setHeader()` — thêm header vào response

```java
response.setHeader("X-App-Version", "1.0");
```

Nghĩa là server sẽ gửi header này về cho client.

Ví dụ:

```http
X-App-Version: 1.0
```

### `setHeader()` vs `addHeader()`

```java
response.setHeader("X-Test", "A");
response.setHeader("X-Test", "B");
```

Kết quả thường là chỉ còn:

```text
X-Test: B
```

Vì `setHeader()` dùng để thay thế giá trị cũ.

Còn:

```java
response.addHeader("X-Test", "A");
response.addHeader("X-Test", "B");
```

có thể có nhiều giá trị.

---

## 18. `sendRedirect()` — redirect sang trang khác

```java
response.sendRedirect("/login");
```

Nghĩa là server bảo browser đi tới URL khác.

Sơ đồ:

```text
Browser gọi /a
  ↓
Server trả 302 + Location: /b
  ↓
Browser tự gọi /b
```

Đây là redirect.

---

## 19. Redirect khác forward

Đây là một điểm rất dễ nhầm.

### Redirect

```text
Browser
  ↓
Gọi /a
  ↓
Server trả 302 sang /b
  ↓
Browser gọi /b lại
```

### Forward

```text
Browser
  ↓
Gọi /a
  ↓
Servlet A chuyển tiếp sang JSP
```

Không tạo request mới từ browser.

Bài sau sẽ nói kỹ hơn.

---

## 20. Ví dụ thực tế: LoginServlet

Giờ gom tất cả lại trong một ví dụ đơn giản.

Request:

```http
POST /login?redirect=/dashboard HTTP/1.1
Host: localhost:8080
Content-Type: application/x-www-form-urlencoded

username=phuc&password=123
```

Servlet:

```java
@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String redirect = request.getParameter("redirect");

        System.out.println(username);
        System.out.println(password);
        System.out.println(redirect);
    }
}
```

### Ý nghĩa

- `username`, `password` là dữ liệu người dùng gửi lên
- `redirect` là query parameter trong URL

---

## 21. Request vs Response — nhớ một câu đơn giản

```text
request = client gửi gì
response = server trả gì
```

### Request thường dùng

| Vấn đề | API |
| --- | --- |
| Lấy query parameter | `getParameter()` |
| Lấy form data | `getParameter()` |
| Lấy header | `getHeader()` |
| Lấy cookie | `getCookies()` |
| Lấy method | `getMethod()` |
| Lấy URI | `getRequestURI()` |

### Response thường dùng

| Vấn đề | API |
| --- | --- |
| Đặt status | `setStatus()` |
| Đặt content type | `setContentType()` |
| Viết text | `getWriter()` |
| Gửi header | `setHeader()` |
| Redirect | `sendRedirect()` |

---

## 22. Bài tập thực hành

Giả sử request như sau:

```http
POST /login?redirect=/dashboard HTTP/1.1
Host: localhost:8080
Content-Type: application/x-www-form-urlencoded
Accept: text/html
Cookie: JSESSIONID=XYZ789
Authorization: Bearer abc123

username=phuc&password=abc123
```

Bro hãy viết một `LoginServlet` xử lý các việc sau:

1. Lấy `username`, `password`, `redirect`
2. Lấy `Authorization`
3. Lấy `JSESSIONID` từ cookie
4. Nếu thiếu username hoặc password thì trả HTTP 400 và nội dung `Missing username or password`
5. Nếu đủ thì trả HTTP 200, content type `text/plain`, nội dung `Login request received for: phuc`

---

## 23. Tóm kết bài học

Sau bài này, bro nên nhớ ba điều chính:

```text
request = dữ liệu người dùng gửi tới
response = dữ liệu server trả về
```

Và các API cốt lõi:

```text
request.getParameter()   → lấy dữ liệu từ query/form
request.getHeader()      → lấy header
request.getCookies()     → lấy cookie
response.setStatus()     → đặt status code
response.setContentType()→ đặt kiểu nội dung
response.getWriter()     → viết text vào response
response.sendRedirect()  → redirect sang trang khác
```

Đây là nền tảng để bro học tiếp các phần như attribute, forward, redirect, request scope và JSP.

---

# 1. Nhìn tổng thể trước

Browser gửi:

```http
POST /login?redirect=/dashboard HTTP/1.1
Host: localhost:8080
Content-Type: application/x-www-form-urlencoded
Cookie: JSESSIONID=ABC123

username=phuc&password=123
```

Servlet nhận được:

```java
doPost(
    HttpServletRequest request,
    HttpServletResponse response
)
```

Ta có:

```text
                    Servlet
                       │
          ┌────────────┴────────────┐
          ↓                         ↓
 HttpServletRequest        HttpServletResponse
          │                         │
          │                         │
      Đọc request               Tạo response
          │                         │
          ↓                         ↓
 getParameter()              setStatus()
 getHeader()                 setHeader()
 getCookies()                getWriter()
 getMethod()                 setContentType()
 getRequestURI()             sendRedirect()
```

Mental model cực đơn giản:

> `request` = **client gửi gì cho server?**

> `response` = **server muốn trả gì cho client?**

---

# 2. `HttpServletRequest`

Đây:

```java
HttpServletRequest request
```

là object chứa thông tin của **request hiện tại**.

Ví dụ browser gửi:

```http
GET /users?id=10 HTTP/1.1
Host: localhost:8080
Accept: application/json
```

Servlet có thể lấy:

```java
request.getMethod();
request.getRequestURI();
request.getParameter("id");
request.getHeader("Accept");
```

---

# 3. `getMethod()`

```java
String method = request.getMethod();
```

Ví dụ:

```http
GET /users
```

→

```java
"GET"
```

POST:

```http
POST /login
```

→

```java
"POST"
```

Có thể:

```java
if ("POST".equals(request.getMethod())) {
    // ...
}
```

Nhưng trong Servlet, nếu bro đã override:

```java
doGet()
doPost()
```

thì thường **không cần tự kiểm tra method như vậy**.

Container đã dispatch cho bro rồi.

---

# 4. `getRequestURI()`

Ví dụ:

```http
GET /users/123 HTTP/1.1
```

thì:

```java
request.getRequestURI()
```

→

```text
/users/123
```

Nó lấy phần URI của request.

---

# 5. `getParameter()` — ⭐ CỰC KỲ QUAN TRỌNG

Đây là method bro sẽ dùng **rất nhiều**.

Ví dụ URL:

```http
GET /users?id=10
```

Servlet:

```java
String id = request.getParameter("id");
```

Kết quả:

```text
"10"
```

---

## Query parameter

Ví dụ:

```http
GET /search?keyword=java&page=2
```

Ta có:

```java
String keyword =
    request.getParameter("keyword");

String page =
    request.getParameter("page");
```

Kết quả:

```text
keyword = "java"
page = "2"
```

Nhớ:

```text
?keyword=java&page=2
      │        │
      ↓        ↓
   parameter parameter
```

---

# 6. Form POST cũng dùng `getParameter()`

Đây là chỗ người mới rất hay nhầm.

Browser gửi:

```http
POST /login HTTP/1.1
Content-Type: application/x-www-form-urlencoded

username=phuc&password=123
```

Servlet:

```java
String username =
    request.getParameter("username");

String password =
    request.getParameter("password");
```

Vẫn là:

```text
getParameter()
```

🔥 Tức là `getParameter()` có thể lấy parameter từ:

```text
Query String
      +
Form Data
```

Ví dụ:

```text
GET /login?redirect=/home
              ↑
          query param
```

và:

```text
username=phuc&password=123
↑
form parameter
```

Servlet có thể lấy cả hai:

```java
request.getParameter("redirect");
request.getParameter("username");
request.getParameter("password");
```

---

# 7. Nếu parameter không tồn tại?

Ví dụ:

```http
GET /users
```

Servlet:

```java
String id =
    request.getParameter("id");
```

Kết quả:

```java
null
```

Không phải:

```text
""
```

nên code kiểu:

```java
Long id = Long.parseLong(
    request.getParameter("id")
);
```

có thể gây lỗi nếu parameter không tồn tại.

Thường cần validate:

```java
String idParam =
    request.getParameter("id");

if (idParam == null) {
    // xử lý thiếu parameter
}
```

---

# 8. `getParameterValues()`

Nếu một parameter xuất hiện nhiều lần:

```http
GET /search?tag=java&tag=servlet&tag=jsp
```

thì:

```java
String[] tags =
    request.getParameterValues("tag");
```

có thể nhận:

```text
["java", "servlet", "jsp"]
```

Cái này sẽ hữu ích với:

```html
<input type="checkbox">
```

ví dụ user chọn nhiều category.

---

# 9. `getHeader()`

Browser gửi:

```http
Accept: application/json
User-Agent: Chrome
Authorization: Bearer abc123
```

Servlet:

```java
String accept =
    request.getHeader("Accept");
```

→

```text
application/json
```

Hoặc:

```java
String authorization =
    request.getHeader("Authorization");
```

→

```text
Bearer abc123
```

---

# 10. Header khác parameter

Đây là thứ bro phải phân biệt rõ.

Request:

```http
GET /users?id=10 HTTP/1.1
Accept: application/json
Authorization: Bearer abc
```

Có:

### Parameter

```text
id=10
```

lấy bằng:

```java
request.getParameter("id");
```

### Header

```text
Accept: application/json
Authorization: Bearer abc
```

lấy bằng:

```java
request.getHeader("Accept");
request.getHeader("Authorization");
```

Không được nhầm:

```java
request.getParameter("Authorization")
```

vì Authorization là **header**, không phải parameter.

---

# 11. `getCookies()`

Browser có:

```http
Cookie: JSESSIONID=ABC123
```

Servlet có thể đọc cookies:

```java
Cookie[] cookies =
    request.getCookies();
```

Sau đó:

```java
for (Cookie cookie : cookies) {
    System.out.println(cookie.getName());
    System.out.println(cookie.getValue());
}
```

Ví dụ:

```text
JSESSIONID
ABC123
```

---

# 12. `HttpServletResponse`

Giờ quay sang chiều ngược lại.

```java
HttpServletResponse response
```

đại diện cho response server chuẩn bị gửi về browser.

Ví dụ:

```http
HTTP/1.1 200 OK
Content-Type: text/plain

Hello Phuc
```

Servlet kiểm soát phần này.

---

# 13. `setStatus()`

Ví dụ:

```java
response.setStatus(
    HttpServletResponse.SC_OK
);
```

→ HTTP 200.

Hoặc:

```java
response.setStatus(
    HttpServletResponse.SC_NOT_FOUND
);
```

→ HTTP 404.

Một số constant thường gặp:

```text
SC_OK                 200
SC_CREATED            201
SC_BAD_REQUEST        400
SC_UNAUTHORIZED       401
SC_FORBIDDEN          403
SC_NOT_FOUND          404
SC_METHOD_NOT_ALLOWED 405
SC_INTERNAL_SERVER_ERROR 500
```

Thay vì:

```java
response.setStatus(404);
```

có thể dùng:

```java
response.setStatus(
    HttpServletResponse.SC_NOT_FOUND
);
```

Dễ đọc hơn.

---

# 14. `setContentType()`

Bro đã dùng cái này:

```java
response.setContentType("text/plain");
```

Nếu trả HTML:

```java
response.setContentType("text/html");
```

JSON:

```java
response.setContentType("application/json");
```

Thường API:

```text
application/json
```

JSP:

```text
text/html
```

---

# 15. Charset

Thực tế nên quan tâm UTF-8.

Ví dụ:

```java
response.setContentType(
    "text/plain;charset=UTF-8"
);
```

hoặc:

```java
response.setCharacterEncoding("UTF-8");
```

Đặc biệt khi trả tiếng Việt:

```text
Xin chào Phúc
```

Nếu charset xử lý không đúng có thể xuất hiện:

```text
Xin chÃ o PhÃºc
```

---

# 16. `getWriter()`

Đây là thứ bro đã dùng:

```java
response.getWriter()
        .println("Hello Phuc");
```

Nó ghi **text** vào response body.

Ví dụ:

```java
response.setContentType("text/plain");

response.getWriter()
        .println("Hello Phuc");
```

Response:

```http
HTTP/1.1 200 OK
Content-Type: text/plain

Hello Phuc
```

---

# 17. Response body có thể là HTML

Ví dụ:

```java
response.setContentType("text/html");

response.getWriter().println("""
    <html>
        <body>
            <h1>Hello Phuc</h1>
        </body>
    </html>
""");
```

Browser sẽ render HTML.

Đây chính là nền tảng của kiểu Servlet/JSP đời cũ:

```text
Servlet
   ↓
HTML Response
   ↓
Browser
```

Nhưng sau này chúng ta sẽ học:

```text
Servlet
   ↓
forward()
   ↓
JSP
   ↓
HTML
   ↓
Browser
```

Đó chính là lý do bro đang học Servlet trước JSP.

---

# 18. `getOutputStream()`

Ngoài:

```java
response.getWriter()
```

còn:

```java
response.getOutputStream()
```

Khác nhau cơ bản:

```text
getWriter()
    ↓
text / character data

getOutputStream()
    ↓
binary data
```

Ví dụ trả file PDF/image thì thường dùng:

```java
response.getOutputStream()
```

Còn:

```text
HTML
JSON
text
```

thường dùng:

```java
getWriter()
```

---

# 19. `setHeader()`

Server muốn gửi header:

```java
response.setHeader(
    "X-App-Version",
    "1.0"
);
```

Response:

```http
X-App-Version: 1.0
```

Hoặc:

```java
response.setHeader(
    "Cache-Control",
    "no-cache"
);
```

---

# 20. `setHeader()` vs `addHeader()`

Cái này bro từng hỏi trước đây, giờ đặt nó vào đúng context.

### `setHeader()`

```java
response.setHeader("X-Test", "A");
response.setHeader("X-Test", "B");
```

Kết quả cuối cùng thường là:

```text
X-Test: B
```

Nó **set/thay thế** giá trị header.

### `addHeader()`

```java
response.addHeader("X-Test", "A");
response.addHeader("X-Test", "B");
```

Có thể tạo nhiều giá trị:

```text
X-Test: A
X-Test: B
```

Mental model:

```text
setHeader()
    ↓
"Đặt giá trị này"

addHeader()
    ↓
"Thêm một giá trị nữa"
```

---

# 21. `sendRedirect()` — ⭐ Quan trọng

Servlet:

```java
response.sendRedirect("/login");
```

Server sẽ trả response redirect, thường là:

```http
302 Found
Location: /login
```

Browser thấy:

```text
302
 ↓
Location: /login
 ↓
Browser gửi request mới
 ↓
GET /login
```

Đây là **redirect**.

---

# 22. Redirect khác forward

Đừng học thuộc vội, chỉ cần nhìn mental model.

### Redirect

```text
Browser
   │
   │ GET /a
   ↓
Servlet
   │
   │ 302 /b
   ↓
Browser
   │
   │ GET /b
   ↓
Servlet /b
```

Có **request mới**.

### Forward

Sau này:

```text
Browser
   │
   │ GET /a
   ↓
Servlet A
   │
   │ forward()
   ↓
JSP
```

Không quay về browser để tạo request HTTP mới.

Phần này bài sau mình sẽ đào sâu.

---

# 23. Ví dụ thực tế: LoginServlet

Giờ gom tất cả lại.

Request:

```http
POST /login?redirect=/dashboard HTTP/1.1
Host: localhost:8080
Content-Type: application/x-www-form-urlencoded

username=phuc&password=123
```

Servlet:

```java
@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        String username =
                request.getParameter("username");

        String password =
                request.getParameter("password");

        String redirect =
                request.getParameter("redirect");

        System.out.println(username);
        System.out.println(password);
        System.out.println(redirect);
    }
}
```

Kết quả:

```text
username = phuc
password = 123
redirect = /dashboard
```

---

# 24. Request vs Response — chốt lại

Bro hãy nhìn bảng này:

| Client gửi      | Servlet đọc bằng                   |
| --------------- | ---------------------------------- |
| Query parameter | `getParameter()`                   |
| Form parameter  | `getParameter()`                   |
| Header          | `getHeader()`                      |
| Cookie          | `getCookies()`                     |
| HTTP method     | `getMethod()`                      |
| URI             | `getRequestURI()`                  |
| Request body    | `getReader()` / `getInputStream()` |

Còn server trả:

| Server muốn làm | API                 |
| --------------- | ------------------- |
| Status code     | `setStatus()`       |
| Content-Type    | `setContentType()`  |
| Header          | `setHeader()`       |
| Thêm header     | `addHeader()`       |
| Text response   | `getWriter()`       |
| Binary response | `getOutputStream()` |
| Redirect        | `sendRedirect()`    |

---

# 🧠 Bài tập 5

Cho request:

```http
POST /login?redirect=/dashboard HTTP/1.1
Host: localhost:8080
Content-Type: application/x-www-form-urlencoded
Accept: text/html
Cookie: JSESSIONID=XYZ789
Authorization: Bearer abc123

username=phuc&password=abc123
```

Viết một `LoginServlet` xử lý request này.

Yêu cầu:

### 1.

Lấy:

```text
username
password
redirect
```

bằng API phù hợp.

### 2.

Lấy:

```text
Authorization
```

### 3.

Lấy:

```text
JSESSIONID
```

từ Cookie.

### 4.

Nếu `username` hoặc `password` bị thiếu:

```text
HTTP 400
```

và response:

```text
Missing username or password
```

### 5.

Nếu đủ:

```text
HTTP 200
Content-Type: text/plain
```

và trả:

```text
Login request received for: phuc
```

---

## 🎯 Bonus

Đừng viết:

```java
if (request.getParameter("username") == null
        || request.getParameter("password") == null) {
    ...
}
```

rồi gọi `getParameter()` đi gọi lại.

Hãy lấy một lần:

```java
String username = ...;
String password = ...;
```

rồi xử lý.

---

Bro tự code **LoginServlet hoàn chỉnh** rồi gửi mình. Mình sẽ review từng dòng. Sau đó **Bài 6** chúng ta sẽ đi vào phần cực kỳ quan trọng của Servlet/JSP:

```text
Request Scope
Attribute
forward()
redirect()
RequestDispatcher
```

Lúc đó bro sẽ bắt đầu hiểu thật sự **Servlet truyền dữ liệu sang JSP như thế nào**.
