package org.example.service;

import org.example.model.Book;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

//Lớp dịch vụ sắp xếp sách, yêu cầu: Dùng Comparator, Dùng Collections.sort()
public class BookSortService {
    private BookService bookService;

    public BookSortService(BookService bookService) {
        this.bookService = bookService;
    }

    // Sắp xếp theo title (A-Z)
    public List<Book> sortByTitle() {
        List<Book> bookList = new ArrayList<>(bookService.getAllBooks());

        // Comparator sắp xếp theo title (không phân biệt hoa thường)
        Collections.sort(bookList, new Comparator<Book>() {
            @Override
            public int compare(Book b1, Book b2) {
                return b1.getTitle().compareToIgnoreCase(b2.getTitle());
            }
        });

        return bookList;
    }

    // Sắp xếp theo title dùng Lambda
    public List<Book> sortByTitleWithLambda() {
        List<Book> bookList = new ArrayList<>(bookService.getAllBooks());
        bookList.sort((b1, b2) -> b1.getTitle().compareToIgnoreCase(b2.getTitle()));
        return bookList;
    }

    // Sắp xếp theo năm xuất bản (tăng dần và từ cũ lên trước)
    public List<Book> sortByYearAscending() {
        List<Book> bookList = new ArrayList<>(bookService.getAllBooks());

        Collections.sort(bookList, new Comparator<Book>() {
            @Override
            public int compare(Book b1, Book b2) {
                return Integer.compare(b1.getYear(), b2.getYear());
            }
        });

        return bookList;
    }

    // Sắp xếp theo năm xuất bản (giảm dần từ mới lên trước)
    public List<Book> sortByYearDescending() {
        List<Book> bookList = new ArrayList<>(bookService.getAllBooks());
        bookList.sort((b1, b2) -> Integer.compare(b2.getYear(), b1.getYear()));
        return bookList;
    }

    // Sắp xếp theo số lượng (tăng dần từ ít lên trước)
    public List<Book> sortByQuantityAscending() {
        List<Book> bookList = new ArrayList<>(bookService.getAllBooks());

        Collections.sort(bookList, new Comparator<Book>() {
            @Override
            public int compare(Book b1, Book b2) {
                return Integer.compare(b1.getQuantity(), b2.getQuantity());
            }
        });

        return bookList;
    }

    // Sắp xếp theo số lượng (giảm dần từ nhiều lên trước)
    public List<Book> sortByQuantityDescending() {
        List<Book> bookList = new ArrayList<>(bookService.getAllBooks());
        bookList.sort((b1, b2) -> Integer.compare(b2.getQuantity(), b1.getQuantity()));
        return bookList;
    }
    // Sắp xếp theo title trước nếu title giống nhau thì theo năm
    public List<Book> sortByTitleThenYear() {
        List<Book> bookList = new ArrayList<>(bookService.getAllBooks());

        Collections.sort(bookList, new Comparator<Book>() {
            @Override
            public int compare(Book b1, Book b2) {
                // So sánh title trước
                int titleCompare = b1.getTitle().compareToIgnoreCase(b2.getTitle());
                if (titleCompare != 0) {
                    return titleCompare;
                }

                // Nếu title giống nhau => so sánh năm
                return Integer.compare(b1.getYear(), b2.getYear());
            }
        });

        return bookList;
    }

    // Sắp xếp theo năm trước, sau đó mới đến số lượng
    public List<Book> sortByYearThenQuantity() {
        List<Book> bookList = new ArrayList<>(bookService.getAllBooks());

        bookList.sort((b1, b2) -> {
            int yearCompare = Integer.compare(b1.getYear(), b2.getYear());
            if (yearCompare != 0) {
                return yearCompare;
            }
            return Integer.compare(b1.getQuantity(), b2.getQuantity());
        });

        return bookList;
    }

    //Sắp xếp theo số lượng giảm dần, sau đó theo title, sách nhiều nhất lên đầu, nếu bằng nhau thì sort theo title
    public List<Book> sortByQuantityDescThenTitle() {
        List<Book> bookList = new ArrayList<>(bookService.getAllBooks());

        bookList.sort((b1, b2) -> {
            // Số lượng giảm dần
            int quantityCompare = Integer.compare(b2.getQuantity(), b1.getQuantity());
            if (quantityCompare != 0) {
                return quantityCompare;
            }
            // Title tăng dần
            return b1.getTitle().compareToIgnoreCase(b2.getTitle());
        });

        return bookList;
    }

}
