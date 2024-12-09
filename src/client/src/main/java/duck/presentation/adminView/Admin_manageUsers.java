package duck.presentation.adminView;
import duck.presentation.Email;

import duck.bus.UserBUS;
import duck.dto.UserDTO;

import duck.bus.LoginHistoryBUS;
import duck.dto.LoginHistoryDTO;

import duck.bus.FriendBUS;
import duck.dto.FriendDTO;

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


public class Admin_manageUsers {
    private UserBUS userBUS;
    List<UserDTO> userList;
    ObservableList<UserDTO> users;

    public Admin_manageUsers() {
        userBUS = new UserBUS();
        userList = userBUS.searchUsers("", "", null);
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
        fullNameColumn.setCellValueFactory(data -> {
            String fullname = (data.getValue().getFullName() == null || data.getValue().getFullName().equals("")) ? "Chưa cập nhật" : data.getValue().getFullName();
            return new javafx.beans.property.SimpleStringProperty(fullname);
        });

        TableColumn<UserDTO, String> addressColumn = new TableColumn<>("Địa chỉ");
        addressColumn.setCellValueFactory(data -> {
            String address = (data.getValue().getAddress() == null || data.getValue().getAddress().equals("")) ? "Chưa cập nhật" : data.getValue().getAddress();
            return new javafx.beans.property.SimpleStringProperty(address);
        });

        TableColumn<UserDTO, LocalDateTime> dobColumn = new TableColumn<>("Ngày sinh");
        dobColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>((LocalDateTime) data.getValue().getDateOfBirth()));

        dobColumn.setCellFactory(_ -> new TableCell<>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null) setText(item.format(formatter)); 
                else setText("Chưa cập nhật");  
            }
        });
        dobColumn.setStyle("-fx-alignment: CENTER;");

        TableColumn<UserDTO, String> genderColumn = new TableColumn<>("Giới tính");
        genderColumn.setCellValueFactory(data -> {
            char genderChar = data.getValue().getGender(); 
            String gender = (genderChar == 'M') ? "Nam" :
                    (genderChar == 'F') ? "Nữ" : "Chưa cập nhật";
            return new javafx.beans.property.SimpleStringProperty(gender);
        });

        TableColumn<UserDTO, String> emailColumn = new TableColumn<>("Email");
        emailColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getEmail()));

        TableColumn<UserDTO, Void> optionsColumn = new TableColumn<>("Tùy chọn");
        
        optionsColumn.setCellFactory(_ -> new TableCell<>() {
            private final MenuButton optionsButton = new MenuButton("Tùy chọn");
        
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
        
                if (empty) {
                    setGraphic(null);
                } else {
                    UserDTO user = getTableView().getItems().get(getIndex());
                    optionsButton.getItems().clear();
                
                    MenuItem edit = new MenuItem("Chỉnh sửa");
                    MenuItem delete = new MenuItem("Xóa");
                    MenuItem toggleLock = new MenuItem(user.isStatus() ? "Khóa" : "Mở khóa");
                    MenuItem editPassword = new MenuItem("Cập nhật mật khẩu");
                    MenuItem loginHistory = new MenuItem("Lịch sử đăng nhập");
                    MenuItem viewFriends = new MenuItem("Danh sách bạn bè");
        
                    viewFriends.setOnAction(_ -> showFriendsPopup(user));
                    loginHistory.setOnAction(_ -> showLoginHistoryPopup(user));
                    editPassword.setOnAction(_ -> showEditPasswordPopup(user));
                    edit.setOnAction(_ -> showEditUserPopup(user, userTable));
                    delete.setOnAction(_ -> {
                        users.remove(user);
                        userBUS.deleteUser(user.getUserId()); 
                    });
        
                    toggleLock.setOnAction(_ -> {
                        boolean isLocked = user.isStatus();
                        user.setStatus(!isLocked);
                        toggleLock.setText(user.isStatus() ? "Khóa" : "Mở khóa");
                        userBUS.lockUnlockUser(user.getUserId(), !isLocked);
                    });
        
                    optionsButton.getItems().addAll(edit, delete, toggleLock, editPassword, loginHistory, viewFriends);
        
                    setGraphic(optionsButton);
                }
            }
        });
        

        userTable.getColumns().addAll(usernameColumn, fullNameColumn, addressColumn, dobColumn, genderColumn, emailColumn, optionsColumn);
        userTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        HBox searchAndFilterControls = createSearchAndFilterControls(userTable);

        VBox content = new VBox(10, searchAndFilterControls, userTable);
        VBox.setVgrow(userTable, Priority.ALWAYS);
        root.setCenter(content);
        return root;
    }

    private HBox createSearchAndFilterControls(TableView<UserDTO> userTable) {
        
        TextField searchField = new TextField();
        searchField.setPromptText("Tìm kiếm...");
        searchField.setPrefWidth(200);

        ComboBox<String> statusFilter = new ComboBox<>(FXCollections.observableArrayList("Tất cả", "Online", "Offline"));
        statusFilter.setValue("Tất cả");
        
        Button addUserButton = new Button("Thêm người dùng");
        addUserButton.setOnAction(_ -> showAddUserPopup(userTable));

        searchField.textProperty().addListener((_, _, newValue) -> {
            userTable.setItems(applySearchFilter(newValue, statusFilter.getValue()));
        });

        statusFilter.valueProperty().addListener((_, _, newValue) -> {
            userTable.setItems(applySearchFilter(searchField.getText(), newValue));
        });

       
        HBox searchAndFilterBox = new HBox(10, searchField, statusFilter, addUserButton);
        searchAndFilterBox.setStyle("-fx-padding: 10;");
        return searchAndFilterBox;
    }

    private ObservableList<UserDTO> applySearchFilter(String keyword, String filterStatus) {
        if (keyword == null) 
            keyword = "";
        String lowerKeyword = keyword.toLowerCase();

        return users.filtered(user -> {
            boolean matchKeyword = user.getFullName().toLowerCase().contains(lowerKeyword) ||
                     user.getUsername().toLowerCase().contains(lowerKeyword);
            
            boolean matchStatus = switch (filterStatus) {
                case "Tất cả" -> true;
                case "Online" -> user.isOnline();
                case "Offline" -> !user.isOnline();
                default -> true;
            };
            
            return matchKeyword && matchStatus;
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
            UserDTO newUser = new UserDTO(
                0,
                usernameField.getText(),
                fullNameField.getText().isEmpty() ? null : fullNameField.getText(),
                addressField.getText().isEmpty() ? null : addressField.getText(),
                (dobPicker.getValue() != null) ? dobPicker.getValue().atStartOfDay() : null,
                genderBox.getValue() != null && genderBox.getValue().equals("Nam") ? 'M' : 
                      (genderBox.getValue() != null && genderBox.getValue().equals("Nữ") ? 'F' : 'U'),
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
        FriendBUS friendBUS = new FriendBUS();
        List<FriendDTO> friends = friendBUS.getFriendsByUserId(user.getUserId());
        ObservableList<FriendDTO> friendlist = FXCollections.observableArrayList(friends);

        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.initStyle(StageStyle.UTILITY);
        popup.setTitle("Danh sách bạn bè");

        VBox content = new VBox(10);
        content.setStyle("-fx-padding: 20;");

        Label title = new Label("Danh sách bạn bè của " + user.getUsername());
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        TableView<FriendDTO> friendsTable = new TableView<>();
        friendsTable.setPrefSize(300, 200);

        TableColumn<FriendDTO, String> friendNameColumn = new TableColumn<>("Bạn bè");
        friendNameColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(userBUS.getUserById(data.getValue().getFriendId()).getFullName()));

        friendsTable.setItems(friendlist);
        friendsTable.getColumns().add(friendNameColumn);
        friendsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        content.getChildren().addAll(title, friendsTable);

        Scene scene = new Scene(content, 350, 300);
        popup.setScene(scene);
        popup.showAndWait();
    }

    private void showLoginHistoryPopup(UserDTO user) {
        LoginHistoryBUS login_history_BUS = new LoginHistoryBUS();
        List<LoginHistoryDTO> loginHistoryList = login_history_BUS.getLoginHistoryByUserId(user.getUserId());
        ObservableList<LoginHistoryDTO> loginHistories = FXCollections.observableArrayList(loginHistoryList);

        TableView<LoginHistoryDTO> loginTable = new TableView<>();
        loginTable.setItems(loginHistories);

        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.initStyle(StageStyle.UTILITY);
        popup.setTitle("Lịch sử đăng nhập");

        VBox content = new VBox(10);
        content.setStyle("-fx-padding: 20;");
        Label title = new Label("Lịch sử đăng nhập của " + user.getUsername());
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        
        loginTable.setPrefSize(300, 200);

        DateTimeFormatter formatterTime = DateTimeFormatter.ofPattern("HH:mm:ss");
        DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        TableColumn<LoginHistoryDTO, String> dateColumn = new TableColumn<>("Ngày");
        dateColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getLoginTime().format(formatterDate)));

        TableColumn<LoginHistoryDTO, String> timeColumn = new TableColumn<>("Giờ");
        timeColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getLoginTime().format(formatterTime)));

        loginTable.getColumns().addAll(dateColumn, timeColumn);
        content.getChildren().addAll(title, loginTable);

        Scene scene = new Scene(content, 350, 300);
        popup.setScene(scene);
        popup.showAndWait();
    }

    private void showEditUserPopup(UserDTO user, TableView<UserDTO> userTable) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.initStyle(StageStyle.UTILITY);
        popup.setTitle("Chỉnh sửa thông tin");

        VBox content = new VBox(10);
        content.setStyle("-fx-padding: 20;");

        TextField usernameField = new TextField(user.getUsername());
        usernameField.setDisable(true);

        TextField fullNameField = new TextField(user.getFullName() != null ? user.getFullName() : "");
        TextField addressField = new TextField(user.getAddress() != null ? user.getAddress() : "");

        DatePicker dobPicker = new DatePicker(
            user.getDateOfBirth() != null ? user.getDateOfBirth().toLocalDate() : null
        );
        

        ComboBox<String> genderBox = new ComboBox<>(FXCollections.observableArrayList("Nam", "Nữ"));
        String gender = user.getGender() == 'M' ? "Nam" : (user.getGender() == 'F' ? "Nữ" : null);
        genderBox.setValue(gender);

        TextField emailField = new TextField(user.getEmail());

        Button saveButton = new Button("Save");
        saveButton.setStyle("-fx-font-size: 14px;");
        saveButton.setOnAction(_ -> {
            user.setFullName(fullNameField.getText().isEmpty() ? null : fullNameField.getText());
            user.setAddress(addressField.getText().isEmpty() ? null : addressField.getText());
        
            if (dobPicker.getValue() != null) 
                user.setDateOfBirth(dobPicker.getValue().atStartOfDay());
            else user.setDateOfBirth(null);
            
            if (genderBox.getValue() != null) 
                user.setGender(genderBox.getValue().equals("Nam") ? 'M' : 'F');
            else user.setGender('U'); 
            
            user.setEmail(emailField.getText());
            if (userBUS.updateUser(user)) {
                popup.close();
                userTable.refresh();
            }
            else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Không thể cập nhật thông tin người dùng.", ButtonType.OK);
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

    private void showEditPasswordPopup(UserDTO user) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.initStyle(StageStyle.UTILITY);
        popup.setTitle("Cập nhật mật khẩu");

        VBox content = new VBox(10);
        content.setStyle("-fx-padding: 20;");

        Label emaiLabel = new Label("Nhập email");
        TextField emailField = new TextField();
        emailField.setPromptText("Email của người dùng");
        emailField.setMaxWidth(250);
        Button submitButton = new Button("Gửi yêu cầu");
        submitButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: black; -fx-font-size: 14px; -fx-padding: 10px 20px; -fx-border-radius: 5px;");

        submitButton.setOnAction(_ -> {
            String email = emailField.getText();
            if (user == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Email không tồn tại trong hệ thống.");
                alert.showAndWait();
            } else {
                Email sendEmail = new Email();
                boolean isSent = sendEmail.sendVerificationCodeToEmail(email);

                Alert alert;
                if (isSent) {
                    alert = new Alert(Alert.AlertType.INFORMATION, "Mã xác minh đã được gửi đến email của người dùng");
                    alert.showAndWait();
                } else {
                    alert = new Alert(Alert.AlertType.ERROR, "Không thể gửi email. Vui lòng thử lại.");
                    alert.showAndWait();
                }
            }
        });

        content.getChildren().addAll(emaiLabel, emailField, submitButton);

        Scene scene = new Scene(content, 400, 600);
        popup.setScene(scene);
        popup.showAndWait();
    }
}
