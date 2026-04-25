package org.example.model;

import java.util.Objects;

public abstract class User {
    private String idUser;
    private String name;
    private String email;

    public User() {}

    public User(String idUser, String name, String email) {
        this.idUser = idUser;
        this.name = name;
        this.email = email;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setId(String idUser) {
        this.idUser = idUser;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    //Kiểm tra có quyền quản lý sách không
    public abstract boolean canManageBooks();
    //Kiểm tra có quyền quản lý người dùng không
    public abstract boolean canManageUsers();
    //Kiểm tra có quyền xem thống kê không
    public abstract boolean canViewStatistics();
    //Kiểm tra có quyền mượn sách không
    public abstract boolean canBorrowBooks();

    public void info() {
        System.out.println("Id người dùng: " + this.idUser + ", Tên: " + this.name + ", email: " + this.email);
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(idUser, user.idUser);
    }

}
