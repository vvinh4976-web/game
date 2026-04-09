import view.LoginFrame; // Đổi import sang LoginFrame
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import com.formdev.flatlaf.FlatDarkLaf;

public class Main {
    public static void main(String[] args) {
        // 1. Cài đặt giao diện Dark Theme
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception e) {
            System.err.println("Loi khoi tao FlatLaf");
        }

        // 2. KHỞI ĐỘNG MÀN HÌNH ĐĂNG NHẬP TRƯỚC
        SwingUtilities.invokeLater(() -> {
            LoginFrame login = new LoginFrame();
            login.setVisible(true);
        });
    }
}