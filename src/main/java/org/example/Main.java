package org.example;

import org.example.controller.LibraryController;
import org.example.model.Book;
import org.example.model.Librarian;
import org.example.model.ReferenceBook;
import org.example.model.TextBook;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        LibraryController controller = new LibraryController();
        controller.start();
    }
}
