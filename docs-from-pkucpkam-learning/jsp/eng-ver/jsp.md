

# 🚀 **JAVA WEB BASICS – DETAILED CORE (INTERVIEW LEVEL)**

---

# 🔥 1. Java Web Overview

---

## ✅ JVM (Java Virtual Machine)

### ✔️ Definition

JVM is a virtual machine that executes Java bytecode.

### ✔️ Flow:

```
.java → (javac) → .class (bytecode) → JVM → Native machine code
```

### ✔️ Key point (interview):

* Platform independent
* Handles:

  * Memory management
  * Garbage Collection
  * Security

👉 **Interview sentence:**

> JVM allows Java to be platform-independent by converting bytecode into machine-specific instructions at runtime.

---

## ✅ Java EE (Enterprise Edition)

### ✔️ Purpose

Used for building **large-scale web applications**

### ✔️ Main components:

* Servlet → handle request
* JSP → render UI
* JDBC → connect database
* JPA/Hibernate → ORM

👉 **Key idea:**

> Java EE provides APIs and specifications for building enterprise web systems.

---

## ✅ Servlet Container (Apache Tomcat)

### ✔️ Definition

A **runtime environment** that:

* Receives HTTP requests
* Maps to Servlet
* Manages lifecycle
* Sends response

### ✔️ Flow:

```
Client → HTTP Request → Tomcat → Servlet → Response → Client
```

👉 **Interview insight:**

* Tomcat ≠ full Java EE server
* It supports Servlet & JSP only (lightweight)

---

# 🌐 2. HTTP & Web Concepts

---

## ✅ HTTP Characteristics

* Stateless → no memory between requests
* Request–Response model
* Text-based protocol

---

## ✅ HTTP Request Structure

```
GET /hello HTTP/1.1
Host: localhost:8080
User-Agent: Chrome
```

---

## ✅ HTTP Response Structure

```
HTTP/1.1 200 OK
Content-Type: text/html

<h1>Hello</h1>
```

---

## ✅ GET vs POST (IMPORTANT)

### ✔️ GET

* Used to **retrieve data**
* Parameters in URL
* Idempotent (no side effects)

### ✔️ POST

* Used to **submit data**
* Data in body
* Not idempotent

👉 **Advanced insight:**

> GET should not modify server state, while POST is used for operations that change data.

---

## ✅ HTTP Status Codes

| Type | Meaning      |
| ---- | ------------ |
| 2xx  | Success      |
| 3xx  | Redirect     |
| 4xx  | Client error |
| 5xx  | Server error |

👉 Must know:

* 200 → OK
* 404 → Not Found
* 500 → Internal Error

---

# ⚙️ 3. Servlet Basics

---

## ✅ Servlet Definition

> A Servlet is a Java class that runs on the server and handles HTTP requests and responses.

---

## ✅ HttpServlet

### ✔️ Methods:

```java
protected void doGet(HttpServletRequest req, HttpServletResponse res)
protected void doPost(HttpServletRequest req, HttpServletResponse res)
```

---

## ✅ Request Object (HttpServletRequest)

### ✔️ Purpose:

* Read data from client

### ✔️ Important methods:

```java
request.getParameter("name")
request.getMethod()
request.getHeader("User-Agent")
request.getSession()
```

---

## ✅ Response Object (HttpServletResponse)

### ✔️ Purpose:

* Send data to client

```java
response.setContentType("text/html")
response.getWriter().println("Hello")
response.sendRedirect("/home")
response.sendError(404)
```

---

## 🎯 Key insight:

> Servlet acts as a controller in MVC architecture.

---

# ⏱️ 4. Servlet Lifecycle (VERY IMPORTANT)

---

## ✅ 3 Phases

---

### 🔹 1. init()

* Called once
* Used to initialize resources

---

### 🔹 2. service()

* Called for every request
* Delegates to:

  * doGet()
  * doPost()

---

### 🔹 3. destroy()

* Called once when server shuts down
* Used to release resources

---

## 🎯 Advanced insight:

> Servlet instance is created once and reused for multiple requests → must be thread-safe.

👉 (Câu này nói ra là hơn fresher level)

---

# 🍪 5. Session & Cookies

---

## ❗ Problem: HTTP is Stateless

→ Server cannot recognize user between requests

---

## ✅ Cookie

### ✔️ Stored on client

```java
Cookie cookie = new Cookie("user", "phuc");
response.addCookie(cookie);
```

### ✔️ Use cases:

* Remember login
* Store preferences

---

## ✅ Session

### ✔️ Stored on server

```java
HttpSession session = request.getSession();
session.setAttribute("user", "phuc");
```

---

### ✔️ How it works:

1. Server creates session
2. Generates **SessionID**
3. Sends via cookie (JSESSIONID)
4. Client sends it back in next request

---

## ✅ Cookie vs Session (INTERVIEW GOLD)

| Cookie      | Session     |
| ----------- | ----------- |
| Client-side | Server-side |
| Less secure | More secure |
| Small size  | Large data  |

---

## 🎯 Insight:

> Session uses Cookie internally (JSESSIONID) to track users.

---

# 🚀 6. Deployment & WAR

---

## ✅ WAR (Web Archive)

Contains:

```
WEB-INF/
  ├─ classes/
  ├─ lib/
  └─ web.xml
```

---

## ✅ Deployment Flow

1. Write Servlet
2. Compile
3. Package → WAR
4. Deploy to Tomcat
5. Access via URL

---

# 🎓 7. Mini Project Flow (REAL WORLD)

---

## ✅ Flow:

```
HTML Form
   ↓ POST
Servlet (Controller)
   ↓ process
Response (HTML/JSP)
```

---

## ✅ Key Responsibilities

### Servlet:

* Receive data
* Validate input
* Call business logic
* Return response

---

# 🎯 🔥 FINAL INTERVIEW ANSWERS (ADVANCED LEVEL)

---

## ❓ Explain Java Web Architecture

> In Java Web, the client sends an HTTP request to the server.
> The Servlet Container like Tomcat receives it and maps it to a specific Servlet.
> The Servlet processes the request, may interact with business logic or database, and generates a response which is sent back to the client.

---

## ❓ How does Servlet handle multiple users?

> A single Servlet instance handles multiple requests using multithreading, so developers must ensure thread safety.

---

## ❓ Why HTTP is stateless and how to solve it?

> HTTP is stateless because each request is independent.
> We solve this using Session or Cookies to maintain user state.

---

## ❓ When to use GET vs POST?

> GET is used for retrieving data and should not change server state, while POST is used for submitting data that modifies the server.

---

# ⚡ KẾT LUẬN (THỰC TẾ)

Nếu ông nắm được:

* Flow Client → Servlet → Response
* Lifecycle
* Session vs Cookie
* GET vs POST

👉 Thì:
**= đủ pass Fresher Java Web (Servlet/JSP)**

---

Nếu muốn hardcore hơn nữa:
👉 Tôi có thể:

* Giả lập phỏng vấn 30 câu (có bắt lỗi)
* Hoặc cho ông 1 list câu hỏi “interviewer hay trap”

Nói 1 tiếng, tôi train ông lên level “không bị vặn chết” 😏
