package view;

import controller.TransactionController;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.Transaction;

/**
 * Lớp giao diện chính của chương trình.
 * Thành phần 'View' trong mô hình MVC.
 */
public class MainFrame extends JFrame {
    private JTextField txtAmount, txtCategory, txtNote;
    private JTable table;
    private DefaultTableModel tableModel;
    private TransactionController controller;

    public MainFrame() {
        // Khởi tạo Controller để lấy dữ liệu
        controller = new TransactionController();
        initUI();
        loadDataToTable();
    }

    private void initUI() {
        setTitle("Quản Lý Thu Chi - Nhóm Vinh Ý Hiền");
        setSize(800, 550);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // --- PHẦN NHẬP LIỆU ---
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Nhập giao dịch mới"));

        inputPanel.add(new JLabel("  Số tiền:"));
        txtAmount = new JTextField();
        inputPanel.add(txtAmount);

        inputPanel.add(new JLabel("  Loại chi tiêu:"));
        txtCategory = new JTextField();
        inputPanel.add(txtCategory);

        inputPanel.add(new JLabel("  Ghi chú:"));
        txtNote = new JTextField();
        inputPanel.add(txtNote);

        JButton btnAdd = new JButton("Lưu Giao Dịch");
        btnAdd.setBackground(new Color(46, 204, 113));
        btnAdd.setForeground(Color.black);
        inputPanel.add(new JLabel("")); // Khoảng trống
        inputPanel.add(btnAdd);

        add(inputPanel, BorderLayout.NORTH);

        // --- PHẦN BẢNG HIỂN THỊ ---
        String[] columns = {"ID", "Số Tiền", "Loại", "Ghi Chú", "Ngày"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // --- PHẦN NÚT BẤM CHỨC NĂNG ---
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnDelete = new JButton("Xóa Dòng Chọn");
        btnDelete.setBackground(new Color(231, 76, 60));
        btnDelete.setForeground(Color.BLACK);
        actionPanel.add(btnDelete);
        add(actionPanel, BorderLayout.SOUTH);

        // --- XỬ LÝ SỰ KIỆN ---

        // Thêm dữ liệu
        btnAdd.addActionListener(e -> {
            try {
                double amount = Double.parseDouble(txtAmount.getText());
                String category = txtCategory.getText();
                String note = txtNote.getText();

                // Tạo đối tượng model (Ý đã làm)
                Transaction t = new Transaction(0, amount, category, note, "");
                
                // Gọi Controller (Hiền/Vinh đã làm) để lưu vào DB
                if (controller.addTransaction(t)) {
                    JOptionPane.showMessageDialog(this, "Thêm thành công!");
                    txtAmount.setText("");
                    txtCategory.setText("");
                    txtNote.setText("");
                    loadDataToTable();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập số tiền là số!");
            }
        });

        // Xóa dữ liệu
        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                int id = (int) tableModel.getValueAt(row, 0);
                if (controller.deleteTransaction(id)) {
                    loadDataToTable();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Hãy chọn 1 dòng để xóa!");
            }
        });
    }

    // Hàm load dữ liệu từ Database lên bảng
    public void loadDataToTable() {
        tableModel.setRowCount(0);
        List<Transaction> list = controller.getAllTransactions();
        for (Transaction t : list) {
            tableModel.addRow(new Object[]{
                t.getId(), 
                t.getAmount(), 
                t.getCategory(), 
                t.getNote(), 
                t.getDate() // Kiểm tra xem file Transaction.java của Ý là getDate() hay getTransactionDate() nhé
            });
        }
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}