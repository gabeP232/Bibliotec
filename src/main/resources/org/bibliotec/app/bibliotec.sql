CREATE DATABASE IF NOT EXISTS bibliotec;
CREATE SCHEMA IF NOT EXISTS bibliotec;
USE bibliotec;

CREATE TABLE IF NOT EXISTS genre(
    name     VARCHAR(45) NOT NULL,
    category VARCHAR(45) NULL DEFAULT NULL,
    PRIMARY KEY (name)
);

CREATE TABLE IF NOT EXISTS books(
    bookName    VARCHAR(45) NOT NULL,
    author      VARCHAR(45) NOT NULL,
    isbn        VARCHAR(45) NOT NULL,
    publisher   VARCHAR(45) NOT NULL,
    genre       VARCHAR(45) NOT NULL,
    totalCopies INT DEFAULT 1,
    PRIMARY KEY (isbn),
    FOREIGN KEY (genre) REFERENCES genre (name)
);

CREATE TABLE IF NOT EXISTS users(
    userID   VARCHAR(45) NOT NULL,
    fullName VARCHAR(45) NOT NULL,
    email    VARCHAR(45) NOT NULL,
    address  VARCHAR(45) NOT NULL,
    password VARCHAR(45) NOT NULL,
    isAdmin  BOOLEAN     NOT NULL DEFAULT FALSE,
    PRIMARY KEY (userID)
);

CREATE TABLE IF NOT EXISTS loans(
    loanID             INT         NOT NULL,
    isbn               VARCHAR(50) NOT NULL,
    userID             VARCHAR(45) NOT NULL,
    checkoutDate       DATE        NOT NULL,
    expectedReturnDate DATE        NOT NULL,
    returned           BOOLEAN,
    PRIMARY KEY (loanID),
    FOREIGN KEY (userID) REFERENCES users (userID),
    FOREIGN KEY (isbn) REFERENCES books (isbn)
);

CREATE TABLE IF NOT EXISTS holds(
    isbn   VARCHAR(45) NOT NULL,
    holdID INT         NOT NULL,
    userID VARCHAR(45) NOT NULL,
    holdDate DATE      NOT NULL,
    PRIMARY KEY (holdID),
    FOREIGN KEY (userID) REFERENCES users (userID),
    FOREIGN KEY (isbn) REFERENCES books (isbn)
);

-- Inserting data into the tables
INSERT IGNORE INTO genre (name, category) VALUES ('Fantasy', 'Fiction');
INSERT IGNORE INTO genre (name, category) VALUES ('Science Fiction', 'Fiction');
INSERT IGNORE INTO genre (name, category) VALUES ('Mystery', 'Fiction');
INSERT IGNORE INTO genre (name, category) VALUES ('Romance', 'Fiction');
INSERT IGNORE INTO genre (name, category) VALUES ('Reference', 'Non-Fiction');
INSERT IGNORE INTO genre (name, category) VALUES ('Biography', 'Non-Fiction');
INSERT IGNORE INTO genre (name, category) VALUES ('Self-Help', 'Non-Fiction');

INSERT IGNORE INTO books (bookName, author, isbn, publisher, genre) VALUES ('Harry Potter and the Sorcerer''s Stone', 'J.K. Rowling', '978-0439708180', 'Scholastic', 'Fantasy');
INSERT IGNORE INTO books (bookName, author, isbn, publisher, genre) VALUES ('The Hobbit', 'J.R.R. Tolkien', '978-0345534835', 'Houghton Mifflin Harcourt', 'Fantasy');
INSERT IGNORE INTO books (bookName, author, isbn, publisher, genre) VALUES ('The Hunger Games', 'Suzanne Collins', '978-0439023481', 'Scholastic', 'Science Fiction');
INSERT IGNORE INTO books (bookName, author, isbn, publisher, genre) VALUES ('Divergent', 'Veronica Roth', '978-0062024039', 'Katherine Tegen Books', 'Science Fiction');
INSERT IGNORE INTO books (bookName, author, isbn, publisher, genre) VALUES ('The Da Vinci Code', 'Dan Brown', '978-0307474278', 'Anchor', 'Mystery');
INSERT IGNORE INTO books (bookName, author, isbn, publisher, genre) VALUES ('English Dictionary', 'Merriam-Webster', '978-0877792956', 'Merriam-Webster', 'Reference');
INSERT IGNORE INTO books (bookName, author, isbn, publisher, genre) VALUES ('Steve Jobs', 'Walter Isaacson', '978-1451648539', 'Simon & Schuster', 'Biography');
INSERT IGNORE INTO books (bookName, author, isbn, publisher, genre) VALUES ('The 7 Habits of Highly Effective People', 'Stephen R. Covey', '978-1982137274', 'Simon & Schuster', 'Self-Help');

INSERT IGNORE INTO users (fullName, email, address, userID, password) VALUES ('John Doe', 'john@doe.com', '123 Main St', 'johndoe', 'password');
INSERT IGNORE INTO users (fullName, email, address, userID, password) VALUES ('Jane Doe', 'jane@doe.com', '123 First St', 'janedoe', 'password');
INSERT IGNORE INTO users (fullName, email, address, userID, password) VALUES ('Bob Smith', 'bob@smith.com', '123 Second St', 'bobsmith', 'password');
INSERT IGNORE INTO users (fullName, email, address, userID, password) VALUES ('James Stewart', 'james@stewart.com', '123 Third St', 'jstewart', 'password');

INSERT IGNORE INTO loans (loanID, isbn, userID, checkoutDate, expectedReturnDate, returned) VALUES (1, '978-0439708180', 'johndoe', '2021-01-01', '2021-01-15', FALSE);
INSERT IGNORE INTO loans (loanID, isbn, userID, checkoutDate, expectedReturnDate, returned) VALUES (2, '978-0345534835', 'janedoe', '2021-01-01', '2021-01-15', FALSE);
INSERT IGNORE INTO loans (loanID, isbn, userID, checkoutDate, expectedReturnDate, returned) VALUES (3, '978-0439023481', 'bobsmith', '2021-01-01', '2021-01-15', FALSE);
INSERT IGNORE INTO loans (loanID, isbn, userID, checkoutDate, expectedReturnDate, returned) VALUES (4, '978-0062024039', 'jstewart', '2021-01-01', '2021-01-15', FALSE);
INSERT IGNORE INTO loans (loanID, isbn, userID, checkoutDate, expectedReturnDate, returned) VALUES (5, '978-0307474278', 'johndoe', '2021-01-01', '2021-01-15', TRUE);

INSERT IGNORE INTO holds (isbn, holdID, userID, holdDate) VALUES ('978-0877792956', 1, 'johndoe', '2024-04-01');
INSERT IGNORE INTO holds (isbn, holdID, userID, holdDate) VALUES ('978-1451648539', 2, 'janedoe', '2024-04-16');
