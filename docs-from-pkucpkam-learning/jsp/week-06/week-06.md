# 📚 Tuần 6: Advanced JSP/Servlet - Filter, Listeners, File Upload & Internationalization

## 🎯 Tổng Quan Tuần 6

Tuần 6 nâng cao kiến thức từ Tuần 5 bằng cách **mở rộng filter architecture**, giới thiệu **listeners để theo dõi lifecycle**, xử lý **file upload/download**, cộng thêm **multi-language support (i18n)**, và tái cấu trúc MVC với **Service layer**.

**Tích hợp với Tuần 5**: Tuần 5 đã xây dựng filter chain cơ bản (AuthFilter, RoleFilter). Tuần 6 sẽ làm sâu hơn với:
- Logging filter, Order của filter, Chain execution model
- Listeners để monitor session/request lifecycle
- File handling trong filter chain
- Internationalization (i18n) patterns

**Kỹ năng cần có trước khi bắt đầu**:
- ✅ Filter basics từ Tuần 5 (Auth, Role filter)
- ✅ Servlet & JSP từ Tuần 1-2
- ✅ MVC architecture từ Tuần 3
- ✅ DAO Pattern từ Tuần 4
- ✅ Session management từ Tuần 5

---

# Session 36: Servlet Filters - Deep Dive

## 📖 Lý Thuyết: Filter Architecture & Execution Order

### 6.1.1 Filter Lifecycle

**Triệu hồi lần đầu (First Request with New Filter)**:
```
1. Container gọi init(FilterConfig)  ← Initialize once
2. Gọi doFilter() cho request đầu tiên
3. Nếu no error loại bỏ filter or update web.xml
4. Container gọi destroy()  ← Cleanup khi shutdown
```

**Mỗi request (Every Request)**:
```
Request → Filter1.doFilter() 
       → Filter2.doFilter() 
       → ... 
       → ServletHandler 
       → Response back → Filter2.doFilter(response) 
       → Filter1.doFilter(response) 
       → Client
```

### 6.1.2 Filter Execution Order

Execution order được xác định bởi **thứ tự <filter-mapping> trong web.xml**:

```xml
<!-- web.xml -->
<filter>
    <filter-name>LoggingFilter</filter-name>
    <filter-class>com.example.filters.LoggingFilter</filter-class>
</filter>

<filter>
    <filter-name>AuthFilter</filter-name>
    <filter-class>com.example.filters.AuthFilter</filter-class>
</filter>

<!-- Execution Order: LoggingFilter → AuthFilter → Servlet → AuthFilter response → LoggingFilter response -->
<filter-mapping>
    <filter-name>LoggingFilter</filter-name>
    <url-pattern>/*</url-pattern>
</filter-mapping>

<filter-mapping>
    <filter-name>AuthFilter</filter-name>
    <url-pattern>/*</url-pattern>
</filter-mapping>
```

### 6.1.3 Filter Chain Pattern

**Diagram: Request Flow Through Filter Chain**
```
Client Request (GET /admin/users)
    ↓
LoggingFilter.doFilter()
  - Log request details (method, path, IP)
  - Call chain.doFilter()  ← CRITICAL: Đây là forward quyền
    ↓
EncodingFilter.doFilter()
  - Set UTF-8 encoding
  - Call chain.doFilter()
    ↓
AuthFilter.doFilter()
  - Check session/token
  - If authenticated: Call chain.doFilter()
  - If not: res.sendError(401)
    ↓
RoleFilter.doFilter()
  - Check role for /admin/*
  - If admin: Call chain.doFilter()
  - If not: res.sendError(403)
    ↓
Servlet Handler (doGet/doPost)
  - Process business logic
  - Return response object
    ↓
Back Through Chain (Response)
RoleFilter response handling
AuthFilter response handling
EncodingFilter response handling
LoggingFilter.doFilter() (after chain)
  - Log response status, time elapsed
    ↓
Client Response
```

### 6.1.4 FilterConfig Methods

```java
public interface FilterConfig {
    // Get filter name from web.xml
    String getFilterName();
    
    // Get servlet context (shared app-level data)
    ServletContext getServletContext();
    
    // Get initialization parameter from web.xml
    String getInitParameter(String name);
    
    // Get all init parameters
    Enumeration<String> getInitParameterNames();
}
```

---

## 🔧 Cách Sử Dụng: Logging Filter

### 6.1.5 LoggingFilter Implementation

**Use Case**: Track mỗi HTTP request (timing, user, endpoint, status code)

```java
package com.example.filters;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class LoggingFilter implements Filter {
    private static final org.slf4j.Logger logger = 
        org.slf4j.LoggerFactory.getLogger(LoggingFilter.class);
    
    private static final DateTimeFormatter formatter = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    
    @Override
    public void init(FilterConfig config) throws ServletException {
        logger.info("LoggingFilter initialized");
        // Có thể đọc init params từ web.xml
        String logLevel = config.getInitParameter("logLevel");
        logger.info("Log level configured: {}", logLevel);
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, 
                        FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        
        // ===== BEFORE Request Processing =====
        String method = req.getMethod();
        String path = req.getRequestURI();
        String query = req.getQueryString();
        String ip = getClientIp(req);
        long startTime = System.currentTimeMillis();
        
        // Log incoming request
        String requestUrl = query != null ? path + "?" + query : path;
        logger.info("→ Request: {} {} from IP: {}", method, requestUrl, ip);
        
        // Retrieve user info from session (if logged in)
        Object userId = req.getSession(false) != null ? 
            req.getSession().getAttribute("userId") : "anonymous";
        logger.debug("  User: {}", userId);
        
        // ===== Process Request Through Chain =====
        try {
            chain.doFilter(request, response);
        } catch (Exception e) {
            logger.error("Error processing request: {}", e.getMessage(), e);
            throw e;
        }
        
        // ===== AFTER Request Processing =====
        long duration = System.currentTimeMillis() - startTime;
        int status = res.getStatus();
        
        // Log response
        logger.info("← Response: {} {} ms [Status: {}] from {}", 
                   method, duration, status, path);
        
        // Warn if request took too long (> 1 second)
        if (duration > 1000) {
            logger.warn("  ⚠ SLOW REQUEST: {} completed in {} ms", path, duration);
        }
        
        // Warn if error status (4xx, 5xx)
        if (status >= 400) {
            logger.warn("  ⚠ ERROR RESPONSE: {} returned status {}", path, status);
        }
    }
    
    @Override
    public void destroy() {
        logger.info("LoggingFilter destroyed");
    }
    
    // Helper: Extract client IP (handle proxies)
    private String getClientIp(HttpServletRequest request) {
        String xForwarded = request.getHeader("X-Forwarded-For");
        if (xForwarded != null && !xForwarded.isEmpty()) {
            return xForwarded.split(",")[0].trim();
        }
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        return request.getRemoteAddr();
    }
}
```

### 6.1.6 web.xml Mapping

```xml
<!-- web.xml -->
<filter>
    <filter-name>LoggingFilter</filter-name>
    <filter-class>com.example.filters.LoggingFilter</filter-class>
    
    <!-- Init parameters -->
    <init-param>
        <param-name>logLevel</param-name>
        <param-value>DEBUG</param-value>
    </init-param>
</filter>

<filter-mapping>
    <filter-name>LoggingFilter</filter-name>
    <url-pattern>/*</url-pattern>
    <!-- Apply to REQUEST and ERROR dispatches -->
    <dispatcher>REQUEST</dispatcher>
    <dispatcher>ERROR</dispatcher>
</filter-mapping>

<!-- Other filters below LoggingFilter will see it first in request, last in response -->
```

### 6.1.7 Improved AuthFilter (from Tuần 5 - with Logging)

```java
package com.example.filters;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthFilter implements Filter {
    private static final Logger logger = 
        LoggerFactory.getLogger(AuthFilter.class);
    
    // Pages that don't require authentication
    private static final Set<String> PUBLIC_PAGES = new HashSet<>();
    
    static {
        PUBLIC_PAGES.add("/login");
        PUBLIC_PAGES.add("/register");
        PUBLIC_PAGES.add("/css/");
        PUBLIC_PAGES.add("/js/");
        PUBLIC_PAGES.add("/images/");
    }
    
    @Override
    public void init(FilterConfig config) throws ServletException {
        logger.info("AuthFilter initialized");
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, 
                        FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String path = req.getRequestURI().substring(req.getContextPath().length());
        
        logger.debug("AuthFilter checking path: {}", path);
        
        // Check if path is public
        boolean isPublic = PUBLIC_PAGES.stream()
            .anyMatch(path::startsWith);
        
        if (isPublic) {
            logger.debug("  {} is public, allowing access", path);
            chain.doFilter(request, response);
            return;
        }
        
        // Check for authenticated session
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("userId") != null) {
            logger.debug("  User authenticated, allowing access");
            chain.doFilter(request, response);
            return;
        }
        
        // Not authenticated + not public → redirect to login
        logger.warn("  Unauthorized access to {}, redirecting to login", path);
        res.sendRedirect(req.getContextPath() + "/login?redirect=" + path);
    }
    
    @Override
    public void destroy() {
        logger.info("AuthFilter destroyed");
    }
}
```

### 6.1.8 EncodingFilter (Character Encoding)

```java
package com.example.filters;

import java.io.IOException;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EncodingFilter implements Filter {
    private static final Logger logger = 
        LoggerFactory.getLogger(EncodingFilter.class);
    
    private String encoding = "UTF-8";
    
    @Override
    public void init(FilterConfig config) throws ServletException {
        String configEncoding = config.getInitParameter("encoding");
        if (configEncoding != null) {
            encoding = configEncoding;
        }
        logger.info("EncodingFilter initialized with encoding: {}", encoding);
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, 
                        FilterChain chain) throws IOException, ServletException {
        // Set request encoding
        request.setCharacterEncoding(encoding);
        
        // Set response encoding
        response.setCharacterEncoding(encoding);
        response.setContentType("text/html; charset=" + encoding);
        
        logger.debug("Set character encoding to: {}", encoding);
        
        chain.doFilter(request, response);
    }
    
    @Override
    public void destroy() {
        logger.info("EncodingFilter destroyed");
    }
}
```

### 6.1.9 web.xml Filter Configuration (Complete Order)

```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="https://jakarta.ee/xml/ns/jakartaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="https://jakarta.ee/xml/ns/jakartaee 
         https://jakarta.ee/xml/ns/jakartaee/web-app_6_0.xsd"
         version="6.0">
    
    <display-name>Advanced JSP Application</display-name>
    
    <!-- FILTERS -->
    <!-- 1. Logging Filter (outermost) -->
    <filter>
        <filter-name>LoggingFilter</filter-name>
        <filter-class>com.example.filters.LoggingFilter</filter-class>
    </filter>
    
    <!-- 2. Encoding Filter -->
    <filter>
        <filter-name>EncodingFilter</filter-name>
        <filter-class>com.example.filters.EncodingFilter</filter-class>
        <init-param>
            <param-name>encoding</param-name>
            <param-value>UTF-8</param-value>
        </init-param>
    </filter>
    
    <!-- 3. Auth Filter -->
    <filter>
        <filter-name>AuthFilter</filter-name>
        <filter-class>com.example.filters.AuthFilter</filter-class>
    </filter>
    
    <!-- 4. Role Filter -->
    <filter>
        <filter-name>RoleFilter</filter-name>
        <filter-class>com.example.filters.RoleFilter</filter-class>
    </filter>
    
    <!-- 5. CORS Filter (innermost) -->
    <filter>
        <filter-name>CORSFilter</filter-name>
        <filter-class>com.example.filters.CORSFilter</filter-class>
    </filter>
    
    <!-- FILTER MAPPINGS (in execution order) -->
    <filter-mapping>
        <filter-name>LoggingFilter</filter-name>
        <url-pattern>/*</url-pattern>
    </filter-mapping>
    
    <filter-mapping>
        <filter-name>EncodingFilter</filter-name>
        <url-pattern>/*</url-pattern>
    </filter-mapping>
    
    <filter-mapping>
        <filter-name>AuthFilter</filter-name>
        <url-pattern>/*</url-pattern>
    </filter-mapping>
    
    <filter-mapping>
        <filter-name>RoleFilter</filter-name>
        <url-pattern>/admin/*</url-pattern>
    </filter-mapping>
    
    <filter-mapping>
        <filter-name>CORSFilter</filter-name>
        <url-pattern>/*</url-pattern>
    </filter-mapping>
    
    <!-- SESSIONS -->
    <session-config>
        <cookie-config>
            <http-only>true</http-only>
            <secure>true</secure>
        </cookie-config>
        <tracking-mode>COOKIE</tracking-mode>
        <timeout>30</timeout>
    </session-config>
</web-app>
```

---

## 🏆 Ví Dụ: Sai vs Đúng

### ❌ Sai: Quên gọi chain.doFilter()

```java
// WRONG - Blocks the request chain!
@Override
public void doFilter(ServletRequest request, ServletResponse response, 
                    FilterChain chain) throws IOException, ServletException {
    HttpServletRequest req = (HttpServletRequest) request;
    
    if (!isAuthorized(req)) {
        // Forgot chain.doFilter() - Request stops here
        return;  // BUG: Response is empty!
    }
    
    // This is unreachable if early return happens
    chain.doFilter(request, response);
}
```

**Vấn đề**: Request bị dừng giữa chừng, client nhận blank response.

### ✅ Đúng: Gọi chain.doFilter() hoặc gửi error

```java
@Override
public void doFilter(ServletRequest request, ServletResponse response, 
                    FilterChain chain) throws IOException, ServletException {
    HttpServletRequest req = (HttpServletRequest) request;
    HttpServletResponse res = (HttpServletResponse) response;
    
    if (!isAuthorized(req)) {
        // Either:
        // 1. Send error response
        res.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        // 2. OR redirect
        res.sendRedirect("/login");
        // 3. OR continue with chain (for logging after unauthorized)
        // chain.doFilter(request, response);
        return;
    }
    
    // Request is authorized, continue to next filter/servlet
    chain.doFilter(request, response);
}
```

### ❌ Sai: Không xử lý exception từ chain

```java
@Override
public void doFilter(ServletRequest request, ServletResponse response, 
                    FilterChain chain) throws IOException, ServletException {
    try {
        // Some pre-filter logic
        chain.doFilter(request, response);  // May throw exception
    } finally {
        // Cleanup - but error is propagated!
    }
}
```

### ✅ Đúng: Xử lý exception properly

```java
@Override
public void doFilter(ServletRequest request, ServletResponse response, 
                    FilterChain chain) throws IOException, ServletException {
    long startTime = System.currentTimeMillis();
    
    try {
        chain.doFilter(request, response);
    } catch (IOException | ServletException e) {
        logger.error("Error in filter chain: {}", e.getMessage(), e);
        throw e;  // Re-throw to container
    } finally {
        long duration = System.currentTimeMillis() - startTime;
        logger.info("Request processed in {} ms", duration);
    }
}
```

---

# Session 37: Servlet Listeners

## 📖 Lý Thuyết: Listener Pattern & Lifecycle Events

### 6.2.1 Listener Types

Servlet API cung cấp 9 loại listeners để track lifecycle events:

| Listener Interface | Event | Khi nào gọi |
|---|---|---|
| **ServletContextListener** | ServletContextEvent | App start/stop |
| **ServletContextAttributeListener** | ServletContextAttributeEvent | Attribute add/remove/update |
| **HttpSessionListener** | HttpSessionEvent | Session create/destroy |
| **HttpSessionAttributeListener** | HttpSessionBindingEvent | Session attribute changes |
| **HttpSessionIdListener** | HttpSessionEvent | Session ID thay đổi |
| **ServletRequestListener** | ServletRequestEvent | Request start/end |
| **ServletRequestAttributeListener** | ServletRequestAttributeEvent | Request attribute changes |
| **HttpSessionActivationListener** | HttpSessionEvent | Session serialization (clustering) |
| **HttpSessionBindingListener** | HttpSessionBindingEvent | Object add/remove từ session |

### 6.2.2 Listener Lifecycle & Event Firing Order

```
APPLICATION STARTUP:
    ↓
ServletContextListener.contextInitialized()
    ├─ Initialize app resources
    ├─ Load configuration
    └─ Create database pools
    
FIRST REQUEST:
    ↓
ServletRequestListener.requestInitialized()
    ├─ Trace request
    └─ Set up thread-local storage
    
SESSION CREATED:
    ↓
HttpSessionListener.sessionCreated()
    ├─ Allocate session storage
    └─ Initialize session attributes
    
REQUEST PROCESSING:
    ├─ HttpSessionAttributeListener.attributeAdded()
    ├─ (Attribute changes)
    └─ HttpSessionAttributeListener.attributeRemoved()
    
REQUEST END:
    ↓
ServletRequestListener.requestDestroyed()
    ├─ Clean up request resources
    └─ Release thread-local storage
    
SESSION TIMEOUT/LOGOUT:
    ↓
HttpSessionListener.sessionDestroyed()
    ├─ Release session memory
    └─ Clean up session storage
    
APPLICATION SHUTDOWN:
    ↓
ServletContextListener.contextDestroyed()
    ├─ Close database pools
    ├─ Release resources
    └─ Save application state
```

### 6.2.3 ServletContextListener Details

```java
public interface ServletContextListener extends EventListener {
    // Called when application starts (before any requests)
    void contextInitialized(ServletContextEvent sce);
    
    // Called when application stops (after all requests/sessions end)
    void contextDestroyed(ServletContextEvent sce);
}

// Event object provides:
public class ServletContextEvent {
    ServletContext getServletContext();
}
```

**Use Cases**:
- Initialize database connection pool
- Load application configuration
- Initialize cache managers
- Start background jobs
- Count active listeners

---

## 🔧 Cách Sử Dụng: Listeners Implementation

### 6.2.4 ApplicationContextListener

```java
package com.example.listeners;

import java.sql.DriverManager;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebListener  // Annotation - no need for web.xml entry
public class ApplicationContextListener implements ServletContextListener {
    
    private static final Logger logger = 
        LoggerFactory.getLogger(ApplicationContextListener.class);
    
    private ScheduledExecutorService scheduledExecutor;
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();
        
        logger.info("=== Application Starting ===");
        
        // 1. Load application configuration
        String appVersion = context.getInitParameter("app.version");
        String appName = context.getInitParameter("app.name");
        logger.info("  App: {} v{}", appName, appVersion);
        
        // Store in context (accessible to entire application)
        context.setAttribute("APP_VERSION", appVersion);
        context.setAttribute("APP_NAME", appName);
        
        try {
            // 2. Initialize database connection pool
            // (Assuming you have HikariCP or DBCP)
            logger.info("  Initializing database connection pool...");
            DatabasePool.init();
            context.setAttribute("DB_POOL", DatabasePool.getPool());
            logger.info("  ✓ Database pool initialized");
            
            // 3. Initialize cache manager
            logger.info("  Initializing cache manager...");
            CacheManager.init();
            context.setAttribute("CACHE_MANAGER", CacheManager.getInstance());
            logger.info("  ✓ Cache manager initialized");
            
            // 4. Load configuration from file
            logger.info("  Loading application config...");
            ConfigLoader.loadConfig(context);
            logger.info("  ✓ Config loaded");
            
            // 5. Start background scheduler for cleanup tasks
            logger.info("  Starting background scheduler...");
            scheduledExecutor = Executors.newScheduledThreadPool(2);
            
            // Schedule session cleanup every 5 minutes
            scheduledExecutor.scheduleAtFixedRate(() -> {
                logger.debug("Running scheduled cleanup tasks");
                DatabasePool.cleanupExpiredSessions();
                CacheManager.evictExpiredEntries();
            }, 5, 5, java.util.concurrent.TimeUnit.MINUTES);
            
            context.setAttribute("SCHEDULER", scheduledExecutor);
            logger.info("  ✓ Scheduler started");
            
            // 6. Initialize monitoring/statistics
            logger.info("  Initializing application statistics...");
            AppStatistics stats = new AppStatistics();
            context.setAttribute("STATISTICS", stats);
            logger.info("  ✓ Statistics initialized");
            
            logger.info("=== Application Ready ===\n");
            
        } catch (Exception e) {
            logger.error("Failed to initialize application", e);
            throw new RuntimeException("Application initialization failed", e);
        }
    }
    
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logger.info("=== Application Shutting Down ===");
        
        try {
            // 1. Stop scheduler
            if (scheduledExecutor != null && !scheduledExecutor.isShutdown()) {
                logger.info("  Stopping scheduler...");
                scheduledExecutor.shutdown();
                logger.info("  ✓ Scheduler stopped");
            }
            
            // 2. Flush cache
            logger.info("  Flushing cache...");
            CacheManager.flush();
            logger.info("  ✓ Cache flushed");
            
            // 3. Close database pool
            logger.info("  Closing database pool...");
            DatabasePool.close();
            logger.info("  ✓ Database pool closed");
            
            // 4. Save statistics
            ServletContext context = sce.getServletContext();
            AppStatistics stats = (AppStatistics) context.getAttribute("STATISTICS");
            if (stats != null) {
                logger.info("  Saving application statistics...");
                stats.saveToDatabase();
                logger.info("  ✓ Statistics saved");
            }
            
            logger.info("=== Application Shutdown Complete ===\n");
            
        } catch (Exception e) {
            logger.error("Error during application shutdown", e);
        }
    }
}
```

### 6.2.5 HttpSessionListener

```java
package com.example.listeners;

import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import jakarta.servlet.annotation.WebListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

@WebListener
public class SessionListener implements HttpSessionListener {
    
    private static final Logger logger = 
        LoggerFactory.getLogger(SessionListener.class);
    
    // Thread-safe counter
    private static final AtomicInteger activeSessions = new AtomicInteger(0);
    private static final AtomicInteger totalSessions = new AtomicInteger(0);
    private static long maxSessionsEveSeen = 0;
    
    @Override
    public void sessionCreated(HttpSessionEvent se) {
        String sessionId = se.getSession().getId();
        int count = activeSessions.incrementAndGet();
        totalSessions.incrementAndGet();
        
        if (count > maxSessionsEveSeen) {
            maxSessionsEveSeen = count;
        }
        
        logger.info("✓ Session created: {} (Active sessions: {}, Peak: {})", 
                   sessionId, count, maxSessionsEveSeen);
        
        // Store creation time in session
        se.getSession().setAttribute("SESSION_CREATED", LocalDateTime.now());
        
        // Trigger event to backend
        logSessionActivity("CREATE", sessionId);
    }
    
    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        String sessionId = se.getSession().getId();
        int count = activeSessions.decrementAndGet();
        
        logger.info("✗ Session destroyed: {} (Active sessions: {})", 
                   sessionId, count);
        
        // Clean up user-specific resources
        Object userId = se.getSession().getAttribute("userId");
        if (userId != null) {
            // Release user resources (file uploads, temp files, etc.)
            logger.debug("  Cleaning up resources for user: {}", userId);
        }
        
        // Log session end
        logSessionActivity("DESTROY", sessionId);
    }
    
    // Helper: Log session activity to database for auditing
    private void logSessionActivity(String action, String sessionId) {
        // In production: Save to database for audit trail
        // For now: Just log
    }
    
    // Static methods to access statistics
    public static int getActiveSessions() {
        return activeSessions.get();
    }
    
    public static int getTotalSessions() {
        return totalSessions.get();
    }
    
    public static long getMaxSessionsEverSeen() {
        return maxSessionsEveSeen;
    }
}
```

### 6.2.6 HttpSessionAttributeListener

```java
package com.example.listeners;

import jakarta.servlet.http.HttpSessionAttributeListener;
import jakarta.servlet.http.HttpSessionBindingEvent;
import jakarta.servlet.annotation.WebListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebListener
public class SessionAttributeListener implements HttpSessionAttributeListener {
    
    private static final Logger logger = 
        LoggerFactory.getLogger(SessionAttributeListener.class);
    
    @Override
    public void attributeAdded(HttpSessionBindingEvent event) {
        String sessionId = event.getSession().getId();
        String attributeName = event.getName();
        Object attributeValue = event.getValue();
        
        logger.debug("Session {} - Attribute added: {} = {}", 
                    sessionId, attributeName, attributeValue);
        
        // You can put business logic here
        if ("userId".equals(attributeName)) {
            logger.info("User {} logged in to session {}", attributeValue, sessionId);
        }
    }
    
    @Override
    public void attributeRemoved(HttpSessionBindingEvent event) {
        String sessionId = event.getSession().getId();
        String attributeName = event.getName();
        
        logger.debug("Session {} - Attribute removed: {}", sessionId, attributeName);
        
        if ("userId".equals(attributeName)) {
            logger.info("User logged out from session {}", sessionId);
        }
    }
    
    @Override
    public void attributeReplaced(HttpSessionBindingEvent event) {
        String sessionId = event.getSession().getId();
        String attributeName = event.getName();
        Object newValue = event.getValue();
        
        logger.debug("Session {} - Attribute updated: {} = {}", 
                    sessionId, attributeName, newValue);
    }
}
```

### 6.2.7 ServletRequestListener

```java
package com.example.listeners;

import jakarta.servlet.ServletRequestEvent;
import jakarta.servlet.ServletRequestListener;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.annotation.WebListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

@WebListener
public class RequestListener implements ServletRequestListener {
    
    private static final Logger logger = 
        LoggerFactory.getLogger(RequestListener.class);
    
    @Override
    public void requestInitialized(ServletRequestEvent sre) {
        HttpServletRequest request = (HttpServletRequest) sre.getServletRequest();
        String requestId = java.util.UUID.randomUUID().toString();
        
        // Store request ID in thread-local MDC (Mapped Diagnostic Context)
        // Useful for logging correlation
        MDC.put("requestId", requestId);
        MDC.put("method", request.getMethod());
        MDC.put("path", request.getRequestURI());
        
        // Store request start time for duration calculation
        request.setAttribute("REQUEST_START_TIME", System.currentTimeMillis());
        
        logger.debug("Request started - ID: {}", requestId);
    }
    
    @Override
    public void requestDestroyed(ServletRequestEvent sre) {
        HttpServletRequest request = (HttpServletRequest) sre.getServletRequest();
        
        // Calculate request duration
        Long startTime = (Long) request.getAttribute("REQUEST_START_TIME");
        if (startTime != null) {
            long duration = System.currentTimeMillis() - startTime;
            logger.debug("Request completed in {} ms", duration);
        }
        
        // Clear MDC
        MDC.clear();
    }
}
```

### 6.2.8 web.xml Listener Registration

```xml
<!-- If not using @WebListener annotation -->
<listener>
    <listener-class>com.example.listeners.ApplicationContextListener</listener-class>
</listener>

<listener>
    <listener-class>com.example.listeners.SessionListener</listener-class>
</listener>

<listener>
    <listener-class>com.example.listeners.SessionAttributeListener</listener-class>
</listener>

<listener>
    <listener-class>com.example.listeners.RequestListener</listener-class>
</listener>

<!-- Order matters: Context listeners first, then Session, then Request -->
```

---

## 🏆 Ví Dụ: Sai vs Đúng

### ❌ Sai: Blocking operation in contextInitialized

```java
@Override
public void contextInitialized(ServletContextEvent sce) {
    // WRONG: This blocks application startup for 10 seconds!
    Thread.sleep(10000);  
    
    // Users can't access app while initialization runs
    initializeDatabaseAsync();  // Returns immediately but not actually async
}
```

### ✅ Đúng: Use proper async initialization

```java
@Override
public void contextInitialized(ServletContextEvent sce) {
    // RIGHT: Use scheduled executor for async tasks
    ScheduledExecutorService executor = 
        Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "AppInitializer");
            t.setDaemon(true);
            return t;
        });
    
    // Submit initialization task asynchronously
    executor.execute(() -> {
        try {
            logger.info("Starting expensive initialization...");
            Thread.sleep(10000);  // OK now - doesn't block startup
            DatabasePool.init();
            logger.info("Initialization completed");
        } catch (Exception e) {
            logger.error("Initialization failed", e);
        }
    });
}
```

### ❌ Sai: Not thread-safe session counter

```java
public class SessionListener implements HttpSessionListener {
    
    private int activeSessions = 0;  // NOT thread-safe!
    
    @Override
    public void sessionCreated(HttpSessionEvent se) {
        activeSessions++;  // Race condition: multiple threads!
    }
    
    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        activeSessions--;  // Counter becomes unreliable
    }
}
```

### ✅ Đúng: Use AtomicInteger

```java
public class SessionListener implements HttpSessionListener {
    
    private static final AtomicInteger activeSessions = 
        new AtomicInteger(0);
    
    @Override
    public void sessionCreated(HttpSessionEvent se) {
        activeSessions.incrementAndGet();  // Thread-safe atomic operation
    }
    
    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        activeSessions.decrementAndGet();  // Thread-safe atomic operation
    }
    
    public static int getActiveSessions() {
        return activeSessions.get();
    }
}
```

---

# Session 38: File Upload & Download

## 📖 Lý Thuyết: File Handling in Web Applications

### 6.3.1 File Upload Flow

```
Client Browser:
    ↓
Select file + Submit form (multipart/form-data)
    ↓
Browser encodes as multipart stream
    ↓
HTTP Request → Server
    ↓
Servlet receives request
    ↓
Parse multipart form data
    ├─ Extract file bytes
    ├─ Extract form fields
    └─ Validate before storage
    ↓
Save to disk or database
    ↓
Response with status/confirmation
    ↓
Client Browser shows result
```

### 6.3.2 Multipart Form Data Format

```
----WebKitFormBoundary7MA4YWxkTrZu0gW
Content-Disposition: form-data; name="username"

john_doe
----WebKitFormBoundary7MA4YWxkTrZu0gW
Content-Disposition: form-data; name="avatar"; filename="photo.jpg"
Content-Type: image/jpeg

[BINARY IMAGE DATA HERE]
----WebKitFormBoundary7MA4YWxkTrZu0gW--
```

### 6.3.3 Apache Commons FileUpload Setup

**pom.xml**:
```xml
<dependency>
    <groupId>commons-fileupload</groupId>
    <artifactId>commons-fileupload</artifactId>
    <version>1.5</version>
</dependency>

<dependency>
    <groupId>commons-io</groupId>
    <artifactId>commons-io</artifactId>
    <version>2.11.0</version>
</dependency>
```

---

## 🔧 Cách Sử Dụng: File Upload Servlet

### 6.3.4 FileUploadServlet Implementation

```java
package com.example.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

@WebServlet("/fileupload")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,      // 1 MB
    maxFileSize = 10 * 1024 * 1024,       // 10 MB per file
    maxRequestSize = 50 * 1024 * 1024,    // 50 MB per request
    location = "/tmp/uploads"              // Temp directory
)
public class FileUploadServlet extends HttpServlet {
    
    private static final Logger logger = 
        LoggerFactory.getLogger(FileUploadServlet.class);
    
    private static final String UPLOAD_DIR = "uploads";
    private static final Set<String> ALLOWED_EXTENSIONS = 
        new HashSet<>(Arrays.asList("jpg", "jpeg", "png", "pdf", "doc", "docx", "txt"));
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;  // 10 MB
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Show upload form
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        out.println("<html>");
        out.println("<body>");
        out.println("<h2>File Upload</h2>");
        out.println("<form method='POST' enctype='multipart/form-data'>");
        out.println("  <input type='file' name='file' required />");
        out.println("  <input type='text' name='description' placeholder='File description' />");
        out.println("  <button type='submit'>Upload</button>");
        out.println("</form>");
        out.println("</body>");
        out.println("</html>");
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        // Check if request contains multipart form data
        if (!ServletFileUpload.isMultipartContent(request)) {
            logger.warn("Non-multipart request received");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\": \"Request must be multipart/form-data\"}");
            return;
        }
        
        try {
            // Configure file upload
            DiskFileItemFactory factory = new DiskFileItemFactory();
            factory.setSizeThreshold(1024 * 1024);  // 1 MB
            
            ServletFileUpload upload = new ServletFileUpload(factory);
            upload.setFileSizeMax(MAX_FILE_SIZE);
            
            // Parse request for file items
            List<FileItem> items = upload.parseRequest(request);
            
            String description = "";
            String uploadedFileName = "";
            
            for (FileItem item : items) {
                if (item.isFormField()) {
                    // Regular form field
                    if ("description".equals(item.getFieldName())) {
                        description = item.getString();
                    }
                } else {
                    // File field
                    String fileName = new File(item.getName()).getName();
                    
                    // Validate file
                    ValidationResult validation = validateFile(fileName, item.getSize());
                    if (!validation.isValid) {
                        logger.warn("File validation failed: {}", validation.message);
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        out.print("{\"error\": \"" + validation.message + "\"}");
                        return;
                    }
                    
                    // Sanitize filename
                    String sanitizedName = sanitizeFilename(fileName);
                    
                    // Create unique filename
                    String uniqueName = generateUniqueFilename(sanitizedName);
                    
                    // Create upload directory
                    Path uploadPath = Paths.get(UPLOAD_DIR);
                    if (!Files.exists(uploadPath)) {
                        Files.createDirectories(uploadPath);
                        logger.info("Created upload directory: {}", uploadPath);
                    }
                    
                    // Save file
                    Path filePath = uploadPath.resolve(uniqueName);
                    item.write(filePath.toFile());
                    
                    uploadedFileName = uniqueName;
                    logger.info("File uploaded successfully: {} ({}bytes)", 
                               uniqueName, item.getSize());
                }
            }
            
            // Save file metadata to database
            saveFileMetadata(uploadedFileName, description, request
                .getSession().getAttribute("userId").toString());
            
            // Return success response
            response.setStatus(HttpServletResponse.SC_OK);
            out.print("{\"success\": true, \"fileName\": \"" + uploadedFileName + "\"}");
            
        } catch (Exception e) {
            logger.error("Error uploading file", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"Upload failed: " + e.getMessage() + "\"}");
        }
    }
    
    // Validation
    private ValidationResult validateFile(String fileName, long fileSize) {
        // Check file extension
        String extension = FilenameUtils.getExtension(fileName).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            return new ValidationResult(false, 
                "File type not allowed. Allowed: " + ALLOWED_EXTENSIONS);
        }
        
        // Check file size
        if (fileSize > MAX_FILE_SIZE) {
            return new ValidationResult(false, 
                "File too large. Maximum: 10 MB");
        }
        
        if (fileSize == 0) {
            return new ValidationResult(false, "File is empty");
        }
        
        return new ValidationResult(true, "");
    }
    
    // Sanitize filename to prevent path traversal
    private String sanitizeFilename(String fileName) {
        // Remove path separators
        fileName = fileName.replaceAll("[\\\\/]", "");
        // Remove special characters
        fileName = fileName.replaceAll("[^a-zA-Z0-9._-]", "_");
        return fileName;
    }
    
    // Generate unique filename to prevent overwrites
    private String generateUniqueFilename(String originalName) {
        String name = FilenameUtils.getBaseName(originalName);
        String ext = FilenameUtils.getExtension(originalName);
        String timestamp = System.currentTimeMillis() + "";
        return name + "_" + timestamp + "." + ext;
    }
    
    // Save metadata to database
    private void saveFileMetadata(String fileName, String description, String userId) {
        // TODO: Insert into files table
        // INSERT INTO uploaded_files (file_name, description, user_id, uploaded_at) ...
    }
    
    private static class ValidationResult {
        boolean isValid;
        String message;
        
        ValidationResult(boolean isValid, String message) {
            this.isValid = isValid;
            this.message = message;
        }
    }
}
```

### 6.3.5 File Download Servlet

```java
package com.example.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@WebServlet("/download")
public class FileDownloadServlet extends HttpServlet {
    
    private static final Logger logger = 
        LoggerFactory.getLogger(FileDownloadServlet.class);
    
    private static final String UPLOAD_DIR = "uploads";
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String fileName = request.getParameter("file");
        
        // Security: Prevent path traversal
        if (fileName == null || fileName.contains("..") || fileName.contains("/")) {
            logger.warn("Invalid file name requested: {}", fileName);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, 
                "Invalid file name");
            return;
        }
        
        Path filePath = Paths.get(UPLOAD_DIR, fileName);
        
        // Check if file exists
        if (!Files.exists(filePath)) {
            logger.warn("Download requested for non-existent file: {}", fileName);
            response.sendError(HttpServletResponse.SC_NOT_FOUND, 
                "File not found");
            return;
        }
        
        // Check if user has permission to download
        Object userId = request.getSession().getAttribute("userId");
        if (!hasDownloadPermission(userId.toString(), fileName)) {
            logger.warn("User {} unauthorized to download {}", userId, fileName);
            response.sendError(HttpServletResponse.SC_FORBIDDEN, 
                "Unauthorized");
            return;
        }
        
        try {
            // Get file info
            File file = filePath.toFile();
            long fileSize = file.length();
            
            // Set response headers
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Length", String.valueOf(fileSize));
            response.setHeader("Content-Disposition", 
                "attachment; filename=\"" + file.getName() + "\"");
            response.setHeader("Cache-Control", "no-cache");
            
            // Stream file to client
            try (InputStream in = new FileInputStream(file);
                 OutputStream out = response.getOutputStream()) {
                
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
                out.flush();
            }
            
            logger.info("File downloaded: {}", fileName);
            
            // Log download activity
            logDownloadActivity(userId.toString(), fileName);
            
        } catch (IOException e) {
            logger.error("Error downloading file: {}", fileName, e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "Download failed");
        }
    }
    
    private boolean hasDownloadPermission(String userId, String fileName) {
        // TODO: Check database - verify user owns this file
        // SELECT COUNT(*) FROM uploaded_files 
        // WHERE user_id = ? AND file_name = ?
        return true;  // Placeholder
    }
    
    private void logDownloadActivity(String userId, String fileName) {
        // TODO: Insert download log to database
    }
}
```

### 6.3.6 HTML Form for Upload

```html
<!DOCTYPE html>
<html>
<head>
    <title>File Upload</title>
    <style>
        .upload-form {
            max-width: 500px;
            margin: 50px auto;
            padding: 20px;
            border: 1px solid #ddd;
            border-radius: 5px;
        }
        .form-group {
            margin-bottom: 15px;
        }
        label {
            display: block;
            margin-bottom: 5px;
            font-weight: bold;
        }
        input[type="file"],
        input[type="text"],
        textarea {
            width: 100%;
            padding: 8px;
            border: 1px solid #ccc;
            border-radius: 3px;
            box-sizing: border-box;
        }
        button {
            background-color: #4CAF50;
            color: white;
            padding: 10px 20px;
            border: none;
            border-radius: 3px;
            cursor: pointer;
        }
        button:hover {
            background-color: #45a049;
        }
        #progress {
            display: none;
            width: 100%;
            height: 20px;
            background-color: #f0f0f0;
            border-radius: 3px;
            margin-top: 10px;
            overflow: hidden;
        }
        #progressBar {
            height: 100%;
            width: 0;
            background-color: #4CAF50;
            transition: width 0.3s;
        }
    </style>
</head>
<body>
    <div class="upload-form">
        <h2>Upload File</h2>
        <form id="uploadForm" enctype="multipart/form-data">
            <div class="form-group">
                <label for="file">Select File:</label>
                <input type="file" id="file" name="file" required />
                <small>Max size: 10 MB. Allowed: jpg, png, pdf, doc, txt</small>
            </div>
            <div class="form-group">
                <label for="description">Description:</label>
                <textarea id="description" name="description" rows="3"></textarea>
            </div>
            <button type="submit">Upload</button>
        </form>
        
        <div id="progress">
            <div id="progressBar"></div>
        </div>
        <div id="message"></div>
    </div>
    
    <script>
        document.getElementById('uploadForm').addEventListener('submit', function(e) {
            e.preventDefault();
            
            const formData = new FormData(this);
            const progressDiv = document.getElementById('progress');
            const progressBar = document.getElementById('progressBar');
            const messageDiv = document.getElementById('message');
            
            progressDiv.style.display = 'block';
            messageDiv.innerText = '';
            
            const xhr = new XMLHttpRequest();
            
            xhr.upload.addEventListener('progress', function(e) {
                if (e.lengthComputable) {
                    const percent = (e.loaded / e.total) * 100;
                    progressBar.style.width = percent + '%';
                }
            });
            
            xhr.addEventListener('load', function() {
                progressDiv.style.display = 'none';
                const response = JSON.parse(xhr.responseText);
                
                if (response.success) {
                    messageDiv.style.color = 'green';
                    messageDiv.innerText = 'File uploaded: ' + response.fileName;
                    document.getElementById('uploadForm').reset();
                } else {
                    messageDiv.style.color = 'red';
                    messageDiv.innerText = 'Error: ' + response.error;
                }
            });
            
            xhr.addEventListener('error', function() {
                progressDiv.style.display = 'none';
                messageDiv.style.color = 'red';
                messageDiv.innerText = 'Upload failed';
            });
            
            xhr.open('POST', '/fileupload');
            xhr.send(formData);
        });
    </script>
</body>
</html>
```

---

## 🏆 Ví Dụ: Sai vs Đúng

### ❌ Sai: No file validation

```java
@Override
protected void doPost(HttpServletRequest request, HttpServletResponse response) {
    Part filePart = request.getPart("file");
    String fileName = filePart.getSubmittedFileName();
    
    // WRONG: No validation!
    Path uploadPath = Paths.get("uploads", fileName);  
    // What if fileName is "../../etc/passwd"? 
    // What if file is 500 MB?
    // What if it's .exe malware?
    
    filePart.write(uploadPath.toString());
}
```

### ✅ Đúng: Full validation & sanitization

```java
@Override
protected void doPost(HttpServletRequest request, HttpServletResponse response) {
    Part filePart = request.getPart("file");
    String fileName = filePart.getSubmittedFileName();
    
    // 1. Sanitize filename
    String sanitized = sanitizeFilename(fileName);
    
    // 2. Validate extension
    String ext = FilenameUtils.getExtension(sanitized).toLowerCase();
    if (!ALLOWED_EXTENSIONS.contains(ext)) {
        throw new SecurityException("File type not allowed");
    }
    
    // 3. Check size
    if (filePart.getSize() > MAX_SIZE) {
        throw new SecurityException("File too large");
    }
    
    // 4. Generate unique name to prevent overwrites
    String uniqueName = generateUniqueFilename(sanitized);
    
    // 5. Save with full path validation
    Path uploadDir = Paths.get("uploads").toAbsolutePath();
    Path filePath = uploadDir.resolve(uniqueName).toAbsolutePath();
    
    // Verify the result path is still within uploadDir
    if (!filePath.getParent().equals(uploadDir)) {
        throw new SecurityException("Invalid file path");
    }
    
    filePart.write(filePath.toString());
}
```

---

# Session 39: Internationalization (i18n)

## 📖 Lý Thuyết: Multi-language Support

### 6.4.1 i18n Concepts

**Localization (L10n)** - Adapting app for specific language/region
**Internationalization (i18n)** - Designing app to support multiple languages

**Key Components**:
1. **Locale** - Language + Country (e.g., "en_US", "vi_VN", "fr_FR")
2. **ResourceBundle** - Dictionary of translations
3. **MessageFormat** - Template for dynamic messages

### 6.4.2 Locale Priorities

```
1. User explicitly selects language
   ↓ (stored in session/cookie)
2. Browser Accept-Language header
   ↓ (sent by browser)
3. Server default locale
   ↓ (fallback)
```

### 6.4.3 ResourceBundle File Format

**src/main/resources/messages_en.properties**:
```properties
app.title=Welcome to My App
app.description=This is a great application
app.language=English

user.login=Login
user.logout=Logout
user.profile=My Profile
user.settings=Settings

form.submit=Submit
form.cancel=Cancel
form.email=Email Address
form.password=Password

error.auth=Invalid username or password
error.email=Invalid email format
error.file=File upload failed

message.welcome=Welcome, {0}!
message.upload=File {0} uploaded successfully
```

**src/main/resources/messages_vi.properties**:
```properties
app.title=Chào mừng đến ứng dụng của tôi
app.description=Đây là một ứng dụng tuyệt vời
app.language=Tiếng Việt

user.login=Đăng nhập
user.logout=Đăng xuất
user.profile=Hồ sơ của tôi
user.settings=Cài đặt

form.submit=Gửi
form.cancel=Hủy
form.email=Địa chỉ Email
form.password=Mật khẩu

error.auth=Tên người dùng hoặc mật khẩu không hợp lệ
error.email=Định dạng email không hợp lệ
error.file=Tải lên tập tin thất bại

message.welcome=Chào mừng, {0}!
message.upload=Tập tin {0} đã được tải lên thành công
```

---

## 🔧 Cách Sử Dụng: i18n Implementation

### 6.4.4 LocaleResolver Servlet

```java
package com.example.i18n;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Locale;

@WebServlet("/locale")
public class LocaleResolverServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Get language parameter
        String lang = request.getParameter("lang");
        
        if (lang != null) {
            // Create locale
            Locale locale;
            if ("en".equals(lang)) {
                locale = new Locale("en", "US");
            } else if ("vi".equals(lang)) {
                locale = new Locale("vi", "VN");
            } else if ("fr".equals(lang)) {
                locale = new Locale("fr", "FR");
            } else {
                locale = Locale.getDefault();  // Fallback
            }
            
            // Store in session
            request.getSession().setAttribute("locale", locale);
            
            // Also store in cookie (persistent)
            jakarta.servlet.http.Cookie cookie = 
                new jakarta.servlet.http.Cookie("locale", lang);
            cookie.setMaxAge(30 * 24 * 60 * 60);  // 30 days
            response.addCookie(cookie);
        }
        
        // Redirect back to referrer
        String referer = request.getHeader("Referer");
        response.sendRedirect(referer != null ? referer : "/");
    }
}
```

### 6.4.5 I18n Utility Class

```java
package com.example.i18n;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public class I18nUtil {
    
    private static final String BUNDLE_NAME = "messages";
    
    // Get translated message
    public static String getMessage(String key, Locale locale) {
        try {
            ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_NAME, locale);
            return bundle.getString(key);
        } catch (Exception e) {
            // Return key if not found
            return key;
        }
    }
    
    // Get translated message with parameters
    public static String getMessage(String key, Locale locale, Object... params) {
        try {
            ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_NAME, locale);
            String template = bundle.getString(key);
            return MessageFormat.format(template, params);
        } catch (Exception e) {
            return key;
        }
    }
    
    // Get locale from request
    public static Locale getLocale(jakarta.servlet.http.HttpServletRequest request) {
        // 1. Check session
        Locale locale = (Locale) request.getSession().getAttribute("locale");
        if (locale != null) {
            return locale;
        }
        
        // 2. Check cookie
        jakarta.servlet.http.Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (jakarta.servlet.http.Cookie cookie : cookies) {
                if ("locale".equals(cookie.getName())) {
                    String langCode = cookie.getValue();
                    if ("en".equals(langCode)) {
                        return new Locale("en", "US");
                    } else if ("vi".equals(langCode)) {
                        return new Locale("vi", "VN");
                    }
                }
            }
        }
        
        // 3. Check Accept-Language header
        String acceptLanguage = request.getHeader("Accept-Language");
        if (acceptLanguage != null && !acceptLanguage.isEmpty()) {
            // Parse the first language preference
            String[] langs = acceptLanguage.split(",")[0].split("-");
            if (langs.length > 0) {
                return new Locale(langs[0]);
            }
        }
        
        // 4. Default
        return new Locale("en", "US");
    }
}
```

### 6.4.6 Using i18n in Servlet

```java
package com.example.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.example.i18n.I18nUtil;
import java.io.IOException;
import java.util.Locale;

@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        Locale locale = I18nUtil.getLocale(request);
        
        // Get translations
        String title = I18nUtil.getMessage("app.title", locale);
        String profileLabel = I18nUtil.getMessage("user.profile", locale);
        String logoutLabel = I18nUtil.getMessage("user.logout", locale);
        
        // Put in request for JSP
        request.setAttribute("title", title);
        request.setAttribute("profileLabel", profileLabel);
        request.setAttribute("logoutLabel", logoutLabel);
        request.setAttribute("locale", locale);
        
        request.getRequestDispatcher("/views/profile.jsp").forward(request, response);
    }
}
```

### 6.4.7 Using i18n in JSP

```jsp
<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>${title}</title>
</head>
<body>
    <!-- Language Selector -->
    <div class="language-selector">
        <a href="?lang=en">English</a> | 
        <a href="?lang=vi">Tiếng Việt</a> | 
        <a href="?lang=fr">Français</a>
    </div>
    
    <h1>${profileLabel}</h1>
    
    <div class="profile-info">
        <p><strong><fmt:message key="form.email" bundle="${messages}" />:</strong> ${user.email}</p>
        <p><strong><fmt:message key="user.profile" bundle="${messages}" />:</strong> ${user.username}</p>
    </div>
    
    <a href="/logout">${logoutLabel}</a>
    
    <script>
        // Welcome message with parameter
        const welcomeMsg = '<c:out value="${welcomeMessage}" />';
        console.log(welcomeMsg);
    </script>
</body>
</html>
```

### 6.4.8 JSTL fmt Tag Library (in JSP)

```jsp
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!-- Create ResourceBundle -->
<fmt:setBundle basename="messages" var="msg" />

<!-- Get message -->
<h1><fmt:message key="app.title" bundle="${msg}" /></h1>

<!-- Get message with parameters -->
<p><fmt:message key="message.welcome" bundle="${msg}">
    <fmt:param value="John" />
</fmt:message></p>

<!-- Format date according to locale -->
<p><fmt:formatDate value="${user.createdDate}" pattern="dd/MM/yyyy" /></p>

<!-- Format number with locale -->
<p>Price: <fmt:formatNumber value="${product.price}" type="currency" /></p>
```

---

## 🏆 Ví Dụ: Sai vs Đúng

### ❌ Sai: Hard-coded strings

```jsp
<h1>Welcome</h1>
<p>Login to your account</p>
<input type="submit" value="Sign In" />

<!-- If you need Vietnamese: Create separate JSP file - NIGHTMARE! -->
<!-- vi/login.jsp, en/login.jsp, fr/login.jsp, ... -->
<!-- Duplicate code everywhere! -->
```

### ✅ Đúng: Use ResourceBundle + i18n

```jsp
<fmt:setBundle basename="messages" var="msg" />

<h1><fmt:message key="app.welcome" bundle="${msg}" /></h1>
<p><fmt:message key="app.login_instruction" bundle="${msg}" /></p>
<input type="submit" value="<fmt:message key="form.submit" bundle="${msg}" />" />

<!-- Single JSP file for all languages! -->
<!-- Just add messages_en.properties, messages_vi.properties, messages_fr.properties -->
```

---

# Session 40: MVC Refactoring - Service Layer

## 📖 Lý Thuyết: Layered Architecture

### 6.5.1 MVC → MVS (Model-View-Service)

**Problem with pure MVC**:
```
Servlet (Controller) directly talks to DAO (Model)
    │
    ├─ Business logic gets mixed in servlet
    ├─ Hard to test (servlet depends on DAO)
    ├─ Code reuse difficult (business logic trapped in servlet)
    └─ Multiple servlets duplicate logic
```

**Solution: Add Service Layer**:
```
Servlet (Controller)
    ↓
Service (Business Logic)
    ↓
DAO (Data Access)
    ↓
Database
```

### 6.5.2 Layer Responsibilities

**Controller (Servlet)**:
- Parse HTTP request
- Call appropriate Service method
- Handle response/errors
- Return view/redirect

**Service**:
- Implement business logic
- Validate data
- Coordinate DAOs
- Handle transactions
- **No servlet/HTTP knowledge**

**DAO**:
- CRUD operations only
- SQL execution
- Result mapping
- **No business logic**

---

## 🔧 Cách Sử Dụng: Service Layer Pattern

### 6.5.3 Service Interface & Implementation

**UserService.java**:
```java
package com.example.services;

import com.example.models.User;
import java.util.List;

public interface UserService {
    
    // Register new user
    User registerUser(String username, String email, String password) 
        throws UserServiceException;
    
    // Authenticate user
    User authenticateUser(String email, String password) 
        throws UserServiceException;
    
   // Get user by ID
    User getUserById(int id) throws UserServiceException;
    
    // Update user profile
    void updateUserProfile(int userId, String email, String username) 
        throws UserServiceException;
    
    // Change password
    void changePassword(int userId, String oldPassword, String newPassword) 
        throws UserServiceException;
    
    // Get all users (admin only)
    List<User> getAllUsers() throws UserServiceException;
    
    // Delete user
    void deleteUser(int userId) throws UserServiceException;
    
    // Check email exists
    boolean emailExists(String email) throws UserServiceException;
    
    // Check username exists
    boolean usernameExists(String username) throws UserServiceException;
}
```

**UserServiceImpl.java**:
```java
package com.example.services;

import com.example.dao.UserDAO;
import com.example.dao.UserDAOImpl;
import com.example.models.User;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.regex.Pattern;

public class UserServiceImpl implements UserService {
    
    private static final Logger logger = 
        LoggerFactory.getLogger(UserServiceImpl.class);
    
    private UserDAO userDAO;
    
    // Constructor injection
    public UserServiceImpl(UserDAO userDAO) {
        this.userDAO = userDAO;
    }
    
    // Dependency injection with default
    public UserServiceImpl() {
        this(new UserDAOImpl());
    }
    
    @Override
    public User registerUser(String username, String email, String password) 
            throws UserServiceException {
        
        // 1. Validation
        if (username == null || username.trim().isEmpty()) {
            throw new UserServiceException("Username is required");
        }
        if (username.length() < 4) {
            throw new UserServiceException("Username must be at least 4 characters");
        }
        
        if (!isValidEmail(email)) {
            throw new UserServiceException("Invalid email format");
        }
        
        if (password == null || password.length() < 6) {
            throw new UserServiceException("Password must be at least 6 characters");
        }
        
        // 2. Check duplicates
        try {
            if (userDAO.isUsernameTaken(username)) {
                throw new UserServiceException("Username already exists");
            }
            if (userDAO.isEmailTaken(email)) {
                throw new UserServiceException("Email already registered");
            }
        } catch (Exception e) {
            logger.error("Error checking duplicate user", e);
            throw new UserServiceException("Registration failed", e);
        }
        
        // 3. Hash password
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        
        // 4. Create user object
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPasswordHash(hashedPassword);
        newUser.setRole("user");
        newUser.setActive(true);
        
        // 5. Save to database
        try {
            User savedUser = userDAO.save(newUser);
            logger.info("User registered: {}", username);
            return savedUser;
        } catch (Exception e) {
            logger.error("Error saving user", e);
            throw new UserServiceException("Registration failed", e);
        }
    }
    
    @Override
    public User authenticateUser(String email, String password) 
            throws UserServiceException {
        
        // 1. Validate input
        if (email == null || email.isEmpty()) {
            throw new UserServiceException("Email is required");
        }
        if (password == null || password.isEmpty()) {
            throw new UserServiceException("Password is required");
        }
        
        // 2. Find user by email
        User user;
        try {
            user = userDAO.findByEmail(email);
        } catch (Exception e) {
            logger.error("Error finding user by email", e);
            throw new UserServiceException("Authentication failed", e);
        }
        
        // 3. Check if user exists
        if (user == null) {
            // IMPORTANT: Don't reveal if email doesn't exist (security best practice)
            logger.warn("Login attempt with non-existent email: {}", email);
            throw new UserServiceException("Invalid email or password");
        }
        
        // 4. Check if user is active
        if (!user.isActive()) {
            logger.warn("Login attempt for inactive user: {}", email);
            throw new UserServiceException("Account is disabled");
        }
        
        // 5. Verify password using BCrypt
        if (!BCrypt.checkpw(password, user.getPasswordHash())) {
            logger.warn("Failed login attempt for user: {}", email);
            throw new UserServiceException("Invalid email or password");
        }
        
        logger.info("User authenticated: {}", email);
        return user;
    }
    
    @Override
    public User getUserById(int id) throws UserServiceException {
        try {
            User user = userDAO.findById(id);
            if (user == null) {
                throw new UserServiceException("User not found");
            }
            return user;
        } catch (Exception e) {
            logger.error("Error fetching user by ID", e);
            throw new UserServiceException("Failed to get user", e);
        }
    }
    
    @Override
    public void updateUserProfile(int userId, String email, String username) 
            throws UserServiceException {
        
        // Validation
        if (!isValidEmail(email)) {
            throw new UserServiceException("Invalid email format");
        }
        
        // Check if new email is not taken by another user
        try {
            User existingUserWithEmail = userDAO.findByEmail(email);
            if (existingUserWithEmail != null && existingUserWithEmail.getId() != userId) {
                throw new UserServiceException("Email already in use");
            }
        } catch (Exception e) {
            throw new UserServiceException("Update failed", e);
        }
        
        // Get current user
        User user = getUserById(userId);
        user.setEmail(email);
        user.setUsername(username);
        
        // Update
        try {
            userDAO.update(user);
            logger.info("User profile updated: {}", userId);
        } catch (Exception e) {
            logger.error("Error updating user profile", e);
            throw new UserServiceException("Update failed", e);
        }
    }
    
    @Override
    public void changePassword(int userId, String oldPassword, String newPassword) 
            throws UserServiceException {
        
        if (newPassword == null || newPassword.length() < 6) {
            throw new UserServiceException("Password must be at least 6 characters");
        }
        
        // Get user
        User user = getUserById(userId);
        
        // Verify old password
        if (!BCrypt.checkpw(oldPassword, user.getPasswordHash())) {
            logger.warn("Wrong old password attempt for user: {}", userId);
            throw new UserServiceException("Current password is incorrect");
        }
        
        // Hash new password
        String newHash = BCrypt.hashpw(newPassword, BCrypt.gensalt());
        user.setPasswordHash(newHash);
        
        // Update
        try {
            userDAO.update(user);
            logger.info("Password changed for user: {}", userId);
        } catch (Exception e) {
            logger.error("Error changing password", e);
            throw new UserServiceException("Password change failed", e);
        }
    }
    
    @Override
    public List<User> getAllUsers() throws UserServiceException {
        try {
            return userDAO.findAll();
        } catch (Exception e) {
            logger.error("Error fetching all users", e);
            throw new UserServiceException("Failed to get users", e);
        }
    }
    
    @Override
    public void deleteUser(int userId) throws UserServiceException {
        try {
            userDAO.delete(userId);
            logger.info("User deleted: {}", userId);
        } catch (Exception e) {
            logger.error("Error deleting user", e);
            throw new UserServiceException("Delete failed", e);
        }
    }
    
    @Override
    public boolean emailExists(String email) throws UserServiceException {
        try {
            return userDAO.isEmailTaken(email);
        } catch (Exception e) {
            logger.error("Error checking email", e);
            throw new UserServiceException("Check failed", e);
        }
    }
    
    @Override
    public boolean usernameExists(String username) throws UserServiceException {
        try {
            return userDAO.isUsernameTaken(username);
        } catch (Exception e) {
            logger.error("Error checking username", e);
            throw new UserServiceException("Check failed", e);
        }
    }
    
    // Helper: Validate email format
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email != null && Pattern.matches(emailRegex, email);
    }
}
```

**UserServiceException.java**:
```java
package com.example.services;

public class UserServiceException extends Exception {
    
    public UserServiceException(String message) {
        super(message);
    }
    
    public UserServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

### 6.5.4 Refactored Servlet Using Service

**Before (no service layer)**:
```java
@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        // Mix of validation, business logic, and data access
        String username = request.getParameter("username");
        
        // Validation mixed in servlet
        if (username.length() < 4) {
            request.setAttribute("error", "Username too short");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }
        
        // Database access mixed in servlet
        UserDAO dao = new UserDAOImpl();
        if (dao.isUsernameTaken(username)) {
            request.setAttribute("error", "Username taken");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }
        
        // Password hashing mixed in servlet
        String hashedPassword = BCrypt.hashpw(...);
        // ...
    }
}
```

**After (with service layer)**:
```java
@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    
    private UserService userService;
    
    @Override
    public void init() throws ServletException {
        // Dependency injection at servlet startup
        userService = new UserServiceImpl();
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        
        try {
            // Just call service - all business logic handled there
            User newUser = userService.registerUser(username, email, password);
            
            // Clear response or redirect
            response.sendRedirect("login.jsp?registered=true");
            
        } catch (UserServiceException e) {
            // Service handles all validation and sends meaningful errors
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/register.jsp").forward(request, response);
        }
    }
}
```

---

# Session 41: Logging Configuration

## 📖 Lý Thuyết: Logging Best Practices

### 6.6.1 Log Levels

```
TRACE  - Most detailed, trace code flow
DEBUG  - Development debugging info
INFO   - Important milestones (user login, app started)
WARN   - Potentially harmful situation (deprecated usage)
ERROR  - Error that prevents operation
FATAL  - Very serious, app may not recover
```

**Log Level Hierarchy**:
```
FATAL > ERROR > WARN > INFO > DEBUG > TRACE

If level is set to WARN:
  ✓ FATAL messages logged
  ✓ ERROR messages logged
  ✓ WARN messages logged
  ✗ INFO messages NOT logged
  ✗ DEBUG messages NOT logged
  ✗ TRACE messages NOT logged
```

### 6.6.2 SLF4J (Simple Logging Facade)

**Why SLF4J over Log4j directly?**:
- Abstraction layer (swappable implementation)
- Simple API
- Performance optimized

---

## 🔧 Cách Sử Dụng: SLF4J + Logback Configuration

### 6.6.3 pom.xml Dependencies

```xml
<!-- SLF4J API -->
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-api</artifactId>
    <version>2.0.5</version>
</dependency>

<!-- Logback implementation -->
<dependency>
    <groupId>ch.qos.logback</groupId>
    <artifactId>logback-classic</artifactId>
    <version>1.4.5</version>
</dependency>

<!-- For Log4j 2 (alternative) -->
<!--
<dependency>
    <groupId>org.apache.logging.log4j</groupId>
    <artifactId>log4j-core</artifactId>
    <version>2.20.0</version>
</dependency>
-->
```

### 6.6.4 logback.xml Configuration

**src/main/resources/logback.xml**:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    
    <!-- Property definitions -->
    <property name="LOG_DIR" value="logs" />
    <property name="LOG_PATTERN" value="%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n" />
    
    <!-- Console Appender (stdout) -->
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>${LOG_PATTERN}</pattern>
            <charset>UTF-8</charset>
        </encoder>
    </appender>
    
    <!-- File Appender (all logs) -->
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>${LOG_DIR}/application.log</file>
        
        <!-- Rolling policy - create new file daily -->
        <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
            <fileNamePattern>${LOG_DIR}/application-%d{yyyy-MM-dd}.%i.log</fileNamePattern>
            <maxFileSize>10MB</maxFileSize>
            <maxHistory>30</maxHistory>
            <totalSizeCap>1GB</totalSizeCap>
        </rollingPolicy>
        
        <encoder>
            <pattern>${LOG_PATTERN}</pattern>
            <charset>UTF-8</charset>
        </encoder>
    </appender>
    
    <!-- Error File Appender (errors only) -->
    <appender name="ERROR_FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>${LOG_DIR}/error.log</file>
        
        <!-- Only log ERROR and above -->
        <filter class="ch.qos.logback.classic.filter.ThresholdFilter">
            <level>ERROR</level>
        </filter>
        
        <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
            <fileNamePattern>${LOG_DIR}/error-%d{yyyy-MM-dd}.%i.log</fileNamePattern>
            <maxFileSize>10MB</maxFileSize>
            <maxHistory>30</maxHistory>
        </rollingPolicy>
        
        <encoder>
            <pattern>${LOG_PATTERN}</pattern>
            <charset>UTF-8</charset>
        </encoder>
    </appender>
    
    <!-- Async Appender (for high-throughput logging) -->
    <appender name="ASYNC_FILE" class="ch.qos.logback.classic.AsyncAppender">
        <queueSize>512</queueSize>
        <discardingThreshold>0</discardingThreshold>
        <appender-ref ref="FILE" />
    </appender>
    
    <!-- Root logger configuration -->
    <root level="INFO">
        <appender-ref ref="CONSOLE" />
        <appender-ref ref="ASYNC_FILE" />
        <appender-ref ref="ERROR_FILE" />
    </root>
    
    <!-- Package-specific loggers -->
    <logger name="com.example" level="DEBUG" />
    <logger name="com.example.dao" level="DEBUG" />
    <logger name="com.example.services" level="INFO" />
    
    <!-- Third-party loggers (suppress noisy libs) -->
    <logger name="org.apache.catalina" level="WARN" />
    <logger name="org.hibernate" level="WARN" />
    <logger name="org.springframework" level="INFO" />
    
    <!-- Development profile (more verbose) -->
    <springProfile name="dev">
        <root level="DEBUG">
            <appender-ref ref="CONSOLE" />
            <appender-ref ref="FILE" />
        </root>
    </springProfile>
    
    <!-- Production profile (less verbose) -->
    <springProfile name="prod">
        <root level="WARN">
            <appender-ref ref="FILE" />
            <appender-ref ref="ERROR_FILE" />
        </root>
    </springProfile>
    
</configuration>
```

### 6.6.5 Using Logger in Code

```java
package com.example.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserService {
    
    // Get logger (usually done at class level)
    private static final Logger logger = 
        LoggerFactory.getLogger(UserService.class);
    
    public void registerUser(String email, String password) {
        
        // INFO level - important business events
        logger.info("User registration initiated: {}", email);
        
        // DEBUG level - development troubleshooting
        logger.debug("Email format validated for: {}", email);
        logger.debug("Password requirements verified");
        
        try {
            // ... business logic ...
            
            // TRACE level - detailed code flow (normally off)
            logger.trace("About to save user to database");
            
            saveUserToDatabase(email, password);
            
            logger.info("User registered successfully: {}", email);
            
        } catch (DatabaseException e) {
            // ERROR level - errors that prevent operation
            logger.error("Failed to save user to database: {}", email, e);
            throw new ServiceException("Registration failed", e);
            
        } catch (ValidationException e) {
            // WARN level - potentially harmful but not critical
            logger.warn("User registration with invalid data: {}", e.getMessage());
            throw new ServiceException(e.getMessage());
        }
    }
}
```

---

## 🏆 Ví Dụ: Sai vs Đúng

### ❌ Sai: String concatenation (performance issue)

```java
for (int i = 0; i < 100000; i++) {
    // WRONG: Creates string even if DEBUG is off!
    logger.debug("Processing record: " + i + " name: " + user.getName());
    // String concatenation happens BEFORE logger.debug() is called
}
```

### ✅ Đúng: Use parameter substitution

```java
for (int i = 0; i < 100000; i++) {
    // RIGHT: If DEBUG is off, {} placeholders are NOT evaluated
    logger.debug("Processing record: {} name: {}", i, user.getName());
    // Much faster - parameters only evaluated if level is enabled
}
```

---

# Session 42: Mini Project - File Upload System with Multi-Language Support

## 📖 Project Overview

**Objective**: Build a complete file-sharing system with:
- File upload/download with validation
- Multi-language support (English, Vietnamese, French)
- User authentication (from Tuần 5)
- Database integration (from Tuần 4)
- Service layer (from Session 40)
- Proper logging (from Session 41)
- Filter chain from Sessions 36-37

**Technologies**:
- Apache Commons FileUpload
- ResourceBundle for i18n
- SLF4J + Logback logging
- Filter chain for request processing
- Service layer for business logic

---

## 🏗️ Project Structure

```
file-sharing-app/
├── src/main/java/com/example/
│   ├── models/
│   │   ├── User.java (from Tuần 5)
│   │   └── FileRecord.java (new)
│   ├── dao/
│   │   ├── UserDAO.java (from Tuần 5)
│   │   ├── UserDAOImpl.java (from Tuần 5)
│   │   ├── FileDAO.java (new)
│   │   └── FileDAOImpl.java (new)
│   ├── services/
│   │   ├── UserService.java (Session 40)
│   │   ├── UserServiceImpl.java (Session 40)
│   │   ├── FileService.java (new)
│   │   ├── FileServiceImpl.java (new)
│   │   └── FileServiceException.java
│   ├── filters/
│   │   ├── LoggingFilter.java (Session 36)
│   │   ├── AuthFilter.java (from Tuần 5)
│   │   ├── EncodingFilter.java (Session 36)
│   │   └── RoleFilter.java (from Tuần 5)
│   ├── listeners/
│   │   └── ApplicationContextListener.java
│   ├── servlets/
│   │   ├── FileUploadServlet.java (Session 38)
│   │   ├── FileDownloadServlet.java (Session 38)
│   │   ├── LoginServlet.java (from Tuần 5)
│   │   ├── LogoutServlet.java (from Tuần 5)
│   │   └── FileListServlet.java (new)
│   ├── i18n/
│   │   └── I18nUtil.java (Session 39)
│   └── utils/
│       └── DatabaseConnection.java
│
├── src/main/resources/
│   ├── messages_en.properties (i18n)
│   ├── messages_vi.properties (i18n)
│   ├── messages_fr.properties (i18n)
│   └── logback.xml (logging)
│
├── src/main/webapp/
│   ├── WEB-INF/
│   │   └── web.xml
│   └── views/
│       ├── login.jsp
│       ├── home.jsp
│       ├── upload.jsp
│       ├── files.jsp
│       └── error.jsp
│
├── pom.xml (Maven)
└── README.md
```

---

## 🗄️ Database Schema

```sql
-- Users table (from Tuần 5)
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('user', 'admin') DEFAULT 'user',
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Files table (NEW for Session 42)
CREATE TABLE uploaded_files (
    id INT AUTO_INCREMENT PRIMARY KEY,
    file_name VARCHAR(255) NOT NULL,
    original_name VARCHAR(255) NOT NULL,
    file_size BIGINT NOT NULL,
    file_type VARCHAR(50),
    description TEXT,
    user_id INT NOT NULL,
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Audit logs table (for tracking activity)
CREATE TABLE audit_logs (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    action VARCHAR(100) NOT NULL,
    description TEXT,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);
```

---

## 🔧 Implementation - Key Classes

### FileRecord.java (Model)

```java
package com.example.models;

import java.time.LocalDateTime;

public class FileRecord {
    private int id;
    private String fileName;       // Unique name on disk
    private String originalName;   // Name user uploaded
    private long fileSize;
    private String fileType;       // MIME type
    private String description;
    private int userId;
    private LocalDateTime uploadedAt;
    private boolean deleted;
    
    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    
    public String getOriginalName() { return originalName; }
    public void setOriginalName(String originalName) { this.originalName = originalName; }
    
    public long getFileSize() { return fileSize; }
    public void setFileSize(long fileSize) { this.fileSize = fileSize; }
    
    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }
    
    public boolean isDeleted() { return deleted; }
    public void setDeleted(boolean deleted) { this.deleted = deleted; }
}
```

### FileService.java

```java
package com.example.services;

import com.example.models.FileRecord;
import java.io.InputStream;
import java.util.List;

public interface FileService {
    
    // Upload file
    FileRecord uploadFile(String originalName, InputStream fileStream, 
                         long fileSize, String description, int userId) 
        throws FileServiceException;
    
    // Get user's files
    List<FileRecord> getUserFiles(int userId) throws FileServiceException;
    
    // Get file by ID
    FileRecord getFileById(int fileId) throws FileServiceException;
    
    // Delete file
    void deleteFile(int fileId, int userId) throws FileServiceException;
    
    // Get file for download
    FileRecord getFileForDownload(int fileId, int userId) throws FileServiceException;
    
    // Search files by name/description
    List<FileRecord> searchFiles(int userId, String query) throws FileServiceException;
}
```

### FileServiceImpl.java

```java
package com.example.services;

import com.example.models.FileRecord;
import com.example.dao.FileDAO;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class FileServiceImpl implements FileService {
    
    private static final Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);
    
    private FileDAO fileDAO;
    private static final String UPLOAD_DIR = "uploads";
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
        "jpg", "jpeg", "png", "gif", "pdf", "doc", "docx", "xls", "xlsx", "txt"
    );
    private static final long MAX_FILE_SIZE = 100 * 1024 * 1024;  // 100 MB
    
    public FileServiceImpl(FileDAO fileDAO) {
        this.fileDAO = fileDAO;
    }
    
    @Override
    public FileRecord uploadFile(String originalName, InputStream fileStream, 
                                long fileSize, String description, int userId) 
            throws FileServiceException {
        
        logger.info("Attempting to upload file: {} for user: {}", originalName, userId);
        
        // Validation
        String extension = FilenameUtils.getExtension(originalName).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            logger.warn("Invalid file extension: {}", extension);
            throw new FileServiceException("File type not allowed: " + extension);
        }
        
        if (fileSize > MAX_FILE_SIZE) {
            logger.warn("File size exceeds limit: {}", fileSize);
            throw new FileServiceException("File too large. Maximum: 100 MB");
        }
        
        if (fileSize == 0) {
            throw new FileServiceException("File is empty");
        }
        
        // Sanitize and create unique filename
        String sanitized = sanitizeFilename(originalName);
        String uniqueFileName = generateUniqueFileName(sanitized);
        
        // Create upload directory if needed
        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            Files.createDirectories(uploadPath);
        } catch (IOException e) {
            logger.error("Failed to create upload directory", e);
            throw new FileServiceException("Upload directory error", e);
        }
        
        // Save file to disk
        Path filePath = Paths.get(UPLOAD_DIR, uniqueFileName);
        try {
            Files.copy(fileStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            logger.error("Failed to save file to disk: {}", filePath, e);
            throw new FileServiceException("Failed to save file", e);
        }
        
        // Create FileRecord and save to database
        FileRecord record = new FileRecord();
        record.setFileName(uniqueFileName);
        record.setOriginalName(originalName);
        record.setFileSize(fileSize);
        record.setFileType("application/" + extension);
        record.setDescription(description);
        record.setUserId(userId);
        record.setUploadedAt(LocalDateTime.now());
        record.setDeleted(false);
        
        try {
            FileRecord savedRecord = fileDAO.save(record);
            logger.info("File uploaded successfully: {} (ID: {})", uniqueFileName, savedRecord.getId());
            return savedRecord;
        } catch (Exception e) {
            logger.error("Failed to save file record to database", e);
            // Try to clean up the uploaded file
            try {
                Files.delete(filePath);
            } catch (IOException deleteErr) {
                logger.error("Failed to delete orphaned file", deleteErr);
            }
            throw new FileServiceException("Database save failed", e);
        }
    }
    
    @Override
    public List<FileRecord> getUserFiles(int userId) throws FileServiceException {
        try {
            logger.debug("Fetching files for user: {}", userId);
            List<FileRecord> files = fileDAO.findByUserId(userId);
            // Filter out deleted files
            return files.stream()
                .filter(f -> !f.isDeleted())
                .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error fetching user files", e);
            throw new FileServiceException("Failed to get files", e);
        }
    }
    
    @Override
    public FileRecord getFileById(int fileId) throws FileServiceException {
        try {
            FileRecord file = fileDAO.findById(fileId);
            if (file == null || file.isDeleted()) {
                throw new FileServiceException("File not found");
            }
            return file;
        } catch (Exception e) {
            logger.error("Error fetching file: {}", fileId, e);
            throw new FileServiceException("Failed to get file", e);
        }
    }
    
    @Override
    public void deleteFile(int fileId, int userId) throws FileServiceException {
        FileRecord file = getFileById(fileId);
        
        // Authorization check
        if (file.getUserId() != userId) {
            logger.warn("User {} attempted to delete file {} owned by {}", 
                       userId, fileId, file.getUserId());
            throw new FileServiceException("Unauthorized to delete this file");
        }
        
        try {
            // Mark as deleted in database
            file.setDeleted(true);
            fileDAO.update(file);
            
            // Optional: Delete from disk
            Path filePath = Paths.get(UPLOAD_DIR, file.getFileName());
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }
            
            logger.info("File deleted: {}", fileId);
        } catch (Exception e) {
            logger.error("Error deleting file: {}", fileId, e);
            throw new FileServiceException("Delete failed", e);
        }
    }
    
    @Override
    public FileRecord getFileForDownload(int fileId, int userId) throws FileServiceException {
        FileRecord file = getFileById(fileId);
        
        // Authorization check
        if (file.getUserId() != userId) {
            logger.warn("User {} attempted to download file {} owned by {}", 
                       userId, fileId, file.getUserId());
            throw new FileServiceException("Unauthorized to download this file");
        }
        
        // Check file exists on disk
        Path filePath = Paths.get(UPLOAD_DIR, file.getFileName());
        if (!Files.exists(filePath)) {
            logger.error("File missing from disk: {}", file.getFileName());
            throw new FileServiceException("File not found on disk");
        }
        
        return file;
    }
    
    @Override
    public List<FileRecord> searchFiles(int userId, String query) throws FileServiceException {
        try {
            return fileDAO.findByUserId(userId)
                .stream()
                .filter(f -> !f.isDeleted())
                .filter(f -> f.getOriginalName().toLowerCase().contains(query.toLowerCase()) ||
                           f.getDescription().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error searching files", e);
            throw new FileServiceException("Search failed", e);
        }
    }
    
    private String sanitizeFilename(String fileName) {
        return fileName.replaceAll("[\\\\/]", "")
                      .replaceAll("[^a-zA-Z0-9._-]", "_");
    }
    
    private String generateUniqueFileName(String originalName) {
        String name = FilenameUtils.getBaseName(originalName);
        String ext = FilenameUtils.getExtension(originalName);
        return name + "_" + System.currentTimeMillis() + "." + ext;
    }
}
```

### messages_en.properties

```properties
# Application
app.title=File Sharing System
app.description=Share files securely with others
app.language=English

# Navigation
nav.home=Home
nav.upload=Upload File
nav.myfiles=My Files
nav.profile=Profile
nav.logout=Logout
nav.login=Login

# Upload Form
form.file=Select File
form.description=Description
form.submit=Upload
form.choose.file=Choose File
form.max.size=Maximum size: 100 MB

# File List
files.name=File Name
files.size=Size
files.uploaded=Uploaded
files.description=Description
files.download=Download
files.delete=Delete
files.no.files=No files uploaded yet

# Messages
msg.upload.success=File uploaded successfully
msg.upload.error=Upload failed
msg.delete.success=File deleted
msg.delete.confirm=Are you sure you want to delete this file?

# Errors
error.auth=Invalid username or password
error.unauthorized=You are not authorized
error.file.not.found=File not found
error.invalid.file=Invalid file type
```

### messages_vi.properties

```properties
# Application
app.title=Hệ thống Chia sẻ File
app.description=Chia sẻ file một cách an toàn
app.language=Tiếng Việt

# Navigation
nav.home=Trang chủ
nav.upload=Tải lên file
nav.myfiles=Tệp của tôi
nav.profile=Trang cá nhân
nav.logout=Đăng xuất
nav.login=Đăng nhập

# Upload Form
form.file=Chọn tệp
form.description=Mô tả
form.submit=Tải lên
form.choose.file=Chọn tệp
form.max.size=Kích thước tối đa: 100 MB

# File List
files.name=Tên tệp
files.size=Kích thước
files.uploaded=Tải lên vào
files.description=Mô tả
files.download=Tải xuống
files.delete=Xóa
files.no.files=Chưa tải lên tệp nào

# Messages
msg.upload.success=Tải lên tệp thành công
msg.upload.error=Tải lên thất bại
msg.delete.success=Xóa tệp thành công
msg.delete.confirm=Bạn có chắc chắn muốn xóa tệp này không?

# Errors
error.auth=Tên người dùng hoặc mật khẩu không hợp lệ
error.unauthorized=Bạn không được phép truy cập
error.file.not.found=Tệp không tìm thấy
error.invalid.file=Loại tệp không hợp lệ
```

---

### upload.jsp

```jsp
<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<fmt:setBundle basename="messages" var="msg" />

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title><fmt:message key="app.title" bundle="${msg}" /></title>
    <style>
        .upload-container {
            max-width: 600px;
            margin: 50px auto;
            padding: 20px;
            border: 1px solid #ddd;
            border-radius: 5px;
        }
        .form-group {
            margin-bottom: 15px;
        }
        label {
            font-weight: bold;
            display: block;
            margin-bottom: 5px;
        }
        input[type="file"], textarea {
            width: 100%;
            padding: 8px;
            border: 1px solid #ccc;
            box-sizing: border-box;
        }
        button {
            background: #4CAF50;
            color: white;
            padding: 10px 20px;
            border: none;
            cursor: pointer;
        }
        button:hover {
            background: #45a049;
        }
    </style>
</head>
<body>
    <div class="upload-container">
        <h2><fmt:message key="nav.upload" bundle="${msg}" /></h2>
        
        <% if (request.getAttribute("error") != null) { %>
            <div style="color: red;">Error: <%= request.getAttribute("error") %></div>
        <% } %>
        
        <form method="POST" enctype="multipart/form-data">
            <div class="form-group">
                <label><fmt:message key="form.file" bundle="${msg}" /></label>
                <input type="file" name="file" required />
                <small><fmt:message key="form.max.size" bundle="${msg}" /></small>
            </div>
            
            <div class="form-group">
                <label><fmt:message key="form.description" bundle="${msg}" /></label>
                <textarea name="description" rows="3"></textarea>
            </div>
            
            <button type="submit"><fmt:message key="form.submit" bundle="${msg}" /></button>
        </form>
    </div>
</body>
</html>
```

### files.jsp (List user files)

```jsp
<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<fmt:setBundle basename="messages" var="msg" />

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title><fmt:message key="app.title" bundle="${msg}" /></title>
    <style>
        .files-container {
            max-width: 1000px;
            margin: 30px auto;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }
        th, td {
            padding: 10px;
            border: 1px solid #ddd;
            text-align: left;
        }
        th {
            background: #f2f2f2;
            font-weight: bold;
        }
        .action-buttons {
            display: flex;
            gap: 10px;
        }
        .btn-download, .btn-delete {
            padding: 5px 10px;
            border: none;
            cursor: pointer;
            border-radius: 3px;
        }
        .btn-download {
            background: #2196F3;
            color: white;
        }
        .btn-delete {
            background: #f44336;
            color: white;
        }
    </style>
</head>
<body>
    <div class="files-container">
        <h2><fmt:message key="nav.myfiles" bundle="${msg}" /></h2>
        
        <c:if test="${empty files}">
            <p><fmt:message key="files.no.files" bundle="${msg}" /></p>
        </c:if>
        
        <c:if test="${not empty files}">
            <table>
                <thead>
                    <tr>
                        <th><fmt:message key="files.name" bundle="${msg}" /></th>
                        <th><fmt:message key="files.size" bundle="${msg}" /></th>
                        <th><fmt:message key="files.uploaded" bundle="${msg}" /></th>
                        <th><fmt:message key="files.description" bundle="${msg}" /></th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="file" items="${files}">
                        <tr>
                            <td>${file.originalName}</td>
                            <td>${file.fileSize / (1024*1024)} MB</td>
                            <td><fmt:formatDate value="${file.uploadedAt}" pattern="dd/MM/yyyy HH:mm" /></td>
                            <td>${file.description}</td>
                            <td>
                                <div class="action-buttons">
                                    <form method="POST" action="/download" style="display: inline;">
                                        <input type="hidden" name="fileId" value="${file.id}" />
                                        <button type="submit" class="btn-download"><fmt:message key="files.download" bundle="${msg}" /></button>
                                    </form>
                                    <form method="POST" action="/delete" style="display: inline;" onsubmit="return confirm('<fmt:message key="files.delete.confirm" bundle="${msg}" />');">
                                        <input type="hidden" name="fileId" value="${file.id}" />
                                        <button type="submit" class="btn-delete"><fmt:message key="files.delete" bundle="${msg}" /></button>
                                    </form>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:if>
    </div>
</body>
</html>
```

---

## 🚀 Deployment Instructions

### Database Setup

```sql
-- Create database
CREATE DATABASE file_sharing_app;
USE file_sharing_app;

-- Create tables (from schema above)
-- ... run SQL schema ...

-- Create test user
INSERT INTO users (username, email, password_hash, role)
VALUES ('testuser', 'test@example.com', '$2a$10$...', 'user');
```

### Tomcat Configuration (server.xml)

```xml
<Context docBase="file-sharing-app" path="" reloadable="true">
    <Resource 
        name="jdbc/FileShareDB" 
        auth="Container" 
        type="javax.sql.DataSource"
        driverClassName="com.mysql.cj.jdbc.Driver"
        url="jdbc:mysql://localhost:3306/file_sharing_app"
        username="root"
        password="password"
        maxActive="20"
        maxIdle="10"
    />
</Context>
```

### Build & Run

```bash
# Build with Maven
mvn clean package

# Deploy WAR to Tomcat
cp target/file-sharing-app.war $TOMCAT_HOME/webapps/

# Restart Tomcat
$TOMCAT_HOME/bin/shutdown.sh
$TOMCAT_HOME/bin/startup.sh

# Access application
http://localhost:8080/file-sharing-app
```

---

## ✅ Testing Scenarios

### Scenario 1: Basic Upload

```
1. User clicks "Upload File"
2. Selects video.mp4 (10 MB)
3. Adds description "Tutorial video"
4. Clicks Upload
5. ✓ File saved to disk with unique name
6. ✓ Record saved to database
7. ✓ Success message shown
```

### Scenario 2: Multi-language

```
1. User clicks language selector "Tiếng Việt"
2. Session updated with locale
3. ✓ All UI labels change to Vietnamese
4. ✓ Form labels, buttons, error messages all in Vietnamese
```

### Scenario 3: Authorization

```
1. User A uploads document.pdf (creates file_id=1)
2. User B tries to download by guessing URL: /download?file=1
3. ✓ FileService checks userId ownership
4. ✓ 403 Forbidden error returned
```

### Scenario 4: File Size Validation

```
1. User tries to upload 500MB_file.iso
2. FileService validates: fileSize > MAX (100MB)
3. ✓ FileServiceException thrown
4. ✓ Error message displayed to user
```

---

## 📊 Project Statistics

| Metric | Count |
|--------|-------|
| Java Classes | 18 |
| JSP Views | 4 |
| Filters | 4 |
| Listeners | 1 |
| Service Interfaces | 2 |
| DAO Interfaces | 2 |
| Database Tables | 3 |
| i18n Property Files | 3 |
| Lines of Code | ~2000 |

---

## 🎓 Learning Outcomes

After completing Tuần 6, students understand:

✅ **Filter Architecture**: 
- Execution order and chain processing
- Pre/post request handling
- Filter configuration in web.xml

✅ **Listeners**:
- Application lifecycle events (startup/shutdown)
- Session lifecycle tracking
- Request lifecycle management
- Thread-safe counters with AtomicInteger

✅ **File Handling**:
- Multipart form data parsing
- Apache Commons FileUpload
- Security validation (type, size, path traversal)
- File sanitization

✅ **Internationalization (i18n)**:
- ResourceBundle for translations
- Locale resolution priorities
- JSTL fmt tag library
- Multi-language application design

✅ **MVC Refactoring**:
- Service layer separation
- Business logic isolation
- Testable architecture
- Dependency injection

✅ **Logging Best Practices**:
- SLF4J + Logback configuration
- Log levels and appenders
- Performance optimization (lazy evaluation)
- Production logs management

✅ **Mini Project Integration**:
- All previous weeks consolidated
- Production-ready file sharing application
- Security, logging, i18n, filtering all used together

---

# 🏁 Tuần 6 Summary

**Tuần 6** builds on the Week 5 foundation by:

1. **Deepening Filters** (Session 36):
   - Execution order & chain architecture
   - Logging, encoding, multi-filter flow
   - Real production patterns

2. **Introducing Listeners** (Session 37):
   - Application lifecycle management
   - Session/request tracking
   - Thread-safe counters

3. **Mastering File Upload** (Session 38):
   - Apache Commons FileUpload
   - Security validation
   - Download handling

4. **Enabling i18n** (Session 39):
   - Multi-language support
   - ResourceBundle management
   - Locale resolution

5. **Refactoring MVC** (Session 40):
   - Service layer introduction
   - Business logic separation
   - Testable architecture

6. **Configuring Logging** (Session 41):
   - SLF4J + Logback setup
   - Log levels and appenders
   - Performance optimization

7. **Complete Mini Project** (Session 42):
   - File sharing system with authentication
   - Full i18n support
   - Production-quality code with logging

**Next**: Tuần 7 will focus on **Testing & Deployment** - ensuring code quality with JUnit/Mockito and deploying to production environments with Docker & Nginx.

"}