package duck.presentation.adminView;

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

public class Admin_userActivity {

    public class UserActivity {
        private String username;
        private String fullName;
        private LocalDateTime createdAt;
        private int openedAppCount;
        private int chattedPeopleCount;
        private int chattedGroupsCount;

        public UserActivity(String username, String fullName, LocalDateTime createdAt, int openedAppCount, int chattedPeopleCount, int chattedGroupsCount) {
            this.username = username;
            this.fullName = fullName;
            this.createdAt = createdAt;
            this.openedAppCount = openedAppCount;
            this.chattedPeopleCount = chattedPeopleCount;
            this.chattedGroupsCount = chattedGroupsCount;
        }

        public String getUsername() {return username;}
        public String getFullName() {return fullName;}
        public LocalDateTime getCreatedAt() {return createdAt;}
        public String getFormattedCreatedAt() {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            return createdAt.format(formatter);
        }
        public int getOpenedAppCount() {return openedAppCount;}
        public int getChattedPeopleCount() {return chattedPeopleCount;}
        public int getChattedGroupsCount() {return chattedGroupsCount;}
    }

    private final ObservableList<UserActivity> activities = FXCollections.observableArrayList(
        new UserActivity("user01", "Nguyễn Văn A", LocalDateTime.now().minusDays(1), 5, 20, 10),
        new UserActivity("user02", "Trần Thị B", LocalDateTime.now().minusDays(2), 3, 15, 7),
        new UserActivity("user03", "Phạm Minh C", LocalDateTime.now().minusDays(3), 7, 25, 12),
        new UserActivity("user04", "Đỗ Quốc D", LocalDateTime.now().minusDays(4), 10, 30, 15),
        new UserActivity("user05", "Nguyễn Văn E", LocalDateTime.now().minusDays(5), 2, 8, 3)
    );

    private final ObservableList<UserActivity> filteredActivities = FXCollections.observableArrayList(activities);

    public BorderPane getContent() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-padding: 20;");

        TableView<UserActivity> activityTable = new TableView<>();
        activityTable.setItems(filteredActivities);

        TableColumn<UserActivity, String> usernameColumn = new TableColumn<>("Tên đăng nhập");
        usernameColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getUsername()));
        usernameColumn.setStyle("-fx-alignment: CENTER;");

        TableColumn<UserActivity, String> fullNameColumn = new TableColumn<>("Họ tên");
        fullNameColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getFullName()));
        fullNameColumn.setStyle("-fx-alignment: CENTER;");

        TableColumn<UserActivity, Integer> openedAppColumn = new TableColumn<>("Mở ứng dụng");
        openedAppColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getOpenedAppCount()));
        openedAppColumn.setStyle("-fx-alignment: CENTER;");

        TableColumn<UserActivity, Integer> chattedPeopleColumn = new TableColumn<>("Chat với người");
        chattedPeopleColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getChattedPeopleCount()));
        chattedPeopleColumn.setStyle("-fx-alignment: CENTER;");

        TableColumn<UserActivity, Integer> chattedGroupsColumn = new TableColumn<>("Chat nhóm");
        chattedGroupsColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getChattedGroupsCount()));
        chattedGroupsColumn.setStyle("-fx-alignment: CENTER;");

        TableColumn<UserActivity, String> createdAtColumn = new TableColumn<>("Thời gian tạo");
        createdAtColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getFormattedCreatedAt()));
        createdAtColumn.setStyle("-fx-alignment: CENTER;");

        activityTable.getColumns().addAll(usernameColumn, fullNameColumn, openedAppColumn, chattedPeopleColumn, chattedGroupsColumn, createdAtColumn);
        activityTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        HBox controls = createControls(activityTable, root);

        VBox content = new VBox(10, controls, activityTable);
        content.setStyle("-fx-padding: 10; -fx-background-color: #f9f9f9; -fx-border-color: #ddd; -fx-border-width: 1;");
        root.setCenter(content);

        return root;
    }

    private HBox createControls(TableView<UserActivity> activityTable, BorderPane root) {
        TextField searchField = new TextField();
        searchField.setPromptText("Lọc theo tên");
        searchField.setPrefWidth(150);

        TextField activityFilter = new TextField();
        activityFilter.setPromptText("Nhập số lượng hoạt động...");
        activityFilter.setPrefWidth(100);

        ComboBox<String> filterOptions = new ComboBox<>(FXCollections.observableArrayList("Bằng", "Nhỏ hơn", "Lớn hơn"));
        filterOptions.setValue("Bằng");

        ComboBox<String> sortOptions = new ComboBox<>(FXCollections.observableArrayList(
            "Tên A-Z", "Tên Z-A", "Thời gian mới nhất", "Thời gian cũ nhất"
        ));
        sortOptions.setValue("Tên A-Z");

        Button applyFilterButton = new Button("Lọc");
        applyFilterButton.setStyle("-fx-font-size: 12px; -fx-padding: 5 10; -fx-background-color: #4CAF50; -fx-text-fill: white;");

        applyFilterButton.setOnAction(_ -> {
            String keyword = searchField.getText().toLowerCase();
            String filterType = filterOptions.getValue();
            int activityCount = activityFilter.getText().isEmpty() ? -1 : Integer.parseInt(activityFilter.getText());

            filteredActivities.setAll(activities.filtered(activity -> {
                boolean matchesName = activity.getFullName().toLowerCase().contains(keyword);
                boolean matchesActivity = true;

                if (activityCount != -1) {
                    switch (filterType) {
                        case "Bằng":
                            matchesActivity = activity.getOpenedAppCount() == activityCount;
                            break;
                        case "Nhỏ hơn":
                            matchesActivity = activity.getOpenedAppCount() < activityCount;
                            break;
                        case "Lớn hơn":
                            matchesActivity = activity.getOpenedAppCount() > activityCount;
                            break;
                    }
                }
                return matchesName && matchesActivity;
            }));
        });

        sortOptions.setOnAction(_ -> {
            String sortChoice = sortOptions.getValue();
            filteredActivities.sort((a1, a2) -> {
                switch (sortChoice) {
                    case "Tên A-Z":
                        return a1.getFullName().compareToIgnoreCase(a2.getFullName());
                    case "Tên Z-A":
                        return a2.getFullName().compareToIgnoreCase(a1.getFullName());
                    case "Thời gian mới nhất":
                        return a2.getCreatedAt().compareTo(a1.getCreatedAt());
                    case "Thời gian cũ nhất":
                        return a1.getCreatedAt().compareTo(a2.getCreatedAt());
                }
                return 0;
            });
        });

        Button chartButton = new Button("Biểu đồ");
        chartButton.setStyle("-fx-font-size: 12px; -fx-padding: 5 10; -fx-background-color: #2196F3; -fx-text-fill: white;");
        chartButton.setOnAction(_ -> showChartPage(root));

        HBox filters = new HBox(10, searchField, activityFilter, filterOptions, applyFilterButton, sortOptions, chartButton);
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
        yAxis.setLabel("Số lượng người mở ứng dụng");
    
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
        backButton.setOnAction(_ -> root.setCenter(getContent().getCenter()));
    
        chartContent.getChildren().addAll(controls, barChart, backButton);
        root.setCenter(chartContent);
    }
    
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
        Scene scene = new Scene(getContent(), 900, 600);
        stage.setScene(scene);
        stage.setTitle("Quản lý hoạt động người dùng");
        stage.show();
    }
}
