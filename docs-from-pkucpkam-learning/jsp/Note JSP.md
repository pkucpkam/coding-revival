4 yếu tố chính của JSP:

1. Directive
	`<%@ ... %>`  -> mục đích báo cho tomcat hướng dẫn về JSP
	- Page Directive: 
		### Một số thuộc tính hay dùng:
		- `contentType`: kiểu dữ liệu trả về (HTML, JSON…)
		- `import`: import package Java
		    
		    ```
		    <%@ page import="java.util.List" %>
		    ```
		    
		- `session`: có dùng session không (`true/false`)
		- `errorPage`: trang xử lý lỗi
		- `isErrorPage`: đánh dấu đây là trang lỗi
		
		### 📌 Hiểu đơn giản:
		
		👉 giống kiểu config cho Servlet phía sau
	Mấy dòng này đều là **`page directive`** trong JSP — tức là cấu hình cho servlet mà JSP sẽ được biên dịch thành (ví dụ chạy trên Apache Tomcat).

Tui đi từng cái cho rõ bản chất luôn:

---

## 1️⃣ `language="java"`

```jsp
<%@ page language="java" %>
```

👉 Chỉ định ngôn ngữ dùng trong JSP

- Mặc định luôn là **Java**
    
- Thực tế: **99.99% không cần ghi**, vì JSP spec chỉ hỗ trợ Java
    

📌 Hiểu đơn giản:

> “File JSP này sẽ được convert thành Servlet Java”

---

## 2️⃣ `contentType="text/html; charset=UTF-8"`

```jsp
<%@ page contentType="text/html; charset=UTF-8" %>
```

👉 Xác định **kiểu dữ liệu response trả về cho client**

- `text/html`: trả về HTML
    
- `charset=UTF-8`: encoding (rất quan trọng để không lỗi tiếng Việt)
    

📌 Tương đương trong Servlet:

```java
response.setContentType("text/html; charset=UTF-8");
```

---

## 3️⃣ `import="java.util.*,java.io.*"`

```jsp
<%@ page import="java.util.*,java.io.*" %>
```

👉 Import package Java để dùng trong JSP

Ví dụ:

```jsp
<%
    List<String> list = new ArrayList<>();
%>
```

📌 Tương đương trong Servlet:

```java
import java.util.*;
import java.io.*;
```

⚠️ Lưu ý:

- Có thể import nhiều package, cách nhau bằng dấu `,`
    
- Nhưng **best practice**: hạn chế viết Java trong JSP (nên dùng JSTL/EL)
    

---

## 4️⃣ `errorPage="error.jsp"`

```jsp
<%@ page errorPage="error.jsp" %>
```

👉 Chỉ định trang xử lý lỗi nếu JSP này bị exception

- Khi có lỗi → tự động forward sang `error.jsp`
    

📌 Ví dụ:

```jsp
<%
    int x = 10 / 0; // lỗi
%>
```

→ sẽ chuyển sang `error.jsp`

📌 Tương đương kiểu:

```java
try {
   ...
} catch(Exception e) {
   forward("error.jsp");
}
```

---

## 5️⃣ `isErrorPage="true"`

```jsp
<%@ page isErrorPage="true" %>
```

👉 Đánh dấu trang này là **trang xử lý lỗi**

- Khi set `true`:
    
    - JSP sẽ có sẵn object `exception`
        

### Ví dụ trong `error.jsp`:

```jsp
<%= exception.getMessage() %>
```

📌 Nếu không có dòng này:

- ❌ `exception` sẽ không tồn tại → lỗi
    

---

## ⚠️ Một lỗi logic trong ví dụ của ông

Ông đang viết **cả 2 cái trong cùng 1 file**:

```jsp
<%@ page errorPage="error.jsp" %>
<%@ page isErrorPage="true" %>
```

👉 Điều này **không hợp lý**

### Vì:

- `errorPage` → dành cho trang **bị lỗi**
    
- `isErrorPage` → dành cho trang **xử lý lỗi**
    

📌 Đúng phải tách ra:

### Trang chính:

```jsp
<%@ page errorPage="error.jsp" %>
```

### Trang error.jsp:

```jsp
<%@ page isErrorPage="true" %>
```

---

## 🧠 Tóm lại cho dễ nhớ

|Directive|Ý nghĩa|
|---|---|
|`language`|ngôn ngữ (Java)|
|`contentType`|kiểu dữ liệu trả về|
|`import`|import thư viện|
|`errorPage`|chuyển hướng khi lỗi|
|`isErrorPage`|trang nhận lỗi (có `exception`)|

---

Nếu ông muốn level up thêm:  
👉 tui có thể show luôn **JSP này compile ra servlet code như nào (có exception handling luôn)** — cái đó hiểu xong là nắm JSP tận gốc luôn.

1. Include Directive (<%@ include ... %>)
	dùng để gộp file JSP khác vào thời điểm runtime 
	`<%@ include file="header.jsp" %>`
2. Taglib Directive 
	-> Import thư viện JSTL 

