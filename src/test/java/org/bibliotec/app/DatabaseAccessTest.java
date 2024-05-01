package org.bibliotec.app;


import org.junit.jupiter.api.Test;

import javax.xml.crypto.Data;
import java.sql.SQLException;

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
}

