# 06-http-servlet


# Document: `javax.servlet.http.HttpServlet`

* **Package:** `javax.servlet.http`
* **Class:** `public abstract class HttpServlet extends GenericServlet implements java.io.Serializable`
* **Inheritance:** `java.lang.Object` $\rightarrow$ `javax.servlet.GenericServlet` $\rightarrow$ `javax.servlet.http.HttpServlet`
* **All Implemented Interfaces:** `java.io.Serializable`, `javax.servlet.Servlet`, `javax.servlet.ServletConfig`

---

## 1. Tổng quan (Overview)

`HttpServlet` cung cấp một lớp trừu tượng (abstract class) dùng làm lớp cơ sở để tạo các HTTP Servlet phù hợp cho ứng dụng Web.

Một lớp con của `HttpServlet` phải ghi đè (override) **ít nhất một phương thức**, thông thường là các phương thức xử lý HTTP request:

* **`doGet`**: Nếu servlet hỗ trợ các HTTP GET request.
* **`doPost`**: Dành cho các HTTP POST request.
* **`doPut`**: Dành cho các HTTP PUT request.
* **`doDelete`**: Dành cho các HTTP DELETE request.
* **`init` và `destroy**`: Để quản lý tài nguyên được lưu giữ trong suốt vòng đời của servlet.
* **`getServletInfo`**: Cung cấp thông tin mô tả về servlet.

### Lưu ý quan trọng khi thiết kế:

1. **Không nên ghi đè `service`:** Phương thức `service` chịu trách nhiệm nhận HTTP request chuẩn và điều phối (dispatch) tới các phương thức xử lý tương ứng (`doGet`, `doPost`,...). Hầu như không có lý do gì để ghi đè `service`.
2. **Không nên ghi đè `doOptions` và `doTrace`:** Các phương thức này đã được cài đặt sẵn theo đúng chuẩn HTTP 1.1.
3. **Môi trường Đa luồng (Multithreaded):** Servlet thường chạy trên server đa luồng, do đó một servlet phải xử lý các request đồng thời. Hãy cẩn trọng khi truy cập các tài nguyên dùng chung (shared resources) như biến instance, biến class, file, kết nối Database hoặc kết nối mạng.

---

## 2. Tóm tắt Constructor & Phương thức (Summary)

### Constructor Summary

| Constructor | Mô tả |
| --- | --- |
| `HttpServlet()` | Khởi tạo mặc định (không thực hiện tác vụ nào vì đây là một abstract class). |

---

### Method Summary

| Kiểu trả về | Phương thức | Mô tả |
| --- | --- | --- |
| `protected void` | `doDelete(HttpServletRequest req, HttpServletResponse resp)` | Được gọi bởi server (qua `service`) để xử lý HTTP DELETE request. |
| `protected void` | `doGet(HttpServletRequest req, HttpServletResponse resp)` | Được gọi bởi server (qua `service`) để xử lý HTTP GET request. |
| `protected void` | `doHead(HttpServletRequest req, HttpServletResponse resp)` | Nhận và xử lý HTTP HEAD request (GET không trả về body). |
| `protected void` | `doOptions(HttpServletRequest req, HttpServletResponse resp)` | Xử lý HTTP OPTIONS request, trả về danh sách phương thức HTTP được hỗ trợ. |
| `protected void` | `doPost(HttpServletRequest req, HttpServletResponse resp)` | Được gọi bởi server (qua `service`) để xử lý HTTP POST request. |
| `protected void` | `doPut(HttpServletRequest req, HttpServletResponse resp)` | Được gọi bởi server (qua `service`) để xử lý HTTP PUT request. |
| `protected void` | `doTrace(HttpServletRequest req, HttpServletResponse resp)` | Xử lý HTTP TRACE request, trả về các headers phục vụ mục đích debugging. |
| `protected long` | `getLastModified(HttpServletRequest req)` | Trả về thời điểm request được sửa đổi lần cuối (tính bằng miligiây tính từ 01/01/1970 GMT). |
| `protected void` | `service(HttpServletRequest req, HttpServletResponse resp)` | Nhận HTTP request từ phương thức `service` public và điều hướng tới các phương thức `doXXX`. |
| `void` | `service(ServletRequest req, ServletResponse res)` | Chuyển tiếp các request từ Servlet Container tới phương thức `service` protected. |

---

## 3. Chi tiết Constructor & Tất cả các phương thức (Detail)

---

### Constructor Detail

#### `HttpServlet()`

* **Cú pháp:** `public HttpServlet()`
* **Mô tả:** Không thực hiện tác vụ nào vì đây là lớp trừu tượng.

---

### Method Detail

#### 1. `doGet`

* **Cú pháp:** `protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, java.io.IOException`
* **Mô tả:**
* Xử lý yêu cầu HTTP GET. Tự động hỗ trợ cả yêu cầu HTTP HEAD (HEAD trả về header giống GET nhưng không có response body).
* Quy trình chuẩn: Đọc dữ liệu request $\rightarrow$ Đặt response headers $\rightarrow$ Lấy `Writer`/`OutputStream` $\rightarrow$ Ghi response data.
* Đảm bảo đặt **Content-Type** và **Encoding** trước khi gọi `resp.getWriter()`.
* Yêu cầu GET phải **Safe** (không gây tác dụng phụ làm thay đổi dữ liệu hệ thống) và **Idempotent** (có thể gọi lặp lại nhiều lần mà không đổi kết quả).


* **Throws:** `java.io.IOException`, `ServletException`.

---

#### 2. `doPost`

* **Cú pháp:** `protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, java.io.IOException`
* **Mô tả:**
* Xử lý yêu cầu HTTP POST. Cho phép client gửi dữ liệu kích thước lớn lên server (như form đăng ký, tải file, thông tin thanh toán).
* Không bắt buộc phải *Safe* hay *Idempotent*. Các thao tác POST có thể làm thay đổi trạng thái dữ liệu trên server.


* **Throws:** `java.io.IOException`, `ServletException`.

---

#### 3. `doPut`

* **Cú pháp:** `protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, java.io.IOException`
* **Mô tả:**
* Xử lý yêu cầu HTTP PUT (tương tự việc gửi file qua FTP để lưu trữ tài nguyên trên server).
* Phải giữ nguyên các content headers được gửi kèm. Nếu không xử lý được header nào, phải trả về lỗi `HTTP 501 - Not Implemented`.


* **Throws:** `java.io.IOException`, `ServletException`.

---

#### 4. `doDelete`

* **Cú pháp:** `protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, java.io.IOException`
* **Mô tả:** Xử lý yêu cầu HTTP DELETE để xóa tài nguyên hoặc trang web khỏi server.
* **Throws:** `java.io.IOException`, `ServletException`.

---

#### 5. `doHead`

* **Cú pháp:** `protected void doHead(HttpServletRequest req, HttpServletResponse resp) throws ServletException, java.io.IOException`
* **Mô tả:** Nhận yêu cầu HTTP HEAD. Mặc định `HttpServlet` sẽ gọi `doGet` nhưng sử dụng một `NoBodyResponse` đặc biệt để chỉ đếm dung lượng và gửi headers mà không ghi body ra ngoài.
* **Throws:** `java.io.IOException`, `ServletException`.

---

#### 6. `doOptions`

* **Cú pháp:** `protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, java.io.IOException`
* **Mô tả:** Xác định các phương thức HTTP mà Servlet hỗ trợ và trả về trong header `Allow` (Ví dụ: `Allow: GET, HEAD, POST, OPTIONS`).
* **Throws:** `java.io.IOException`, `ServletException`.

---

#### 7. `doTrace`

* **Cú pháp:** `protected void doTrace(HttpServletRequest req, HttpServletResponse resp) throws ServletException, java.io.IOException`
* **Mô tả:** Dùng cho debugging, phản hồi lại đúng các headers client đã gửi đến.
* **Throws:** `java.io.IOException`, `ServletException`.

---

#### 8. `getLastModified`

* **Cú pháp:** `protected long getLastModified(HttpServletRequest req)`
* **Mô tả:** Trả về thời gian chỉnh sửa lần cuối của tài nguyên (ms tính từ 01/01/1970 GMT). Giúp trình duyệt và Proxy Cache tối ưu tài nguyên qua header `If-Modified-Since`. Mặc định trả về số âm (`-1`) nếu không xác định được.
* **Returns:** Số nguyên kiểu `long`.

---

#### 9. `service(HttpServletRequest req, HttpServletResponse resp)`

* **Cú pháp:** `protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, java.io.IOException`
* **Mô tả:** Nhận request đã ép kiểu HTTP và điều hướng (dispatch) tới các hàm `doGet`, `doPost`, `doPut`, `doDelete`,... tương ứng với HTTP Method của Request.

---

#### 10. `service(ServletRequest req, ServletResponse res)`

* **Cú pháp:** `public void service(ServletRequest req, ServletResponse res) throws ServletException, java.io.IOException`
* **Mô tả:** Điểm đầu vào chính từ Servlet Container. Phương thức này ép kiểu `ServletRequest`/`ServletResponse` sang `HttpServletRequest`/`HttpServletResponse` rồi gọi phương thức `service` protected.

---

## 4. Ví dụ triển khai mã nguồn thực tế (Code Examples)

### Ví dụ 1: Xây dựng RESTful API Servlet hỗ trợ CRUD (`GET`, `POST`, `PUT`, `DELETE`)

Ví dụ triển khai một Servlet quản lý sản phẩm hỗ trợ đầy đủ các HTTP Methods chuẩn:

```java
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/api/products/*")
public class ProductApiServlet extends HttpServlet {

    // 1. HTTP GET: Truy xuất dữ liệu sản phẩm
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        resp.setContentType("application/json; charset=UTF-8");
        PrintWriter out = resp.getWriter();

        String pathInfo = req.getPathInfo(); // Lấy ID từ URL (vd: /api/products/101)

        if (pathInfo == null || pathInfo.equals("/")) {
            out.print("[{\"id\": 101, \"name\": \"Laptop\"}, {\"id\": 102, \"name\": \"Phone\"}]");
        } else {
            String productId = pathInfo.substring(1);
            out.print("{\"id\": " + productId + ", \"name\": \"Sample Product\"}");
        }
    }

    // 2. HTTP POST: Tạo mới sản phẩm
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        req.setCharacterEncoding("UTF-8");
        String name = req.getParameter("name");
        String price = req.getParameter("price");

        resp.setStatus(HttpServletResponse.SC_CREATED); // 201 Created
        resp.setContentType("application/json; charset=UTF-8");
        resp.getWriter().print("{\"status\": \"created\", \"name\": \"" + name + "\"}");
    }

    // 3. HTTP PUT: Cập nhật sản phẩm
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        String pathInfo = req.getPathInfo();
        if (pathInfo != null && pathInfo.length() > 1) {
            String productId = pathInfo.substring(1);
            
            resp.setContentType("application/json; charset=UTF-8");
            resp.getWriter().print("{\"status\": \"updated\", \"id\": " + productId + "}");
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Thieu Product ID!");
        }
    }

    // 4. HTTP DELETE: Xóa sản phẩm
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        String pathInfo = req.getPathInfo();
        if (pathInfo != null && pathInfo.length() > 1) {
            String productId = pathInfo.substring(1);

            resp.setStatus(HttpServletResponse.SC_NO_CONTENT); // 204 No Content
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Thieu Product ID!");
        }
    }
}

```

---

### Ví dụ 2: Tối ưu Cache với `getLastModified`

Minh họa việc ghi đè `getLastModified` để tận dụng Browser Caching, giúp giảm tải băng thông và tài nguyên hệ thống.

```java
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/static-report")
public class ReportServlet extends HttpServlet {

    // Giả lập thời gian cập nhật báo cáo lần cuối
    private final long lastModifiedTime = System.currentTimeMillis();

    @Override
    protected long getLastModified(HttpServletRequest req) {
        // Trả về thời điểm báo cáo được chỉnh sửa lần cuối
        return lastModifiedTime;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        // Nếu trình duyệt gửi header If-Modified-Since và dữ liệu chưa đổi,
        // Container sẽ tự động trả về 304 Not Modified mà không chạy vào phần ghi dữ liệu này.
        resp.setContentType("text/html; charset=UTF-8");
        resp.getWriter().println("<h1>Bao cao he thong (Dung luong lon)</h1>");
    }
}

```