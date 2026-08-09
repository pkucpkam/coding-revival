
# Bài 2 — HTTP Request & Response Deep Dive

Ở bài trước, bro đã nắm được:

```text
Browser
   ↓
HTTP Request
   ↓
Server / Servlet Container
   ↓
Servlet
   ↓
HTTP Response
   ↓
Browser
```

Hôm nay ta sẽ **mổ xẻ request/response**, vì ngay sau bài này bro sẽ bắt đầu thấy:

```java
HttpServletRequest
HttpServletResponse
```

không còn xa lạ nữa.

---

# 1. HTTP Request thực sự trông như thế nào?

Giả sử browser gửi:

```http
GET /users?id=10 HTTP/1.1
Host: localhost:8080
Accept: text/html
User-Agent: Chrome
Cookie: JSESSIONID=ABC123
```

Hãy tưởng tượng nó như một phong bì:

```text
┌─────────────────────────────────────────┐
│ Request Line                            │
│ GET /users?id=10 HTTP/1.1              │
├─────────────────────────────────────────┤
│ Headers                                 │
│ Host: localhost:8080                    │
│ Accept: text/html                       │
│ User-Agent: Chrome                      │
│ Cookie: JSESSIONID=ABC123               │
├─────────────────────────────────────────┤
│ Body                                    │
│                                         │
└─────────────────────────────────────────┘
```

Có 3 phần chính:

```text
Request
├── Request Line
├── Headers
└── Body
```

**Không phải request nào cũng có body.**

Đây là điểm rất quan trọng.

---

# 2. Request Line

Ví dụ:

```http
GET /users?id=10 HTTP/1.1
```

Có 3 thành phần:

```text
GET
 ↓
HTTP Method

/users?id=10
 ↓
Request Target

HTTP/1.1
 ↓
HTTP Version
```

---

# 3. HTTP Method

Những method bro sẽ gặp thường xuyên:

| Method | Mục đích          |
| ------ | ----------------- |
| GET    | Lấy dữ liệu       |
| POST   | Gửi/tạo dữ liệu   |
| PUT    | Cập nhật/thay thế |
| PATCH  | Cập nhật một phần |
| DELETE | Xóa               |

Ví dụ:

```http
GET /users
```

Nghĩ:

> "Server, cho tôi danh sách users."

---

```http
GET /users/10
```

> "Cho tôi user số 10."

---

```http
POST /users
```

> "Tôi muốn tạo một user."

---

```http
DELETE /users/10
```

> "Xóa user số 10."

---

# 4. GET và POST — cực kỳ quan trọng

Đây là thứ bro sẽ dùng liên tục với JSP/Form/Servlet.

## GET

Ví dụ:

```http
GET /users?id=10
```

Dữ liệu thường nằm trong:

```text
Query String
```

```text
/users?id=10
       └───┬───┘
           Query
```

Servlet có thể lấy:

```java
String id = request.getParameter("id");
```

Kết quả:

```text
10
```

---

## POST

Ví dụ login:

```http
POST /login HTTP/1.1
Content-Type: application/x-www-form-urlencoded

username=phuc&password=123
```

Dữ liệu nằm trong **request body**.

Servlet vẫn có thể:

```java
String username = request.getParameter("username");
String password = request.getParameter("password");
```

Đây là điểm thú vị:

> `request.getParameter()` không chỉ dành cho GET.

Nó có thể lấy parameter từ nhiều nguồn, tùy loại request/content type.

---

# 5. Query Parameter

Ví dụ:

```text
/users?id=10&status=active
```

Ta có:

```text
Path:
    /users

Query parameters:
    id=10
    status=active
```

Mental model:

```text
/users?id=10&status=active
  │          │
  │          └── status = active
  └───────────── id = 10
```

Trong Servlet:

```java
String id = request.getParameter("id");
String status = request.getParameter("status");
```

---

# 6. Headers là gì?

Headers là **metadata về request/response**.

Ví dụ:

```http
Host: localhost:8080
Accept: text/html
User-Agent: Chrome
Cookie: JSESSIONID=ABC123
Content-Type: application/json
Authorization: Bearer xxx
```

Nó không phải business data chính.

Ví dụ:

```text
username=phuc
```

là data.

Trong khi:

```text
Content-Type: application/json
```

là metadata nói cho server biết:

> "Body của tôi có format JSON."

---

# 7. Một số Request Header quan trọng

## Host

```http
Host: localhost:8080
```

Cho server biết request đang nhắm tới host nào.

---

## User-Agent

```http
User-Agent: Mozilla/5.0 ...
```

Thông tin về client/browser.

---

## Accept

```http
Accept: text/html
```

Client nói:

> "Tôi ưu tiên nhận HTML."

Hoặc:

```http
Accept: application/json
```

> "Tôi muốn JSON."

---

## Content-Type

Cực kỳ quan trọng.

Ví dụ:

```http
Content-Type: application/json
```

nghĩa là body:

```json
{
    "username": "phuc"
}
```

được gửi dưới dạng JSON.

Hoặc form:

```http
Content-Type: application/x-www-form-urlencoded
```

body:

```text
username=phuc&password=123
```

---

# 8. Cookie

Ví dụ:

```http
Cookie: JSESSIONID=ABC123
```

Browser đang gửi cookie lên server.

Cookie rất quan trọng khi học:

```text
Session
Authentication
Login
```

Mental model ban đầu:

```text
Server
   ↓
Set-Cookie
   ↓
Browser lưu cookie
   ↓
Request tiếp theo
   ↓
Cookie: JSESSIONID=...
```

Lát nữa khi học Session, bro sẽ thấy cơ chế này cực kỳ rõ.

---

# 9. Request Body

Body là phần chứa dữ liệu gửi lên server.

Ví dụ JSON:

```http
POST /users HTTP/1.1
Content-Type: application/json

{
    "name": "Phuc",
    "age": 22
}
```

Body:

```json
{
    "name": "Phuc",
    "age": 22
}
```

---

Một ví dụ khác:

```http
POST /login HTTP/1.1
Content-Type: application/x-www-form-urlencoded

username=phuc&password=123
```

Body:

```text
username=phuc&password=123
```

---

# 10. Mapping HTTP Request → Servlet

Đây là phần quan trọng nhất của bài.

HTTP:

```http
GET /users?id=10 HTTP/1.1
Host: localhost:8080
Accept: text/html
Cookie: JSESSIONID=ABC123
```

Servlet:

```java
request.getMethod();
request.getRequestURI();
request.getParameter("id");
request.getHeader("Accept");
request.getCookies();
request.getSession();
```

Mapping:

| HTTP     | Servlet API                   |
| -------- | ----------------------------- |
| `GET`    | `request.getMethod()`         |
| `/users` | `request.getRequestURI()`     |
| `?id=10` | `request.getParameter("id")`  |
| `Accept` | `request.getHeader("Accept")` |
| Cookie   | `request.getCookies()`        |
| Session  | `request.getSession()`        |

Bro bắt đầu thấy chưa?

`HttpServletRequest` thực chất là **Java representation của HTTP request mà client gửi lên**.

---

# 11. Bây giờ tới Response

Server nhận:

```http
GET /users
```

Servlet xử lý rồi tạo:

```http
HTTP/1.1 200 OK
Content-Type: text/html

<h1>Users</h1>
```

Response cũng có:

```text
Response
├── Status Line
├── Headers
└── Body
```

---

# 12. Status Line

Ví dụ:

```http
HTTP/1.1 200 OK
```

Gồm:

```text
HTTP/1.1
   ↓
Version

200
   ↓
Status Code

OK
   ↓
Reason Phrase
```

Trong code Servlet:

```java
response.setStatus(200);
```

hoặc:

```java
response.setStatus(HttpServletResponse.SC_OK);
```

Nhưng thực tế code thường không cần tự set `200`, vì response thành công mặc định thường đã là 200 nếu không có lỗi/redirect khác.

---

# 13. Response Status quan trọng

Bro phải phân biệt ít nhất:

```text
200 OK
201 Created
204 No Content

301/302 Redirect

400 Bad Request
401 Unauthorized
403 Forbidden
404 Not Found

500 Internal Server Error
```

Đặc biệt:

### 401 vs 403

Đây là câu phỏng vấn rất hay hỏi.

```text
401
→ Authentication problem
→ "Mày là ai?"

403
→ Authorization problem
→ "Tao biết mày là ai, nhưng mày không có quyền."
```

Ví dụ:

```text
GET /admin
```

User chưa login:

```text
401
```

User đã login nhưng chỉ là USER:

```text
403
```

---

# 14. Response Headers

Ví dụ:

```http
HTTP/1.1 200 OK
Content-Type: text/html
Content-Length: 1234
Set-Cookie: JSESSIONID=ABC123
```

Server đang gửi metadata cho client.

Servlet:

```java
response.setContentType("text/html");
```

Hoặc:

```java
response.setHeader(
    "X-Custom-Header",
    "Hello"
);
```

---

# 15. Response Body

Cuối cùng:

```http
HTTP/1.1 200 OK
Content-Type: text/html

<html>
    <body>
        <h1>Hello Phuc</h1>
    </body>
</html>
```

Body là:

```html
<html>
    <body>
        <h1>Hello Phuc</h1>
    </body>
</html>
```

Servlet có thể ghi:

```java
response.getWriter()
        .println("<h1>Hello Phuc</h1>");
```

Browser nhận HTML → render.

---

# 16. Ghép tất cả lại

Giờ hãy nhìn toàn bộ flow:

```text
Browser
   │
   │ GET /users?id=10
   │
   │ Headers
   │ Cookies
   ↓
┌───────────────────────┐
│ Servlet Container     │
│                       │
│ HttpServletRequest    │
│        ↓              │
│    UserServlet        │
└───────────┬───────────┘
            │
            ↓
         Service
            │
            ↓
           DAO
            │
            ↓
        Database
            │
            ↓
         User data
            │
            ↓
        UserServlet
            │
            │ HttpServletResponse
            ↓
┌───────────────────────┐
│ HTTP Response         │
│                       │
│ 200 OK                │
│ Content-Type: text/html│
│                       │
│ <html>...</html>      │
└───────────┬───────────┘
            ↓
         Browser
```

Đây chính là **request-response lifecycle ở mức application**.

---

# 17. Một điểm cực kỳ quan trọng: HTTP Stateless

HTTP về bản chất là **stateless**.

Ví dụ:

```text
Request 1:
GET /users

Request 2:
GET /users/10

Request 3:
GET /orders
```

Server không mặc định biết:

> "À, request 3 chính là ông user vừa gửi request 1."

Mỗi request về bản chất là một request độc lập.

Vậy làm sao login hoạt động?

```text
Login
 ↓
Session
 ↓
Cookie
 ↓
JSESSIONID
 ↓
Request tiếp theo
```

Đây chính là lý do:

**Cookie + Session**

là topic cực kỳ quan trọng trong Servlet.

---

# 18. GET vs POST — đừng hiểu sai

Một misconception phổ biến:

> GET không gửi data, POST mới gửi data.

❌ Sai.

GET có thể gửi:

```text
/users?id=10&name=phuc
```

POST cũng gửi data:

```text
username=phuc&password=123
```

Điểm khác biệt không đơn giản là:

```text
GET = không data
POST = có data
```

Mà cần hiểu:

* semantics
* location của data
* caching
* idempotency
* browser behavior
* security implications
* form submission

Phần này sau này mình sẽ đào sâu.

Đặc biệt:

> **POST không tự động làm data "an toàn".**

Ví dụ:

```http
POST /login

username=phuc&password=123
```

Password vẫn có thể bị đọc nếu không dùng HTTPS.

---

# 19. Request thực tế của Login

Đây là request bro sẽ gặp rất nhiều khi làm JSP/Servlet:

```http
POST /login HTTP/1.1
Host: localhost:8080
Content-Type: application/x-www-form-urlencoded
Cookie: JSESSIONID=ABC123

username=phuc&password=123456
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

        // xử lý login...

        response.sendRedirect("/home");
    }
}
```

Bro chưa cần nhớ code.

Hãy đọc nó theo HTTP:

```text
POST /login
       ↓
@WebServlet("/login")
       ↓
LoginServlet
       ↓
doPost()
       ↓
request.getParameter()
       ↓
username/password
       ↓
login processing
       ↓
response.sendRedirect()
```

---

# 20. `sendRedirect()` — hé lộ bài sau

Khi:

```java
response.sendRedirect("/home");
```

server **không trực tiếp gửi nội dung `/home` về**.

Nó trả một response kiểu:

```http
HTTP/1.1 302 Found
Location: /home
```

Browser thấy:

```text
302
+
Location: /home
```

và thực hiện **request mới**:

```http
GET /home HTTP/1.1
```

Flow:

```text
Browser
   │
   │ POST /login
   ↓
Servlet
   │
   │ 302 Location: /home
   ↓
Browser
   │
   │ GET /home       ← REQUEST MỚI
   ↓
Server
```

🔥 Đây chính là thứ chúng ta sẽ dùng để hiểu cực sâu:

```text
forward()
vs
sendRedirect()
```

ở bài Servlet.

---

# 🧠 Mental Model cuối bài

Bro hãy ghi nhớ cái này:

```text
HTTP Request
│
├── Request Line
│   ├── Method
│   ├── Path
│   └── Query String
│
├── Headers
│
└── Body
```

↓

```text
HttpServletRequest
```

↓

```text
Servlet
```

↓

```text
HttpServletResponse
```

↓

```text
HTTP Response
│
├── Status Code
├── Headers
└── Body
```

---

# 🧪 Ôn tập 

Dưới đây là phân tích chi tiết cho HTTP request:

```
POST /login?redirect=/dashboard HTTP/1.1
Host: localhost:8080
Content-Type: application/x-www-form-urlencoded
Accept: text/html
Cookie: JSESSIONID=XYZ789

username=phuc&password=abc123
```

---

### 1. HTTP method là gì?

HTTP method là **`POST`**. Method này được dùng để gửi dữ liệu từ client lên server để xử lý hoặc tạo/cập nhật tài nguyên.

---

### 2. Path là gì?

Path (đường dẫn tài nguyên) là **`/login`**.

---

### 3. Có query parameter nào?

Có **1 query parameter**:

* **Tên:** `redirect`
* **Giá trị:** `/dashboard`
*(Dùng để chỉ định cho server biết cần chuyển hướng user về trang `/dashboard` sau khi đăng nhập thành công).*

---

### 4. Có những request header nào?

Có **3 request headers**:

* **`Host`**: `localhost:8080` (Tên miền/IP và cổng của server nhận request)
* **`Content-Type`**: `application/x-www-form-urlencoded` (Kiểu định dạng dữ liệu trong Body)
* **`Accept`**: `text/html` (Định dạng dữ liệu trả về mà client mong muốn nhận được)

---

### 5. Cookie là gì và giá trị `JSESSIONID` là gì?

* **Cookie:** Là header dùng để truyền dữ liệu lưu trữ phía client lên server trong mỗi request (`Cookie: JSESSIONID=XYZ789`).
* **Giá trị `JSESSIONID`:** Là **`XYZ789`**. Đây là mã định danh phiên làm việc (Session ID) do Servlet container (như Tomcat) tạo ra để nhận diện người dùng hiện tại giữa các request liên tiếp.

---

### 6. Body chứa gì?

Body chứa thông tin biểu mẫu (form data) đã được mã hóa dạng URL với nội dung:

* `username=phuc`
* `password=abc123`

---

### 7. Tại sao `LoginServlet` phải dùng `doPost()` thay vì `doGet()`?

`LoginServlet` bắt buộc/nên dùng `doPost()` vì các lý do an toàn và kỹ thuật sau:

1. **Bảo mật mật khẩu (Security):**
* Dữ liệu trong `doPost()` nằm ở **Request Body**, trong khi `doGet()` sẽ đưa tham số lên **URL** (ví dụ: `/login?username=phuc&password=abc123`).
* Nếu dùng `doGet()`, mật khẩu sẽ bị lộ trong lịch sử trình duyệt, log của Web Server, và khi người dùng chia sẻ URL.


2. **Không giới hạn độ dài:** `doGet()` bị giới hạn độ dài URL (tùy thuộc trình duyệt/server), còn `doPost()` có thể gửi dữ liệu dung lượng lớn hơn nhiều.
3. **Đúng bản chất HTTP Semantics:** `GET` mang tính chất "đọc/lấy dữ liệu" (safe & idempotent) và có thể bị caching bởi trình duyệt. Trong khi đăng nhập làm thay đổi trạng thái xác thực trên server (tạo Session mới), do đó dùng `POST` là chuẩn thiết kế REST/HTTP.

