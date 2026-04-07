package view;

import controller.TransactionController;
import java.awt.*;
import java.text.NumberFormat;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import model.Transaction;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatDarkLaf; 
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatDarkLaf;

// Import thư viện JFreeChart
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.RingPlot;
import org.jfree.data.general.DefaultPieDataset;

// Import thư viện FlatLaf cho giao diện Dark Theme
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatDarkLaf;

public class MainFrame extends JFrame {
    private JTextField txtAmount, txtCategory, txtNote;
    private JTable table;
    private DefaultTableModel tableModel;
    private TransactionController controller;
    private JPanel chartContainer; 
    
    private Timer chartTimer;

    // ĐỊNH NGHĨA BẢNG MÀU DARK THEME HIỆN ĐẠI
    private final Color COLOR_BG = new Color(18, 18, 18); // Nền đen sâu (giống ảnh mẫu)
    private final Color COLOR_PANEL_BG = new Color(30, 30, 30); // Nền panel xám đậm
    private final Color COLOR_TEXT = new Color(240, 240, 240); // Chữ trắng sáng
    private final Color COLOR_ACCENT_GREEN = new Color(46, 204, 113); // Xanh lá
    private final Color COLOR_ACCENT_RED = new Color(231, 76, 60); // Đỏ

    public MainFrame() {
        controller = new TransactionController();
        
        // Kích hoạt giao diện Dark Theme ngay khi khởi tạo
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception ex) {
            System.err.println("Loi FlatLaf");
        }

        initUI();
        loadDataToTable();
        updateChart(); 
    }

    private void initUI() {
        setTitle("Quan Ly Tai Chinh - Nhom Vinh, Hien, Y");
        setSize(950, 670);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(COLOR_BG);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));

        // --- TAB 1: QUẢN LÝ GIAO DỊCH ---
        JPanel panelQuanLy = new JPanel(new BorderLayout(15, 15));
        panelQuanLy.setBorder(new EmptyBorder(15, 15, 15, 15));
        panelQuanLy.setBackground(COLOR_BG); 

        // 1. Form nhập liệu
        JPanel inputPanel = new JPanel(new GridLayout(2, 4, 15, 15));
        inputPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY), " Nhap Giao Dich Moi ", 
                0, 0, new Font("Segoe UI", Font.BOLD, 15), COLOR_TEXT));
        inputPanel.setBackground(COLOR_PANEL_BG);

        Font labelFont = new Font("Segoe UI", Font.BOLD, 13);
        
        JLabel lblAmount = new JLabel(" So tien (VND):"); lblAmount.setForeground(COLOR_TEXT); lblAmount.setFont(labelFont);
        inputPanel.add(lblAmount);
        txtAmount = new JTextField(); 
        inputPanel.add(txtAmount);

        JLabel lblCat = new JLabel(" Danh muc:"); lblCat.setForeground(COLOR_TEXT); lblCat.setFont(labelFont);
        inputPanel.add(lblCat);
        txtCategory = new JTextField(); 
        inputPanel.add(txtCategory);

        JLabel lblNote = new JLabel(" Ghi chu:"); lblNote.setForeground(COLOR_TEXT); lblNote.setFont(labelFont);
        inputPanel.add(lblNote);
        txtNote = new JTextField(); 
        inputPanel.add(txtNote);

        // Nút LƯU GIAO DỊCH bo góc "nổi"
        JButton btnAdd = new JButton("LUU GIAO DICH");
        btnAdd.setBackground(COLOR_ACCENT_GREEN); 
        btnAdd.setForeground(Color.BLACK); 
        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 14));
btnAdd.putClientProperty(FlatClientProperties.STYLE, "" +
                "arc: 10; " + 
                "borderWidth: 0; " +
                "hoverBackground: #27ae60; " + // Màu xanh lá đậm hơn khi lướt chuột
                "pressedBackground: #1e8449;"); // Màu xanh lá tối nhất khi bấm chuột
        inputPanel.add(btnAdd);

        panelQuanLy.add(inputPanel, BorderLayout.NORTH);

        // 2. Bảng JTable
        String[] columns = {"ID", "So Tien", "Danh Muc", "Ngay Giao Dich", "Ghi Chu"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(50, 50, 50)); 
        table.getTableHeader().setForeground(COLOR_TEXT); 
        
        table.setBackground(COLOR_PANEL_BG); 
        table.setForeground(COLOR_TEXT); 
        table.setShowGrid(false); 
        table.setIntercellSpacing(new Dimension(0, 5)); 

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new LineBorder(new Color(60, 60, 60))); 
        panelQuanLy.add(scrollPane, BorderLayout.CENTER);

        // 3. Nút XÓA bo góc
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setBackground(COLOR_BG);
        
        JButton btnDelete = new JButton("XOA DONG CHON");
        btnDelete.setBackground(COLOR_ACCENT_RED); 
        btnDelete.setForeground(Color.WHITE); 
        btnDelete.setFont(new Font("Segoe UI", Font.BOLD, 14));
     btnDelete.putClientProperty(FlatClientProperties.STYLE, "" +
                "arc: 10; " + 
                "borderWidth: 0; " +
                "hoverBackground: #e86558; " + // Màu đỏ nhạt hơn khi lướt chuột
                "pressedBackground: #ff7669;"); // Màu đỏ sáng nhất khi bấm chuột
        actionPanel.add(btnDelete);
        
        panelQuanLy.add(actionPanel, BorderLayout.SOUTH);

        // --- TAB 2: THỐNG KÊ BIỂU ĐỒ ---
        JPanel panelThongKe = new JPanel(new BorderLayout());
        chartContainer = new JPanel(new BorderLayout()); 
        chartContainer.setBackground(COLOR_BG); 
        
        JButton btnRefreshChart = new JButton("Lam moi bieu do");
        btnRefreshChart.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnRefreshChart.putClientProperty(FlatClientProperties.STYLE, "arc: 5;");
        btnRefreshChart.addActionListener(e -> updateChart()); 

        panelThongKe.add(btnRefreshChart, BorderLayout.NORTH);
        panelThongKe.add(chartContainer, BorderLayout.CENTER);

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
                    // Hiện thông báo bằng 'null' để chống đơ máy
                    JOptionPane.showMessageDialog(null, "Luu thanh cong!", "Thong bao", JOptionPane.INFORMATION_MESSAGE);
                    txtAmount.setText(""); txtCategory.setText(""); txtNote.setText("");
                    
                    loadDataToTable(); 
                    updateChart(); 
                } else {
                    JOptionPane.showMessageDialog(null, "Loi he thong, khong the luu!", "Loi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "O So tien chi duoc phep nhap so!", "Loi nhap lieu", JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(), "Loi nhap lieu", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                int confirm = JOptionPane.showConfirmDialog(null, "Ban co chac muon xoa?", "Xac nhan", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    int id = (int) tableModel.getValueAt(row, 0);
                    if (controller.deleteTransaction(id)) {
                        loadDataToTable();
                        updateChart();
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null, "Vui long click chon 1 dong tren bang de xoa!");
            }
        });
    }

    public void loadDataToTable() {
        tableModel.setRowCount(0);
        List<Transaction> list = controller.getAllTransactions();
        for (Transaction t : list) {
            tableModel.addRow(new Object[]{
                t.getId(), String.format("%,.0f VND", t.getAmount()), t.getCategory(), t.getDate(), t.getNote()
            });
        }
    }

    // ================= HÀM VẼ BIỂU ĐỒ DONUT =================
    private void updateChart() {
        chartContainer.removeAll();
        
        Map<String, Double> categoryData = controller.getExpenseSummaryByCategory();

        if (categoryData.isEmpty()) {
            chartContainer.revalidate();
            chartContainer.repaint();
            return;
        }

        DefaultPieDataset dataset = new DefaultPieDataset();
        for (Map.Entry<String, Double> entry : categoryData.entrySet()) {
            dataset.setValue(entry.getKey(), entry.getValue());
        }

        JFreeChart chart = ChartFactory.createRingChart(
                "PHAN TICH CHI TIEU", dataset, false, true, false); 

        RingPlot plot = (RingPlot) chart.getPlot();
        plot.setSectionDepth(0.30); 
        
        // Màu nền: Đen sâu
        chart.setBackgroundPaint(COLOR_BG); 
        plot.setBackgroundPaint(COLOR_BG);
        plot.setOutlineVisible(false); 
        plot.setShadowPaint(null);     
        
        // Tạo khe hở đen mượt giữa các múi màu
        plot.setSeparatorPaint(COLOR_BG); 
        plot.setSeparatorStroke(new BasicStroke(5.0f)); 
        
        // Cài đặt màu cho các danh mục quen thuộc (Bạn nhập đúng chữ thì sẽ ra màu đẹp)
        plot.setSectionPaint("an uong", Color.decode("#FFB142")); // Vàng cam
        plot.setSectionPaint("mua sam", Color.decode("#33D9B2")); // Xanh ngọc
        plot.setSectionPaint("giai tri", Color.decode("#FF5252")); // Đỏ nhạt
        plot.setSectionPaint("hoa don", Color.decode("#34ACE0")); // Xanh dương
        plot.setSectionPaint("di lai", Color.decode("#D980FA"));  // Tím nhạt
        
        // Chữ màu trắng sáng
        plot.setLabelFont(new Font("Segoe UI", Font.BOLD, 13));
        plot.setLabelPaint(COLOR_TEXT); 
        plot.setLabelBackgroundPaint(COLOR_BG); 
        plot.setLabelOutlinePaint(null); 
        plot.setLabelShadowPaint(null);  
        plot.setLabelLinkPaint(new Color(150, 150, 150)); 

        plot.setLabelGenerator(new StandardPieSectionLabelGenerator(
                "{0}: {2}", NumberFormat.getNumberInstance(), NumberFormat.getPercentInstance()
        ));

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setBackground(COLOR_BG); 
        chartContainer.add(chartPanel, BorderLayout.CENTER);
        chartContainer.revalidate();
        chartContainer.repaint();
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception e) {}
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}