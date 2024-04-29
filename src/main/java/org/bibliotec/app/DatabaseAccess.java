package org.bibliotec.app;

import org.apache.ibatis.jdbc.ScriptRunner;

import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseAccess {
    record Book(String title, String author, String isbn, String publisher, int year) {}
//    record Book(String title, String author, String isbn, String genre, String publisher, int year, int pages) {}
    record User(String username, String password) {}
//    record User(String name, String email, String username, String password) {}
    record Patron(String name, String phoneNum, String address, int id) {}
//    record Patron(String name, String email) {}
    record Loan(User user, Book book, String date) {}
    record Admin(String username, String password) {}

    private static Connection connection;

    public static Connection connection() {
        // initialize database, creating tables if doesn't exist, open connection, etc...
        if (connection == null) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                try (var rootConnection = DriverManager.getConnection("jdbc:mysql://localhost:3306", "root", "An15no35gabe!")) {
                    new ScriptRunner(rootConnection).runScript(new InputStreamReader(DatabaseAccess.class.getResourceAsStream("bibliotec.sql")));
                }
                connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/bibliotec", "root", "An15no35gabe!");
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

    public static List<Book> getBooks() throws SQLException {
        // return list of books
        List<Book> books = new ArrayList<>();
        try (var stmt = connection().prepareStatement("SELECT * FROM books")) {
            stmt.execute();
            while (stmt.getResultSet().next()) {
                String title = stmt.getResultSet().getString(1);
                String author = stmt.getResultSet().getString(2);
                String isbn = stmt.getResultSet().getString(3);
                String publisher = stmt.getResultSet().getString(4);
                int year = stmt.getResultSet().getInt(5);

                Book bk = new Book(title, author, isbn, publisher, year);
                books.add(bk);
                System.out.println("Title: " + title + ", Author: " + author + ", ISBN: " + isbn + ", Publisher: " + publisher + ", Year: " + year);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return books;
//        return List.of(
//            new Book("The Great Gatsby", "F. Scott Fitzgerald", "9780743273565", "Novel", "Scribner", 1925, 180),
//            new Book("To Kill a Mockingbird", "Harper Lee", "9780061120084", "Novel", "J. B. Lippincott & Co.", 1960, 281)
//        );
    }

    public static List<Patron> getPatrons() {
        // return list of patrons
        List<Patron> patrons = new ArrayList<>();
        try (var stmt = connection().prepareStatement("SELECT * FROM patrons")) {
            stmt.execute();
            while (stmt.getResultSet().next()) {
                String name = stmt.getResultSet().getString(1);
                String phoneNum = stmt.getResultSet().getString(2);
                String address = stmt.getResultSet().getString(3);
                int id = stmt.getResultSet().getInt(4);

                Patron ptr = new Patron(name, phoneNum, address, id);
                patrons.add(ptr);
                System.out.println("Name: " + id + ", Phone: " + phoneNum + ", Address: " + address + ", Name: " + id);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return patrons;

//        return List.of(
//                new Patron("Bob", "bob@gmail.com"));
    }

    public static List<User> getUsers() {
        List<User> users = new ArrayList<>();
        try (var stmt = connection().prepareStatement("SELECT * FROM login")) {
            stmt.execute();
            while (stmt.getResultSet().next()) {
                String username = stmt.getResultSet().getString(1);
                String password = stmt.getResultSet().getString(2);

                User use = new User(username, password);
                users.add(use);
                System.out.println("Username: " + username + ", Password: " + password);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return users;

//        return List.of(
//            new User("John Doe", "john@doe.com", "johndoe", "password")
//        );
    }

    public static void addBook(Book book) {
        // add book to database
        try (var stmt = connection().prepareStatement("INSERT INTO books (bookName, author, isbn, publisher, year) VALUES (?, ?, ?, ?, ?)")) {

            // Set parameters\\
            stmt.setString(1, book.title);
            stmt.setString(2, book.author);
            stmt.setString(3, book.isbn);
            stmt.setString(4, book.publisher);
            stmt.setInt(5, book.year);
//            stmt.getResultSet().getString(1);
//            stmt.getResultSet().getString(2);
//            stmt.getResultSet().getString(3);
//            stmt.getResultSet().getString(4);
//            stmt.getResultSet().getInt(5);
            // Execute the statement
            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("A new book was added successfully.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static void removeBook(String isbn) {
        try (var stmt = connection().prepareStatement("DELETE FROM books WHERE isbn = ?")) {

            stmt.setString(1, isbn);

            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Book with ISBN " + isbn + " was deleted successfully.");
            } else {
                System.out.println("No book with ISBN " + isbn + " was found.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean registerUser(String username, String password) {
        try (var stmt = connection().prepareStatement("INSERT INTO LOGIN (username, password) VALUES (?, ?)")) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            return stmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
