package duck.presentation.adminView;

import duck.bus.SpamReportBUS;
import duck.bus.UserBUS;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class Admin_newUsers {
    private UserBUS userBUS;
    private List<Map<String, Object>> signupList;
    private ObservableList<Map<String, Object>> newSignup;
    private ObservableList<Map<String, Object>> filteredUsers;

   
    public Admin_newUsers() {
        userBUS = new UserBUS();
        signupList = userBUS.getNewUsers(null, null);
        newSignup = FXCollections.observableArrayList(signupList);
        filteredUsers = FXCollections.observableArrayList(newSignup);
    }
    
    public BorderPane getContent() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-padding: 20;");

        TableView<Map<String, Object>> userTable = new TableView<>();
        userTable.setItems(filteredUsers);

        TableColumn<Map<String, Object>, String> usernameColumn = new TableColumn<>("Tên đăng nhập");
        usernameColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty((String)data.getValue().get("username")));
        usernameColumn.setStyle("-fx-alignment: CENTER;");

        TableColumn<Map<String, Object>, String> fullNameColumn = new TableColumn<>("Họ tên");
        fullNameColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty((String)data.getValue().get("fullname")));
        fullNameColumn.setStyle("-fx-alignment: CENTER;");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        TableColumn<Map<String, Object>, String> createdAtColumn = new TableColumn<>("Thời gian tạo");
        createdAtColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(((LocalDateTime)data.getValue().get("createdAt")).format(formatter)));
        createdAtColumn.setStyle("-fx-alignment: CENTER;");

       
        userTable.getColumns().addAll(usernameColumn, fullNameColumn, createdAtColumn);
        userTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        HBox controls = createControls(userTable, root);

        VBox content = new VBox(10, controls, userTable);
        content.setStyle("-fx-padding: 10; -fx-background-color: #f9f9f9; -fx-border-color: #ddd; -fx-border-width: 1;");
        root.setCenter(content);

        return root;
    }

    private HBox createControls(TableView<Map<String, Object>> userTable, BorderPane root) {
        TextField searchField = new TextField();
        searchField.setPromptText("Lọc theo tên...");
        searchField.setPrefWidth(200);

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
        
            List<Map<String, Object>> filteredList = newSignup.stream()
                .filter(user -> {
                    boolean matchesKeyword = true;
                    boolean matchesDate = true;
        
                    if (!keyword.isEmpty()) {
                        String fullName = ((String) user.get("fullname")).toLowerCase();
                        matchesKeyword = fullName.contains(keyword);
                    }
        
                    if (startDate != null || endDate != null) {
                        LocalDateTime createdAt = (LocalDateTime) user.get("createdAt");
                        matchesDate = (startDate == null || !createdAt.isBefore(startDate)) &&
                                      (endDate == null || !createdAt.isAfter(endDate));
                    }
        
                    return matchesKeyword && matchesDate;
                })
                .toList();
        
            filteredUsers.setAll(filteredList); 
        });
        
      

        Button chartButton = new Button("Biểu đồ trực quan");
        chartButton.setStyle("-fx-font-size: 12px; -fx-padding: 5 10; -fx-background-color: #2196F3; -fx-text-fill: white;");
        chartButton.setOnAction(_ -> showChartPage(root));

        HBox filters = new HBox(10, searchField, startDatePicker, endDatePicker, applyFilterButton, chartButton);
        filters.setStyle("-fx-padding: 10; -fx-background-color: #f1f1f1; -fx-border-color: #ddd; -fx-border-width: 1;");
        return filters;
    }

    private void showChartPage(BorderPane root) {
        VBox chartContent = new VBox(10);
        chartContent.setStyle("-fx-padding: 20;");
    
        ComboBox<Integer> yearSelector = new ComboBox<>(FXCollections.observableArrayList(2023, 2024));
        yearSelector.setValue(2024); // default 2024
    
        Button loadChartButton = new Button("Hiển thị biểu đồ");
        loadChartButton.setStyle("-fx-font-size: 12px; -fx-padding: 5 10; -fx-background-color: #4CAF50; -fx-text-fill: white;");
    
        HBox controls = new HBox(10, new Label("Chọn năm:"), yearSelector, loadChartButton);
        controls.setStyle("-fx-padding: 10; -fx-background-color: #f1f1f1; -fx-border-color: #ddd; -fx-border-width: 1;");
    
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Tháng");
    
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Số lượng đăng ký");
    
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Số lượng người đăng ký theo tháng");
    
        
        loadChartButton.setOnAction(_ -> {
            Integer selectedYear = yearSelector.getValue();
            XYChart.Series<String, Number> series = generateChartData(selectedYear);
            series.setName("Năm " + selectedYear);
    
            barChart.getData().clear(); 
            barChart.getData().add(series);
        });
    
        Button backButton = new Button("Quay lại");
        backButton.setStyle("-fx-font-size: 12px; -fx-padding: 5 10; -fx-background-color: #ff6666; -fx-text-fill: white;");
        backButton.setOnAction(_ -> root.setCenter(getContent().getCenter()));
    
        chartContent.getChildren().addAll(controls, barChart, backButton);
        root.setCenter(chartContent);
    }
    // tạo sơ sơ dữ liệu để chạy ui
    private XYChart.Series<String, Number> generateChartData(int year) {
        int[] monthlyData = new int[12];
    
        for (int i = 0; i < 12; i++) 
            monthlyData[i] = (int) (Math.random() * 100); 
        
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (int i = 0; i < 12; i++) 
            series.getData().add(new XYChart.Data<>(String.valueOf(i + 1), monthlyData[i]));
        return series;
    }

    public void start(Stage stage) {
        Scene scene = new Scene(getContent(), 800, 600);
        stage.setScene(scene);
        stage.setTitle("Quản lý người dùng đăng ký mới");
        stage.show();
    }
}
