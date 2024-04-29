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
        DatabaseAccess.Book bk = new DatabaseAccess.Book("test", "author", "isbn", "publisher", 1983);
        DatabaseAccess.addBook(bk);

    }

    @Test
    void deleteBooks() {
        DatabaseAccess.removeBook("isbn");
    }
}

