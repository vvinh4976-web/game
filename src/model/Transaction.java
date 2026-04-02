package model;

import java.util.Date;

public class Transaction {
    private int id;
    private String title;
    private double amount;
    private String category;
    private Date date;
    private String type; // "Thu" hoặc "Chi"

    // Constructor không tham số
    public Transaction() {}

    // Getter và Setter (Chuột phải -> Source -> Generate Getters and Setters)
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    // ... làm tương tự cho các trường còn lại
}
