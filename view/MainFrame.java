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
    private JTable table;
    private DefaultTableModel tableModel;
    private TransactionController controller;
    private JPanel chartContainer; 

    public MainFrame() {
        controller = new TransactionController();
        initUI();
        loadDataToTable();
        updateChart(); // Gọi biểu đồ ngay khi mở app
    }

    private void initUI() {
        // Đã xóa Emoji để không bị lỗi ô vuông
        setTitle("Phan Mem Quan Ly Tai Chinh - Nhom Vinh, Hien, Y");
        setSize(950, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));

        // --- TAB 1: QUẢN LÝ GIAO DỊCH ---
        JPanel panelQuanLy = new JPanel(new BorderLayout(10, 10));
        panelQuanLy.setBorder(new EmptyBorder(10, 10, 10, 10));
        panelQuanLy.setBackground(new Color(240, 248, 255)); 

        // 1. Form nhập liệu
        JPanel inputPanel = new JPanel(new GridLayout(2, 4, 15, 15));
        inputPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY), "Nhap Giao Dich Moi", 
                0, 0, new Font("Arial", Font.BOLD, 14), Color.BLUE));
        inputPanel.setBackground(Color.WHITE);

        inputPanel.add(new JLabel("So tien (VND):"));
        txtAmount = new JTextField();
        inputPanel.add(txtAmount);

        inputPanel.add(new JLabel("Danh muc (An uong, Luong...):"));
        txtCategory = new JTextField();
        inputPanel.add(txtCategory);

        inputPanel.add(new JLabel("Ghi chu:"));
        txtNote = new JTextField();
        inputPanel.add(txtNote);

        JButton btnAdd = new JButton("LUU GIAO DICH");
        btnAdd.setBackground(new Color(46, 204, 113)); 
        btnAdd.setForeground(Color.BLACK); // Đã đổi thành màu ĐEN để hiện rõ chữ
        btnAdd.setFont(new Font("Arial", Font.BOLD, 14));
        inputPanel.add(btnAdd);

        panelQuanLy.add(inputPanel, BorderLayout.NORTH);

        // 2. Bảng hiển thị JTable
        String[] columns = {"ID", "So Tien", "Danh Muc", "Ghi Chu", "Ngay Giao Dich"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(52, 152, 219));
        table.getTableHeader().setForeground(Color.WHITE);
        
        panelQuanLy.add(new JScrollPane(table), BorderLayout.CENTER);

        // 3. Nút Xóa
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setBackground(new Color(240, 248, 255));
        JButton btnDelete = new JButton("XOA DONG CHON");
        btnDelete.setBackground(new Color(231, 76, 60)); 
        btnDelete.setForeground(Color.BLACK); // Đã đổi thành màu ĐEN
        actionPanel.add(btnDelete);
        panelQuanLy.add(actionPanel, BorderLayout.SOUTH);

        // --- TAB 2: THỐNG KÊ BIỂU ĐỒ ---
        JPanel panelThongKe = new JPanel(new BorderLayout());
        chartContainer = new JPanel(new BorderLayout()); 
        chartContainer.setBackground(Color.WHITE);
        
        JButton btnRefreshChart = new JButton("Lam moi bieu do");
        btnRefreshChart.addActionListener(e -> updateChart()); 

        panelThongKe.add(btnRefreshChart, BorderLayout.NORTH);
        panelThongKe.add(chartContainer, BorderLayout.CENTER);

        // Add 2 Tab
        tabbedPane.addTab("Quan Ly Thu Chi", panelQuanLy);
        tabbedPane.addTab("Bieu Do Thong Ke", panelThongKe);
        add(tabbedPane);

        // ================= XỬ LÝ SỰ KIỆN =================

        btnAdd.addActionListener(e -> {
            try {
                if (txtAmount.getText().isEmpty() || txtCategory.getText().isEmpty()) {
                    throw new Exception("Khong duoc de trong So tien va Danh muc!");
                }
                
                double amount = Double.parseDouble(txtAmount.getText());
                
                if (amount <= 0) {
                    throw new Exception("So tien phai lon hon 0!");
                }

                String category = txtCategory.getText();
                String note = txtNote.getText();

                Transaction t = new Transaction(0, amount, category, note, "");
                if (controller.addTransaction(t)) {
                    JOptionPane.showMessageDialog(this, "Luu thanh cong!", "Thong bao", JOptionPane.INFORMATION_MESSAGE);
                    txtAmount.setText(""); txtCategory.setText(""); txtNote.setText("");
                    loadDataToTable(); 
                    updateChart(); // Cập nhật lại biểu đồ sau khi lưu
                } else {
                    JOptionPane.showMessageDialog(this, "Loi he thong, khong the luu!", "Loi", JOptionPane.ERROR_MESSAGE);
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "O So tien chi duoc phep nhap so!", "Loi nhap lieu", JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Loi nhap lieu", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                int confirm = JOptionPane.showConfirmDialog(this, "Ban co chac muon xoa?", "Xac nhan", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    int id = (int) tableModel.getValueAt(row, 0);
                    if (controller.deleteTransaction(id)) {
                        loadDataToTable();
                        updateChart();
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Vui long click chon 1 dong tren bang de xoa!");
            }
        });
    }

    public void loadDataToTable() {
        tableModel.setRowCount(0);
        List<Transaction> list = controller.getAllTransactions();
        for (Transaction t : list) {
            tableModel.addRow(new Object[]{
                t.getId(), String.format("%,.0f VND", t.getAmount()), t.getCategory(), t.getNote(), t.getDate()
            });
        }
    }

    // ================= HÀM VẼ BIỂU ĐỒ (Đã được dời ra ngoài đúng chuẩn) =================
    private void updateChart() {
        chartContainer.removeAll();
        
        double[] summary = controller.getSummaryData();
        double totalThu = summary[0];
        double totalChi = summary[1];

        if (totalThu == 0 && totalChi == 0) {
            chartContainer.revalidate();
            chartContainer.repaint();
            return;
        }

        org.jfree.data.general.DefaultPieDataset dataset = new org.jfree.data.general.DefaultPieDataset();
        dataset.setValue("Tong Thu", totalThu);
        dataset.setValue("Tong Chi", totalChi);

        org.jfree.chart.JFreeChart pieChart = org.jfree.chart.ChartFactory.createPieChart(
                "BIEU DO TY LE THU CHI", dataset, true, true, false);

        org.jfree.chart.ChartPanel chartPanel = new org.jfree.chart.ChartPanel(pieChart);
        chartContainer.add(chartPanel, java.awt.BorderLayout.CENTER);
        
        chartContainer.revalidate();
        chartContainer.repaint(); 
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}