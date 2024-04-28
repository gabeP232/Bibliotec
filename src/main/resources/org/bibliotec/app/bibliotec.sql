
CREATE SCHEMA IF NOT EXISTS `bibliotec`;
USE `bibliotec` ;


CREATE TABLE IF NOT EXISTS `bibliotec`.`books` (
  `bookName` VARCHAR(45) NOT NULL,
  `author` VARCHAR(45) NOT NULL,
  `isbn` VARCHAR(45) NOT NULL,
  `publisher` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`isbn`));


CREATE TABLE IF NOT EXISTS `bibliotec`.`patrons` (
  `name` VARCHAR(45) NOT NULL,
  `phoneNum` VARCHAR(45) NOT NULL,
  `address` VARCHAR(45) NOT NULL,
  `patronID` INT NOT NULL,
  PRIMARY KEY (`patronID`));


CREATE TABLE IF NOT EXISTS `bibliotec`.`checkout` (
  `checkoutID` INT NOT NULL,
  `bookName` VARCHAR(45) NOT NULL,
  `patronID` INT NOT NULL,
  `returnDate` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`checkoutID`),
  INDEX `patronID_idx` (`patronID` ASC) VISIBLE,
  CONSTRAINT `patronID_fk`
    FOREIGN KEY (`patronID`)
    REFERENCES `bibliotec`.`patrons` (`patronID`));




CREATE TABLE IF NOT EXISTS `bibliotec`.`genre` (
  `type` VARCHAR(45) NOT NULL,
  `author_fk` VARCHAR(45) NOT NULL,
  `subtype` VARCHAR(45) NULL DEFAULT NULL,
  PRIMARY KEY (`type`),
  INDEX `author_fk_idx` (`author_fk` ASC) VISIBLE,
  CONSTRAINT `author_fk`
    FOREIGN KEY (`author_fk`)
    REFERENCES `bibliotec`.`books` (`isbn`));



CREATE TABLE IF NOT EXISTS `bibliotec`.`login` (
  `username` TEXT NOT NULL,
  `password` TEXT NOT NULL);

