package org.bibliotec.app;

import org.apache.ibatis.jdbc.ScriptRunner;

import java.io.InputStreamReader;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DatabaseAccess {

    public record Genre(String name, String category) {}
    public record Book(String bookName, String author, String isbn, String publisher, String genre, int totalCopies) {}
    public record User(String userID, String fullName, String email, String address, String password, boolean isAdmin) {}
    public record Loan(int loanID, String isbn, String userID, LocalDate checkoutDate, LocalDate expectedReturnDate, boolean returned) {}
    public record Hold(String isbn, int holdID, String userID, LocalDate holdDate) {}

    private static Connection connection;

    public static Connection connection() {
        // initialize database, creating tables if doesn't exist, open connection, etc...
        if (connection == null) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                try (var rootConnection = DriverManager.getConnection("jdbc:mysql://localhost:3306", "root", System.getenv("DB_PASSWORD"))) {
                    new ScriptRunner(rootConnection).runScript(new InputStreamReader(DatabaseAccess.class.getResourceAsStream("bibliotec.sql")));
                }
                connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/bibliotec", "root", System.getenv("DB_PASSWORD"));
            } catch (ClassNotFoundException | SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return connection;
    }

    public static Optional<User> login(String username, String password) {
        // return true if success, false otherwise
        try (var stmt = connection().prepareStatement("SELECT * FROM users where userID = ? and password = ? ")) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? Optional.of(new User(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getBoolean(6)))
                    : Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Book> getBooks() {
        // return list of books
        List<Book> books = new ArrayList<>();
        try (var stmt = connection().prepareStatement("SELECT * FROM books")) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                books.add(new Book(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getInt(6)));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return books;
    }

    public static List<User> getPatrons() {
        var patrons = new ArrayList<User>();
        try (var stmt = connection().prepareStatement("SELECT * FROM users WHERE isAdmin = FALSE")) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                patrons.add(new User(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getBoolean(6)));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return patrons;
    }

    public static void addPatron(User patron) {
        try (var stmt = connection().prepareStatement("INSERT INTO users (userID, fullName, email, address, password) VALUES (?, ?, ?, ?, ?)")) {
            stmt.setString(1, patron.userID);
            stmt.setString(2, patron.fullName);
            stmt.setString(3, patron.email);
            stmt.setString(4, patron.address);
            stmt.setString(5, patron.password);

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("A new Patron was added successfully.");
            }
        } catch (SQLIntegrityConstraintViolationException e ) {
            throw new RuntimeException("Duplicate User ID", e);
        }  catch (SQLException e ) {
            throw new RuntimeException(e);
        }
    }

    public static void removePatron(String id) {
        try (var stmt = connection().prepareStatement("DELETE FROM users WHERE userID = ?")) {
            stmt.setString(1, id);

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
        var users = new ArrayList<User>();
        try (var stmt = connection().prepareStatement("SELECT * FROM users")) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                users.add(new User(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getBoolean(6)));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return users;

    }

    public static List<Hold> getHolds() {
        var holds = new ArrayList<Hold>();
        try (var stmt = connection().prepareStatement("SELECT * FROM holds")) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                holds.add(new Hold(rs.getString(1), rs.getInt(2), rs.getString(3), rs.getDate(4).toLocalDate()));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return holds;
    }

    public static List<Loan> getLoans() {
        var loans = new ArrayList<Loan>();
        try (var stmt = connection().prepareStatement("SELECT * FROM loans")) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                loans.add(new Loan(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getDate(4).toLocalDate(), rs.getDate(5).toLocalDate(), rs.getBoolean(6)));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return loans;
    }

    public static List<Loan> getLoansForPatron(String userID) {
        var loans = new ArrayList<Loan>();
        try (var stmt = connection().prepareStatement("SELECT * FROM loans WHERE userID = ?")) {
            stmt.setString(1, userID);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                loans.add(new Loan(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getDate(4).toLocalDate(), rs.getDate(5).toLocalDate(), rs.getBoolean(6)));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return loans;
    }

    public static List<Loan> getLoansForBook(String isbn) {
        var loans = new ArrayList<Loan>();
        try (var stmt = connection().prepareStatement("SELECT * FROM loans WHERE isbn = ?")) {
            stmt.setString(1, isbn);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                loans.add(new Loan(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getDate(4).toLocalDate(), rs.getDate(5).toLocalDate(), rs.getBoolean(6)));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return loans;
    }

    public static User getPatronForLoan(Loan loan) {
        try (var stmt = connection().prepareStatement("SELECT * FROM users WHERE userID = ?")) {
            stmt.setString(1, loan.userID);
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? new User(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getBoolean(6))
                    : null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void addBook(Book book) {
        // add book to database
        try (var stmt = connection().prepareStatement("INSERT INTO books (bookName, author, isbn, publisher, genre, totalCopies) VALUES (?, ?, ?, ?, ?, ?)")) {
            stmt.setString(1, book.bookName);
            stmt.setString(2, book.author);
            stmt.setString(3, book.isbn);
            stmt.setString(4, book.publisher);
            stmt.setString(5, book.genre);
            stmt.setInt(6, book.totalCopies);
            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("A new book was added successfully.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
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

    public static int getAvailableCopies(String isbn) {
        try (var checkedOutCopiesStmt = connection().prepareStatement("SELECT COUNT(*) FROM loans WHERE returned = FALSE AND isbn = ?")) {
            checkedOutCopiesStmt.setString(1, isbn);
            ResultSet rs = checkedOutCopiesStmt.executeQuery();
            int checkedOutCopies = rs.next() ? rs.getInt(1) : 0;
            try (var totalCopiesStmt = connection().prepareStatement("SELECT totalCopies FROM books WHERE isbn = ?")) {
                totalCopiesStmt.setString(1, isbn);
                rs = totalCopiesStmt.executeQuery();
                int totalCopies = rs.next() ? rs.getInt(1) : 0;
                return totalCopies - checkedOutCopies;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isBookReturned(String isbn) {
        return getAvailableCopies(isbn) > 0;
    }

    public static Hold addHold(Hold hold) {
        if (!isBookReturned(hold.isbn)) {
            try (var stmt = connection().prepareStatement("INSERT INTO holds (isbn, userID, holdDate) VALUES (?, ?, ?)")) {
                stmt.setString(1, hold.isbn);
                stmt.setString(2, hold.userID);
                stmt.setDate(3, Date.valueOf(hold.holdDate));

                int rowsInserted = stmt.executeUpdate();
                if (rowsInserted > 0) {
                    System.out.println("A new hold was added successfully.");
                    try (var st = connection().prepareStatement("SELECT holdID FROM holds WHERE isbn = ? AND userID = ? AND holdDate = ?")) {
                        st.setString(1, hold.isbn);
                        st.setString(2, hold.userID);
                        st.setDate(3, Date.valueOf(hold.holdDate));
                        return hold;
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    public static Loan addLoan(Loan loan) {
        if (!isBookReturned(loan.isbn)) {
            try (var stmt = connection().prepareStatement("INSERT INTO loans (isbn, userID, checkoutDate, expectedReturnDate, returned) VALUES (?, ?, ?, ?,?)")) {
                stmt.setString(1, loan.isbn);
                stmt.setString(2, loan.userID);
                stmt.setDate(3, Date.valueOf(loan.checkoutDate));
                stmt.setDate(4, Date.valueOf(loan.expectedReturnDate));
                stmt.setBoolean(5, loan.returned);

                int rowsInserted = stmt.executeUpdate();
                if (rowsInserted > 0) {
                    System.out.println("A new loan was added successfully.");
                    try (var st = connection().prepareStatement("SELECT loanID FROM Loans WHERE isbn = ? AND userID = ? AND returned = ?")) {
                        st.setString(1, loan.isbn);
                        st.setString(2, loan.userID);
                        st.setBoolean(3, loan.returned);
                        return loan;
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }


    //find by loan id and set returned to true
    public static void removeLoan(Loan loan) {
        try (var stmt = connection().prepareStatement("DELETE FROM loans WHERE isbn = ?")) {
            stmt.setString(1, loan.isbn);

            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Book with ISBN " + loan.isbn + " was successfully returned.");

            } else {
                System.out.println("No book with ISBN " + loan.isbn + " was found in loans.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
