package org.bibliotec.app;


import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

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
    void connect() {
        DatabaseAccess.connection();
    }
}

