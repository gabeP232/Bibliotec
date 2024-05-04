package org.bibliotec.app;

import org.apache.ibatis.jdbc.ScriptRunner;

import java.io.InputStreamReader;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DatabaseAccess {

    private static Connection connection;

    public static Connection connection() {
        // initialize database, creating tables if doesn't exist, open connection, etc...
        if (connection == null) {
            try {
                //noinspection Java9ReflectionClassVisibility
                Class.forName("com.mysql.cj.jdbc.Driver");
                try (var rootConnection = DriverManager.getConnection("jdbc:mysql://localhost:3306", "root", System.getenv("DB_PASSWORD"))) {
                    //noinspection DataFlowIssue
                    new ScriptRunner(rootConnection).runScript(new InputStreamReader(DatabaseAccess.class.getResourceAsStream("bibliotec.sql")));
                }
                connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/bibliotec", "root", System.getenv("DB_PASSWORD"));
            } catch (ClassNotFoundException | SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return connection;
    }

    interface Updatable {
        void updateDB();
    }

    public record Genre(String name, String category) {}
    public record Book(String bookName, String author, String isbn, String publisher, String genre, int totalCopies) implements Updatable {
        public void updateDB() {
            try (var stmt = connection().prepareStatement("UPDATE books SET bookName = ?, author = ?, publisher = ?, genre = ?, totalCopies = ? WHERE isbn = ?")) {
                stmt.setString(1, bookName);
                stmt.setString(2, author);
                stmt.setString(3, publisher);
                stmt.setString(4, genre);
                stmt.setInt(5, totalCopies);
                stmt.setString(6, isbn);

                int rowsUpdated = stmt.executeUpdate();
                if (rowsUpdated > 0) {
                    System.out.println("Book with ISBN " + isbn + " was updated successfully.");
                } else {
                    throw new RuntimeException("No book with ISBN " + isbn + " was found.");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        public static Book empty() {
            return new Book("<edit>", "<edit>", Utils.makeISBN(), "<edit>", "Reference", 0);
        }
    }
    public record User(String userID, String fullName, String email, String address, String password, boolean isAdmin) implements Updatable {
        public void updateDB() {
            try (var stmt = connection().prepareStatement("UPDATE users SET fullName = ?, email = ?, address = ?, password = ? WHERE userID = ?")) {
                stmt.setString(1, fullName);
                stmt.setString(2, email);
                stmt.setString(3, address);
                stmt.setString(4, password);
                stmt.setString(5, userID);

                int rowsUpdated = stmt.executeUpdate();
                if (rowsUpdated > 0) {
                    System.out.println("User with ID " + userID + " was updated successfully.");
                } else {
                    throw new RuntimeException("No user with ID " + userID + " was found.");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        public static User empty() {
            return new User("<edit>", "<edit>", "<edit>", "<edit>", "<edit>", false);
        }
    }
    public record Loan(int loanID, String isbn, String userID, Date checkoutDate, Date expectedReturnDate, boolean returned) implements Updatable {
        public void updateDB() {
            try (var stmt = connection().prepareStatement("UPDATE loans SET isbn = ?, userID = ?, checkoutDate = ?, expectedReturnDate = ?, returned = ? WHERE loanID = ?")) {
                stmt.setString(1, isbn);
                stmt.setString(2, userID);
                stmt.setDate(3, checkoutDate);
                stmt.setDate(4, expectedReturnDate);
                stmt.setBoolean(5, returned);
                stmt.setInt(6, loanID);

                int rowsUpdated = stmt.executeUpdate();
                if (rowsUpdated > 0) {
                    System.out.println("Loan with ID " + loanID + " was updated successfully.");
                } else {
                    throw new RuntimeException("No loan with ID " + loanID + " was found.");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        public static Loan empty() {
            return new Loan(-1, "<edit>", "<edit>", Date.valueOf(LocalDate.now()), Date.valueOf(LocalDate.now()), false);
        }
    }
    public record Hold(String isbn, int holdID, String userID, Date holdDate) implements Updatable {
        public void updateDB() {
            try (var stmt = connection().prepareStatement("UPDATE holds SET isbn = ?, userID = ?, holdDate = ? WHERE holdID = ?")) {
                stmt.setString(1, isbn);
                stmt.setString(2, userID);
                stmt.setDate(3, holdDate);
                stmt.setInt(4, holdID);

                int rowsUpdated = stmt.executeUpdate();
                if (rowsUpdated > 0) {
                    System.out.println("Hold with ID " + holdID + " was updated successfully.");
                } else {
                    throw new RuntimeException("No hold with ID " + holdID + " was found.");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        public static Hold empty() {
            return new Hold("<edit>", -1, "<edit>", Date.valueOf(LocalDate.now()));
        }
    }

    public static Optional<User> login(String username, String password) {
        try (var stmt = connection().prepareStatement("SELECT * FROM users where userID = ?")) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next() && BCrypt.checkpw(password, rs.getString("password"))) {
                return Optional.of(new User(rs.getString("userID"), rs.getString("fullName"), rs.getString("email"), rs.getString("address"), rs.getString("password"), rs.getBoolean("isAdmin")));
            }
            else {
                return Optional.empty();
            }
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
                books.add(new Book(rs.getString("bookName"), rs.getString("author"), rs.getString("isbn"), rs.getString("publisher"), rs.getString("genre"), rs.getInt("totalCopies")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return books;
    }
    
    public static Optional<User> getPatron(String userID) {
        try (var stmt = connection().prepareStatement("SELECT * FROM users WHERE isAdmin = FALSE AND userID = ?")) {
            stmt.setString(1, userID);
            ResultSet rs = stmt.executeQuery();
            return rs.next() ?
                    Optional.of(new User(rs.getString("userID"), rs.getString("fullName"), rs.getString("email"), rs.getString("address"), rs.getString("password"), false))
                    : Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<User> getPatrons() {
        var patrons = new ArrayList<User>();
        try (var stmt = connection().prepareStatement("SELECT * FROM users WHERE isAdmin = FALSE")) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                patrons.add(new User(rs.getString("userID"), rs.getString("fullName"), rs.getString("email"), rs.getString("address"), rs.getString("password"), false));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return patrons;
    }

    public static void addPatron(User patron) {
        try (var stmt = connection().prepareStatement("INSERT INTO users (userID, fullName, email, address, password) VALUES (?, ?, ?, ?, ?)")) {
            String hashed = BCrypt.hashpw(patron.password, BCrypt.gensalt());
            stmt.setString(1, patron.userID);
            stmt.setString(2, patron.fullName);
            stmt.setString(3, patron.email);
            stmt.setString(4, patron.address);
            stmt.setString(5, hashed);

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("A new Patron was added successfully.");
            }
            if (BCrypt.checkpw(patron.password, hashed)) {
                System.out.println("Passwords match hash");
            } else {
                System.out.println("Passwords do not match");
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

    public static List<Hold> getHolds() {
        var holds = new ArrayList<Hold>();
        try (var stmt = connection().prepareStatement("SELECT * FROM holds")) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                holds.add(new Hold(rs.getString("isbn"), rs.getInt("holdID"), rs.getString("userID"), rs.getDate("holdDate")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return holds;
    }

    public static List<Hold> getHoldsForPatron(String userID) {
        var holds = new ArrayList<Hold>();
        try (var stmt = connection().prepareStatement("SELECT * FROM holds WHERE userID = ?")) {
            stmt.setString(1, userID);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                holds.add(new Hold(rs.getString("isbn"), rs.getInt("holdID"), rs.getString("userID"), rs.getDate("holdDate")));
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
                loans.add(new Loan(rs.getInt("loanID"), rs.getString("isbn"), rs.getString("userID"), rs.getDate("checkoutDate"), rs.getDate("expectedReturnDate"), rs.getBoolean("returned")));
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
                loans.add(new Loan(rs.getInt("loanID"), rs.getString("isbn"), rs.getString("userID"), rs.getDate("checkoutDate"), rs.getDate("expectedReturnDate"), rs.getBoolean("returned")));
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
                loans.add(new Loan(rs.getInt("loanID"), rs.getString("isbn"), rs.getString("userID"), rs.getDate("checkoutDate"), rs.getDate("expectedReturnDate"), rs.getBoolean("returned")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return loans;
    }

    public static Optional<User> getPatronForLoan(Loan loan) {
        try (var stmt = connection().prepareStatement("SELECT * FROM users WHERE userID = ?")) {
            stmt.setString(1, loan.userID);
            ResultSet rs = stmt.executeQuery();
            return rs.next() ?
                    Optional.of(new User(rs.getString("userID"), rs.getString("fullName"), rs.getString("email"), rs.getString("address"), rs.getString("password"), rs.getBoolean("isAdmin")))
                    : Optional.empty();
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
                int totalCopies = rs.next() ? rs.getInt("totalCopies") : 0;
                return Math.max(totalCopies - checkedOutCopies, 0);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isBookAvailable(String isbn) {
        return getAvailableCopies(isbn) > 0;
    }

    public static Optional<Hold> addHold(Hold hold) {
        if (!isBookAvailable(hold.isbn)) {
            try (var stmt = connection().prepareStatement("INSERT INTO holds (isbn, userID, holdDate) VALUES (?, ?, ?)")) {
                stmt.setString(1, hold.isbn);
                stmt.setString(2, hold.userID);
                stmt.setDate(3, hold.holdDate);

                int rowsInserted = stmt.executeUpdate();
                if (rowsInserted > 0) {
                    System.out.println("A new hold was added successfully.");
                    try (var st = connection().prepareStatement("SELECT holdID FROM holds WHERE isbn = ? AND userID = ? AND holdDate = ?")) {
                        st.setString(1, hold.isbn);
                        st.setString(2, hold.userID);
                        st.setDate(3, hold.holdDate);

                        ResultSet rs = st.executeQuery();
                        return rs.next() ? Optional.of(new Hold(hold.isbn, rs.getInt("holdID"), hold.userID, hold.holdDate))
                                : Optional.empty();
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return Optional.empty();
    }

    public static Optional<Loan> addLoan(Loan loan) {
        if (isBookAvailable(loan.isbn)) {
            try (var stmt = connection().prepareStatement("INSERT INTO loans (isbn, userID, checkoutDate, expectedReturnDate, returned) VALUES (?, ?, ?, ?,?)")) {
                stmt.setString(1, loan.isbn);
                stmt.setString(2, loan.userID);
                stmt.setDate(3, loan.checkoutDate);
                stmt.setDate(4, loan.expectedReturnDate);
                stmt.setBoolean(5, loan.returned);

                int rowsInserted = stmt.executeUpdate();
                if (rowsInserted > 0) {
                    try (var st = connection().prepareStatement("SELECT loanID FROM Loans WHERE isbn = ? AND userID = ? AND checkoutDate = ? AND returned = ?")) {
                        st.setString(1, loan.isbn);
                        st.setString(2, loan.userID);
                        st.setDate(3, loan.checkoutDate);
                        st.setBoolean(4, loan.returned);

                        ResultSet rs = st.executeQuery();
                        return rs.next() ?
                                Optional.of(new Loan(rs.getInt("loanID"), loan.isbn, loan.userID, loan.checkoutDate, loan.expectedReturnDate, loan.returned))
                                : Optional.empty();
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return Optional.empty();
    }

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

    public static void removeHold(int holdID) {
        try (var stmt = connection().prepareStatement("DELETE FROM holds WHERE holdID = ?")) {
            stmt.setInt(1, holdID);

            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Hold with ID " + holdID + " was successfully removed.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
