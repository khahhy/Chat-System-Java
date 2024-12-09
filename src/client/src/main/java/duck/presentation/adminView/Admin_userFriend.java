package duck.presentation.adminView;

import duck.bus.FriendBUS;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;


public class Admin_userFriend {
    private FriendBUS friend_BUS;
    List<Map<String, Object>> friendList;    
    ObservableList<Map<String, Object>> friends;

    public Admin_userFriend() {
        friend_BUS = new FriendBUS();
        friendList = friend_BUS.getFriendDetails();
        friends = FXCollections.observableArrayList(friendList);
    }

    public BorderPane getContent() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-padding: 20;");

        TableView<Map<String, Object>> userTable = new TableView<>();
        userTable.setItems(friends);

        TableColumn<Map<String, Object>, String> usernameColumn = new TableColumn<>("Tên đăng nhập");
        usernameColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty((String)data.getValue().get("username")));
        usernameColumn.setStyle("-fx-alignment: CENTER;");

        TableColumn<Map<String, Object>, String> fullNameColumn = new TableColumn<>("Họ tên");
        fullNameColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty((String)data.getValue().get("fullname")));
        fullNameColumn.setStyle("-fx-alignment: CENTER;");

        TableColumn<Map<String, Object>, Integer> directFriendsColumn = new TableColumn<>("Bạn trực tiếp");
        directFriendsColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>((int) data.getValue().get("totalFriend")));
        directFriendsColumn.setStyle("-fx-alignment: CENTER;");


        TableColumn<Map<String, Object>, Integer> friendsOfFriendsColumn = new TableColumn<>("Bạn của bạn");
        friendsOfFriendsColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>((int)data.getValue().get("totalFrOfFr")));
        friendsOfFriendsColumn.setStyle("-fx-alignment: CENTER;");

        TableColumn<Map<String, Object>, LocalDateTime> createdAtColumn = new TableColumn<>("Thời gian tạo");
        createdAtColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>((LocalDateTime) data.getValue().get("createdAt")));

        createdAtColumn.setCellFactory(_ -> new TableCell<>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null) setText(item.format(formatter)); 
                else setText("");  
            }
        });
        createdAtColumn.setStyle("-fx-alignment: CENTER;");

        userTable.getColumns().addAll(usernameColumn, fullNameColumn, directFriendsColumn, friendsOfFriendsColumn, createdAtColumn);
        userTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        HBox controls = createControls(userTable);

        VBox content = new VBox(10, controls, userTable);
        VBox.setVgrow(userTable, Priority.ALWAYS);
        content.setStyle("-fx-padding: 10; -fx-background-color: #f9f9f9; -fx-border-color: #ddd; -fx-border-width: 1;");
        root.setCenter(content);

        return root;
    }

    private HBox createControls(TableView<Map<String, Object>> userTable) {
        TextField searchField = new TextField();
        searchField.setPromptText("Lọc theo tên...");
        searchField.setPrefWidth(200);
    
        TextField directFriendsFilter = new TextField();
        directFriendsFilter.setPromptText("Nhập số bạn trực tiếp...");
        directFriendsFilter.setPrefWidth(150);
    
        ComboBox<String> filterOptions = new ComboBox<>(FXCollections.observableArrayList("Bằng", "Nhỏ hơn", "Lớn hơn"));
        filterOptions.setValue("Bằng");
    
        Button applyFilterButton = new Button("Lọc");
        applyFilterButton.setStyle("-fx-font-size: 12px; -fx-padding: 5 10; -fx-background-color: #4CAF50; -fx-text-fill: white;");
    
        applyFilterButton.setOnAction(_ -> {
            String keyword = searchField.getText().toLowerCase().trim();
            String filterType = filterOptions.getValue();
            int friendCount = directFriendsFilter.getText().isEmpty() ? -1 : Integer.parseInt(directFriendsFilter.getText());
    
            List<Map<String, Object>> filteredList = friendList.stream()
                .filter(user -> {
                    boolean matchesName = true;
                    boolean matchesFriends = true;
    
                    if (!keyword.isEmpty()) {
                        String fullName = ((String) user.get("fullname")).toLowerCase();
                        matchesName = fullName.contains(keyword);
                    }
    
                    if (friendCount != -1) {
                        int totalFriends = (int) user.get("totalFriend");
                        switch (filterType) {
                            case "Bằng":
                                matchesFriends = totalFriends == friendCount;
                                break;
                            case "Nhỏ hơn":
                                matchesFriends = totalFriends < friendCount;
                                break;
                            case "Lớn hơn":
                                matchesFriends = totalFriends > friendCount;
                                break;
                        }
                    }
    
                    return matchesName && matchesFriends;
                })
                .toList();
    
            friends.setAll(filteredList);
        });
    
        HBox filters = new HBox(10, searchField, directFriendsFilter, filterOptions, applyFilterButton);
        filters.setStyle("-fx-padding: 10; -fx-background-color: #f1f1f1; -fx-border-color: #ddd; -fx-border-width: 1;");
        return filters;
    }
    
}
