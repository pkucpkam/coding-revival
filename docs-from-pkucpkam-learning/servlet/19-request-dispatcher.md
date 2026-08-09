# 19-request-dispatcher

# Document: `javax.servlet.RequestDispatcher`

* **Package:** `javax.servlet`
* **Interface:** `public interface RequestDispatcher`
* **See Also:** `ServletContext.getRequestDispatcher(String)`, `ServletContext.getNamedDispatcher(String)`, `ServletRequest.getRequestDispatcher(String)`

---

## 1. Tổng quan (Overview)

Interface `RequestDispatcher` định nghĩa đối tượng chịu trách nhiệm nhận các yêu cầu (request) từ client và gửi chúng đến bất kỳ tài nguyên nào trên server (như Servlet, file HTML hoặc file JSP).

Servlet Container tạo ra đối tượng `RequestDispatcher` như một lớp bọc (wrapper) xung quanh tài nguyên server nằm tại một đường dẫn cụ thể hoặc được định danh bằng một tên nhất định.

Interface này được thiết kế chủ yếu để bọc các Servlet, nhưng Servlet Container có thể tạo các đối tượng `RequestDispatcher` để bọc bất kỳ loại tài nguyên nào.

---

## 2. Cách lấy đối tượng `RequestDispatcher`

Bạn có thể lấy đối tượng `RequestDispatcher` theo các cách sau:

1. **`request.getRequestDispatcher(String path)`**: Nhận đường dẫn tương đối (tính từ ngữ cảnh ứng dụng hoặc request).
2. **`context.getRequestDispatcher(String path)`**: Nhận đường dẫn tuyệt đối bắt đầu bằng dấu `/` (tính từ root context của ứng dụng web).
3. **`context.getNamedDispatcher(String name)`**: Nhận theo tên khai báo của Servlet (tên định nghĩa trong `web.xml` hoặc `@WebServlet`).

---

## 3. Tóm tắt Phương thức (Method Summary)

| Kiểu trả về | Phương thức | Mô tả |
| --- | --- | --- |
| `void` | `forward(ServletRequest request, ServletResponse response)` | Chuyển tiếp (forward) request từ servlet hiện tại tới một tài nguyên khác trên server. |
| `void` | `include(ServletRequest request, ServletResponse response)` | Nhúng (include) nội dung của một tài nguyên khác vào response hiện tại. |

---

## 4. Chi tiết Tất cả các phương thức (Detail)

---

### 1. `forward(ServletRequest request, ServletResponse response)`

```java
public void forward(ServletRequest request, ServletResponse response) 
             throws ServletException, java.io.IOException

```

* **Mô tả:** Chuyển tiếp request từ một servlet tới một tài nguyên khác (servlet, JSP, file HTML) trên server. Phương thức này cho phép một servlet xử lý sơ bộ dữ liệu, sau đó nhường quyền cho tài nguyên khác tạo và trả về phản hồi (response).
* **Quy tắc quan trọng:**
* Nếu `RequestDispatcher` được lấy qua `getRequestDispatcher()`, các phần tử đường dẫn (path elements) và tham số của `ServletRequest` sẽ được điều chỉnh cho phù hợp với đường dẫn của tài nguyên đích.
* **Chỉ được gọi trước khi response bị committed** (trước khi dữ liệu response body được đẩy về client qua flush). Nếu response đã committed, phương thức sẽ quăng ra `IllegalStateException`.
* Dữ liệu chưa committed nằm trong response buffer sẽ tự động bị xóa (cleared) trước khi tiến hành chuyển tiếp.
* Tham số `request` và `response` truyền vào phải là đối tượng gốc từ hàm `service` hoặc là các đối tượng bọc (`ServletRequestWrapper` / `ServletResponseWrapper`).


* **Parameters:**
* `request`: Đối tượng `ServletRequest` biểu diễn yêu cầu từ client.
* `response`: Đối tượng `ServletResponse` biểu diễn phản hồi trả về client.


* **Throws:**
* `ServletException`: Nếu tài nguyên đích ném ra ngoại lệ này.
* `java.io.IOException`: Nếu xảy ra lỗi I/O.
* `java.lang.IllegalStateException`: Nếu response đã bị committed trước đó.



---

### 2. `include(ServletRequest request, ServletResponse response)`

```java
public void include(ServletRequest request, ServletResponse response) 
             throws ServletException, java.io.IOException

```

* **Mô tả:** Nhúng nội dung của một tài nguyên khác (servlet, JSP page, HTML file) vào response hiện tại. Về bản chất, phương thức này cho phép thực hiện **Server-Side Includes** bằng lập trình.
* **Quy tắc quan trọng:**
* Đối tượng `ServletResponse` giữ nguyên các phần tử đường dẫn và tham số của bên gọi (caller).
* **Tài nguyên được nhúng KHÔNG THỂ thay đổi status code hoặc thiết lập response headers**; bất kỳ nỗ lực thay đổi header/status code nào từ tài nguyên được nhúng đều bị container bỏ qua.
* Tham số `request` và `response` truyền vào phải là đối tượng gốc từ hàm `service` hoặc là các đối tượng bọc (`ServletRequestWrapper` / `ServletResponseWrapper`).


* **Parameters:**
* `request`: Đối tượng `ServletRequest` chứa yêu cầu của client.
* `response`: Đối tượng `ServletResponse` chứa phản hồi của servlet.


* **Throws:**
* `ServletException`: Nếu tài nguyên được nhúng ném ra ngoại lệ này.
* `java.io.IOException`: Nếu xảy ra lỗi I/O.



---

## 5. Bảng so sánh `forward()` vs `include()` vs `sendRedirect()`

| Tiêu chí | `RequestDispatcher.forward()` | `RequestDispatcher.include()` | `HttpServletResponse.sendRedirect()` |
| --- | --- | --- | --- |
| **Bản chất** | Chuyển giao hoàn toàn xử lý sang tài nguyên mới. | Nhúng kết quả của tài nguyên mới vào response hiện tại. | Yêu cầu Client/Browser tự gửi một Request mới (HTTP 302). |
| **Số lượng Request** | 1 Request duy nhất trên Server. | 1 Request duy nhất trên Server. | 2 Request độc lập từ Browser. |
| **Thay đổi URL trên Browser** | **Không** đổi URL. | **Không** đổi URL. | **Có** đổi sang URL mới. |
| **Phạm vi tài nguyên** | Chỉ trong cùng một Web Application. | Chỉ trong cùng một Web Application. | Bất kỳ URL nào (kể cả Domain bên ngoài). |
| **Thay đổi Response Header** | Tài nguyên đích có thể đặt header/status. | Tài nguyên nhúng **không thể** đặt header/status. | Tạo Response mới hoàn toàn. |

---

## 6. Ví dụ triển khai mã nguồn thực tế (Code Examples)

### Ví dụ 1: Sử dụng `forward()` để chuyển hướng dữ liệu sang trang JSP

Lớp Controller xử lý logic nghiệp vụ, gán dữ liệu vào Request Attribute, sau đó dùng `forward()` để nhờ trang JSP hiển thị giao diện.

```java
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/user-profile")
public class UserProfileServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        // 1. Giả lập xử lý nghiệp vụ lấy thông tin User
        String username = "NguyenVanA";
        String email = "nguyenvana@example.com";

        // 2. Lưu thông tin vào Request Scope
        req.setAttribute("userName", username);
        req.setAttribute("userEmail", email);

        // 3. Lấy RequestDispatcher tới file JSP giao diện
        RequestDispatcher dispatcher = req.getRequestDispatcher("/views/user-profile.jsp");

        // 4. Chuyển tiếp Request và Response sang JSP
        dispatcher.forward(req, resp);
    }
}

```

---

### Ví dụ 2: Sử dụng `include()` để nhúng Header và Footer chung vào trang Web

Dùng `include()` để ghép các thành phần giao diện chung (như Thanh điều hướng Header hoặc chân trang Footer) vào response của trang chính.

```java
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        resp.setContentType("text/html; charset=UTF-8");
        PrintWriter out = resp.getWriter();

        // 1. Nhúng giao diện Header chung vào đầu Response
        RequestDispatcher headerDispatcher = req.getRequestDispatcher("/includes/header.html");
        headerDispatcher.include(req, resp);

        // 2. Ghi nội dung chính của trang Dashboard
        out.println("<main class='content'>");
        out.println("<h2>Trang Quản Lý Hệ Thống (Dashboard)</h2>");
        out.println("<p>Chào mừng bạn đã quay trở lại làm việc!</p>");
        out.println("</main>");

        // 3. Nhúng giao diện Footer chung vào cuối Response
        RequestDispatcher footerDispatcher = req.getRequestDispatcher("/includes/footer.html");
        footerDispatcher.include(req, resp);
    }
}

```