package model;

public class Transaction {
    private int id;
    private double amount;
    private String category;
    private String note;
    private String date;

    public Transaction(int id, double amount, String category, String note, String date) {
        this.id = id;
        this.amount = amount;
        this.category = category;
        this.note = note;
        this.date = date;
    }
    // Getters
    public int getId() { return id; }
    public double getAmount() { return amount; }
    public String getCategory() { return category; }
    public String getNote() { return note; }
    public String getDate() { return date; }
}
TransactionDAO.java
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
package utils;
import java.sql.*;

public class DBConnection {
    // Quy tắc CamelCase: tên biến bắt đầu bằng chữ thường
    private static String url = "jdbc:mysql://localhost:3306/db_quanlythuchi";
    private static String user = "root";
    private static String password = "";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}
package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Lớp tiện ích để quản lý kết nối đến cơ sở dữ liệu MySQL.
 * Tuân thủ quy tắc đặt tên CamelCase và có comment giải thích.
 */
public class DBConnection {
    
    // Thông tin cấu hình database (Sử dụng CamelCase cho tên biến)
    private static final String hostName = "localhost";
    private static final String dbName = "db_quanlythuchi"; // Tên database đã tạo trong XAMPP
    private static final String userName = "root";
    private static final String password = ""; // XAMPP mặc định để trống
    
    // Chuỗi kết nối có thêm các tham số để tránh lỗi múi giờ và SSL
    private static final String connectionUrl = "jdbc:mysql://" + hostName + ":3306/" + dbName 
            + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

    /**
     * Hàm thiết lập kết nối đến MySQL.
     * @return Connection đối tượng kết nối
     * @throws SQLException nếu kết nối thất bại
     */
    public static Connection getConnection() throws SQLException {
        Connection conn = null;
        try {
            // Đăng ký Driver (tùy chọn với các bản Java mới nhưng nên có để ổn định)
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(connectionUrl, userName, password);
            System.out.println("Kết nối Database thành công!");
        } catch (ClassNotFoundException e) {
            System.out.println("Không tìm thấy Driver MySQL! Hãy kiểm tra file .jar trong thư mục lib.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Lỗi kết nối: Hãy đảm bảo XAMPP đã bật MySQL.");
            throw e;
        }
        return conn;
    }
}
DBConnection.java
Transaction.java
package model;

/**
 * Lớp Transaction đại diện cho một thực thể giao dịch thu chi.
 * Đây là thành phần 'Model' trong mô hình MVC.
 * Tuân thủ quy tắc CamelCase và Clean Code.
 */
public class Transaction {
    // Các thuộc tính (fields) khai báo private để đảm bảo tính đóng gói
    private int id;
    private double amount;
    private String category;
    private String note;
    private String transactionDate; // Đổi từ transaction_date (SQL) sang CamelCase

    /**
     * Constructor không tham số (Mặc định)
     */
    public Transaction() {
    }

    /**
     * Constructor có đầy đủ tham số để khởi tạo đối tượng nhanh
     * @param id Mã giao dịch
     * @param amount Số tiền
     * @param category Loại thu chi
     * @param note Ghi chú
     * @param transactionDate Ngày giao dịch
     */
    public Transaction(int id, double amount, String category, String note, String transactionDate) {
        this.id = id;
        this.amount = amount;
        this.category = category;
        this.note = note;
        this.transactionDate = transactionDate;
    }

    // --- Các hàm Getter và Setter (Dùng để truy xuất và cập nhật dữ liệu) ---

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    /**
     * Ghi đè phương thức toString để hỗ trợ in dữ liệu ra Terminal khi cần kiểm tra (Debug)
     */
    @Override
    public String toString() {
        return "Transaction [id=" + id + ", amount=" + amount + ", category=" + category + ", note=" + note + ", date=" + transactionDate + "]";
    }
}
@