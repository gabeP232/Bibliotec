package org.bibliotec.app;


import org.junit.jupiter.api.Test;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.xml.crypto.Data;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.*;

class DatabaseAccessTest {

    @Test
    void login() {
    }

    @Test
    void getBooks() throws SQLException {
        DatabaseAccess.getBooks();

    }

    @Test
    void doStuff() {
        System.out.println("Hello world!");
    }

//    @Test
//    void connect() {
//        DatabaseAccess.connection();
//    }

    @Test
    void getUsers() {
        DatabaseAccess.getUsers();
    }

    @Test
    void addbooks() {
        DatabaseAccess.Book bk = new DatabaseAccess.Book("test", "author", "isbn", "publisher", "Romance", 2);
        DatabaseAccess.addBook(bk);

    }

    @Test
    void deleteBooks() {
        DatabaseAccess.removeBook("isbn");
    }

    @Test
    void addPatrons() {
//        DatabaseAccess.User pt = new DatabaseAccess.User("Jerry47!", "Jerry Richards", "jerry@gmail", "123 SJ St.", "cheese");
    }

//    @Test
//    void deletePatrons() {
//        DatabaseAccess.removePatron(1);
//    }

//    @Test
//    void addHolds() {
//        DatabaseAccess.Hold hld = new DatabaseAccess.Hold("")
//        DatabaseAccess.addHold();
//    }

    @Test
    void addLoan() {
        DatabaseAccess.Loan loan;
        loan = new DatabaseAccess.Loan(1, "978-1982137274", "Toed", LocalDate.of(2024, 8, 16), LocalDate.of(2024, 9, 23), false);

    }


    @Test
    void passwordCheck() {
        String hashed = org.mindrot.jbcrypt.BCrypt.hashpw("password", org.mindrot.jbcrypt.BCrypt.gensalt(10));
        if (org.mindrot.jbcrypt.BCrypt.checkpw("password", hashed)) {
            System.out.println("Passwords match hash");
        }
        else {
            System.out.println("Passwords do not match");
        }
    }

}

