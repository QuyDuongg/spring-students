# Student App - Spring Boot CRUD API

Ứng dụng Spring Boot đơn giản với API CRUD cho quản lý Students, kết nối với Azure Database for PostgreSQL flexible server.

## Yêu cầu

-   Java 17 hoặc cao hơn
-   Maven 3.6+
-   Azure Database for PostgreSQL flexible server

## Cấu hình

1. Mở file `src/main/resources/application.properties`
2. Cập nhật thông tin kết nối Azure PostgreSQL:
    - `spring.datasource.url`: URL của database (ví dụ: `jdbc:postgresql://myserver.postgres.database.azure.com:5432/mydb?sslmode=require`)
    - `spring.datasource.username`: Username (format: `username@servername`)
    - `spring.datasource.password`: Password của database

## Chạy ứng dụng

```bash
# Build project
mvn clean install

# Chạy ứng dụng
mvn spring-boot:run
```

Ứng dụng sẽ chạy tại: `http://localhost:8080`

## API Endpoints

### 1. GET /students

Lấy danh sách tất cả students

**Request:**

```bash
GET http://localhost:8080/students
```

**Response:**

```json
[
    {
        "id": 1,
        "name": "Nguyễn Văn A",
        "email": "nguyenvana@example.com",
        "age": 20
    },
    {
        "id": 2,
        "name": "Trần Thị B",
        "email": "tranthib@example.com",
        "age": 21
    }
]
```

### 2. GET /students/{id}

Lấy thông tin student theo ID

**Request:**

```bash
GET http://localhost:8080/students/1
```

**Response:**

```json
{
    "id": 1,
    "name": "Nguyễn Văn A",
    "email": "nguyenvana@example.com",
    "age": 20
}
```

### 3. POST /students

Tạo student mới

**Request:**

```bash
POST http://localhost:8080/students
Content-Type: application/json

{
  "name": "Lê Văn C",
  "email": "levanc@example.com",
  "age": 22
}
```

**Response:**

```json
{
    "id": 3,
    "name": "Lê Văn C",
    "email": "levanc@example.com",
    "age": 22
}
```

## Cấu trúc Project

```
student-app/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/studentapp/
│   │   │       ├── StudentAppApplication.java
│   │   │       ├── controller/
│   │   │       │   └── StudentController.java
│   │   │       ├── model/
│   │   │       │   └── Student.java
│   │   │       └── repository/
│   │   │           └── StudentRepository.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
├── pom.xml
└── README.md
```

## Lưu ý

-   Database table sẽ được tự động tạo khi ứng dụng chạy lần đầu (do `spring.jpa.hibernate.ddl-auto=update`)
-   Đảm bảo firewall của Azure PostgreSQL cho phép kết nối từ IP của bạn
-   Sử dụng SSL connection (`sslmode=require`) để bảo mật kết nối
