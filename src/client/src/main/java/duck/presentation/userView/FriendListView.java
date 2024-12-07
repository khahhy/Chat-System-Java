package duck.presentation.userView;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class FriendListView {

    // tạo tạm để biểu diễn ui
    static class Friend {
        private final String name;
        private final boolean isOnline;
        public Friend(String name, boolean isOnline) {
            this.name = name;
            this.isOnline = isOnline;
        }
        public String getName() {return name;}
        public boolean isOnline() {return isOnline;}
    }

    public VBox getContent() {
        VBox content = new VBox(10);
        content.setStyle("-fx-padding: 10;");

        ObservableList<Friend> friends = FXCollections.observableArrayList(
            new Friend("Nguyễn Văn A", true),
            new Friend("Trần Thị B", false),
            new Friend("Phạm Minh C", true),
            new Friend("Đỗ Quốc D", false)
        );
        
        ObservableList<Friend> displayedFriends = FXCollections.observableArrayList(friends);
        
        TextField searchField = new TextField();
        searchField.setPromptText("Tìm bạn...");
        searchField.setStyle("-fx-font-size: 14px;");
        searchField.textProperty().addListener((_, _, newValue) -> {
            displayedFriends.setAll(friends.filtered(
                friend -> friend.getName().toLowerCase().contains(newValue.toLowerCase())
            ));
        });
        
        HBox controls = new HBox(10);
        
        ComboBox<String> sortOptions = new ComboBox<>();
        sortOptions.getItems().addAll("A-Z", "Z-A");
        sortOptions.setValue("A-Z");
        sortOptions.setStyle("-fx-font-size: 14px;");

        ComboBox<String> filterOptions = new ComboBox<>();
        filterOptions.getItems().addAll("Đang hoạt động", "Tất cả");
        filterOptions.setValue("Tất cả");
        filterOptions.setStyle("-fx-font-size: 14px;");

        controls.getChildren().addAll(sortOptions, filterOptions);

        ListView<Friend> friendList = new ListView<>(displayedFriends);
        VBox.setVgrow(friendList, Priority.ALWAYS);

        
        friendList.setCellFactory(_ -> new ListCell<>() {
            @Override
            protected void updateItem(Friend item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {                    
                    BorderPane container = new BorderPane();
                    container.setStyle("-fx-background-color: #E8E8E8; -fx-padding: 10;");
        
                    Text nameText = new Text(item.getName());
                    nameText.setStyle("-fx-font-size: 14px; -fx-fill: #333;");
                    container.setLeft(nameText);

                    HBox rightContainer = new HBox(10); // gap là 10
                    rightContainer.setStyle("-fx-alignment: center-right;");
                    
                    Label statusLabel = new Label(item.isOnline() ? "Online" : "Offline");
                    statusLabel.setStyle(item.isOnline()
                        ? "-fx-text-fill: green; -fx-font-size: 12px;"
                        : "-fx-text-fill: gray; -fx-font-size: 12px;");
                            
                    MenuButton optionsButton = new MenuButton();
                    MenuItem viewInfo = new MenuItem("Xem thông tin");
                    MenuItem sendMessage = new MenuItem("Nhắn tin");
                    MenuItem removeFriend = new MenuItem("Xóa bạn");
                    MenuItem blockFriend = new MenuItem("Block");
                    MenuItem createGroup = new MenuItem("Tạo group");
                    optionsButton.getItems().addAll(viewInfo, sendMessage, removeFriend, blockFriend, createGroup);
                    optionsButton.setStyle("-fx-font-size: 14px;");

                    viewInfo.setOnAction(_ -> showFriendInfoPopup(item));

                    removeFriend.setOnAction(_ -> {
                        friends.remove(item); 
                        displayedFriends.remove(item); 
                    });

                    blockFriend.setOnAction(_ -> {
                        friends.remove(item); 
                        displayedFriends.remove(item); 
                    });

                    createGroup.setOnAction(_ -> {
                        CreateGroupPopup createGroupPopup = new CreateGroupPopup();
                        createGroupPopup.show(item.getName());
                    });

                    rightContainer.getChildren().addAll(statusLabel, optionsButton);
                    container.setRight(rightContainer);
                          
                    setGraphic(container);
                }
            }
        });
        
        sortOptions.setOnAction(_ -> {
            String sortChoice = sortOptions.getValue();
            if ("A-Z".equals(sortChoice)) {
                displayedFriends.sort((f1, f2) -> f1.getName().compareToIgnoreCase(f2.getName()));
            } else if ("Z-A".equals(sortChoice)) {
                displayedFriends.sort((f1, f2) -> f2.getName().compareToIgnoreCase(f1.getName()));
            }
        });

        filterOptions.setOnAction(_ -> {
            String filterChoice = filterOptions.getValue();
            if ("Đang hoạt động".equals(filterChoice)) {
                displayedFriends.setAll(friends.filtered(Friend::isOnline));
            } else {
                displayedFriends.setAll(friends);
            }
        });
        
        

        content.getChildren().addAll(searchField, controls, friendList);
        return content;
    }


    private void showFriendInfoPopup(Friend friend) {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL); // Chặn tương tác với cửa sổ chính
        popupStage.initStyle(StageStyle.TRANSPARENT);

        VBox content = new VBox(10);
        content.setStyle("-fx-padding: 20;");
    
   
        Label nameLabel = new Label("Tên: " + friend.getName());
        nameLabel.setStyle("-fx-font-size: 16px;");

        Label dobLabel = new Label("Ngày sinh: dd/mm/yyyy");
        dobLabel.setStyle("-fx-font-size: 16px;");

        Label genderLabel = new Label("Giới tính: m/f");
        genderLabel.setStyle("-fx-font-size: 16px;");

        Button closeButton = new Button("Đóng");
        closeButton.setStyle("-fx-font-size: 14px;");
        closeButton.setOnAction(_ -> popupStage.close());

   
        content.getChildren().addAll(nameLabel, dobLabel, genderLabel, closeButton);
    
        Scene scene = new Scene(content, 350, 250);
        popupStage.setScene(scene);
        popupStage.showAndWait();
    }
}
