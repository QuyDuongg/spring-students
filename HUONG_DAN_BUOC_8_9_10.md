# Hướng Dẫn Bước 8, 9, 10

## BƯỚC 8 - Redis Cache ✅

### Mục tiêu

Cache kết quả API `/students` trong 30 giây để tăng hiệu suất và giảm tải cho database.

### Đã thực hiện

1. **Thêm dependency** vào `pom.xml`:

    - `spring-boot-starter-cache` - Hỗ trợ caching
    - `spring-boot-starter-data-redis` - Đã có sẵn

2. **Tạo Redis Configuration** (`RedisConfig.java`):

    - Cấu hình TTL = 30 giây
    - Serialize dữ liệu JSON
    - Cache Manager với Redis

3. **Enable Caching** trong `StudentAppApplication.java`:

    - Thêm `@EnableCaching`

4. **Thêm Cache Annotations** trong `StudentController.java`:
    - `@Cacheable(value = "students", key = "'all'")` cho GET `/students`
    - `@CacheEvict(value = "students", key = "'all'")` cho POST `/students` (xóa cache khi có dữ liệu mới)

### Cấu hình Redis trong Azure

1. **Tạo Azure Cache for Redis**:

    - Vào Azure Portal > Create a resource > Azure Cache for Redis
    - Chọn Basic tier (đủ cho test)
    - Đặt tên: `your-redis-host`
    - Chọn Resource Group và Location

2. **Lấy thông tin kết nối**:

    - Vào Redis instance > Access keys
    - Copy Primary connection string hoặc:
        - Host: `your-redis-host.redis.cache.windows.net`
        - Port: `6380` (SSL)
        - Primary Key: Copy từ Access keys

3. **Cập nhật `application.properties`**:

    ```properties
    spring.data.redis.host=your-redis-host.redis.cache.windows.net
    spring.data.redis.port=6380
    spring.data.redis.password=your-primary-key
    spring.data.redis.ssl.enabled=true
    ```

4. **Cấu hình Firewall**:
    - Vào Redis instance > Networking
    - Thêm IP của Azure App Service hoặc chọn "Allow access from all networks" (chỉ cho test)

### Kiểm tra Cache hoạt động

1. **Build và deploy ứng dụng**:

    ```bash
    mvn clean install
    ```

2. **Test API**:

    ```bash
    # Lần đầu: Query database (chậm)
    GET http://your-app-url/students

    # Trong 30 giây tiếp theo: Lấy từ cache (nhanh)
    GET http://your-app-url/students

    # Sau 30 giây: Cache hết hạn, query lại database
    GET http://your-app-url/students
    ```

3. **Kiểm tra trong Azure Portal**:
    - Vào Redis instance > Metrics
    - Xem số lượng cache hits/misses

---

## BƯỚC 9 - Azure Synapse Analytics

### Mục tiêu

-   Import dữ liệu từ CosmosDB vào Synapse
-   Tạo query đơn giản
-   Xuất dataset cho Power BI

### Các bước thực hiện

#### 1. Tạo Azure Synapse Workspace

1. **Tạo Synapse Workspace**:

    - Vào Azure Portal > Create a resource > Azure Synapse Analytics
    - Điền thông tin:
        - Workspace name: `synapse-workspace-student`
        - Resource Group: Chọn resource group hiện tại
        - Location: Chọn cùng location với CosmosDB
        - SQL Admin username/password: Tạo mới
        - Storage account: Tạo mới hoặc chọn có sẵn

2. **Mở Synapse Studio**:
    - Vào Synapse Workspace > Open Synapse Studio
    - Hoặc truy cập: `https://web.azuresynapse.net`

#### 2. Kết nối CosmosDB với Synapse

1. **Tạo Linked Service**:

    - Vào Synapse Studio > Manage > Linked services
    - Click "New" > Chọn "Azure Cosmos DB (SQL API)"
    - Điền thông tin:
        - Name: `CosmosDB_Student`
        - Cosmos DB account name: Tên CosmosDB account của bạn
        - Database name: `studentdb`
        - Authentication: Account key
        - Account key: Copy từ CosmosDB Access keys
    - Click "Test connection" > "Create"

2. **Tạo Dataset**:
    - Vào Synapse Studio > Data > Datasets
    - Click "New" > "Azure Cosmos DB (SQL API)"
    - Chọn Linked service: `CosmosDB_Student`
    - Container: `students`
    - Name: `Dataset_CosmosDB_Students`
    - Click "OK"

#### 3. Copy dữ liệu từ CosmosDB vào Synapse SQL Pool

1. **Tạo SQL Pool** (nếu chưa có):

    - Vào Synapse Studio > Manage > SQL pools
    - Click "New" > Chọn "Dedicated SQL pool"
    - Name: `sqlpool1`
    - Performance level: DW100c (đủ cho test)
    - Click "Create"

2. **Tạo Pipeline để Copy Data**:
    - Vào Synapse Studio > Integrate > Pipelines
    - Click "New pipeline"
    - Name: `Copy_CosmosDB_to_SQLPool`
    - Kéo thả "Copy data" activity vào canvas
    - Cấu hình Source:
        - Source dataset: `Dataset_CosmosDB_Students`
    - Cấu hình Sink:
        - New dataset > Azure Synapse Analytics
        - Linked service: Tạo mới hoặc chọn SQL Pool
        - Table name: `students` (sẽ tự tạo)
    - Click "Publish" > "Trigger now" để chạy

#### 4. Tạo Query đơn giản trong SQL Pool

1. **Mở SQL Script**:

    - Vào Synapse Studio > Develop > SQL scripts
    - Click "New SQL script"

2. **Viết Query**:

    ```sql
    -- Kết nối với SQL Pool
    USE sqlpool1;
    GO

    -- Xem dữ liệu từ bảng students
    SELECT * FROM students;

    -- Query đơn giản: Đếm số lượng sinh viên
    SELECT COUNT(*) AS TotalStudents FROM students;

    -- Query theo độ tuổi
    SELECT
        age,
        COUNT(*) AS StudentCount
    FROM students
    GROUP BY age
    ORDER BY age;

    -- Query top 10 sinh viên
    SELECT TOP 10
        id,
        name,
        email,
        age
    FROM students
    ORDER BY id;
    ```

3. **Chạy Query**:
    - Click "Run" để thực thi
    - Xem kết quả ở tab Results

#### 5. Tạo View cho Power BI

1. **Tạo View**:

    ```sql
    USE sqlpool1;
    GO

    CREATE VIEW vw_StudentSummary AS
    SELECT
        COUNT(*) AS TotalStudents,
        AVG(CAST(age AS FLOAT)) AS AverageAge,
        MIN(age) AS MinAge,
        MAX(age) AS MaxAge
    FROM students;
    GO
    ```

2. **Tạo View chi tiết**:
    ```sql
    CREATE VIEW vw_StudentsForPowerBI AS
    SELECT
        id,
        name,
        email,
        age,
        CASE
            WHEN age < 20 THEN 'Under 20'
            WHEN age BETWEEN 20 AND 25 THEN '20-25'
            ELSE 'Over 25'
        END AS AgeGroup
    FROM students;
    GO
    ```

#### 6. Xuất Dataset cho Power BI

**Cách 1: Direct Query từ Synapse**

-   Power BI sẽ kết nối trực tiếp với Synapse SQL Pool
-   Dữ liệu real-time, không cần export

**Cách 2: Export sang Azure Data Lake Storage**

1. Tạo Linked Service cho Data Lake Storage Gen2
2. Tạo Pipeline export dữ liệu từ SQL Pool sang Parquet files
3. Power BI import từ Data Lake

**Cách 3: Export sang CSV/Excel**

1. Chạy query trong Synapse Studio
2. Click "Export results" > Chọn format
3. Download file và import vào Power BI

---

## BƯỚC 10 - Power BI

### Mục tiêu

-   Vẽ biểu đồ đơn giản: "Số lượng sinh viên"
-   Publish report lên Power BI Service

### Các bước thực hiện

#### 1. Cài đặt Power BI Desktop

1. **Download Power BI Desktop**:

    - Truy cập: https://powerbi.microsoft.com/desktop/
    - Download và cài đặt

2. **Đăng nhập**:
    - Mở Power BI Desktop
    - Sign in với Azure account

#### 2. Kết nối với Azure Synapse Analytics

1. **Get Data**:

    - Click "Get Data" > "Azure" > "Azure Synapse Analytics (SQL Data Warehouse)"
    - Hoặc "More..." > Tìm "Azure Synapse Analytics"

2. **Nhập thông tin kết nối**:

    - Server: `synapse-workspace-student.sql.azuresynapse.net`
    - Database: `sqlpool1`
    - Data connectivity mode: "Import" hoặc "DirectQuery"
    - Click "OK"

3. **Authentication**:

    - Chọn "Database"
    - Username: SQL admin username của Synapse
    - Password: SQL admin password
    - Click "Connect"

4. **Chọn dữ liệu**:
    - Chọn view: `vw_StudentsForPowerBI` hoặc table `students`
    - Click "Load"

#### 3. Tạo Biểu đồ "Số lượng sinh viên"

1. **Tạo Measure**:

    - Vào Data view (icon bảng ở bên trái)
    - Click "New measure"
    - Công thức:
        ```DAX
        Total Students = COUNTROWS('students')
        ```
    - Hoặc:
        ```DAX
        Total Students = COUNT('students'[id])
        ```

2. **Tạo Visual Card**:

    - Vào Report view
    - Chọn "Card" visual từ Visualizations pane
    - Kéo measure "Total Students" vào field "Fields"
    - Format: Thay đổi font, màu sắc, title

3. **Tạo Biểu đồ cột**:

    - Chọn "Column chart" visual
    - Axis: `age` hoặc `AgeGroup`
    - Values: `Total Students` measure
    - Title: "Số lượng sinh viên theo độ tuổi"

4. **Tạo Biểu đồ tròn**:

    - Chọn "Pie chart" visual
    - Legend: `AgeGroup`
    - Values: `Total Students` measure
    - Title: "Phân bố sinh viên theo nhóm tuổi"

5. **Tạo Table**:
    - Chọn "Table" visual
    - Kéo các fields: `name`, `email`, `age`
    - Thêm filter nếu cần

#### 4. Format và Design Report

1. **Thêm Title**:

    - Insert > Text box
    - Nhập: "Báo cáo Quản lý Sinh viên"
    - Format: Font size 24, Bold

2. **Sắp xếp Layout**:

    - Drag và drop các visuals
    - Resize cho đẹp mắt
    - Align và distribute evenly

3. **Format Visuals**:
    - Click vào visual > Format pane
    - Thay đổi màu sắc, font, background
    - Thêm data labels

#### 5. Publish Report lên Power BI Service

1. **Publish**:

    - Click "Publish" button trên ribbon
    - Chọn workspace: "My workspace" hoặc workspace khác
    - Click "Select"
    - Đợi quá trình publish hoàn tất
    - Click "Open 'your-report-name.pbix' in Power BI"

2. **Xem Report trên Web**:

    - Mở trình duyệt
    - Truy cập: https://app.powerbi.com
    - Vào "My workspace" > Tìm report vừa publish
    - Click để mở và xem

3. **Share Report**:
    - Click "Share" button trên report
    - Nhập email người cần share
    - Chọn quyền: "Viewer" hoặc "Contributor"
    - Click "Share"

#### 6. Tạo Dashboard (Optional)

1. **Pin Visuals to Dashboard**:

    - Hover vào visual > Click icon pin
    - Chọn "New dashboard" hoặc dashboard có sẵn
    - Đặt tên dashboard: "Student Management Dashboard"
    - Click "Pin"

2. **Customize Dashboard**:
    - Vào Dashboard
    - Resize và arrange tiles
    - Thêm text box, images nếu cần

### Lưu ý

1. **Refresh Data**:

    - Power BI Desktop: Click "Refresh" để cập nhật dữ liệu
    - Power BI Service: Cấu hình scheduled refresh trong Settings

2. **DirectQuery vs Import**:

    - **Import**: Dữ liệu được copy vào Power BI, refresh theo lịch
    - **DirectQuery**: Kết nối trực tiếp, dữ liệu real-time nhưng chậm hơn

3. **Best Practices**:
    - Sử dụng measures thay vì calculated columns khi có thể
    - Optimize queries trong Synapse trước khi load vào Power BI
    - Sử dụng aggregations để tăng performance

---

## Tổng kết

✅ **Bước 8**: Redis Cache đã được implement với TTL 30 giây
✅ **Bước 9**: Hướng dẫn chi tiết về Synapse Analytics
✅ **Bước 10**: Hướng dẫn chi tiết về Power BI

### Checklist hoàn thành:

-   [ ] Tạo Azure Cache for Redis và cấu hình
-   [ ] Test Redis Cache hoạt động
-   [ ] Tạo Azure Synapse Workspace
-   [ ] Kết nối CosmosDB với Synapse
-   [ ] Copy dữ liệu vào SQL Pool
-   [ ] Tạo queries và views
-   [ ] Cài đặt Power BI Desktop
-   [ ] Kết nối Power BI với Synapse
-   [ ] Tạo biểu đồ "Số lượng sinh viên"
-   [ ] Publish report lên Power BI Service
