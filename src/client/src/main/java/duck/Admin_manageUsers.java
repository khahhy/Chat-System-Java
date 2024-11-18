package duck;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;

public class Admin_manageUsers {

    public class User {
        private String username;
        private String fullName;
        private String address;
        private LocalDate dob;
        private String gender;
        private String email;
        private LocalDateTime createdAt;

        public User(String username, String fullName, String address, LocalDate dob, String gender, String email, LocalDateTime createdAt) {
            this.username = username;
            this.fullName = fullName;
            this.address = address;
            this.dob = dob;
            this.gender = gender;
            this.email = email;
            this.createdAt = createdAt;
        }

        public String getUsername() { return username; }
        public String getFullName() { return fullName; }
        public String getAddress() { return address; }
        public LocalDate getDob() { return dob; }
        public String getGender() { return gender; }
        public String getEmail() { return email; }
        public String getFormattedCreatedAt() {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            return createdAt.format(formatter);
        }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        public void setAddress(String address) { this.address = address; }
        public void setDob(LocalDate dob) { this.dob = dob; }
        public void setGender(String gender) { this.gender = gender; }
        public void setEmail(String email) { this.email = email; }
    }

    public class LoginRecord {
        private final String date;
        private final String time;

        public LoginRecord(String date, String time) {
            this.date = date;
            this.time = time;
        }
        public String getDate() {return date;}
        public String getTime() {return time;}
    }

    private final ObservableList<User> users = FXCollections.observableArrayList(
        new User("user01", "Nguyễn Văn A", "Hà Nội", LocalDate.of(1990, 1, 15), "Nam", "nva@gmail.com", LocalDateTime.now()),
        new User("user02", "Trần Thị B", "Hồ Chí Minh", LocalDate.of(1995, 3, 10), "Nữ", "ttb@yahoo.com", LocalDateTime.of(2023, 11, 17, 15, 30, 0)),
        new User("user03", "Phạm Minh C", "Đà Nẵng", LocalDate.of(1988, 6, 20), "Nam", "pmc@outlook.com", LocalDateTime.of(2023, 11, 16, 10, 45, 30)),
        new User("user04", "Đỗ Quốc D", "Cần Thơ", LocalDate.of(1992, 11, 5), "Nam", "dqd@gmail.com", LocalDateTime.of(2023, 11, 15, 9, 0, 0)),
        new User("user05", "Nguyễn Văn E", "Hải Phòng", LocalDate.of(1999, 9, 25), "Nam", "nve@gmail.com", LocalDateTime.of(2023, 11, 14, 14, 20, 45))
    );

    public BorderPane getContent() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-padding: 20;");

        
        TableView<User> userTable = new TableView<>();
        userTable.setItems(users);

        TableColumn<User, String> usernameColumn = new TableColumn<>("Tên đăng nhập");
        usernameColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getUsername()));

        TableColumn<User, String> fullNameColumn = new TableColumn<>("Họ tên");
        fullNameColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getFullName()));

        TableColumn<User, String> addressColumn = new TableColumn<>("Địa chỉ");
        addressColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getAddress()));

        TableColumn<User, String> dobColumn = new TableColumn<>("Ngày sinh");
        dobColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getDob().toString()));

        TableColumn<User, String> genderColumn = new TableColumn<>("Giới tính");
        genderColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getGender()));

        TableColumn<User, String> emailColumn = new TableColumn<>("Email");
        emailColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getEmail()));

        TableColumn<User, Void> optionsColumn = new TableColumn<>("Tùy chọn");
        optionsColumn.setCellFactory(_ -> new TableCell<>() {
            private final MenuButton optionsButton = new MenuButton("Tùy chọn");

            {
                MenuItem viewFriends = new MenuItem("Danh sách bạn bè");
                MenuItem loginHistory = new MenuItem("Lịch sử đăng nhập");
                MenuItem edit = new MenuItem("Chỉnh sửa");
                MenuItem delete = new MenuItem("Xóa");

                viewFriends.setOnAction(_ -> showFriendsPopup(getTableView().getItems().get(getIndex())));
                loginHistory.setOnAction(_ -> showLoginHistoryPopup(getTableView().getItems().get(getIndex())));
                edit.setOnAction(_ -> showEditUserPopup(getTableView().getItems().get(getIndex())));
                delete.setOnAction(_ -> users.remove(getTableView().getItems().get(getIndex())));

                optionsButton.getItems().addAll(viewFriends, loginHistory, edit, delete);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(optionsButton);
                }
            }
        });

        userTable.getColumns().addAll(usernameColumn, fullNameColumn, addressColumn, dobColumn, genderColumn, emailColumn, optionsColumn);
        userTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        
        HBox searchAndFilterControls = createSearchAndFilterControls(userTable);

        
        VBox content = new VBox(10, searchAndFilterControls, userTable);
        root.setCenter(content);
        return root;
    }

    private HBox createSearchAndFilterControls(TableView<User> userTable) {
        
        TextField searchField = new TextField();
        searchField.setPromptText("Tìm kiếm...");
        searchField.setPrefWidth(200);

        
        ComboBox<String> filterOptions = new ComboBox<>(FXCollections.observableArrayList("Tên", "Tên đăng nhập", "Địa chỉ", "Email", "Ngày tạo"));
        filterOptions.setValue("Tên");

        Button addUserButton = new Button("Thêm người dùng");
        addUserButton.setOnAction(_ -> showAddUserPopup(userTable));

        searchField.textProperty().addListener((_, _, newValue) -> {
            String filterType = filterOptions.getValue();
            userTable.setItems(applySearchFilter(newValue, filterType));
        });


        searchField.textProperty().addListener((_, _, newValue) -> {
            String filterType = filterOptions.getValue();
            userTable.setItems(applySearchFilter(newValue, filterType));
        });

       
        HBox searchAndFilterBox = new HBox(10, searchField, filterOptions, addUserButton);
        searchAndFilterBox.setStyle("-fx-padding: 10;");
        return searchAndFilterBox;
    }

    private ObservableList<User> applySearchFilter(String keyword, String filterType) {
        if (keyword == null || keyword.isEmpty()) {
            return users; // Hiển thị tất cả nếu không có từ khóa
        }

        String lowerKeyword = keyword.toLowerCase();

        return users.filtered(user -> {
            switch (filterType) {
                case "Tên":
                    return user.getFullName().toLowerCase().contains(lowerKeyword);
                case "Tên đăng nhập":
                    return user.getUsername().toLowerCase().contains(lowerKeyword);
                case "Địa chỉ":
                    return user.getAddress().toLowerCase().contains(lowerKeyword);
                case "Email":
                    return user.getEmail().toLowerCase().contains(lowerKeyword);
                case "Ngày tạo":
                    return user.getFormattedCreatedAt().contains(keyword);

                default:
                    return false;
            }
        });
    }

    private void showAddUserPopup(TableView<User> userTable) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.initStyle(StageStyle.UTILITY);
        popup.setTitle("Thêm người dùng mới");

        VBox content = new VBox(10);
        content.setStyle("-fx-padding: 20;");

        TextField usernameField = new TextField();
        TextField fullNameField = new TextField();
        TextField addressField = new TextField();
        DatePicker dobPicker = new DatePicker();
        ComboBox<String> genderBox = new ComboBox<>(FXCollections.observableArrayList("Nam", "Nữ"));
        TextField emailField = new TextField();

        Button saveButton = new Button("Thêm");
        saveButton.setOnAction(_ -> {
            User newUser = new User(
                usernameField.getText(),
                fullNameField.getText(),
                addressField.getText(),
                dobPicker.getValue(),
                genderBox.getValue(),
                emailField.getText(),
                LocalDateTime.now()
            );
            users.add(newUser);
            popup.close();
            userTable.refresh();
        });

        content.getChildren().addAll(
            new Label("Tên đăng nhập:"), usernameField,
            new Label("Họ tên:"), fullNameField,
            new Label("Địa chỉ:"), addressField,
            new Label("Ngày sinh:"), dobPicker,
            new Label("Giới tính:"), genderBox,
            new Label("Email:"), emailField,
            saveButton
        );

        Scene scene = new Scene(content, 400, 600);
        popup.setScene(scene);
        popup.showAndWait();
    }

    private void showFriendsPopup(User user) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.initStyle(StageStyle.UTILITY);
        popup.setTitle("Danh sách bạn bè");

        VBox content = new VBox(10);
        content.setStyle("-fx-padding: 20;");

        Label title = new Label("Danh sách bạn bè của " + user.getUsername());
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        TableView<String> friendsTable = new TableView<>();
        friendsTable.setPrefSize(300, 200);

        TableColumn<String, String> friendNameColumn = new TableColumn<>("Bạn bè");
        friendNameColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()));

        ObservableList<String> friendNames = FXCollections.observableArrayList(
            "Nguyễn Văn A",
            "Trần Thị B",
            "Phạm Minh C"
        );
        friendsTable.setItems(friendNames);
        friendsTable.getColumns().add(friendNameColumn);
        friendsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        content.getChildren().addAll(title, friendsTable);

        Scene scene = new Scene(content, 350, 300);
        popup.setScene(scene);
        popup.showAndWait();
    }

    private void showLoginHistoryPopup(User user) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.initStyle(StageStyle.UTILITY);
        popup.setTitle("Lịch sử đăng nhập");

        VBox content = new VBox(10);
        content.setStyle("-fx-padding: 20;");
        Label title = new Label("Lịch sử đăng nhập của " + user.getUsername());
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        TableView<LoginRecord> loginHistoryTable = new TableView<>();
        loginHistoryTable.setPrefSize(300, 200);

        TableColumn<LoginRecord, String> dateColumn = new TableColumn<>("Ngày");
        dateColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getDate()));

        TableColumn<LoginRecord, String> timeColumn = new TableColumn<>("Giờ");
        timeColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getTime()));

        ObservableList<LoginRecord> loginRecords = FXCollections.observableArrayList(
            new LoginRecord("18/11/2024", "10:15:30"),
            new LoginRecord("17/11/2024", "22:45:10"),
            new LoginRecord("16/11/2024", "08:30:00")
        );
        loginHistoryTable.setItems(loginRecords);
        loginHistoryTable.getColumns().addAll(dateColumn, timeColumn);
        loginHistoryTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        content.getChildren().addAll(title, loginHistoryTable);

        Scene scene = new Scene(content, 350, 300);
        popup.setScene(scene);
        popup.showAndWait();
    }

    private void showEditUserPopup(User user) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.initStyle(StageStyle.UTILITY);
        popup.setTitle("Chỉnh sửa thông tin");

        VBox content = new VBox(10);
        content.setStyle("-fx-padding: 20;");

        TextField usernameField = new TextField(user.getUsername());
        usernameField.setDisable(true);

        TextField fullNameField = new TextField(user.getFullName());
        TextField addressField = new TextField(user.getAddress());

        DatePicker dobPicker = new DatePicker(user.getDob());

        ComboBox<String> genderBox = new ComboBox<>(FXCollections.observableArrayList("Nam", "Nữ"));
        genderBox.setValue(user.getGender());

        TextField emailField = new TextField(user.getEmail());

        Button saveButton = new Button("Save");
        saveButton.setStyle("-fx-font-size: 14px;");
        saveButton.setOnAction(_ -> {
            user.setFullName(fullNameField.getText());
            user.setAddress(addressField.getText());
            user.setDob(dobPicker.getValue());
            user.setGender(genderBox.getValue());
            user.setEmail(emailField.getText());
            popup.close();
        });

        content.getChildren().addAll(
            new Label("Tên đăng nhập:"), usernameField,
            new Label("Họ tên:"), fullNameField,
            new Label("Địa chỉ:"), addressField,
            new Label("Ngày sinh:"), dobPicker,
            new Label("Giới tính:"), genderBox,
            new Label("Email:"), emailField,
            saveButton
        );

        Scene scene = new Scene(content, 400, 600);
        popup.setScene(scene);
        popup.showAndWait();
    }
}
