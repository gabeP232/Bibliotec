package org.bibliotec.app;


import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class DatabaseAccessTest {

    @Test
    void login() {
        assertThat(DatabaseAccess.login("admin", "password")).isTrue();
        assertThat(DatabaseAccess.login("admin", "password")).isFalse();
    }

    @Test
    void getBooks() {
        assertThat(DatabaseAccess.getBooks()).isNotEmpty();

    }

    @Test
    void doStuff() {
        System.out.println("Hello world!");
    }
}