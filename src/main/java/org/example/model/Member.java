package org.example.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class Member extends User {
    private LocalDateTime createdAt; // Thời điểm tạo Member
    private Set<String> borrowedBookIds; // Danh sách ID sách đang mượn


    public Member(String idUser, String name, String email) {
        super(idUser, name, email);
        this.createdAt = LocalDateTime.now(); //ghi tại thời điểm tạo
        this.borrowedBookIds = new HashSet<>(); // khởi tạo Set rỗng
    }

    @Override
    public boolean canManageBooks() {
        return false;
    }

    @Override
    public boolean canManageUsers() {
        return false;
    }

    @Override
    public boolean canViewStatistics() {
        return false;
    }

    @Override
    public boolean canBorrowBooks() {
        return true;
    }

    //Lấy số lượng sách đang mượn hiện tại
    public int getCurrentBorrowCount() {
        return borrowedBookIds.size();
    }

    //kiểm tra có thể nhận được thêm sách ko mỗi Member tối đa mượn 3 cuốn
    public boolean canBorrowMore() {
        return borrowedBookIds.size() < 3;
    }

    //Kiểm tra xem đã mượn cuốn sách đấy chưa, ko đc mượn cùng 1 quyển nếu chưa trả quyển đấy
    public boolean hasBorrowedBook(String bookId) {
        return borrowedBookIds.contains(bookId);
    }

    //Thêm sách vào danh sách đang mượn khi mượn thành công
    public void addBorrowedBook(String bookId) {
        borrowedBookIds.add(bookId);
    }

    //Khi trả sách sẽ xóa nó khỏi danh sách mượn
    public void removeBorrowedBook(String bookId) {
        borrowedBookIds.remove(bookId);
    }

    //Lấy danh sách Id sách đang mượn, trả về bản sao để tránh sửa ngoài ý muốn
    public Set<String> getBorrowedBookIds() {
        return new HashSet<>(borrowedBookIds);
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public void info() {
        System.out.println("Id người dùng: " + super.getIdUser() + ", Tên: " + super.getName() + ", email: " + super.getEmail() + ", CreatedAt: " + createdAt + ", Số lần mượn: " + borrowedBookIds.size());
    }
}
