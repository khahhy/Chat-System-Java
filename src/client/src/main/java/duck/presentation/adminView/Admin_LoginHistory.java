package duck.presentation.adminView;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Admin_LoginHistory {

    public class LoginRecord {
        private String timestamp;
        private String username;
        private String fullName;
        public LoginRecord(String timestamp, String username, String fullName) {
            this.timestamp = timestamp;
            this.username = username;
            this.fullName = fullName;
        }
        public String getTimestamp() {return timestamp;}
        public String getUsername() {return username;}
        public String getFullName() {return fullName;}
    }

    private final ObservableList<LoginRecord> loginHistory = FXCollections.observableArrayList(
        new LoginRecord(formatTimestamp(LocalDateTime.now()), "user01", "Nguyễn Văn A"),
        new LoginRecord(formatTimestamp(LocalDateTime.now().minusHours(1)), "user02", "Trần Thị B"),
        new LoginRecord(formatTimestamp(LocalDateTime.now().minusDays(1)), "user03", "Phạm Minh C"),
        new LoginRecord(formatTimestamp(LocalDateTime.now().minusDays(2)), "user04", "Đỗ Quốc D"),
        new LoginRecord(formatTimestamp(LocalDateTime.now().minusDays(3)), "user05", "Nguyễn Văn E")
    );

    public BorderPane getContent() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-padding: 20;");

        TableView<LoginRecord> loginTable = new TableView<>();
        loginTable.setItems(loginHistory);

        TableColumn<LoginRecord, String> timestampColumn = new TableColumn<>("Thời gian");
        timestampColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getTimestamp()));

        TableColumn<LoginRecord, String> usernameColumn = new TableColumn<>("Tên đăng nhập");
        usernameColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getUsername()));

        TableColumn<LoginRecord, String> fullNameColumn = new TableColumn<>("Họ tên");
        fullNameColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getFullName()));

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
