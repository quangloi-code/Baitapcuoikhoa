package org.example.service;

import org.example.exception.*;
import org.example.model.Book;
import org.example.model.BorrowRecord;
import org.example.model.Member;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/*Lớp dịch vụ quản lý mượn/trả sách. Tạo bản ghi mượn, trả sách, kiểm tra quy tắc mượn
Đặc biệt:Không dùng database, sử dụng Map và List để lưu mối quan hệ*/
public class BorrowRecordService {
    private Map<String, BorrowRecord> recordDatabase; // Lưu tất cả bản ghi mượn
    private Map<String, List<String>> memberToRecords; // Member -> danh sách record ID
    private Map<String, String> activeBorrowByBook; // Book -> record ID (chỉ sách đang mượn)
    private AtomicInteger idCounter; // Tạo ID tự động cho bản ghi
    private BookService bookService;
    private UserService userService;

    public BorrowRecordService(BookService bookService, UserService userService) {
        this.recordDatabase = new HashMap<>();
        this.memberToRecords = new HashMap<>();
        this.activeBorrowByBook = new HashMap<>();
        this.idCounter = new AtomicInteger(1);
        this.bookService = bookService;
        this.userService = userService;
    }

    //Tạo bản ghi mượn sách
    public BorrowRecord createBorrowRecord(String memberId, String bookId)
            throws UserNotFoundException, BookNotFoundException,
            BorrowLimitExceededException, DuplicateBorrowException,
            InvalidBookException {

        //Lấy thông tin Member và Book
        Member member = (Member) userService.findUserById(memberId);

        Book book = bookService.getBookById(bookId);
        if (book == null) {
            throw new BookNotFoundException("Không tìm thấy sách có ID: " + bookId);
        }

        //Kiểm tra tất cả quy tắc mượn
        if (!member.canBorrowMore()) {
            throw new BorrowLimitExceededException("Thành viên " + memberId + " đã mượn tối đa 3 sách.");
        }

        if (book.getQuantity() <= 0) {
            throw new InvalidBookException("Sách " + bookId + " đã hết, không thể mượn.");
        }

        if (member.hasBorrowedBook(bookId)) {
            throw new DuplicateBorrowException("Thành viên " + memberId + " đã mượn sách " + bookId + " chưa trả.");
        }

        if (!book.isAllowedToBorrow()) {
            throw new InvalidBookException("Sách " + bookId + " là " + book.getClass().getSimpleName() + ", không được mượn về nhà.");
        }

        if (activeBorrowByBook.containsKey(bookId)) {
            throw new DuplicateBorrowException("Sách " + bookId + " đang được người khác mượn.");
        }

        //Tạo bản ghi mượn
        String recordId = generateRecordId();
        BorrowRecord record = new BorrowRecord(recordId, member, book);

        // Lưu vào database
        recordDatabase.put(recordId, record);

        // Cập nhật quan hệ Member -> Records
        memberToRecords.computeIfAbsent(memberId, k -> new ArrayList<>()).add(recordId);

        // Cập nhật quan hệ Book -> Record (đang mượn)
        activeBorrowByBook.put(bookId, recordId);

        //Cập nhật trạng thái
        // Thêm vào danh sách mượn của Membe
        member.addBorrowedBook(bookId);

        // Giảm số lượng sách
        bookService.decreaseQuantity(bookId);

        // Đánh dấu sách đang được mượn
        bookService.markAsBorrowed(bookId);

        return record;
    }

    //=====Trả sách=====
    public BorrowRecord returnBook(String memberId, String bookId)
            throws UserNotFoundException, BookNotFoundException,
            BorrowRecordNotFoundException, InvalidBookException {

        //Lấy thông tin Member và Book
        Member member = (Member) userService.findUserById(memberId);

        Book book = bookService.getBookById(bookId);
        if (book == null) {
            throw new BookNotFoundException("Không tìm thấy sách ID: " + bookId);
        }

        //Kiểm tra sách đã được mượn chưa
        String recordId = activeBorrowByBook.get(bookId);
        if (recordId == null) {
            throw new BorrowRecordNotFoundException("Sách " + bookId + " hiện không trong trạng thái được mượn.");
        }

        // Lấy bản ghi mượn
        BorrowRecord record = recordDatabase.get(recordId);
        if (record == null) {
            throw new BorrowRecordNotFoundException("Không tìm thấy bản ghi mượn cho sách " + bookId);
        }

        //Kiểm tra đúng người mượn trả
        if (!record.isForMember(memberId)) {
            throw new InvalidBookException("Sách " + bookId + " không phải do thành viên " + memberId + " mượn.");
        }

        //Kiểm tra sách đã được trả chưa (tránh trả 2 lần)
        if (record.isReturned()) {
            throw new BorrowRecordNotFoundException("Sách " + bookId + " đã được trả từ ngày " + record.getReturnDate());
        }

        // Cập nhật ngày trả (tự động tính số ngày mượn bên trong)
        record.returnBook();
        long borrowDays = record.getBorrowDays();

        // Gỡ bản ghi khỏi active map
        activeBorrowByBook.remove(bookId);

        // Cập nhật trạng thái Member
        member.removeBorrowedBook(bookId);

        // Cập nhật số lượng sách
        bookService.increaseQuantity(bookId);
        bookService.markAsReturned(bookId);

        System.out.println("Trả sách thành công. Số ngày bạn mượn: " + borrowDays + " ngày");

        return record;
    }
    //Phương thứ tra cứu
    //Lấy tất cả bản ghi mượn trong hệ thống
    public Collection<BorrowRecord> getAllBorrowRecords() {
        return recordDatabase.values();
    }

    //Lấy bản ghi mượn theo ID
    public BorrowRecord getRecordById(String recordId) throws BorrowRecordNotFoundException {
        BorrowRecord record = recordDatabase.get(recordId);
        if (record == null) {
            throw new BorrowRecordNotFoundException("Không tìm thấy bản ghi ID: " + recordId);
        }
        return record;
    }

    //Lấy tất cả bản ghi mượn của một Member
    public List<BorrowRecord> getRecordsByMember(String memberId) {
        List<BorrowRecord> records = new ArrayList<>();
        List<String> recordIds = memberToRecords.get(memberId);

        if (recordIds != null) {
            for (String recordId : recordIds) {
                BorrowRecord record = recordDatabase.get(recordId);
                if (record != null) {
                    records.add(record);
                }
            }
        }
        return records;
    }

    //Lấy bản ghi đang mượn của một sách
    public BorrowRecord getActiveBorrowByBook(String bookId) {
        String recordId = activeBorrowByBook.get(bookId);
        if (recordId != null) {
            return recordDatabase.get(recordId);
        }
        return null;
    }

    //Kiểm tra sách đang được mượn hay không
    public boolean isBookCurrentlyBorrowed(String bookId) {
        return activeBorrowByBook.containsKey(bookId);
    }

    //Lấy tất cả bản ghi đang mượn
    public List<BorrowRecord> getActiveBorrowRecords() {
        List<BorrowRecord> activeRecords = new ArrayList<>();
        for (String bookId : activeBorrowByBook.keySet()) {
            String recordId = activeBorrowByBook.get(bookId);
            BorrowRecord record = recordDatabase.get(recordId);
            if (record != null && !record.isReturned()) {
                activeRecords.add(record);
            }
        }
        return activeRecords;
    }

    // Tạo ID bản ghi mượn theo format: BR00001, BR00002,
    private String generateRecordId() {
        return "BR" + String.format("%05d", idCounter.getAndIncrement());
    }
}




