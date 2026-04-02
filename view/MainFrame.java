package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import controller.TransactionController;
import model.Transaction;

/**
 * Lớp MainFrame thực hiện hiển thị giao diện người dùng (GUI).
 * Thành phần 'View' trong mô hình MVC.
 */
public class MainFrame extends JFrame {

    private JTextField txtAmount, txtCategory, txtNote;
    private JTable table;
    private DefaultTableModel tableModel;
    private TransactionController controller;

    public MainFrame() {
        controller = new TransactionController();
        initUI();
    }

    private void initUI() {
        setTitle("HỆ THỐNG QUẢN LÝ THU CHI - NHÓM VINH Ý HIỀN");
        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Hiển thị giữa màn hình

        // Panel chính với khoảng cách lề (Padding)
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        setContentPane(mainPanel);

        // --- PHẦN 1: PANEL NHẬP LIỆU (NORTH) ---
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("Thông tin giao dịch"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtAmount = new JTextField(15);
        txtCategory = new JTextField(15);
        txtNote = new JTextField(15);
        JButton btnAdd = new JButton("Thêm Mới");
        btnAdd.setBackground(new Color(46, 204, 113));
        btnAdd.setForeground(Color.WHITE);

        // Bố trí các ô nhập liệu bằng GridBagLayout cho thẳng hàng
        gbc.gridx = 0; gbc.gridy = 0; inputPanel.add(new JLabel("Số tiền:"), gbc);
        gbc.gridx = 1; inputPanel.add(txtAmount, gbc);
        gbc.gridx = 0; gbc.gridy = 1; inputPanel.add(new JLabel("Loại chi tiêu:"), gbc);
        gbc.gridx = 1; inputPanel.add(txtCategory, gbc);
        gbc.gridx = 0; gbc.gridy = 2; inputPanel.add(new JLabel("Ghi chú:"), gbc);
        gbc.gridx = 1; inputPanel.add(txtNote, gbc);
        gbc.gridx = 1; gbc.gridy = 3; inputPanel.add(btnAdd, gbc);

        mainPanel.add(inputPanel, BorderLayout.NORTH);

        // --- PHẦN 2: BẢNG HIỂN THỊ (CENTER) ---
        String[] columnNames = {"ID", "Số Tiền", "Loại", "Ghi Chú", "Ngày"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        table.setRowHeight(30);
        mainPanel.add(new JScrollPane(table), BorderLayout.CENTER);

        // --- PHẦN 3: CÁC NÚT ĐIỀU KHIỂN (SOUTH) ---
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnDelete = new JButton("Xóa Dòng Chọn");
        JButton btnRefresh = new JButton("Làm Mới");
        JButton btnChart = new JButton("Xem Biểu Đồ");

        btnDelete.setBackground(new Color(231, 76, 60));
        btnDelete.setForeground(Color.WHITE);

        actionPanel.add(btnRefresh);
        actionPanel.add(btnChart);
        actionPanel.add(btnDelete);
        mainPanel.add(actionPanel, BorderLayout.SOUTH);

        // --- SỰ KIỆN NÚT BẤM ---
        
        // Nút Thêm
        btnAdd.addActionListener(e -> {
            try {
                double amount = Double.parseDouble(txtAmount.getText());
                String category = txtCategory.getText();
                String note = txtNote.getText();
                
                Transaction t = new Transaction(0, amount, category, note, "");
                if (controller.addTransaction(t)) {
                    JOptionPane.showMessageDialog(this, "Thêm thành công!");
                    clearFields();
                    loadDataToTable();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập số tiền hợp lệ!");
            }
        });

        // Nút Xóa
        btnDelete.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                int id = (int) tableModel.getValueAt(selectedRow, 0);
                int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa?", "Xác nhận", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    if (controller.deleteTransaction(id)) {
                        loadDataToTable();
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một dòng để xóa!");
            }
        });

        // Nút Làm mới
        btnRefresh.addActionListener(e -> loadDataToTable());

        loadDataToTable();
    }

    private void loadDataToTable() {
        tableModel.setRowCount(0);
        List<Transaction> list = controller.getAllTransactions();
        for (Transaction t : list) {
            tableModel.addRow(new Object[]{t.getId(), t.getAmount(), t.getCategory(), t.getNote(), t.getTransactionDate()});
        }
    }

    private void clearFields() {
        txtAmount.setText("");
        txtCategory.setText("");
        txtNote.setText("");
    }

    public static void main(String[] args) {
        // Áp dụng giao diện hệ thống cho đẹp hơn
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}