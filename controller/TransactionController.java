package controller;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.Transaction;
import utils.DBConnection;

public class TransactionController {
    
    // 👉 [PHẦN CỦA VINH]: Hàm nòng cốt. Vinh dùng SQL "SELECT *" để lấy toàn bộ dữ liệu 
    // từ MySQL lên, sau đó Hiền sẽ dùng danh sách này để đổ vào bảng JTable ở Tab 1.
    /**
     * Hàm lấy toàn bộ danh sách giao dịch từ Database.
     * @return List danh sách các đối tượng Transaction.
     */
    public List<Transaction> getAllTransactions() {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT * FROM transactions ORDER BY transaction_date DESC";
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

    // 👉 [PHẦN CỦA VINH]: Xử lý logic Lưu dữ liệu. Vinh dùng PreparedStatement (có dấu ?)
    // để chèn dữ liệu mà Hiền thu thập từ giao diện (Tab 1) xuống Database an toàn.
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

    // 👉 [PHẦN CỦA VINH]: Xử lý logic Xóa dòng trong Database dựa vào ID.
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

    // 👉 [PHẦN CỦA VINH (Viết logic) & Ý (Sử dụng)]: 
    // Vinh viết thuật toán duyệt mảng này để lọc ra Tổng Thu và Tổng Chi, 
    // sau đó trả dữ liệu về để Ý đưa vào biểu đồ hoặc Hiền đưa lên nhãn Label.
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

    // 👉 [PHẦN CỦA VINH (Viết logic) & Ý (Sử dụng)]: 
    // Thay vì bắt Ý phải tự viết câu lệnh SQL "GROUP BY" phức tạp trên giao diện, 
    // Vinh đóng vai trò Backend gộp sẵn tiền theo từng danh mục (như Thuật toán Map-Reduce). 
    // Ý chỉ việc gọi hàm này để lấy cục Map<String, Double> nhét thẳng vào biểu đồ Donut JFreeChart.
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
            // bỏ qua các lương thu thưởng để chỉ tập trung vào chi tiêu 
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