package view;

import controller.UserController;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import com.formdev.flatlaf.FlatClientProperties;

public class RegisterFrame extends JFrame {
    private JTextField txtUsername, txtDisplayName;
    private JPasswordField txtPassword, txtConfirmPassword;
    private UserController userController;

    public RegisterFrame() {
        userController = new UserController();
        setTitle("Đăng Ký - Hệ Thống Quản Lý Tài Chính");
        setSize(400, 480); // Nới rộng xíu để chứa thêm Tên hiển thị
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(20, 30, 20, 30));
        mainPanel.setBackground(new Color(18, 18, 18));

        JLabel lblTitle = new JLabel("TẠO TÀI KHOẢN", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(new Color(52, 172, 224));
        lblTitle.setBorder(new EmptyBorder(0, 0, 20, 0));
        mainPanel.add(lblTitle, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(8, 1, 5, 5));
        formPanel.setBackground(new Color(18, 18, 18));
        Font fontLabel = new Font("Segoe UI", Font.BOLD, 13);

        JLabel lblName = new JLabel("Tên hiển thị (Tên của bạn):"); lblName.setForeground(Color.WHITE); lblName.setFont(fontLabel);
        txtDisplayName = new JTextField(); txtDisplayName.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "VD: Nguyễn Văn A");
        
        JLabel lblUser = new JLabel("Tên đăng nhập:"); lblUser.setForeground(Color.WHITE); lblUser.setFont(fontLabel);
        txtUsername = new JTextField(); txtUsername.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nhập tên tài khoản...");

        JLabel lblPass = new JLabel("Mật khẩu:"); lblPass.setForeground(Color.WHITE); lblPass.setFont(fontLabel);
        txtPassword = new JPasswordField(); txtPassword.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nhập mật khẩu...");

        JLabel lblConfirm = new JLabel("Nhập lại mật khẩu:"); lblConfirm.setForeground(Color.WHITE); lblConfirm.setFont(fontLabel);
        txtConfirmPassword = new JPasswordField(); txtConfirmPassword.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Xác nhận lại mật khẩu...");

        formPanel.add(lblName); formPanel.add(txtDisplayName);
        formPanel.add(lblUser); formPanel.add(txtUsername);
        formPanel.add(lblPass); formPanel.add(txtPassword);
        formPanel.add(lblConfirm); formPanel.add(txtConfirmPassword);
        mainPanel.add(formPanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        btnPanel.setBackground(new Color(18, 18, 18));
btnPanel.setBorder(new EmptyBorder(25, 0, 0, 0));

        JButton btnRegister = new JButton("ĐĂNG KÝ");
        btnRegister.setBackground(new Color(52, 172, 224)); btnRegister.setForeground(Color.WHITE); btnRegister.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnRegister.setPreferredSize(new Dimension(140, 38));
        btnRegister.putClientProperty(FlatClientProperties.STYLE, "arc: 10; borderWidth: 0; hoverBackground: #2980b9;");

        JButton btnBack = new JButton("QUAY LẠI");
        btnBack.setBackground(new Color(80, 80, 80)); btnBack.setForeground(Color.WHITE); btnBack.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnBack.setPreferredSize(new Dimension(140, 38));
        btnBack.putClientProperty(FlatClientProperties.STYLE, "arc: 10; borderWidth: 0; hoverBackground: #666666;");

        btnPanel.add(btnRegister); btnPanel.add(btnBack);
        mainPanel.add(btnPanel, BorderLayout.SOUTH);
        add(mainPanel);

        // --- XỬ LÝ SỰ KIỆN GỌI DATABASE ---
        btnBack.addActionListener(e -> {
            this.dispose();
            SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
        });

        btnRegister.addActionListener(e -> {
            String name = txtDisplayName.getText().trim();
            String user = txtUsername.getText().trim();
            String pass = new String(txtPassword.getPassword());
            String confirm = new String(txtConfirmPassword.getPassword());

            if (name.isEmpty() || user.isEmpty() || pass.isEmpty() || confirm.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!pass.equals(confirm)) {
                JOptionPane.showMessageDialog(this, "Mật khẩu xác nhận không khớp!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                txtConfirmPassword.setText(""); return;
            }

            // GỌI DB ĐỂ LƯU THẬT
            if (userController.register(user, pass, name)) {
                JOptionPane.showMessageDialog(this, "Tạo tài khoản thành công!\nVui lòng đăng nhập để sử dụng.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                this.dispose();
                SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
            } else {
                JOptionPane.showMessageDialog(this, "Tên đăng nhập đã tồn tại hoặc có lỗi xảy ra!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}