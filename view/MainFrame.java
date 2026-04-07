} catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "⚠️ Ô Số tiền chỉ được phép nhập số!", "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "⚠️ " + ex.getMessage(), "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa?", "Xác nhận", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    int id = (int) tableModel.getValueAt(row, 0);
                    if (controller.deleteTransaction(id)) {
                        loadDataToTable();
                        updateChart();
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "⚠️ Vui lòng click chọn 1 dòng trên bảng để xóa!");
            }
        });
    }

    public void loadDataToTable() {
        tableModel.setRowCount(0);
        List<Transaction> list = controller.getAllTransactions();
        for (Transaction t : list) {
            tableModel.addRow(new Object[]{
                t.getId(), String.format("%,.0f VNĐ", t.getAmount()), t.getCategory(), t.getNote(), t.getDate()
            });
        }
    }

    // Hàm gọi biểu đồ của Ý
    private void updateChart() {
        chartContainer.removeAll();
        // Nơi này Ý sẽ code hàm lấy Panel biểu đồ dán vào đây
        // Ví dụ: chartContainer.add(Ý_Chart_Class.createChartPanel());
        chartContainer.revalidate();
        chartContainer.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}