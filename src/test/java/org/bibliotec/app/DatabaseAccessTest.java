package org.bibliotec.app;


import org.bibliotec.app.DatabaseAccess.Book;
import org.bibliotec.app.DatabaseAccess.Loan;
import org.bibliotec.app.DatabaseAccess.User;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

import java.sql.Date;

class DatabaseAccessTest {

    @Test
    void login() {
    }

    @Test
    void getBooks() {
        assertThat(DatabaseAccess.getBooks()).isNotEmpty();
    }

    @Test
    void doStuff() {
        System.out.println("Hello world!");
    }

    @Test
    void getUsers() {
        DatabaseAccess.getUsers();
    }

    @Test
    void addbooks() {
        Book bk = new Book("test", "author", "isbn", "publisher", "Romance", 2);
        DatabaseAccess.addBook(bk);

    }

    @Test
    void deleteBooks() {
        DatabaseAccess.removeBook("isbn");
    }

    @Test
    void addPatrons() {
//        User pt = new User("Jerry47!", "Jerry Richards", "jerry@gmail", "123 SJ St.", "cheese", false);
    }

    @Test
    void addLoan() {
        Loan loan = new Loan(1, "978-1982137274", "Toed", Date.valueOf("2023-8-16"), Date.valueOf("2024-9-23"), false);
    }


    @Test
    void passwordCheck() {
        String hashed = BCrypt.hashpw("password", BCrypt.gensalt());
        assertThat(BCrypt.checkpw("password", hashed)).isTrue();
    }

}

