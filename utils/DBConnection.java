package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    
    // Thông tin cấu hình database (Sử dụng CamelCase cho tên biến)
    private static final String hostName = "localhost";
    private static final String dbName = "db_quanlythuchi"; // Tên database đã tạo trong XAMPP
    private static final String userName = "root";
    private static final String password = ""; // XAMPP mặc định để trống
    
    // Chuỗi kết nối có thêm các tham số để tránh lỗi múi giờ và SSL
    private static final String connectionUrl = "jdbc:mysql://" + hostName + ":3306/" + dbName 
            + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

    /**
     * Hàm thiết lập kết nối đến MySQL.
     * @return Connection đối tượng kết nối
     * @throws SQLException nếu kết nối thất bại
     */
    public static Connection getConnection() throws SQLException {
        Connection conn = null;
        try {
            // Đăng ký Driver (tùy chọn với các bản Java mới nhưng nên có để ổn định)
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(connectionUrl, userName, password);
            System.out.println("Kết nối Database thành công!");
        } catch (ClassNotFoundException e) {
            System.out.println("Không tìm thấy Driver MySQL! Hãy kiểm tra file .jar trong thư mục lib.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Lỗi kết nối: Hãy đảm bảo XAMPP đã bật MySQL.");
            throw e;
        }
        return conn;
    }
}