
# 🚀 Phase 0 — Java Web bắt đầu từ đâu?

Trước khi học Servlet, hãy trả lời câu:

> Một website hoạt động như thế nào ở mức khái niệm?

Ví dụ gõ `https://example.com/users` và nhấn Enter.

Mental model (tóm tắt):

Browser → HTTP Request → Server (Servlet Container) → Ứng dụng Java xử lý → Database (nếu cần) → HTTP Response → Browser

Ứng dụng Java nằm bên trong một container (Tomcat, WildFly, v.v.) — container này nhận request từ mạng, chuyển cho ứng dụng, rồi trả response.

---

## 1. Web thực chất là gì?

Ở mức đơn giản: **Web = client giao tiếp với server qua HTTP/HTTPS.**

- Client: trình duyệt (Chrome/Firefox), mobile app, frontend SPA (React/Vue), Postman, `curl`.
- Server: nơi chạy ứng dụng, nhận request, thực hiện logic, trả response. Có thể là máy chủ đơn (Tomcat) hoặc ứng dụng enterprise (WildFly/JBoss).

Server thường chạy một HTTP server (nghe socket), sau đó container sẽ map request tới component xử lý (Servlet, controller, v.v.).

---

## 2. Request và Response — khái niệm cốt lõi

Client gửi một *request*, server gửi *response*.

Ví dụ HTTP request đơn giản (raw):

```http
GET /users HTTP/1.1
Host: example.com
User-Agent: curl/8.0
Accept: application/json

```

Response ví dụ:

```http
HTTP/1.1 200 OK
Content-Type: application/json
Content-Length: 123

[ {"id":1,"name":"Phuc"} ]
```

Lưu ý: request/response có thể chứa headers, body, status codes. Servlet chính là nơi Java đọc request và tạo response.

---

## 3. HTTP Request gồm gì? (chi tiết)

Một HTTP request gồm 3 phần chính:

- Request line: `GET /path HTTP/1.1` → method, path, version.
- Headers: `Host`, `Content-Type`, `Cookie`, `Authorization`, v.v.
- Body (tuỳ method): dữ liệu gửi lên server (form data, JSON, file upload).

Ví dụ POST (form urlencoded):

```http
POST /login HTTP/1.1
Host: example.com
Content-Type: application/x-www-form-urlencoded
Content-Length: 29

username=phuc&password=123456
```

Practical: gửi request bằng `curl`:

```bash
curl -i https://example.com/users
curl -i -X POST -d "username=phuc&password=123456" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  https://example.com/login
```

`-i` hiển thị cả headers của response.

---

## 4. HTTP Methods (cốt lõi)

- `GET`: lấy dữ liệu, không có body (chuẩn). Idempotent.
- `POST`: tạo/tạo hành động, có body.
- `PUT`: thay thế tài nguyên (idempotent).
- `PATCH`: cập nhật một phần.
- `DELETE`: xóa.

RESTful APIs sử dụng các method này để mô tả hành vi.

---

## 5. URL và query

URL = `scheme://host[:port]/path?query#fragment`.

Ví dụ: `https://example.com/users?id=10&page=2`

- `scheme` = `https` (giao thức)
- `host` = `example.com`
- `path` = `/users`
- `query` = `id=10&page=2`

Trong Java Servlet: `request.getParameter("id")` sẽ trả `"10"` nếu request là `GET /users?id=10`.

---

## 6. HTTP Response — cấu trúc và status codes

Response gồm:

- Status line: `HTTP/1.1 200 OK` (status code + reason)
- Headers: `Content-Type`, `Set-Cookie`, `Location`, v.v.
- Body: HTML, JSON, hình ảnh, v.v.

Nhóm status code quan trọng:

- 2xx: success (200, 201, 204)
- 3xx: redirect (301, 302)
- 4xx: client error (400, 401, 403, 404)
- 5xx: server error (500, 502, 503)

Khi gặp `500`, phải debug toàn stack: container → filter → servlet → service → dao → db.

---

## 7. Header, Cookie, Session (ngắn)

- `Header`: metadata của request/response.
- `Cookie`: key/value nhỏ được client lưu và gửi lại server; dùng để giữ session id.
- `Session`: khái niệm phía server liên kết session id với state (thường container quản lý, ví dụ `JSESSIONID`).

Ví dụ response trả cookie:

```http
Set-Cookie: JSESSIONID=ABC123; Path=/; HttpOnly
```

Trình duyệt sẽ gửi header `Cookie: JSESSIONID=ABC123` trong các request sau, container sẽ ánh xạ để tìm session tương ứng.

---

## 8. Java nằm ở đâu? Servlet, Container và sự khác nhau

- Servlet Container (Tomcat, WildFly) là môi trường: mở socket, lắng nghe HTTP, quản lý servlet lifecycle, thread pool, session, security, JNDI, deployment.
- Servlet là class Java do developer viết, container khởi tạo và gọi `doGet()/doPost()` để xử lý request.

Flow tóm tắt:

Browser → Container (HTTP listener) → Container tìm mapping (URL → Servlet) → Container gọi lifecycle/doXXX → Servlet xử lý → Container gửi response

---

## 9. Servlet lifecycle (tóm tắt)

Các bước chính:

- `init()`: gọi một lần khi servlet được khởi tạo.
- `service()` → `doGet()`/`doPost()` gọi nhiều lần cho từng request.
- `destroy()`: gọi khi container dừng servlet.

Ví dụ đơn giản:

```java
@WebServlet("/hello")
public class HelloServlet extends HttpServlet {
    @Override
    public void init() throws ServletException { /* khởi tạo tài nguyên */ }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("text/plain;charset=UTF-8");
        resp.getWriter().println("Hello World");
    }

    @Override
    public void destroy() { /* giải phóng */ }
}
```

Quan trọng: đừng cố tạo servlet mỗi request — container quản lý instance và threading.

---

## 10. JSP và MVC — vị trí trong stack

- JSP: công cụ tạo HTML động (view).
- Servlet: thường đóng vai controller (nhận request, gọi service, chọn view).
- Service/DAO: business logic và truy cập database.

Model: dữ liệu; View: JSP/Thymeleaf; Controller: Servlet/Controller.

---

## 11. Thực hành nhỏ — dùng `curl` để quan sát HTTP

1) GET request (xem headers):

```bash
curl -i https://httpbin.org/get
```

2) POST form:

```bash
curl -i -X POST -d "username=phuc&password=123" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  https://httpbin.org/post
```

3) Gửi header tuỳ ý:

```bash
curl -i -H "X-Demo: hello" https://httpbin.org/headers
```

Xem kết quả trả về để quan sát request/response headers và body.

---
Dưới đây là lời giải thích ngắn gọn, dễ hiểu và chuẩn kiến thức chuyên ngành cho 3 câu hỏi của bro:

---

# Ôn tập

## 1. Khi nhập `[https://example.com/users](https://example.com/users)` và Enter, chuyện gì xảy ra?

Ở mức khái niệm (conceptual level), toàn bộ quy trình diễn ra qua các bước chính sau:

1. **Phân giải tên miền (DNS Lookup):**
* Trình duyệt không biết `example.com` ở đâu. Nó gửi yêu cầu hỏi máy chủ DNS để đổi tên miền `example.com` thành một địa chỉ IP cụ thể (ví dụ: `93.184.216.34`).


2. **Thiết lập kết nối an toàn (TCP + TLS Handshake):**
* Trình duyệt mở một "đường truyền" kết nối đến IP đó (TCP Handshake).
* Vì dùng `https`, trình duyệt và server tiến hành "bắt tay" mã hóa (TLS Handshake) để đảm bảo dữ liệu truyền qua lại không bị nghe lén.


3. **Gửi HTTP Request:**
* Trình duyệt tạo và gửi một yêu cầu **HTTP GET** chứa URL `/users` cùng các thông tin đính kèm (headers, cookies...).


4. **Server xử lý:**
* Server (Nginx, Apache hoặc Tomcat...) nhận request, chuyển đến ứng dụng backend xử lý (lấy danh sách users từ database, chuẩn bị dữ liệu).


5. **Trả về HTTP Response:**
* Server đóng gói dữ liệu thành một HTTP Response (chứa mã trạng thái `200 OK`, định dạng HTML/JSON...) và gửi lại cho trình duyệt.


6. **Hiển thị (Rendering):**
* Trình duyệt nhận response, đọc dữ liệu (HTML/CSS/JS) và vẽ (render) giao diện lên màn hình cho bro xem.



---

## 2. Servlet là gì? Khác gì Server và Servlet Container?

### A. Servlet là gì?

* **Servlet** là một chương trình **Java** chạy trên server, chuyên dùng để **nhận request** từ client (trình duyệt) và **tạo ra response** trả về.
* *Ví dụ:* Một lớp Java nhận request `/users`, query database lấy danh sách user rồi trả về mảng JSON chính là một Servlet.

### B. So sánh Servlet, Servlet Container và Web Server

| Khái niệm | Nó là cái gì? | Vai trò chính | Ví dụ |
| --- | --- | --- | --- |
| **Web Server** | Phần mềm/Phần cứng quản lý kết nối mạng. | Xử lý các file tĩnh (HTML, CSS, JS, ảnh) và lắng nghe cổng mạng (80, 443). | Nginx, Apache HTTP Server |
| **Servlet Container** *(Web Container)* | Môi trường chạy (Runtime Environment) quản lý các Servlet. | Quản lý vòng đời Servlet (khởi tạo, gọi hàm `service()`, hủy), định tuyến request đến đúng Servlet. | Apache Tomcat, Jetty |
| **Servlet** | Đoạn code Java do coder viết. | Chứa logic nghiệp vụ thực sự để xử lý dữ liệu động. | Lớp Java `UserServlet extends HttpServlet` |

> **Tóm tắt cho dễ hình dung:**
> * **Web Server** là ông bảo vệ tòa nhà (đón khách ở cửa).
> * **Servlet Container** là quản lý văn phòng (sắp xếp chỗ ngồi, phân việc).
> * **Servlet** là nhân viên thực hiện công việc cụ thể.
> 
> 

---

## 3. Phân biệt Request vs Response

Trong giao tiếp Web (giao thức HTTP), đây là 2 chiều của một cuộc hội thoại:

```
    [ Client / Trình duyệt ]  ====== Request =====>  [ Server ]
    [ Client / Trình duyệt ]  <===== Response =====  [ Server ]

```

* **Request (Yêu cầu):**
* **Chiều gửi:** Do **Client (Trình duyệt)** tạo ra và gửi tới **Server**.
* **Mục đích:** Yêu cầu lấy dữ liệu (GET), gửi dữ liệu mới (POST), cập nhật (PUT), xóa (DELETE)...
* **Thành phần chính:**
* *URL & Method:* `GET /users`
* *Headers:* Thông tin thiết bị, token xác thực, loại nội dung mong muốn...
* *Body:* Dữ liệu gửi kèm (khi dùng POST/PUT, ví dụ: thông tin form đăng ký).




* **Response (Phản hồi):**
* **Chiều gửi:** Do **Server** tạo ra và gửi ngược lại cho **Client**.
* **Mục đích:** Trả lại kết quả sau khi đã xử lý xong yêu cầu.
* **Thành phần chính:**
* *Status Code (Mã trạng thái):* `200` (Thành công), `404` (Không tìm thấy), `500` (Lỗi server)...
* *Headers:* Loại dữ liệu trả về (`content-type: application/json`), độ dài...
* *Body:* Nội dung kết quả (file HTML, dữ liệu JSON, ảnh, v.v.).

