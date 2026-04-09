# 💰 Phần Mềm Quản Lý Tài Chính Cá Nhân Pro

Dự án đồ án môn học Lập trình Java Swing & Hệ quản trị CSDL MySQL.
**🔗 [Link Video Demo Hướng Dẫn Sử Dụng trên YouTube](Dán_link_youtube_của_nhóm_vào_đây)**

---

## 🚀 Tính năng nổi bật
* **Hệ thống Bảo mật:** Xác thực người dùng với màn hình Đăng nhập/Đăng ký an toàn.
* **Dashboard Thống kê:** Tự động tính toán và hiển thị tổng số giao dịch & tổng tiền theo thời gian thực.
* **Quản lý Thu Chi:** Thêm mới, xóa và liệt kê các khoản giao dịch.
* **Tìm kiếm Thông minh (Live Search):** Lọc dữ liệu tức thì ngay khi gõ từ khóa.
* **Biểu đồ Trực quan:** Phân tích chi tiêu qua biểu đồ Donut (JFreeChart) tích hợp hiệu ứng hoạt ảnh (Animation) xoay 360 độ và bảng màu Pastel.
* **Giao diện Hiện đại:** Sử dụng Dark Theme (FlatLaf) bảo vệ mắt, tối ưu UI/UX với hiệu ứng Hover mượt mà.

---

## 🛠 Công nghệ & Yêu cầu hệ thống
* **Ngôn ngữ:** Java (JDK 17+)
* **Cơ sở dữ liệu:** MySQL Server (XAMPP / MySQL Workbench)
* **Kiến trúc:** Mô hình MVC (Model - View - Controller)
* **Thư viện tích hợp (nằm trong thư mục `/lib`):**
  * `mysql-connector-java-*.jar`: Kết nối CSDL.
  * `jfreechart-*.jar`: Vẽ biểu đồ thống kê.
  * `flatlaf-*.jar`: Render giao diện Dark Theme.

---

## 📦 Hướng dẫn cài đặt & Chạy dự án
1. **Khởi tạo Database:** * Mở XAMPP, bật Apache và MySQL.
   * Truy cập `localhost/phpmyadmin` và import file `database.sql` đi kèm.
2. **Cấu hình Thư viện:** * Mở dự án bằng VS Code (hoặc Eclipse/IntelliJ).
   * Thêm toàn bộ các file `.jar` trong thư mục `lib` vào **Referenced Libraries** (hoặc Build Path).
3. **Khởi chạy Ứng dụng:** * Mở file **`Main.java`** (Nằm ở thư mục gốc, KHÔNG chạy trong thư mục view).
   * Ấn **Run** để khởi động màn hình Đăng nhập.

---

## 👥 Đội ngũ Phát triển
* **Vinh:** Backend Architecture, Xử lý Logic (Controller) & Thiết kế Cơ sở dữ liệu (MySQL).
* **Hiền:** UI/UX Design, Tích hợp FlatLaf & Tối ưu trải nghiệm người dùng (Frontend).
* **Ý:** Data Visualization (JFreeChart), Xử lý Hoạt ảnh (Animation) & Live Search.