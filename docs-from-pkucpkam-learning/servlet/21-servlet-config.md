# 21-servlet-config

# Document: `javax.servlet.ServletConfig`

* **Package:** `javax.servlet`
* **Interface:** `public interface ServletConfig`
* **All Known Implementing Classes:** `javax.servlet.GenericServlet`, `javax.servlet.http.HttpServlet`
* **See Also:** `javax.servlet.Servlet`, `javax.servlet.ServletContext`

---

## 1. Tổng quan (Overview)

`ServletConfig` là một đối tượng cấu hình được **Servlet Container** sử dụng để truyền các thông tin cấu hình và tham số khởi tạo (initialization parameters) cho một `Servlet` trong suốt quá trình khởi tạo (hàm `init(ServletConfig config)`).

Mỗi một `Servlet` instance trong ứng dụng web sẽ sở hữu duy nhất một đối tượng `ServletConfig` riêng biệt.

### Vai trò chính của `ServletConfig`:

1. **Đọc tham số khởi tạo riêng (`init-param`):** Lấy các giá trị cấu hình tĩnh được thiết lập riêng cho từng Servlet.
2. **Lấy tên Servlet:** Đọc tên instance của Servlet được định nghĩa trong deployment descriptor (`web.xml`) hoặc annotation `@WebServlet`.
3. **Truy cập `ServletContext`:** Lấy tham chiếu tới môi trường chia sẻ dữ liệu toàn ứng dụng web.

---

## 2. Tóm tắt Phương thức (Method Summary)

| Kiểu trả về | Phương thức | Mô tả |
| --- | --- | --- |
| `String` | `getServletName()` | Trả về tên của instance Servlet này. |
| `ServletContext` | `getServletContext()` | Trả về tham chiếu tới đối tượng `ServletContext` mà Servlet đang chạy bên trong. |
| `String` | `getInitParameter(String name)` | Trả về giá trị tham số khởi tạo theo tên chỉ định, hoặc `null` nếu không tồn tại. |
| `Enumeration<String>` | `getInitParameterNames()` | Trả về danh sách tất cả các tên tham số khởi tạo của Servlet dưới dạng `Enumeration`. |

---

## 3. Chi tiết Tất cả các phương thức (Detail)

---

### 1. `getServletName`

```java
public java.lang.String getServletName()

```

* **Mô tả:** Trả về tên của instance Servlet này. Tên Servlet có thể được cung cấp thông qua giao diện quản trị server, khai báo trong file cấu hình `web.xml` (thẻ `<servlet-name>`), hoặc thuộc tính `name` của `@WebServlet`. Nếu Servlet không đăng ký tên, phương thức sẽ trả về tên đầy đủ của lớp Java (Class Name).
* **Returns:** Chuỗi `String` chứa tên của Servlet instance.

---

### 2. `getServletContext`

```java
public ServletContext getServletContext()

```

* **Mô tả:** Trả về tham chiếu đến đối tượng `ServletContext` nơi Servlet đang thực thi. Đối tượng này cho phép Servlet tương tác với Servlet Container và chia sẻ dữ liệu giữa các thành phần trong cùng một Web Application.
* **Returns:** Đối tượng `ServletContext`.
* **See Also:** `javax.servlet.ServletContext`

---

### 3. `getInitParameter`

```java
public java.lang.String getInitParameter(java.lang.String name)

```

* **Mô tả:** Trả về giá trị của tham số khởi tạo chỉ định dưới dạng chuỗi `String`.
* **Parameters:** `name` - chuỗi `String` chỉ định tên của tham số khởi tạo cần lấy.
* **Returns:** Giá trị chuỗi `String` của tham số, hoặc `null` nếu tham số không tồn tại.

---

### 4. `getInitParameterNames`

```java
public java.util.Enumeration getInitParameterNames()

```

* **Mô tả:** Trả về danh sách tất cả tên các tham số khởi tạo của Servlet dưới dạng một tập hợp `Enumeration` chứa các chuỗi `String`. Trả về an `Enumeration` rỗng nếu Servlet không có tham số khởi tạo nào.
* **Returns:** Đối tượng `Enumeration` chứa danh sách tên các tham số khởi tạo.

---

## 4. Bảng so sánh `ServletConfig` vs `ServletContext`

| Tiêu chí | `ServletConfig` | `ServletContext` |
| --- | --- | --- |
| **Phạm vi (Scope)** | **Riêng biệt:** Cho đúng **1 Servlet instance** duy nhất. | **Toàn cục:** Dùng chung cho **toàn bộ Web Application** (tất cả Servlets, Filters, JSPs). |
| **Số lượng Instance** | Mỗi Servlet có **1 đối tượng `ServletConfig` riêng**. | Mỗi ứng dụng Web chỉ có **1 đối tượng `ServletContext` duy nhất**. |
| **Khai báo trong `web.xml**` | Nằm bên trong thẻ `<servlet>` $\rightarrow$ `<init-param>`. | Nằm trực tiếp bên trong thẻ root `<web-app>` $\rightarrow$ `<context-param>`. |
| **Mục đích chính** | Lưu trữ cấu hình riêng của từng Servlet (như file config riêng, email hỗ trợ). | Lưu trữ biến dùng chung (như Database Connection Pool, Cấu hình chung hệ thống). |

---

## 5. Ví dụ triển khai mã nguồn thực tế (Code Examples)

### Ví dụ 1: Đọc Init Parameters sử dụng Annotation `@WebServlet`

Cấu hình tham số khởi tạo trực tiếp trên Servlet bằng `@WebInitParam` và truy xuất qua các phương thức của `ServletConfig` (đã được kế thừa sẵn bởi `HttpServlet`/`GenericServlet`).

```java
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(
    urlPatterns = "/payment-gateway",
    name = "PaymentGatewayServlet",
    initParams = {
        @WebInitParam(name = "merchantId", value = "MCH_998822"),
        @WebInitParam(name = "environment", value = "SANDBOX")
    }
)
public class PaymentGatewayServlet extends HttpServlet {

    private String merchantId;
    private String environment;

    @Override
    public void init(ServletConfig config) throws ServletException {
        // Luôn nhớ gọi super.init(config) khi override dạng hàm có tham số
        super.init(config);

        // 1. Lấy tên Servlet
        String servletName = config.getServletName();

        // 2. Đọc các tham số khởi tạo riêng bằng ServletConfig
        this.merchantId = config.getInitParameter("merchantId");
        this.environment = config.getInitParameter("environment");

        // 3. Lấy ServletContext để ghi log hệ thống
        ServletContext context = config.getServletContext();
        context.log("[" + servletName + "] Đã khởi tạo cấu hình: Merchant=" + merchantId + ", Env=" + environment);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        resp.setContentType("text/html; charset=UTF-8");
        PrintWriter out = resp.getWriter();

        out.println("<h2>Cấu Hình Cổng Thanh Toán</h2>");
        out.println("<p>Servlet Name: <b>" + getServletName() + "</b></p>");
        out.println("<p>Merchant ID: <b>" + this.merchantId + "</b></p>");
        out.println("<p>Environment: <b>" + this.environment + "</b></p>");
    }
}

```

---

### Ví dụ 2: Duyệt toàn bộ Init Parameters cấu hình qua `web.xml`

Trường hợp Servlet có nhiều tham số cấu hình trong file `web.xml`, ta duyệt qua tất cả tham số bằng `getInitParameterNames()`.

#### Cấu hình trong `web.xml`:

```xml
<servlet>
    <servlet-name>ReportGeneratorServlet</servlet-name>
    <servlet-class>com.example.ReportGeneratorServlet</servlet-class>
    <init-param>
        <param-name>outputDir</param-name>
        <param-value>/var/reports/pdf</param-value>
    </init-param>
    <init-param>
        <param-name>dateFormat</param-name>
        <param-value>yyyy-MM-dd HH:mm:ss</param-value>
    </init-param>
</servlet>
<servlet-mapping>
    <servlet-name>ReportGeneratorServlet</servlet-name>
    <url-pattern>/generate-report</url-pattern>
</servlet-mapping>

```

#### File Java `ReportGeneratorServlet.java`:

```java
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

public class ReportGeneratorServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        resp.setContentType("text/html; charset=UTF-8");
        PrintWriter out = resp.getWriter();

        // Lấy đối tượng ServletConfig
        ServletConfig config = getServletConfig();

        out.println("<h3>Danh sách Init Parameters của Servlet: " + config.getServletName() + "</h3>");
        out.println("<ul>");

        // Lấy danh sách tên tất cả tham số
        Enumeration<String> paramNames = config.getInitParameterNames();

        while (paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            String paramValue = config.getInitParameter(paramName);
            
            out.println("<li><b>" + paramName + "</b>: " + paramValue + "</li>");
        }

        out.println("</ul>");
    }
}

```