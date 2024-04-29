CREATE DATABASE IF NOT EXISTS bibliotec;
CREATE SCHEMA IF NOT EXISTS bibliotec;
USE bibliotec;

CREATE TABLE IF NOT EXISTS books (
  bookName VARCHAR(45) NOT NULL,
  author VARCHAR(45) NOT NULL,
  isbn VARCHAR(45) NOT NULL,
  publisher VARCHAR(45) NOT NULL,
  PRIMARY KEY (isbn));


CREATE TABLE IF NOT EXISTS patrons (
  name VARCHAR(45) NOT NULL,
  phoneNum VARCHAR(45) NOT NULL,
  address VARCHAR(45) NOT NULL,
  patronID INT NOT NULL,
  PRIMARY KEY (patronID));


CREATE TABLE IF NOT EXISTS checkout (
  checkoutID INT NOT NULL,
  bookName VARCHAR(45) NOT NULL,
  patronID INT NOT NULL,
  returnDate VARCHAR(45) NOT NULL,
  PRIMARY KEY (checkoutID),
  INDEX patronID_idx (patronID ASC) VISIBLE,
  CONSTRAINT patronID_fk
    FOREIGN KEY (patronID)
    REFERENCES bibliotec.patrons (patronID));




CREATE TABLE IF NOT EXISTS genre (
  type VARCHAR(45) NOT NULL,
  author_fk VARCHAR(45) NOT NULL,
  subtype VARCHAR(45) NULL DEFAULT NULL,
  PRIMARY KEY (type),
  INDEX author_fk_idx (author_fk ASC) VISIBLE,
  CONSTRAINT author_fk
    FOREIGN KEY (author_fk)
    REFERENCES books (isbn));



CREATE TABLE IF NOT EXISTS login (
  username TEXT NOT NULL,
  password TEXT NOT NULL);

-- Inserting data into the tables
INSERT INTO books (bookName, author, isbn, publisher) VALUES ('The Great Gatsby', 'F. Scott Fitzgerald', '9780743273565', 'Scribner');
INSERT INTO books (bookName, author, isbn, publisher) VALUES ('To Kill a Mockingbird', 'Harper Lee', '9780061120084', 'Harper Perennial Modern Classics');
INSERT INTO books (bookName, author, isbn, publisher) VALUES ('1984', 'George Orwell', '9780451524935', 'Signet Classic');
INSERT INTO books (bookName, author, isbn, publisher) VALUES ('Pride and Prejudice', 'Jane Austen', '9780679783268', 'Modern Library');
INSERT INTO books (bookName, author, isbn, publisher) VALUES ('The Catcher in the Rye', 'J.D. Salinger', '9780316769488', 'Little, Brown and Company');
INSERT INTO books (bookName, author, isbn, publisher) VALUES ('The Hobbit', 'J.R.R. Tolkien', '9780345339683', 'Del Rey');
INSERT INTO books (bookName, author, isbn, publisher) VALUES ('Fahrenheit 451', 'Ray Bradbury', '9781451673319', 'Simon & Schuster');
INSERT INTO books (bookName, author, isbn, publisher) VALUES ('The Lord of the Rings', 'J.R.R. Tolkien', '9780618640157', 'Houghton Mifflin Harcourt');

INSERT INTO patrons (name, phoneNum, address, patronID) VALUES ('John Doe', '123-456-7890', '123 Main St', 1);
INSERT INTO patrons (name, phoneNum, address, patronID) VALUES ('Jane Doe', '123-456-7890', '123 Main St', 2);
INSERT INTO patrons (name, phoneNum, address, patronID) VALUES ('Alice Smith', '123-456-7890', '123 Main St', 3);
INSERT INTO patrons (name, phoneNum, address, patronID) VALUES ('Bob Smith', '123-456-7890', '123 Main St', 4);
INSERT INTO patrons (name, phoneNum, address, patronID) VALUES ('Charlie Brown', '123-456-7890', '123 Main St', 5);
INSERT INTO patrons (name, phoneNum, address, patronID) VALUES ('Lucy Brown', '123-456-7890', '123 Main St', 6);
INSERT INTO patrons (name, phoneNum, address, patronID) VALUES ('Snoopy', '123-456-7890', '123 Main St', 7);
INSERT INTO patrons (name, phoneNum, address, patronID) VALUES ('Linus', '123-456-7890', '123 Main St', 8);

INSERT INTO checkout (checkoutID, bookName, patronID, returnDate) VALUES (1, 'The Great Gatsby', 1, '2021-12-31');
INSERT INTO checkout (checkoutID, bookName, patronID, returnDate) VALUES (2, 'To Kill a Mockingbird', 2, '2021-12-31');
INSERT INTO checkout (checkoutID, bookName, patronID, returnDate) VALUES (3, '1984', 3, '2021-12-31');
INSERT INTO checkout (checkoutID, bookName, patronID, returnDate) VALUES (4, 'Pride and Prejudice', 4, '2021-12-31');
INSERT INTO checkout (checkoutID, bookName, patronID, returnDate) VALUES (5, 'The Catcher in the Rye', 5, '2021-12-31');
INSERT INTO checkout (checkoutID, bookName, patronID, returnDate) VALUES (6, 'The Hobbit', 6, '2021-12-31');

INSERT INTO genre (type, author_fk, subtype) VALUES ('Fiction', '9780743273565', 'Classic');
INSERT INTO genre (type, author_fk, subtype) VALUES ('Fiction', '9780061120084', 'Classic');
INSERT INTO genre (type, author_fk, subtype) VALUES ('Fiction', '9780451524935', 'Classic');
INSERT INTO genre (type, author_fk, subtype) VALUES ('Fiction', '9780679783268', 'Classic');
