package dao;
import model.Transaction;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {
    private static final String URL = "jdbc:mysql://localhost:3306/db_quanlythuchi";
    private static final String USER = "root";
    private static final String PASS = "";

    // Hàm lấy danh sách từ MySQL
    public List<Transaction> getAll() {
        List<Transaction> list = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM transactions ORDER BY transaction_date DESC")) {
            while (rs.next()) {
                list.add(new Transaction(rs.getInt("id"), rs.getDouble("amount"), 
                         rs.getString("category"), rs.getString("note"), rs.getString("transaction_date")));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // Hiền viết thêm các hàm insert(), delete(), update() ở dưới này nhé...
}