import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.Vector;
public class MainFrame extends JFrame {
    DefaultTableModel tableModel;
    JTable table;
    JTextField txtAmount, txtCategory, txtNote;

    public MainFrame() {
        setTitle("Quản lý Thu chi - Giao diện chính");
        setSize(700, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Khu vực nhập liệu
        JPanel pnlInput = new JPanel(new GridLayout(4, 2, 5, 5));
        txtAmount = new JTextField();
        txtCategory = new JTextField();
        txtNote = new JTextField();
        JButton btnAdd = new JButton("Thêm mới");
        
        pnlInput.add(new JLabel("Số tiền:")); pnlInput.add(txtAmount);
        pnlInput.add(new JLabel("Loại:")); pnlInput.add(txtCategory);
        pnlInput.add(new JLabel("Ghi chú:")); pnlInput.add(txtNote);
        pnlInput.add(new JLabel("")); pnlInput.add(btnAdd);

        // Khu vực bảng
        tableModel = new DefaultTableModel(new String[]{"ID", "Số tiền", "Loại", "Ghi chú", "Ngày"}, 0);
        table = new JTable(tableModel);
        add(pnlInput, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Sự kiện nút thêm
        btnAdd.addActionListener(e -> {
            double amount = Double.parseDouble(txtAmount.getText());
            if (DatabaseHelper.insert(amount, txtCategory.getText(), txtNote.getText())) {
                JOptionPane.showMessageDialog(this, "Thêm thành công!");
                refreshTable();
            }
        });

        refreshTable();
        setVisible(true);
    }

    public void refreshTable() {
        try {
            tableModel.setRowCount(0);
            ResultSet rs = DatabaseHelper.getAll();
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("id"));
                row.add(rs.getDouble("amount"));
                row.add(rs.getString("category"));
                row.add(rs.getString("note"));
                row.add(rs.getDate("transaction_date"));
                tableModel.addRow(row);
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static void main(String[] args) { new MainFrame(); }
}