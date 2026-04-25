package org.example.service;

import org.example.exception.BookAlreadyBorrowedException;
import org.example.exception.BookNotFoundException;
import org.example.exception.DuplicateIdException;
import org.example.exception.InvalidBookException;
import org.example.model.Book;
import org.example.model.Novel;
import org.example.model.ReferenceBook;
import org.example.model.TextBook;

import java.util.*;

//Lớp dịch vụ quản lý sách. Chịu trách nhiệm: CRUD sách, validate dữ liệu, quản lý trạng thái mượn/trả
public class BookService {
    private Map<String, Book> bookDatabase; // Lưu sách theo ID (key = id, value = Book object)
    private Set<String> borrowedBookIds; // Lưu ID sách đang được mượn (để kiểm khi xóa)

    public BookService() {
        this.bookDatabase = new HashMap<>();
        this.borrowedBookIds = new HashSet<>();
    }

    // ======Thêm sách vào hệ thống======
    public void addBook(Book book) throws DuplicateIdException, InvalidBookException {
        //Kiểm tra Id duy nhất
        if (bookDatabase.containsKey(book.getId())) {
            throw  new DuplicateIdException("Id sách: " + book.getId() + ", đã tồn tại.");
        }
        // Validate năm xuất bản
        validateYear(book.getYear());

        // Validate số lượng (phải >= 0)
        validateQuantity(book.getQuantity());

        // Thêm sách vào database
        bookDatabase.put(book.getId(), book);
    }
    //Tạo và thêm sách theo loại (TextBook, Novel, ReferenceBook)
    public Book createBook(String type, String id, String title, String author, int year, int qty)
            throws InvalidBookException, DuplicateIdException {

        // In ra để debug xem type nhập vào là gì
        System.out.println("DEBUG - Type nhận được: '" + type + "'");

        // Chuyển về chữ thường và loại bỏ khoảng trắng đầu cuối
        String normalizedType = type.trim().toLowerCase();

        Book book;

        switch (normalizedType) {
            case "textbook":
                book = new TextBook(id, title, author, year, qty);
                break;
            case "novel":
                book = new Novel(id, title, author, year, qty);
                break;
            case "referencebook":
                book = new ReferenceBook(id, title, author, year, qty);
                break;
            default:
                throw new InvalidBookException("Loại sách không hợp lệ: '" + type + "'. Chấp nhận: textbook, novel, referencebook");
        }

        addBook(book);
        return book;
    }

    //Cập nhật thông tin sách, không cho phép sửa Id
    public void updateBook(String id, String newTitle, String newAuthor, Integer newYear,Integer newQuantity)
        throws BookNotFoundException, InvalidBookException {

        //Kiểm tra sách tồn tại trước khi sửa
        Book book = bookDatabase.get(id);
        if (book == null) {
            throw new BookNotFoundException("Không tìm thấy sách có ID: " + id);
        }

        //Cập nhật metadata
        if (newTitle != null && !newTitle.trim().isEmpty()) {
            book.setTitle(newTitle);
        }

        if (newAuthor != null && !newAuthor.trim().isEmpty()) {
            book.setAuthor(newAuthor);
        }

        if (newYear != null) {
            validateYear(newYear);
            book.setYear(newYear);
        }

        if (newQuantity != null) {
            validateQuantity(newQuantity);
            book.setQuantity(newQuantity);
        }

    }

    //=====Xóa sách khỏi hệ thống=====
    public void deleteBook(String id)
        throws BookNotFoundException, BookAlreadyBorrowedException {

        //Kiểm tra sách tồn tại
        Book book = bookDatabase.get(id);
        if (book == null) {
            throw new BookNotFoundException("Không tìm thấy sách có ID: " + id);
        }

        //Kiểm tra sách đang được mượn, không được phép xóa sách đang mượn
        if (borrowedBookIds.contains(id)) {
            throw new BookAlreadyBorrowedException("Không thể xóa sách " + id + " vì sách đang được mượn.");
        }
        //Xóa sách
        bookDatabase.remove(id);
    }
    //Kiểm tra năm xuất bản có hợp lệ ko
    private void validateYear(int year) throws InvalidBookException {
        int currentYear = java.time.Year.now().getValue();
        if (year < 1700 || year > currentYear) {
            throw new InvalidBookException("Năm xuất bản không hơp lệ: " + year + ". Năm xuất bản phải từ 1700 - " + currentYear);
        }
    }

    //Kiểm tra số lượng sách hợp lệ, số lượng không thể âm
    private void validateQuantity(int quantity)
        throws InvalidBookException {
        if (quantity < 0) {
            throw new InvalidBookException("Số lượng sách không hợp lệ: " + quantity + ". Số lượng sách phải lớn hơn 0.");
        }
    }

    //Kiểm tra sách còn tồn tại trong hệ thống
    public boolean isBookExist(String id) {
        return bookDatabase.containsKey(id);
    }

    //Lấy sách theo ID, trả về Book object hoặc null nếu không tìm thấy
    public Book getBookById(String id) {
        return bookDatabase.get(id);
    }

    //Lấy tất cả sách trong hệ thống
    public Collection<Book> getAllBooks() {
        return bookDatabase.values();
    }

    //=====quản lí trang thái mượn/trả=====

    //Đánh dấu sách đã được mượn(BorrowService)
    public void markAsBorrowed(String bookId)
        throws BookNotFoundException {
        if (!bookDatabase.containsKey(bookId)) {
            throw new BookNotFoundException("Không tìm thấy sách ID: " + bookId);
        }
        borrowedBookIds.add(bookId);
    }

    //Đánh dấu sách đã được trả(BorrowService)
    public void markAsReturned(String bookId) {
        borrowedBookIds.remove(bookId);
    }

    //Kiểm tra sách có đang được mượn không
    public boolean isBookBorrowed(String bookId) {
        return borrowedBookIds.contains(bookId);
    }

    //Giảm số lượng sách khi mượn
    public void decreaseQuantity(String bookId)
        throws BookNotFoundException, InvalidBookException {
        Book book = bookDatabase.get(bookId);
        if(book == null) {
            throw new BookNotFoundException("Không tìm thấy sách ID: " + bookId);
        }
        int newQuantity = book.getQuantity() - 1;
        if (newQuantity < 0) {
            throw new InvalidBookException("Sách " + bookId + " đã hết, không mượn được.");
        }
        book.setQuantity(newQuantity);
    }

    //Tăng số lượng sách khi trả về
    public void increaseQuantity(String bookId)
        throws BookNotFoundException {
        Book book = bookDatabase.get(bookId);
        if (book == null) {
            throw new BookNotFoundException("Không tìm thấy sách ID: " + bookId);
        }
        book.setQuantity(book.getQuantity() + 1);
    }
}
