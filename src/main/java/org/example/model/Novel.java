package org.example.model;

public class Novel extends Book {
    public Novel (String id, String title, String author, int year, int quantity) {
        super(id, title, author, year, quantity);
    }

    @Override
    public String getBorrowRule() {
        return "Tiểu thuyết mượn tối đa 14 ngày, không được gia hạn.";
    }

    @Override
    public boolean isAllowedToBorrow() {//Tiểu thuyết đc phép mượn về nhà
        return true;
    }

    @Override
    public void showInfo() {
        System.out.println("ID: " + super.getId() + ", Tiêu đề: " + super.getTitle() + ", Tác giả: " + super.getAuthor() + ", Năm xuất bản: " + super.getYear() + ", Số lượng: " + super.getQuantity() + ", Novel");
    }
}
