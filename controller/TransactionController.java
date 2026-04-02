package controller;

import model.Transaction;
import utils.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp TransactionController xử lý các nghiệp vụ liên quan đến giao dịch.
 * Thành phần 'Controller' trong mô hình MVC.
 */
public class TransactionController {

    /**
     * Hàm lấy toàn bộ danh sách giao dịch từ Database.
     * @return List danh sách các đối tượng Transaction.
     */
    public List<Transaction> getAllTransactions() {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT * FROM transactions ORDER BY transaction_date DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareCall(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Transaction t = new Transaction(
                    rs.getInt("id"),
                    rs.getDouble("amount"),
                    rs.getString("category"),
                    rs.getString("note"),
                    rs.getString("transaction_date")
                );
                list.add(t);
            }
        } catch (SQLException e) {
            System.out.println("Lỗi khi lấy dữ liệu: " + e.getMessage());
        }
        return list;
    }

    /**
     * Hàm thêm một giao dịch mới vào Database.
     * @param t Đối tượng giao dịch cần thêm.
     * @return true nếu thêm thành công, false nếu thất bại.
     */
    public boolean addTransaction(Transaction t) {
        String sql = "INSERT INTO transactions (amount, category, note, transaction_date) VALUES (?, ?, ?, CURDATE())";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDouble(1, t.getAmount());
            pstmt.setString(2, t.getCategory());
            pstmt.setString(3, t.getNote());
            
            int result = pstmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            System.out.println("Lỗi khi thêm giao dịch: " + e.getMessage());
            return false;
        }
    }

    /**
     * Hàm xóa một giao dịch dựa trên ID.
     * @param id Mã định danh của giao dịch.
     * @return true nếu xóa thành công.
     */
    public boolean deleteTransaction(int id) {
        String sql = "DELETE FROM transactions WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            int result = pstmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            System.out.println("Lỗi khi xóa giao dịch: " + e.getMessage());
            return false;
        }
    }
}
