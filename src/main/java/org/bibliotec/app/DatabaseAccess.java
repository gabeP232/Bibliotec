package org.bibliotec.app;

import org.apache.ibatis.jdbc.ScriptRunner;

import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DatabaseAccess {
    record Book(String title, String author, String isbn, String publisher, int year) {}
//    record Book(String title, String author, String isbn, String genre, String publisher, int year, int pages) {}
    record User(String username, String password) {}
//    record User(String name, String email, String username, String password) {}
    record Patron(String name, String phoneNum, String address, int id) {}
//    record Patron(String name, String email) {}
    record Loan(String checkoutID, String bookName, String patronID, String returnDate) {}
    record Admin(String username, String password) {}

    private static Connection connection;

    public static Connection connection() {
        // initialize database, creating tables if doesn't exist, open connection, etc...
        if (connection == null) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                try (var rootConnection = DriverManager.getConnection("jdbc:mysql://localhost:3306", "root", null)) {
                    new ScriptRunner(rootConnection).runScript(new InputStreamReader(DatabaseAccess.class.getResourceAsStream("bibliotec.sql")));
                }
                connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/bibliotec", "root", null);
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

    private static final Random random = new Random();

    public static List<Book> getBooks() {
        // return list of books
        List<Book> books = new ArrayList<>();
        try (var stmt = connection().prepareStatement("SELECT * FROM books")) {
            stmt.execute();
            while (stmt.getResultSet().next()) {
                String title = stmt.getResultSet().getString(1);
                String author = stmt.getResultSet().getString(2);
                String isbn = stmt.getResultSet().getString(3);
                String publisher = stmt.getResultSet().getString(4);
                int year = random.nextInt(34) + 1990;

                Book bk = new Book(title, author, isbn, publisher, year);
                books.add(bk);
//                System.out.println(bk);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return books;
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

                patrons.add(new Patron(name, phoneNum, address, id));
//                System.out.println(patrons.getLast());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return patrons;
    }

    public static void addPatrons(Patron patrons) {
        // add book to database
        try (var stmt = connection().prepareStatement("INSERT INTO patrons (name, phoneNum, address, patronID) VALUES (?, ?, ?, ?, ?)")) {

            // Set parameters
            stmt.setString(1, patrons.name);
            stmt.setString(2, patrons.phoneNum);
            stmt.setString(3, patrons.address);
            stmt.setInt(4, patrons.id);

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("A new Patron was added successfully.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static void removePatron(int id) {
        try (var stmt = connection().prepareStatement("DELETE FROM patrons WHERE patronID = ?")) {
            stmt.setInt(1, id);

            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Patron with ID " + id + " was deleted successfully.");
            } else {
                System.out.println("No Patron with ID" + id + " was found.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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
//                System.out.println("Username: " + username + ", Password: " + password);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return users;

    }

    public static List<Loan> getLoans() {
        List<Loan> loans = new ArrayList<>();
        try (var stmt = connection().prepareStatement("SELECT * FROM checkout")) {
            stmt.execute();
            while (stmt.getResultSet().next()) {
                String checkoutID = stmt.getResultSet().getString(1);
                String bookName = stmt.getResultSet().getString(2);
                String patronID = stmt.getResultSet().getString(2);
                String returnDate = stmt.getResultSet().getString(3);

                loans.add(new Loan(checkoutID, bookName, patronID, returnDate));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return loans;
    }

    public static List<Loan> getLoansForPatron(int patronIdent) {
        List<Loan> loans = new ArrayList<>();
        try (var stmt = connection().prepareStatement("SELECT * FROM checkout WHERE patronID = ?")) {
            stmt.setInt(1, patronIdent);
            stmt.execute();
            while (stmt.getResultSet().next()) {
                String checkoutID = stmt.getResultSet().getString(1);
                String bookName = stmt.getResultSet().getString(2);
                String patronID = stmt.getResultSet().getString(2);
                String returnDate = stmt.getResultSet().getString(3);

                loans.add(new Loan(checkoutID, bookName, patronID, returnDate));
//                System.out.println("Checkout ID: " + checkoutID + ", Book: " + title + ", Date: " + date);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return loans;
    }

    public static void addBook(Book book) {
        // add book to database
        try (var stmt = connection().prepareStatement("INSERT INTO books (bookName, author, isbn, publisher) VALUES (?, ?, ?, ?)")) {

            // Set parameters
            stmt.setString(1, book.title);
            stmt.setString(2, book.author);
            stmt.setString(3, book.isbn);
            stmt.setString(4, book.publisher);
//            stmt.setInt(5, book.year);
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

    public static void removeLoan(Loan loan) {

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
