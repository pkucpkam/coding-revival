# 28-servlet-input-stream

# Document: `javax.servlet.ServletInputStream`

* **Package:** `javax.servlet`
* **Class:** `public abstract class ServletInputStream extends java.io.InputStream`
* **Inheritance:** `java.lang.Object` $\rightarrow$ `java.io.InputStream` $\rightarrow$ `javax.servlet.ServletInputStream`
* **See Also:** `javax.servlet.ServletRequest`, `javax.servlet.ServletRequest.getInputStream()`

---

## 1. Tổng quan (Overview)

`ServletInputStream` cung cấp một luồng nhập (input stream) để đọc dữ liệu nhị phân (binary data) gửi từ client request, bao gồm phương thức `readLine` tối ưu để đọc dữ liệu theo từng dòng một.

Đối với một số giao thức như **HTTP POST** và **HTTP PUT**, đối tượng `ServletInputStream` được sử dụng để đọc dữ liệu thân yêu cầu (request body) gửi từ client (ví dụ: JSON, XML, file upload).

### Các điểm cần lưu ý:

1. **Lấy đối tượng:** Thường được lấy thông qua phương thức `ServletRequest.getInputStream()`.
2. **Lớp trừu tượng (Abstract Class):** Đây là một abstract class do Servlet Container (như Apache Tomcat) triển khai. Các lớp con bắt buộc phải triển khai phương thức `java.io.InputStream.read()`.
3. **Quy tắc luồng đọc:** Trong một request, bạn chỉ có thể gọi **`request.getInputStream()`** HOẶC **`request.getReader()`**, không thể gọi cả hai (sẽ quăng `IllegalStateException`).

---

## 2. Tóm tắt Constructor & Phương thức (Summary)

### Constructor Summary

| Constructor | Mô tả |
| --- | --- |
| `protected ServletInputStream()` | Constructor protected không làm gì cả vì đây là một abstract class. |

---

### Method Summary

| Kiểu trả về | Phương thức | Mô tả |
| --- | --- | --- |
| `int` | `readLine(byte[] b, int off, int len)` | Đọc luồng nhập theo từng dòng một vào mảng byte. |

| Phương thức Kế thừa từ `java.io.InputStream` |
| --- |
| `available()`, `close()`, `mark(int readlimit)`, `markSupported()`, `read()`, `read(byte[] b)`, `read(byte[] b, int off, int len)`, `reset()`, `skip(long n)` |

---

## 3. Chi tiết Constructor & Tất cả các phương thức (Detail)

---

### Constructor Detail

#### `protected ServletInputStream()`

* **Cú pháp:** `protected ServletInputStream()`
* **Mô tả:** Constructor protected mặc định cho các lớp con triển khai.

---

### Method Detail

#### `readLine(byte[] b, int off, int len)`

```java
public int readLine(byte[] b, int off, int len) throws java.io.IOException

```

* **Mô tả:** Đọc luồng dữ liệu từng dòng một. Bắt đầu từ vị trí offset (`off`), đọc các byte vào mảng `b` cho đến khi đọc đủ số lượng `len` byte tối đa hoặc gặp ký tự xuống dòng (`\n` hoặc `\r\n`). Ký tự xuống dòng cũng sẽ được đọc đính kèm vào mảng byte.
* **Parameters:**
* `b`: Mảng `byte[]` chứa dữ liệu đọc được.
* `off`: Vị trí chỉ mục (offset) bắt đầu ghi trong mảng byte.
* `len`: Số byte tối đa được đọc.


* **Returns:** Số byte thực tế đã đọc được, hoặc `-1` nếu đạt đến cuối luồng (EOF) trước khi đọc được byte nào.
* **Throws:** `java.io.IOException` nếu xảy ra lỗi I/O trong quá trình đọc.

---

## 4. Ví dụ triển khai mã nguồn thực tế (Code Examples)

### Ví dụ 1: Đọc Raw Payload (JSON/XML) từ Request Body bằng `ServletInputStream`

Khi client gửi payload dữ liệu (như JSON) trong body của HTTP POST/PUT request, bạn có thể dùng `ServletInputStream` để đọc toàn bộ mảng byte thô.

```java
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@WebServlet("/api/raw-payload")
public class RawPayloadServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        // 1. Lấy ServletInputStream từ HttpServletRequest
        ServletInputStream inputStream = req.getInputStream();

        ByteArrayOutputStream resultBuffer = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;

        // 2. Đọc luồng dữ liệu thô
        while ((length = inputStream.read(buffer)) != -1) {
            resultBuffer.write(buffer, 0, length);
        }

        // 3. Chuyển đổi dữ liệu byte thành Chuỗi String theo mã hóa UTF-8
        String rawBody = resultBuffer.toString(StandardCharsets.UTF_8.name());

        System.out.println("[REQUEST BODY RECEIVED]:\n" + rawBody);

        // Trả về phản hồi
        resp.setContentType("application/json; charset=UTF-8");
        resp.getWriter().write("{\"status\": \"success\", \"bytesRead\": " + resultBuffer.size() + "}");
    }
}

```

---

### Ví dụ 2: Sử dụng phương thức `readLine()` để xử lý tập tin hoặc dữ liệu từng dòng

Minh họa cách duyệt từng dòng dữ liệu nhị phân gửi lên bằng `readLine()`.

```java
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@WebServlet("/read-line-demo")
public class ReadLineDemoServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        ServletInputStream inputStream = req.getInputStream();
        byte[] buffer = new byte[256];
        int bytesRead;
        int lineNumber = 1;

        resp.setContentType("text/plain; charset=UTF-8");

        // Đọc từng dòng dữ liệu bằng readLine()
        while ((bytesRead = inputStream.readLine(buffer, 0, buffer.length)) != -1) {
            String lineText = new String(buffer, 0, bytesRead, StandardCharsets.UTF_8);
            System.out.print("Line " + lineNumber + ": " + lineText);
            lineNumber++;
        }

        resp.getWriter().println("Đã đọc xong tổng cộng " + (lineNumber - 1) + " dòng.");
    }
}

```

---