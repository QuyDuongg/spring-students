# Hướng Dẫn Chạy Ứng Dụng

## 1. Chạy Ứng Dụng

```bash
mvn spring-boot:run
```

Hoặc:

```bash
mvn clean install
java -jar target/student-app-1.0.0.jar
```

Ứng dụng sẽ chạy tại: **http://localhost:8080**

## 2. Kiểm Tra Kết Nối Database

### Test 1: Kiểm tra trang chủ

```
GET http://localhost:8080/
```

Hoặc mở trình duyệt: http://localhost:8080/

### Test 2: Kiểm tra kết nối database

```
GET http://localhost:8080/test-db
```

Endpoint này sẽ kiểm tra xem database có kết nối được không và cho biết số lượng students hiện có.

### Test 3: Health check

```
GET http://localhost:8080/health
```

## 3. Test API CRUD

### Lấy danh sách tất cả students

```bash
GET http://localhost:8080/students
```

### Tạo student mới

```bash
POST http://localhost:8080/students
Content-Type: application/json

{
  "name": "Nguyễn Văn A",
  "email": "nguyenvana@example.com",
  "age": 20
}
```

### Lấy student theo ID

```bash
GET http://localhost:8080/students/1
```

## 4. Lưu Ý

-   **Firewall Azure**: Đảm bảo Azure PostgreSQL firewall cho phép kết nối từ IP của bạn
-   **SSL**: Database đang sử dụng SSL (`sslmode=require`)
-   **Auto-create tables**: Bảng `students` sẽ được tự động tạo khi ứng dụng chạy lần đầu

## 5. Kiểm Tra Logs

Khi ứng dụng chạy, bạn sẽ thấy:

-   SQL queries được log ra console (do `spring.jpa.show-sql=true`)
-   Thông báo kết nối database thành công hoặc lỗi

## 6. Xử Lý Lỗi Thường Gặp

### Lỗi kết nối database

-   Kiểm tra lại thông tin trong `application.properties`
-   Kiểm tra firewall Azure
-   Kiểm tra username/password

### Lỗi port đã được sử dụng

-   Đổi port trong `application.properties`: `server.port=8081`
