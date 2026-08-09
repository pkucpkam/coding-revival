# 🚀 Bài 3 — Servlet đầu tiên

Chào bro, từ bài này chúng ta chính thức bước từ web fundamentals sang Java Servlet.

Mục tiêu của bài hôm nay là giúp bro hình thành một mental model đúng về cách một request web đi từ trình duyệt tới Java code.

## Mental model cần ghi nhớ

```text
Browser
  ↓
HTTP Request
  ↓
Servlet Container
  ↓
Servlet
  ↓
Java code
  ↓
HTTP Response
  ↓
Browser
```

Sau khi học xong bài này, bro nên tự trả lời được câu hỏi:

> Vì sao request `GET /hello` lại chạy vào `doGet()` của `HelloServlet`?

---

## 1. Java Web Application là gì?

Một Java Web Application là ứng dụng Java chạy trong môi trường web/container, nhận HTTP request, xử lý logic và trả HTTP response.

Ví dụ một ứng dụng có thể có:

```text
Servlet
JSP
Java classes
Configuration
Libraries
Static resources
```

Cấu trúc tổng quát:

```text
Browser
  ↓
HTTP
  ↓
Tomcat / JBoss / Jetty
  ↓
Java Web Application
  ├── Servlet
  ├── JSP
  ├── Service
  ├── DAO
  └── ...
```

---

## 2. WAR là gì?

Trong Java Enterprise, một ứng dụng web thường được đóng gói thành file `.war`.

WAR = Web Application Archive.

Nghĩ đơn giản: `.war` giống như một gói chứa toàn bộ ứng dụng web để deploy lên server.

Ví dụ cấu trúc cơ bản:

```text
my-app.war
├── WEB-INF/
│   ├── web.xml
│   ├── classes/
│   └── lib/
├── index.jsp
├── css/
├── js/
└── images/
```

Đừng lo lắng nếu bro chưa nhớ toàn bộ cấu trúc ngay. Chỉ cần hiểu một điều:

```text
Java Web Application → WAR → Deploy lên Tomcat/JBoss
```

---

## 3. Servlet Container làm gì?

Đây là phần cực kỳ quan trọng.

Khi browser gửi request như:

```http
GET /hello HTTP/1.1
```

trình duyệt không biết gì về class `HelloServlet` cả. Nó chỉ biết URL là `/hello`.

Nhiệm vụ của Servlet Container là:

```text
GET /hello
  ↓
Tìm mapping phù hợp
  ↓
Gọi đúng Servlet
```

Container có vai trò như một “người điều phối” giữa request HTTP và Java code.

---

## 4. URL Mapping là gì?

URL mapping là cách nối một URL với một Servlet.

Ví dụ:

```java
@WebServlet("/hello")
public class HelloServlet extends HttpServlet {
}
```

Ta có mapping:

```text
/hello → HelloServlet
```

Như vậy khi browser gọi:

```http
GET /hello
```

container sẽ biết phải gửi request tới `HelloServlet`.

---

## 5. Servlet đầu tiên

Đây là đoạn code mà bro sẽ bắt đầu làm quen đầu tiên.

```java
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/hello")
public class HelloServlet extends HttpServlet {

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        response.setContentType("text/plain");
        response.getWriter().println("Hello World");
    }
}
```

Đừng chạy ngay mà hãy đọc từng phần một.

---

## 6. `extends HttpServlet`

```java
public class HelloServlet extends HttpServlet
```

Có nghĩa là `HelloServlet` kế thừa từ lớp `HttpServlet` do Servlet API cung cấp.

`HttpServlet` giúp servlet xử lý các HTTP methods như:

```text
GET    → doGet()
POST   → doPost()
PUT    → doPut()
DELETE → doDelete()
```

Do đó khi chúng ta override `doGet()`, chúng ta đang nói với container:

> “Nếu request này là GET, hãy xử lý bằng phương thức này.”

---

## 7. `@WebServlet("/hello")`

```java
@WebServlet("/hello")
```

Đây là annotation-based URL mapping.

Nó nói với container rằng:

> Servlet này sẽ xử lý URL `/hello`.

Ví dụ:

```java
@WebServlet("/users")
```

tương đương với mapping:

```text
/users → UserServlet
```

### Vì sao cần `@WebServlet`?

`@WebServlet` có vai trò giống như một “bản đồ định tuyến” giữa HTTP request và servlet.

Ví dụ:

```java
@WebServlet("/hello")
public class HelloServlet extends HttpServlet {
}
```

Khi browser gọi:

```http
GET /hello
```

container sẽ biết phải chuyển request đó tới class `HelloServlet`.

Nếu không có `@WebServlet`, thì container sẽ không biết servlet này liên quan tới URL nào, nên request đó sẽ không được route đúng.

Nói ngắn gọn:

```text
URL /hello → @WebServlet → HelloServlet
```

`@WebServlet` giúp:

- đăng ký servlet với container
- ánh xạ URL tới servlet phù hợp
- cho phép request được chuyển đúng lớp xử lý

---

## 8. `HttpServletRequest`

```java
HttpServletRequest request
```

Đây là đối tượng đại diện cho request mà client gửi tới server.

Bro có thể đọc thông tin từ request như:

```java
request.getMethod();
request.getRequestURI();
request.getParameter("name");
request.getHeader("Accept");
```

Ví dụ request như sau:

```http
GET /hello?name=Phuc HTTP/1.1
Host: localhost:8080
Accept: text/plain
```

thì servlet có thể đọc được các thông tin đó từ `request`.

---

## 9. `HttpServletResponse`

```java
HttpServletResponse response
```

Đây là đối tượng đại diện cho response sẽ gửi về client.

Servlet dùng nó để quyết định nội dung trả về.

Ví dụ:

```java
response.setStatus(200);
response.setContentType("text/plain");
response.getWriter().println("Hello World");
```

Có thể hiểu đơn giản:

```text
request → servlet xử lý → response trả về client
```

---

## 10. `doGet()` là gì?

Đây là phần rất quan trọng.

```java
@Override
protected void doGet(
        HttpServletRequest request,
        HttpServletResponse response)
```

Nó tương ứng với HTTP GET.

Khi browser gửi:

```http
GET /hello HTTP/1.1
```

container sẽ xử lý theo luồng sau:

```text
GET
  ↓
service()
  ↓
doGet()
```

Nhắc lại một điểm quan trọng:

> Container không gọi `doGet()` trực tiếp ngay lập tức như một hàm đơn giản. Trước đó còn có bước `service()`.

Một ghi chú rất đáng nhớ nữa:

Nếu bro gửi:

```http
POST /greeting
```

mà servlet chỉ có `doGet()` nhưng không override `doPost()`, thì `HttpServlet.service()` vẫn sẽ dispatch POST sang `doPost()`.

Tuy nhiên, vì `doPost()` không được override, `HttpServlet` sẽ dùng implementation mặc định.

Kết quả thường là:

```http
HTTP/1.1 405 Method Not Allowed
```

chứ không phải là “không tìm thấy hàm `doPost()`”.

Flow tương ứng:

```text
POST /greeting
  ↓
GreetingServlet
  ↓
service()
  ↓
POST
  ↓
doPost()
  ↓
Không override doPost()
  ↓
HttpServlet xử lý mặc định
  ↓
405 Method Not Allowed
```

Đây là kiến thức rất đáng nhớ.

### GET

```text
GET
 ↓
doGet()
```

### POST

```text
POST
 ↓
doPost()
```

### PUT

```text
PUT
 ↓
doPut()
```

### DELETE

```text
DELETE
 ↓
doDelete()
```

---

## 11. Servlet lifecycle — điều cần hiểu đúng

Một servlet không phải được tạo lại cho mỗi request.

Mental model đúng hơn là:

```text
Application deployed
  ↓
Container loads servlet
  ↓
Create servlet instance
  ↓
init()
  ↓
Request arrives
  ↓
service()
  ↓
doGet()/doPost()
  ↓
...
  ↓
destroy()
```

Ba lifecycle method quan trọng:

```text
init()
service()
destroy()
```

### `init()`

Gọi khi servlet được khởi tạo.

```java
@Override
public void init() throws ServletException {
    System.out.println("Servlet initialized");
}
```

Thông thường chạy một lần cho một servlet instance.

### `service()`

Đây là điểm điều phối request theo method HTTP.

```text
GET  → doGet()
POST → doPost()
```

### `destroy()`

Gọi khi container dừng hoặc loại bỏ servlet.

```java
@Override
public void destroy() {
    System.out.println("Servlet destroyed");
}
```

---

## 12. Một hiểu lầm rất quan trọng: servlet có được tạo mỗi request không?

Không đúng.

Không nên nghĩ rằng:

```text
Request 1 → new Servlet()
Request 2 → new Servlet()
Request 3 → new Servlet()
```

Thay vào đó, một servlet instance thường được tạo một lần và có thể phục vụ nhiều request liên tiếp.

```text
Servlet instance
  ↓
init()
  ↓
service() cho request 1
  ↓
service() cho request 2
  ↓
service() cho request 3
  ↓
destroy()
```

Điều này dẫn đến vấn đề thread safety.

Ví dụ nếu servlet có field như:

```java
private String username;
```

và nhiều request cùng lúc cập nhật field này, sẽ có rủi ro vì nhiều thread có thể truy cập cùng instance.

Đây là điểm mà sau này bro sẽ học sâu hơn.

---

## 13. Request flow hoàn chỉnh

Giả sử browser gọi:

```http
GET /hello HTTP/1.1
Host: localhost:8080
```

Flow sẽ là:

1. Browser tạo HTTP request.
2. Request đi tới Tomcat hoặc container khác.
3. Container đọc URL `/hello`.
4. Container tìm mapping phù hợp.
5. Nếu servlet chưa được khởi tạo, container tạo instance và gọi `init()`.
6. Container gọi `service()`.
7. `service()` thấy đây là `GET` nên gọi `doGet()`.
8. `doGet()` viết response body.
9. Container gửi response về browser.
10. Browser hiển thị nội dung.

Sơ đồ tổng quát:

```text
Browser
  ↓
Servlet Container
  ↓
URL Mapping
  ↓
HelloServlet
  ↓
service()
  ↓
doGet()
  ↓
HTTP Response
  ↓
Browser
```

---

## 14. `response.getWriter()` và `setContentType()`

Đoạn code này:

```java
response.setContentType("text/plain");
response.getWriter().println("Hello World");
```

có ý nghĩa:

- `setContentType("text/plain")`: nói với client rằng response body là plain text.
- `getWriter()`: lấy writer để ghi nội dung vào body của response.

Nếu đổi thành HTML:

```java
response.setContentType("text/html");
```

và body là:

```html
<h1>Hello World</h1>
```

browser sẽ render thành HTML.

Nếu dùng JSON:

```java
response.setContentType("application/json");
```

thì sau này bro sẽ thấy nó rất thường xuyên trong REST API.

---

## 15. Bài tập nhỏ — suy luận trước

Giả sử có servlet như sau:

```java
@WebServlet("/users")
public class UserServlet extends HttpServlet {

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        response.setContentType("text/plain");
        response.getWriter().println("User list");
    }
}
```

Khi browser gửi:

```http
GET /users HTTP/1.1
Host: localhost:8080
```

bro hãy tự trace các bước từ browser → container → servlet → response.

---

## 16. Bài tập code bắt buộc

Hãy tự viết một servlet có URL `/greeting`.

Khi request:

```http
GET /greeting
```

response trả về:

```text
Hello Phuc
```

Yêu cầu:

- extends `HttpServlet`
- dùng `@WebServlet("/greeting")`
- override `doGet()`
- set content type là `text/plain`
- dùng `HttpServletResponse`
- không cần database, không cần JSP

Cấu trúc mong muốn:

```text
src/
└── GreetingServlet.java
```

---

## 17. Câu hỏi ôn tập

Sau khi làm xong, bro hãy tự trả lời 5 câu hỏi sau bằng lời nói, không chỉ bằng code:

1. Vì sao `@WebServlet("/greeting")` lại khiến request `GET /greeting` được route tới class này?
2. Vì sao `doGet()` được gọi?
3. `init()` khác `service()` như thế nào?
4. Một servlet có phải được tạo mới cho mỗi request không?
5. Nếu có 100 user cùng lúc request `/greeting`, điều gì có thể xảy ra với servlet instance?

---

## 18. Tổng kết ngắn gọn

Sau bài này, bro nên nắm được 5 ý chính:

- Servlet là component Java dùng để xử lý request web.
- Container chịu trách nhiệm routing request tới servlet đúng.
- `@WebServlet` dùng để ánh xạ URL tới servlet.
- `doGet()` xử lý HTTP GET.
- Servlet lifecycle gồm `init()`, `service()`, `destroy()`.

Nếu bro đã hiểu được các ý trên, tức là đã bước qua được bước đầu tiên để trở thành người hiểu Servlet, không chỉ biết viết một đoạn code đơn giản.
