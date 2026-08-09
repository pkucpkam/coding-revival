# 01-cookies

# Document: `javax.servlet.http.Cookie`

* **Package:** `javax.servlet.http`
* **Class:** `public class Cookie implements java.lang.Cloneable`
* **Direct Known Subclasses:** None
* **All Implemented Interfaces:** `java.lang.Cloneable`, `java.io.Serializable`

---

## 1. Tổng quan (Overview)

Lớp `Cookie` được dùng để tạo một **cookie** — một lượng nhỏ dữ liệu do Servlet gửi đến trình duyệt web (Web browser), được trình duyệt lưu trữ trên máy khách và gửi ngược lại server trong các request sau đó. Giá trị của cookie có thể xác định duy nhất một client, do đó cookie thường được sử dụng trong việc quản lý phiên làm việc (**Session Management**).

---

### Đặc điểm chính của Cookie:

1. **Cấu trúc:** Một cookie bao gồm một **tên (name)**, một **giá trị (value)** duy nhất và các thuộc tính tùy chọn như *comment, path, domain, max age,* và *version number*.
2. **Cách Servlet gửi Cookie:** Servlet gửi cookie về trình duyệt bằng phương thức `HttpServletResponse.addCookie(Cookie)`. Phương thức này sẽ thêm thông tin vào header của HTTP Response.
3. **Giới hạn của trình duyệt:**
* Tối đa **20 cookies** cho mỗi Web server.
* Tối đa **300 cookies** tổng cộng trên trình duyệt.
* Kích thước tối đa **4 KB** cho mỗi cookie.


4. **Cách trình duyệt trả Cookie:** Trình duyệt tự động đính kèm cookie vào header của HTTP Request. Servlet lấy danh sách cookie thông qua `HttpServletRequest.getCookies()`.
5. **Phiên bản hỗ trợ:** Lớp này hỗ trợ cả **Version 0** (theo chuẩn Netscape) và **Version 1** (theo chuẩn RFC 2109). Mặc định tạo theo Version 0 để đạt tương thích tốt nhất.

---

## 2. Tóm tắt Constructor & Phương thức (Summary)

### Constructor Summary

| Constructor | Mô tả |
| --- | --- |
| `Cookie(String name, String value)` | Khởi tạo một cookie với tên và giá trị xác định. |

---

### Method Summary

| Kiểu trả về | Phương thức | Mô tả |
| --- | --- | --- |
| `Object` | `clone()` | Ghi đè phương thức `java.lang.Object.clone` để trả về một bản sao của cookie này. |
| `String` | `getComment()` | Trả về chuỗi ghi chú mô tả mục đích của cookie, hoặc `null` nếu không có. |
| `String` | `getDomain()` | Trả về tên miền (domain) được thiết lập cho cookie này. |
| `int` | `getMaxAge()` | Trả về thời hạn tồn tại tối đa của cookie tính bằng giây (mặc định `-1`). |
| `String` | `getName()` | Trả về tên của cookie. |
| `String` | `getPath()` | Trả về đường dẫn (path) trên server mà trình duyệt sẽ gửi cookie tới. |
| `boolean` | `getSecure()` | Trả về `true` nếu cookie chỉ được gửi qua giao thức bảo mật (HTTPS/SSL). |
| `String` | `getValue()` | Trả về giá trị của cookie. |
| `int` | `getVersion()` | Trả về phiên bản giao thức cookie mà cookie này tuân thủ. |
| `void` | `setComment(String purpose)` | Bổ sung ghi chú mô tả mục đích sử dụng của cookie. |
| `void` | `setDomain(String pattern)` | Quy định miền (domain) mà cookie có thể được gửi tới. |
| `void` | `setMaxAge(int expiry)` | Thiết lập thời hạn tồn tại tối đa của cookie tính bằng giây. |
| `void` | `setPath(String uri)` | Thiết lập đường dẫn URL ứng dụng nhận cookie. |
| `void` | `setSecure(boolean flag)` | Chỉ định xem cookie có chỉ được gửi qua kết nối bảo mật hay không. |
| `void` | `setValue(String newValue)` | Gán giá trị mới cho cookie sau khi khởi tạo. |
| `void` | `setVersion(int v)` | Thiết lập phiên bản chuẩn giao thức cho cookie (`0` hoặc `1`). |

---

## 3. Chi tiết Constructor & Tất cả các phương thức (Detail & Examples)

---

### Constructor Detail

#### `Cookie(String name, String value)`

* **Mô tả:** Khởi tạo một cookie mới.
* Tên phải tuân theo chuẩn **RFC 2109** (chỉ chứa ký tự ASCII chữ và số, không chứa dấu phẩy, chấm phẩy, khoảng trắng, hoặc bắt đầu bằng `$`). Tên **không thể thay đổi** sau khi tạo.
* Giá trị có thể là bất kỳ chuỗi nào mà server muốn gửi. Giá trị có thể thay đổi sau khi tạo bằng `setValue()`.


* **Parameters:**
* `name`: Chuỗi chứa tên của cookie.
* `value`: Chuỗi chứa giá trị của cookie.


* **Throws:** `java.lang.IllegalArgumentException` nếu tên cookie chứa ký tự không hợp lệ.

```java
// Ví dụ khởi tạo Cookie
Cookie sessionCookie = new Cookie("SESSION_ID", "XYZ123456789");

```

---

### Method Detail

#### 1. `clone()`

* **Cú pháp:** `public Object clone()`
* **Mô tả:** Trả về một bản sao độc lập của đối tượng `Cookie` hiện tại.
* **Returns:** Một `Object` là bản sao của cookie này.

```java
Cookie original = new Cookie("theme", "dark");
Cookie copy = (Cookie) original.clone();
System.out.println(copy.getName() + " = " + copy.getValue()); // Output: theme = dark

```

---

#### 2. `setComment(String purpose)` & `getComment()`

* **Cú pháp:**
* `public void setComment(String purpose)`
* `public String getComment()`


* **Mô tả:**
* `setComment`: Thiết lập ghi chú giải thích mục đích của cookie (hữu ích khi trình duyệt hiển thị thông báo cookie cho người dùng). Không áp dụng cho Version 0 cookies.
* `getComment`: Trả về ghi chú đó, hoặc `null` nếu chưa đặt.



```java
Cookie cartCookie = new Cookie("cart_id", "99823");
cartCookie.setVersion(1); // Yêu cầu Version 1 để hỗ trợ Comment
cartCookie.setComment("Luu tru gio hang tam thoi cua nguoi dung");

System.out.println("Comment: " + cartCookie.getComment());

```

---

#### 3. `setDomain(String pattern)` & `getDomain()`

* **Cú pháp:**
* `public void setDomain(String pattern)`
* `public String getDomain()`


* **Mô tả:**
* `setDomain`: Thiết lập miền (domain) mà cookie sẽ được trình duyệt gửi kèm. Dạng miền tuân theo RFC 2109 (bắt đầu bằng dấu chấm, ví dụ `.example.com`, cho phép gửi đến `[www.example.com](https://www.example.com)` hay `api.example.com`).
* `getDomain`: Trả về miền đã thiết lập.



```java
Cookie ssoCookie = new Cookie("sso_token", "AUTH_TOKEN_ABC");
// Cho phép tất cả các subdomain của example.com truy cập Cookie này
ssoCookie.setDomain(".example.com"); 

response.addCookie(ssoCookie);

```

---

#### 4. `setMaxAge(int expiry)` & `getMaxAge()`

* **Cú pháp:**
* `public void setMaxAge(int expiry)`
* `public int getMaxAge()`


* **Mô tả:**
* `setMaxAge`: Đặt thời gian sống tối đa của cookie tính bằng **giây**.
* **Số dương (`> 0`):** Cookie hết hạn sau đúng số giây đó và lưu trên đĩa cứng.
* **Số âm (`< 0`):** Cookie không lưu cố định, tự bị xóa khi người dùng tắt trình duyệt (In-memory/Session cookie).
* **Số không (`0`):** Xóa ngay lập tức cookie trên máy khách.


* `getMaxAge`: Trả về thời gian sống hiện tại (mặc định `-1`).



```java
// Ví dụ 1: Lưu thông tin trong 30 ngày (Persistent Cookie)
Cookie rememberMe = new Cookie("remember_user", "john_doe");
rememberMe.setMaxAge(30 * 24 * 60 * 60); // 30 ngày = 2,592,000 giây
response.addCookie(rememberMe);

// Ví dụ 2: Xóa Cookie khỏi browser (Delete Cookie)
Cookie removeCookie = new Cookie("remember_user", "");
removeCookie.setMaxAge(0); // Xóa ngay
response.addCookie(removeCookie);

```

---

#### 5. `setPath(String uri)` & `getPath()`

* **Cú pháp:**
* `public void setPath(String uri)`
* `public String getPath()`


* **Mô tả:**
* `setPath`: Giới hạn các đường dẫn URL trên server mà trình duyệt được phép gửi cookie tới. Cookie sẽ hiển thị với tất cả trang trong thư mục này và các thư mục con của nó.
* `getPath`: Trả về đường dẫn của cookie.



```java
Cookie adminCookie = new Cookie("admin_level", "super");
// Cookie này chỉ gửi khi URL bắt đầu bằng /app/admin
adminCookie.setPath("/app/admin"); 

response.addCookie(adminCookie);

```

---

#### 6. `setSecure(boolean flag)` & `getSecure()`

* **Cú pháp:**
* `public void setSecure(boolean flag)`
* `public boolean getSecure()`


* **Mô tả:**
* `setSecure`: Nếu truyền vào `true`, trình duyệt sẽ **chỉ gửi cookie này qua đường truyền bảo mật** HTTPS hoặc SSL. Giá trị mặc định là `false`.
* `getSecure`: Trả về trạng thái cờ secure (`true`/`false`).



```java
Cookie secureToken = new Cookie("AUTH_SECURE", "SECURE_HASH_123");
secureToken.setSecure(true); // Ngăn ngừa việc truyền tải token qua kết nối HTTP thường

response.addCookie(secureToken);

```

---

#### 7. `getName()`

* **Cú pháp:** `public String getName()`
* **Mô tả:** Trả về tên của cookie. Tên này không thể thay đổi sau khi cookie được tạo.

```java
Cookie cookie = new Cookie("language", "vi_VN");
System.out.println("Cookie Name: " + cookie.getName()); // Output: language

```

---

#### 8. `setValue(String newValue)` & `getValue()`

* **Cú pháp:**
* `public void setValue(String newValue)`
* `public String getValue()`


* **Mô tả:**
* `setValue`: Cập nhật giá trị mới cho cookie. Với Version 0, giá trị không nên chứa khoảng trắng, dấu ngoặc, dấu bằng, dấu phẩy, dấu chấm phẩy, v.v. Nếu sử dụng dữ liệu nhị phân, nên **Base64 Encode**.
* `getValue`: Lấy giá trị hiện tại của cookie.



```java
Cookie preference = new Cookie("theme", "light");
// Cập nhật giá trị mới
preference.setValue("dark_mode");

System.out.println("Gia tri hien tai: " + preference.getValue()); // Output: dark_mode

```

---

#### 9. `setVersion(int v)` & `getVersion()`

* **Cú pháp:**
* `public void setVersion(int v)`
* `public int getVersion()`


* **Mô tả:**
* `setVersion`: Thiết lập chuẩn cookie (`0` tuân theo chuẩn Netscape gốc, `1` tuân theo chuẩn RFC 2109).
* `getVersion`: Trả về phiên bản protocol tương ứng (`0` hoặc `1`).



```java
Cookie rfcCookie = new Cookie("custom_attr", "value");
rfcCookie.setVersion(1); // Chuyển sang RFC 2109

System.out.println("Version: " + rfcCookie.getVersion()); // Output: 1

```

---

## 4. Ví dụ tổng hợp trọn bộ (Complete Integration Example)

Ví dụ dưới đây tổng hợp cách làm việc thực tế trong dự án Java Web (sử dụng Servlet) bao gồm: **Ghi Cookie**, **Đọc toàn bộ Cookie**, và **Cập nhật / Xóa Cookie**.

```java
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/cookie-demo")
public class CookieDemoServlet extends HttpServlet {

    // 1. Ghi (Tạo mới) Cookie về Client
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String userTheme = request.getParameter("theme");

        // Khởi tạo và thiết lập các thuộc tính cho Cookie
        Cookie themeCookie = new Cookie("app_theme", userTheme);
        themeCookie.setMaxAge(7 * 24 * 60 * 60); // Tồn tại 7 ngày
        themeCookie.setPath("/");                // Có hiệu lực trên toàn bộ Website
        themeCookie.setSecure(false);            // Cho phép cả HTTP và HTTPS

        // Đẩy Cookie vào Header của HTTP Response
        response.addCookie(themeCookie);

        response.getWriter().println("Da luu cau hinh Theme thanh cong!");
    }

    // 2. Đọc tất cả các Cookie gửi từ Client lên
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        // Lấy danh sách Cookie từ Request
        Cookie[] cookies = request.getCookies();

        out.println("<h2>Danh sach Cookies nhan duoc:</h2>");
        if (cookies != null && cookies.length > 0) {
            out.println("<ul>");
            for (Cookie c : cookies) {
                out.println("<li><b>" + c.getName() + "</b>: " + c.getValue() + "</li>");
            }
            out.println("</ul>");
        } else {
            out.println("<p>Khong tim thay Cookie nao!</p>");
        }
    }

    // 3. Xóa Cookie khoi Browser
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Tạo Cookie trùng tên, gán MaxAge = 0
        Cookie removeTheme = new Cookie("app_theme", "");
        removeTheme.setMaxAge(0);
        removeTheme.setPath("/");

        response.addCookie(removeTheme);
        response.getWriter().println("Da xoa Cookie app_theme!");
    }
}

```

---