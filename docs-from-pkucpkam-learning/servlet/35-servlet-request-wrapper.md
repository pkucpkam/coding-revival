# 35-servlet-request-wrapper

# Document: `javax.servlet.ServletRequestWrapper`

* **Package:** `javax.servlet`
* **Class:** `public class ServletRequestWrapper extends java.lang.Object implements ServletRequest`
* **Inheritance:** `java.lang.Object` $\rightarrow$ `javax.servlet.ServletRequestWrapper`
* **All Implemented Interfaces:** `javax.servlet.ServletRequest`
* **Direct Known Subclasses:** `javax.servlet.http.HttpServletRequestWrapper`
* **Since:** Servlet 2.3
* **See Also:** `javax.servlet.ServletRequest`

---

## 1. Tổng quan (Overview)

`ServletRequestWrapper` cung cấp một lớp triển khai tiện lợi của interface `ServletRequest`, áp dụng thiết kế **Wrapper / Decorator Pattern**.

Lớp này cho phép lập trình viên dễ dàng mở rộng và tùy biến hành vi của đối tượng request (chẳng hạn như lọc/sanitise tham số đầu vào, đọc lặp lại request body, hoặc ghi đè thuộc tính) trước khi truyền sang cho Servlet hoặc Filter tiếp theo trong chuỗi xử lý (`FilterChain`).

Theo mặc định, tất cả phương thức của `ServletRequestWrapper` chỉ đơn giản là gọi lại (call through) phương thức tương ứng trên đối tượng `ServletRequest` gốc được bọc bên trong.

---

## 2. Tóm tắt Constructor & Phương thức (Summary)

### Constructor Summary

| Constructor | Mô tả |
| --- | --- |
| `ServletRequestWrapper(ServletRequest request)` | Khởi tạo một đối tượng adapter bọc đối tượng `ServletRequest` được truyền vào. |

---

### Method Summary

| Kiểu trả về | Phương thức | Mô tả |
| --- | --- | --- |
| `ServletRequest` | `getRequest()` | Trả về đối tượng `ServletRequest` gốc đang được bọc bên trong. |
| `void` | `setRequest(ServletRequest request)` | Thiết lập hoặc thay đổi đối tượng `ServletRequest` đang được bọc. |
| `Object` | `getAttribute(String name)` | Mặc định gọi `getAttribute(name)` trên đối tượng request gốc. |
| `Enumeration<String>` | `getAttributeNames()` | Mặc định gọi `getAttributeNames()` trên đối tượng request gốc. |
| `String` | `getCharacterEncoding()` | Mặc định gọi `getCharacterEncoding()` trên đối tượng request gốc. |
| `int` | `getContentLength()` | Mặc định gọi `getContentLength()` trên đối tượng request gốc. |
| `String` | `getContentType()` | Mặc định gọi `getContentType()` trên đối tượng request gốc. |
| `ServletInputStream` | `getInputStream()` | Mặc định gọi `getInputStream()` trên đối tượng request gốc. |
| `String` | `getLocalAddr()` | Mặc định gọi `getLocalAddr()` trên đối tượng request gốc. |
| `Locale` | `getLocale()` | Mặc định gọi `getLocale()` trên đối tượng request gốc. |
| `Enumeration<Locale>` | `getLocales()` | Mặc định gọi `getLocales()` trên đối tượng request gốc. |
| `String` | `getLocalName()` | Mặc định gọi `getLocalName()` trên đối tượng request gốc. |
| `int` | `getLocalPort()` | Mặc định gọi `getLocalPort()` trên đối tượng request gốc. |
| `String` | `getParameter(String name)` | Mặc định gọi `getParameter(name)` trên đối tượng request gốc. |
| `Map<String, String[]>` | `getParameterMap()` | Mặc định gọi `getParameterMap()` trên đối tượng request gốc. |
| `Enumeration<String>` | `getParameterNames()` | Mặc định gọi `getParameterNames()` trên đối tượng request gốc. |
| `String[]` | `getParameterValues(String name)` | Mặc định gọi `getParameterValues(name)` trên đối tượng request gốc. |
| `String` | `getProtocol()` | Mặc định gọi `getProtocol()` trên đối tượng request gốc. |
| `BufferedReader` | `getReader()` | Mặc định gọi `getReader()` trên đối tượng request gốc. |
| `String` | `getRealPath(String path)` | *(Deprecated)* Mặc định gọi `getRealPath(path)` trên đối tượng request gốc. |
| `String` | `getRemoteAddr()` | Mặc định gọi `getRemoteAddr()` trên đối tượng request gốc. |
| `String` | `getRemoteHost()` | Mặc định gọi `getRemoteHost()` trên đối tượng request gốc. |
| `int` | `getRemotePort()` | Mặc định gọi `getRemotePort()` trên đối tượng request gốc. |
| `RequestDispatcher` | `getRequestDispatcher(String path)` | Mặc định gọi `getRequestDispatcher(path)` trên đối tượng request gốc. |
| `String` | `getScheme()` | Mặc định gọi `getScheme()` trên đối tượng request gốc. |
| `String` | `getServerName()` | Mặc định gọi `getServerName()` trên đối tượng request gốc. |
| `int` | `getServerPort()` | Mặc định gọi `getServerPort()` trên đối tượng request gốc. |
| `boolean` | `isSecure()` | Mặc định gọi `isSecure()` trên đối tượng request gốc. |
| `void` | `removeAttribute(String name)` | Mặc định gọi `removeAttribute(name)` trên đối tượng request gốc. |
| `void` | `setAttribute(String name, Object o)` | Mặc định gọi `setAttribute(name, o)` trên đối tượng request gốc. |
| `void` | `setCharacterEncoding(String enc)` | Mặc định gọi `setCharacterEncoding(enc)` trên đối tượng request gốc. |

---

## 3. Chi tiết Constructor & Các phương thức cốt lõi (Detail)

---

### Constructor Detail

#### `ServletRequestWrapper(ServletRequest request)`

```java
public ServletRequestWrapper(ServletRequest request)

```

* **Mô tả:** Tạo một adapter `ServletRequestWrapper` bọc lại đối tượng `ServletRequest` gốc.
* **Parameters:** `request` - đối tượng `ServletRequest` cần bọc.
* **Throws:** `java.lang.IllegalArgumentException` nếu `request` truyền vào là `null`.

---

### Wrapper Management Methods

#### 1. `getRequest()`

```java
public ServletRequest getRequest()

```

* **Mô tả:** Lấy đối tượng `ServletRequest` thực tế đang nằm bên trong wrapper.
* **Returns:** Đối tượng `ServletRequest` đang được bọc.

---

#### 2. `setRequest(ServletRequest request)`

```java
public void setRequest(ServletRequest request)

```

* **Mô tả:** Thay thế đối tượng `ServletRequest` đang bọc bằng một thể hiện `ServletRequest` mới.
* **Parameters:** `request` - đối tượng request mới.
* **Throws:** `java.lang.IllegalArgumentException` nếu `request` là `null`.

---

## 4. Ví dụ triển khai mã nguồn thực tế (Code Examples)

### Ví dụ 1: Tùy biến loại bỏ khoảng trắng dư thừa (Trim Space) trong Form Parameters

Bằng cách kế thừa `ServletRequestWrapper` (hoặc `HttpServletRequestWrapper`), ta có thể ghi đè các hàm `getParameter`, `getParameterValues` để tự động làm sạch (trim) dữ liệu gửi lên từ form.

```java
import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestWrapper;

public class TrimmedRequestWrapper extends ServletRequestWrapper {

    public TrimmedRequestWrapper(ServletRequest request) {
        super(request);
    }

    @Override
    public String getParameter(String name) {
        String value = super.getParameter(name);
        return value != null ? value.trim() : null;
    }

    @Override
    public String[] getParameterValues(String name) {
        String[] values = super.getParameterValues(name);
        if (values == null) {
            return null;
        }

        String[] trimmedValues = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            trimmedValues[i] = (values[i] != null) ? values[i].trim() : null;
        }
        return trimmedValues;
    }
}

```

---

### Ví dụ 2: Áp dụng Request Wrapper trong Servlet Filter

Đưa `TrimmedRequestWrapper` vào `Filter` để tất cả các Servlet phía sau đều nhận được tham số đã làm sạch dữ liệu.

```java
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;

@WebFilter(urlPatterns = "/*")
public class ParameterTrimFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Khởi tạo filter
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        // Bọc request gốc bằng wrapper tùy biến
        TrimmedRequestWrapper wrappedRequest = new TrimmedRequestWrapper(request);

        // Chuyển đối tượng wrappedRequest tiếp tục qua FilterChain
        chain.doFilter(wrappedRequest, response);
    }

    @Override
    public void destroy() {
        // Dọn dẹp tài nguyên
    }
}

```

---