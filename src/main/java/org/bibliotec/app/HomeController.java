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

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class HomeController {

    private static Scene scene;

    @SuppressWarnings("rawtypes") @FXML private TableView booksTable, loansTable;
    @FXML private ToggleGroup tabs;

    public static void show() {
        if (scene == null) {
            try {
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
        columnsFromRecord(booksTable, DatabaseAccess.Book.class,
                Map.of("title", "Title", "author", "Author", "isbn", "ISBN", "genre", "Genre", "publisher", "Publisher", "year", "Year", "pages", "Pages"));
        booksTable.setItems(FXCollections.observableArrayList(DatabaseAccess.getBooks()));
    }

    private static final Object placeholder = new Object();

    private static<R extends Record> void columnsFromRecord(TableView<R> table, Class<R> record, Map<String, String> columnNames) {
        for (var component : record.getRecordComponents()) {
            var column = new TableColumn<R, String>(columnNames.getOrDefault(component.getName(), component.getName()));
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
                R recordInstance = event.getRowValue();
                // save the data
            });
            column.setCellFactory(TextFieldTableCell.forTableColumn());
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
        var table = ((TableView) ((Node) actionEvent.getTarget()).getUserData());
        table.getItems().remove(table.getSelectionModel().getSelectedItem());
    }
}
