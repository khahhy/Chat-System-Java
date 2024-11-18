package duck;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Admin_SpamReports {

    public class SpamReport {
        private String reportedUsername;
        private String timestamp;

        public SpamReport(String reportedUsername, LocalDateTime timestamp) {
            this.reportedUsername = reportedUsername;
            this.timestamp = formatTimestamp(timestamp);
        }
        public String getReportedUsername() {return reportedUsername;}

        public String getTimestamp() {return timestamp;}
    }

    private final ObservableList<SpamReport> spamReports = FXCollections.observableArrayList(
        new SpamReport("user01", LocalDateTime.now()),
        new SpamReport("user02", LocalDateTime.now().minusHours(1)),
        new SpamReport("user03", LocalDateTime.now().minusDays(1)),
        new SpamReport("user04", LocalDateTime.now().minusDays(2)),
        new SpamReport("user05", LocalDateTime.now().minusDays(3))
    );

    private final ObservableList<SpamReport> filteredReports = FXCollections.observableArrayList(spamReports);

    public BorderPane getContent() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-padding: 20;");

        TableView<SpamReport> spamTable = new TableView<>();
        spamTable.setItems(filteredReports);

        TableColumn<SpamReport, String> usernameColumn = new TableColumn<>("Tên đăng nhập"); // ten dang nhap cua nguoi bi report
        usernameColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getReportedUsername()));
        usernameColumn.setStyle("-fx-alignment: CENTER;");

        TableColumn<SpamReport, String> timestampColumn = new TableColumn<>("Thời gian");
        timestampColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getTimestamp()));
        timestampColumn.setStyle("-fx-alignment: CENTER;");

        TableColumn<SpamReport, Void> lockColumn = new TableColumn<>("Tùy chọn");
        lockColumn.setCellFactory(_ -> new TableCell<>() {
            private final Button lockButton = new Button("Khóa tài khoản");

            {
                lockButton.setStyle("-fx-font-size: 12px; -fx-padding: 5 10; -fx-background-color: #ff6666; -fx-text-fill: white;");
                lockButton.setOnAction(_ -> {
                    SpamReport report = getTableView().getItems().get(getIndex());
                    spamReports.remove(report); 
                    filteredReports.remove(report); 
                });
            }

            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) 
                    setGraphic(null);
                else 
                    setGraphic(lockButton);
            }
        });
        lockColumn.setStyle("-fx-alignment: CENTER;");

      
        spamTable.getColumns().addAll(usernameColumn, timestampColumn, lockColumn);
        spamTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        HBox controls = createControls(spamTable);

        VBox content = new VBox(10, controls, spamTable);
        content.setStyle("-fx-padding: 10; -fx-background-color: #f9f9f9; -fx-border-color: #ddd; -fx-border-width: 1;");
        root.setCenter(content);

        return root;
    }

    private HBox createControls(TableView<SpamReport> spamTable) {
        TextField searchField = new TextField();
        searchField.setPromptText("Lọc theo tên đăng nhập");
        searchField.setPrefWidth(150);

        DatePicker startDatePicker = new DatePicker();
        startDatePicker.setPromptText("Từ ngày");
        startDatePicker.setPrefWidth(150);

        DatePicker endDatePicker = new DatePicker();
        endDatePicker.setPromptText("Đến ngày");
        endDatePicker.setPrefWidth(150);

        ComboBox<String> sortOptions = new ComboBox<>(FXCollections.observableArrayList(
            "Tên A-Z", "Tên Z-A", "Thời gian mới nhất", "Thời gian cũ nhất"
        ));
        sortOptions.setValue("Tên A-Z");

        Button applyFilterButton = new Button("Lọc");
        applyFilterButton.setStyle("-fx-font-size: 12px; -fx-padding: 5 10; -fx-background-color: #4CAF50; -fx-text-fill: white;");
        applyFilterButton.setOnAction(_ -> {
            String keyword = searchField.getText().toLowerCase();
            LocalDateTime startDate = startDatePicker.getValue() != null ? startDatePicker.getValue().atStartOfDay() : null;
            LocalDateTime endDate = endDatePicker.getValue() != null ? endDatePicker.getValue().atTime(23, 59, 59) : null;

            filteredReports.setAll(spamReports.filtered(report -> {
                boolean matchesKeyword = report.getReportedUsername().toLowerCase().contains(keyword);
                boolean matchesDate = true;

                if (startDate != null || endDate != null) {
                    LocalDateTime reportDate = LocalDateTime.parse(report.getTimestamp(), DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
                    matchesDate = (startDate == null || !reportDate.isBefore(startDate)) && (endDate == null || !reportDate.isAfter(endDate));
                }

                return matchesKeyword && matchesDate;
            }));
        });

        sortOptions.setOnAction(_ -> {
            String sortChoice = sortOptions.getValue();
            filteredReports.sort((r1, r2) -> {
                switch (sortChoice) {
                    case "Tên A-Z":
                        return r1.getReportedUsername().compareToIgnoreCase(r2.getReportedUsername());
                    case "Tên Z-A":
                        return r2.getReportedUsername().compareToIgnoreCase(r1.getReportedUsername());
                    case "Thời gian mới nhất":
                        return r2.getTimestamp().compareTo(r1.getTimestamp());
                    case "Thời gian cũ nhất":
                        return r1.getTimestamp().compareTo(r2.getTimestamp());
                }
                return 0;
            });
        });

        HBox filters = new HBox(10, searchField, startDatePicker, endDatePicker, applyFilterButton, sortOptions);
        filters.setStyle("-fx-padding: 10; -fx-background-color: #f1f1f1; -fx-border-color: #ddd; -fx-border-width: 1;");
        return filters;
    }

    private String formatTimestamp(LocalDateTime timestamp) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return timestamp.format(formatter);
    }

    public void start(Stage stage) {
        Scene scene = new Scene(getContent(), 800, 600);
        stage.setScene(scene);
        stage.setTitle("Quản lý báo cáo spam");
        stage.show();
    }
}
