package duck.presentation.adminView;

import duck.bus.LoginHistoryBUS;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
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

        TableColumn<Map<String, Object>, String> timestampColumn = new TableColumn<>("Thời gian");
        timestampColumn.setCellValueFactory(data -> {
            LocalDateTime loginTime = (LocalDateTime) data.getValue().get("loginTime");
            return new javafx.beans.property.SimpleStringProperty(formatTimestamp(loginTime));
        });

        TableColumn<Map<String, Object>, String> usernameColumn = new TableColumn<>("Tên đăng nhập");
        usernameColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty((String)data.getValue().get("username")));

        TableColumn<Map<String, Object>, String> fullNameColumn = new TableColumn<>("Họ tên");
        fullNameColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty((String)data.getValue().get("fullname")));

        loginTable.getColumns().addAll(timestampColumn, usernameColumn, fullNameColumn);
        loginTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        root.setCenter(loginTable);
        return root;
    }

    private String formatTimestamp(LocalDateTime timestamp) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return timestamp.format(formatter);
    }

    public void start(Stage stage) {
        Scene scene = new Scene(getContent(), 600, 400);
        stage.setScene(scene);
        stage.setTitle("Lịch sử đăng nhập");
        stage.show();
    }
}
