package duck;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class Admin_userFriend {

    public class User {
        private String username;
        private String fullName;
        private LocalDateTime createdAt;
        private int directFriends;
        private int friendsOfFriends;

        public User(String username, String fullName, LocalDateTime createdAt, int directFriends, int friendsOfFriends) {
            this.username = username;
            this.fullName = fullName;
            this.createdAt = createdAt;
            this.directFriends = directFriends;
            this.friendsOfFriends = friendsOfFriends;
        }

        public String getUsername() {return username;}
        public String getFullName() {return fullName;}
        public LocalDateTime getCreatedAt() {return createdAt;}
        public int getDirectFriends() {return directFriends;}
        public int getFriendsOfFriends() {return friendsOfFriends;}
        public String getFormattedCreatedAt() {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            return createdAt.format(formatter);
        }
    }

    private final ObservableList<User> users = FXCollections.observableArrayList(
        new User("user01", "Nguyễn Văn A", LocalDateTime.now().minusDays(1), 5, 20),
        new User("user02", "Trần Thị B", LocalDateTime.now().minusDays(2), 3, 15),
        new User("user03", "Phạm Minh C", LocalDateTime.now().minusDays(3), 7, 30),
        new User("user04", "Đỗ Quốc D", LocalDateTime.now().minusDays(4), 10, 40),
        new User("user05", "Nguyễn Văn E", LocalDateTime.now().minusDays(5), 2, 8)
    );

    private final ObservableList<User> filteredUsers = FXCollections.observableArrayList(users);

    public BorderPane getContent() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-padding: 20;");

        TableView<User> userTable = new TableView<>();
        userTable.setItems(filteredUsers);

        TableColumn<User, String> usernameColumn = new TableColumn<>("Tên đăng nhập");
        usernameColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getUsername()));
        usernameColumn.setStyle("-fx-alignment: CENTER;");

        TableColumn<User, String> fullNameColumn = new TableColumn<>("Họ tên");
        fullNameColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getFullName()));
        fullNameColumn.setStyle("-fx-alignment: CENTER;");

        TableColumn<User, Integer> directFriendsColumn = new TableColumn<>("Bạn trực tiếp");
        directFriendsColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getDirectFriends()));
        directFriendsColumn.setStyle("-fx-alignment: CENTER;");


        TableColumn<User, Integer> friendsOfFriendsColumn = new TableColumn<>("Bạn của bạn");
        friendsOfFriendsColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getFriendsOfFriends()));
        friendsOfFriendsColumn.setStyle("-fx-alignment: CENTER;");

        TableColumn<User, String> createdAtColumn = new TableColumn<>("Thời gian tạo");
        createdAtColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getFormattedCreatedAt()));
        createdAtColumn.setStyle("-fx-alignment: CENTER;");

        userTable.getColumns().addAll(usernameColumn, fullNameColumn, directFriendsColumn, friendsOfFriendsColumn, createdAtColumn);
        userTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        HBox controls = createControls(userTable);

        VBox content = new VBox(10, controls, userTable);
        content.setStyle("-fx-padding: 10; -fx-background-color: #f9f9f9; -fx-border-color: #ddd; -fx-border-width: 1;");
        root.setCenter(content);

        return root;
    }

    private HBox createControls(TableView<User> userTable) {
        TextField searchField = new TextField();
        searchField.setPromptText("Lọc theo tên...");
        searchField.setPrefWidth(200);

        TextField directFriendsFilter = new TextField();
        directFriendsFilter.setPromptText("Nhập số bạn trực tiếp...");
        directFriendsFilter.setPrefWidth(150);

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
            int friendCount = directFriendsFilter.getText().isEmpty() ? -1 : Integer.parseInt(directFriendsFilter.getText());

            filteredUsers.setAll(users.filtered(user -> {
                boolean matchesName = user.getFullName().toLowerCase().contains(keyword);
                boolean matchesFriends = true;

                if (friendCount != -1) {
                    switch (filterType) {
                        case "Bằng":
                            matchesFriends = user.getDirectFriends() == friendCount;
                            break;
                        case "Nhỏ hơn":
                            matchesFriends = user.getDirectFriends() < friendCount;
                            break;
                        case "Lớn hơn":
                            matchesFriends = user.getDirectFriends() > friendCount;
                            break;
                    }
                }
                return matchesName && matchesFriends;
            }));
        });

        sortOptions.setOnAction(_ -> {
            String sortChoice = sortOptions.getValue();
            filteredUsers.sort((u1, u2) -> {
                switch (sortChoice) {
                    case "Tên A-Z":
                        return u1.getFullName().compareToIgnoreCase(u2.getFullName());
                    case "Tên Z-A":
                        return u2.getFullName().compareToIgnoreCase(u1.getFullName());
                    case "Thời gian mới nhất":
                        return u2.getCreatedAt().compareTo(u1.getCreatedAt());
                    case "Thời gian cũ nhất":
                        return u1.getCreatedAt().compareTo(u2.getCreatedAt());
                }
                return 0;
            });
        });

        HBox filters = new HBox(10, searchField, directFriendsFilter, filterOptions, applyFilterButton, sortOptions);
        filters.setStyle("-fx-padding: 10; -fx-background-color: #f1f1f1; -fx-border-color: #ddd; -fx-border-width: 1;");
        return filters;
    }
}
