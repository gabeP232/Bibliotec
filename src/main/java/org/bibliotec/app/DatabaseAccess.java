package org.bibliotec.app;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public class DatabaseAccess {

    record Book(String title, String author, String isbn, String genre, String publisher, int year, int pages) {}
    record User(String name, String email, String username, String password) {}
    record Patron(String name, String email) {}
    record Loan(User user, Book book, String date) {}
    record Admin(String username, String password) {}

    private static Connection connection;

    private static Connection connection() {
        // initialize database, creating tables if doesn't exist, open connection, etc...
        if (connection == null) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/bibliotec", "root", "An15no35gabe!");
                System.out.println(connection);
            } catch (ClassNotFoundException | SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return connection;
    }

    public static boolean login(String username, String password) {
        // return true if success, false otherwise
        try (var stmt = connection().prepareStatement("SELECT * FROM LOGIN where username = ? and password = ? ")) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            return stmt.executeQuery().next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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
