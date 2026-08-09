# 29-servlet-output-stream

# Document: `javax.servlet.ServletOutputStream`

* **Package:** `javax.servlet`
* **Class:** `public abstract class ServletOutputStream extends java.io.OutputStream`
* **Inheritance:** `java.lang.Object` $\rightarrow$ `java.io.OutputStream` $\rightarrow$ `javax.servlet.ServletOutputStream`
* **See Also:** `javax.servlet.ServletResponse`, `javax.servlet.ServletResponse.getOutputStream()`

---

## 1. Tổng quan (Overview)

`ServletOutputStream` cung cấp một luồng xuất (output stream) để ghi và gửi dữ liệu nhị phân (binary data) từ Server về Web Client. Đối tượng này thường được lấy ra thông qua phương thức **`ServletResponse.getOutputStream()`**.

Lớp này kế thừa từ `java.io.OutputStream` và bổ sung thêm các phương thức tiện ích `print()` cũng như `println()` để ghi các kiểu dữ liệu nguyên thủy (`int`, `boolean`, `char`, `float`, `double`, `long`) và chuỗi `String` trực tiếp ra luồng phản hồi.

### Các điểm lưu ý quan trọng:

1. **Abstract Class:** Đây là lớp trừu tượng do Servlet Container (như Apache Tomcat) triển khai. Các lớp con bắt buộc phải ghi đè phương thức `java.io.OutputStream.write(int)`.
2. **Quy tắc luồng ghi:** Trong cùng một response, bạn chỉ có thể gọi **`response.getOutputStream()`** HOẶC **`response.getWriter()`**, không thể gọi đồng thời cả hai (sẽ ném ra `IllegalStateException`).
3. **Mục đích sử dụng:** Thường dùng để xuất file nhị phân (như hình ảnh, PDF, Excel, ZIP) hoặc trả về các luồng dữ liệu thô (raw binary streams).

---

## 2. Tóm tắt Constructor & Phương thức (Summary)

### Constructor Summary

| Constructor | Mô tả |
| --- | --- |
| `protected ServletOutputStream()` | Constructor protected mặc định cho các lớp con triển khai. |

---

### Method Summary

| Kiểu trả về | Phương thức | Mô tả |
| --- | --- | --- |
| `void` | `print(boolean b)` | Ghi giá trị `boolean` ra client (không kèm CRLF). |
| `void` | `print(char c)` | Ghi một ký tự `char` ra client (không kèm CRLF). |
| `void` | `print(double d)` | Ghi giá trị `double` ra client (không kèm CRLF). |
| `void` | `print(float f)` | Ghi giá trị `float` ra client (không kèm CRLF). |
| `void` | `print(int i)` | Ghi một số nguyên `int` ra client (không kèm CRLF). |
| `void` | `print(long l)` | Ghi giá trị `long` ra client (không kèm CRLF). |
| `void` | `print(String s)` | Ghi một chuỗi `String` ra client (không kèm CRLF). |
| `void` | `println()` | Ghi một ký tự xuống dòng (CRLF: `\r\n`) ra client. |
| `void` | `println(boolean b)` | Ghi giá trị `boolean` kèm ký tự xuống dòng (CRLF) ra client. |
| `void` | `println(char c)` | Ghi ký tự `char` kèm ký tự xuống dòng (CRLF) ra client. |
| `void` | `println(double d)` | Ghi giá trị `double` kèm ký tự xuống dòng (CRLF) ra client. |
| `void` | `println(float f)` | Ghi giá trị `float` kèm ký tự xuống dòng (CRLF) ra client. |
| `void` | `println(int i)` | Ghi số nguyên `int` kèm ký tự xuống dòng (CRLF) ra client. |
| `void` | `println(long l)` | Ghi giá trị `long` kèm ký tự xuống dòng (CRLF) ra client. |
| `void` | `println(String s)` | Ghi chuỗi `String` kèm ký tự xuống dòng (CRLF) ra client. |

| Phương thức Kế thừa từ `java.io.OutputStream` |
| --- |
| `close()`, `flush()`, `write(byte[] b)`, `write(byte[] b, int off, int len)`, `write(int b)` |

---

## 3. Chi tiết Constructor & Tất cả các phương thức (Detail)

---

### Constructor Detail

#### `protected ServletOutputStream()`

* **Cú pháp:** `protected ServletOutputStream()`
* **Mô tả:** Constructor protected mặc định, không thực hiện thao tác gì.

---

### Method Detail

#### 1. Nhóm phương thức `print(...)`

Ghi dữ liệu tương ứng ra client mà **không** tự động đính kèm ký tự xuống dòng (CRLF - Carriage Return Line Feed).

* `public void print(String s) throws IOException`
* `public void print(boolean b) throws IOException`
* `public void print(char c) throws IOException`
* `public void print(int i) throws IOException`
* `public void print(long l) throws IOException`
* `public void print(float f) throws IOException`
* `public void print(double d) throws IOException`

---

#### 2. Nhóm phương thức `println(...)`

Ghi dữ liệu tương ứng ra client và **tự động đính kèm** ký tự xuống dòng (CRLF - `\r\n`) ở cuối.

* `public void println() throws IOException` (chỉ ghi CRLF)
* `public void println(String s) throws IOException`
* `public void println(boolean b) throws IOException`
* `public void println(char c) throws IOException`
* `public void println(int i) throws IOException`
* `public void println(long l) throws IOException`
* `public void println(float f) throws IOException`
* `public void println(double d) throws IOException`

---

## 4. Ví dụ triển khai mã nguồn thực tế (Code Examples)

### Ví dụ 1: Xuất File hình ảnh (Binary Stream Output) bằng `ServletOutputStream`

Sử dụng `ServletOutputStream` để đọc file ảnh từ thư mục hệ thống và ghi nhị phân về cho browser hiển thị/download.

```java
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@WebServlet("/images/logo")
public class ImageRenderServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        // 1. Cấu hình Content-Type là hình ảnh PNG
        resp.setContentType("image/png");

        File imageFile = new File(getServletContext().getRealPath("/WEB-INF/assets/logo.png"));

        if (!imageFile.exists()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Không tìm thấy file hình ảnh!");
            return;
        }

        // 2. Đọc file nhị phân và ghi ra ServletOutputStream
        try (FileInputStream fis = new FileInputStream(imageFile);
             ServletOutputStream out = resp.getOutputStream()) {

            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = fis.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead); // Ghi mảng byte ra client
            }

            out.flush(); // Đẩy toàn bộ dữ liệu ra luồng
        }
    }
}

```

---

### Ví dụ 2: Xuất file CSV động bằng phương thức `println()`

Minh họa việc tạo file CSV và cho phép người dùng download thông qua các phương thức `println()` của `ServletOutputStream`.

```java
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/export/users-csv")
public class ExportCsvServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        // 1. Cấu hình Header cho phép Download File CSV
        resp.setContentType("text/csv; charset=UTF-8");
        resp.setHeader("Content-Disposition", "attachment; filename=\"users_list.csv\"");

        // 2. Lấy ServletOutputStream
        ServletOutputStream out = resp.getOutputStream();

        // Ghi BOM để Excel hiển thị đúng tiếng Việt UTF-8
        out.write(0xEF);
        out.write(0xBB);
        out.write(0xBF);

        // 3. Sử dụng println() để ghi dòng Header CSV
        out.println("STT,Mã NV,Họ và Tên,Lương");

        // 4. Ghi các dòng dữ liệu
        out.print(1);
        out.print(",");
        out.print("NV001");
        out.print(",");
        out.print("Nguyễn Văn A");
        out.print(",");
        out.println(15000000.50);

        out.print(2);
        out.print(",");
        out.print("NV002");
        out.print(",");
        out.print("Trần Thị B");
        out.print(",");
        out.println(18500000.00);

        out.flush();
    }
}

```

---