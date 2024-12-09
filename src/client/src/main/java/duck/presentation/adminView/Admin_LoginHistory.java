package duck.presentation.adminView;

import duck.bus.LoginHistoryBUS;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class Admin_LoginHistory {
    private LoginHistoryBUS login_history_BUS;
    List<Map<String, Object>> loginHistoryList;
    ObservableList<Map<String, Object>> loginHistories;

    public Admin_LoginHistory() {
        login_history_BUS = new LoginHistoryBUS();
        loginHistoryList = login_history_BUS.getAllLoginHistory();
        loginHistories = FXCollections.observableArrayList(loginHistoryList);
    }


    public BorderPane getContent() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-padding: 20;");

        TableView<Map<String, Object>> loginTable = new TableView<>();
        loginTable.setItems(loginHistories);

        TableColumn<Map<String, Object>, LocalDateTime> timestampColumn = new TableColumn<>("Thời gian");
        timestampColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>((LocalDateTime) data.getValue().get("loginTime")));

        timestampColumn.setCellFactory(_ -> new TableCell<>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null) setText(item.format(formatter)); 
                else setText("");  
            }
        });
        timestampColumn.setStyle("-fx-alignment: CENTER;");

        TableColumn<Map<String, Object>, String> usernameColumn = new TableColumn<>("Tên đăng nhập");
        usernameColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty((String)data.getValue().get("username")));

        TableColumn<Map<String, Object>, String> fullNameColumn = new TableColumn<>("Họ tên");
        fullNameColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty((String)data.getValue().get("fullname")));

        loginTable.getColumns().addAll(timestampColumn, usernameColumn, fullNameColumn);
        loginTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        root.setCenter(loginTable);
        return root;
    }

    public void start(Stage stage) {
        Scene scene = new Scene(getContent(), 600, 400);
        stage.setScene(scene);
        stage.setTitle("Lịch sử đăng nhập");
        stage.show();
    }
}
