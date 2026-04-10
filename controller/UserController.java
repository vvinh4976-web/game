package controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import utils.DBConnection;

public class UserController {
    
    // 1. Hàm kiểm tra Đăng nhập
    public String login(String username, String password) {
        String sql = "SELECT display_name FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getString("display_name"); // Trả về Tên hiển thị nếu đúng
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Trả về null nếu sai tài khoản/mật khẩu
    }

    // 2. Hàm xử lý Đăng ký
    public boolean register(String username, String password, String displayName) {
        String sql = "INSERT INTO users (username, password, display_name) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, displayName);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            // Lỗi thường do trùng Username (vì cột username đặt là UNIQUE)
            System.err.println("Lỗi đăng ký: Tài khoản có thể đã tồn tại.");
            return false;
        }
    }
}