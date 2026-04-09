# 💰 Phần Mềm Quản Lý Tài Chính Cá Nhân Pro

Dự án đồ án môn học Lập trình Java Swing & Hệ quản trị CSDL MySQL.
**🔗 [Link Video Demo Hướng Dẫn Sử Dụng trên YouTube](Dán_link_youtube_của_nhóm_vào_đây)**

---
## ✨ Các chức năng chính (Features)
- [x] Quản lý thông tin (Thêm, Xóa, Liệt kê dữ liệu) tích hợp thanh tìm kiếm thông minh **Live Search**.
- [x] Lưu trữ dữ liệu vĩnh viễn với Cơ sở dữ liệu MySQL an toàn.
- [x] Giao diện người dùng (GUI) thân thiện, thiết kế **Dark Theme** hiện đại bằng thư viện FlatLaf cùng hiệu ứng Hover mượt mà.
- [x] Bắt lỗi nhập liệu chặt chẽ (Exception Handling) và cảnh báo trực quan.
- [x] **[Tính năng nổi bật 1]: Trực quan hóa dữ liệu chi tiêu bằng Biểu đồ Donut (JFreeChart) tùy biến màu Pastel và tích hợp hiệu ứng hoạt ảnh (Animation) xoay 360 độ.**
- [x] **[Tính năng nổi bật 2]: Hệ thống Cố vấn Tài chính Thông minh: Tự động phân bổ ngân sách (Quy tắc 50/30/20), cảnh báo rủi ro chi tiêu âm tiền và dự báo tích lũy lãi kép trong tương lai.**
- [x] **[Tính năng nổi bật 3]: Hệ thống Đăng nhập (Login) an toàn, hỗ trợ nhận diện nhiều người dùng (Vinh, Hiền, Ý) với lời chào cá nhân hóa.**
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