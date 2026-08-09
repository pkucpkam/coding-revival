# 22-servlet-context

# Document: `javax.servlet.ServletContext`

* **Package:** `javax.servlet`
* **Interface:** `public interface ServletContext`
* **See Also:** `Servlet.getServletConfig()`, `ServletConfig.getServletContext()`

---

## 1. Tổng quan (Overview)

`ServletContext` định nghĩa một tập hợp các phương thức mà một Servlet sử dụng để giao tiếp với **Servlet Container** của nó (ví dụ: lấy kiểu MIME của file, điều hướng yêu cầu `RequestDispatcher`, ghi vào file log, hoặc quản lý các thuộc tính phạm vi ứng dụng).

### Các đặc điểm quan trọng:

1. **Một Context trên mỗi Ứng dụng Web:** Có duy nhất **một** đối tượng `ServletContext` cho mỗi ứng dụng web (`web application`) trên mỗi Java Virtual Machine (JVM).
2. **Môi trường Phân tán (Distributed Environment):** Đối với ứng dụng web được đánh dấu là "distributed" trong file deployment descriptor (`web.xml`), sẽ có một instance `ServletContext` cho mỗi JVM. Trong trường hợp này, `ServletContext` **không thể** được dùng làm nơi chia sẻ dữ liệu toàn cục (global) vì dữ liệu sẽ không thực sự đồng bộ giữa các JVM. Hãy sử dụng tài nguyên bên ngoài như Database hoặc Redis.
3. **Cách lấy `ServletContext`:** Đối tượng `ServletContext` được chứa bên trong đối tượng `ServletConfig` mà Web Server cung cấp cho Servlet khi khởi tạo.

---

## 2. Tóm tắt Phương thức (Method Summary)

| Kiểu trả về | Phương thức | Mô tả |
| --- | --- | --- |
| `Object` | `getAttribute(String name)` | Trả về thuộc tính trong ngữ cảnh ứng dụng có tên chỉ định, hoặc `null` nếu không có. |
| `Enumeration<String>` | `getAttributeNames()` | Trả về danh sách tên tất cả các thuộc tính sẵn có trong ngữ cảnh này. |
| `ServletContext` | `getContext(String uripath)` | Trả về đối tượng `ServletContext` tương ứng với một URL/Context Path khác trên server. |
| `String` | `getInitParameter(String name)` | Trả về giá trị tham số khởi tạo dùng chung cho toàn bộ context (`context-param`). |
| `Enumeration<String>` | `getInitParameterNames()` | Trả về tên tất cả các tham số khởi tạo phạm vi context. |
| `int` | `getMajorVersion()` | Trả về phiên bản lớn (Major version) của Java Servlet API mà Container hỗ trợ. |
| `String` | `getMimeType(String file)` | Trả về kiểu MIME của file được chỉ định, hoặc `null` nếu không rõ. |
| `int` | `getMinorVersion()` | Trả về phiên bản nhỏ (Minor version) của Java Servlet API mà Container hỗ trợ. |
| `RequestDispatcher` | `getNamedDispatcher(String name)` | Trả về `RequestDispatcher` bọc một Servlet đã được đặt tên. |
| `String` | `getRealPath(String path)` | Trả về đường dẫn thực tế trên hệ thống file tương ứng với đường dẫn ảo (virtual path). |
| `RequestDispatcher` | `getRequestDispatcher(String path)` | Trả về `RequestDispatcher` bọc tài nguyên nằm tại đường dẫn chỉ định. |
| `URL` | `getResource(String path)` | Trả về đối tượng `URL` trỏ tới tài nguyên được ánh xạ theo đường dẫn. |
| `InputStream` | `getResourceAsStream(String path)` | Trả về tài nguyên dạng `InputStream`. |
| `Set<String>` | `getResourcePaths(String path)` | Trả về danh sách tất cả các đường dẫn tài nguyên bên trong ứng dụng web bắt đầu bằng đường dẫn chỉ định. |
| `String` | `getServerInfo()` | Trả về tên và phiên bản của Servlet Container đang chạy ứng dụng. |
| `Servlet` | `getServlet(String name)` | *(Deprecated)* Luôn trả về `null` kể từ Servlet 2.1. |
| `String` | `getServletContextName()` | Trả về tên hiển thị của ứng dụng web chỉ định trong thẻ `<display-name>` của `web.xml`. |
| `Enumeration<String>` | `getServletNames()` | *(Deprecated)* Luôn trả về Enumeration rỗng kể từ Servlet 2.1. |
| `Enumeration<Servlet>` | `getServlets()` | *(Deprecated)* Luôn trả về Enumeration rỗng kể từ Servlet 2.0. |
| `void` | `log(Exception exception, String msg)` | *(Deprecated)* Thay thế bằng `log(String, Throwable)`. |
| `void` | `log(String msg)` | Ghi thông điệp chỉ định vào file log của Servlet Container. |
| `void` | `log(String message, Throwable throwable)` | Ghi thông điệp và stack trace của ngoại lệ vào file log. |
| `void` | `removeAttribute(String name)` | Xóa thuộc tính có tên chỉ định khỏi `ServletContext`. |
| `void` | `setAttribute(String name, Object object)` | Gán/Ràng buộc một đối tượng vào tên thuộc tính chỉ định trong `ServletContext`. |

---

## 3. Chi tiết Tất cả các phương thức (Detail)

---

### 1. `getContext(String uripath)`

```java
public ServletContext getContext(String uripath)

```

* **Mô tả:** Trả về đối tượng `ServletContext` tương ứng với một Context Path của ứng dụng web khác trên cùng server. Cho phép Servlet truy cập vào context khác và lấy `RequestDispatcher`. Đường dẫn phải bắt đầu bằng `/`. Trong môi trường bảo mật, container có thể trả về `null`.
* **Parameters:** `uripath` - context path của ứng dụng web khác trong container.
* **Returns:** Đối tượng `ServletContext` tương ứng hoặc `null`.

---

### 2. `getMajorVersion()` & `getMinorVersion()`

```java
public int getMajorVersion()
public int getMinorVersion()

```

* **Mô tả:** Trả về phiên bản Servlet API mà container hỗ trợ. Tuân thủ Servlet 2.4 thì `getMajorVersion()` trả về `2`, `getMinorVersion()` trả về `4`.

---

### 3. `getMimeType(String file)`

```java
public String getMimeType(String file)

```

* **Mô tả:** Trả về kiểu MIME đại diện của file (ví dụ: `"text/html"`, `"image/png"`, `"application/pdf"`). Kiểu MIME do cấu hình của container hoặc cấu hình trong `web.xml` quy định.
* **Parameters:** `file` - tên hoặc đường dẫn của file.
* **Returns:** Chuỗi kiểu MIME hoặc `null` nếu không xác định được.

---

### 4. `getResourcePaths(String path)`

```java
public java.util.Set getResourcePaths(String path)

```

* **Mô tả:** Trả về danh sách dạng thư mục chứa tất cả đường dẫn tài nguyên trong ứng dụng web có phần đầu khớp với đường dẫn truyền vào. Đường dẫn thư mục con kết thúc bằng dấu `/`.
* **Ví dụ:** Nếu ứng dụng chứa `/welcome.html`, `/catalog/index.html`, `/catalog/products.html`:
* `getResourcePaths("/")` $\rightarrow$ `{"/welcome.html", "/catalog/", ...}`
* `getResourcePaths("/catalog/")` $\rightarrow$ `{"/catalog/index.html", "/catalog/products.html"}`


* **Since:** Servlet 2.3

---

### 5. `getResource(String path)` & `getResourceAsStream(String path)`

```java
public java.net.URL getResource(String path) throws MalformedURLException
public java.io.InputStream getResourceAsStream(String path)

```

* **Mô tả:** Lấy tài nguyên từ web application mà không cần thông qua Java ClassLoader (khác với `Class.getResource`). Đường dẫn phải bắt đầu bằng `/` và tính từ root context.
* **Lưu ý:** Yêu cầu một trang `.jsp` qua hàm này sẽ trả về **mã nguồn JSP raw**, không phải kết quả HTML đã qua thực thi. Muốn lấy kết quả thực thi hãy dùng `RequestDispatcher`.

---

### 6. `getRequestDispatcher(String path)` & `getNamedDispatcher(String name)`

```java
public RequestDispatcher getRequestDispatcher(String path)
public RequestDispatcher getNamedDispatcher(String name)

```

* **Mô tả:**
* `getRequestDispatcher`: Trả về `RequestDispatcher` bọc tài nguyên tại đường dẫn tương đối tính từ root context (phải bắt đầu bằng `/`).
* `getNamedDispatcher`: Trả về `RequestDispatcher` bọc một Servlet/JSP được định danh bằng tên (tên cấu hình trong `web.xml` hoặc `@WebServlet`).



---

### 7. `getRealPath(String path)`

```java
public String getRealPath(String path)

```

* **Mô tả:** Chuyển đổi đường dẫn ảo (virtual path, ví dụ: `/index.html`) thành đường dẫn tuyệt đối thực tế trên ổ đĩa hệ điều hành (ví dụ: `C:\tomcat\webapps\app\index.html`). Trả về `null` nếu ứng dụng chạy trực tiếp từ file `.war` nén mà không giải nén ra đĩa.

---

### 8. `getServerInfo()`

```java
public String getServerInfo()

```

* **Mô tả:** Trả về tên và phiên bản của Servlet Container (ví dụ: `Apache Tomcat/9.0.85` hoặc `JavaServer Web Dev Kit/1.0`).

---

### 9. `getInitParameter(String name)` & `getInitParameterNames()`

```java
public String getInitParameter(String name)
public Enumeration getInitParameterNames()

```

* **Mô tả:** Đọc các tham số cấu hình khởi tạo phạm vi toàn ứng dụng (`context-param` trong `web.xml`). Khác với `ServletConfig.getInitParameter` chỉ có phạm vi trong 1 Servlet.

---

### 10. `getAttribute(String name)`, `setAttribute(...)`, `removeAttribute(...)`

```java
public Object getAttribute(String name)
public void setAttribute(String name, Object object)
public void removeAttribute(String name)

```

* **Mô tả:** Quản lý các thuộc tính dùng chung ở phạm vi **Application Scope** (toàn bộ ứng dụng web).
* Nếu truyền `object = null` vào `setAttribute`, hiệu ứng tương đương gọi `removeAttribute`.
* Đặt tên attribute nên tuân theo quy tắc đặt tên package (tránh dùng tiền tố `java.*`, `javax.*`, `sun.*`).



---

### 11. `log(String msg)` & `log(String message, Throwable throwable)`

```java
public void log(String msg)
public void log(String message, Throwable throwable)

```

* **Mô tả:** Ghi thông điệp hoặc vết lỗi (stack trace) của ngoại lệ vào file log mặc định của Servlet Container.

---

### 12. `getServletContextName()`

```java
public String getServletContextName()

```

* **Mô tả:** Trả về tên hiển thị của ứng dụng web được định nghĩa trong thẻ `<display-name>` của file `web.xml`. Trả về `null` nếu chưa được khai báo.
* **Since:** Servlet 2.3

---

## 4. Ví dụ triển khai mã nguồn thực tế (Code Examples)

### Ví dụ 1: Quản lý Application-Scoped Attributes & Read Context Params

Đọc tham số cấu hình hệ thống từ `web.xml` và sử dụng `ServletContext` để chia sẻ dữ liệu bộ đệm (Global Cache) cho toàn bộ ứng dụng.

#### Khai báo `context-param` trong `web.xml`:

```xml
<web-app xmlns="http://java.sun.com/xml/ns/javaee" version="2.5">
    <context-param>
        <param-name>adminEmail</param-name>
        <param-value>support@company.com</param-value>
    </context-param>
</web-app>

```

#### File Java `AppInitializerServlet.java`:

```java
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/app-init")
public class AppInitializerServlet extends HttpServlet {

    @Override
    public void init() throws ServletException {
        ServletContext context = getServletContext();

        // 1. Đọc tham số khởi tạo toàn ứng dụng (context-param)
        String adminEmail = context.getInitParameter("adminEmail");
        context.log("Admin Email tu web.xml: " + adminEmail);

        // 2. Tạo một Global Cache lưu trữ trong ServletContext (Application Scope)
        Map<String, String> globalConfig = new HashMap<>();
        globalConfig.put("appName", "E-Commerce System");
        globalConfig.put("adminEmail", adminEmail);

        // Lưu vào ServletContext
        context.setAttribute("GLOBAL_CONFIG", globalConfig);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        ServletContext context = getServletContext();
        
        // Lấy lại dữ liệu từ Application Scope
        @SuppressWarnings("unchecked")
        Map<String, String> config = (Map<String, String>) context.getAttribute("GLOBAL_CONFIG");

        resp.setContentType("text/html; charset=UTF-8");
        PrintWriter out = resp.getWriter();

        out.println("<h2>Thông tin Ứng Dụng:</h2>");
        out.println("<p>App Name: " + config.get("appName") + "</p>");
        out.println("<p>Server Info: " + context.getServerInfo() + "</p>");
        out.println("<p>Servlet Version: " + context.getMajorVersion() + "." + context.getMinorVersion() + "</p>");
    }
}

```

---

### Ví dụ 2: Đọc File đính kèm và Xác định MIME Type bằng `ServletContext`

Sử dụng `getMimeType`, `getResourceAsStream` và `getRealPath` để làm tính năng Download File an toàn.

```java
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@WebServlet("/download-file")
public class FileDownloadServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        ServletContext context = getServletContext();
        String virtualPath = "/WEB-INF/documents/sample.pdf";

        // 1. Xác định MIME Type động của File
        String mimeType = context.getMimeType(virtualPath);
        if (mimeType == null) {
            mimeType = "application/octet-stream"; // Mặc định nếu không rõ
        }

        resp.setContentType(mimeType);
        resp.setHeader("Content-Disposition", "attachment; filename=\"sample.pdf\"");

        // 2. Lấy dữ liệu file dạng Stream thông qua ServletContext
        try (InputStream in = context.getResourceAsStream(virtualPath);
             OutputStream out = resp.getOutputStream()) {

            if (in == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Không tìm thấy file trong WEB-INF!");
                return;
            }

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
    }
}

```

---