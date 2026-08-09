# 31-servlet-request-attribute-event

# Document: `javax.servlet.ServletRequestAttributeEvent`

* **Package:** `javax.servlet`
* **Class:** `public class ServletRequestAttributeEvent extends ServletRequestEvent`
* **Inheritance:** `java.lang.Object` $\rightarrow$ `java.util.EventObject` $\rightarrow$ `javax.servlet.ServletRequestEvent` $\rightarrow$ `javax.servlet.ServletRequestAttributeEvent`
* **All Implemented Interfaces:** `java.io.Serializable`
* **Since:** Servlet 2.4
* **See Also:** `javax.servlet.ServletRequestAttributeListener`, `javax.servlet.ServletRequest`

---

## 1. Tổng quan (Overview)

`ServletRequestAttributeEvent` là lớp sự kiện đại diện cho các thông báo về sự thay đổi danh sách thuộc tính (attributes) trong phạm vi **Request Scope** (`ServletRequest`) của ứng dụng web.

Đối tượng của lớp này được Servlet Container tự động khởi tạo và truyền làm tham số vào các phương thức của **`ServletRequestAttributeListener`** mỗi khi có một thuộc tính trong `ServletRequest` được **thêm mới**, **xóa bỏ**, hoặc **thay thế (ghi đè)**.

---

## 2. Tóm tắt Field & Constructor (Summary)

### Field Summary

| Field (Inherited) | Mô tả |
| --- | --- |
| `protected Object source` | Đối tượng phát ra sự kiện, kế thừa từ `java.util.EventObject`. |

---

### Constructor Summary

| Constructor | Mô tả |
| --- | --- |
| `ServletRequestAttributeEvent(ServletContext sc, ServletRequest request, String name, Object value)` | Khởi tạo sự kiện thuộc tính request với đối tượng ServletContext, ServletRequest, tên thuộc tính và giá trị tương ứng. |

---

## 3. Tóm tắt Phương thức (Method Summary)

| Kiểu trả về | Phương thức | Mô tả |
| --- | --- | --- |
| `String` | `getName()` | Trả về tên của thuộc tính đã thay đổi trên `ServletRequest`. |
| `Object` | `getValue()` | Trả về giá trị của thuộc tính đã được thêm, bị xóa, hoặc giá trị CŨ trước khi bị thay thế. |
| `ServletContext` | `getServletContext()` | *(Inherited từ `ServletRequestEvent`)* Trả về `ServletContext` của ứng dụng web. |
| `ServletRequest` | `getServletRequest()` | *(Inherited từ `ServletRequestEvent`)* Trả về `ServletRequest` xảy ra sự kiện. |

---

## 4. Chi tiết Constructor & Tất cả các phương thức (Detail)

---

### Constructor Detail

#### `ServletRequestAttributeEvent(...)`

```java
public ServletRequestAttributeEvent(ServletContext sc,
                                    ServletRequest request,
                                    java.lang.String name,
                                    java.lang.Object value)

```

* **Mô tả:** Khởi tạo đối tượng `ServletRequestAttributeEvent` chứa thông tin về `ServletContext`, `ServletRequest`, tên thuộc tính bị thay đổi và giá trị liên quan.
* **Parameters:**
* `sc`: Đối tượng `ServletContext` gửi sự kiện.
* `request`: Đối tượng `ServletRequest` gửi sự kiện.
* `name`: Tên thuộc tính request bị thay đổi.
* `value`: Giá trị thuộc tính request (giá trị vừa thêm, vừa xóa, hoặc giá trị cũ trước khi bị thay thế).



---

### Method Detail

#### 1. `getName()`

```java
public java.lang.String getName()

```

* **Mô tả:** Trả về tên của thuộc tính đã gán, xóa hoặc thay thế trong `ServletRequest`.
* **Returns:** Chuỗi `String` chứa tên thuộc tính.

---

#### 2. `getValue()`

```java
public java.lang.Object getValue()

```

* **Mô tả:** Trả về đối tượng giá trị của thuộc tính tương ứng với từng hành vi:
* **Nếu thuộc tính được thêm mới (`attributeAdded`):** Trả về giá trị của thuộc tính vừa gán vào.
* **Nếu thuộc tính bị xóa (`attributeRemoved`):** Trả về giá trị của thuộc tính vừa bị xóa khỏi request.
* **Nếu thuộc tính bị thay thế (`attributeReplaced`):** Trả về **giá trị CŨ** của thuộc tính trước khi bị ghi đè.


* **Returns:** Đối tượng `Object` đại diện cho giá trị thuộc tính request.

---

## 5. Ví dụ triển khai mã nguồn thực tế (Code Examples)

### Ví dụ 1: Sử dụng `ServletRequestAttributeEvent` trong `ServletRequestAttributeListener`

Minh họa việc đăng ký Listener để kiểm vết (Audit Log) các thay đổi dữ liệu diễn ra trong phạm vi Request Scope khi luồng request luân chuyển qua các Filter và Servlet.

```java
import javax.servlet.ServletRequestAttributeEvent;
import javax.servlet.ServletRequestAttributeListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class RequestAttributeAuditListener implements ServletRequestAttributeListener {

    // 1. Khi một thuộc tính mới được thêm vào Request Scope
    @Override
    public void attributeAdded(ServletRequestAttributeEvent srae) {
        String name = srae.getName();
        Object value = srae.getValue();
        
        System.out.println("[REQUEST SCOPE - ATTR ADDED]");
        System.out.println("  - Name: " + name);
        System.out.println("  - Value: " + value);
    }

    // 2. Khi một thuộc tính bị xóa khỏi Request Scope
    @Override
    public void attributeRemoved(ServletRequestAttributeEvent srae) {
        String name = srae.getName();
        Object removedValue = srae.getValue();

        System.out.println("[REQUEST SCOPE - ATTR REMOVED]");
        System.out.println("  - Name: " + name);
        System.out.println("  - Removed Value: " + removedValue);
    }

    // 3. Khi một thuộc tính trong Request Scope bị ghi đè/thay thế
    @Override
    public void attributeReplaced(ServletRequestAttributeEvent srae) {
        String name = srae.getName();
        
        // srae.getValue() trả về giá trị CŨ trước khi ghi đè
        Object oldValue = srae.getValue();
        
        // Lấy giá trị MỚI vừa cập nhật trực tiếp từ ServletRequest
        Object newValue = srae.getServletRequest().getAttribute(name);

        System.out.println("[REQUEST SCOPE - ATTR REPLACED]");
        System.out.println("  - Name: " + name);
        System.out.println("  - Old Value: " + oldValue);
        System.out.println("  - New Value: " + newValue);
    }
}

```

---

### Ví dụ 2: Servlet thao tác với Request Scope kích hoạt Event

Servlet gán dữ liệu bằng `setAttribute()` và `removeAttribute()`, phát ra các đối tượng `ServletRequestAttributeEvent` tương ứng cho Listener.

```java
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/req-attr-demo")
public class RequestAttrDemoServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {

        // 1. Kích hoạt attributeAdded
        req.setAttribute("PROCESS_STAGE", "INITIATED");

        // 2. Kích hoạt attributeReplaced (event.getValue() sẽ trả về "INITIATED")
        req.setAttribute("PROCESS_STAGE", "PROCESSING");

        // 3. Kích hoạt attributeRemoved
        req.removeAttribute("PROCESS_STAGE");

        resp.setContentType("text/plain; charset=UTF-8");
        resp.getWriter().write("Đã thực hiện xong thao tác trên Request Attributes!");
    }
}

```

---