package org.example.service;

import org.example.exception.*;
import org.example.model.Book;
import org.example.model.Member;

public class BorrowService {
    private BookService bookService;
    private UserService userService;

    public BorrowService(BookService bookService, UserService userService) {
        this.bookService = bookService;
        this.userService = userService;
    }

    public void borrowBook(String memberId, String bookId)
        throws UserNotFoundException, BookNotFoundException, BorrowLimitExceededException,
            DuplicateBorrowException, InvalidBookException {
        Member member = (Member) userService.findUserById(memberId);

        if(!member.canBorrowBooks()) {
            throw new BorrowLimitExceededException("Thành viên: " + memberId + " đã mượn tối đa 3 sách. Không thể mượn thêm.");
        }
        Book book = bookService.getBookById(bookId);
        if (book == null) {
            throw new BookNotFoundException("Không tìm thấy sách có Id: " + bookId);
        }
        if (book.getQuantity() <= 0) {
            throw new BookNotFoundException("Sách: " + bookId + " đã hết hàng không thể mượn.");
        }
        if (member.hasBorrowedBook(bookId)) {
            throw new DuplicateBorrowException("Thành viên " + memberId + " đang mượn cuốn sách " + bookId + " này rồi. Chưa trả không thể mượn được.");
        }
        if (!book.isAllowedToBorrow()) {
            throw new InvalidBookException("Sách " + bookId + " là " + book.getClass().getSimpleName() + " không được phép mượn về nhà.");
        }
        member.addBorrowedBook(bookId);

        bookService.decreaseQuantity(bookId);
        bookService.markAsBorrowed(bookId);
    }
    public void returnBook(String memberId, String bookId)
        throws UserNotFoundException, BookNotFoundException, InvalidBookException {
        Member member = (Member) userService.findUserById(memberId);
        Book book = bookService.getBookById(bookId);
        if(book == null) {
            throw new BookNotFoundException("Không tìm thấy sách Id: " + bookId);
        }
        if (!member.hasBorrowedBook(bookId)) {
            throw new InvalidBookException("Thành viên " + memberId + " không mượn sách " + bookId + bookId);
        }
        member.removeBorrowedBook(bookId);

        bookService.increaseQuantity(bookId);
        bookService.markAsReturned(bookId);
    }
}
