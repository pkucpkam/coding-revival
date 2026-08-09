# 30-servlet-request

# Document: `javax.servlet.ServletRequest`

* **Package:** `javax.servlet`
* **Interface:** `public interface ServletRequest`
* **All Known Subinterfaces:** `javax.servlet.http.HttpServletRequest`
* **All Known Implementing Classes:** `javax.servlet.ServletRequestWrapper`, `javax.servlet.http.HttpServletRequestWrapper`
* **See Also:** `javax.servlet.http.HttpServletRequest`

---

## 1. Tổng quan (Overview)

Interface `ServletRequest` định nghĩa đối tượng cung cấp thông tin yêu cầu (request) từ client tới Servlet. Servlet Container sẽ tự động tạo đối tượng `ServletRequest` và truyền nó làm tham số vào phương thức `service()` của Servlet.

Đối tượng `ServletRequest` chứa các dữ liệu cốt lõi bao gồm:

* Tham số truyền lên (**Parameter Names & Values**).
* Thuộc tính lưu tạm trong phạm vi Request Scope (**Attributes**).
* Luồng đọc dữ liệu từ Body (**ServletInputStream / BufferedReader**).
* Thông tin kết nối mạng, địa chỉ IP, Port, mã hóa (Character Encoding), Locale.

> **Lưu ý:** Với ứng dụng Web chuẩn HTTP, Servlet Container sẽ truyền một thể hiện triển khai interface mở rộng là **`HttpServletRequest`**.

---

## 2. Tóm tắt Phương thức (Method Summary)

| Kiểu trả về | Phương thức | Mô tả |
| --- | --- | --- |
| `Object` | `getAttribute(String name)` | Trả về giá trị thuộc tính Request Scope có tên chỉ định. |
| `Enumeration<String>` | `getAttributeNames()` | Trả về danh sách tất cả các tên thuộc tính có trong request. |
| `String` | `getCharacterEncoding()` | Trả về tên bảng mã mã hóa ký tự (encoding) dùng trong body của request. |
| `int` | `getContentLength()` | Trả về độ dài (byte) của request body, hoặc `-1` nếu không rõ. |
| `String` | `getContentType()` | Trả về kiểu MIME của request body, hoặc `null` nếu không rõ. |
| `ServletInputStream` | `getInputStream()` | Lấy luồng dữ liệu nhị phân (binary stream) của request body. |
| `String` | `getLocalAddr()` | Trả về địa chỉ IP của giao diện mạng trên Server nhận request. |
| `Locale` | `getLocale()` | Trả về `Locale` ưu tiên của client dựa trên header `Accept-Language`. |
| `Enumeration<Locale>` | `getLocales()` | Trả về danh sách tất cả `Locale` ưu tiên của client theo thứ tự giảm dần. |
| `String` | `getLocalName()` | Trả về tên host của giao diện IP nhận request. |
| `int` | `getLocalPort()` | Trả về số cổng IP trên Server nhận request. |
| `String` | `getParameter(String name)` | Trả về giá trị chuỗi đơn của tham số request theo tên chỉ định. |
| `Map<String, String[]>` | `getParameterMap()` | Trả về một `Map` bất biến chứa tất cả tham số của request (key: tên, value: mảng chuỗi). |
| `Enumeration<String>` | `getParameterNames()` | Trả về danh sách tất cả các tên tham số có trong request. |
| `String[]` | `getParameterValues(String name)` | Trả về mảng chứa tất cả giá trị của một tham số request (khi tham số có nhiều giá trị). |
| `String` | `getProtocol()` | Trả về tên và phiên bản giao thức được dùng (ví dụ: `HTTP/1.1`). |
| `BufferedReader` | `getReader()` | Đọc body của request dưới dạng luồng ký tự (character data). |
| `String` | `getRealPath(String path)` | *(Deprecated)* Thay thế bằng `ServletContext.getRealPath(String)`. |
| `String` | `getRemoteAddr()` | Trả về địa chỉ IP của client hoặc proxy cuối cùng gửi request. |
| `String` | `getRemoteHost()` | Trả về tên miền đầy đủ (FQDN) của client hoặc proxy gửi request. |
| `int` | `getRemotePort()` | Trả về cổng IP nguồn gửi request của client hoặc proxy. |
| `RequestDispatcher` | `getRequestDispatcher(String path)` | Trả về `RequestDispatcher` để điều hướng tới đường dẫn (hỗ trợ đường dẫn tương đối). |
| `String` | `getScheme()` | Trả về tên scheme của request (ví dụ: `http`, `https`, `ftp`). |
| `String` | `getServerName()` | Trả về tên server nhận request. |
| `int` | `getServerPort()` | Trả về số cổng server nhận request. |
| `boolean` | `isSecure()` | Kiểm tra request có được thực hiện qua kênh bảo mật (HTTPS) hay không. |
| `void` | `removeAttribute(String name)` | Xóa một thuộc tính khỏi request. |
| `void` | `setAttribute(String name, Object o)` | Lưu một thuộc tính vào request (Request Scope). |
| `void` | `setCharacterEncoding(String env)` | Thiết lập/ghi đè bảng mã mã hóa ký tự cho request body (phải gọi trước khi đọc parameter). |

---

## 3. Chi tiết Tất cả các phương thức (Detail)

---

### 1. `getParameter(String name)` & `getParameterValues(String name)`

```java
public String getParameter(String name)
public String[] getParameterValues(String name)

```

* **Mô tả:**
* `getParameter`: Lấy giá trị duy nhất của tham số. Nếu tham số gửi nhiều giá trị, phương thức trả về giá trị đầu tiên.
* `getParameterValues`: Lấy tất cả các giá trị dưới dạng mảng `String[]` (ví dụ: checkbox chọn nhiều lựa chọn).


* **Lưu ý:** Nếu request body đã được đọc trực tiếp bằng `getInputStream()` hoặc `getReader()`, dữ liệu POST parameter có thể không còn đọc được qua `getParameter()`.

---

### 2. `getParameterMap()`

```java
public java.util.Map getParameterMap()

```

* **Mô tả:** Trả về một `Map` bất biến (read-only) chứa toàn bộ tham số gửi lên. Key là tên tham số kiểu `String`, Value là mảng các giá trị kiểu `String[]`.

---

### 3. `setAttribute(...)` & `getAttribute(...)`

```java
public void setAttribute(String name, Object o)
public Object getAttribute(String name)

```

* **Mô tả:** Quản lý dữ liệu trong **Request Scope** (tồn tại trong suốt vòng đời của 1 request, thường dùng để chuyển tiếp dữ liệu qua `RequestDispatcher.forward()`).
* **Quy tắc:** Đặt tên nên tuân theo quy ước tên package. Truyền `null` vào `setAttribute` tương đương gọi `removeAttribute`.

---

### 4. `getInputStream()` vs `getReader()`

```java
public ServletInputStream getInputStream() throws IOException
public BufferedReader getReader() throws IOException

```

* **Mô tả:**
* `getInputStream()`: Đọc request body dưới dạng dữ liệu nhị phân (`ServletInputStream`).
* `getReader()`: Đọc request body dưới dạng chuỗi ký tự theo mã hóa character encoding.


* **Quy tắc tuyệt đối:** Trong cùng 1 request, **chỉ được gọi 1 trong 2 phương thức**. Gọi cả hai sẽ ném ra `IllegalStateException`.

---

### 5. `setCharacterEncoding(String env)`

```java
public void setCharacterEncoding(String env) throws UnsupportedEncodingException

```

* **Mô tả:** Thiết lập bảng mã ký tự (như `"UTF-8"`) để giải mã request body và tham số form POST.
* **Quy tắc bắt buộc:** Must be called **trước khi** đọc request parameters (`getParameter`) hoặc đọc dữ liệu từ `getReader()`.

---

### 6. `getRequestDispatcher(String path)`

```java
public RequestDispatcher getRequestDispatcher(String path)

```

* **Mô tả:** Trả về đối tượng `RequestDispatcher` bọc tài nguyên tại đường dẫn truyền vào.
* **Khác biệt so với `ServletContext.getRequestDispatcher`:** Phương thức này của `ServletRequest` **chấp nhận đường dẫn tương đối** (relative path) tính từ vị trí Servlet hiện tại.

---

### 7. `getRemoteAddr()`, `getLocalAddr()`, `getServerName()`

```java
public String getRemoteAddr() // Địa chỉ IP của Client
public String getLocalAddr()  // Địa chỉ IP giao diện Server nhận kết nối
public String getServerName() // Tên host server (từ Host Header hoặc IP)

```

---

## 4. Ví dụ triển khai mã nguồn thực tế (Code Examples)

### Ví dụ 1: Đọc Form Parameters và thiết lập UTF-8 Character Encoding

Đọc dữ liệu tham số từ Form (bao gồm tham số đơn và đa giá trị) đúng chuẩn mã hóa tiếng Việt.

```java
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

@WebServlet("/register")
public class RegisterFormServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        // 1. Thiết lập encoding UTF-8 TRƯỚC KHI đọc bất kỳ parameter nào
        req.setCharacterEncoding("UTF-8");

        // 2. Đọc tham số đơn giá trị
        String fullName = req.getParameter("fullName");
        String email = req.getParameter("email");

        // 3. Đọc tham số đa giá trị (Ví dụ: danh sách Checkbox sở thích)
        String[] hobbies = req.getParameterValues("hobbies");

        resp.setContentType("text/html; charset=UTF-8");
        PrintWriter out = resp.getWriter();

        out.println("<h2>Thông Tin Đăng Ký Received:</h2>");
        out.println("<p>Họ và tên: <b>" + fullName + "</b></p>");
        out.println("<p>Email: <b>" + email + "</b></p>");
        out.println("<p>Sở thích chọn:");
        if (hobbies != null) {
            out.println("<ul>");
            for (String hobby : hobbies) {
                out.println("<li>" + hobby + "</li>");
            }
            out.println("</ul>");
        } else {
            out.println(" <i>Không chọn sở thích nào.</i></p>");
        }

        // 4. Duyệt qua Parameter Map
        out.println("<h3>Tất cả Parameters (Map):</h3>");
        Map<String, String[]> paramMap = req.getParameterMap();
        for (Map.Entry<String, String[]> entry : paramMap.entrySet()) {
            out.println("<p>" + entry.getKey() + " = " + String.join(", ", entry.getValue()) + "</p>");
        }
    }
}

```

---

### Ví dụ 2: Truyền dữ liệu qua Request Scope (`setAttribute`) và `getRequestDispatcher`

Lớp Controller xử lý dữ liệu, lưu vào `ServletRequest` attribute và chuyển tiếp (forward) sang trang JSP hiển thị.

```java
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/user-info")
public class UserInfoServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        // 1. Lấy thông tin mạng từ Request
        String clientIp = req.getRemoteAddr();
        boolean isHttps = req.isSecure();

        // 2. Lưu thông tin vào Request Scope bằng setAttribute
        req.setAttribute("CLIENT_IP", clientIp);
        req.setAttribute("IS_SECURE", isHttps);
        req.setAttribute("SERVER_PORT", req.getServerPort());

        // 3. Lấy RequestDispatcher với đường dẫn tương đối hoặc tuyệt đối
        RequestDispatcher dispatcher = req.getRequestDispatcher("/views/info.jsp");

        // 4. Forward Request + Response tới JSP
        dispatcher.forward(req, resp);
    }
}

```

---