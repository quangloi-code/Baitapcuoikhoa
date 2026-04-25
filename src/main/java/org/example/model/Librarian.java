package org.example.model;

public class Librarian extends User {
    public Librarian(String id, String name, String email) {
        super(id, name, email);
    }

    @Override
    public boolean canManageBooks() {
        return true;
    }

    @Override
    public boolean canManageUsers() {
        return true;
    }

    @Override
    public boolean canViewStatistics() {
        return true;
    }

    @Override
    public boolean canBorrowBooks() {
        return true;
    }
    @Override
    public void info() {
        System.out.println("Id người dùng: " + super.getIdUser() + ", Tên: " + super.getName() + ", email: " + super.getEmail() + ", Thủ thư.");
    }
}
