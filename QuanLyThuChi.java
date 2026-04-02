import java.awt.*;
import java.sql.*;
import javax.swing.*;

public class QuanLyThuChi {
    // Thông tin kết nối MySQL
    static final String URL = "jdbc:mysql://localhost:3306/db_quanlythuchi";
    static final String USER = "root";
    static final String PASS = "";

    public static void main(String[] args) {
        JFrame frame = new JFrame("Quản lý Thu chi - Nhóm 3");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(5, 2, 10, 10));

        // 1. Khai báo các thành phần
        JTextField txtAmount = new JTextField();
        JTextField txtCategory = new JTextField();
        JTextField txtNote = new JTextField();
        JButton btnAdd = new JButton("Lưu Giao Dịch");

        // 2. PHẢI THÊM VÀO FRAME (Quan trọng nhất)
        frame.add(new JLabel(" Số tiền:")); frame.add(txtAmount);
        frame.add(new JLabel(" Loại chi tiêu:")); frame.add(txtCategory);
        frame.add(new JLabel(" Ghi chú:")); frame.add(txtNote);
        frame.add(new JLabel("")); frame.add(btnAdd);

        // 3. CHO HIỆN CỬA SỔ
        frame.setVisible(true);

        // Giữ nguyên đoạn try-catch kết nối DB của bạn ở dưới...
    }

        frame.add(new JLabel(" Số tiền:")); frame.add(txtAmount);
        frame.add(new JLabel(" Loại (Ăn uống...):")); frame.add(txtCategory);
        frame.add(new JLabel(" Ghi chú:")); frame.add(txtNote);
        frame.add(new JLabel("")); frame.add(btnAdd);

        // Sự kiện khi nhấn nút Lưu
        btnAdd.addActionListener(e -> {
            try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
                String sql = "INSERT INTO transactions (amount, category, note, transaction_date) VALUES (?, ?, ?, CURDATE())";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setDouble(1, Double.parseDouble(txtAmount.getText()));
                pstmt.setString(2, txtCategory.getText());
                pstmt.setString(3, txtNote.getText());

                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(frame, "Đã lưu thành công vào Database!");
                
                // Xóa trắng ô nhập sau khi lưu
                txtAmount.setText(""); txtCategory.setText(""); txtNote.setText("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Lỗi: " + ex.getMessage());
            }
        });

        frame.setVisible(true);
    }
}