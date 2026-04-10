package view;

import controller.TransactionController;
import model.Transaction;
import java.awt.*;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import javax.swing.Timer;
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
    private JComboBox<String> cbType;
    private JTable table;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> rowSorter; 
    private JLabel lblTotalAmount, lblTotalCount, lblWarningStatus; 
    private JTextArea txtBudgetAdvice, txtFutureForecast;
    
    private TransactionController controller;
    private JPanel chartContainer; 
    private Timer chartTimer;
    private double currentTotalExpense = 0; 
    private double currentTotalIncome = 0;

    // 👉 [PHẦN CỦA HIỀN]: Khai báo các mã màu Custom cho giao diện Dark Theme
    private final Color COLOR_BG = new Color(18, 18, 18); 
    private final Color COLOR_PANEL_BG = new Color(30, 30, 30); 
    private final Color COLOR_TEXT = new Color(240, 240, 240); 
    private final Color COLOR_ACCENT_GREEN = new Color(46, 204, 113); 
    private final Color COLOR_ACCENT_RED = new Color(231, 76, 60); 
    private final Color COLOR_ACCENT_BLUE = new Color(52, 172, 224);

    public MainFrame() {
        // 👉 [PHẦN CỦA VINH]: Khởi tạo Controller để kết nối Database
        controller = new TransactionController();
        
        // 👉 [PHẦN CỦA HIỀN]: Áp dụng giao diện FlatDarkLaf trước khi load UI
        try { UIManager.setLookAndFeel(new FlatDarkLaf()); } catch (Exception ex) {}
        
        initUI();
        loadDataToTable();
        updateChart(); 
    }

    private void initUI() {
        setTitle("Quan Ly Tai Chinh Pro - Nhom Vinh, Hien, Y");
        setSize(1050, 720);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(COLOR_BG);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));

        // 👉 [PHẦN CỦA HIỀN]: Thiết kế Layout Tab 1 (Quản lý giao dịch), bo góc các Panel
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
        
        lblTotalAmount = new JLabel("Tổng Thu: 0 | Tổng Chi: 0");
        lblTotalAmount.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTotalAmount.setForeground(COLOR_ACCENT_GREEN);
        
        statsPanel.add(lblTotalCount); statsPanel.add(new JLabel(" | ")); statsPanel.add(lblTotalAmount);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setBackground(COLOR_BG);
        searchPanel.add(createLabel("Tìm kiếm:", new Font("Segoe UI", Font.BOLD, 13)));
        txtSearch = new JTextField(15);
        txtSearch.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nhập danh mục, ghi chú...");
        searchPanel.add(txtSearch);

        topFeaturePanel.add(statsPanel, BorderLayout.WEST);
        topFeaturePanel.add(searchPanel, BorderLayout.EAST);

        // 👉 [PHẦN CỦA HIỀN]: Layout Form nhập liệu chuẩn GridLayout
        JPanel inputPanel = new JPanel(new GridLayout(2, 5, 10, 5)); 
        inputPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), " Nhập Giao Dịch Mới ", 0, 0, new Font("Segoe UI", Font.BOLD, 15), COLOR_TEXT));
        inputPanel.setBackground(COLOR_PANEL_BG);

        Font labelFont = new Font("Segoe UI", Font.BOLD, 13);
        
        inputPanel.add(createLabel(" Loại:", labelFont)); 
        inputPanel.add(createLabel(" Số tiền (VND):", labelFont)); 
        inputPanel.add(createLabel(" Danh mục:", labelFont)); 
        inputPanel.add(createLabel(" Ghi chú:", labelFont)); 
        inputPanel.add(new JLabel("")); 

        cbType = new JComboBox<>(new String[]{"Khoản Chi", "Khoản Thu"}); inputPanel.add(cbType);
        txtAmount = new JTextField(); inputPanel.add(txtAmount);
        txtCategory = new JTextField(); inputPanel.add(txtCategory);
        txtNote = new JTextField(); inputPanel.add(txtNote);

        JButton btnAdd = new JButton("LƯU");
        btnAdd.setBackground(COLOR_ACCENT_GREEN); btnAdd.setForeground(Color.BLACK); btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 14));
        //  [PHẦN CỦA HIỀN]: Custom CSS Hover cho nút bấm
        btnAdd.putClientProperty(FlatClientProperties.STYLE, "arc: 10; borderWidth: 0; hoverBackground: #27ae60; pressedBackground: #1e8449;");
        inputPanel.add(btnAdd);
        
        JPanel headerPanel = new JPanel(new BorderLayout(0, 15));
        headerPanel.setBackground(COLOR_BG);
        headerPanel.add(topFeaturePanel, BorderLayout.NORTH); headerPanel.add(inputPanel, BorderLayout.CENTER);
        panelQuanLy.add(headerPanel, BorderLayout.NORTH);

        String[] columns = {"ID", "So Tien", "Danh Muc", "Ngay Giao Dich", "Ghi Chu"};
        tableModel = new DefaultTableModel(columns, 0); table = new JTable(tableModel);
        
        //  [PHẦN CỦA Ý]: Cấu hình RowSorter để làm tính năng Live Search
        rowSorter = new TableRowSorter<>(tableModel); table.setRowSorter(rowSorter);

        //  [PHẦN CỦA Ý]: Xử lý sự kiện gõ phím để tìm kiếm tức thì (Live Search)
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { search(txtSearch.getText()); }
            public void removeUpdate(DocumentEvent e) { search(txtSearch.getText()); }
            public void changedUpdate(DocumentEvent e) { search(txtSearch.getText()); }
            public void search(String str) {
                if (str.trim().length() == 0) rowSorter.setRowFilter(null); 
                else rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + str)); // Regex tìm không phân biệt hoa thường
            }
        });

        //  [PHẦN CỦA HIỀN]: Tùy chỉnh màu sắc bảng (JTable) và hiệu ứng Hover khi chọn dòng
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
        btnDelete.putClientProperty(FlatClientProperties.STYLE, "arc: 10; borderWidth: 0; hoverBackground: #e86558;");
        actionPanel.add(btnDelete); panelQuanLy.add(actionPanel, BorderLayout.SOUTH);

        // [PHẦN CỦA Ý & HIỀN]: Layout Tab 2 (Biểu đồ)
        // ================= TAB 2: THỐNG KÊ BIỂU ĐỒ =================
        JPanel panelThongKe = new JPanel(new BorderLayout());
        panelThongKe.setBackground(COLOR_BG);
        
        chartContainer = new JPanel(new BorderLayout()); 
        chartContainer.setBackground(COLOR_BG); 
        
        JPanel bottomChartPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 15));
        bottomChartPanel.setBackground(COLOR_BG);
        
        JButton btnRefreshChart = new JButton("LÀM MỚI BIỂU ĐỒ");
        btnRefreshChart.setBackground(new Color(60, 60, 60)); 
        btnRefreshChart.setForeground(Color.WHITE);
        btnRefreshChart.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnRefreshChart.setPreferredSize(new Dimension(180, 35));
        btnRefreshChart.putClientProperty(FlatClientProperties.STYLE, "arc: 15; borderWidth: 0; hoverBackground: #4d4d4d;");
        btnRefreshChart.addActionListener(e -> updateChart()); 
        
        bottomChartPanel.add(btnRefreshChart);
        
        panelThongKe.add(chartContainer, BorderLayout.CENTER);
        panelThongKe.add(bottomChartPanel, BorderLayout.SOUTH); 

        // [PHẦN CỦA HIỀN]: Thiết kế Layout Tab 3 Cố Vấn AI
        // ================= TAB 3: HOẠCH ĐỊNH & TIẾT KIỆM =================
        JPanel panelHoachDinh = new JPanel(new BorderLayout(20, 20));
        panelHoachDinh.setBorder(new EmptyBorder(20, 20, 20, 20));
        panelHoachDinh.setBackground(COLOR_BG);

        JPanel incomePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        incomePanel.setBackground(COLOR_PANEL_BG);
        incomePanel.putClientProperty(FlatClientProperties.STYLE, "arc: 15;");
        
        incomePanel.add(createLabel("Thu Nhập Hệ Thống Ghi Nhận (VND): ", new Font("Segoe UI", Font.BOLD, 16)));
        txtIncome = new JTextField(15);
        txtIncome.setFont(new Font("Segoe UI", Font.BOLD, 16));
        txtIncome.setEditable(false); 
        txtIncome.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Bấm Phân tích để lấy data");
        
        JButton btnAnalyze = new JButton("PHÂN TÍCH DATA & CỐ VẤN");
        btnAnalyze.setBackground(COLOR_ACCENT_BLUE);
        btnAnalyze.setForeground(Color.WHITE);
        btnAnalyze.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAnalyze.putClientProperty(FlatClientProperties.STYLE, "arc: 10; borderWidth: 0; hoverBackground: #2980b9;");
        incomePanel.add(txtIncome); incomePanel.add(btnAnalyze);

        JPanel reportPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        reportPanel.setBackground(COLOR_BG);

        txtBudgetAdvice = new JTextArea("Vui lòng ấn Phân Tích để hệ thống đồng bộ dữ liệu...");
        styleTextArea(txtBudgetAdvice);
        JPanel pnlLeft = createTitledPanel("Đối Chiếu Chi Tiêu Thực Tế", txtBudgetAdvice);

        txtFutureForecast = new JTextArea("Dự báo tích lũy tài sản trong tương lai...");
        styleTextArea(txtFutureForecast);
        JPanel pnlRight = createTitledPanel("Dự Báo Tiết Kiệm & Lãi Kép", txtFutureForecast);

        reportPanel.add(pnlLeft); reportPanel.add(pnlRight);

        lblWarningStatus = new JLabel("TRẠNG THÁI: CHƯA ĐỒNG BỘ", SwingConstants.CENTER);
        lblWarningStatus.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblWarningStatus.setForeground(Color.GRAY);
        lblWarningStatus.setBorder(new EmptyBorder(10, 0, 10, 0));

        panelHoachDinh.add(incomePanel, BorderLayout.NORTH);
        panelHoachDinh.add(reportPanel, BorderLayout.CENTER);
        panelHoachDinh.add(lblWarningStatus, BorderLayout.SOUTH);

        tabbedPane.addTab("Quản Lý Thu Chi", panelQuanLy);
        tabbedPane.addTab("Biểu Đồ Thống Kê", panelThongKe);
        tabbedPane.addTab("Hoạch Định & Tiết Kiệm", panelHoachDinh);
        add(tabbedPane);

        //  [PHẦN CỦA VINH]: Bắt sự kiện LƯU và XÓA (Gọi xuống Database)
        // ================= XỬ LÝ SỰ KIỆN =================
        btnAdd.addActionListener(e -> {
            try {
                if (txtAmount.getText().isEmpty() || txtCategory.getText().isEmpty()) throw new Exception("Không được để trống!");
                double amount = Double.parseDouble(txtAmount.getText().replace(",", "").trim());
                
                String typePrefix = cbType.getSelectedItem().equals("Khoản Thu") ? "[THU] " : "[CHI] ";
                String finalCategory = typePrefix + txtCategory.getText().trim();

                Transaction t = new Transaction(0, amount, finalCategory, txtNote.getText(), "");
                if (controller.addTransaction(t)) {
                    JOptionPane.showMessageDialog(null, "Lưu thành công!");
                    txtAmount.setText(""); txtCategory.setText(""); txtNote.setText("");
                    loadDataToTable(); updateChart(); 
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Vui lòng nhập đúng số tiền hợp lệ!", "Lỗi", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1 && JOptionPane.showConfirmDialog(null, "Bạn có chắc xóa?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                int modelRow = table.convertRowIndexToModel(row);
                if (controller.deleteTransaction((int) tableModel.getValueAt(modelRow, 0))) {
                    loadDataToTable(); updateChart();
                }
            }
        });

        btnAnalyze.addActionListener(e -> analyzeFinance());
    }

    //  [PHẦN CỦA HIỀN]: Các hàm tiện ích style cho Text Area và Panel
    // ================= CÁC HÀM TIỆN ÍCH =================
    private void styleTextArea(JTextArea area) {
        area.setEditable(false); area.setBackground(COLOR_PANEL_BG); area.setForeground(COLOR_TEXT);
        area.setFont(new Font("Segoe UI", Font.PLAIN, 14)); area.setMargin(new Insets(15, 15, 15, 15));
        area.setLineWrap(true); area.setWrapStyleWord(true);
    }

    private JPanel createTitledPanel(String title, JTextArea content) {
        JPanel p = new JPanel(new BorderLayout()); p.setBackground(COLOR_PANEL_BG);
        p.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(COLOR_ACCENT_BLUE), title, 0, 0, new Font("Segoe UI", Font.BOLD, 15), COLOR_ACCENT_BLUE));
        p.add(new JScrollPane(content), BorderLayout.CENTER); return p;
    }

    private JLabel createLabel(String text, Font f) {
        JLabel lbl = new JLabel(text); lbl.setForeground(COLOR_TEXT); lbl.setFont(f); return lbl;
    }

    //  [PHẦN CỦA VINH]: Logic AI Nhận diện từ khóa để tự động chia Thu/Chi
    // ================= LOGIC PHÂN BIỆT THU CHI DỰA TRÊN DANH MỤC ================= 
    private boolean isIncomeCategory(String category) {
        String cat = category.toLowerCase();
        if (cat.contains("[thu]")) return true;
        if (cat.contains("luong") || cat.contains("lương")) return true;
        if (cat.contains("thuong") || cat.contains("thưởng")) return true;
        if (cat.contains("phu cap") || cat.contains("phụ cấp")) return true;
        if (cat.contains("lixi") || cat.contains("lì xì") || cat.contains("mung tuoi")) return true;
        return false;
    }

    //  [PHẦN CỦA VINH & HIỀN]: Vinh xử lý logic đếm tiền tổng, Hiền xử lý load lên bảng
    public void loadDataToTable() {
        tableModel.setRowCount(0); currentTotalExpense = 0; currentTotalIncome = 0; int count = 0;
        
        for (Transaction t : controller.getAllTransactions()) {
            tableModel.addRow(new Object[]{ t.getId(), String.format("%,.0f VND", t.getAmount()), t.getCategory(), t.getDate(), t.getNote() });
            count++;
            if (isIncomeCategory(t.getCategory())) {
                currentTotalIncome += t.getAmount();
            } else {
                currentTotalExpense += t.getAmount();
            }
        }
        lblTotalCount.setText("Giao dịch: " + count);
        lblTotalAmount.setText(String.format("Tổng Thu: %,.0f VND | Tổng Chi: %,.0f VND", currentTotalIncome, currentTotalExpense));
        
        if(currentTotalExpense > currentTotalIncome && currentTotalIncome > 0) lblTotalAmount.setForeground(COLOR_ACCENT_RED);
        else lblTotalAmount.setForeground(COLOR_ACCENT_GREEN);
    }

    //  [PHẦN CỦA Ý]: Toàn bộ thuật toán vẽ biểu đồ Donut và Hoạt ảnh Animation xoay
    private void updateChart() {
        Map<String, Double> categoryData = new HashMap<>();
        for (Transaction t : controller.getAllTransactions()) {
            if (!isIncomeCategory(t.getCategory())) {
                String cleanName = t.getCategory().replace("[CHI] ", "").replace("[CHI]", "").trim();
                categoryData.put(cleanName, categoryData.getOrDefault(cleanName, 0.0) + t.getAmount());
            }
        }

        if (categoryData.isEmpty()) { chartContainer.removeAll(); chartContainer.revalidate(); chartContainer.repaint(); return; }
        
        DefaultPieDataset dataset = new DefaultPieDataset();
        for (Map.Entry<String, Double> entry : categoryData.entrySet()) {
            dataset.setValue(entry.getKey(), entry.getValue());
        }

        JFreeChart chart = ChartFactory.createRingChart("PHÂN TÍCH CHI TIÊU", dataset, false, true, false); 
        chart.getTitle().setPaint(COLOR_TEXT); chart.getTitle().setFont(new Font("Segoe UI", Font.BOLD, 18));
        RingPlot plot = (RingPlot) chart.getPlot();
        plot.setSectionDepth(0.30); chart.setBackgroundPaint(COLOR_BG); plot.setBackgroundPaint(COLOR_BG);
        plot.setOutlineVisible(false); plot.setShadowPaint(null);     
        plot.setSeparatorPaint(COLOR_BG); plot.setSeparatorStroke(new BasicStroke(5.0f)); 
        
        plot.setSectionPaint("an uong", Color.decode("#FFB142")); plot.setSectionPaint("mua sam", Color.decode("#33D9B2")); 
        plot.setSectionPaint("giai tri", Color.decode("#FF5252")); plot.setSectionPaint("hoa don", Color.decode("#34ACE0")); 
        plot.setSectionPaint("di lai", Color.decode("#D980FA"));  
        
        plot.setLabelFont(new Font("Segoe UI", Font.BOLD, 13)); plot.setLabelPaint(COLOR_TEXT); 
        plot.setLabelBackgroundPaint(COLOR_BG); plot.setLabelOutlinePaint(null); plot.setLabelShadowPaint(null);  

        ChartPanel chartPanel = new ChartPanel(chart); chartPanel.setBackground(COLOR_BG); 
        chartContainer.removeAll(); chartContainer.add(chartPanel, BorderLayout.CENTER); chartContainer.revalidate();
        
        //  [PHẦN CỦA Ý]: Code xử lý Timer tạo hiệu ứng biểu đồ xoay 360 độ
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

    // [PHẦN CỦA VINH]: Toàn bộ thuật toán AI phân bổ ngân sách 50/30/20 và Lãi kép
    // ================= LOGIC PHÂN TÍCH TÀI CHÍNH THÔNG MINH AUTO =================
    private void analyzeFinance() {
        try {
            double income = 0; double actualNeeds = 0; double actualWants = 0;
            
            for (Transaction t : controller.getAllTransactions()) {
                String cat = t.getCategory().toLowerCase();
                double amount = t.getAmount();

                if (isIncomeCategory(cat)) {
                    income += amount; 
                } else {
                    if (cat.contains("an uong") || cat.contains("ăn uống") || cat.contains("hoa don") || cat.contains("hóa đơn") || cat.contains("di lai") || cat.contains("đi lại") || cat.contains("thue") || cat.contains("thuê") || cat.contains("tien nha") || cat.contains("tiền nhà")) {
                        actualNeeds += amount;
                    } else {
                        actualWants += amount; 
                    }
                }
            }

            txtIncome.setText(String.format("%,.0f", income));

            if (income <= 0) {
                JOptionPane.showMessageDialog(this, "Hệ thống chưa tìm thấy dữ liệu Thu Nhập!\nVui lòng sang Tab 'Quản Lý Thu Chi' thêm các 'Khoản Thu' trước.", "Chưa có dữ liệu", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            double targetNeeds = income * 0.50; double targetWants = income * 0.30; double targetSavings = income * 0.20;

            StringBuilder advice = new StringBuilder();
            advice.append(String.format("Đã quét Database. Đối chiếu Thu nhập %,.0f VND với Chi tiêu:\n\n", income));

            advice.append(String.format("[!] 50%% THIẾT YẾU (Ngân sách chuẩn: %,.0f VND)\n", targetNeeds));
            advice.append(String.format(" >> Thực tế đã chi: %,.0f VND (Chiếm %.1f%%)\n", actualNeeds, (actualNeeds/income)*100));
            if (actualNeeds > targetNeeds) advice.append(" ⚠️ BÁO ĐỘNG: Tiền sinh hoạt đang lố. Cần siết chặt lại!\n\n");
            else advice.append(" [v] TỐT: Chi tiêu sinh hoạt trong mức an toàn.\n\n");

            advice.append(String.format("[*] 30%% CÁ NHÂN (Ngân sách chuẩn: %,.0f VND)\n", targetWants));
            advice.append(String.format(" >> Thực tế đã chi: %,.0f VND (Chiếm %.1f%%)\n", actualWants, (actualWants/income)*100));
            if (actualWants > targetWants) advice.append(" ⚠️ BÁO ĐỘNG: Đang mua sắm, giải trí quá tay!\n\n");
            else advice.append(" [v] TỐT: Kiểm soát nhu cầu cá nhân rất tốt.\n\n");

            double actualSaved = income - (actualNeeds + actualWants);
            advice.append(String.format("[+] 20%% TIẾT KIỆM (Mục tiêu: %,.0f VND)\n", targetSavings));
            if (actualSaved >= targetSavings) advice.append(String.format(" >> Tiền dư hiện tại: %,.0f VND. Xuất sắc!\n", actualSaved));
            else if (actualSaved > 0) advice.append(String.format(" >> Tiền dư hiện tại: %,.0f VND. Hãy cố gắng tiết kiệm thêm!\n", actualSaved));
            else advice.append(" Bạn đã tiêu âm vào cả tiền tiết kiệm của tương lai.\n");

            txtBudgetAdvice.setText(advice.toString());

            double monthlySavings = (actualSaved > 0) ? actualSaved : targetSavings; 
            double savings1Year = (monthlySavings * 12) * 1.06;
            double savings3Years = (monthlySavings * 12 * 3) * 1.15;
            double savings5Years = (monthlySavings * 12 * 5) * 1.25;

            String forecast = String.format(
                "Dựa trên số dư hiện tại (%,.0f VND/tháng) và đem tích lũy (lãi 6%%/năm):\n\n" +
                "- Sau 1 năm, tài sản của bạn là:\n >> %,.0f VND\n\n" +
                "- Sau 3 năm, bạn sẽ có:\n >> %,.0f VND\n\n" +
                "- Sau 5 năm, lãi kép biến nó thành:\n >> %,.0f VND\n\n" +
                "=> Hãy duy trì thu chi hợp lý để sớm tự do tài chính!", 
                monthlySavings, savings1Year, savings3Years, savings5Years
            );
            txtFutureForecast.setText(forecast);

            if (currentTotalExpense >= income) {
                lblWarningStatus.setText("[!] BÁO ĐỘNG ĐỎ: ĐÃ TIÊU ÂM TIỀN LƯƠNG!");
                lblWarningStatus.setForeground(COLOR_ACCENT_RED);
            } else if (currentTotalExpense >= income * 0.8) {
                lblWarningStatus.setText("(!) CẢNH BÁO VÀNG: Tổng chi đã lố 80% thu nhập! Siết chặt ví lại!");
                lblWarningStatus.setForeground(Color.YELLOW);
            } else {
                lblWarningStatus.setText("[v] AN TOÀN: Kỷ luật tài chính của bạn đang rất tuyệt vời!");
                lblWarningStatus.setForeground(COLOR_ACCENT_GREEN);
            }

        } catch (Exception ex) {}
    }
}