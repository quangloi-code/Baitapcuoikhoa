package org.example.controller;

import org.example.exception.*;
import org.example.model.*;
import org.example.service.*;
import org.w3c.dom.ls.LSOutput;

import java.time.format.DateTimeFormatter;
import java.util.*;

/* Lớp điều khiển chính của ứng dụng
- Chịu trách nhiệm: Khởi tạo các service, tạo dữ liệu mẫu khi khởi động, hiển thị menu và xử lý lựa chọn của người dùng,
điều phối các nghiệp vụ giữa các service.
*/
public class LibraryController {
    private final BookService bookService = new BookService();
    private final UserService userService = new UserService();
    private final BorrowRecordService borrowService;
    private final BookSearchService searchService;
    private final BookSortService sortService;
    private final Scanner sc = new Scanner(System.in);
    private User currentUser;

    public LibraryController() {
        this.borrowService = new BorrowRecordService(bookService, userService);
        this.searchService = new BookSearchService(bookService);
        this.sortService = new BookSortService(bookService);
        initData();
    }

    private void initData() {
        try {
            // Tạo Librarian
            userService.createLibrarian("L001", "Admin", "admin@lib.com");

            // Tạo Members
            userService.createMember("M001", "Nguyễn Văn A", "nguyenvana@email.com");
            userService.createMember("M002", "Trần Thị B", "tranthi@email.com");

            // Tạo sách
            bookService.createBook("textbook", "B001", "Lập trình Java", "Nguyễn Văn A", 2021, 5);
            bookService.createBook("novel", "B002", "Đắc Nhân Tâm", "Dale Carnegie", 2018, 3);
            bookService.createBook("referencebook", "B003", "Bách khoa toàn thư", "Nhiều tác giả", 2020, 2);

            System.out.println("Khởi tạo dữ liệu thành công.");

        } catch (Exception e) {
            System.out.println("Lỗi init: " + e.getMessage());
           // e.printStackTrace();  // In chi tiết lỗi để debug
        }
    }

    public void start() {
        while (true) {
            System.out.println();
            System.out.println("===== SMART LIBRARY =====");
            System.out.println("1. Đăng nhập");
            System.out.println("2. Thoát");
            System.out.println("Chọn: ");
            try {
                int choice = Integer.parseInt(sc.nextLine());
                if (choice == 1)
                    login();
                else if (choice == 2) {
                    System.out.println("Tạm biệt.");
                    return; }
                else
                    throw new InvalidMenuOptionException("Chọn 1-2.");
            } catch (NumberFormatException | InvalidMenuOptionException e) {
                System.out.println("Lỗi: " + e.getMessage());
            }
        }
    }


    private void login() {
        System.out.println();
        System.out.print("Nhập User ID: ");
        String id = sc.nextLine();
        try {
            currentUser = userService.findUserById(id);
            System.out.println("Chào " + currentUser.getName());
            if (currentUser instanceof Librarian)
                showLibrarianMenu();
            else showMemberMenu();
        } catch (UserNotFoundException e) {
            System.out.println("Lỗi: " + e.getMessage());
        }
    }

    private void showLibrarianMenu() {
        while (true) {
            System.out.println();
            System.out.println("===== Librarian menu =====");
            System.out.println("1. Quản lý sách");
            System.out.println("2. Quản lý user");
            System.out.println("3. Thống kê");
            System.out.println("4. Tìm kiếm");
            System.out.println("5. Sắp xếp");
            System.out.println("6. Đăng xuất");
            System.out.println("Chọn: ");
            try {
                int c = Integer.parseInt(sc.nextLine());
                switch (c) {
                    case 1 -> manageBooks();
                    case 2 -> manageUsers();
                    case 3 -> viewStatistics();
                    case 4 -> searchBooks();
                    case 5 -> sortBooks();
                    case 6 -> {
                        System.out.println("Đăng xuất.");
                        return; }
                    default
                            -> throw new InvalidMenuOptionException("Chọn 1-6.");
                }
            } catch (Exception e) {
                System.out.println("Lỗi: " + e.getMessage()); }
        }
    }

    private void showMemberMenu() {
        while (true) {
            System.out.println();
            System.out.println("===== MEMBER MENU =====");
            System.out.println("1. Xem sách");
            System.out.println("2. Tìm kiếm");
            System.out.println("3. Sắp xếp");
            System.out.println("4. Mượn sách");
            System.out.println("5. Trả sách");
            System.out.println("6. Sách đang mượn");
            System.out.println("7. Đăng xuất");
            System.out.println("Chọn: ");
            try {
                int c = Integer.parseInt(sc.nextLine());
                switch (c) {
                    case 1 -> viewAllBooks();
                    case 2 -> searchBooks();
                    case 3 -> sortBooks();
                    case 4 -> borrowBook();
                    case 5 -> returnBook();
                    case 6 -> viewBorrowedBooks();
                    case 7 -> {
                        System.out.println("Đăng xuất.");
                        return; }
                    default -> throw new InvalidMenuOptionException("Chọn 1-7.");
                }
            } catch (Exception e) {
                System.out.println("Lỗi: " + e.getMessage()); }
        }
    }

    private void manageBooks() {
        System.out.println();
        System.out.println("1. Thêm");
        System.out.println("2. Sửa");
        System.out.println("3. Xóa");
        System.out.println("4. Back");
        System.out.println("Chon: ");
        try {
            int c = Integer.parseInt(sc.nextLine());
            if (c == 1)
                addBook();
            else if (c == 2)
                updateBook();
            else if (c == 3)
                deleteBook();
            else if (c == 4)
                return ;
            else
                throw new InvalidMenuOptionException("Chọn 1-4.");
        } catch (Exception e) {
            System.out.println("Lỗi: " + e.getMessage()); }
    }

    private void addBook() {
        try {
            System.out.println();
            System.out.print("Loại (TextBook/Novel/ReferenceBook): ");
            String type = sc.nextLine();

            System.out.print("ID: ");
            String id = sc.nextLine();

            System.out.print("Title: ");
            String title = sc.nextLine();

            System.out.print("Author: ");
            String author = sc.nextLine();

            System.out.print("Year: ");
            int year = Integer.parseInt(sc.nextLine());

            System.out.print("Quantity: ");
            int qty = Integer.parseInt(sc.nextLine());

            bookService.createBook(type, id, title, author, year, qty);
            System.out.println("Thêm thành công!");

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void updateBook() {
        try {
            System.out.println();
            System.out.print("ID sách cần sửa: ");
            String id = sc.nextLine();

            System.out.print("Title mới (bỏ trống nếu không đổi): ");
            String title = sc.nextLine();

            System.out.print("Author mới: ");
            String author = sc.nextLine();

            System.out.print("Year mới (-1 nếu không): ");
            int y = Integer.parseInt(sc.nextLine());

            System.out.print("Quantity mới (-1 nếu không): ");
            int q = Integer.parseInt(sc.nextLine());

            bookService.updateBook(id, title.isEmpty() ? null : title, author.isEmpty() ? null : author,
                    y == -1 ? null : y, q == -1 ? null : q);
            System.out.println("Sửa thành công.");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void deleteBook() {
        try {
            System.out.println();
            System.out.print("ID sách cần xóa: ");
            String id = sc.nextLine();

            bookService.deleteBook(id);
            System.out.println("Xóa thành công.");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void manageUsers() {
        try {
            System.out.println();
            System.out.print("ID Member mới: ");
            String id = sc.nextLine();

            System.out.print("Tên: ");
            String name = sc.nextLine();

            System.out.print("Email: ");
            String email = sc.nextLine();

            userService.createMember(id, name, email);
            System.out.println("Tạo Member thành công.");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void viewStatistics() {
        System.out.println();
        System.out.println("===== Thống kê =====");
        System.out.println("Tổng sách: " + bookService.getAllBooks().size());
        System.out.println("Tổng Member: " + userService.getAllMembers().size());
        System.out.println("Sách đang mượn: " + borrowService.getActiveBorrowRecords().size());
    }

    private void viewAllBooks() {
        System.out.println();
        System.out.println("===== Danh sách sách =====");
        for (Book b : bookService.getAllBooks())
            System.out.println(b.getId() + " - " + b.getTitle() + " (" + b.getQuantity() + ")");
    }

    private void searchBooks() {
        System.out.println();
        System.out.println("1. Theo Title");
        System.out.println("2. Theo Author");
        System.out.println("3. Theo Category");
        System.out.println("Chọn: ");
        try {
            System.out.println();
            int c = Integer.parseInt(sc.nextLine());
            System.out.print("Nhập từ khóa: ");
            String kw = sc.nextLine();
            List<Book> results = switch (c) {
                case 1 -> searchService.searchByTitle(kw);
                case 2 -> searchService.searchByAuthor(kw);
                case 3 -> searchService.searchByCategory(kw);
                default ->
                        throw new InvalidMenuOptionException("Chọn 1-3.");
            };
            System.out.println("Kết quả (" + results.size() + "):");
            results.forEach(b -> System.out.println("  - " + b.getTitle()));
        } catch (Exception e) {
            System.out.println("Lỗi: " + e.getMessage()); }
    }

    private void sortBooks() {
        System.out.println();
        System.out.println("1. Theo Title");
        System.out.println("2. Theo Year");
        System.out.println("3. Theo Quantity");
        System.out.println("Chọn: ");
        try {
            int c = Integer.parseInt(sc.nextLine());
            List<Book> sorted = switch (c) {
                case 1 -> sortService.sortByTitle();
                case 2 -> sortService.sortByYearDescending();
                case 3 -> sortService.sortByQuantityDescending();
                default -> throw new InvalidMenuOptionException("Chọn 1-3.");
            };
            sorted.forEach(b -> System.out.println(b.getTitle() + " - " + b.getYear() + " - " + b.getQuantity()));
        } catch (Exception e) {
            System.out.println("Lỗi: " + e.getMessage()); }
    }

    private void borrowBook() {
        try {
            System.out.println();
            System.out.print("Nhập ID sách: ");
            String bookId = sc.nextLine();
            borrowService.createBorrowRecord(currentUser.getIdUser(), bookId);
            System.out.println("Mượn thành công.");
        } catch (Exception e) {
            System.out.println(e.getMessage()); }
    }

    private void returnBook() {
        try {
            System.out.println();
            System.out.print("Nhập ID sách cần trả: ");
            String bookId = sc.nextLine();
            BorrowRecord record = borrowService.returnBook(currentUser.getIdUser(), bookId);
            System.out.println("Trả thành công. Mượn " + record.getBorrowDays() + " ngày");
        } catch (Exception e) {
            System.out.println(e.getMessage()); }
    }

    private void viewBorrowedBooks() {
        System.out.println();
        Member m = (Member) currentUser;
        System.out.println("===== Sách đang mượn (" + m.getBorrowedBookIds().size() + "/3) =====");
        for (String id : m.getBorrowedBookIds()) {
            Book b = bookService.getBookById(id);
            if (b != null)
                System.out.println(b.getTitle());
        }
    }
}