package org.bibliotec.app;

import java.util.List;

public class DatabaseAccess {

    record Book(String title, String author, String isbn, String genre, String publisher, int year, int pages) {}
    record User(String name, String email, String username, String password) {}
    record Patron(String name, String email) {}
    record Loan(User user, Book book, String date) {}

    public static void initialize() {
        // initialize database, creating tables if doesn't exist, open connection, etc...
    }

    public static boolean login(String username, String password) {
        // return true if success, false otherwise
        return username.equals("admin") && password.equals("password");
    }

    public static List<Book> getBooks() {
        // return list of books
        return List.of(
            new Book("The Great Gatsby", "F. Scott Fitzgerald", "9780743273565", "Novel", "Scribner", 1925, 180),
            new Book("To Kill a Mockingbird", "Harper Lee", "9780061120084", "Novel", "J. B. Lippincott & Co.", 1960, 281)
        );
    }

    public static List<User> getUsers() {
        // return list of users
        return List.of(
            new User("John Doe", "john@doe.com", "johndoe", "password")
        );
    }

    public static List<Patron> getPatrons() {
        return List.of(
            new Patron("Bob", "bob@gmail.com"));
    }

    public void addBook(Book book) {
        // add book to database
    }

}
