package org.example.model;

import java.util.Objects;

public abstract class Book {
    private String id;
    private String title;
    private String author;
    private int year;
    private int quantity;

    public Book() {}

    public Book(String id, String title, String author, int year, int quantity) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.year = year;
        this.quantity = quantity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public abstract String getBorrowRule();
    public abstract boolean isAllowedToBorrow();

    //so sánh hai sách dựa trên ID
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;// null hoặc khác class
        Book book = (Book) obj;
        return Objects.equals(id, book.id);
    }

    public void showInfo() {
        System.out.println("ID: " + this.id + ", Tiêu đề: " + this.title + ", Tác giả: " + this.author + ", Năm xuất bản: " + this.year + ", Số lượng: " + this.quantity);
    }
}
