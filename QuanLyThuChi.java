import java.awt.*;
import java.sql.*;
import java.util.Vector;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class QuanLyThuChi {
    static final String URL = "jdbc:mysql://localhost:3306/db_quanlythuchi";
    static final String USER = "root";
    static final String PASS = "";

    // Khai báo bảng ở ngoài để các hàm khác dùng chung
    static DefaultTableModel tableModel;
    static JTable table;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Quản lý Thu chi - Nhóm 3");
        frame.setSize(600, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout(10, 10));

        // --- PHẦN 1: PANEL NHẬP LIỆU (BÊN TRÊN) ---
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        JTextField txtAmount = new JTextField();
        JTextField txtCategory = new JTextField();
        JTextField txtNote = new JTextField();
        JButton btnAdd = new JButton("Lưu Giao Dịch");

        inputPanel.add(new JLabel(" Số tiền:")); inputPanel.add(txtAmount);
        inputPanel.add(new JLabel(" Loại chi tiêu:")); inputPanel.add(txtCategory);
        inputPanel.add(new JLabel(" Ghi chú:")); inputPanel.add(txtNote);
        inputPanel.add(new JLabel("")); inputPanel.add(btnAdd);

        // --- PHẦN 2: BẢNG HIỂN THỊ (Ở GIỮA) ---
        tableModel = new DefaultTableModel(new String[]{"ID", "Số tiền", "Loại", "Ghi chú", "Ngày"}, 0);
        table = new JTable(tableModel);
        loadData(); // Tải dữ liệu từ DB lên bảng ngay khi mở app

        // --- PHẦN 3: XỬ LÝ SỰ KIỆN ---
        btnAdd.addActionListener(e -> {
            try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
                String sql = "INSERT INTO transactions (amount, category, note, transaction_date) VALUES (?, ?, ?, CURDATE())";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setDouble(1, Double.parseDouble(txtAmount.getText()));
                pstmt.setString(2, txtCategory.getText());
                pstmt.setString(3, txtNote.getText());

                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(frame, "Đã lưu thành công!");
                
                txtAmount.setText(""); txtCategory.setText(""); txtNote.setText("");
                loadData(); // Lưu xong thì tải lại bảng để cập nhật dòng mới
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Lỗi: " + ex.getMessage());
            }
        });

        frame.add(inputPanel, BorderLayout.NORTH);
        frame.add(new JScrollPane(table), BorderLayout.CENTER);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // Hàm lấy dữ liệu từ MySQL đổ vào JTable
    public static void loadData() {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
            tableModel.setRowCount(0); // Xóa bảng cũ
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM transactions ORDER BY transaction_date DESC");
            
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("id"));
                row.add(rs.getDouble("amount"));
                row.add(rs.getString("category"));
                row.add(rs.getString("note"));
                row.add(rs.getDate("transaction_date"));
                tableModel.addRow(row);
            }
        } catch (Exception e) {
            System.out.println("Lỗi load bảng: " + e.getMessage());
        }
    }
}