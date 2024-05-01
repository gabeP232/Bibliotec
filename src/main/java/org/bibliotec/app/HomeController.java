package org.bibliotec.app;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.VBox;
import org.bibliotec.app.DatabaseAccess.Book;
import org.bibliotec.app.DatabaseAccess.Loan;
import org.bibliotec.app.DatabaseAccess.User;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class HomeController {

    private static Scene scene;

    @SuppressWarnings("rawtypes") @FXML private TableView booksTable, patronsTable, patronLoansTable, loansTable, bookLoansTable;
    @FXML private ToggleGroup tabs;

    public static void show() {
        if (scene == null) {
            try {
                //noinspection DataFlowIssue
                scene = new Scene(FXMLLoader.load(HomeController.class.getResource("home.fxml")));
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
        Main.stage.setScene(scene);
    }

    @SuppressWarnings("unchecked")
    public void initialize() {
        tabs.selectedToggleProperty().addListener((__, oldVal, selected) -> {
            if (selected == null) {
                oldVal.setSelected(true);
            } else {
                var childs = ((VBox) scene.getRoot()).getChildren();
                childs.set(childs.size() - 1, (Node) selected.getUserData());
            }
        });

        columnsFromRecord(loansTable, Loan.class,
                Map.of("loanID", "Loan ID", "isbn", "ISBN", "userID", "User ID", "checkoutDate", "Checkout Date", "expectedReturnDate", "Return Date", "returned", "Returned"));
        loansTable.setItems(FXCollections.observableArrayList(DatabaseAccess.getLoans()));

        columnsFromRecord(patronsTable, User.class,
                Map.of("userID", "User ID", "fullName", "Name", "email", "Email", "address", "Address", "password", "Password"));
        patronsTable.setItems(FXCollections.observableArrayList(DatabaseAccess.getPatrons()));

        columnsFromRecord(patronLoansTable, Loan.class,
                Map.of("loanID", "Loan ID", "isbn", "ISBN", "checkoutDate", "Check Out Date", "expectedReturnDate", "Expected Return Date", "returned", "Is Returned"));
        patronsTable.getSelectionModel().selectedItemProperty().addListener((__, ___, selected) -> {
            if (selected == null) {
                patronLoansTable.setItems(FXCollections.emptyObservableList());
            } else if (selected instanceof User patron) {
                System.out.println("Selected: " + selected);
                patronLoansTable.setItems(FXCollections.observableArrayList(DatabaseAccess.getLoansForPatron(patron.userID())));
                System.out.println(patronLoansTable.getItems());
            }
        });

        columnsFromRecord(bookLoansTable, Loan.class,
                Map.of("checkoutID", "Checkout ID", "patronID", "Patron", "returnDate", "Return Date"));
        booksTable.getSelectionModel().selectedItemProperty().addListener((__, ___, selected) -> {
            if (selected == null) {
                bookLoansTable.setItems(FXCollections.emptyObservableList());
            } else if (selected instanceof Book book) {
                bookLoansTable.setItems(FXCollections.observableArrayList(DatabaseAccess.getLoansForBook(book.bookName())));
            }
        });

        columnsFromRecord(booksTable, Book.class,
                Map.of("bookName", "Title", "author", "Author", "isbn", "ISBN", "publisher", "Publisher", "genre", "Genre", "totalCopies", "Total Copies"));
        booksTable.setItems(FXCollections.observableArrayList(DatabaseAccess.getBooks()));
    }

    private static final Object placeholder = new Object();

    private static <R extends Record> void columnsFromRecord(TableView<R> table, Class<R> record, Map<String, String> columnNames) {
        for (var component : record.getRecordComponents()) {
            if (!columnNames.containsKey(component.getName())) continue;

            var column = new TableColumn<R, String>(columnNames.get(component.getName()));
            column.setCellValueFactory(cellData -> {
                try {
                    if (cellData.getValue() == null || cellData.getValue() == placeholder) {
                        return new ReadOnlyStringWrapper("<edit>");
                    }
                    return new ReadOnlyStringWrapper(String.valueOf(component.getAccessor().invoke(cellData.getValue())));
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            });
            column.setOnEditCommit(event -> {
                String newValue = event.getNewValue();
//                R recordInstance = event.getRowValue();
                // save the data
            });
            column.setCellFactory(TextFieldTableCell.forTableColumn());
//            TextFieldTableCell.forTableColumn(StringConverter.)
            table.getColumns().add(column);
        }
    }

    public void logout() {
        LoginController.show();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public void add(ActionEvent actionEvent) {
        ((TableView) ((Node) actionEvent.getTarget()).getUserData()).getItems().add(placeholder);
    }

    @SuppressWarnings("rawtypes")
    public void delete(ActionEvent actionEvent) {
        var table = (TableView) ((Node) actionEvent.getTarget()).getUserData();
        var item = table.getSelectionModel().getSelectedItem();
        table.getItems().remove(item);
        switch (item) {
            case Book book -> DatabaseAccess.removeBook(book.isbn());
            case User patron -> DatabaseAccess.removePatron(patron.userID());
            case Loan loan -> DatabaseAccess.removeLoan(loan);
            default -> {}
        }
    }
}
