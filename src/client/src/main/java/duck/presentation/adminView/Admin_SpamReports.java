package duck.presentation.adminView;

import duck.bus.SpamReportBUS;

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
import java.util.List;
import java.util.Map;

public class Admin_SpamReports {
    private SpamReportBUS spamReportBUS;
    private List<Map<String, Object>> spamReportList;
    private ObservableList<Map<String, Object>> spamReports;
    private ObservableList<Map<String, Object>> filteredReports;

    public Admin_SpamReports() {
        spamReportBUS = new SpamReportBUS();
        spamReportList = spamReportBUS.getSpamReports(null, null, null, null);
        spamReports = FXCollections.observableArrayList(spamReportList);
        filteredReports = FXCollections.observableArrayList(spamReports);
    }
    
    public BorderPane getContent() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-padding: 20;");

        TableView<Map<String, Object>> reportTable = new TableView<>();
        reportTable.setItems(filteredReports);

        TableColumn<Map<String, Object>, String> usernameColumn = new TableColumn<>("Tên đăng nhập"); 
        usernameColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty((String)data.getValue().get("username")));
        usernameColumn.setStyle("-fx-alignment: CENTER;");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        TableColumn<Map<String, Object>, String> timestampColumn = new TableColumn<>("Thời gian");
        timestampColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(((LocalDateTime)data.getValue().get("reportTime")).format(formatter)));
        timestampColumn.setStyle("-fx-alignment: CENTER;");

        TableColumn<Map<String, Object>, Void> lockColumn = new TableColumn<>("Tùy chọn");
        lockColumn.setCellFactory(_ -> new TableCell<>() {
            private final Button lockButton = new Button("Khóa tài khoản");

            {
                lockButton.setStyle("-fx-font-size: 12px; -fx-padding: 5 10; -fx-background-color: #ff6666; -fx-text-fill: white;");
                lockButton.setOnAction(_ -> {
                    Map<String, Object> report = getTableView().getItems().get(getIndex());
                    int reportedId = (int) report.get("reportedId");
                    if (spamReportBUS.lockUser(reportedId)) {
                        spamReports.removeIf(r -> (int) r.get("reportedId") == reportedId);
                        filteredReports.removeIf(r -> (int) r.get("reportedId") == reportedId);
                        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Tài khoản đã bị khóa!", ButtonType.OK);
                        alert.showAndWait();
                    } else {
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Không thể khóa tài khoản!", ButtonType.OK);
                        alert.showAndWait();
                    }
                    
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

      
        reportTable.getColumns().addAll(usernameColumn, timestampColumn, lockColumn);
        reportTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        HBox controls = createControls(reportTable);

        VBox content = new VBox(10, controls, reportTable);
        content.setStyle("-fx-padding: 10; -fx-background-color: #f9f9f9; -fx-border-color: #ddd; -fx-border-width: 1;");
        root.setCenter(content);

        return root;
    }

    private HBox createControls(TableView<Map<String, Object>> spamTable) {
        TextField searchField = new TextField();
        searchField.setPromptText("Lọc theo tên đăng nhập");
        searchField.setPrefWidth(150);

        DatePicker startDatePicker = new DatePicker();
        startDatePicker.setPromptText("Từ ngày");
        startDatePicker.setPrefWidth(150);

        DatePicker endDatePicker = new DatePicker();
        endDatePicker.setPromptText("Đến ngày");
        endDatePicker.setPrefWidth(150);


        Button applyFilterButton = new Button("Lọc");
        applyFilterButton.setStyle("-fx-font-size: 12px; -fx-padding: 5 10; -fx-background-color: #4CAF50; -fx-text-fill: white;");
        applyFilterButton.setOnAction(_ -> {
            String keyword = searchField.getText().toLowerCase();
            LocalDateTime startDate = startDatePicker.getValue() != null ? startDatePicker.getValue().atStartOfDay() : null;
            LocalDateTime endDate = endDatePicker.getValue() != null ? endDatePicker.getValue().atTime(23, 59, 59) : null;

            filteredReports.setAll(spamReports.filtered(report -> {
                boolean matchesKeyword = ((String)report.get("username")).toLowerCase().contains(keyword);
                boolean matchesDate = true;

                if (startDate != null || endDate != null) {
                    LocalDateTime reportDate = (LocalDateTime) report.get("reportTime");
                    matchesDate = (startDate == null || !reportDate.isBefore(startDate)) &&
                                  (endDate == null || !reportDate.isAfter(endDate));
                }

                return matchesKeyword && matchesDate;
            }));
        });


        HBox filters = new HBox(10, searchField, startDatePicker, endDatePicker, applyFilterButton);
        filters.setStyle("-fx-padding: 10; -fx-background-color: #f1f1f1; -fx-border-color: #ddd; -fx-border-width: 1;");
        return filters;
    }

    public void start(Stage stage) {
        Scene scene = new Scene(getContent(), 800, 600);
        stage.setScene(scene);
        stage.setTitle("Quản lý báo cáo spam");
        stage.show();
    }
}
