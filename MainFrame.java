package view;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import controller.TransactionController;
import model.Transaction;
import java.awt.*;

public class MainFrame extends JFrame {
    private JTable table;
    private DefaultTableModel tableModel;
    private TransactionController controller = new TransactionController();

    public MainFrame() {
        // Thiết kế giao diện tại đây
        setTitle("Quản Lý Thu Chi - Nhóm Vinh Ý Hiền");
        setSize(800, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        tableModel = new DefaultTableModel(new String[]{"ID", "Số Tiền", "Loại", "Ghi Chú", "Ngày"}, 0);
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        loadDataToTable();
    }

    private void loadDataToTable() {
        tableModel.setRowCount(0);
        // Gọi Controller để lấy dữ liệu (đúng chuẩn MVC)
        for (Transaction t : controller.getAllTransactions()) {
            tableModel.addRow(new Object[]{t.getId(), t.getAmount(), t.getCategory(), t.getNote(), t.getDate()});
        }
    }

    public static void main(String[] args) {
        new MainFrame().setVisible(true);
    }
}