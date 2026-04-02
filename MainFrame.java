package view;
import dao.TransactionDAO;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.Transaction;

public class MainFrame extends JFrame {
    private DefaultTableModel model;
    private JTable table;
    private TransactionDAO dao = new TransactionDAO(); // Gọi phần của Hiền

    public MainFrame() {
        setTitle("QUẢN LÝ THU CHI - NHÓM VINH - Ý - HIỀN");
        setSize(800, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Bảng hiển thị
        model = new DefaultTableModel(new String[]{"ID", "Tiền", "Loại", "Ghi chú", "Ngày"}, 0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        loadData();
        setVisible(true);
    }

    public void loadData() {
        model.setRowCount(0);
        for (Transaction t : dao.getAll()) { // Dùng đối tượng của Ý và hàm của Hiền
            model.addRow(new Object[]{t.getId(), t.getAmount(), t.getCategory(), t.getNote(), t.getDate()});
        }
    }

    public static void main(String[] args) {
        new MainFrame();
    }
}