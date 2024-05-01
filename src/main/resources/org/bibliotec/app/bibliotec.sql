CREATE DATABASE IF NOT EXISTS bibliotec;
CREATE SCHEMA IF NOT EXISTS bibliotec;
USE bibliotec;

CREATE TABLE IF NOT EXISTS genre (
                                     name VARCHAR(45) NOT NULL,
                                     category VARCHAR(45) NULL DEFAULT NULL,
                                     PRIMARY KEY (name));

-- Java record
-- public record Genre(String name, String category) {}

CREATE TABLE IF NOT EXISTS books (
  bookName VARCHAR(45) NOT NULL,
  author VARCHAR(45) NOT NULL,
  isbn VARCHAR(45) NOT NULL,
  publisher VARCHAR(45) NOT NULL,
  genre VARCHAR(45) NOT NULL,
  totalCopies INT DEFAULT 1,
  PRIMARY KEY (isbn),
  FOREIGN KEY (genre) REFERENCES genre (name));

-- Java record
-- public record Book(String bookName, String author, String isbn, String publisher, String genre, int totalCopies) {}


CREATE TABLE IF NOT EXISTS users (
  name VARCHAR(45) NOT NULL,
  phoneNum VARCHAR(45) NOT NULL,
  address VARCHAR(45) NOT NULL,
  userID VARCHAR(45) NOT NULL,
  password VARCHAR(45) NOT NULL,
  isAdmin BOOLEAN NOT NULL DEFAULT FALSE,
  PRIMARY KEY (userID));

-- Java record
-- public record User(String name, String phoneNum, String address, String userID, String userPassword, boolean isAdmin) {}

CREATE TABLE IF NOT EXISTS checkout (
  checkoutID INT NOT NULL,
  isbn VARCHAR(50) NOT NULL,
  userID VARCHAR(45) NOT NULL,
  checkoutDate DATE NOT NULL,
  expectedReturnDate DATE NOT NULL,
  returned BOOLEAN,
  PRIMARY KEY (checkoutID),
  FOREIGN KEY (userID) REFERENCES users (userID));

-- Java record
-- public record Checkout(int checkoutID, String isbn, String userID, Date checkoutDate, Date expectedReturnDate, boolean returned) {}

-- Inserting data into the tables
INSERT INTO genre (name, category) VALUES ('Fantasy', 'Fiction');
INSERT INTO genre (name, category) VALUES ('Science Fiction', 'Fiction');
INSERT INTO genre (name, category) VALUES ('Mystery', 'Fiction');
INSERT INTO genre (name, category) VALUES ('Romance', 'Fiction');
INSERT INTO genre (name, category) VALUES ('Reference', 'Non-Fiction');
INSERT INTO genre (name, category) VALUES ('Biography', 'Non-Fiction');
INSERT INTO genre (name, category) VALUES ('Self-Help', 'Non-Fiction');

INSERT INTO books (bookName, author, isbn, publisher, genre) VALUES ('Harry Potter and the Sorcerer''s Stone', 'J.K. Rowling', '978-0439708180', 'Scholastic', 'Fantasy');
INSERT INTO books (bookName, author, isbn, publisher, genre) VALUES ('The Hobbit', 'J.R.R. Tolkien', '978-0345534835', 'Houghton Mifflin Harcourt', 'Fantasy');
INSERT INTO books (bookName, author, isbn, publisher, genre) VALUES ('The Hunger Games', 'Suzanne Collins', '978-0439023481', 'Scholastic', 'Science Fiction');
INSERT INTO books (bookName, author, isbn, publisher, genre) VALUES ('Divergent', 'Veronica Roth', '978-0062024039', 'Katherine Tegen Books', 'Science Fiction');
INSERT INTO books (bookName, author, isbn, publisher, genre) VALUES ('The Da Vinci Code', 'Dan Brown', '978-0307474278', 'Anchor', 'Mystery');
INSERT INTO books (bookName, author, isbn, publisher, genre) VALUES ('English Dictionary', 'Merriam-Webster', '978-0877792956', 'Merriam-Webster', 'Reference');
INSERT INTO books (bookName, author, isbn, publisher, genre) VALUES ('Steve Jobs', 'Walter Isaacson', '978-1451648539', 'Simon & Schuster', 'Biography');
INSERT INTO books (bookName, author, isbn, publisher, genre) VALUES ('The 7 Habits of Highly Effective People', 'Stephen R. Covey', '978-1982137274', 'Simon & Schuster', 'Self-Help');

INSERT INTO users (name, phoneNum, address, userID, password) VALUES ('John Doe', '123-456-7890', '123 Main St', 'johndoe', 'password');
INSERT INTO users (name, phoneNum, address, userID, password) VALUES ('Jane Doe', '123-456-7890', '123 First St', 'janedoe', 'password');
INSERT INTO users (name, phoneNum, address, userID, password) VALUES ('Bob Smith', '123-456-7890', '123 Second St', 'bobsmith', 'password');
INSERT INTO users (name, phoneNum, address, userID, password) VALUES ('James Stewart', '123-456-7890', '123 Third St', 'jstewart', 'password');

INSERT INTO checkout (checkoutID, isbn, userID, checkoutDate, expectedReturnDate, returned) VALUES (1, '978-0439708180', 'johndoe', '2021-01-01', '2021-01-15', FALSE);
INSERT INTO checkout (checkoutID, isbn, userID, checkoutDate, expectedReturnDate, returned) VALUES (2, '978-0345534835', 'janedoe', '2021-01-01', '2021-01-15', FALSE);
INSERT INTO checkout (checkoutID, isbn, userID, checkoutDate, expectedReturnDate, returned) VALUES (3, '978-0439023481', 'bobsmith', '2021-01-01', '2021-01-15', FALSE);
INSERT INTO checkout (checkoutID, isbn, userID, checkoutDate, expectedReturnDate, returned) VALUES (4, '978-0062024039', 'jstewart', '2021-01-01', '2021-01-15', FALSE);
INSERT INTO checkout (checkoutID, isbn, userID, checkoutDate, expectedReturnDate, returned) VALUES (5, '978-0307474278', 'johndoe', '2021-01-01', '2021-01-15', FALSE);