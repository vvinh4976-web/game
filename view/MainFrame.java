package view;

import controller.TransactionController;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import model.Transaction;

public class MainFrame extends JFrame {
    private JTextField txtAmount, txtCategory, txtNote;
    private JComboBox<String> cbxType; // Thêm ComboBox chọn Thu/Chi
    private JTable table;
    private DefaultTableModel tableModel;
    private TransactionController controller;
    private JPanel chartContainer; // Nơi Ý sẽ nhúng biểu đồ vào

    public MainFrame() {
        controller = new TransactionController();
        initUI();
        loadDataToTable();
    }

    private void initUI() {
        setTitle("Phần Mềm Quản Lý Tài Chính - Nhóm Vinh, Hiền, Ý");
        setSize(950, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Sử dụng JTabbedPane để chia Tab
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));

        // --- TAB 1: QUẢN LÝ GIAO DỊCH ---
        JPanel panelQuanLy = new JPanel(new BorderLayout(10, 10));
        panelQuanLy.setBorder(new EmptyBorder(10, 10, 10, 10));
        panelQuanLy.setBackground(new Color(240, 248, 255)); // Màu nền xanh nhạt cực đẹp

        // 1. Form nhập liệu (Bên trên)
        JPanel inputPanel = new JPanel(new GridLayout(2, 4, 15, 15));
        inputPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY), "📋 Nhập Giao Dịch Mới", 
                0, 0, new Font("Arial", Font.BOLD, 14), Color.BLUE));
        inputPanel.setBackground(Color.WHITE);

        inputPanel.add(new JLabel("💰 Số tiền (VNĐ):"));
        txtAmount = new JTextField();
        inputPanel.add(txtAmount);

        inputPanel.add(new JLabel("🏷️ Danh mục (Ăn uống, Lương...):"));
        txtCategory = new JTextField();
        inputPanel.add(txtCategory);

        inputPanel.add(new JLabel("📝 Ghi chú:"));
        txtNote = new JTextField();
        inputPanel.add(txtNote);

        JButton btnAdd = new JButton("💾 LƯU GIAO DỊCH");
        btnAdd.setBackground(new Color(46, 204, 113)); // Màu xanh lá cây
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFont(new Font("Arial", Font.BOLD, 14));
        inputPanel.add(btnAdd);

        panelQuanLy.add(inputPanel, BorderLayout.NORTH);

        // 2. Bảng hiển thị JTable (Ở giữa)
        String[] columns = {"ID", "Số Tiền", "Danh Mục", "Ghi Chú", "Ngày Giao Dịch"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(52, 152, 219));
table.getTableHeader().setForeground(Color.WHITE);
        
        panelQuanLy.add(new JScrollPane(table), BorderLayout.CENTER);

        // 3. Nút Xóa (Bên dưới)
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setBackground(new Color(240, 248, 255));
        JButton btnDelete = new JButton("🗑️ XÓA DÒNG CHỌN");
        btnDelete.setBackground(new Color(231, 76, 60)); // Màu đỏ
        btnDelete.setForeground(Color.WHITE);
        actionPanel.add(btnDelete);
        panelQuanLy.add(actionPanel, BorderLayout.SOUTH);

        // --- TAB 2: THỐNG KÊ BIỂU ĐỒ (Dành cho Ý) ---
        JPanel panelThongKe = new JPanel(new BorderLayout());
        chartContainer = new JPanel(new BorderLayout()); // Container rỗng để Ý nhúng JFreeChart vào
        chartContainer.setBackground(Color.WHITE);
        
        JButton btnRefreshChart = new JButton("🔄 Làm mới biểu đồ");
        btnRefreshChart.addActionListener(e -> updateChart()); // Nút để load lại biểu đồ

        panelThongKe.add(btnRefreshChart, BorderLayout.NORTH);
        panelThongKe.add(chartContainer, BorderLayout.CENTER);

        // Add 2 Tab vào cửa sổ chính
        tabbedPane.addTab("💸 Quản Lý Thu Chi", panelQuanLy);
        tabbedPane.addTab("📊 Biểu Đồ Thống Kê", panelThongKe);
        add(tabbedPane);

        // ================= XỬ LÝ SỰ KIỆN (BẮT LỖI) =================

        btnAdd.addActionListener(e -> {
            try {
                // Kiểm tra rỗng
                if (txtAmount.getText().isEmpty() || txtCategory.getText().isEmpty()) {
                    throw new Exception("Không được để trống Số tiền và Danh mục!");
                }
                
                // Parse ép kiểu, nếu nhập chữ sẽ nhảy xuống catch NumberFormatException
                double amount = Double.parseDouble(txtAmount.getText());
                
                // Bắt lỗi số âm
                if (amount <= 0) {
                    throw new Exception("Số tiền phải lớn hơn 0!");
                }

                String category = txtCategory.getText();
                String note = txtNote.getText();

                // Tạo object và đẩy xuống DB
                Transaction t = new Transaction(0, amount, category, note, "");
                if (controller.addTransaction(t)) {
                    JOptionPane.showMessageDialog(this, "✅ Lưu thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    txtAmount.setText(""); txtCategory.setText(""); txtNote.setText("");
                    loadDataToTable(); // Tải lại bảng
                    updateChart();     // Tải lại biểu đồ
                } else {
                    JOptionPane.showMessageDialog(this, "❌ Lỗi hệ thống, không thể lưu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
} catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "⚠️ Ô Số tiền chỉ được phép nhập số!", "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "⚠️ " + ex.getMessage(), "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa?", "Xác nhận", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    int id = (int) tableModel.getValueAt(row, 0);
                    if (controller.deleteTransaction(id)) {
                        loadDataToTable();
                        updateChart();
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "⚠️ Vui lòng click chọn 1 dòng trên bảng để xóa!");
            }
        });
    }

    public void loadDataToTable() {
        tableModel.setRowCount(0);
        List<Transaction> list = controller.getAllTransactions();
        for (Transaction t : list) {
            tableModel.addRow(new Object[]{
                t.getId(), String.format("%,.0f VNĐ", t.getAmount()), t.getCategory(), t.getNote(), t.getDate()
            });
        }
    }

    // Hàm gọi biểu đồ của Ý
    private void updateChart() {
        chartContainer.removeAll();
        // Nơi này Ý sẽ code hàm lấy Panel biểu đồ dán vào đây
        // Ví dụ: chartContainer.add(Ý_Chart_Class.createChartPanel());
        chartContainer.revalidate();
        chartContainer.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}