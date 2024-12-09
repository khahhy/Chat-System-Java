package duck.presentation.userView;
import java.time.LocalDateTime;
import java.util.List;

import duck.bus.UserBUS;
import duck.dto.FriendDTO;
import duck.dto.UserDTO;
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

    private final UserDTO user;
    
    public FriendListView(UserDTO user) {
        this.user = user;
    }

    static class Friend {
    private final int friendId;
    private final String name;
    private final boolean isOnline;
    private final LocalDateTime dateOfBirth; // Để lưu ngày sinh
    private final char gender; // Để lưu giới tính

    // Constructor mới
    public Friend(int friendId, String name, boolean isOnline, LocalDateTime dateOfBirth, char gender) {
        this.friendId = friendId;
        this.name = name;
        this.isOnline = isOnline;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
    }

    public int getFriendId() {
        return friendId;
    }

    public String getName() {
        return name;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public LocalDateTime getDateOfBirth() {
        return dateOfBirth;
    }

    public char getGender() {
        return gender;
    }
}

    

    public VBox getContent() {
    VBox content = new VBox(10);
    content.setStyle("-fx-padding: 10;");

    // Lấy danh sách bạn bè từ cơ sở dữ liệu
    ObservableList<Friend> friends = FXCollections.observableArrayList();
    try {
        UserBUS userBUS = new UserBUS(); // Khởi tạo trực tiếp đối tượng UserBUS
    
        List<FriendDTO> friendDTOs = userBUS.getFriendList(user.getUserId());
        for (FriendDTO friendDTO : friendDTOs) {
            UserDTO friendUser = userBUS.getUserById(friendDTO.getFriendId());
            
            // Sử dụng các phương thức từ UserDTO để lấy thông tin bạn bè
            friends.add(new Friend(
                friendUser.getUserId(),
                friendUser.getFullName(), // Tên bạn bè
                friendUser.isOnline(), // Trạng thái online
                friendUser.getDateOfBirth(), // Ngày sinh
                friendUser.getGender() // Giới tính
            ));
        }
        
    } catch (Exception e) {
        e.printStackTrace();
        // Xử lý lỗi khi không thể tải danh sách bạn bè
    }
    

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

                HBox rightContainer = new HBox(10);
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
                    // Gọi thêm API xóa bạn nếu cần thiết
                });

                blockFriend.setOnAction(_ -> {
                    friends.remove(item);
                    displayedFriends.remove(item);
                    // Gọi thêm API block bạn nếu cần thiết
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
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.initStyle(StageStyle.UTILITY);

        VBox content = new VBox(10);
        content.setStyle("-fx-padding: 20; -fx-alignment: center; -fx-background-color: white; -fx-border-color: gray; -fx-border-width: 1;");

        Label nameLabel = new Label("Tên: " + friend.getName());
        nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Hiển thị ngày sinh
        Label dobLabel = new Label("Ngày sinh: " + (friend.getDateOfBirth() != null ? friend.getDateOfBirth().toLocalDate().toString() : "Không rõ"));
        dobLabel.setStyle("-fx-font-size: 14px;");

        // Hiển thị giới tính
        Label genderLabel = new Label("Giới tính: " + (friend.getGender() == 'M' ? "Nam" : "Nữ"));
        genderLabel.setStyle("-fx-font-size: 14px;");

        Button closeButton = new Button("Đóng");
        closeButton.setStyle("-fx-font-size: 14px;");
        closeButton.setOnAction(event -> popupStage.close());

        content.getChildren().addAll(nameLabel, dobLabel, genderLabel, closeButton);

        Scene scene = new Scene(content, 300, 200);
        popupStage.setScene(scene);
        popupStage.showAndWait();
    }



}
