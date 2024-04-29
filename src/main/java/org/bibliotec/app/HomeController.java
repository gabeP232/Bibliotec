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
import org.bibliotec.app.DatabaseAccess.Patron;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class HomeController {

    private static Scene scene;

    @SuppressWarnings("rawtypes") @FXML private TableView booksTable, patronsTable, patronLoansTable, loansTable;
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
                Map.of("user", "User", "book", "Book", "date", "Date"));
        loansTable.setItems(FXCollections.observableArrayList(DatabaseAccess.getLoans()));

        columnsFromRecord(patronsTable, Patron.class,
                Map.of("name", "Name", "phoneNum", "Phone Number", "address", "Address", "id", "ID"));
        patronsTable.setItems(FXCollections.observableArrayList(DatabaseAccess.getPatrons()));
        columnsFromRecord(patronLoansTable, Loan.class,
                Map.of("book", "Book", "date", "Date"));
        patronLoansTable.itemsProperty().bind(patronsTable.getSelectionModel().selectedItemProperty().map(patron ->
                FXCollections.observableArrayList(DatabaseAccess.getLoansForPatron(((Patron) patron).id()))));

        columnsFromRecord(booksTable, Book.class,
                Map.of("title", "Title", "author", "Author", "isbn", "ISBN", "genre", "Genre", "publisher", "Publisher", "year", "Year", "pages", "Pages"));
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
            case Patron patron -> DatabaseAccess.removePatron(patron.id());
            case Loan loan -> DatabaseAccess.removeLoan(loan);
            default -> {}
        }
    }
}
