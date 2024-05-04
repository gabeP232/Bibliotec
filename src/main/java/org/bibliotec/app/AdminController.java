package org.bibliotec.app;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
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
import java.lang.invoke.MethodType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.RecordComponent;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

public class AdminController {

    private static Scene scene;

    @FXML private TableView<Book> booksTable;
    @FXML private TableView<User> patronsTable;
    @FXML private TableView<Loan> patronLoansTable, loansTable, bookLoansTable;
    @FXML private TableView<Hold> holdsTable;
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
        patronsTable.getSelectionModel().selectedItemProperty().addListener((__, ___, patron) -> {
            if (patron == null) {
                patronLoansTable.setItems(FXCollections.emptyObservableList());
            } else {
                patronLoansTable.setItems(FXCollections.observableArrayList(DatabaseAccess.getLoansForPatron(patron.userID())));
            }
        });

        columnsFromRecord(bookLoansTable, Loan.class,
                Map.of("loanID", "Loan ID", "checkoutDate", "Checkout Date", "expectedReturnDate", "Expected Return Date", "returned", "Is Returned"));
        booksTable.getSelectionModel().selectedItemProperty().addListener((__, ___, book) -> {
            if (book == null) {
                bookLoansTable.setItems(FXCollections.emptyObservableList());
            } else {
                bookLoansTable.setItems(FXCollections.observableArrayList(DatabaseAccess.getLoansForBook(book.isbn())));
            }
        });

        columnsFromRecord(booksTable, Book.class,
                Map.of("bookName", "Title", "author", "Author", "isbn", "ISBN", "publisher", "Publisher", "genre", "Genre", "totalCopies", "Total Copies"));
        booksTable.setItems(FXCollections.observableArrayList(DatabaseAccess.getBooks()));
        var availableCopies = new TableColumn<Book, String>("Available Copies");
        availableCopies.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(String.valueOf(DatabaseAccess.getAvailableCopies(cellData.getValue().isbn()))));
        booksTable.getColumns().add(availableCopies);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static <R extends Record & DatabaseAccess.Updatable> void columnsFromRecord(TableView<R> table, Class<R> record, Map<String, String> columnNames) {
        for (var component : record.getRecordComponents()) {
            if (!columnNames.containsKey(component.getName())) continue;
            final boolean typeIsBoolean = component.getType().equals(boolean.class);

            var column = new TableColumn<R, Object>(columnNames.get(component.getName()));
            BiConsumer<R, Object> onEdit = (oldRecord, newValue) -> {
                var args = Stream.of(record.getRecordComponents()).map(RecordComponent::getAccessor).map(accessor -> {
                    try {
                        if (accessor.equals(component.getAccessor())) {
                            Class type = accessor.getReturnType();
                            if (typeIsBoolean || type.isAssignableFrom(String.class)) {
                                return newValue;
                            } else if (type.isPrimitive()) {
                                // boxes primitive types (e.g. int.class -> Integer.class)
                                type = MethodType.methodType(type).wrap().returnType();
                            }
                            return type.getDeclaredMethod("valueOf", String.class).invoke(null, newValue);
                        }
                        return accessor.invoke(oldRecord);
                    } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                        throw new RuntimeException(e);
                    }
                }).toArray();
                try {
                    R newRecord = (R) record.getConstructors()[0].newInstance(args);
                    if (newValue.equals(newRecord.primaryKey())) {
                        newRecord.updatePrimaryKey(oldRecord.primaryKey());
                    } else {
                        newRecord.updateDB();
                    }
                    Platform.runLater(() -> table.getItems().set(table.getItems().indexOf(oldRecord), newRecord));
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            };
            column.setCellValueFactory(cellData -> {
                try {
                    if (typeIsBoolean) {
                        var property = new SimpleBooleanProperty((Boolean) component.getAccessor().invoke(cellData.getValue()));
                        property.addListener((__, ___, newValue) -> onEdit.accept(cellData.getValue(), newValue));
                        return (ObservableValue) property;
                    } else {
                        return (ObservableValue) new SimpleStringProperty(String.valueOf(component.getAccessor().invoke(cellData.getValue())));
                    }
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            });
            column.setOnEditCommit(event -> onEdit.accept(event.getRowValue(), event.getNewValue()));
            if (typeIsBoolean) {
                column.setCellFactory(CheckBoxTableCell.forTableColumn((TableColumn) column));
            } else {
                column.setCellFactory((Callback) TextFieldTableCell.forTableColumn());
            }
            table.getColumns().add(column);
        }
    }

    public void add(ActionEvent actionEvent) {
        var selectedTable = ((Node) actionEvent.getTarget()).getUserData();
        if (selectedTable == booksTable) {
            booksTable.getItems().add(Book.empty());
            DatabaseAccess.addBook(booksTable.getItems().getLast());
        } else if (selectedTable == loansTable) {
            var dialog = new TextInputDialog("ISBN");
            dialog.setHeaderText("Enter the ISBN of the book to loan.");
            dialog.showAndWait().ifPresent(isbn -> {
                loansTable.getItems().add(DatabaseAccess.addLoan(Loan.empty(isbn)).orElseThrow(() -> new RuntimeException("Failed to create loan.")));
            });
        } else if (selectedTable == holdsTable) {
            var dialog = new TextInputDialog("ISBN");
            dialog.setHeaderText("Enter the ISBN of the book to hold.");
            dialog.showAndWait().ifPresent(isbn -> {
                DatabaseAccess.getBook(isbn).orElseThrow(() -> new RuntimeException("Book not found."));
                holdsTable.getItems().add(DatabaseAccess.addHold(Hold.empty(isbn)).orElseThrow(() -> new RuntimeException("Failed to create hold.")));
            });
        }
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
