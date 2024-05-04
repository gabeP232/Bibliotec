package org.bibliotec.app;

import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.layout.VBox;
import org.bibliotec.app.DatabaseAccess.Book;
import org.bibliotec.app.DatabaseAccess.Hold;
import org.bibliotec.app.DatabaseAccess.Loan;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Map;

public class PatronController {
    private static Scene scene;
    private static String userID;

    @FXML private TableView<Book> booksTable;
    @FXML private TableView<Loan> loansTable;
    @FXML private TableView<Hold> holdsTable;
    @FXML private TextField nameField, emailField, addressField;
    @FXML private Button holdButton;
    @FXML private ToggleGroup tabs;

    public static void show(String userID) {
        PatronController.userID = userID;
        if (scene == null) {
            try {
                //noinspection DataFlowIssue
                scene = new Scene(FXMLLoader.load(AdminController.class.getResource("patron.fxml")));
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
        Main.stage.setScene(scene);
    }

    public void initialize() {
        tabs.selectedToggleProperty().addListener((__, oldVal, selected) -> {
            if (selected == null) {
                oldVal.setSelected(true);
            } else {
                var childs = ((VBox) scene.getRoot()).getChildren();
                childs.set(childs.size() - 1, (Node) selected.getUserData());
            }
        });

        DatabaseAccess.getPatron(userID).ifPresent(patron -> {
            nameField.setText(patron.fullName());
            emailField.setText(patron.email());
            addressField.setText(patron.address());
        });

        columnsFromRecord(loansTable, Loan.class,
                Map.of("loanID", "Loan ID", "isbn", "ISBN", "checkoutDate", "Checkout Date", "expectedReturnDate", "Return Date", "returned", "Returned"));
        loansTable.setItems(FXCollections.observableArrayList(DatabaseAccess.getLoansForPatron(userID)));

        columnsFromRecord(holdsTable, Hold.class,
                Map.of("holdID", "Hold ID", "isbn", "ISBN", "holdDate", "Hold Date"));
        holdsTable.setItems(FXCollections.observableArrayList(DatabaseAccess.getHoldsForPatron(userID)));

        columnsFromRecord(booksTable, Book.class,
                Map.of("bookName", "Title", "author", "Author", "isbn", "ISBN", "publisher", "Publisher", "genre", "Genre", "totalCopies", "Total Copies"));
        booksTable.setItems(FXCollections.observableArrayList(DatabaseAccess.getBooks()));
        var availableCopies = new TableColumn<Book, String>("Available Copies");
        availableCopies.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(String.valueOf(DatabaseAccess.getAvailableCopies(cellData.getValue().isbn()))));
        booksTable.getColumns().add(availableCopies);

//        holdButton.disableProperty().bind(booksTable.getSelectionModel().selectedItemProperty().(item -> !(item instanceof Book book && DatabaseAccess.getAvailableCopies(book.isbn()) > 0));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static <R extends Record> void columnsFromRecord(TableView<R> table, Class<R> record, Map<String, String> columnNames) {
        for (var component : record.getRecordComponents()) {
            if (!columnNames.containsKey(component.getName())) continue;
            final boolean typeIsBoolean = component.getType().equals(boolean.class) || component.getType().equals(Boolean.class);

            var column = new TableColumn<R, Object>(columnNames.get(component.getName()));
            column.setCellValueFactory(cellData -> {
                try {
                    if (typeIsBoolean) {
                        return (ObservableValue) new ReadOnlyBooleanWrapper((Boolean) component.getAccessor().invoke(cellData.getValue()));
                    } else {
                        return (ObservableValue) new ReadOnlyStringWrapper(String.valueOf(component.getAccessor().invoke(cellData.getValue())));
                    }
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            });
            if (typeIsBoolean) {
                column.setCellFactory(CheckBoxTableCell.forTableColumn((TableColumn) column));
            }
            table.getColumns().add(column);
        }
    }

    public void hold() {
        booksTable.getSelectionModel().getSelectedItems().forEach(book ->
                DatabaseAccess.addHold(new Hold(-1, book.isbn(), userID, Date.valueOf(LocalDate.now())))
                .ifPresent(hold -> holdsTable.getItems().add(hold)));
    }

    public void cancelHold() {
        holdsTable.getSelectionModel().getSelectedItems().forEach(hold -> {
            holdsTable.getItems().remove(hold);
            DatabaseAccess.removeHold(hold.holdID());
        });
    }

    public void logout() {
        userID = null;
        LoginController.show();
    }
}
