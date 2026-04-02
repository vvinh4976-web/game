import org.jfree.chart.*;
import org.jfree.data.general.DefaultPieDataset;
import javax.swing.*;
import java.sql.*;

public class ChartReport extends JFrame {
    public ChartReport() {
        setTitle("Biểu đồ Thu chi - Nhóm 3");
        setSize(600, 400);

        DefaultPieDataset dataset = new DefaultPieDataset();
        try (Connection conn = DatabaseHelper.getConnection()) {
            String sql = "SELECT category, SUM(amount) as total FROM transactions GROUP BY category";
            ResultSet rs = conn.createStatement().executeQuery(sql);
            while (rs.next()) {
                dataset.setValue(rs.getString("category"), rs.getDouble("total"));
            }
        } catch (Exception e) { e.printStackTrace(); }

        JFreeChart chart = ChartFactory.createPieChart("Tỉ lệ chi tiêu", dataset, true, true, false);
        add(new ChartPanel(chart));
        setVisible(true);
    }
}