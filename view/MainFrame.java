package view;

import controller.TransactionController;
import model.Transaction;
import utils.InvalidInputException;

import java.awt.*;
import java.text.NumberFormat;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

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
    private JTextField txtAmount, txtCategory, txtNote, txtSearch, txtIncome;
    private JTable table;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> rowSorter; 
    private JLabel lblTotalAmount, lblTotalCount, lblWarningStatus; 
    private JTextArea txtBudgetAdvice, txtFutureForecast;
    
    private TransactionController controller;
    private JPanel chartContainer; 
    private Timer chartTimer;
    private double currentTotalExpense = 0; // Lưu tổng chi tiêu hiện tại

    private final Color COLOR_BG = new Color(18, 18, 18); 
    private final Color COLOR_PANEL_BG = new Color(30, 30, 30); 
    private final Color COLOR_TEXT = new Color(240, 240, 240); 
    private final Color COLOR_ACCENT_GREEN = new Color(46, 204, 113); 
    private final Color COLOR_ACCENT_RED = new Color(231, 76, 60); 
    private final Color COLOR_ACCENT_BLUE = new Color(52, 172, 224);

    public MainFrame() {
        controller = new TransactionController();
        try { UIManager.setLookAndFeel(new FlatDarkLaf()); } catch (Exception ex) {}
        initUI();
        loadDataToTable();
        updateChart(); 
    }

    private void initUI() {
        setTitle("Quan Ly Tai Chinh Pro - Nhom Vinh, Hien, Y");
        setSize(1000, 720);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(COLOR_BG);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));

        // ================= TAB 1: QUẢN LÝ GIAO DỊCH =================
        JPanel panelQuanLy = new JPanel(new BorderLayout(15, 15));
        panelQuanLy.setBorder(new EmptyBorder(15, 15, 15, 15));
        panelQuanLy.setBackground(COLOR_BG); 

        JPanel topFeaturePanel = new JPanel(new BorderLayout(10, 10));
        topFeaturePanel.setBackground(COLOR_BG);
        
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        statsPanel.setBackground(COLOR_PANEL_BG);
        statsPanel.putClientProperty(FlatClientProperties.STYLE, "arc: 10;");
        
        lblTotalCount = new JLabel("Giao dịch: 0"); 
        lblTotalCount.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTotalCount.setForeground(new Color(170, 170, 170));
        
        lblTotalAmount = new JLabel("Tổng tiền: 0 VND");
        lblTotalAmount.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTotalAmount.setForeground(COLOR_ACCENT_GREEN);
        
        statsPanel.add(lblTotalCount); statsPanel.add(new JLabel(" | ")); statsPanel.add(lblTotalAmount);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setBackground(COLOR_BG);
        searchPanel.add(createLabel("🔍 Tìm kiếm:", new Font("Segoe UI", Font.BOLD, 13)));
        txtSearch = new JTextField(15);
        txtSearch.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nhập danh mục, ghi chú...");
        searchPanel.add(txtSearch);

        topFeaturePanel.add(statsPanel, BorderLayout.WEST);
        topFeaturePanel.add(searchPanel, BorderLayout.EAST);

        JPanel inputPanel = new JPanel(new GridLayout(2, 4, 15, 15));
        inputPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), " Nhap Giao Dich Moi ", 0, 0, new Font("Segoe UI", Font.BOLD, 15), COLOR_TEXT));
        inputPanel.setBackground(COLOR_PANEL_BG);

        Font labelFont = new Font("Segoe UI", Font.BOLD, 13);
        inputPanel.add(createLabel(" So tien (VND):", labelFont)); txtAmount = new JTextField(); inputPanel.add(txtAmount);
        inputPanel.add(createLabel(" Danh muc:", labelFont)); txtCategory = new JTextField(); inputPanel.add(txtCategory);
        inputPanel.add(createLabel(" Ghi chu:", labelFont)); txtNote = new JTextField(); inputPanel.add(txtNote);

        JButton btnAdd = new JButton("LUU GIAO DICH");
        btnAdd.setBackground(COLOR_ACCENT_GREEN); btnAdd.setForeground(Color.BLACK); btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAdd.putClientProperty(FlatClientProperties.STYLE, "arc: 10; borderWidth: 0; hoverBackground: #27ae60; pressedBackground: #1e8449;");
        inputPanel.add(btnAdd);
        
        JPanel headerPanel = new JPanel(new BorderLayout(0, 15));
        headerPanel.setBackground(COLOR_BG);
        headerPanel.add(topFeaturePanel, BorderLayout.NORTH); headerPanel.add(inputPanel, BorderLayout.CENTER);
        panelQuanLy.add(headerPanel, BorderLayout.NORTH);

        String[] columns = {"ID", "So Tien", "Danh Muc", "Ngay Giao Dich", "Ghi Chu"};
        tableModel = new DefaultTableModel(columns, 0); table = new JTable(tableModel);
        rowSorter = new TableRowSorter<>(tableModel); table.setRowSorter(rowSorter);

        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { search(txtSearch.getText()); }
            public void removeUpdate(DocumentEvent e) { search(txtSearch.getText()); }
            public void changedUpdate(DocumentEvent e) { search(txtSearch.getText()); }
            public void search(String str) {
                if (str.trim().length() == 0) rowSorter.setRowFilter(null); 
                else rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + str));
            }
        });

        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                if (isSelected) c.setBackground(new Color(60, 80, 100)); else c.setBackground(COLOR_PANEL_BG); 
                return c;
            }
        });

        table.setRowHeight(28); table.setBackground(COLOR_PANEL_BG); table.setForeground(COLOR_TEXT); table.setShowGrid(false); 
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new LineBorder(new Color(60, 60, 60))); 
        panelQuanLy.add(scrollPane, BorderLayout.CENTER);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setBackground(COLOR_BG);
        JButton btnDelete = new JButton("XOA DONG CHON");
        btnDelete.setBackground(COLOR_ACCENT_RED); btnDelete.setForeground(Color.WHITE); btnDelete.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnDelete.putClientProperty(FlatClientProperties.STYLE, "arc: 10; borderWidth: 0;");
        actionPanel.add(btnDelete); panelQuanLy.add(actionPanel, BorderLayout.SOUTH);

        // ================= TAB 2: THỐNG KÊ BIỂU ĐỒ =================
        JPanel panelThongKe = new JPanel(new BorderLayout());
        chartContainer = new JPanel(new BorderLayout()); chartContainer.setBackground(COLOR_BG); 
        JButton btnRefreshChart = new JButton("Lam moi bieu do");
        btnRefreshChart.addActionListener(e -> updateChart()); 
        panelThongKe.add(btnRefreshChart, BorderLayout.NORTH); panelThongKe.add(chartContainer, BorderLayout.CENTER);

        // ================= TAB 3: HOẠCH ĐỊNH & TIẾT KIỆM (TÍNH NĂNG MỚI) =================
        JPanel panelHoachDinh = new JPanel(new BorderLayout(20, 20));
        panelHoachDinh.setBorder(new EmptyBorder(20, 20, 20, 20));
        panelHoachDinh.setBackground(COLOR_BG);

        // Header: Nhập Thu Nhập
        JPanel incomePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        incomePanel.setBackground(COLOR_PANEL_BG);
        incomePanel.putClientProperty(FlatClientProperties.STYLE, "arc: 15;");
        
        incomePanel.add(createLabel("Nhập Thu Nhập Tháng Này (VND): ", new Font("Segoe UI", Font.BOLD, 16)));
        txtIncome = new JTextField(15);
        txtIncome.setFont(new Font("Segoe UI", Font.BOLD, 16));
        JButton btnAnalyze = new JButton("PHÂN TÍCH & CỐ VẤN");
        btnAnalyze.setBackground(COLOR_ACCENT_BLUE);
        btnAnalyze.setForeground(Color.WHITE);
        btnAnalyze.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAnalyze.putClientProperty(FlatClientProperties.STYLE, "arc: 10; borderWidth: 0;");
        incomePanel.add(txtIncome); incomePanel.add(btnAnalyze);

        // Body: 2 Cột Báo Cáo
        JPanel reportPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        reportPanel.setBackground(COLOR_BG);

        // Cột 1: Đề xuất ngân sách 50/30/20
        txtBudgetAdvice = new JTextArea("Vui lòng nhập thu nhập và bấm Phân Tích...");
        styleTextArea(txtBudgetAdvice);
        JPanel pnlLeft = createTitledPanel("🎯 Đề Xuất Chi Tiêu (Quy tắc 50/30/20)", txtBudgetAdvice);

        // Cột 2: Dự báo tương lai
        txtFutureForecast = new JTextArea("Dự báo tích lũy tài sản trong tương lai...");
        styleTextArea(txtFutureForecast);
        JPanel pnlRight = createTitledPanel("🚀 Dự Báo Tiết Kiệm & Lãi Kép", txtFutureForecast);

        reportPanel.add(pnlLeft); reportPanel.add(pnlRight);

        // Footer: Cảnh báo an toàn
        lblWarningStatus = new JLabel("TRẠNG THÁI: CHƯA XÁC ĐỊNH", SwingConstants.CENTER);
        lblWarningStatus.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblWarningStatus.setForeground(Color.GRAY);
        lblWarningStatus.setBorder(new EmptyBorder(10, 0, 10, 0));

        panelHoachDinh.add(incomePanel, BorderLayout.NORTH);
        panelHoachDinh.add(reportPanel, BorderLayout.CENTER);
        panelHoachDinh.add(lblWarningStatus, BorderLayout.SOUTH);

        // ADD TABS
        tabbedPane.addTab("Quản Lý Thu Chi", panelQuanLy);
        tabbedPane.addTab("Biểu Đồ Thống Kê", panelThongKe);
        tabbedPane.addTab("Hoạch Định & Tiết Kiệm", panelHoachDinh); // Thêm Tab 3
        add(tabbedPane);

        // ================= XỬ LÝ SỰ KIỆN =================
        btnAdd.addActionListener(e -> {
            try {
                if (txtAmount.getText().isEmpty() || txtCategory.getText().isEmpty()) throw new InvalidInputException("Khong duoc de trong!");
                double amount = Double.parseDouble(txtAmount.getText());
                Transaction t = new Transaction(0, amount, txtCategory.getText(), txtNote.getText(), "");
                if (controller.addTransaction(t)) {
                    JOptionPane.showMessageDialog(null, "Luu thanh cong!");
                    txtAmount.setText(""); txtCategory.setText(""); txtNote.setText("");
                    loadDataToTable(); updateChart(); 
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Loi nhap lieu: " + ex.getMessage());
            }
        });

        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1 && JOptionPane.showConfirmDialog(null, "Ban co chac xoa?", "Xac nhan", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                int modelRow = table.convertRowIndexToModel(row);
                if (controller.deleteTransaction((int) tableModel.getValueAt(modelRow, 0))) {
                    loadDataToTable(); updateChart();
                }
            }
        });

        // SỰ KIỆN NÚT PHÂN TÍCH (Tính năng AI Cố vấn)
        btnAnalyze.addActionListener(e -> analyzeFinance());
    }

    private void styleTextArea(JTextArea area) {
        area.setEditable(false);
        area.setBackground(COLOR_PANEL_BG);
        area.setForeground(COLOR_TEXT);
        area.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        area.setMargin(new Insets(15, 15, 15, 15));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
    }

    private JPanel createTitledPanel(String title, JTextArea content) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(COLOR_PANEL_BG);
        p.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(COLOR_ACCENT_BLUE), title, 0, 0, new Font("Segoe UI", Font.BOLD, 15), COLOR_ACCENT_BLUE));
        p.add(new JScrollPane(content), BorderLayout.CENTER);
        return p;
    }

    private JLabel createLabel(String text, Font f) {
        JLabel lbl = new JLabel(text); lbl.setForeground(COLOR_TEXT); lbl.setFont(f); return lbl;
    }

    public void loadDataToTable() {
        tableModel.setRowCount(0);
        currentTotalExpense = 0;
        int count = 0;
        for (Transaction t : controller.getAllTransactions()) {
            tableModel.addRow(new Object[]{ t.getId(), String.format("%,.0f VND", t.getAmount()), t.getCategory(), t.getDate(), t.getNote() });
            currentTotalExpense += t.getAmount();
            count++;
        }
        lblTotalCount.setText("Giao dịch: " + count);
        lblTotalAmount.setText("Tổng tiền: " + String.format("%,.0f VND", currentTotalExpense));
    }

    private void updateChart() {
        Map<String, Double> categoryData = controller.getExpenseSummaryByCategory();
        if (categoryData.isEmpty()) {
            chartContainer.removeAll(); chartContainer.revalidate(); chartContainer.repaint(); return;
        }
        DefaultPieDataset dataset = new DefaultPieDataset();
        for (Map.Entry<String, Double> entry : categoryData.entrySet()) dataset.setValue(entry.getKey(), entry.getValue());

        JFreeChart chart = ChartFactory.createRingChart("PHAN TICH CHI TIEU", dataset, false, true, false); 
        chart.getTitle().setPaint(COLOR_TEXT); chart.getTitle().setFont(new Font("Segoe UI", Font.BOLD, 18));

        RingPlot plot = (RingPlot) chart.getPlot();
        plot.setSectionDepth(0.30); chart.setBackgroundPaint(COLOR_BG); plot.setBackgroundPaint(COLOR_BG);
        plot.setOutlineVisible(false); plot.setShadowPaint(null);     
        plot.setSeparatorPaint(COLOR_BG); plot.setSeparatorStroke(new BasicStroke(5.0f)); 
        
        plot.setSectionPaint("an uong", Color.decode("#FFB142")); 
        plot.setSectionPaint("mua sam", Color.decode("#33D9B2")); 
        plot.setSectionPaint("giai tri", Color.decode("#FF5252")); 
        plot.setSectionPaint("hoa don", Color.decode("#34ACE0")); 
        plot.setSectionPaint("di lai", Color.decode("#D980FA"));  
        
        plot.setLabelFont(new Font("Segoe UI", Font.BOLD, 13));
        plot.setLabelPaint(COLOR_TEXT); plot.setLabelBackgroundPaint(COLOR_BG); plot.setLabelOutlinePaint(null); plot.setLabelShadowPaint(null);  

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setBackground(COLOR_BG); 
        chartContainer.removeAll(); chartContainer.add(chartPanel, BorderLayout.CENTER); chartContainer.revalidate();
        
        if (chartTimer != null && chartTimer.isRunning()) chartTimer.stop();
        final double[] currentAngle = {0.0}; plot.setLabelGenerator(null); 
        chartTimer = new Timer(15, e -> { 
            currentAngle[0] += 5.0; plot.setStartAngle(currentAngle[0]); chartPanel.repaint();
            if (currentAngle[0] >= 360.0) {
                ((Timer) e.getSource()).stop();
                plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}: {2}", NumberFormat.getNumberInstance(), NumberFormat.getPercentInstance()));
                chartPanel.repaint(); 
            }
        });
        chartTimer.start(); 
    }
    // ================= LOGIC PHÂN TÍCH TÀI CHÍNH THÔNG MINH =================
   // ================= LOGIC PHÂN TÍCH TÀI CHÍNH THÔNG MINH =================
    private void analyzeFinance() {
        try {
            double income = Double.parseDouble(txtIncome.getText());
            if (income <= 0) throw new Exception();

            // 1. Quy tắc 50/30/20
            double needs = income * 0.50;  // Thiết yếu
            double wants = income * 0.30;  // Cá nhân
            double savings = income * 0.20; // Tiết kiệm

            String advice = String.format(
                "Để tài chính khỏe mạnh, hệ thống đề xuất chia %,.0f VND như sau:\n\n" +
                "[!] 50%% Nhu cầu thiết yếu (Ăn uống, Hóa đơn, Thuê nhà):\n >> %,.0f VND\n\n" +
                "[*] 30%% Chi tiêu cá nhân (Mua sắm, Giải trí, Bạn bè):\n >> %,.0f VND\n\n" +
                "[+] 20%% Tiết kiệm & Đầu tư (Quỹ dự phòng, Gửi tiết kiệm):\n >> %,.0f VND\n\n" +
                "=> Mẹo: Hãy trích 20%% tiết kiệm ngay khi nhận lương trước khi chi tiêu!", 
                income, needs, wants, savings
            );
            txtBudgetAdvice.setText(advice);

            // 2. Dự báo tương lai (Giả sử lãi suất 6%/năm)
            double savings1Year = (savings * 12) * 1.06;
            double savings3Years = (savings * 12 * 3) * 1.15; // Lãi kép ước tính
            double savings5Years = (savings * 12 * 5) * 1.25;

            String forecast = String.format(
                "Nếu bạn duy trì kỷ luật tiết kiệm 20%% (%,.0f VND/tháng) và đem gửi tiết kiệm (lãi suất ước tính 6%%/năm):\n\n" +
                "- Sau 1 năm, bạn sẽ có:\n >> %,.0f VND\n\n" +
                "- Sau 3 năm, bạn sẽ có:\n >> %,.0f VND\n\n" +
                "- Sau 5 năm, bạn sẽ có một gia tài nhỏ:\n >> %,.0f VND\n\n" +
                "=> Lãi kép chính là kỳ quan thứ 8 của thế giới. Hãy bắt đầu ngay hôm nay!", 
                savings, savings1Year, savings3Years, savings5Years
            );
            txtFutureForecast.setText(forecast);

            // 3. Cảnh báo thông minh
            if (currentTotalExpense >= income) {
                lblWarningStatus.setText("[!] BÁO ĐỘNG ĐỎ: BẠN ĐANG CHI TIÊU ÂM TIỀN (Vượt quá thu nhập)!");
                lblWarningStatus.setForeground(COLOR_ACCENT_RED);
            } else if (currentTotalExpense >= income * 0.8) {
                lblWarningStatus.setText("(!) CẢNH BÁO VÀNG: Đã tiêu hơn 80% thu nhập. Hãy cẩn thận!");
                lblWarningStatus.setForeground(Color.YELLOW);
            } else {
                lblWarningStatus.setText("[v] AN TOÀN: Tình hình thu chi của bạn đang nằm trong tầm kiểm soát.");
                lblWarningStatus.setForeground(COLOR_ACCENT_GREEN);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đúng số tiền thu nhập (chỉ nhập số)!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}