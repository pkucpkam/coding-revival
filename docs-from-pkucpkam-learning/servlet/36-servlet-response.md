# 36-servlet-response

# Document: `javax.servlet.ServletResponse`

* **Package:** `javax.servlet`
* **Interface:** `public interface ServletResponse`
* **All Known Subinterfaces:** `javax.servlet.http.HttpServletResponse`
* **All Known Implementing Classes:** `javax.servlet.ServletResponseWrapper`, `javax.servlet.http.HttpServletResponseWrapper`
* **See Also:** `javax.servlet.ServletOutputStream`, `javax.servlet.http.HttpServletResponse`

---

## 1. Tổng quan (Overview)

Interface `ServletResponse` định nghĩa đối tượng hỗ trợ Servlet trong việc gửi phản hồi (response) về cho Web Client. Servlet Container tự động tạo đối tượng `ServletResponse` và truyền nó làm tham số vào phương thức `service()` của Servlet.

### Các cơ chế gửi dữ liệu chính:

1. **Dữ liệu nhị phân (Binary Data):** Sử dụng `ServletOutputStream` lấy từ phương thức `getOutputStream()`.
2. **Dữ liệu văn bản/ký tự (Character/Text Data):** Sử dụng `PrintWriter` lấy từ phương thức `getWriter()`.
3. **Dữ liệu kết hợp (Multipart):** Sử dụng `ServletOutputStream` và tự quản lý các phần ký tự thủ công.

### Quy tắc quan trọng về Character Encoding & Content Type:

* Bộ mã hóa ký tự (charset) có thể được chỉ định rõ ràng qua `setCharacterEncoding(String)` hoặc `setContentType(String)`, hoặc ngầm định qua `setLocale(Locale)`.
* Thiết lập rõ ràng (`setCharacterEncoding`, `setContentType`) luôn có độ ưu tiên cao hơn thiết lập ngầm định (`setLocale`).
* **Mặc định:** Nếu không khai báo charset, container sẽ dùng **`ISO-8859-1`**.
* **Thời điểm gọi:** Các hàm `setCharacterEncoding`, `setContentType`, hoặc `setLocale` **phải được gọi TRƯỚC HÀM `getWriter()**` và trước khi response bị committed để charset có hiệu lực.
* **Quy tắc luồng duy nhất:** Trong cùng một response, bạn chỉ được gọi **`getOutputStream()`** HOẶC **`getWriter()`**, gọi cả hai sẽ ném ra `IllegalStateException`.

---

## 2. Tóm tắt Phương thức (Method Summary)

| Kiểu trả về | Phương thức | Mô tả |
| --- | --- | --- |
| `void` | `flushBuffer()` | Ép tất cả dữ liệu còn trong bộ đệm (buffer) ghi ra client và tự động commit response. |
| `int` | `getBufferSize()` | Trả về kích thước bộ đệm thực tế được sử dụng cho response (trả về `0` nếu không dùng buffer). |
| `String` | `getCharacterEncoding()` | Trả về tên bảng mã mã hóa ký tự (MIME charset) đang dùng cho body response. |
| `String` | `getContentType()` | Trả về kiểu MIME content-type của response (bao gồm cả tham số charset nếu có). |
| `Locale` | `getLocale()` | Trả về `Locale` được chỉ định cho response qua `setLocale()`. |
| `ServletOutputStream` | `getOutputStream()` | Trả về `ServletOutputStream` thích hợp để ghi dữ liệu nhị phân. |
| `PrintWriter` | `getWriter()` | Trả về đối tượng `PrintWriter` để gửi chuỗi văn bản/ký tự ra client. |
| `boolean` | `isCommitted()` | Kiểm tra xem response đã bị committed (đã ghi status code và header về client) chưa. |
| `void` | `reset()` | Xóa toàn bộ dữ liệu trong buffer, đồng thời xóa sạch status code và response headers. |
| `void` | `resetBuffer()` | Chỉ xóa dữ liệu trong buffer mà không làm ảnh hưởng tới headers hay status code. |
| `void` | `setBufferSize(int size)` | Thiết lập kích thước bộ đệm ưu tiên cho body của response. |
| `void` | `setCharacterEncoding(String charset)` | Thiết lập trực tiếp bảng mã ký tự (MIME charset) cho response (ví dụ: `"UTF-8"`). |
| `void` | `setContentLength(int len)` | Thiết lập độ dài dữ liệu (bytes) của body response (tương ứng HTTP header `Content-Length`). |
| `void` | `setContentType(String type)` | Thiết lập Content-Type (MIME type) cho response gửi tới client (ví dụ: `"text/html; charset=UTF-8"`). |
| `void` | `setLocale(Locale loc)` | Thiết lập `Locale` cho response và tự động gắn charset tương ứng nếu chưa được thiết lập. |

---

## 3. Chi tiết Tất cả các phương thức (Detail)

---

### 1. `getCharacterEncoding()` & `setCharacterEncoding(String charset)`

```java
public String getCharacterEncoding()
public void setCharacterEncoding(String charset)

```

* **Mô tả:**
* `setCharacterEncoding`: Khai báo bảng mã mã hóa ký tự (MIME charset) gửi về client (như `"UTF-8"`). Nếu đã gọi `setContentType` hoặc `setLocale` trước đó, phương thức này sẽ ghi đè charset. Phương thức không có hiệu lực nếu gọi **sau khi** đã gọi `getWriter()` hoặc khi response đã `committed`.
* `getCharacterEncoding`: Lấy tên bảng mã hiện tại. Trả về `"ISO-8859-1"` nếu chưa khai báo.



---

### 2. `getContentType()` & `setContentType(String type)`

```java
public String getContentType()
public void setContentType(String type)

```

* **Mô tả:**
* `setContentType`: Thiết lập kiểu MIME của nội dung (ví dụ: `"application/json"`, `"text/html; charset=UTF-8"`). Nếu chuỗi truyền vào chứa khai báo `charset=...`, charset của response sẽ được cập nhật tương ứng (nếu chưa gọi `getWriter()`).
* `getContentType`: Trả về chuỗi MIME type đầy đủ kèm charset (nếu có), hoặc `null` nếu chưa thiết lập.



---

### 3. `getOutputStream()` vs `getWriter()`

```java
public ServletOutputStream getOutputStream() throws IOException
public PrintWriter getWriter() throws IOException

```

* **Mô tả:**
* `getOutputStream()`: Trả về `ServletOutputStream` dùng để xuất luồng dữ liệu nhị phân (binary stream). Calling `flush()` trên stream này sẽ commit response.
* `getWriter()`: Trả về `PrintWriter` xuất dữ liệu văn bản theo bảng mã của `getCharacterEncoding()`. Calling `flush()` trên writer sẽ commit response.


* **Throws:** `IllegalStateException` nếu phương thức còn lại đã được gọi trước đó trên cùng đối tượng response.

---

### 4. `setBufferSize(int size)`, `getBufferSize()`, `flushBuffer()`, `resetBuffer()`, `reset()`, `isCommitted()`

```java
public void setBufferSize(int size)
public int getBufferSize()
public void flushBuffer() throws IOException
public void resetBuffer()
public void reset()
public boolean isCommitted()

```

* **Cơ chế Buffer & Commit:**
* **Bộ đệm lớn (`setBufferSize`):** Giúp ghi nhiều nội dung trước khi đẩy về client, cho phép Servlet có thời gian đổi header/status code. Phải gọi **trước khi** ghi nội dung body.
* `isCommitted()`: Trả về `true` nếu status code và header đã được đẩy ra mạng về client.
* `flushBuffer()`: Đẩy toàn bộ dữ liệu trong bộ đệm ra client và đánh dấu response là committed.
* `resetBuffer()`: Xóa sạch dữ liệu bộ đệm chưa đẩy ra client (headers & status code giữ nguyên). Throws `IllegalStateException` nếu đã committed.
* `reset()`: Xóa sạch cả dữ liệu bộ đệm lẫn headers và status code. Throws `IllegalStateException` nếu đã committed.



---

### 5. `setLocale(Locale loc)` & `getLocale()`

```java
public void setLocale(Locale loc)
public Locale getLocale()

```

* **Mô tả:** Thiết lập ngôn ngữ/vùng miền cho response. Trong HTTP, `setLocale` truyền thông tin qua header `Content-Language` và tự động mapping sang charset phù hợp cho `Content-Type` nếu charset chưa được khai báo thủ công.

---

## 4. Ví dụ triển khai mã nguồn thực tế (Code Examples)

### Ví dụ 1: Trả về Văn bản HTML UTF-8 đúng chuẩn bằng `getWriter()`

Thiết lập đúng thứ tự `setContentType` / `setCharacterEncoding` **trước khi** lấy `getWriter()` để tránh lỗi phông chữ tiếng Việt.

```java
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/hello-utf8")
public class Utf8ResponseServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        // 1. Khai báo ContentType VÀ CharacterEncoding TRƯỚC KHI gọi getWriter()
        resp.setContentType("text/html; charset=UTF-8");
        // Hoặc: resp.setCharacterEncoding("UTF-8");

        // 2. Lấy PrintWriter sau khi đã cài đặt encoding
        PrintWriter out = resp.getWriter();

        out.println("<!DOCTYPE html>");
        out.println("<html><head><title>Demo ServletResponse</title></head><body>");
        out.println("<h1>Xin chào! Đây là nội dung hiển thị tiếng Việt UTF-8 chuẩn.</h1>");
        out.println("<p>Bảng mã đang sử dụng: <b>" + resp.getCharacterEncoding() + "</b></p>");
        out.println("<p>Content-Type: <b>" + resp.getContentType() + "</b></p>");
        out.println("</body></html>");
    }
}

```

---

### Ví dụ 2: Xử lý Buffer và Reset Response khi xảy ra lỗi đột xuất

Sử dụng `resetBuffer()` hoặc `reset()` để xóa bỏ nội dung dở dang trong bộ đệm khi phát sinh lỗi hệ thống giữa chừng.

```java
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/buffer-demo")
public class BufferManagementServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        resp.setContentType("text/html; charset=UTF-8");
        // Thiết lập bộ đệm 8KB
        resp.setBufferSize(8192);

        PrintWriter out = resp.getWriter();
        out.println("<p>Bắt đầu tính toán dữ liệu báo cáo...</p>");

        try {
            // Giả lập logic tính toán bị lỗi nửa chừng
            boolean hasError = true;
            if (hasError) {
                throw new RuntimeException("Lỗi kết nối Cơ sở dữ liệu giữa chừng!");
            }

            out.println("<p>Tính toán thành công! Dữ liệu đã sẵn sàng.</p>");

        } catch (Exception e) {
            // Kiểm tra xem response đã lỡ committed chưa
            if (!resp.isCommitted()) {
                // Xóa bỏ đoạn HTML "<p>Bắt đầu tính toán...</p>" đang nằm tạm trong buffer
                resp.resetBuffer(); 
                
                // Ghi lại nội dung thông báo lỗi sạch sẽ
                out.println("<h2 style='color:red;'>Đã xảy ra lỗi trong quá trình xử lý!</h2>");
                out.println("<p>Chi tiết: " + e.getMessage() + "</p>");
            }
        }
    }
}

```

---