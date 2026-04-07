package controller;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.Transaction;
import utils.DBConnection;
/* * Lớp TransactionController xử lý các nghiệp vụ liên quan đến giao dịch.
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

        // Đã sửa lại thành prepareStatement ở dòng dưới đây:
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
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

    /**
     * Hàm lấy dữ liệu để vẽ biểu đồ (Dành cho Ý)
     * Giả sử: Danh mục "Lương" hoặc "Thưởng" là THU. Còn lại là CHI.
     */
    public double[] getSummaryData() {
        double totalThu = 0;
        double totalChi = 0;
        
        List<Transaction> list = getAllTransactions();
        for (Transaction t : list) {
            // Logic đơn giản: Nếu Category chứa chữ Lương hoặc Thu thì cộng vào Tổng Thu
            if (t.getCategory().toLowerCase().contains("lương") || t.getCategory().toLowerCase().contains("thu")) {
                totalThu += t.getAmount();
            } else {
                totalChi += t.getAmount(); // Còn lại tính là Chi
            }
        }
        return new double[]{totalThu, totalChi};
    }
    /**
     * Hàm lấy dữ liệu gom nhóm theo từng Danh mục để Ý vẽ biểu đồ Donut nhiều màu.
     * Hàm này sẽ tự động phân tích: Ví dụ có 3 khoản "Ăn uống" nó sẽ tự cộng dồn lại thành 1 cục.
     * @return Map chứa Tên danh mục và Tổng tiền của danh mục đó
     */
    public Map<String, Double> getExpenseSummaryByCategory() {
        Map<String, Double> summary = new HashMap<>();
        List<Transaction> list = getAllTransactions();

        for (Transaction t : list) {
            String cat = t.getCategory().toLowerCase();
            
            // Tạm thời bỏ qua các khoản Thu (Lương, Thưởng...) để biểu đồ chỉ phân tích Chi Tiêu (như app mẫu)
            if (cat.contains("lương") || cat.contains("thu") || cat.contains("thưởng")) {
                continue; 
            }

            // Tên danh mục gốc do người dùng nhập
            String categoryName = t.getCategory(); 
            
            // Cộng dồn tiền: Nếu danh mục đã có thì cộng thêm, chưa có thì gán bằng số tiền hiện tại
            summary.put(categoryName, summary.getOrDefault(categoryName, 0.0) + t.getAmount());
        }
        return summary;
    }
}