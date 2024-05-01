package org.bibliotec.app;

import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import org.bibliotec.app.DatabaseAccess.Book;
import org.bibliotec.app.DatabaseAccess.Hold;
import org.bibliotec.app.DatabaseAccess.Loan;
import org.bibliotec.app.DatabaseAccess.User;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class AdminController {

    private static Scene scene;

    @SuppressWarnings("rawtypes") @FXML
    private TableView booksTable, patronsTable, patronLoansTable, loansTable, bookLoansTable, holdsTable;
    @FXML private ToggleGroup tabs;

    public static void show() {
        if (scene == null) {
            try {
                //noinspection DataFlowIssue
                scene = new Scene(FXMLLoader.load(AdminController.class.getResource("admin.fxml")));
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

        columnsFromRecord(holdsTable, Hold.class,
                Map.of("holdID", "Hold ID", "isbn", "ISBN", "userID", "User ID", "holdDate", "Hold Date"));
        holdsTable.setItems(FXCollections.observableArrayList(DatabaseAccess.getHolds()));

        columnsFromRecord(patronsTable, User.class,
                Map.of("userID", "User ID", "fullName", "Name", "email", "Email", "address", "Address"));
        patronsTable.setItems(FXCollections.observableArrayList(DatabaseAccess.getPatrons()));

        columnsFromRecord(patronLoansTable, Loan.class,
                Map.of("loanID", "Loan ID", "isbn", "ISBN", "checkoutDate", "Checkout Date", "expectedReturnDate", "Expected Return Date", "returned", "Returned"));
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
                Map.of("loanID", "Loan ID", "checkoutDate", "Checkout Date", "expectedReturnDate", "Expected Return Date", "returned", "Is Returned"));
        booksTable.getSelectionModel().selectedItemProperty().addListener((__, ___, selected) -> {
            if (selected == null) {
                bookLoansTable.setItems(FXCollections.emptyObservableList());
            } else if (selected instanceof Book book) {
                bookLoansTable.setItems(FXCollections.observableArrayList(DatabaseAccess.getLoansForBook(book.isbn())));
            }
        });

        columnsFromRecord(booksTable, Book.class,
                Map.of("bookName", "Title", "author", "Author", "isbn", "ISBN", "publisher", "Publisher", "genre", "Genre", "totalCopies", "Total Copies"));
        booksTable.setItems(FXCollections.observableArrayList(DatabaseAccess.getBooks()));
    }

    private static final Object placeholder = new Object();

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static <R extends Record> void columnsFromRecord(TableView<R> table, Class<R> record, Map<String, String> columnNames) {
        for (var component : record.getRecordComponents()) {
            if (!columnNames.containsKey(component.getName())) continue;
            final boolean typeIsBoolean = component.getType().equals(boolean.class) || component.getType().equals(Boolean.class);

            var column = new TableColumn<R, Object>(columnNames.get(component.getName()));
            column.setCellValueFactory(cellData -> {
                try {
                    if (cellData.getValue() == null || cellData.getValue() == placeholder) {
                        if (typeIsBoolean) {
                            return (ObservableValue) new ReadOnlyBooleanWrapper(false);
                        } else {
                            return (ObservableValue) new ReadOnlyStringWrapper("<edit>");
                        }
                    }
                    if (typeIsBoolean) {
                        return (ObservableValue) new ReadOnlyBooleanWrapper((Boolean) component.getAccessor().invoke(cellData.getValue()));
                    } else {
                        return (ObservableValue) new ReadOnlyStringWrapper(String.valueOf(component.getAccessor().invoke(cellData.getValue())));
                    }
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            });
            column.setOnEditCommit(event -> {
//                String newValue = event.getNewValue();
//                R recordInstance = event.getRowValue();
                // save the data
            });
            if (typeIsBoolean) {
                column.setCellFactory(CheckBoxTableCell.forTableColumn((TableColumn) column));
            } else {
                column.setCellFactory((Callback) TextFieldTableCell.forTableColumn());
            }
            table.getColumns().add(column);
        }
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
            case Hold hold -> DatabaseAccess.removeHold(hold.holdID());
            default -> {}
        }
    }

    public void logout() {
        LoginController.show();
    }
}
