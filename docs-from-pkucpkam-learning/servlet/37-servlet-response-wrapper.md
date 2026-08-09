# 37-servlet-response-wrapper
# Document: `javax.servlet.ServletResponseWrapper`

* **Package:** `javax.servlet`
* **Class:** `public class ServletResponseWrapper extends java.lang.Object implements ServletResponse`
* **Inheritance:** `java.lang.Object` $\rightarrow$ `javax.servlet.ServletResponseWrapper`
* **All Implemented Interfaces:** `javax.servlet.ServletResponse`
* **Direct Known Subclasses:** `javax.servlet.http.HttpServletResponseWrapper`
* **Since:** Servlet 2.3
* **See Also:** `javax.servlet.ServletResponse`

---

## 1. Tổng quan (Overview)

`ServletResponseWrapper` cung cấp một lớp triển khai tiện lợi của interface `ServletResponse`, áp dụng **Decorator Pattern** (hoặc Wrapper Pattern).

Lớp này cho phép lập trình viên dễ dàng mở rộng, sửa đổi hoặc bắt chặn (intercept) phản hồi từ Servlet (ví dụ: bắt giữ nội dung response để nén GZIP, ghi đè header, ghi đè `PrintWriter`/`ServletOutputStream` để biến đổi nội dung HTML/JSON) trước khi trả về cho client.

Theo mặc định, tất cả các phương thức trong `ServletResponseWrapper` đều gọi lại (call through) phương thức tương ứng của đối tượng `ServletResponse` gốc được bọc bên trong.

---

## 2. Tóm tắt Constructor & Phương thức (Summary)

### Constructor Summary

| Constructor | Mô tả |
| --- | --- |
| `ServletResponseWrapper(ServletResponse response)` | Khởi tạo một đối tượng adapter bọc đối tượng `ServletResponse` truyền vào. |

---

### Method Summary

| Kiểu trả về | Phương thức | Mô tả |
| --- | --- | --- |
| `ServletResponse` | `getResponse()` | Trả về đối tượng `ServletResponse` gốc đang được bọc bên trong. |
| `void` | `setResponse(ServletResponse response)` | Thay đổi đối tượng `ServletResponse` đang được bọc. |
| `void` | `flushBuffer()` | Mặc định gọi `flushBuffer()` trên đối tượng response gốc. |
| `int` | `getBufferSize()` | Mặc định gọi `getBufferSize()` trên đối tượng response gốc. |
| `String` | `getCharacterEncoding()` | Mặc định gọi `getCharacterEncoding()` trên đối tượng response gốc. |
| `String` | `getContentType()` | Mặc định gọi `getContentType()` trên đối tượng response gốc. |
| `Locale` | `getLocale()` | Mặc định gọi `getLocale()` trên đối tượng response gốc. |
| `ServletOutputStream` | `getOutputStream()` | Mặc định gọi `getOutputStream()` trên đối tượng response gốc. |
| `PrintWriter` | `getWriter()` | Mặc định gọi `getWriter()` trên đối tượng response gốc. |
| `boolean` | `isCommitted()` | Mặc định gọi `isCommitted()` trên đối tượng response gốc. |
| `void` | `reset()` | Mặc định gọi `reset()` trên đối tượng response gốc. |
| `void` | `resetBuffer()` | Mặc định gọi `resetBuffer()` trên đối tượng response gốc. |
| `void` | `setBufferSize(int size)` | Mặc định gọi `setBufferSize(size)` trên đối tượng response gốc. |
| `void` | `setCharacterEncoding(String charset)` | Mặc định gọi `setCharacterEncoding(charset)` trên đối tượng response gốc. |
| `void` | `setContentLength(int len)` | Mặc định gọi `setContentLength(len)` trên đối tượng response gốc. |
| `void` | `setContentType(String type)` | Mặc định gọi `setContentType(type)` trên đối tượng response gốc. |
| `void` | `setLocale(Locale loc)` | Mặc định gọi `setLocale(loc)` trên đối tượng response gốc. |

---

## 3. Chi tiết Constructor & Các phương thức cốt lõi (Detail)

---

### Constructor Detail

#### `ServletResponseWrapper(ServletResponse response)`

```java
public ServletResponseWrapper(ServletResponse response)

```

* **Mô tả:** Tạo đối tượng `ServletResponseWrapper` bọc lại đối tượng `ServletResponse` gốc.
* **Parameters:** `response` - đối tượng `ServletResponse` cần bọc.
* **Throws:** `java.lang.IllegalArgumentException` nếu `response` truyền vào là `null`.

---

### Wrapper Management Methods

#### 1. `getResponse()`

```java
public ServletResponse getResponse()

```

* **Mô tả:** Trả về đối tượng `ServletResponse` thực tế đang nằm bên trong wrapper.
* **Returns:** Đối tượng `ServletResponse` được bọc.

---

#### 2. `setResponse(ServletResponse response)`

```java
public void setResponse(ServletResponse response)

```

* **Mô tả:** Thiết lập đối tượng `ServletResponse` mới để bọc.
* **Parameters:** `response` - đối tượng response mới.
* **Throws:** `java.lang.IllegalArgumentException` nếu `response` truyền vào là `null`.

---

## 4. Ví dụ triển khai mã nguồn thực tế (Code Examples)

### Ví dụ 1: Tạo CharArrayWriterResponseWrapper để Bắt giữ (Capture) Nội dung Response

Sử dụng `ServletResponseWrapper` (hoặc `HttpServletResponseWrapper`) ghi đè phương thức `getWriter()` để tạm thời lưu trữ toàn bộ nội dung HTML/JSON vào bộ nhớ đệm thay vì ghi trực tiếp ra socket client.

```java
import javax.servlet.ServletResponse;
import javax.servlet.ServletResponseWrapper;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class CharCaptureResponseWrapper extends ServletResponseWrapper {

    private final CharArrayWriter charArrayWriter = new CharArrayWriter();
    private final PrintWriter printWriter = new PrintWriter(charArrayWriter);

    public CharCaptureResponseWrapper(ServletResponse response) {
        super(response);
    }

    // Ghi đè getWriter để trả về PrintWriter ghi dữ liệu vào CharArrayWriter
    @Override
    public PrintWriter getWriter() throws IOException {
        return printWriter;
    }

    // Lấy lại toàn bộ nội dung response dạng String đã ghi
    public String getCapturedOutput() {
        printWriter.flush();
        return charArrayWriter.toString();
    }
}

```

---

### Ví dụ 2: Sử dụng Response Wrapper trong Servlet Filter để Biến đổi Output (Uppercase Output)

Đưa wrapper vào Filter để biến đổi nội dung HTML trước khi trả về cho trình duyệt.

```java
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;
import java.io.PrintWriter;

@WebFilter(urlPatterns = "/transform/*")
public class UpperCaseFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        // 1. Khởi tạo Response Wrapper
        CharCaptureResponseWrapper responseWrapper = new CharCaptureResponseWrapper(response);

        // 2. Chuyển wrapper qua FilterChain cho Servlet xử lý
        chain.doFilter(request, responseWrapper);

        // 3. Lấy dữ liệu output đã capture từ Servlet
        String originalContent = responseWrapper.getCapturedOutput();

        // 4. Biến đổi dữ liệu (Ví dụ: Chuyển toàn bộ thành chữ hoa)
        String transformedContent = originalContent.toUpperCase();

        // 5. Ghi nội dung đã biến đổi ra response thực tế
        response.setContentLength(transformedContent.getBytes(response.getCharacterEncoding()).length);
        PrintWriter realWriter = response.getWriter();
        realWriter.write(transformedContent);
        realWriter.flush();
    }

    @Override
    public void destroy() {}
}

```

---