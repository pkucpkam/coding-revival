# 18-http-utils

# Document: `javax.servlet.http.HttpUtils`

* **Package:** `javax.servlet.http`
* **Class:** `public class HttpUtils extends java.lang.Object`
* **Inheritance:** `java.lang.Object` $\rightarrow$ `javax.servlet.http.HttpUtils`
* **Deprecated:** Kể từ Java Servlet API 2.3. Các phương thức của lớp này chỉ hữu ích khi sử dụng bảng mã mã hóa mặc định và đã được di chuyển trực tiếp vào các interface Request (như `HttpServletRequest` và `ServletRequest`).

---

## 1. Tổng quan (Overview)

Lớp `HttpUtils` cung cấp các phương thức tiện ích hỗ trợ phân tích chuỗi truy vấn (query string), xử lý dữ liệu form gửi qua HTTP POST, và tái tạo URL yêu cầu từ phía client.

Tuy nhiên, do hạn chế về khả năng xử lý đa dạng các bảng mã ký tự (character encoding) và tính tiện dụng, lớp này đã bị **bãi bỏ (Deprecated)** từ phiên bản **Servlet 2.3**. Lập trình viên được khuyến nghị sử dụng trực tiếp các phương thức tương đương do Servlet Container cung cấp sẵn trong `HttpServletRequest` và `ServletRequest`.

---

## 2. Tóm tắt Constructor & Phương thức (Summary)

### Constructor Summary

| Constructor | Mô tả |
| --- | --- |
| `HttpUtils()` | *(Deprecated)* Khởi tạo một đối tượng `HttpUtils` rỗng. |

---

### Method Summary

| Kiểu trả về | Phương thức | Mô tả |
| --- | --- | --- |
| `static StringBuffer` | `getRequestURL(HttpServletRequest req)` | *(Deprecated)* Tái tạo lại URL đầy đủ mà client dùng để gửi request dựa trên thông tin trong `HttpServletRequest`. |
| `static Hashtable<String, String[]>` | `parsePostData(int len, ServletInputStream in)` | *(Deprecated)* Phân tích dữ liệu gửi từ HTML Form bằng phương thức HTTP POST với MIME type `application/x-www-form-urlencoded`. |
| `static Hashtable<String, String[]>` | `parseQueryString(String s)` | *(Deprecated)* Phân tích chuỗi Query String được gửi từ client để tạo đối tượng `Hashtable` chứa các cặp key-value. |

---

## 3. Chi tiết Constructor & Tất cả các phương thức (Detail)

---

### Constructor Detail

#### `HttpUtils()`

* **Cú pháp:** `public HttpUtils()`
* **Mô tả:** *(Deprecated)* Constructor mặc định khởi tạo một đối tượng `HttpUtils` rỗng.

---

### Method Detail

#### 1. `parseQueryString(String s)`

```java
public static java.util.Hashtable parseQueryString(java.lang.String s)

```

* **Mô tả:** *(Deprecated)* Phân tích chuỗi truy vấn (query string) dạng `key1=value1&key2=value2` gửi từ client và tạo một đối tượng `Hashtable`.
* Nếu một key xuất hiện nhiều lần với các giá trị khác nhau, key đó chỉ xuất hiện một lần trong `Hashtable` nhưng giá trị đi kèm sẽ là một mảng chuỗi (`String[]`) chứa tất cả các giá trị gửi lên.
* Các key và value đều được giải mã (decode): dấu `+` chuyển thành khoảng trắng, các ký tự mã hóa hex (`%xx`) được chuyển thành ký tự ASCII tương ứng.


* **Parameters:** `s` - chuỗi query string cần phân tích.
* **Returns:** Đối tượng `Hashtable` chứa các cặp key-value đã phân tích.
* **Throws:** `java.lang.IllegalArgumentException` nếu chuỗi query string không hợp lệ.

---

#### 2. `parsePostData(int len, ServletInputStream in)`

```java
public static java.util.Hashtable parsePostData(int len, ServletInputStream in)

```

* **Mô tả:** *(Deprecated)* Đọc và phân tích dữ liệu stream gửi từ form HTML thông qua phương thức HTTP POST với Content-Type `application/x-www-form-urlencoded`.
* Kết quả trả về là một `Hashtable` trong đó các giá trị là mảng chuỗi (`String[]`) chứa các tham số tương ứng.
* Dữ liệu key và value được tự động giải mã từ định dạng URL encoding.


* **Parameters:**
* `len`: Độ dài (tính bằng số ký tự/bytes) của dữ liệu trong `ServletInputStream`.
* `in`: Đối tượng `ServletInputStream` chứa dữ liệu gửi từ client.


* **Returns:** Đối tượng `Hashtable` chứa các tham số POST đã phân tích.
* **Throws:** `java.lang.IllegalArgumentException` nếu dữ liệu POST gửi lên không hợp lệ.

---

#### 3. `getRequestURL(HttpServletRequest req)`

```java
public static java.lang.StringBuffer getRequestURL(HttpServletRequest req)

```

* **Mô tả:** *(Deprecated)* Tái dựng lại đường dẫn URL client đã sử dụng để gửi request. URL trả về bao gồm giao thức (protocol), tên server, cổng (port number), và đường dẫn server (server path), nhưng **không bao gồm query string**.
* **Parameters:** `req` - đối tượng `HttpServletRequest` chứa yêu cầu của client.
* **Returns:** Đối tượng `StringBuffer` chứa URL đã được tái tạo.

---

## 4. Bảng so sánh & Phương thức thay thế chuẩn (Standard Replacements)

Lớp `HttpUtils` bị bãi bỏ do các phương thức của nó đã có phương thức thay thế trực tiếp, tối ưu hơn trong các interface chuẩn của Servlet API:

| Phương thức trong `HttpUtils` (Deprecated) | Phương thức thay thế chuẩn (Recommended) | Interface chứa phương thức thay thế |
| --- | --- | --- |
| `HttpUtils.getRequestURL(req)` | `req.getRequestURL()` | `javax.servlet.http.HttpServletRequest` |
| `HttpUtils.parseQueryString(s)` | `req.getParameterMap()`, `req.getParameter()`, `req.getParameterValues()` | `javax.servlet.ServletRequest` |
| `HttpUtils.parsePostData(len, in)` | `req.getParameterMap()`, `req.getParameter()`, `req.getParameterValues()` | `javax.servlet.ServletRequest` |

---

## 5. Ví dụ mã nguồn: Từ mã Deprecated sang mã chuẩn (Code Examples)

### Ví dụ 1: Tái tạo URL yêu cầu (Reconstruct Request URL)

#### Mã nguồn cũ (Deprecated - Dùng `HttpUtils`):

```java
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpUtils; // Deprecated
import java.io.IOException;

public class OldUrlServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        // CÁCH CŨ: Sử dụng HttpUtils.getRequestURL (Deprecated)
        @SuppressWarnings("deprecation")
        StringBuffer url = HttpUtils.getRequestURL(req);
        
        resp.getWriter().println("URL: " + url.toString());
    }
}

```

#### Mã nguồn MỚI (Khuyên dùng - Dùng trực tiếp `HttpServletRequest`):

```java
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/modern-url")
public class ModernUrlServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        // CÁCH MỚI: Gọi trực tiếp phương thức getRequestURL() từ req
        StringBuffer requestURL = req.getRequestURL();
        
        // Nếu muốn đính kèm Query String thủ công
        String queryString = req.getQueryString();
        if (queryString != null) {
            requestURL.append("?").append(queryString);
        }

        resp.getWriter().println("Full URL: " + requestURL.toString());
    }
}

```

---

### Ví dụ 2: Phân tích Parameter từ Form / Query String

#### Mã nguồn cũ (Deprecated - Dùng `parsePostData` hoặc `parseQueryString`):

```java
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpUtils; // Deprecated
import java.io.IOException;
import java.util.Hashtable;

public class OldParamParserServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        // CÁCH CŨ: Tự đọc Stream và parse qua HttpUtils (Deprecated)
        @SuppressWarnings("deprecation")
        Hashtable<String, String[]> params = HttpUtils.parsePostData(
            req.getContentLength(), 
            req.getInputStream()
        );

        String[] usernames = params.get("username");
        if (usernames != null && usernames.length > 0) {
            resp.getWriter().println("User: " + usernames[0]);
        }
    }
}

```

#### Mã nguồn MỚI (Khuyên dùng - Dùng `getParameterMap` / `getParameter`):

```java
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@WebServlet("/modern-params")
public class ModernParamServlet extends HttpServlet {
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        // Đảm bảo thiết lập mã hóa ký tự trước khi đọc dữ liệu
        req.setCharacterEncoding("UTF-8");

        // 1. Lấy giá trị đơn lẻ
        String username = req.getParameter("username");

        // 2. Hoặc lấy mảng giá trị nếu tham số xuất hiện nhiều lần
        String[] hobbies = req.getParameterValues("hobbies");

        // 3. Hoặc lấy toàn bộ Map chứa tham số đã được Container giải mã sẵn
        Map<String, String[]> parameterMap = req.getParameterMap();

        resp.setContentType("text/plain; charset=UTF-8");
        resp.getWriter().println("Username: " + username);
        if (hobbies != null) {
            resp.getWriter().println("Hobbies count: " + hobbies.length);
        }
    }
}

```