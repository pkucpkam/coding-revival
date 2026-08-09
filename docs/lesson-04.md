# 🚀 Bài 4 — Servlet Lifecycle & Thread Model

Đây là một bài cực kỳ quan trọng vì nó giúp bro hiểu sâu hơn về cách Servlet thực sự hoạt động khi có nhiều người truy cập cùng lúc.

Nếu bài 3 giúp bro hiểu:

```text
Browser
  ↓
HTTP Request
  ↓
Container
  ↓
Servlet
  ↓
service()
  ↓
doGet()/doPost()
```

thì bài 4 sẽ giúp bro trả lời câu hỏi tiếp theo:

> Khi có nhiều user cùng truy cập, Servlet chạy như thế nào?

Và từ đó bro sẽ hiểu vì sao không nên để dữ liệu của từng request trong instance field của Servlet.

---

## 1. Hãy tưởng tượng Servlet như một người làm việc

Đừng nghĩ Servlet là một cái hàm chạy rồi kết thúc ngay.

Hãy hình dung Servlet như một người nhân viên làm việc trong một công ty:

```text
Container như quản lý công ty
Servlet như nhân viên
Request như công việc đến
```

Một nhân viên có thể nhận nhiều công việc liên tiếp.

Một nhân viên cũng có thể làm nhiều việc cùng lúc nếu có nhiều người giao việc.

Servlet cũng vậy.

```text
Container
  ↓
Nhận request
  ↓
Giao cho Servlet xử lý
```

---

## 2. Servlet lifecycle là gì?

Lifecycle của Servlet là vòng đời từ lúc nó được tạo ra cho tới lúc bị hủy.

Có thể hiểu đơn giản là:

```text
Tạo Servlet
  ↓
Khởi tạo (init)
  ↓
Nhận request (service)
  ↓
Xử lý request
  ↓
Dừng / hủy (destroy)
```

Ba method quan trọng nhất là:

```java
init()
service()
destroy()
```

---

# 1. Servlet lifecycle là gì?

Lifecycle của Servlet là vòng đời từ khi nó được container tạo ra, cho tới khi nó bị hủy.

Một Servlet có thể được mô tả theo đúng thứ tự sau:

```text
Deploy application
  ↓
Container tạo Servlet instance
  ↓
init()
  ↓
service() cho request 1
  ↓
service() cho request 2
  ↓
...
  ↓
destroy()
```

Có 3 method quan trọng:

```java
init()
service()
destroy()
```

## 2.1 `init()` — lúc servlet bắt đầu làm việc

`init()` chạy khi servlet được khởi tạo lần đầu.

Hãy nghĩ nó như lúc nhân viên vừa vào công ty, chuẩn bị đồ nghề và môi trường làm việc.

```text
Servlet được tạo
  ↓
init()
  ↓
Sẵn sàng nhận request
```

```java
@Override
public void init() throws ServletException {
    System.out.println("Servlet initialized");
}
```

Ý nghĩa:
- dùng để khởi tạo dữ liệu ban đầu
- load cấu hình
- chuẩn bị resource

Thông thường `init()` chỉ chạy một lần cho một servlet instance.

## 2.2 `service()` — lúc servlet nhận việc

`service()` là method trung tâm để container nhận request và điều phối sang đúng handler.

Hãy hình dung:

```text
Một request đến
  ↓
service()
  ↓
Xác định là GET hay POST
  ↓
Gọi doGet() / doPost()
```

Ví dụ:

```text
GET /hello
  ↓
service()
  ↓
doGet()
```

```text
POST /hello
  ↓
service()
  ↓
doPost()
```

```java
@Override
protected void service(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
    // container gọi ở đây
}
```

Trong `HttpServlet`, `service()` sẽ phân loại theo HTTP method:

```text
GET  → doGet()
POST → doPost()
PUT  → doPut()
DELETE → doDelete()
```

## 2.3 `destroy()` — lúc servlet nghỉ việc

`destroy()` chạy khi servlet bị container loại bỏ.

Hãy tưởng tượng như nhân viên nghỉ việc, đóng cửa và thu dọn đồ dùng.

```text
Servlet bị dừng
  ↓
destroy()
  ↓
Dọn dẹp resource
```

```java
@Override
public void destroy() {
    System.out.println("Servlet destroyed");
}
```

Dùng để cleanup tài nguyên như:
- đóng connection
- giải phóng file
- dọn cache

---

# 3. `init()` không chạy mỗi request

Đây là một điểm rất dễ nhầm lần đầu.

Ví dụ:

```java
public class HelloServlet extends HttpServlet {

    @Override
    public void init() {
        System.out.println("INIT");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        System.out.println("GET");
    }
}
```

Nếu có 3 request liên tiếp:

```text
GET /hello
GET /hello
GET /hello
```

thì output sẽ không là:

```text
INIT
GET
INIT
GET
INIT
GET
```

mà sẽ là:

```text
INIT
GET
GET
GET
```

### Vì sao?

Vì servlet instance được tạo một lần, rồi dùng lại nhiều lần cho những request sau.

Mental model:

```text
Servlet instance
  ├── init()        ← chạy 1 lần
  ├── service()     ← request 1
  ├── service()     ← request 2
  ├── service()     ← request 3
  └── destroy()     ← chạy 1 lần
```

---

# 4. Nhưng nếu có nhiều request cùng lúc thì sao?

Đây mới là phần thật sự quan trọng.

Giả sử có 3 người cùng truy cập:

```text
Phúc
Nam
An
```

cùng gọi:

```http
GET /hello
```

Container có thể xử lý bằng nhiều thread khác nhau:

```text
Request Phúc ── Thread 1 ──┐
                           │
Request Nam  ── Thread 2 ──┼──→ HelloServlet
                           │
Request An   ── Thread 3 ──┘
```

### Điều quan trọng

Một servlet instance có thể được nhiều thread sử dụng đồng thời.

Nên mental model đúng là:

```text
                Servlet instance
                 ┌──────────────┐
Thread 1 ───────→│              │
Thread 2 ───────→│   HelloServlet│
Thread 3 ───────→│              │
Thread 4 ───────→│              │
                 └──────────────┘
```

> Một servlet instance không phải “chỉ phục vụ một request rồi chết”.
> Nó có thể phục vụ nhiều request, và nhiều request có thể chạy cùng lúc.

---

# 5. Vì sao điều này nguy hiểm?

Giả sử mình viết servlet như thế này:

```java
public class UserServlet extends HttpServlet {

    private String username;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        username = request.getParameter("username");

        response.setContentType("text/plain;charset=UTF-8");
        response.getWriter().println("Hello " + username);
    }
}
```

Có vẻ không có gì sai phải không?

Nhưng hãy tưởng tượng có 2 request chạy cùng lúc:

### Request A

```http
GET /user?username=Phuc
```

### Request B

```http
GET /user?username=Nam
```

---

# 6. Vấn đề xảy ra như thế nào?

Giả sử ban đầu:

```text
username = null
```

### Thread A

```java
username = "Phuc";
```

### Thread B

```java
username = "Nam";
```

Sau đó Thread A có thể đọc lại biến này và in ra:

```text
Hello Nam
```

Đây là một bug rất phổ biến gọi là:

```text
race condition
```

hoặc nói ngắn gọn là:

```text
shared mutable state problem
```

## 5.1 Ví dụ dễ hình dung

Hãy tưởng tượng:
- một cái bàn chung
- nhiều người cùng sử dụng
- ai cũng viết lên cùng một tờ giấy

Nếu mỗi người dùng riêng một tờ giấy thì không sao.
Nhưng nếu cùng dùng một tờ giấy chung thì dữ liệu bị overwrite.

Đó chính là vấn đề ở đây.

---

# 7. Vì sao instance field lại nguy hiểm?

Vì field này:

```java
private String username;
```

là instance variable, nghĩa là thuộc về servlet object chung.

Mỗi thread đều có thể đọc/ghi vào cùng một biến này.

```text
UserServlet object
    │
    └── username = ?
         ↑
    Thread A / Thread B / Thread C
```

### Tóm lại

- instance field là shared state
- shared state bị nhiều thread truy cập cùng lúc
- nếu có write operation thì dễ bị race condition

---

# 8. Local variable thì khác

Đây là cách an toàn hơn nhiều:

```java
@Override
protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws IOException {

    String username = request.getParameter("username");

    response.setContentType("text/plain;charset=UTF-8");
    response.getWriter().println("Hello " + username);
}
```

Ở đây `username` là local variable.

Mỗi thread sẽ có biến riêng cho từng execution context:

```text
Thread A
  └── username = "Phuc"

Thread B
  └── username = "Nam"
```

Không ai dùng chung cùng một biến.

## 7.1 Mental model

```text
Servlet instance
  ├── Thread A → local username = "Phuc"
  ├── Thread B → local username = "Nam"
  └── Thread C → local username = "An"
```

### Nguyên tắc quan trọng

> Request-specific data nên nằm trong local variable hoặc trong scope phù hợp như request/session, chứ không nên đặt vào instance field nếu nó thay đổi theo request.

---

# 9. Instance field có phải lúc nào cũng sai không?

Không hoàn toàn.

Đây là điểm cần hiểu kỹ.

```java
private String something;
```

Không phải tự động là bug.

Vấn đề là nó có bị nhiều thread cùng đọc/ghi hay không.

## 8.1 Các trường hợp an toàn

### 1. Constant / immutable

```java
private static final String APP_NAME = "Cathay";
```

Giá trị này không đổi, nên không có vấn đề gì.

### 2. Service dependency không thay đổi

```java
private final UserService userService;
```

Nếu `UserService` là stateless và thread-safe thì việc nhiều thread dùng chung cũng thường ổn.

## 8.2 Các trường hợp nguy hiểm

```java
private String username;
private User currentUser;
private Order currentOrder;
```

Vì đây là dữ liệu thay đổi theo request, nên rất dễ bị race condition.

### Quy tắc nhớ nhanh

```text
Shared + Mutable + Request-specific = dangerous
```

---

# 10. `HttpServletRequest` và `HttpServletResponse` có dùng chung không?

Không.

Mỗi request có một object request riêng.

```text
Request A → HttpServletRequest A
Request B → HttpServletRequest B
```

Tương tự:

```text
Request A → HttpServletResponse A
Request B → HttpServletResponse B
```

Đây là dữ liệu riêng cho từng request, nên không phải là shared state nguy hiểm như instance field.

---

# 11. `HttpSession` thì khác

Session là một khái niệm hơi khác.

```text
Browser
  ↓
JSESSIONID
  ↓
HttpSession
```

Một session có thể được dùng bởi nhiều request của cùng một user.

Ví dụ:

```text
Request 1 ──┐
Request 2 ──┼──→ HttpSession
Request 3 ──┘
```

Vì vậy session cũng có thể gặp vấn đề concurrency nếu nhiều request cùng cập nhật dữ liệu session.

```java
session.setAttribute("cart", cart);
```

Đây là một dạng shared state ở mức session, khác với instance field nhưng vẫn cần cẩn thận.

---

# 12. `init()` dùng để làm gì?

`init()` phù hợp cho việc khởi tạo ban đầu.

```java
@Override
public void init() throws ServletException {
    System.out.println("Servlet starting...");
}
```

Ví dụ các việc nên làm ở `init()`:
- load config
- khởi tạo resource
- chuẩn bị dependency

Tuy nhiên, trong các framework hiện đại như Spring, việc này thường được giao cho container/DI framework.

---

# 13. `destroy()` dùng để làm gì?

Khi servlet bị dừng, container gọi `destroy()` để cleanup.

```java
@Override
public void destroy() {
    System.out.println("Servlet shutting down...");
}
```

Ví dụ:
- đóng connection
- giải phóng file handle
- dọn resource đã mở

Mental model:

```text
init()    → prepare
service() → work
destroy() → cleanup
```

---

# 14. Servlet có init ngay khi deploy không?

Không nhất thiết.

Container có thể lazy initialize servlet khi request đầu tiên đến.

```text
Deploy app
  ↓
Servlet chưa init
  ↓
Request đến
  ↓
Tạo servlet instance
  ↓
init()
  ↓
service()
  ↓
doGet()
```

Có thể cấu hình để servlet load ngay lúc startup bằng:

```java
@WebServlet(value = "/hello", loadOnStartup = 1)
```

Đây là một chi tiết nâng cao, lúc đầu bro không cần nhớ sâu. Chỉ cần hiểu là:

```text
lazy initialization
vs
startup initialization
```

---

# 15. Lifecycle đúng hơn theo hình ảnh

```text
                   Deploy
                     │
                     ↓
              Servlet available
                     │
                     ↓
             Create servlet instance
                     │
                     ↓
                   init()
                     │
                     ↓
      ┌────────────────────────────┐
      │         SERVICE             │
      │ Request 1 → doGet()        │
      │ Request 2 → doPost()       │
      │ Request 3 → doGet()        │
      │ Request 4 → doGet()        │
      └──────────────┬─────────────┘
                     │
                     ↓
                  destroy()
```

---

# 16. `service()` làm gì?

Bro nhớ lại từ bài trước:

```text
GET  → doGet()
POST → doPost()
PUT  → doPut()
DELETE → doDelete()
```

Đây là do `HttpServlet.service()` làm.

```text
HTTP method
  ↓
service()
  ↓
đúng handler
```

Ví dụ:

```text
GET /hello
  ↓
service()
  ↓
doGet()
```

```text
POST /hello
  ↓
service()
  ↓
doPost()
```

---

# 17. Thường thì bro không override `service()`

Thông thường bro chỉ override các method cụ thể như:

```java
@Override
protected void doGet(...) {
}
```

hoặc:

```java
@Override
protected void doPost(...) {
}
```

Vì `HttpServlet` đã tự làm việc phân phối request cho mình.

```text
HTTP method
  ↓
service()
  ↓
đúng method của bạn
```

---

# 18. Thread model — phần cốt lõi

Mental model cần nhớ:

```text
Servlet instance
   ┌────────────────────┐
   │                    │
Thread 1 ─┤                    │
Thread 2 ─┤   UserServlet     │
Thread 3 ─┤                    │
Thread 4 ─┤                    │
   └────────────────────┘
```

Nghĩa là:
- cùng một servlet instance có thể được nhiều thread dùng cùng lúc
- vì vậy mọi data nằm trong instance field đều có thể bị nhiều thread truy cập

### ❌ Tránh

```java
public class UserServlet extends HttpServlet {

    private User currentUser;
}
```

nếu `currentUser` được thay đổi theo từng request.

### ✅ Thường an toàn hơn

```java
@Override
protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws IOException {

    User currentUser = userService.findById(1);
}
```

Ở đây `currentUser` là local variable, nên mỗi request có một bản copy riêng.

---

# 19. Nhưng đừng hiểu sai rằng “Servlet không được có field”

Đây là nuance cực kỳ quan trọng.

```java
public class UserServlet extends HttpServlet {

    private final UserService userService;
}
```

Đây không nhất thiết là sai.

Nếu `UserService` là stateless và thread-safe thì nhiều thread cùng dùng vẫn thường ổn.

Vấn đề không phải là “có field” mà là:

```text
shared + mutable + request-specific
```

Nếu field là immutable hoặc không thay đổi, thì thường không vấn đề.

---

# 20. Ví dụ thực tế dễ hiểu

## 19.1 Ví dụ sai

```java
public class LoginServlet extends HttpServlet {

    private String username;
    private String password;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        username = request.getParameter("username");
        password = request.getParameter("password");

        // login logic
    }
}
```

Nếu 2 user login cùng lúc:

```text
User A → username = phuc
User B → username = nam
```

khi đó dữ liệu có thể bị overwrite.

## 19.2 Ví dụ tốt hơn

```java
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        // login logic
    }
}
```

Ở đây mỗi request có dữ liệu riêng.

---

# 21. Tại sao `Thread.sleep(100)` làm bug dễ xảy ra hơn?

Đây là một ví dụ rất hay để hiểu race condition.

```java
@Override
protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws IOException {

    username = request.getParameter("username");

    try {
        Thread.sleep(100);
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    }

    response.getWriter().println("Hello " + username);
}
```

Nếu có 2 request chạy cùng lúc:
- Thread A set `username = "Phuc"`
- Thread A ngủ 100ms
- Thread B set `username = "Nam"`
- Thread A tiếp tục in ra `Hello Nam`

=> Phúc nhận được response của Nam.

Đây là bug rất điển hình.

---

# 22. Timeline của race condition

Giả sử có 2 request:

```text
Request A: username=Phuc
Request B: username=Nam
```

Timeline có thể là:

```text
T0: username = null
T1: Thread A set username = "Phuc"
T2: Thread A sleep 100ms
T3: Thread B set username = "Nam"
T4: Thread A wake up and print username
T5: output = "Hello Nam"
```

Chính vì thế user A nhận kết quả sai.

---

# 23. Quy tắc vàng của bài này

Bro nhớ 4 câu này:

> 1. Servlet instance có thể phục vụ nhiều request.

> 2. Nhiều request có thể được xử lý đồng thời bởi nhiều thread.

> 3. Instance field là shared state giữa các thread.

> 4. Request-specific mutable data nên tránh đặt trong instance field.

---

# 24. Bài tập thực hành

Cho servlet sau:

```java
@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {

    private String username;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        username = request.getParameter("username");

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        response.setContentType("text/plain;charset=UTF-8");
        response.getWriter().println("Hello " + username);
    }
}
```

### Câu hỏi

1. Vì sao `Thread.sleep(100)` làm bug dễ xảy ra hơn?
2. Có thể xảy ra trường hợp Phúc nhận `Hello Nam` không?
3. Biến nào đang bị shared?
4. Sửa code như thế nào để mỗi request luôn dùng username riêng?
5. Nếu đổi thành:

```java
private final UserService userService;
```

thì có còn cùng bug không? Vì sao?

---

# 25. Tóm kết bài học

Sau bài này, bro nên hiểu được:

- Servlet lifecycle gồm `init()`, `service()`, `destroy()`.
- Servlet instance có thể phục vụ nhiều request.
- Nhiều request có thể chạy song song bằng nhiều thread.
- Instance field là shared state và có thể gây race condition.
- Dữ liệu của từng request nên dùng local variable hoặc scope phù hợp.

Đây là nền tảng để hiểu sâu hơn về Servlet trong các dự án thực tế.

---

Sau bài này, mình sẽ dẫn bro sang bài 5: Servlet Request/Response API, nơi mình bắt đầu code thật với `getParameter`, `getAttribute`, `setAttribute`, `forward`, `redirect`, headers, cookies và session.
