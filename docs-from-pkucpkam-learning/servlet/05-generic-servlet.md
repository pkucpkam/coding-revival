# 05-generic-servlet


# Document: `javax.servlet.GenericServlet`

* **Package:** `javax.servlet`
* **Class:** `public abstract class GenericServlet`
* **Inheritance:** `java.lang.Object` $\rightarrow$ `javax.servlet.GenericServlet`
* **All Implemented Interfaces:** `java.io.Serializable`, `javax.servlet.Servlet`, `javax.servlet.ServletConfig`
* **Direct Known Subclasses:** `javax.servlet.http.HttpServlet`

---

## 1. Tổng quan (Overview)

`GenericServlet` định nghĩa một servlet **độc lập với giao thức** (protocol-independent). Để viết một servlet phục vụ giao thức HTTP trên web, lập trình viên thường mở rộng (extend) lớp con `HttpServlet` thay vì kế thừa trực tiếp lớp này.

### Đặc điểm và vai trò chính:

1. **Thực thi các Interfaces:** `GenericServlet` cài đặt sẵn cả hai interface `Servlet` và `ServletConfig`.
2. **Đơn giản hóa việc viết Servlet:** Lớp này cung cấp các triển khai mặc định, đơn giản cho các phương thức vòng đời (`init` và `destroy`) cũng như các phương thức trong `ServletConfig`.
3. **Tích hợp Logging:** Cung cấp sẵn các phương thức `log()` (ủy quyền xử lý cho `ServletContext`).
4. **Abstract Method:** Để tạo một generic servlet, bạn chỉ cần ghi đè (override) duy nhất phương thức abstract `service(ServletRequest req, ServletResponse res)`.

---

## 2. Tóm tắt Constructor & Phương thức (Summary)

### Constructor Summary

| Constructor | Mô tả |
| --- | --- |
| `GenericServlet()` | Khởi tạo mặc định (không thực hiện tác vụ nào; việc khởi tạo servlet được đảm nhận bởi phương thức `init`). |

---

### Method Summary

| Kiểu trả về | Phương thức | Mô tả |
| --- | --- | --- |
| `void` | `destroy()` | Được servlet container gọi để thông báo rằng servlet sắp bị dừng dịch vụ. |
| `String` | `getInitParameter(String name)` | Trả về giá trị của tham số khởi tạo theo tên, hoặc `null` nếu không tồn tại. |
| `Enumeration<String>` | `getInitParameterNames()` | Trả về danh sách tất cả các tên tham số khởi tạo dưới dạng `Enumeration`. |
| `ServletConfig` | `getServletConfig()` | Trả về đối tượng `ServletConfig` đang khởi tạo servlet này. |
| `ServletContext` | `getServletContext()` | Trả về tham chiếu đến `ServletContext` nơi servlet đang chạy. |
| `String` | `getServletInfo()` | Trả về thông tin về servlet (tác giả, phiên bản, bản quyền). Mặc định trả về chuỗi rỗng. |
| `String` | `getServletName()` | Trả về tên của thể hiện (instance) servlet này. |
| `void` | `init()` | Phương thức tiện ích hỗ trợ ghi đè để khởi tạo cấu hình mà không cần gọi `super.init(config)`. |
| `void` | `init(ServletConfig config)` | Được servlet container gọi để đưa servlet vào hoạt động. |
| `void` | `log(String msg)` | Ghi một thông điệp vào file log của servlet (được tiền tố bởi tên của servlet). |
| `void` | `log(String message, Throwable t)` | Ghi thông điệp giải thích cùng stack trace của ngoại lệ vào file log. |
| `abstract void` | `service(ServletRequest req, ServletResponse res)` | Phương thức trừu tượng được container gọi để xử lý và phản hồi request từ client. |

---

## 3. Chi tiết Constructor & Tất cả các phương thức (Detail)

---

### Constructor Detail

#### `GenericServlet()`

* **Cú pháp:** `public GenericServlet()`
* **Mô tả:** Constructor mặc định không thực hiện tác vụ nào. Toàn bộ quá trình khởi tạo cấu hình cho servlet được thực hiện thông qua các phương thức `init`.

---

### Method Detail

#### 1. `init(ServletConfig config)`

* **Cú pháp:** `public void init(ServletConfig config) throws ServletException`
* **Mô tả:** Được servlet container gọi để thông báo rằng servlet bắt đầu được đưa vào hoạt động. Triển khai mặc định này lưu giữ đối tượng `ServletConfig` để sử dụng sau này. Nếu ghi đè phương thức này, bạn bắt buộc phải gọi `super.init(config)`.
* **Specified by:** `init` trong interface `Servlet`.
* **Parameters:** `config` - đối tượng `ServletConfig` chứa cấu hình của servlet.
* **Throws:** `ServletException` nếu xảy ra lỗi gián đoạn hoạt động bình thường của servlet.

---

#### 2. `init()`

* **Cú pháp:** `public void init() throws ServletException`
* **Mô tả:** Phương thức tiện ích được thiết kế để lập trình viên ghi đè (override) khi muốn viết code khởi tạo mà **không cần gọi `super.init(config)**`. Phương thức này tự động được gọi từ bên trong `GenericServlet.init(ServletConfig config)`. Bạn vẫn có thể truy cập `ServletConfig` thông qua `getServletConfig()`.
* **Throws:** `ServletException` nếu xảy ra lỗi khởi tạo.

---

#### 3. `service(ServletRequest req, ServletResponse res)`

* **Cú pháp:** `public abstract void service(ServletRequest req, ServletResponse res) throws ServletException, java.io.IOException`
* **Mô tả:** Phương thức trừu tượng chính được servlet container gọi để xử lý request từ client. Các lớp con (như `HttpServlet` hoặc một Custom Protocol Servlet) bắt buộc phải triển khai phương thức này.
* **Specified by:** `service` trong interface `Servlet`.
* **Parameters:**
* `req`: Đối tượng `ServletRequest` chứa thông tin request từ client.
* `res`: Đối tượng `ServletResponse` chứa thông tin phản hồi trả về client.


* **Throws:** `ServletException`, `java.io.IOException`.

---

#### 4. `destroy()`

* **Cú pháp:** `public void destroy()`
* **Mô tả:** Được servlet container gọi để đánh dấu servlet dừng hoạt động và ngưng phục vụ request.
* **Specified by:** `destroy` trong interface `Servlet`.

---

#### 5. `getInitParameter(String name)`

* **Cú pháp:** `public String getInitParameter(String name)`
* **Mô tả:** Phương thức tiện ích giúp lấy giá trị tham số khởi tạo trực tiếp từ đối tượng `ServletConfig` của servlet.
* **Specified by:** `getInitParameter` trong interface `ServletConfig`.
* **Parameters:** `name` - tên tham số khởi tạo.
* **Returns:** Giá trị dạng `String`, hoặc `null` nếu không tồn tại.

---

#### 6. `getInitParameterNames()`

* **Cú pháp:** `public java.util.Enumeration getInitParameterNames()`
* **Mô tả:** Trả về danh sách tất cả các tên tham số khởi tạo từ đối tượng `ServletConfig`.
* **Specified by:** `getInitParameterNames` trong interface `ServletConfig`.
* **Returns:** `Enumeration` chứa tập hợp tên các tham số.

---

#### 7. `getServletConfig()`

* **Cú pháp:** `public ServletConfig getServletConfig()`
* **Mô tả:** Trả về đối tượng `ServletConfig` đã được truyền vào trong quá trình khởi tạo.
* **Specified by:** `getServletConfig` trong interface `Servlet`.
* **Returns:** Đối tượng `ServletConfig`.

---

#### 8. `getServletContext()`

* **Cú pháp:** `public ServletContext getServletContext()`
* **Mô tả:** Phương thức tiện ích lấy đối tượng `ServletContext` trực tiếp từ `ServletConfig`.
* **Specified by:** `getServletContext` trong interface `ServletConfig`.
* **Returns:** Đối tượng `ServletContext`.

---

#### 9. `getServletInfo()`

* **Cú pháp:** `public String getServletInfo()`
* **Mô tả:** Trả về thông tin về servlet (tác giả, phiên bản, bản quyền). Mặc định trả về chuỗi rỗng `""`. Lập trình viên có thể ghi đè để cung cấp thông tin có nghĩa.
* **Specified by:** `getServletInfo` trong interface `Servlet`.

---

#### 10. `getServletName()`

* **Cú pháp:** `public String getServletName()`
* **Mô tả:** Trả về tên đại diện của Servlet instance này (được cấu hình trong `web.xml` hoặc `@WebServlet`).
* **Specified by:** `getServletName` trong interface `ServletConfig`.

---

#### 11. `log(String msg)`

* **Cú pháp:** `public void log(String msg)`
* **Mô tả:** Ghi tin nhắn được chỉ định vào file log của servlet container, có gắn tiền tố là tên của Servlet (`getServletName()`).
* **Parameters:** `msg` - nội dung tin nhắn cần ghi log.

---

#### 12. `log(String message, Throwable t)`

* **Cú pháp:** `public void log(String message, Throwable t)`
* **Mô tả:** Ghi thông điệp giải thích cùng với thông tin vết lỗi (stack trace) của ngoại lệ `Throwable` vào file log của container.
* **Parameters:**
* `message`: Mô tả lỗi hoặc sự kiện.
* `t`: Đối tượng ngoại lệ `Throwable`.



---

## 4. Ví dụ triển khai mã nguồn thực tế (Code Examples)

### Ví dụ 1: Tự tạo một Protocol-Independent Servlet với `GenericServlet`

Dưới đây là ví dụ triển khai một Servlet độc lập giao thức kế thừa trực tiếp từ `GenericServlet` để xử lý các yêu cầu không phụ thuộc riêng vào HTTP.

```java
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.annotation.WebInitParam;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(
    urlPatterns = "/generic-echo",
    name = "EchoGenericServlet",
    initParams = {
        @WebInitParam(name = "adminEmail", value = "admin@example.com")
    }
)
public class EchoGenericServlet extends GenericServlet {

    private String adminEmail;

    // 1. Ghi đè phương thức init() không tham số (tiện ích của GenericServlet)
    @Override
    public void init() throws ServletException {
        // Đọc tham số khởi tạo mà không cần gọi super.init(config)
        this.adminEmail = getInitParameter("adminEmail");
        
        // Ghi log qua phương thức log() tích hợp sẵn
        log("EchoGenericServlet da khoi tao voi Admin Email: " + adminEmail);
    }

    // 2. Bắt buộc triển khai phương thức trừu tượng service()
    @Override
    public void service(ServletRequest req, ServletResponse res) 
            throws ServletException, IOException {
        
        res.setContentType("text/plain; charset=UTF-8");
        PrintWriter out = res.getWriter();

        out.println("=== GENERIC SERVLET RESPONSE ===");
        out.println("Servlet Name: " + getServletName());
        out.println("Server Info: " + getServletContext().getServerInfo());
        out.println("Admin Contact: " + this.adminEmail);
        out.println("Remote Address: " + req.getRemoteAddr());
        
        log("Da xu ly xong 1 request tu client: " + req.getRemoteAddr());
    }

    // 3. Ghi đè destroy() để dọn dẹp
    @Override
    public void destroy() {
        log("EchoGenericServlet dang bi ngung hoat dong.");
    }
}

```

---

### Ví dụ 2: Xử lý ngoại lệ và Ghi log lỗi bằng `log(String, Throwable)`

Minh họa việc bắt ngoại lệ trong quá trình thực thi và ghi vết lỗi vào log tập trung của container thông qua phương thức `log()`.

```java
import javax.servlet.*;
import java.io.IOException;

public class DataProcessingServlet extends GenericServlet {

    @Override
    public void service(ServletRequest req, ServletResponse res) 
            throws ServletException, IOException {
        
        try {
            // Giả lập đoạn code đọc dữ liệu có thể phát sinh lỗi
            String rawData = req.getParameter("data");
            if (rawData == null) {
                throw new IllegalArgumentException("Tham so 'data' khong duoc de null!");
            }
            
            res.getWriter().write("Du lieu nhan duoc: " + rawData);

        } catch (Exception e) {
            // Ghi log ngoại lệ cùng stack trace vào log file của Container
            log("Loi xay ra khi xu ly request trong " + getServletName(), e);

            // Ném lỗi ra ServletException để Container chuyển sang trang báo lỗi
            throw new ServletException("Loi he thong noi bo", e);
        }
    }

    @Override
    public String getServletInfo() {
        return "DataProcessingServlet - Version 1.0 - Author: DevTeam";
    }
}

```