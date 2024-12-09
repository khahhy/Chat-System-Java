package duck.presentation.adminView;

import duck.bus.LoginHistoryBUS;
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
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Admin_userActivity {
    private UserBUS userBUS;
    private List<Map<String, Object>> activityList;
    //private ObservableList<Map<String, Object>> activities;
    private ObservableList<Map<String, Object>> filteredActivities;
    VBox content;
    
    public Admin_userActivity() {
        userBUS = new UserBUS();
        activityList = userBUS.getActivities(null, null);
        //activities = FXCollections.observableArrayList(activityList);
        filteredActivities = FXCollections.observableArrayList(activityList);
    
        getContent();
    }
    
    public BorderPane getContent() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-padding: 20;");

        TableView<Map<String, Object>> activityTable = new TableView<>();
        activityTable.setItems(filteredActivities);

        TableColumn<Map<String, Object>, String> usernameColumn = new TableColumn<>("Tên đăng nhập");
        usernameColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty((String)data.getValue().get("username")));
        usernameColumn.setStyle("-fx-alignment: CENTER;");

        TableColumn<Map<String, Object>, String> fullNameColumn = new TableColumn<>("Họ tên");
        fullNameColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty((String)data.getValue().get("fullname")));
        fullNameColumn.setStyle("-fx-alignment: CENTER;");

        TableColumn<Map<String, Object>, Integer> openedAppColumn = new TableColumn<>("Mở ứng dụng");
        openedAppColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>((int)data.getValue().get("logins")));
        openedAppColumn.setStyle("-fx-alignment: CENTER;");

        TableColumn<Map<String, Object>, Integer> chattedPeopleColumn = new TableColumn<>("Chat cá nhân");
        chattedPeopleColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>((int)data.getValue().get("chatUsers")));
        chattedPeopleColumn.setStyle("-fx-alignment: CENTER;");

        TableColumn<Map<String, Object>, Integer> chattedGroupsColumn = new TableColumn<>("Chat nhóm");
        chattedGroupsColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>((int)data.getValue().get("chatGroups")));
        chattedGroupsColumn.setStyle("-fx-alignment: CENTER;");

        TableColumn<Map<String, Object>, LocalDateTime> createdAtColumn = new TableColumn<>("Thời gian tạo");
        createdAtColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(((LocalDateTime) data.getValue().get("createdAt"))));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        createdAtColumn.setCellFactory(_ -> new TableCell<>() {
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item.format(formatter)); 
            }
        });

        createdAtColumn.setStyle("-fx-alignment: CENTER;");

        activityTable.getColumns().addAll(usernameColumn, fullNameColumn, openedAppColumn, chattedPeopleColumn, chattedGroupsColumn, createdAtColumn);
        activityTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        HBox controls = createControls(activityTable, root);

        content = new VBox(10, controls, activityTable);
        VBox.setVgrow(activityTable, Priority.ALWAYS);
        content.setStyle("-fx-padding: 10; -fx-background-color: #f9f9f9; -fx-border-color: #ddd; -fx-border-width: 1;");
        root.setCenter(content);

        return root;
    }

    private HBox createControls(TableView<Map<String, Object>> activityTable, BorderPane root) {
        TextField searchField = new TextField();
        searchField.setPromptText("Tìm kiếm");
        searchField.setPrefWidth(100);

        DatePicker startDatePicker = new DatePicker();
        startDatePicker.setPromptText("Từ ...");
        startDatePicker.setPrefWidth(150);

        DatePicker endDatePicker = new DatePicker();
        endDatePicker.setPromptText("Đến ...");
        endDatePicker.setPrefWidth(150);

        TextField activityFilter = new TextField();
        activityFilter.setPromptText("Số lượng hoạt động");
        activityFilter.setPrefWidth(50);

        ComboBox<String> filterOptions = new ComboBox<>(FXCollections.observableArrayList("Bằng", "Nhỏ hơn", "Lớn hơn"));
        filterOptions.setValue("Bằng");

        Button applyFilterButton = new Button("Lọc");
        applyFilterButton.setStyle("-fx-font-size: 12px; -fx-padding: 5 10; -fx-background-color: #4CAF50; -fx-text-fill: white;");
        
        applyFilterButton.setOnAction(_ -> {
            String keyword = searchField.getText().toLowerCase();
            LocalDateTime startDate = startDatePicker.getValue() != null ? startDatePicker.getValue().atStartOfDay() : null;
            LocalDateTime endDate = endDatePicker.getValue() != null ? endDatePicker.getValue().atTime(23, 59, 59) : null;
            String filterType = filterOptions.getValue();
            int activityCount = activityFilter.getText().isEmpty() ? -1 : Integer.parseInt(activityFilter.getText());

            List<Map<String, Object>> filteredList = userBUS.getActivities(startDate, endDate).stream()
                .filter(user -> {
                    boolean matchesKeyword = true;
                    boolean matchesTotal = true;
        
                    if (!keyword.isEmpty()) {
                        String fullName = ((String) user.get("fullname")).toLowerCase();
                        matchesKeyword = fullName.contains(keyword);
                    }
        
                    if (activityCount != -1) {
                    switch (filterType) {
                        case "Bằng":
                            matchesTotal = ((int) user.get("totalActivities")) == activityCount;
                            break;
                        case "Nhỏ hơn":
                            matchesTotal = ((int) user.get("totalActivities")) < activityCount;
                            break;
                        case "Lớn hơn":
                            matchesTotal = ((int) user.get("totalActivities")) > activityCount;
                            break;
                    }
                }
        
                    return matchesKeyword && matchesTotal;
                })
                .toList();
        
            filteredActivities.setAll(filteredList); 
        });


        Button chartButton = new Button("Biểu đồ");
        chartButton.setStyle("-fx-font-size: 12px; -fx-padding: 5 10; -fx-background-color: #2196F3; -fx-text-fill: white;");
        chartButton.setOnAction(_ -> showChartPage(root));

        HBox filters = new HBox(10, searchField, activityFilter, filterOptions, startDatePicker, endDatePicker, applyFilterButton, chartButton);
        filters.setStyle("-fx-padding: 10; -fx-background-color: #f1f1f1; -fx-border-color: #ddd; -fx-border-width: 1;");
        return filters;
    }

    private void showChartPage(BorderPane root) {
        LoginHistoryBUS lhBUS = new LoginHistoryBUS();
        VBox chartContent = new VBox(10);
        chartContent.setStyle("-fx-padding: 20;");
        
        int currentYear = LocalDate.now().getYear();
        int firstYear = lhBUS.getAllLoginHistory().stream()
                         .map(login -> ((LocalDateTime)login.get("loginTime")).getYear())
                         .min(Integer::compare)
                         .orElse(currentYear);
        ObservableList<Integer> years = FXCollections.observableArrayList();
            for (int year = firstYear; year <= currentYear; year++) 
                years.add(year);
            

        ComboBox<Integer> yearSelector = new ComboBox<>(years);
        yearSelector.setValue(currentYear); 
    
        Button loadChartButton = new Button("Hiển thị biểu đồ");
        loadChartButton.setStyle("-fx-font-size: 12px; -fx-padding: 5 10; -fx-background-color: #4CAF50; -fx-text-fill: white;");
    
        HBox controls = new HBox(10, new Label("Chọn năm:"), yearSelector, loadChartButton);
        controls.setStyle("-fx-padding: 10; -fx-background-color: #f1f1f1; -fx-border-color: #ddd; -fx-border-width: 1;");
    
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Tháng");
    
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Số lượng người mở ứng dụng");
        yAxis.setTickUnit(1); // don vi 1
        yAxis.setForceZeroInRange(true);
    
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Số lượng người mở ứng dụng theo tháng");
    
        loadChartButton.setOnAction(_ -> {
            Integer selectedYear = yearSelector.getValue();
            XYChart.Series<String, Number> series = generateChartData(selectedYear);
            series.setName("Năm " + selectedYear);
    
            barChart.getData().clear(); 
            barChart.getData().add(series);
        });
    
        Button backButton = new Button("Quay lại");
        backButton.setStyle("-fx-font-size: 12px; -fx-padding: 5 10; -fx-background-color: #ff6666; -fx-text-fill: white;");
        backButton.setOnAction(_ -> root.setCenter(content));
    
        chartContent.getChildren().addAll(controls, barChart, backButton);
        root.setCenter(chartContent);
    }
    
    private XYChart.Series<String, Number> generateChartData(int year) {
        Map<Integer, Set<Integer>> monthlyUserData = new HashMap<>();
        for (int i = 1; i <= 12; i++) {
            monthlyUserData.put(i, new HashSet<>()); 
        }
    
        LoginHistoryBUS lhBUS = new LoginHistoryBUS();
        lhBUS.getAllLoginHistory().stream()
            .filter(login -> ((LocalDateTime) login.get("loginTime")).getYear() == year)
            .forEach(login -> {
                int month = ((LocalDateTime) login.get("loginTime")).getMonthValue();
                int userId = (int) login.get("userid"); 
                monthlyUserData.get(month).add(userId); 
            });
    
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (int i = 1; i <= 12; i++) {
            int uniqueUsers = monthlyUserData.get(i).size(); 
            series.getData().add(new XYChart.Data<>(String.valueOf(i), uniqueUsers));
        }
        return series;
    }
    

    public void start(Stage stage) {
        Scene scene = new Scene(getContent(), 900, 600);
        stage.setScene(scene);
        stage.setTitle("Quản lý hoạt động người dùng");
        stage.show();
    }
}
