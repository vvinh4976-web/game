package view;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import com.formdev.flatlaf.FlatClientProperties;

public class LoginFrame extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;

    public LoginFrame() {
        setTitle("Đăng Nhập - Hệ Thống Quản Lý Tài Chính");
        setSize(400, 360); 
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(20, 30, 20, 30));
        mainPanel.setBackground(new Color(18, 18, 18));

        JLabel lblTitle = new JLabel("ĐĂNG NHẬP", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(new Color(46, 204, 113));
        lblTitle.setBorder(new EmptyBorder(0, 0, 25, 0));
        mainPanel.add(lblTitle, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        formPanel.setBackground(new Color(18, 18, 18));

        JLabel lblUser = new JLabel("Tên đăng nhập:"); lblUser.setForeground(Color.WHITE); lblUser.setFont(new Font("Segoe UI", Font.BOLD, 13));
        txtUsername = new JTextField(); 
        // Đổi chữ gợi ý cho ngầu
        txtUsername.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nhập tài khoản ");

        JLabel lblPass = new JLabel("Mật khẩu:"); lblPass.setForeground(Color.WHITE); lblPass.setFont(new Font("Segoe UI", Font.BOLD, 13));
        txtPassword = new JPasswordField(); 
        txtPassword.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nhập mật khẩu...");

        formPanel.add(lblUser); formPanel.add(txtUsername);
        formPanel.add(lblPass); formPanel.add(txtPassword);
        mainPanel.add(formPanel, BorderLayout.CENTER);

        // --- KHU VỰC NÚT BẤM ---
        JPanel bottomPanel = new JPanel(new GridLayout(2, 1, 0, 10)); 
        bottomPanel.setBackground(new Color(18, 18, 18));
        bottomPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        btnPanel.setBackground(new Color(18, 18, 18));

        JButton btnLogin = new JButton("ĐĂNG NHẬP");
        btnLogin.setBackground(new Color(46, 204, 113)); btnLogin.setForeground(Color.BLACK); btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setPreferredSize(new Dimension(140, 38));
        btnLogin.putClientProperty(FlatClientProperties.STYLE, "arc: 10; borderWidth: 0; hoverBackground: #27ae60; pressedBackground: #1e8449;");

        JButton btnExit = new JButton("THOÁT");
        btnExit.setBackground(new Color(231, 76, 60)); btnExit.setForeground(Color.WHITE); btnExit.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnExit.setPreferredSize(new Dimension(140, 38));
        btnExit.putClientProperty(FlatClientProperties.STYLE, "arc: 10; borderWidth: 0; hoverBackground: #e86558; pressedBackground: #ff7669;");

        btnPanel.add(btnLogin); btnPanel.add(btnExit);

        JButton btnGoRegister = new JButton("Chưa có tài khoản? Đăng ký ngay");
        btnGoRegister.setContentAreaFilled(false); 
        btnGoRegister.setForeground(new Color(52, 172, 224));
        btnGoRegister.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGoRegister.setBorderPainted(false);

        bottomPanel.add(btnPanel);
        bottomPanel.add(btnGoRegister);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        add(mainPanel);

        // ================= XỬ LÝ SỰ KIỆN NÚT BẤM =================
        btnExit.addActionListener(e -> System.exit(0));

        btnLogin.addActionListener(e -> {
            // Lấy dữ liệu người dùng nhập
            String user = txtUsername.getText().trim();
            String pass = new String(txtPassword.getPassword());
            boolean isSuccess = false;
            String displayName = "";
            // --- KIỂM TRA TÀI KHOẢN NHIỀU NGƯỜI (Bỏ qua viết hoa/thường) ---
            if (user.equalsIgnoreCase("Vinh") && pass.equals("12345")) {
                isSuccess = true; displayName = "Vinh";
            } else if (user.equalsIgnoreCase("Hien") && pass.equals("12345")) {
                isSuccess = true; displayName = "Hiền";
            } else if (user.equalsIgnoreCase("Y") && pass.equals("12345")) {
                isSuccess = true; displayName = "Ý";
            } else if (user.equalsIgnoreCase("admin") && pass.equals("123456")) {
                isSuccess = true; displayName = "Admin";
            }

            // --- KẾT QUẢ ĐĂNG NHẬP ---
            if (isSuccess) {
                // Hiện thông báo chào mừng tên riêng
                JOptionPane.showMessageDialog(this, "Xin chào " + displayName + "! Đăng nhập thành công.", "Chào mừng", JOptionPane.INFORMATION_MESSAGE);
                this.dispose(); 
                SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
            } else {
                JOptionPane.showMessageDialog(this, "Sai tài khoản hoặc mật khẩu!\nVui lòng nhập lại.", "Lỗi đăng nhập", JOptionPane.ERROR_MESSAGE);
                txtPassword.setText(""); 
            }
        });

        // Mở trang Đăng Ký
        btnGoRegister.addActionListener(e -> {
            this.dispose(); 
            SwingUtilities.invokeLater(() -> new RegisterFrame().setVisible(true)); 
        });
    }
}