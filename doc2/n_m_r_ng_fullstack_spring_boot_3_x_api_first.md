# ĐỒ ÁN MỞ RỘNG FULLSTACK: SPRING BOOT 3.X & API-FIRST

## ĐỀ BÀI BÀI TẬP LỚN FULLSTACK: SHOP E-COMMERCE
**Phát triển Toàn diện từ Phân tích BA, Quy trình API-First, Kiểm thử Contract đến Tích hợp BE (Spring Boot 3.x) & FE**

| Thông tin | Chi tiết |
| :--- | :--- |
| **Đối tượng** | Sinh viên Lập trình Java / Spring Boot / Fullstack |
| **Thời lượng** | Bài tập lớn / Đồ án môn học |
| **Công nghệ BE** | Java 21, Spring Boot 3.4+, Spring Data JPA, MySQL |
| **Công nghệ FE & API** | OpenAPI 3.0, Swagger, HTML5/JS/React |

---

## TỔNG QUAN YÊU CẦU ĐỒ ÁN
Yêu cầu bắt buộc đạt được:
1. **Hoàn thành Tài liệu BA:** Xác định đầy đủ Luồng nghiệp vụ (Business Workflow) và User Stories cho 3 module (Khách hàng, Sản phẩm, Đơn hàng).
2. **Hoàn thành API-First & Contract Test:** Soạn thảo file đặc tả OpenAPI 3.0, khởi chạy Mock Server kết nối UI prototype và viết test kịch bản API trước khi code Backend.
3. **Hoàn thành Backend & Frontend:** Sinh mã server stub tự động, triển khai logic nghiệp vụ với `@Transactional`, database seeding dữ liệu mẫu và xây dựng giao diện FE kết nối tương tác hoàn chỉnh.
4. **Kiểm thử Toàn diện:** Thực thi Unit Test, Integration Test và E2E Flow Test.

---

## 1. PHÂN TÍCH NGHIỆP VỤ & PHẦN CHỨC NĂNG BA (BUSINESS ANALYST)
Sinh viên cần đóng vai trò Business Analyst để làm rõ các quy tắc nghiệp vụ (Business Rules) và luồng xử lý trước khi xây dựng hệ thống:
* **Module Khách hàng (Customers):** Đăng ký, đăng nhập tài khoản, quản lý thông tin cá nhân và xem lịch sử đơn hàng. Bắt buộc kiểm tra tính hợp lệ của Email và Số điện thoại.
* **Module Danh mục & Sản phẩm (Categories & Products):** Quản lý danh mục hàng hóa. Sản phẩm bao gồm giá bán, hình ảnh, mô tả và số lượng tồn kho (Stock Quantity). Hỗ trợ tìm kiếm, lọc theo giá và phân trang.
* **Module Giỏ hàng & Đặt hàng (Cart & Orders):** Cho phép thêm/sửa/xóa sản phẩm trong giỏ hàng. Khi bấm Đặt hàng (Checkout): kiểm tra tồn kho, tính tổng tiền, tạo đơn hàng ở trạng thái `PENDING`, trừ số lượng tồn kho trong CSDL.
* **Quy tắc Quản lý Trạng thái Đơn hàng:** Đơn hàng chuyển đổi qua các trạng thái: `PENDING` (Chờ xử lý) $\rightarrow$ `PAID` (Đã thanh toán) $\rightarrow$ `SHIPPED` (Đã giao) hoặc `CANCELLED` (Đã hủy). Nếu hủy đơn, số lượng tồn kho phải được hoàn lại.

---

## 2. CHUẨN NGUYÊN TẮC RESTFUL API & QUY TRÌNH API-FIRST
Áp dụng nghiêm ngặt các nguyên tắc thiết kế API chuẩn mực theo các nguồn tài liệu đào tạo:
* **Đặt tên Endpoint chuẩn mực:** Dùng danh từ số nhiều (plural nouns) như `/api/v1/customers`, `/api/v1/products`, `/api/v1/orders`. Tuyệt đối không chứa động từ trên URL đường dẫn.
* **HTTP Verbs & Status Codes:** Dùng đúng `GET` (lấy dữ liệu), `POST` (tạo mới - trả về `201 Created`), `PUT/PATCH` (cập nhật), `DELETE` (xóa - trả về `204 No Content`). Chuẩn hóa mã lỗi `400 Bad Request`, `404 Not Found`, `422 Unprocessable Entity`, `500 Internal Error`.
* **Cấu trúc Phản hồi Lỗi JSON thống nhất:** Trả về Object dạng `{"code": "ERR_STOCK_EMPTY", "message": "Sản phẩm đã hết hàng", "details": [...]}`.
* **Quy trình API-First & Mock Server:**
  - **Bước 2.1:** Soạn thảo file đặc tả OpenAPI 3.0 (`shop-api.yaml`) làm *Single Source of Truth*.
  - **Bước 2.2:** Khởi chạy Mock Server (Swagger Editor / Postman Mock) để nhóm Frontend ghép nối giao diện UI/UX thử nghiệm luồng người dùng mà chưa cần code Backend.
  - **Bước 2.3:** Viết kịch bản kiểm thử hợp đồng API (Postman Collection Test Runner / BDD Gherkin) để kiểm tra tính hợp lệ của các Endpoint.

---

## 3. XÂY DỰNG BACKEND (SPRING BOOT 3.X, JPA & MYSQL)
Triển khai mã nguồn Backend đáp ứng các tiêu chí tối ưu hiệu năng và quản lý giao dịch an toàn:
* **Codegen tự động:** Sử dụng `openapi-generator-maven-plugin` sinh tự động các Java Server Interfaces và DTOs/Records bất biến vào thư mục `target/generated-sources`.
* **Quản lý Giao dịch `@Transactional`:**
  - Tất cả các hàm đọc truy vấn dữ liệu phải đánh dấu `@Transactional(readOnly = true)` để Hibernate tắt cơ chế Dirty Checking, giảm thiểu CPU và RAM.
  - Phương thức tạo Đơn hàng (Checkout) phải được bọc trong `@Transactional` để bảo đảm tính toàn vẹn dữ liệu (ACID) khi đồng thời tạo Đơn hàng và trừ Tồn kho sản phẩm.
* **Database Seeding:** Viết lớp `CommandLineRunner` kết hợp thư viện Faker để tự động nạp dữ liệu giả lập (danh mục, sản phẩm mẫu, khách hàng) khi ứng dụng khởi chạy.

---

## 4. XÂY DỰNG FRONTEND (UI/UX APPLICATION)
*Xây dựng ứng dụng giao diện người dùng (Single Page Application - HTML5/JS/React/Vue):*
* **Giao diện Bán hàng (Storefront UI):** Trang danh sách sản phẩm (có phân trang & lọc danh mục), trang chi tiết sản phẩm, giỏ hàng động và trang hoàn tất đơn hàng.
* **Giao diện Quản trị (Admin Dashboard):** Trang xem danh sách đơn hàng, thay đổi trạng thái đơn hàng (`PENDING` $\rightarrow$ `PAID` $\rightarrow$ `SHIPPED`) và quản lý tồn kho sản phẩm.
* **Tích hợp 2 Giai đoạn:** Giai đoạn 1 kết nối tới Mock Server từ file OpenAPI. Giai đoạn 2 chuyển đổi API Base URL sang kết nối Backend Spring Boot thực tế.

---

## 5. QUY TRÌNH KIỂM THỬ HỆ THỐNG (TESTING)
Sinh viên bắt buộc thực thi và nộp kết quả kiểm thử đầy đủ các cấp độ:
* **API Contract Test:** Chạy tự động bộ test trên Postman/Newman kiểm tra toàn bộ các Endpoint theo đặc tả OpenAPI.
* **Backend Unit & Integration Test:** Viết kiểm thử cho Service layer và Controller bằng `@SpringBootTest`, `MockMvc` (kiểm tra trường hợp đặt hàng thành công và trường hợp thất bại do hết tồn kho).
* **End-to-End (E2E) Test:** Thử nghiệm liên thông từ FE chọn sản phẩm $\rightarrow$ Đặt hàng $\rightarrow$ Backend xử lý ghi CSDL $\rightarrow$ FE hiển thị mã đơn hàng thành công.

---

## 6. SẢN PHẨM NỘP & BẢNG TIÊU CHÍ ĐÁNH GIÁ (RUBRIC)

| Tiêu chí đánh giá | Nội dung yêu cầu chi tiết | Điểm |
| :--- | :--- | :---: |
| **1. Tài liệu BA & Flow** | Đầy đủ User Stories, sơ đồ luồng nghiệp vụ Đặt hàng, Hủy đơn và quy tắc chuyển đổi trạng thái. | 1.5 |
| **2. Quy trình API-First** | File `shop-api.yaml` chuẩn OpenAPI 3.0, chạy Mock Server kết nối UI prototype, chạy API Contract Test thành công. | 2.5 |
| **3. Backend & Tối ưu JPA** | Maven Codegen sinh code, áp dụng `@Transactional(readOnly=true)` cho thao tác đọc, xử lý giao dịch Đặt hàng an toàn, Seeding dữ liệu bằng Faker. | 2.5 |
| **4. Frontend UI/UX** | Giao diện Storefront & Admin Dashboard hoạt động mượt mà, kết nối thành công với Spring Boot Backend. | 2.0 |
| **5. Testing & Nộp bài** | Đầy đủ Unit Test / Integration Test Backend, E2E Test và báo cáo thử nghiệm. | 1.5 |

*Phát triển Ứng dụng Doanh nghiệp với Spring Boot*