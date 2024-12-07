package duck.presentation.adminView;

import duck.bus.UserBUS;
import duck.dto.UserDTO;

import duck.presentation.adminView.Admin_LoginHistory.LoginRecord;
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
import java.util.List;
import java.time.LocalDate;

public class Admin_manageUsers {
    private UserBUS userBUS;
    List<UserDTO> userList;
    ObservableList<UserDTO> users;

    public Admin_manageUsers() {
        userBUS = new UserBUS();
        userList = userBUS.searchUsers("", "", true);
        users = FXCollections.observableArrayList(userList);
    }

    public BorderPane getContent() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-padding: 20;");

        
        TableView<UserDTO> userTable = new TableView<>();
        userTable.setItems(users);

        TableColumn<UserDTO, String> usernameColumn = new TableColumn<>("Tên đăng nhập");
        usernameColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getUsername()));

        TableColumn<UserDTO, String> fullNameColumn = new TableColumn<>("Họ tên");
        fullNameColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getFullName()));

        TableColumn<UserDTO, String> addressColumn = new TableColumn<>("Địa chỉ");
        addressColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getAddress()));

        TableColumn<UserDTO, String> dobColumn = new TableColumn<>("Ngày sinh");
        dobColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getDateOfBirth().toString()));

        TableColumn<UserDTO, String> genderColumn = new TableColumn<>("Giới tính");
        genderColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(Character.toString(data.getValue().getGender())));

        TableColumn<UserDTO, String> emailColumn = new TableColumn<>("Email");
        emailColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getEmail()));

        TableColumn<UserDTO, Void> optionsColumn = new TableColumn<>("Tùy chọn");
        
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

    private HBox createSearchAndFilterControls(TableView<UserDTO> userTable) {
        
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

    private ObservableList<UserDTO> applySearchFilter(String keyword, String filterType) {
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
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                    return user.getCreatedAt().format(formatter).contains(keyword);

                default:
                    return false;
            }
        });
    }

    private void showAddUserPopup(TableView<UserDTO> userTable) {
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
            char genderChar = genderBox.getValue().equals("Nam") ? 'M' : 'F';

            UserDTO newUser = new UserDTO(
                0,
                usernameField.getText(),
                fullNameField.getText(),
                addressField.getText(),
                dobPicker.getValue().atStartOfDay(),
                genderChar, // Chuyển đổi từ String sang char
                emailField.getText(),
                "123", // mat khau
                true, // status, delault active
                false, // offline
                LocalDateTime.now(),
                false
            );
            if (userBUS.addUser(newUser)) {
                users.add(newUser);  
                popup.close();
                userTable.refresh();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Lỗi");
                alert.setHeaderText("Không thể thêm người dùng");
                alert.setContentText("Có lỗi xảy ra khi thêm người dùng mới.");
                alert.showAndWait();
            }
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

    private void showFriendsPopup(UserDTO user) {
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

    private void showLoginHistoryPopup(UserDTO user) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.initStyle(StageStyle.UTILITY);
        popup.setTitle("Lịch sử đăng nhập");

        VBox content = new VBox(10);
        content.setStyle("-fx-padding: 20;");
        Label title = new Label("Lịch sử đăng nhập của " + user.getUsername());
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        /*
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
        popup.showAndWait();*/
    }

    private void showEditUserPopup(UserDTO user) {
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

        DatePicker dobPicker = new DatePicker(user.getDateOfBirth().toLocalDate());
        LocalDate selectedDate = dobPicker.getValue();

        ComboBox<String> genderBox = new ComboBox<>(FXCollections.observableArrayList("Nam", "Nữ"));
        String gender = user.getGender() == 'M' ? "Nam" : "Nữ";  
        genderBox.setValue(gender);

        TextField emailField = new TextField(user.getEmail());

        Button saveButton = new Button("Save");
        saveButton.setStyle("-fx-font-size: 14px;");
        saveButton.setOnAction(_ -> {
            user.setFullName(fullNameField.getText());
            user.setAddress(addressField.getText());
            user.setDateOfBirth(selectedDate.atStartOfDay());
            user.setGender(genderBox.getValue().equals("Nam") ? 'M' : 'F');
            user.setEmail(emailField.getText());
            popup.close();
        });
        
        userBUS.updateUser(user);

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
