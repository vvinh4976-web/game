package view;

import controller.TransactionController;
import model.Transaction;
import java.awt.*;
import java.text.NumberFormat;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter; // Thư viện cho tính năng Tìm kiếm

// Thư viện giao diện FlatLaf
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatDarkLaf;

// Thư viện biểu đồ JFreeChart
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.RingPlot;
import org.jfree.data.general.DefaultPieDataset;

public class MainFrame extends JFrame {
    private JTextField txtAmount, txtCategory, txtNote, txtSearch;
    private JTable table;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> rowSorter; // Bộ lọc tìm kiếm
    private JLabel lblTotalAmount, lblTotalCount; // Nhãn thống kê
    
    private TransactionController controller;
    private JPanel chartContainer; 
    private Timer chartTimer;

    private final Color COLOR_BG = new Color(18, 18, 18); 
    private final Color COLOR_PANEL_BG = new Color(30, 30, 30); 
    private final Color COLOR_TEXT = new Color(240, 240, 240); 
    private final Color COLOR_ACCENT_GREEN = new Color(46, 204, 113); 
    private final Color COLOR_ACCENT_RED = new Color(231, 76, 60); 

    public MainFrame() {
        controller = new TransactionController();
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
        setTitle("Quan Ly Tai Chinh Pro - Nhom Vinh, Hien, Y");
        setSize(1000, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(COLOR_BG);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));

        // ================= TAB 1: QUẢN LÝ GIAO DỊCH =================
        JPanel panelQuanLy = new JPanel(new BorderLayout(15, 15));
        panelQuanLy.setBorder(new EmptyBorder(15, 15, 15, 15));
        panelQuanLy.setBackground(COLOR_BG); 

        // --- PHẦN TÍNH NĂNG MỚI 1: THỐNG KÊ & TÌM KIẾM ---
        JPanel topFeaturePanel = new JPanel(new BorderLayout(10, 10));
        topFeaturePanel.setBackground(COLOR_BG);
        
        // Thẻ Thống kê
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        statsPanel.setBackground(COLOR_PANEL_BG);
        statsPanel.putClientProperty(FlatClientProperties.STYLE, "arc: 10;");
        
        lblTotalCount = new JLabel("Giao dịch: 0"); 
        lblTotalCount.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTotalCount.setForeground(new Color(170, 170, 170));
        
        lblTotalAmount = new JLabel("Tổng tiền: 0 VND");
        lblTotalAmount.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTotalAmount.setForeground(COLOR_ACCENT_GREEN);
        
        statsPanel.add(lblTotalCount);
        statsPanel.add(new JLabel(" | "));
        statsPanel.add(lblTotalAmount);

        // Thanh tìm kiếm
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setBackground(COLOR_BG);
        searchPanel.add(createLabel("🔍 Tìm kiếm:", new Font("Segoe UI", Font.BOLD, 13)));
        txtSearch = new JTextField(15);
        txtSearch.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nhập danh mục, ghi chú...");
        searchPanel.add(txtSearch);

        topFeaturePanel.add(statsPanel, BorderLayout.WEST);
        topFeaturePanel.add(searchPanel, BorderLayout.EAST);
        // ------------------------------------------------

        // --- Form nhập liệu ---
        JPanel inputPanel = new JPanel(new GridLayout(2, 4, 15, 15));
        inputPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY), " Nhap Giao Dich Moi ", 
                0, 0, new Font("Segoe UI", Font.BOLD, 15), COLOR_TEXT));
        inputPanel.setBackground(COLOR_PANEL_BG);

        Font labelFont = new Font("Segoe UI", Font.BOLD, 13);
        inputPanel.add(createLabel(" So tien (VND):", labelFont));
        txtAmount = new JTextField(); inputPanel.add(txtAmount);
        inputPanel.add(createLabel(" Danh muc:", labelFont));
        txtCategory = new JTextField(); inputPanel.add(txtCategory);
        inputPanel.add(createLabel(" Ghi chu:", labelFont));
        txtNote = new JTextField(); inputPanel.add(txtNote);

        JButton btnAdd = new JButton("LUU GIAO DICH");
        btnAdd.setBackground(COLOR_ACCENT_GREEN); 
        btnAdd.setForeground(Color.BLACK); 
        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAdd.putClientProperty(FlatClientProperties.STYLE, "arc: 10; borderWidth: 0; hoverBackground: #27ae60; pressedBackground: #1e8449;");
        inputPanel.add(btnAdd);
        
        JPanel headerPanel = new JPanel(new BorderLayout(0, 15));
        headerPanel.setBackground(COLOR_BG);
        headerPanel.add(topFeaturePanel, BorderLayout.NORTH);
        headerPanel.add(inputPanel, BorderLayout.CENTER);
        
        panelQuanLy.add(headerPanel, BorderLayout.NORTH);

        // --- Bảng JTable ---
        String[] columns = {"ID", "So Tien", "Danh Muc", "Ngay Giao Dich", "Ghi Chu"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        
        // Tích hợp RowSorter cho tính năng Tìm kiếm
        rowSorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(rowSorter);

        // Tính năng Live Search
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { search(txtSearch.getText()); }
            @Override
            public void removeUpdate(DocumentEvent e) { search(txtSearch.getText()); }
            @Override
            public void changedUpdate(DocumentEvent e) { search(txtSearch.getText()); }
            
            public void search(String str) {
                if (str.trim().length() == 0) { rowSorter.setRowFilter(null); } 
                else { rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + str)); }
            }
        });

        table.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            int lastHoveredRow = -1;
            public void mouseMoved(java.awt.event.MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                if (row != lastHoveredRow) { lastHoveredRow = row; table.repaint(); }
            }
        });
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent e) {
                table.putClientProperty("lastHoveredRow", -1); table.repaint(); 
            }
        });
        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                Point p = table.getMousePosition(); 
                int hoveredRow = (p != null) ? table.rowAtPoint(p) : -1;
                if (isSelected) c.setBackground(new Color(60, 80, 100)); 
                else if (row == hoveredRow) c.setBackground(new Color(50, 50, 50)); 
                else c.setBackground(COLOR_PANEL_BG); 
                return c;
            }
        });

        table.setRowHeight(28);
        table.setBackground(COLOR_PANEL_BG); 
        table.setForeground(COLOR_TEXT); 
        table.setShowGrid(false); 

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new LineBorder(new Color(60, 60, 60))); 
        panelQuanLy.add(scrollPane, BorderLayout.CENTER);

        // --- Nút Xóa ---
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setBackground(COLOR_BG);
        JButton btnDelete = new JButton("XOA DONG CHON");
        btnDelete.setBackground(COLOR_ACCENT_RED); 
        btnDelete.setForeground(Color.WHITE); 
        btnDelete.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnDelete.putClientProperty(FlatClientProperties.STYLE, "arc: 10; borderWidth: 0; hoverBackground: #e86558; pressedBackground: #ff7669;");
        actionPanel.add(btnDelete);
        panelQuanLy.add(actionPanel, BorderLayout.SOUTH);

        // ================= TAB 2: THỐNG KÊ BIỂU ĐỒ =================
        JPanel panelThongKe = new JPanel(new BorderLayout());
        chartContainer = new JPanel(new BorderLayout()); 
        chartContainer.setBackground(COLOR_BG); 
        JButton btnRefreshChart = new JButton("Lam moi bieu do");
        btnRefreshChart.addActionListener(e -> updateChart()); 
        panelThongKe.add(btnRefreshChart, BorderLayout.NORTH);
        panelThongKe.add(chartContainer, BorderLayout.CENTER);

        tabbedPane.addTab("Quan Ly Thu Chi", panelQuanLy);
        tabbedPane.addTab("Bieu Do Thong Ke", panelThongKe);
        add(tabbedPane);

        // ================= XỬ LÝ SỰ KIỆN =================
        btnAdd.addActionListener(e -> {
            try {
                if (txtAmount.getText().isEmpty() || txtCategory.getText().isEmpty()) throw new Exception("Khong duoc de trong!");
                double amount = Double.parseDouble(txtAmount.getText());
                Transaction t = new Transaction(0, amount, txtCategory.getText(), txtNote.getText(), "");
                if (controller.addTransaction(t)) {
                    JOptionPane.showMessageDialog(null, "Luu thanh cong!");
                    txtAmount.setText(""); txtCategory.setText(""); txtNote.setText("");
                    loadDataToTable(); updateChart(); 
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(), "Loi", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                if (JOptionPane.showConfirmDialog(null, "Ban co chac xoa?", "Xac nhan", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    // Cần lấy ID thực tế từ rowSorter để tránh lỗi khi đang tìm kiếm
                    int modelRow = table.convertRowIndexToModel(row);
                    if (controller.deleteTransaction((int) tableModel.getValueAt(modelRow, 0))) {
                        loadDataToTable(); updateChart();
                    }
                }
            } else {
                 JOptionPane.showMessageDialog(null, "Chưa chọn dòng nào để xóa!");
            }
        });
    }

    private JLabel createLabel(String text, Font f) {
        JLabel lbl = new JLabel(text); lbl.setForeground(COLOR_TEXT); lbl.setFont(f); return lbl;
    }

    public void loadDataToTable() {
        tableModel.setRowCount(0);
        double totalAmount = 0;
        int count = 0;
        
        for (Transaction t : controller.getAllTransactions()) {
            tableModel.addRow(new Object[]{ t.getId(), String.format("%,.0f VND", t.getAmount()), t.getCategory(), t.getDate(), t.getNote() });
            totalAmount += t.getAmount();
            count++;
        }
        
        // Cập nhật thẻ thống kê
        lblTotalCount.setText("Giao dịch: " + count);
        lblTotalAmount.setText("Tổng tiền: " + String.format("%,.0f VND", totalAmount));
    }

    private void updateChart() {
        Map<String, Double> categoryData = controller.getExpenseSummaryByCategory();
        if (categoryData.isEmpty()) {
            chartContainer.removeAll(); chartContainer.revalidate(); chartContainer.repaint(); return;
        }
        DefaultPieDataset dataset = new DefaultPieDataset();
        for (Map.Entry<String, Double> entry : categoryData.entrySet()) dataset.setValue(entry.getKey(), entry.getValue());

        JFreeChart chart = ChartFactory.createRingChart("PHAN TICH CHI TIEU", dataset, false, true, false); 
        chart.getTitle().setPaint(COLOR_TEXT);
        chart.getTitle().setFont(new Font("Segoe UI", Font.BOLD, 18));

        RingPlot plot = (RingPlot) chart.getPlot();
        plot.setSectionDepth(0.30); 
        chart.setBackgroundPaint(COLOR_BG); 
        plot.setBackgroundPaint(COLOR_BG);
        plot.setOutlineVisible(false); 
        plot.setShadowPaint(null);     
        plot.setSeparatorPaint(COLOR_BG); 
        plot.setSeparatorStroke(new BasicStroke(5.0f)); 
        
        plot.setSectionPaint("an uong", Color.decode("#FFB142")); 
        plot.setSectionPaint("mua sam", Color.decode("#33D9B2")); 
        plot.setSectionPaint("giai tri", Color.decode("#FF5252")); 
        plot.setSectionPaint("hoa don", Color.decode("#34ACE0")); 
        plot.setSectionPaint("di lai", Color.decode("#D980FA"));  
        
        plot.setLabelFont(new Font("Segoe UI", Font.BOLD, 13));
        plot.setLabelPaint(COLOR_TEXT); 
        plot.setLabelBackgroundPaint(COLOR_BG); 
        plot.setLabelOutlinePaint(null); 
        plot.setLabelShadowPaint(null);  

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setBackground(COLOR_BG); 
        chartContainer.removeAll();
        chartContainer.add(chartPanel, BorderLayout.CENTER);
        chartContainer.revalidate();
        
        if (chartTimer != null && chartTimer.isRunning()) chartTimer.stop();
        final double[] currentAngle = {0.0}; 
        plot.setLabelGenerator(null); 

        chartTimer = new Timer(15, e -> { 
            currentAngle[0] += 5.0; 
            plot.setStartAngle(currentAngle[0]);
            chartPanel.repaint();
            if (currentAngle[0] >= 360.0) {
                ((Timer) e.getSource()).stop();
                plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}: {2}", NumberFormat.getNumberInstance(), NumberFormat.getPercentInstance()));
                chartPanel.repaint(); 
            }
        });
        chartTimer.start(); 
    }
}