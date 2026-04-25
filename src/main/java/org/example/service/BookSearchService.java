package org.example.service;

import org.example.model.Book;
import org.example.model.Novel;
import org.example.model.ReferenceBook;
import org.example.model.TextBook;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/*
 Lớp dịch vụ tìm kiếm sách
 Yêu cầu:
 Linear search (tìm tuần tự)
 Clean code, không lặp code
 Nâng cao: dùng Predicate / Lambda
*/
public class BookSearchService {
    private BookService bookService;

    public BookSearchService(BookService bookService) {
        this.bookService = bookService;
    }
    // Tìm kiếm theo title (không phân biệt hoa thường) và Sử dụng Linear Search: duyệt từ đầu đến cuối danh sách
    public List<Book> searchByTitle(String keyword) {
        List<Book> result = new ArrayList<>();

        if (keyword == null || keyword.trim().isEmpty()) {
            return result;
        }

        String lowerKeyword = keyword.toLowerCase().trim();

        // // Linear search: duyệt từng phần tử
        for (Book book : bookService.getAllBooks()) {
            if (book.getTitle().toLowerCase().contains(lowerKeyword)) {
                result.add(book);
            }
        }

        return result;
    }

    // Tìm kiếm theo author (không phân biệt hoa thường), sử dụng Linear Search
    public List<Book> searchByAuthor(String keyword) {
        List<Book> result = new ArrayList<>();

        if (keyword == null || keyword.trim().isEmpty()) {
            return result;
        }

        String lowerKeyword = keyword.toLowerCase().trim();

        // Linear search
        for (Book book : bookService.getAllBooks()) {
            if (book.getAuthor().toLowerCase().contains(lowerKeyword)) {
                result.add(book);
            }
        }

        return result;
    }

    // Tìm kiếm theo category (loại sách). Category có thể: TextBook, Novel, ReferenceBook
    public List<Book> searchByCategory(String category) {
        List<Book> result = new ArrayList<>();

        if (category == null || category.trim().isEmpty()) {
            return result;
        }

        String lowerCategory = category.toLowerCase().trim();

        // Linear search sử dụng instanceof để xác định category
        for (Book book : bookService.getAllBooks()) {
            String bookCategory = getBookCategory(book);
            if (bookCategory.toLowerCase().contains(lowerCategory)) {
                result.add(book);
            }
        }

        return result;
    }

    // Lấy tên category của sách (dùng instanceof)
    private String getBookCategory(Book book) {
        if (book instanceof TextBook) {
            return "TextBook";
        } else if (book instanceof Novel) {
            return "Novel";
        } else if (book instanceof ReferenceBook) {
            return "ReferenceBook";
        }
        return "Unknown";
    }

    // Tìm kiếm tổng quát với Predicate (không lặp code)
    public List<Book> search(Predicate<Book> condition) {
        List<Book> result = new ArrayList<>();

        for (Book book : bookService.getAllBooks()) {
            if (condition.test(book)) {
                result.add(book);
            }
        }

        return result;
    }

    // Tìm kiếm theo title dùng Predicate
    public List<Book> searchByTitleWithPredicate(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return new ArrayList<>();
        }

        String lowerKeyword = keyword.toLowerCase().trim();
        return search(book -> book.getTitle().toLowerCase().contains(lowerKeyword));
    }

    // Tìm kiếm theo author dùng Predicate
    public List<Book> searchByAuthorWithPredicate(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return new ArrayList<>();
        }

        String lowerKeyword = keyword.toLowerCase().trim();
        return search(book -> book.getAuthor().toLowerCase().contains(lowerKeyword));
    }

    // Tìm kiếm theo category dùng Predicate
    public List<Book> searchByCategoryWithPredicate(String category) {
        if (category == null || category.trim().isEmpty()) {
            return new ArrayList<>();
        }

        String lowerCategory = category.toLowerCase().trim();
        return search(book -> getBookCategory(book).toLowerCase().contains(lowerCategory));
    }

    // Tìm kiếm kết hợp nhiều điều kiện (ví dụ: title và author)
    public List<Book> searchCombined(String titleKeyword, String authorKeyword) {
        Predicate<Book> titleCondition = book -> true;
        Predicate<Book> authorCondition = book -> true;

        if (titleKeyword != null && !titleKeyword.trim().isEmpty()) {
            String lowerTitle = titleKeyword.toLowerCase().trim();
            titleCondition = book -> book.getTitle().toLowerCase().contains(lowerTitle);
        }

        // Thêm điều kiện author nếu có
        if (authorKeyword != null && !authorKeyword.trim().isEmpty()) {
            String lowerAuthor = authorKeyword.toLowerCase().trim();
            authorCondition = book -> book.getAuthor().toLowerCase().contains(lowerAuthor);
        }

        // Kết hợp hai điều kiện bằng and
        return search(titleCondition.and(authorCondition));
    }
}
