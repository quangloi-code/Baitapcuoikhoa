package org.example.model;

public class ReferenceBook extends Book {
    public ReferenceBook (String id, String title, String author, int year, int quantity) {
        super(id, title, author, year, quantity);
    }

    @Override
    public String getBorrowRule() {
        return "Sách tham khảo không được mượn về, chỉ được phép đọc tại thư viện.";
    }

    @Override
    public boolean isAllowedToBorrow() {//Sách tham khảo ko đc phép mượn về nhà
        return true;
    }

    @Override
    public void showInfo() {
        System.out.println("ID: " + super.getId() + ", Tiêu đề: " + super.getTitle() + ", Tác giả: " + super.getAuthor() + ", Năm xuất bản: " + super.getYear() + ", Số lượng: " + super.getQuantity() + ", ReferenceBook");
    }
}
