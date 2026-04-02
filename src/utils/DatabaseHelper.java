import java.sql.*;

public class DatabaseHelper {
    private static final String URL = "jdbc:mysql://localhost:3306/db_quanlythuchi";
    private static final String USER = "root";
    private static final String PASS = "";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    // Hàm thêm dữ liệu (Bạn 1 - Eclipse sẽ phụ trách tối ưu hàm này)
    public static boolean insert(double amount, String category, String note) {
        String sql = "INSERT INTO transactions (amount, category, note, transaction_date) VALUES (?, ?, ?, CURDATE())";
        try (Connection conn = getConnection(); 
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, amount);
            pstmt.setString(2, category);
            pstmt.setString(3, note);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Hàm lấy dữ liệu cho bảng
    public static ResultSet getAll() throws SQLException {
        Connection conn = getConnection();
        Statement stmt = conn.createStatement();
        return stmt.executeQuery("SELECT * FROM transactions ORDER BY transaction_date DESC");
    }
}
