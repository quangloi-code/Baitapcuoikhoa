package org.example.model;

public class TextBook extends Book {
    public TextBook (String id, String title, String author, int year, int quantity) {
        super(id, title, author, year, quantity);
    }

    @Override
    public String getBorrowRule() {
        return "Sách giáo khoa mượn tối đa 30 ngày, có thể gia hạn 1 lần.";
    }

    @Override
    public boolean isAllowedToBorrow() {//Sách giáo khoa đc phép mượn về nhà
        return true;
    }

    @Override
    public void showInfo() {
        System.out.println("ID: " + super.getId() + ", Tiêu đề: " + super.getTitle() + ", Tác giả: " + super.getAuthor() + ", Năm xuất bản: " + super.getYear() + ", Số lượng: " + super.getQuantity() + ", TextBook");
    }
}
