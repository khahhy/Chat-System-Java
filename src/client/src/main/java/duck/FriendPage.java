package duck;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.*;


public class FriendPage {
    // tạo tạm để chạy ui
    public class Friend {
        private final String name;
        private final boolean isFriend;
        public Friend(String name, boolean isFriend) {
            this.name = name; this.isFriend = isFriend;
        }
        public String getName() {return name;}
        public boolean isFriend() {return isFriend;}
    }
    
    private final ObservableList<Friend> friends = FXCollections.observableArrayList(
        new Friend("Nguyễn Văn A", true),
        new Friend("Trần Thị B", true),
        new Friend("Phạm Minh C", false),
        new Friend("Đỗ Quốc D", true),
        new Friend("Nguyễn Văn E", false)
    );

    private final ObservableList<String> groups = FXCollections.observableArrayList(
        "Nhóm học tập", "Nhóm thể thao", "Nhóm công việc"
    );

    private final ObservableList<String> messages = FXCollections.observableArrayList(
        "Nguyễn Văn A: ngủ đi",
        "Nhóm học tập: Cập nhật tài liệu",
        "Trần Thị B: Khi nào họp"
    );

    public BorderPane getContent() {
        BorderPane root = new BorderPane();

        VBox leftMenu = createLeftMenu(root);
        root.setLeft(leftMenu);

        VBox defaultContent = new VBox();
        root.setCenter(defaultContent);

        return root;
    }

    private VBox createLeftMenu(BorderPane root) {
        VBox leftMenu = new VBox(10);
        leftMenu.setStyle("-fx-background-color: #E7ECEF; -fx-padding: 10;");
        leftMenu.setPrefWidth(320);

        
        HBox searchBox = new HBox(10);

        TextField searchField = new TextField();
        searchField.setPromptText("Tìm kiếm...");
        searchField.setStyle("-fx-font-size: 14px;");
        searchField.setPrefWidth(200);

        Button backButton = new Button("Back");
        backButton.setStyle("-fx-font-size: 12px;");
        backButton.setVisible(false); // chưa tìm thì k hiện back
        backButton.setOnAction(_ -> {
            resetLeftMenu(leftMenu);
            searchField.clear();
        });

        searchBox.getChildren().addAll(searchField, backButton);

       
        VBox searchResults = new VBox(10);
        searchResults.setStyle("-fx-padding: 10;");
        searchResults.setVisible(false); 
        searchResults.setManaged(false); 

        searchField.textProperty().addListener((_, _, newValue) -> {
            if (newValue.isEmpty()) {
                searchResults.setVisible(false);
                searchResults.setManaged(false); // k lấy k gian thi đã ẩn
                backButton.setVisible(false);
                showDefaultMenu(leftMenu);
                return;
            }
            searchResults.setVisible(true);
            searchResults.setManaged(true); // hiển thị và không gian
            backButton.setVisible(true);
            hideDefaultMenu(leftMenu);

            searchResults.getChildren().clear();

            // tìm người
            friends.filtered(item -> item.getName().toLowerCase().contains(newValue.toLowerCase()))
                .forEach(friend -> searchResults.getChildren().add(createSearchResult("Người dùng", friend)));
            // nhóm
            groups.filtered(name -> name.toLowerCase().contains(newValue.toLowerCase()))
                .forEach(name -> searchResults.getChildren().add(createSearchResult("Nhóm", name)));
            // chat
            messages.filtered(content -> content.toLowerCase().contains(newValue.toLowerCase()))
                .forEach(content -> searchResults.getChildren().add(createSearchResult("Tin nhắn", content)));
        });

       
        Button friendListButton = new Button("Danh sách bạn bè");
        Button groupListButton = new Button("Danh sách nhóm");
        Button friendRequestsButton = new Button("Lời mời kết bạn");
        Button groupRequestsButton = new Button("Lời mời vào nhóm");

        friendListButton.setPrefWidth(200);
        groupListButton.setPrefWidth(200);
        friendRequestsButton.setPrefWidth(200);
        groupRequestsButton.setPrefWidth(200);

        friendListButton.setStyle("-fx-font-size: 14px;");
        groupListButton.setStyle("-fx-font-size: 14px;");
        friendRequestsButton.setStyle("-fx-font-size: 14px;");
        groupRequestsButton.setStyle("-fx-font-size: 14px;");

        friendListButton.setOnAction(_ -> root.setCenter(new FriendListView().getContent()));
        groupListButton.setOnAction(_ -> root.setCenter(new GroupListView().getContent()));
        friendRequestsButton.setOnAction(_ -> root.setCenter(new FriendRequestView().getContent()));
        groupRequestsButton.setOnAction(_ -> root.setCenter(new GroupRequestView().getContent()));

        leftMenu.getChildren().addAll(searchBox, searchResults, friendListButton, groupListButton, friendRequestsButton, groupRequestsButton);
        return leftMenu;
    }

    private void resetLeftMenu(VBox leftMenu) {
        leftMenu.getChildren().stream()
            .filter(node -> node instanceof VBox && ((VBox) node).isVisible())
            .forEach(node -> {
                node.setVisible(false);
                node.setManaged(false); 
            });
        showDefaultMenu(leftMenu);
    }

    private void hideDefaultMenu(VBox leftMenu) {
        leftMenu.getChildren().stream()
            .filter(node -> node instanceof Button)
            .forEach(node -> node.setVisible(false));
    }

    private void showDefaultMenu(VBox leftMenu) {
        leftMenu.getChildren().stream()
            .filter(node -> node instanceof Button)
            .forEach(node -> node.setVisible(true));
    }

    private BorderPane createSearchResult(String tag, Object content) {
        BorderPane result = new BorderPane();
        result.setStyle("-fx-padding: 5; -fx-border-color: #CCCCCC; -fx-border-width: 1; -fx-background-color: #F9F9F9;");
        HBox info = new HBox(5);
        info.setStyle("-fx-alignment: center-left;");

        Label tagLabel = new Label("[" + tag + "]");
        tagLabel.setStyle("-fx-font-size: 10px; -fx-font-weight: bold;");
    
        Label contentLabel = new Label(content.toString());
        if ("Người dùng".equals(tag)) {
            if (content instanceof Friend friend) {
                contentLabel.setText(friend.getName()); 
            }
        }
        contentLabel.setStyle("-fx-font-size: 12px;");
        info.getChildren().addAll(tagLabel, contentLabel);
        result.setLeft(info);
    
        HBox options = new HBox(5);
        options.setStyle("-fx-alignment: center-right;");
    
        if ("Người dùng".equals(tag)) {
            if (content instanceof Friend friend) {
                MenuButton optionsButton = new MenuButton();
                MenuItem chatOption = new MenuItem("Nhắn tin");
                MenuItem groupOption = new MenuItem("Tạo nhóm");

                groupOption.setOnAction(_ -> {
                    CreateGroupPopup createGroupPopup = new CreateGroupPopup();
                    createGroupPopup.show(friend.getName());
                });
    
                optionsButton.getItems().addAll(chatOption, groupOption);
                optionsButton.setStyle("-fx-font-size: 12px;");
                
                if (friend.isFriend()) {
                    Button addButton = new Button("Kết bạn");
                    addButton.setStyle("-fx-font-size: 11px;");
    
                    options.getChildren().addAll(addButton, optionsButton);
                }
                else {
                    options.getChildren().add(optionsButton);
                }
            }
        } else if ("Nhóm".equals(tag)) {
            MenuButton optionsButton = new MenuButton();
            MenuItem chatOption = new MenuItem("Nhắn tin");
            MenuItem viewInfo = new MenuItem("Xem thông tin");
            MenuItem leaveGroup = new MenuItem("Rời nhóm");
    
            optionsButton.getItems().addAll(chatOption, viewInfo, leaveGroup);
            optionsButton.setStyle("-fx-font-size: 12px;");
            options.getChildren().addAll(optionsButton);
    
        } else if ("Tin nhắn".equals(tag)) {
            MenuButton optionsButton = new MenuButton();
            MenuItem viewChatOption = new MenuItem("Xem cuộc trò chuyện");
            MenuItem deleteOption = new MenuItem("Xóa tin nhắn");
    
            optionsButton.getItems().addAll(viewChatOption, deleteOption);
            optionsButton.setStyle("-fx-font-size: 12px;");
            options.getChildren().addAll(optionsButton);
        }
    
        result.setRight(options);
        return result;
    }
    
    
}
