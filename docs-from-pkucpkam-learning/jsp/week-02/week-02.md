	# 📚 TUẦN 2: JSP Basics - Tài Liệu Lý Thuyết Chi Tiết

**Giáo viên hướng dẫn: Senior Java Developer**
**Cấp độ: Người mới bắt đầu (Tiếp theo Tuần 1)**
**Thời lượng: 1 tuần (7 phiên)**

---

## 📋 Nội dung Tuần 2

- **Session 8**: JSP Introduction
- **Session 9**: Implicit Objects
- **Session 10**: JSP Include
- **Session 11**: JSP Expression Language (EL)
- **Session 12**: JSTL Basics
- **Session 13**: Form Handling
- **Session 14**: Mini Project

---

# 🎯 SESSION 8: JSP Introduction

## 8.1 JSP là gì?

**JSP** = **J**ava **S**erver **P**ages

### Định nghĩa

JSP là một công nghệ cho phép bạn viết **HTML kết hợp với Java code** để tạo trang web động.

**Khác nhau giữa Servlet và JSP:**

```
Servlet:
  Java → Biên dịch → Bytecode → Chạy
  (Code Java với HTML print)

JSP:
  HTML + Java Tags → Biên dịch → Servlet → Chạy
  (HTML với Java code nhúng)
```

### Ví dụ đơn giản

**Servlet:**
```java
protected void doGet(HttpServletRequest request, 
                    HttpServletResponse response) {
    response.setContentType("text/html; charset=UTF-8");
    PrintWriter out = response.getWriter();
    out.println("<h1>Hello " + request.getParameter("name") + "</h1>");
}
```

**JSP (đơn giản hơn):**
```jsp
<h1>Hello <%= request.getParameter("name") %></h1>
```

---

## 8.2 Quy trình Biên dịch JSP

```
1. Client gửi request đến JSP
         ↓
2. Tomcat kiểm tra: JSP file có tồn tại chưa?
         ↓
3. JSP → Tomcat biên dịch thành Servlet (.java)
         ↓
4. Servlet → Biên dịch thành Bytecode (.class)
         ↓
5. Bytecode → Chạy
         ↓
6. Kết quả HTML → gửi về client
         ↓
7. Browser nhận và hiển thị
```

### Ví dụ Quá trình Biên dịch

**File JSP gốc:** `hello.jsp`
```jsp
<h1>Hello <%= new java.util.Date() %></h1>
```

**Servlet được tạo:** `hello_jsp.java`
```java
public class hello_jsp extends HttpJspBase {
    public void _jspService(HttpServletRequest request,
                           HttpServletResponse response) {
        // ... Tomcat tạo code tự động
        out.println("<h1>Hello ");
        out.print(new java.util.Date());
        out.println("</h1>");
    }
}
```

**Bytecode:** `hello_jsp.class`

---

## 8.3 Bốn yếu tố chính của JSP

### 1. Directive - `<%@ ... %>`

**Mục đích:** Báo cho Tomcat hướng dẫn về JSP.

**Các loại Directive:**

#### a) Page Directive
```jsp
<%@ page attribute="value" %>

Ví dụ:
<%@ page language="java" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.*,java.io.*" %>
<%@ page errorPage="error.jsp" %>
<%@ page isErrorPage="true" %>
```

#### b) Include Directive
```jsp
<%@ include file="header.jsp" %>
<!-- Nhúng nội dung file khác vào lúc biên dịch -->
```

#### c) Taglib Directive
```jsp
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!-- Import thư viện JSTL -->
```

**Ví dụ thực tế:**
```jsp
<%@ page language="java" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.Date, java.util.List" %>
<%@ page errorPage="error.jsp" %>

<html>
<head><title>Page Directive Demo</title></head>
<body>
  <h1>Hello World</h1>
</body>
</html>
```

---

### 2. Scriptlet - `<% ... %>`

**Mục đích:** Nhúng Java code trực tiếp vào JSP.

**Cú pháp:**
```jsp
<% Java code here %>
```

**Ví dụ:**
```jsp
<% 
    String name = request.getParameter("name");
    int age = 25;
    
    if (name != null && !name.isEmpty()) {
        out.println("<h1>Hello " + name + "</h1>");
    } else {
        out.println("<h1>Please enter your name</h1>");
    }
%>
```

**Khi nào dùng Scriptlet:**
- Viết logic phức tạp
- Loop, if/else
- Khai báo biến tạm thời
- Gọi method từ Java class

**Lưu ý:** Nếu quá nhiều Scriptlet, code sẽ khó đọc. Nên dùng JSP Model 2 (Servlet + JSP).

---

### 3. Expression - `<%= ... %>`

**Mục đích:** In giá trị của biến hoặc expression ra trang web.

**Cú pháp:**
```jsp
<%= expression %>
```

**Ví dụ:**
```jsp
<!-- In giá trị -->
<p>Tổng: <%= 5 + 3 %></p>

<!-- In biến -->
<% String name = "Nhân"; %>
<p>Xin chào <%= name %></p>

<!-- In kết quả method -->
<p>Thời gian hiện tại: <%= new java.util.Date() %></p>

<!-- In request parameter -->
<p>Email: <%= request.getParameter("email") %></p>
```

**Tương đương với:**
```jsp
<% out.print(expression); %>
```

**Lưu ý:** Expression không chứa dấu chấm phẩy (;) ở cuối.

---

### 4. Declaration - `<%! ... %>`

**Mục đích:** Khai báo biến hoặc method ở mức class (không phải local).

**Cú pháp:**
```jsp
<%! 
    declaration
%>
```

**Ví dụ:**

```jsp
<%! 
    // Biến instance (toàn bộ JSP có thể truy cập)
    int counter = 0;
    String appName = "My JSP App";
    
    // Method
    public int add(int a, int b) {
        return a + b;
    }
    
    public String getGreeting(String name) {
        return "Hello " + name + "!";
    }
%>

<h1><%= getGreeting("Nhân") %></h1>
<p>Tổng 5 + 3 = <%= add(5, 3) %></p>
<p>Tên ứng dụng: <%= appName %></p>
```

**Khác biệt:**

```
Scriptlet:      <% int x = 5; %>       (Local variable)
Declaration:    <%! int x = 5; %>      (Instance variable/method)
```

---

## 8.4 JSP Lifecycle (Vòng đời JSP)

### Sơ đồ chi tiết

```
┌────────────────────────────────────┐
│   Client request JSP file          │
└────────────────────────────────────┘
                ↓
┌────────────────────────────────────┐
│   Tomcat tìm JSP file              │
│   Có phải lần đầu tiên?            │
└────────────────────────────────────┘
                ↓
         ┌──────┴──────┐
         ↓             ↓
     [YES]          [NO]
  (Lần đầu)      (Request khác)
     ↓             ↓
 Translation   Service (Servlet)
 (Dịch JSP)       ↓
     ↓        Gọi _jspService()
  Compilation    ↓
  (Biên dịch)   Trả HTML response
     ↓             ↓
   Loading    ┌─────┴───────┐
   (Load)     │   Có error? │
     ↓        └─────┬───────┘
 Instantiation      ↓
 (Tạo object)   [YES] → Error page
     ↓         [NO]
 Initialization     ↓
  (jspInit())   Gửi response
     ↓
  Service
```

### 5 Giai đoạn JSP Lifecycle

#### 1. Translation
```
hello.jsp → Tomcat biên dịch → hello_jsp.java
```

#### 2. Compilation
```
hello_jsp.java → Compiler → hello_jsp.class
```

#### 3. Loading
```
hello_jsp.class được load vào memory
```

#### 4. Instantiation
```
Tạo object từ class: hello_jsp servlet = new hello_jsp();
```

#### 5. Initialization
```
Gọi jspInit() - khởi tạo servlet
```

#### 6. Service
```
Mỗi lần có request → Gọi _jspService()
```

#### 7. Destruction
```
Khi Tomcat shutdown → Gọi jspDestroy()
```

---

## 8.5 Ví dụ JSP Hoàn Chỉnh

```jsp
<%@ page language="java" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.Date, java.text.SimpleDateFormat" %>

<%!
    // Declaration - Biến class
    private int visitCount = 0;
    
    // Declaration - Method
    public String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return sdf.format(date);
    }
%>

<html>
<head>
    <title>JSP Demo</title>
</head>
<body>
    <h1>JSP Basics Demo</h1>
    
    <%-- Scriptlet - Java code --%>
    <% 
        String userName = request.getParameter("name");
        if (userName == null) {
            userName = "Visitor";
        }
        visitCount++;
    %>
    
    <%-- Expression - In giá trị --%>
    <h2>Hello <%= userName %>! 👋</h2>
    
    <%-- Sử dụng method đã khai báo --%>
    <p>Thời gian hiện tại: <%= formatDate(new Date()) %></p>
    
    <%-- In biến class --%>
    <p>Lượt truy cập: <%= visitCount %></p>
    
    <%-- Scriptlet - Loop --%>
    <h3>Danh sách số:</h3>
    <ul>
    <% 
        for (int i = 1; i <= 5; i++) {
            out.println("<li>Số " + i + "</li>");
        }
    %>
    </ul>
</body>
</html>
```

---

## 📌 Tóm tắt Session 8

| Yếu tố | Cú pháp | Mục đích |
|--------|--------|---------|
| Directive | `<%@ ... %>` | Hướng dẫn cho Tomcat |
| Scriptlet | `<% ... %>` | Nhúng Java code |
| Expression | `<%= ... %>` | In giá trị |
| Declaration | `<%! ... %>` | Khai báo biến/method class |

---

---

# 🔧 SESSION 9: Implicit Objects

## 9.1 Implicit Objects là gì?

**Implicit Objects** = Các object được Tomcat tự động cung cấp trong mỗi JSP.

Bạn không cần phải tạo, có sẵn để dùng.

```
┌────────────────────────────────────┐
│     JSP Implicit Objects           │
├────────────────────────────────────┤
│ 1. request                         │
│ 2. response                        │
│ 3. session                         │
│ 4. application                     │
│ 5. out                             │
│ 6. config                          │
│ 7. pageContext                     │
│ 8. page                            │
│ 9. exception                       │
└────────────────────────────────────┘
```

---

## 9.2 Request Object

**Loại:** `javax.servlet.http.HttpServletRequest`

**Mục đích:** Lấy thông tin về HTTP request từ client.

### Các phương thức thường dùng

```jsp
<%
    // Lấy parameter từ form hoặc query string
    String name = request.getParameter("name");
    String[] hobbies = request.getParameterValues("hobby");
    
    // Lấy thông tin request
    String method = request.getMethod();              // GET, POST
    String uri = request.getRequestURI();             // /app/login
    String query = request.getQueryString();          // name=john&age=25
    String host = request.getServerName();            // localhost
    int port = request.getServerPort();               // 8080
    
    // Lấy header
    String userAgent = request.getHeader("User-Agent");
    String acceptLang = request.getHeader("Accept-Language");
    
    // Lấy cookie
    Cookie[] cookies = request.getCookies();
    
    // Lấy Client IP
    String clientIP = request.getRemoteAddr();
    String clientHost = request.getRemoteHost();
    
    // Lấy session
    HttpSession session = request.getSession();
    
    // Lấy session(false) - không tạo mới nếu không có
    HttpSession session2 = request.getSession(false);
%>

<h2>Request Information</h2>
<p>Method: <%= request.getMethod() %></p>
<p>URI: <%= request.getRequestURI() %></p>
<p>Name: <%= request.getParameter("name") %></p>
<p>Client IP: <%= request.getRemoteAddr() %></p>
```

---

## 9.3 Response Object

**Loại:** `javax.servlet.http.HttpServletResponse`

**Mục đích:** Gửi dữ liệu response về cho client.

### Các phương thức thường dùng

```jsp
<%
    // Set content type (cần set ở đầu JSP)
    response.setContentType("text/html; charset=UTF-8");
    
    // Set status
    response.setStatus(HttpServletResponse.SC_OK);
    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
    
    // Gửi error
    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Page not found");
    
    // Redirect
    response.sendRedirect("https://example.com");
    response.sendRedirect("/app/home");
    
    // Set header
    response.setHeader("Content-Disposition", "attachment; filename=file.pdf");
    response.addHeader("Custom-Header", "value");
    
    // Set cookie
    Cookie cookie = new Cookie("username", "john");
    response.addCookie(cookie);
%>

<%
    // Ví dụ: Redirect nếu không có quyền
    if (!isAdmin) {
        response.sendRedirect("/app/unauthorized");
    }
%>
```

---

## 9.4 Session Object

**Loại:** `javax.servlet.http.HttpSession`

**Mục đích:** Lưu thông tin session của client.

### Các phương thức thường dùng

```jsp
<%
    // Lấy hoặc tạo session
    HttpSession sess = request.getSession();
    
    // Set attribute
    sess.setAttribute("username", "john");
    sess.setAttribute("role", "admin");
    
    // Get attribute
    String username = (String) sess.getAttribute("username");
    
    // Xóa specific attribute
    sess.removeAttribute("username");
    
    // Xóa toàn bộ session
    sess.invalidate();
    
    // Set timeout (phút)
    sess.setMaxInactiveInterval(30);
    
    // Lấy session ID
    String sessionID = sess.getId();
    
    // Lấy creation time
    long createdTime = sess.getCreationTime();
%>

<%
    // Ví dụ: Check login
    String user = (String) session.getAttribute("username");
    if (user == null) {
        response.sendRedirect("/app/login");
        return;
    }
%>

<h1>Hello <%= user %></h1>
```

---

## 9.5 Application Object

**Loại:** `javax.servlet.ServletContext`

**Mục đích:** Lưu thông tin toàn ứng dụng (tất cả client).

```
┌──────────────────────────────────┐
│    Application Scope             │
│  (Shared by all clients)         │
│                                  │
│  Client A ←→ Client B ←→ Client C
└──────────────────────────────────┘
```

### Các phương thức thường dùng

```jsp
<%
    // Set attribute (toàn ứng dụng)
    application.setAttribute("appName", "My App");
    application.setAttribute("totalUsers", 100);
    
    // Get attribute
    String appName = (String) application.getAttribute("appName");
    Integer totalUsers = (Integer) application.getAttribute("totalUsers");
    
    // Remove attribute
    application.removeAttribute("totalUsers");
    
    // Lấy real path
    String realPath = application.getRealPath("/uploads/");
    
    // Lấy init parameter
    String dbUrl = application.getInitParameter("db.url");
%>

<%
    // Ví dụ: Đếm lượt truy cập toàn app
    Integer visitCount = (Integer) application.getAttribute("visitCount");
    if (visitCount == null) {
        visitCount = 1;
    } else {
        visitCount++;
    }
    application.setAttribute("visitCount", visitCount);
%>

<p>Tổng lượt truy cập: <%= application.getAttribute("visitCount") %></p>
```

---

## 9.6 Out Object

**Loại:** `javax.servlet.jsp.JspWriter`

**Mục đích:** In dữ liệu ra trang web (tương tự PrintWriter).

### Các phương thức

```jsp
<%
    // Print text
    out.print("Hello");
    out.println("World");
    
    // Print variable
    String name = "Nhân";
    out.print(name);
    
    // Out.println thêm dòng mới
    out.println("<h1>Heading</h1>");
    out.println("<p>Paragraph</p>");
%>

<!-- Tương đương -->
<h1>Heading</h1>
<p>Paragraph</p>

<%
    // Flush output
    out.flush();
    
    // Clear output (xóa buffer)
    out.clearBuffer();
    
    // Get buffer info
    int size = out.getBufferSize();
    int remain = out.getRemaining();
%>
```

---

## 9.7 Config Object

**Loại:** `javax.servlet.ServletConfig`

**Mục đích:** Lấy thông tin config từ web.xml.

```jsp
<%
    // Lấy servlet name
    String servletName = config.getServletName();
    
    // Lấy init parameter từ web.xml
    String dbUrl = config.getInitParameter("database.url");
    
    // Lấy servlet context
    ServletContext context = config.getServletContext();
%>
```

**Ví dụ web.xml:**
```xml
<servlet>
    <servlet-name>MyServlet</servlet-name>
    <jsp-file>/index.jsp</jsp-file>
    <init-param>
        <param-name>database.url</param-name>
        <param-value>jdbc:mysql://localhost:3306/db</param-value>
    </init-param>
</servlet>
```

**Sử dụng trong JSP:**
```jsp
<%
    String dbUrl = config.getInitParameter("database.url");
%>
<p>Database URL: <%= dbUrl %></p>
```

---

## 9.8 PageContext Object

**Loại:** `javax.servlet.jsp.PageContext`

**Mục đích:** Truy cập tất cả implicit object và scope.

```jsp
<%
    // Page scope
    pageContext.setAttribute("name", "Nhân", PageContext.PAGE_SCOPE);
    
    // Request scope
    pageContext.setAttribute("email", "nhan@example.com", PageContext.REQUEST_SCOPE);
    
    // Session scope
    pageContext.setAttribute("user", "admin", PageContext.SESSION_SCOPE);
    
    // Application scope
    pageContext.setAttribute("appName", "MyApp", PageContext.APPLICATION_SCOPE);
    
    // Get attribute từ không gian nào đó
    String name = (String) pageContext.getAttribute("name");
    
    // Lấy request/response
    HttpServletRequest req = (HttpServletRequest) pageContext.getRequest();
    HttpServletResponse resp = (HttpServletResponse) pageContext.getResponse();
    
    // Forward hoặc include
    pageContext.forward("other.jsp");
    pageContext.include("header.jsp");
%>
```

---

## 9.9 Bảng Tóm Tắt Implicit Objects

| Object | Loại | Scope | Mục đích |
|--------|------|-------|---------|
| request | HttpServletRequest | Request | Lấy info request |
| response | HttpServletResponse | Page | Gửi response |
| session | HttpSession | Session | Lưu dữ liệu session |
| application | ServletContext | Application | Lưu dữ liệu toàn app |
| out | JspWriter | Page | In dữ liệu ra trang |
| config | ServletConfig | Page | Lấy config từ web.xml |
| pageContext | PageContext | Page | Truy cập toàn bộ scope |

---

## 📌 Tóm tắt Session 9

Sử dụng Implicit Objects để:
- ✅ Lấy request parameter (request)
- ✅ Lưu session data (session)
- ✅ Chia sẻ data toàn app (application)
- ✅ In output ra trang (out)
- ✅ Điều hướng (response)

---

---

# 📄 SESSION 10: JSP Include

## 10.1 Tại sao cần Include?

**Tình huống thực tế:**

```
┌──────────────────────────────────┐
│     Website Layout               │
├──────────────────────────────────┤
│         HEADER (chung)           │
├──────────────────────────────────┤
│                                  │
│  CONTENT (khác nhau)             │
│                                  │
├──────────────────────────────────┤
│         FOOTER (chung)           │
└──────────────────────────────────┘
```

Nếu không dùng include, phải code header/footer ở mỗi JSP → lặp code!

**Giải pháp: Include file chứa header/footer**

---

## 10.2 Page Directive Include

**Cú pháp:**
```jsp
<%@ include file="path/to/file.jsp" %>
```

**Tính chất:**
- Nhúng file lúc **biên dịch** (translation time)
- Kết hợp 2 file thành 1 servlet
- Chia sẻ biến giữa file
- Nhanh hơn (chỉ biên dịch 1 lần)

### Ví dụ

**header.jsp:**
```jsp
<%-- header.jsp --%>
<div class="header" style="background-color: #333; color: white; padding: 20px; text-align: center;">
    <h1>🌐 My Website</h1>
    <nav>
        <a href="/app/home" style="color: white; margin: 0 10px;">Home</a>
        <a href="/app/about" style="color: white; margin: 0 10px;">About</a>
        <a href="/app/contact" style="color: white; margin: 0 10px;">Contact</a>
    </nav>
</div>
```

**footer.jsp:**
```jsp
<%-- footer.jsp --%>
<div class="footer" style="background-color: #333; color: white; padding: 20px; text-align: center;">
    <p>&copy; 2025 My Website. All rights reserved.</p>
    <p>Contact: info@example.com</p>
</div>
```

**index.jsp (trang chủ):**
```jsp
<%@ include file="header.jsp" %>

<div class="content" style="padding: 20px;">
    <h2>Welcome to My Website!</h2>
    <p>This is the home page content.</p>
</div>

<%@ include file="footer.jsp" %>
```

**about.jsp (trang riêng):**
```jsp
<%@ include file="header.jsp" %>

<div class="content" style="padding: 20px;">
    <h2>About Us</h2>
    <p>This is the about page content.</p>
</div>

<%@ include file="footer.jsp" %>
```

### Quy trình Biên dịch

```
index.jsp            header.jsp          footer.jsp
    ↓                   ↓                    ↓
    └───────────────────┴────────────────────┘
                    ↓
        Tomcat kết hợp 3 file
                    ↓
        index_jsp.java (1 file)
                    ↓
            Biên dịch → .class
                    ↓
               Chạy servlet
```

---

## 10.3 JSP Action Include

**Cú pháp:**
```jsp
<jsp:include page="path/to/file.jsp" />
```

**Tính chất:**
- Nhúng file lúc **chạy** (runtime)
- Mỗi file là servlet riêng
- Không chia sẻ biến trực tiếp
- Chậm hơn (nhưng linh hoạt hơn)

### Ví dụ

```jsp
<%@ page contentType="text/html; charset=UTF-8" %>

<html>
<head><title>Dynamic Page</title></head>
<body>
    <jsp:include page="header.jsp" />
    
    <div class="content" style="padding: 20px;">
        <h2>Welcome!</h2>
        <p>Content here</p>
    </div>
    
    <jsp:include page="footer.jsp" />
</body>
</html>
```

### Truyền Parameter qua Include

```jsp
<%-- main.jsp --%>
<jsp:include page="user_card.jsp">
    <jsp:param name="username" value="john" />
    <jsp:param name="role" value="admin" />
</jsp:include>

<%-- user_card.jsp --%>
<%
    String username = request.getParameter("username");
    String role = request.getParameter("role");
%>
<div>
    <p>Username: <%= username %></p>
    <p>Role: <%= role %></p>
</div>
```

---

## 10.4 So sánh Include

| Yếu tố | Page Directive | JSP Action |
|--------|----------------|-----------|
| Cú pháp | `<%@ include ... %>` | `<jsp:include ... />` |
| Lúc nào | Biên dịch (Translation) | Chạy (Runtime) |
| File sinh ra | 1 servlet | Nhiều servlet |
| Chia sẻ biến | Có (cùng servlet) | Không (servlet riêng) |
| Truyền parameter | Không | Có |
| Tốc độ | Nhanh | Chậm hơn |
| Linh hoạt | Ít | Nhiều |
| Dùng khi | Header/Footer cố định | Content động |

---

## 10.5 Ví dụ Thực Tế: Website Layout

**Cấu trúc:**
```
webapp/
├─ header.jsp
├─ footer.jsp
├─ sidebar.jsp
├─ index.jsp
├─ products.jsp
└─ about.jsp
```

**header.jsp:**
```jsp
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<div style="background-color: #2c3e50; color: white; padding: 20px; margin-bottom: 20px;">
    <h1>📚 Learning Portal</h1>
    <nav>
        <a href="index.jsp" style="color: white; margin-right: 20px; text-decoration: none;">Home</a>
        <a href="products.jsp" style="color: white; margin-right: 20px; text-decoration: none;">Products</a>
        <a href="about.jsp" style="color: white; text-decoration: none;">About</a>
    </nav>
</div>
```

**index.jsp:**
```jsp
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<!DOCTYPE html>
<html>
<head>
    <title>Home</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 0; }
        .container { max-width: 1200px; margin: 0 auto; }
        .main-content { display: flex; }
        .content { flex: 1; padding: 20px; }
        .sidebar { width: 250px; background-color: #ecf0f1; padding: 20px; }
    </style>
</head>
<body>
    <div class="container">
        <%@ include file="header.jsp" %>
        
        <div class="main-content">
            <div class="content">
                <h2>Welcome to Home Page</h2>
                <p>This is the main content area.</p>
            </div>
            
            <div class="sidebar">
                <h3>Sidebar</h3>
                <ul>
                    <li><a href="#">Link 1</a></li>
                    <li><a href="#">Link 2</a></li>
                    <li><a href="#">Link 3</a></li>
                </ul>
            </div>
        </div>
        
        <%@ include file="footer.jsp" %>
    </div>
</body>
</html>
```

**footer.jsp:**
```jsp
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<div style="background-color: #2c3e50; color: white; padding: 20px; text-align: center; margin-top: 20px;">
    <p>&copy; 2025 Learning Portal. All rights reserved.</p>
</div>
```

---

## 📌 Tóm tắt Session 10

| Tính năng | Page Include | JSP Include |
|-----------|-------------|------------|
| Dùng | Header/Footer cố định | Content động |
| Cú pháp | `<%@ include file="..." %>` | `<jsp:include page="..." />` |
| Thời điểm | Biên dịch | Runtime |
| Tốc độ | Nhanh | Chậm |

---

---

# 🎯 SESSION 11: JSP Expression Language (EL)

## 11.1 EL là gì?

**EL** = **E**xpression **L**anguage

**Mục đích:** Cung cấp cách đơn giản hơn để truy cập dữ liệu trong JSP.

**So sánh:**

```jsp
<!-- Scriptlet (cũ, phức tạp) -->
<% String name = (String) session.getAttribute("username"); %>
<h1>Hello <%= name %></h1>

<!-- EL (mới, đơn giản) -->
<h1>Hello ${sessionScope.username}</h1>
```

---

## 11.2 EL Scopes

Dữ liệu có thể lưu ở 4 scope:

### 1. **pageScope** - Trang hiện tại

```jsp
<% pageContext.setAttribute("message", "Page message"); %>

${pageScope.message}  <!-- In: Page message -->
```

### 2. **requestScope** - Request hiện tại

```jsp
<% request.setAttribute("user", "john"); %>

${requestScope.user}  <!-- In: john -->
```

### 3. **sessionScope** - Session hiện tại

```jsp
<% session.setAttribute("username", "admin"); %>

${sessionScope.username}  <!-- In: admin -->
```

### 4. **applicationScope** - Toàn ứng dụng

```jsp
<% application.setAttribute("appName", "My App"); %>

${applicationScope.appName}  <!-- In: My App -->
```

---

## 11.3 EL Automatic Lookup

**Nếu không chỉ scope, EL sẽ tìm tự động:**

```
pageScope → requestScope → sessionScope → applicationScope
```

**Ví dụ:**
```jsp
<% pageContext.setAttribute("name", "Page"); %>
<% request.setAttribute("name", "Request"); %>
<% session.setAttribute("name", "Session"); %>

${ name }  <!-- Tìm từ pageScope trước → In: Page -->
```

---

## 11.4 Truy cập Object Properties

### Dạng Dot Notation

```jsp
<!-- Truy cập property của object -->
${ user.name }
${ user.email }
${ user.address.city }

<!-- Tương đương -->
user.getName()
user.getEmail()
user.getAddress().getCity()
```

### Dạng Bracket Notation

```jsp
<!-- Truy cập array hoặc map -->
${ array[0] }
${ map['key'] }
${ map["key"] }

<!-- Tương đương -->
array[0]
map.get("key")
```

### Ví dụ thực tế

```jsp
<%
    class User {
        public String name = "Nhân";
        public int age = 25;
        public String getEmail() { return "nhan@example.com"; }
    }
    
    User user = new User();
    request.setAttribute("user", user);
    
    List<String> hobbies = Arrays.asList("Reading", "Gaming", "Coding");
    request.setAttribute("hobbies", hobbies);
%>

<p>Name: ${ user.name }</p>
<p>Age: ${ user.age }</p>
<p>Email: ${ user.email }</p>
<p>First hobby: ${ hobbies[0] }</p>
<p>Second hobby: ${ hobbies[1] }</p>
```

---

## 11.5 Request Parameters và Headers

### Lấy Request Parameter

```jsp
<!-- request.getParameter("name") -->
<p>Name: ${ param.name }</p>

<!-- request.getParameterValues("hobby") -->
<p>Hobbies: ${ paramValues.hobby[0] }, ${ paramValues.hobby[1] }</p>
```

**Ví dụ:**
```
URL: http://localhost:8080/app?name=john&age=25&hobby=reading&hobby=coding

<p>Name: ${ param.name }</p>           <!-- In: john -->
<p>Age: ${ param.age }</p>             <!-- In: 25 -->
<p>Hobby 1: ${ paramValues.hobby[0] }</p>  <!-- In: reading -->
<p>Hobby 2: ${ paramValues.hobby[1] }</p>  <!-- In: coding -->
```

### Lấy Request Header

```jsp
<!-- request.getHeader("User-Agent") -->
<p>User Agent: ${ header['User-Agent'] }</p>

<!-- request.getHeaderValues("Accept-Language") -->
<p>Languages: ${ headerValues['Accept-Language'][0] }</p>
```

---

## 11.6 EL Operators

### Arithmetic Operators

```jsp
${ 5 + 3 }              <!-- 8 -->
${ 10 - 2 }             <!-- 8 -->
${ 4 * 3 }              <!-- 12 -->
${ 12 / 3 }             <!-- 4 -->
${ 12 / 5 }             <!-- 2.4 -->
${ 12 % 5 }             <!-- 2 -->
```

### Comparison Operators

```jsp
${ 5 == 5 }             <!-- true -->
${ 5 != 3 }             <!-- true -->
${ 10 > 5 }             <!-- true -->
${ 5 < 10 }             <!-- true -->
${ 10 >= 5 }            <!-- true -->
${ 5 <= 10 }            <!-- true -->
```

### Logical Operators

```jsp
${ true && true }       <!-- true -->
${ true || false }      <!-- true -->
${ !true }              <!-- false -->
${ (5 > 3) && (3 > 1) } <!-- true -->
```

### Empty Operator

```jsp
${ empty user }         <!-- true if user is null/empty -->
${ not empty user }     <!-- false if user is null/empty -->
```

### Ternary Operator

```jsp
${ age > 18 ? "Adult" : "Minor" }
${ status == "active" ? "Active" : "Inactive" }
```

---

## 11.7 EL Functions

```jsp
<!-- Length -->
${ fn:length("Hello") }         <!-- 5 -->
${ fn:length(list) }            <!-- Size of list -->

<!-- Substring -->
${ fn:substring("Hello", 0, 3) } <!-- Hel -->

<!-- Uppercase/Lowercase -->
${ fn:toUpperCase("hello") }    <!-- HELLO -->
${ fn:toLowerCase("HELLO") }    <!-- hello -->

<!-- Contains -->
${ fn:contains("Hello World", "World") }  <!-- true -->

<!-- Split -->
${ fn:split("a,b,c", ",") }     <!-- Array [a, b, c] -->

<!-- Join -->
${ fn:join(array, ",") }        <!-- a,b,c -->
```

**Cần import JSTL:**
```jsp
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
```

---

## 11.8 Ví dụ Thực Tế EL

```jsp
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%
    // Set dữ liệu
    session.setAttribute("username", "Nhân");
    request.setAttribute("age", 25);
    request.setAttribute("email", "nhan@example.com");
    request.setAttribute("message", "   Xin chào   ");
%>

<!DOCTYPE html>
<html>
<head><title>EL Demo</title></head>
<body>
    <h1>Expression Language Examples</h1>
    
    <!-- Simple property access -->
    <p>Username (session): ${ sessionScope.username }</p>
    <p>Age (request): ${ requestScope.age }</p>
    <p>Email: ${ email }</p>
    
    <!-- Arithmetic -->
    <p>Next year age: ${ age + 1 }</p>
    <p>Age doubled: ${ age * 2 }</p>
    
    <!-- Comparison -->
    <p>Is adult? ${ age >= 18 ? "Yes" : "No" }</p>
    
    <!-- String functions -->
    <p>Email uppercase: ${ fn:toUpperCase(email) }</p>
    <p>Message length: ${ fn:length(fn:trim(message)) }</p>
    <p>Email contains '@'? ${ fn:contains(email, "@") ? "Yes" : "No" }</p>
    
    <!-- Request parameter -->
    <p>Search keyword: ${ param.q }</p>
    <p>Is search empty? ${ empty param.q ? "Yes" : "No" }</p>
</body>
</html>
```

---

## 📌 Tóm tắt Session 11

| Yếu tố | EL | Scriptlet |
|--------|-----|----------|
| Cú pháp | `${ expression }` | `<% code %>` |
| Đơn giản | Có | Không |
| Readability | Cao | Thấp |
| Dùng | Hiển thị dữ liệu | Logic phức tạp |

---

---

# 🏷️ SESSION 12: JSTL Basics

## 12.1 JSTL là gì?

**JSTL** = **J**ava**S**erver **P**ages **S**tandard **T**ag **L**ibrary

**Mục đích:** Cung cấp các tag tiêu chuẩn để thay thế Scriptlet, code JSP sạch hơn.

```
Trước:  <% for (int i = 0; i < 5; i++) { %> ... <% } %>
Sau:    <c:forEach var="i" begin="0" end="4"> ... </c:forEach>
```

---

## 12.2 Tài nguyên JSTL

**Thư mục Core JSTL (phổ biến nhất):**

```jsp
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
```

**JAR file cần:**
- `jstl-api-1.2.jar`
- `jstl-impl-1.2.jar`

Hoặc: `javax.servlet.jsp.jstl-1.2.x.jar`

---

## 12.3 Core Tags

### 1. `<c:set>` - Gán giá trị

```jsp
<%-- Gán vào page scope --%>
<c:set var="name" value="Nhân" />

<%-- Gán vào object property --%>
<c:set target="${ user }" property="age" value="25" />

<%-- Gán biểu thức --%>
<c:set var="total" value="${ 5 + 3 }" />
```

### 2. `<c:if>` - Điều kiện

```jsp
<c:if test="${ age >= 18 }">
    <p>You are an adult</p>
</c:if>

<c:if test="${ not empty username }">
    <p>Hello ${ username }</p>
</c:if>
```

### 3. `<c:choose>`, `<c:when>`, `<c:otherwise>` - Switch/Case

```jsp
<c:choose>
    <c:when test="${ role == 'admin' }">
        <p>Welcome Admin</p>
    </c:when>
    <c:when test="${ role == 'user' }">
        <p>Welcome User</p>
    </c:when>
    <c:otherwise>
        <p>Unknown role</p>
    </c:otherwise>
</c:choose>
```

### 4. `<c:forEach>` - Loop

```jsp
<%-- Loop từ 1 đến 5 --%>
<c:forEach var="i" begin="1" end="5">
    <p>Number: ${ i }</p>
</c:forEach>

<%-- Loop với step --%>
<c:forEach var="i" begin="0" end="10" step="2">
    <p>${ i }</p>  <!-- 0, 2, 4, 6, 8, 10 -->
</c:forEach>

<%-- Loop qua collection --%>
<c:forEach var="item" items="${ items }">
    <p>${ item }</p>
</c:forEach>

<%-- Loop qua map --%>
<c:forEach var="entry" items="${ map }">
    <p>Key: ${ entry.key }, Value: ${ entry.value }</p>
</c:forEach>

<%-- Loop với status --%>
<c:forEach var="item" items="${ items }" varStatus="status">
    <p>
        Item #${ status.count }: ${ item }
        (First: ${ status.first }, Last: ${ status.last })
    </p>
</c:forEach>
```

### 5. `<c:redirect>` - Chuyển hướng

```jsp
<c:redirect url="https://example.com" />
<c:redirect url="/app/home" />
```

### 6. `<c:url>` - Tạo URL

```jsp
<c:url var="loginUrl" value="/app/login">
    <c:param name="username" value="john" />
    <c:param name="returnUrl" value="/home" />
</c:url>

<a href="${ loginUrl }">Login</a>
<!-- Sinh ra: /app/login?username=john&returnUrl=%2Fhome -->
```

### 7. `<c:out>` - In giá trị (với escape HTML)

```jsp
<!-- Escape HTML - an toàn -->
<c:out value="${ userInput }" default="No value" />

<!-- Không escape -->
<c:out value="${ userInput }" escapeXml="false" />
```

---

## 12.4 Ví dụ Thực Tế: Danh sách sản phẩm

```jsp
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%
    // Tạo dữ liệu
    java.util.List<String> products = java.util.Arrays.asList(
        "Laptop", "Mouse", "Keyboard", "Monitor", "Headphones"
    );
    request.setAttribute("products", products);
    
    java.util.Map<String, Integer> prices = new java.util.HashMap<>();
    prices.put("Laptop", 15000000);
    prices.put("Mouse", 500000);
    prices.put("Keyboard", 1000000);
    prices.put("Monitor", 5000000);
    prices.put("Headphones", 2000000);
    request.setAttribute("prices", prices);
%>

<!DOCTYPE html>
<html>
<head>
    <title>Products</title>
    <style>
        table { border-collapse: collapse; width: 100%; }
        th, td { border: 1px solid #ddd; padding: 10px; text-align: left; }
        th { background-color: #4CAF50; color: white; }
    </style>
</head>
<body>
    <h1>📦 Product List</h1>
    
    <table>
        <thead>
            <tr>
                <th>No.</th>
                <th>Product Name</th>
                <th>Price (VND)</th>
                <th>Action</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="product" items="${ products }" varStatus="status">
                <tr>
                    <td>${ status.count }</td>
                    <td>${ product }</td>
                    <td>${ prices[product] }</td>
                    <td>
                        <c:choose>
                            <c:when test="${ prices[product] > 10000000 }">
                                <span style="color: red; font-weight: bold;">Expensive</span>
                            </c:when>
                            <c:when test="${ prices[product] > 1000000 }">
                                <span style="color: orange;">Medium</span>
                            </c:when>
                            <c:otherwise>
                                <span style="color: green;">Cheap</span>
                            </c:otherwise>
                        </c:choose>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
    
    <p>Total products: <strong><c:out value="${ products.size() }" default="0" /></strong></p>
</body>
</html>
```

---

## 12.5 So sánh: Scriptlet vs JSTL

```jsp
<!-- SCRIPTLET - Code lộn xộn -->
<%
    List<String> items = (List<String>) request.getAttribute("items");
    if (items != null && items.size() > 0) {
        for (String item : items) {
            out.println("<li>" + item + "</li>");
        }
    } else {
        out.println("<p>No items</p>");
    }
%>

<!-- JSTL - Code sạch -->
<c:choose>
    <c:when test="${ not empty items }">
        <c:forEach var="item" items="${ items }">
            <li>${ item }</li>
        </c:forEach>
    </c:when>
    <c:otherwise>
        <p>No items</p>
    </c:otherwise>
</c:choose>
```

---

## 📌 Tóm tắt Session 12

| Tag | Mục đích |
|-----|---------|
| `<c:set>` | Gán giá trị |
| `<c:if>` | Điều kiện if |
| `<c:choose>` | Switch/case |
| `<c:forEach>` | Loop |
| `<c:redirect>` | Redirect |
| `<c:url>` | Tạo URL |
| `<c:out>` | In giá trị |

---

---

# 📝 SESSION 13: Form Handling

## 13.1 Quy trình Form Handling

```
HTML Form (index.jsp)
       ↓ (User submits)
Form POST request
       ↓ (Tomcat receives)
Servlet xử lý (Controller)
       ↓ (Get parameters)
Logic xử lý
       ↓ (Set attributes)
JSP (View) hiển thị
       ↓
Response gửi về Browser
```

---

## 13.2 HTML Form

```html
<form method="POST" action="register">
    <label>Name:</label>
    <input type="text" name="name" required />
    
    <label>Email:</label>
    <input type="email" name="email" required />
    
    <label>Age:</label>
    <input type="number" name="age" />
    
    <label>Country:</label>
    <select name="country">
        <option value="">-- Select --</option>
        <option value="Vietnam">Vietnam</option>
        <option value="USA">USA</option>
    </select>
    
    <label>Interests:</label>
    <input type="checkbox" name="interest" value="Sports" /> Sports
    <input type="checkbox" name="interest" value="Music" /> Music
    <input type="checkbox" name="interest" value="Reading" /> Reading
    
    <button type="submit">Register</button>
</form>
```

---

## 13.3 Servlet xử lý Form

```java
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    
    @Override
    protected void doPost(HttpServletRequest request, 
                        HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 1. Lấy form data
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String ageStr = request.getParameter("age");
        String country = request.getParameter("country");
        String[] interests = request.getParameterValues("interest");
        
        // 2. Validate
        if (name == null || name.isEmpty()) {
            request.setAttribute("error", "Name is required");
            request.getRequestDispatcher("register.jsp").forward(request, response);
            return;
        }
        
        if (email == null || !email.contains("@")) {
            request.setAttribute("error", "Invalid email");
            request.getRequestDispatcher("register.jsp").forward(request, response);
            return;
        }
        
        int age = 0;
        if (ageStr != null && !ageStr.isEmpty()) {
            try {
                age = Integer.parseInt(ageStr);
            } catch (NumberFormatException e) {
                request.setAttribute("error", "Invalid age");
                request.getRequestDispatcher("register.jsp").forward(request, response);
                return;
            }
        }
        
        // 3. Xử lý logic (lưu DB, gửi email, v.v.)
        // ... code lưu DB ...
        
        // 4. Set attributes cho JSP
        request.setAttribute("success", true);
        request.setAttribute("name", name);
        request.setAttribute("email", email);
        request.setAttribute("age", age);
        request.setAttribute("country", country);
        request.setAttribute("interests", interests);
        
        // 5. Forward đến JSP
        request.getRequestDispatcher("result.jsp").forward(request, response);
    }
}
```

---

## 13.4 JSP Form (index.jsp)

```jsp
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<!DOCTYPE html>
<html>
<head>
    <title>Registration Form</title>
    <style>
        body { font-family: Arial; max-width: 600px; margin: 50px auto; }
        .form-group { margin: 15px 0; }
        label { display: block; margin-bottom: 5px; font-weight: bold; }
        input[type="text"],
        input[type="email"],
        input[type="number"],
        select { width: 100%; padding: 8px; }
        button { padding: 10px 20px; background-color: #4CAF50; color: white; border: none; }
        .error { color: red; }
    </style>
</head>
<body>
    <h1>Registration Form</h1>
    
    <% if (request.getAttribute("error") != null) { %>
        <p class="error">Error: <%= request.getAttribute("error") %></p>
    <% } %>
    
    <form method="POST" action="register">
        <div class="form-group">
            <label>Name:</label>
            <input type="text" name="name" required />
        </div>
        
        <div class="form-group">
            <label>Email:</label>
            <input type="email" name="email" required />
        </div>
        
        <div class="form-group">
            <label>Age:</label>
            <input type="number" name="age" />
        </div>
        
        <div class="form-group">
            <label>Country:</label>
            <select name="country">
                <option value="">-- Select Country --</option>
                <option value="Vietnam">Vietnam</option>
                <option value="Thailand">Thailand</option>
                <option value="USA">USA</option>
            </select>
        </div>
        
        <div class="form-group">
            <label>Interests:</label>
            <input type="checkbox" name="interest" value="Sports" /> Sports
            <input type="checkbox" name="interest" value="Music" /> Music
            <input type="checkbox" name="interest" value="Reading" /> Reading
        </div>
        
        <button type="submit">Register</button>
    </form>
</body>
</html>
```

---

## 13.5 JSP Result (result.jsp)

```jsp
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
    <title>Registration Result</title>
    <style>
        body { font-family: Arial; max-width: 600px; margin: 50px auto; }
        .success { background-color: #dff0d8; padding: 15px; border-radius: 5px; }
        .info { background-color: #d9edf7; padding: 10px; margin: 10px 0; }
        a { color: blue; text-decoration: none; }
    </style>
</head>
<body>
    <h1>Registration Successful! ✅</h1>
    
    <div class="success">
        <h2>Thank you for registering!</h2>
        
        <div class="info">
            <strong>Name:</strong> ${ name }
        </div>
        
        <div class="info">
            <strong>Email:</strong> ${ email }
        </div>
        
        <c:if test="${ age > 0 }">
            <div class="info">
                <strong>Age:</strong> ${ age }
            </div>
        </c:if>
        
        <c:if test="${ not empty country }">
            <div class="info">
                <strong>Country:</strong> ${ country }
            </div>
        </c:if>
        
        <c:if test="${ not empty interests }">
            <div class="info">
                <strong>Interests:</strong>
                <c:forEach var="interest" items="${ interests }">
                    <span style="background-color: #f0f0f0; padding: 5px; margin: 2px;">${ interest }</span>
                </c:forEach>
            </div>
        </c:if>
        
        <p>
            <a href="index.jsp">← Back to Form</a>
        </p>
    </div>
</body>
</html>
```

---

## 13.6 MVC Flow Đầy Đủ

```
User fills form
       ↓
Submit POST
       ↓
[Servlet - Controller]
├─ Lấy parameters (Model)
├─ Validate dữ liệu
├─ Xử lý logic (business logic)
├─ Lưu dữ liệu
└─ Set attributes
       ↓ Forward
[JSP - View]
├─ Nhận dữ liệu từ request
├─ Hiển thị kết quả
└─ Gửi HTML
       ↓
Browser nhận HTML
       ↓
User thấy result
```

---

## 📌 Tóm tắt Session 13

Quy trình Form Handling:
1. ✅ Tạo HTML Form
2. ✅ Submit POST request
3. ✅ Servlet lấy parameters
4. ✅ Validate dữ liệu
5. ✅ Xử lý logic
6. ✅ Set attributes
7. ✅ Forward đến JSP
8. ✅ JSP hiển thị kết quả

---

---

# 🎓 SESSION 14: Mini Project - Simple Form Submission App

## 14.1 Yêu cầu Dự Án

Tạo ứng dụng **Student Registration System** với:

1. **Trang chủ** - Form đăng ký sinh viên
2. **Validation** - Server-side validation
3. **Processing** - Servlet xử lý
4. **Result** - Hiển thị kết quả
5. **Database** - Lưu vào file (text)

---

## 14.2 Cấu trúc Project

```
StudentRegistration/
├─ src/main/java/
│  └─ com/example/servlets/
│     └─ RegistrationServlet.java
├─ src/main/webapp/
│  ├─ WEB-INF/
│  │  └─ web.xml
│  ├─ index.jsp         (Form)
│  ├─ result.jsp        (Kết quả)
│  └─ error.jsp         (Lỗi)
└─ build/
   └─ StudentRegistration.war
```

---

## 14.3 Code - RegistrationServlet.java

```java
// src/main/java/com/example/servlets/RegistrationServlet.java
package com.example.servlets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@WebServlet("/register")
public class RegistrationServlet extends HttpServlet {
    
    @Override
    protected void doPost(HttpServletRequest request, 
                        HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 1. Lấy dữ liệu từ form
        String fullName = request.getParameter("fullName");
        String studentId = request.getParameter("studentId");
        String emailStr = request.getParameter("email");
        String phoneStr = request.getParameter("phone");
        String majorStr = request.getParameter("major");
        String yearStr = request.getParameter("year");
        
        // 2. Trim whitespace
        if (fullName != null) fullName = fullName.trim();
        if (studentId != null) studentId = studentId.trim();
        if (emailStr != null) emailStr = emailStr.trim();
        if (phoneStr != null) phoneStr = phoneStr.trim();
        
        // 3. Validate dữ liệu
        String error = validateInput(fullName, studentId, emailStr, phoneStr, majorStr, yearStr);
        
        if (error != null) {
            // Có lỗi → quay lại form
            request.setAttribute("error", error);
            request.setAttribute("fullName", fullName);
            request.setAttribute("studentId", studentId);
            request.setAttribute("email", emailStr);
            request.setAttribute("phone", phoneStr);
            request.getRequestDispatcher("index.jsp").forward(request, response);
            return;
        }
        
        // 4. Xử lý logic (lưu vào file)
        try {
            saveToFile(fullName, studentId, emailStr, phoneStr, majorStr, yearStr);
            
            // 5. Set attributes cho JSP
            request.setAttribute("success", true);
            request.setAttribute("fullName", fullName);
            request.setAttribute("studentId", studentId);
            request.setAttribute("email", emailStr);
            request.setAttribute("phone", phoneStr);
            request.setAttribute("major", majorStr);
            request.setAttribute("year", yearStr);
            request.setAttribute("registrationTime", LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
            
            // 6. Forward đến result JSP
            request.getRequestDispatcher("result.jsp").forward(request, response);
            
        } catch (Exception e) {
            request.setAttribute("error", "System error: " + e.getMessage());
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }
    }
    
    /**
     * Validate input
     */
    private String validateInput(String fullName, String studentId, String email, 
                                String phone, String major, String year) {
        
        // Kiểm tra tên
        if (fullName == null || fullName.isEmpty()) {
            return "Full name is required";
        }
        if (fullName.length() < 3) {
            return "Full name must be at least 3 characters";
        }
        if (fullName.length() > 50) {
            return "Full name must be less than 50 characters";
        }
        
        // Kiểm tra student ID
        if (studentId == null || studentId.isEmpty()) {
            return "Student ID is required";
        }
        if (!studentId.matches("^[0-9]{8}$")) {
            return "Student ID must be 8 digits";
        }
        
        // Kiểm tra email
        if (email == null || email.isEmpty()) {
            return "Email is required";
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            return "Invalid email format";
        }
        
        // Kiểm tra phone
        if (phone == null || phone.isEmpty()) {
            return "Phone is required";
        }
        if (!phone.matches("^[0-9]{10,11}$")) {
            return "Phone must be 10-11 digits";
        }
        
        // Kiểm tra major
        if (major == null || major.isEmpty()) {
            return "Please select a major";
        }
        
        // Kiểm tra year
        if (year == null || year.isEmpty()) {
            return "Please select graduation year";
        }
        
        return null; // Không có lỗi
    }
    
    /**
     * Lưu dữ liệu vào file
     */
    private void saveToFile(String fullName, String studentId, String email, 
                           String phone, String major, String year) throws IOException {
        
        String logDir = getServletContext().getRealPath("/logs/");
        File dir = new File(logDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
        String filename = logDir + "registration_" + timestamp + ".txt";
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("=== Student Registration ===");
            writer.println("Date: " + LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
            writer.println();
            writer.println("Full Name: " + fullName);
            writer.println("Student ID: " + studentId);
            writer.println("Email: " + email);
            writer.println("Phone: " + phone);
            writer.println("Major: " + major);
            writer.println("Graduation Year: " + year);
            writer.println();
            writer.println("Status: Successfully registered");
        }
        
        System.out.println("Registration saved to: " + filename);
    }
    
    @Override
    protected void doGet(HttpServletRequest request, 
                       HttpServletResponse response) 
            throws ServletException, IOException {
        // Redirect GET requests to form
        response.sendRedirect("index.jsp");
    }
}
```

---

## 14.4 Code - index.jsp (Form)

```jsp
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Student Registration</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
            padding: 20px;
        }
        
        .container {
            background: white;
            border-radius: 10px;
            box-shadow: 0 10px 40px rgba(0,0,0,0.2);
            width: 100%;
            max-width: 500px;
            padding: 40px;
        }
        
        h1 {
            color: #333;
            margin-bottom: 30px;
            text-align: center;
            font-size: 28px;
        }
        
        .error-message {
            background-color: #f8d7da;
            border: 1px solid #f5c6cb;
            color: #721c24;
            padding: 12px;
            border-radius: 5px;
            margin-bottom: 20px;
        }
        
        .form-group {
            margin-bottom: 20px;
        }
        
        label {
            display: block;
            margin-bottom: 8px;
            color: #333;
            font-weight: 600;
            font-size: 14px;
        }
        
        input[type="text"],
        input[type="email"],
        input[type="tel"],
        select {
            width: 100%;
            padding: 12px;
            border: 2px solid #e0e0e0;
            border-radius: 5px;
            font-size: 14px;
            transition: border-color 0.3s;
            font-family: Arial, sans-serif;
        }
        
        input[type="text"]:focus,
        input[type="email"]:focus,
        input[type="tel"]:focus,
        select:focus {
            outline: none;
            border-color: #667eea;
            box-shadow: 0 0 5px rgba(102, 126, 234, 0.3);
        }
        
        button {
            width: 100%;
            padding: 14px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            border: none;
            border-radius: 5px;
            font-size: 16px;
            font-weight: 600;
            cursor: pointer;
            transition: transform 0.2s, box-shadow 0.2s;
        }
        
        button:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 20px rgba(102, 126, 234, 0.4);
        }
        
        button:active {
            transform: translateY(0);
        }
        
        .help-text {
            font-size: 12px;
            color: #666;
            margin-top: 5px;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>📚 Student Registration</h1>
        
        <% if (request.getAttribute("error") != null) { %>
            <div class="error-message">
                <strong>❌ Error:</strong> <%= request.getAttribute("error") %>
            </div>
        <% } %>
        
        <form method="POST" action="register">
            <!-- Full Name -->
            <div class="form-group">
                <label for="fullName">Full Name *</label>
                <input 
                    type="text" 
                    id="fullName" 
                    name="fullName" 
                    placeholder="E.g., Nguyễn Văn Nhân"
                    value="<%= request.getAttribute("fullName") != null ? request.getAttribute("fullName") : "" %>"
                    required />
                <div class="help-text">Minimum 3 characters, maximum 50 characters</div>
            </div>
            
            <!-- Student ID -->
            <div class="form-group">
                <label for="studentId">Student ID *</label>
                <input 
                    type="text" 
                    id="studentId" 
                    name="studentId" 
                    placeholder="E.g., 12345678"
                    value="<%= request.getAttribute("studentId") != null ? request.getAttribute("studentId") : "" %>"
                    required />
                <div class="help-text">8 digits only</div>
            </div>
            
            <!-- Email -->
            <div class="form-group">
                <label for="email">Email *</label>
                <input 
                    type="email" 
                    id="email" 
                    name="email" 
                    placeholder="your.email@example.com"
                    value="<%= request.getAttribute("email") != null ? request.getAttribute("email") : "" %>"
                    required />
            </div>
            
            <!-- Phone -->
            <div class="form-group">
                <label for="phone">Phone Number *</label>
                <input 
                    type="tel" 
                    id="phone" 
                    name="phone" 
                    placeholder="09XXXXXXXXX"
                    value="<%= request.getAttribute("phone") != null ? request.getAttribute("phone") : "" %>"
                    required />
                <div class="help-text">10-11 digits</div>
            </div>
            
            <!-- Major -->
            <div class="form-group">
                <label for="major">Major *</label>
                <select id="major" name="major" required>
                    <option value="">-- Select Major --</option>
                    <option value="Computer Science">Computer Science</option>
                    <option value="Software Engineering">Software Engineering</option>
                    <option value="Information Technology">Information Technology</option>
                    <option value="Data Science">Data Science</option>
                    <option value="Business">Business</option>
                </select>
            </div>
            
            <!-- Graduation Year -->
            <div class="form-group">
                <label for="year">Expected Graduation Year *</label>
                <select id="year" name="year" required>
                    <option value="">-- Select Year --</option>
                    <option value="2025">2025</option>
                    <option value="2026">2026</option>
                    <option value="2027">2027</option>
                    <option value="2028">2028</option>
                </select>
            </div>
            
            <button type="submit">✅ Register</button>
        </form>
    </div>
</body>
</html>
```

---

## 14.5 Code - result.jsp (Kết Quả)

```jsp
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Registration Success</title>
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
            padding: 20px;
        }
        
        .container {
            background: white;
            border-radius: 10px;
            box-shadow: 0 10px 40px rgba(0,0,0,0.2);
            width: 100%;
            max-width: 600px;
            padding: 40px;
            text-align: center;
        }
        
        .success-icon {
            font-size: 60px;
            margin-bottom: 20px;
        }
        
        h1 {
            color: #28a745;
            margin-bottom: 10px;
            font-size: 32px;
        }
        
        .subtitle {
            color: #666;
            margin-bottom: 30px;
            font-size: 16px;
        }
        
        .info-box {
            background: #f8f9fa;
            border-left: 4px solid #667eea;
            padding: 20px;
            margin: 20px 0;
            text-align: left;
            border-radius: 5px;
        }
        
        .info-row {
            display: flex;
            justify-content: space-between;
            padding: 10px 0;
            border-bottom: 1px solid #e0e0e0;
        }
        
        .info-row:last-child {
            border-bottom: none;
        }
        
        .info-label {
            font-weight: 600;
            color: #333;
            min-width: 150px;
        }
        
        .info-value {
            color: #667eea;
            font-weight: 500;
        }
        
        .button-group {
            display: flex;
            gap: 10px;
            margin-top: 30px;
            justify-content: center;
        }
        
        button, a {
            padding: 12px 30px;
            border: none;
            border-radius: 5px;
            font-size: 16px;
            font-weight: 600;
            cursor: pointer;
            text-decoration: none;
            display: inline-block;
            transition: all 0.3s;
        }
        
        .btn-primary {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
        }
        
        .btn-primary:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 20px rgba(102, 126, 234, 0.4);
        }
        
        .btn-secondary {
            background: #e0e0e0;
            color: #333;
        }
        
        .btn-secondary:hover {
            background: #d0d0d0;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="success-icon">✅</div>
        
        <h1>Registration Successful!</h1>
        <p class="subtitle">Your registration has been processed successfully</p>
        
        <div class="info-box">
            <div class="info-row">
                <span class="info-label">Full Name:</span>
                <span class="info-value"><%= request.getAttribute("fullName") %></span>
            </div>
            
            <div class="info-row">
                <span class="info-label">Student ID:</span>
                <span class="info-value"><%= request.getAttribute("studentId") %></span>
            </div>
            
            <div class="info-row">
                <span class="info-label">Email:</span>
                <span class="info-value"><%= request.getAttribute("email") %></span>
            </div>
            
            <div class="info-row">
                <span class="info-label">Phone:</span>
                <span class="info-value"><%= request.getAttribute("phone") %></span>
            </div>
            
            <div class="info-row">
                <span class="info-label">Major:</span>
                <span class="info-value"><%= request.getAttribute("major") %></span>
            </div>
            
            <div class="info-row">
                <span class="info-label">Graduation Year:</span>
                <span class="info-value"><%= request.getAttribute("year") %></span>
            </div>
            
            <div class="info-row">
                <span class="info-label">Registration Time:</span>
                <span class="info-value"><%= request.getAttribute("registrationTime") %></span>
            </div>
        </div>
        
        <p style="color: #666; margin-top: 20px;">
            📧 Confirmation email has been sent to your email address.<br>
            🎓 You will receive further instructions soon.
        </p>
        
        <div class="button-group">
            <a href="index.jsp" class="btn-primary">Register Another Student</a>
            <a href="javascript:window.print()" class="btn-secondary">Print</a>
        </div>
    </div>
</body>
</html>
```

---

## 14.6 web.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app version="4.0"
         xmlns="http://xmlns.jcp.org/xml/ns/javaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee
         http://xmlns.jcp.org/xml/ns/javaee/web-app_4_0.xsd">

    <display-name>Student Registration System</display-name>
    
    <welcome-file-list>
        <welcome-file>index.jsp</welcome-file>
    </welcome-file-list>
    
</web-app>
```

---

## 14.7 Deploy & Chạy

### Bước 1: Build WAR

```cmd
mkdir -p build/StudentRegistration/WEB-INF/classes
javac -cp "C:\apache-tomcat-10.1.x\lib\*" ^
      -d build/StudentRegistration/WEB-INF/classes ^
      src/main/java/com/example/servlets/RegistrationServlet.java

copy src/main/webapp/WEB-INF/web.xml build/StudentRegistration/WEB-INF\
copy src/main/webapp/*.jsp build/StudentRegistration\

cd build
jar cvf StudentRegistration.war StudentRegistration/
```

### Bước 2: Deploy

Copy `StudentRegistration.war` vào `C:\apache-tomcat-10.1.x\webapps\`

### Bước 3: Truy cập

```
http://localhost:8080/StudentRegistration/
```

---

## 🎉 Hoàn thành Tuần 2!

**Bạn đã học:**
1. ✅ JSP Introduction (Directive, Scriptlet, Expression, Declaration)
2. ✅ JSP Lifecycle
3. ✅ 9 Implicit Objects
4. ✅ JSP Include (Page vs Action)
5. ✅ Expression Language (EL)
6. ✅ JSTL Core Tags
7. ✅ Form Handling
8. ✅ Mini Project - Student Registration

**Bước tiếp theo:** Tuần 3 - MVC Architecture

---

---

# 📚 Tài liệu Tham Khảo

- Oracle JSP/Servlet Documentation
- Apache Tomcat Guide
- Java EE Platform
- JSTL Tutorial

**Tài liệu được biên soạn bởi: Senior Java Developer**
**Ngày cập nhật: 2025**
**Phiên bản: 1.0**
