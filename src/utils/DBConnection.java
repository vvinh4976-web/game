package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    // Thông tin cấu hình kết nối
    private static final String URL = "jdbc:mysql://localhost:3306/quanlythuchi_db";
    private static final String USER = "root";      // Mặc định của XAMPP
    private static final String PASSWORD = "";      // Mặc định của XAMPP là để trống

    public static Connection getConnection() {
        Connection conn = null;
        try {
            // 1. Đăng ký Driver (Không bắt buộc với các bản Java mới nhưng nên có)
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // 2. Thiết lập kết nối
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            
        } catch (ClassNotFoundException e) {
            System.out.println("Không tìm thấy Driver MySQL! Hãy kiểm tra file .jar");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Kết nối thất bại! Hãy kiểm tra XAMPP hoặc tên Database.");
            e.printStackTrace();
        }
        return conn;
    }

    // Hàm hỗ trợ đóng kết nối (Tránh lỗi rác bộ nhớ - Tiêu chí 2)
    public static void closeConnection(Connection conn) {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
