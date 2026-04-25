package org.example.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/*
  Lớp BorrowRecord - Bản ghi mỗi lần mượn sách
 Lưu thông tin: ai mượn, mượn sách gì, ngày mượn, ngày trả
*/
public class BorrowRecord {
    private String recordId; // Mã bản ghi mượn (duy nhất)
    private Member member; // Người mượn (Member, không phải Librarian)
    private Book book; // Sách được mượn
    private LocalDate borrowDate; // Ngày mượn
    private LocalDate returnDate; // Ngày trả (null nếu chưa trả)

    public BorrowRecord(String recordId, Member member, Book book) {
        this.recordId = recordId;
        this.member = member;
        this.book = book;
        this.borrowDate = LocalDate.now(); //Ghi ngày mượn
        this.returnDate = null; //Chưa trả
    }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(LocalDate borrowDate) {
        this.borrowDate = borrowDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    //Thực hiện trả sách, Cập nhật ngày trả là thời điểm hiện tại
    public void returnBook() {
        this.returnDate = LocalDate.now();
    }

    //Kiểm tra sách đã được trả chưa, return: true nếu đã trả, false nếu đang mượn
    public boolean isReturned() {
        return returnDate != null;
    }

    //Tính số ngày mượn tính khi đã trả sách,return số ngày mượn, nếu chưa trả trả về 0
    public long getBorrowDays() {
        if (returnDate == null) {
            return 0;
        }
        return ChronoUnit.DAYS.between(borrowDate, returnDate);
    }

    //Kiểm tra bản ghi có liên quan đến sách nào không?
    public boolean isForBook(String bookId) {
        return book.getId().equals(bookId);
    }

    //Kiểm tra bản ghi có liên quan đến member nào không?
    public boolean isForMember(String memberId) {
        return member.getIdUser().equals(memberId);
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BorrowRecord that = (BorrowRecord) o;
        return Objects.equals(recordId, that.recordId);
    }
    public void display() {
        System.out.println("RecordID: " + recordId + ", memberID: " + member.getIdUser() + ", bookID: " + book.getId() + ", ngày mượn: " + borrowDate + ", ngày trả: " + (returnDate == null ? "Chưa trả" : returnDate) + ", số ngày mượn: " + (isReturned() ? getBorrowDays() + " ngày" : "Chưa trả"));
    }
}
