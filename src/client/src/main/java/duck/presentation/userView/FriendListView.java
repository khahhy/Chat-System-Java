package duck.presentation.userView;

import java.util.List;

import duck.bus.FriendBUS;
import duck.bus.UserBUS;
import duck.dto.FriendDTO;
import duck.dto.UserDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class FriendListView {
    private final UserBUS userBUS;
    private final FriendBUS friendBUS;
    private final UserDTO user;
    
    public FriendListView(UserDTO user) {
        this.userBUS = new UserBUS();
        this.friendBUS = new FriendBUS();
        this.user = user;
    }
    

    public VBox getContent() {
        VBox content = new VBox(10);
        content.setStyle("-fx-padding: 10;");

        ObservableList<UserDTO> friends = FXCollections.observableArrayList();

        List<FriendDTO> friendDTOs = friendBUS.getFriendsByUserId(user.getUserId());
        for (FriendDTO friendDTO : friendDTOs) {
            UserDTO friendUser = userBUS.getUserById(friendDTO.getFriendId());
            friends.add(friendUser);
        }

        ObservableList<UserDTO> displayedFriends = FXCollections.observableArrayList(friends);
    
        TextField searchField = new TextField();
        searchField.setPromptText("Tìm bạn...");
        searchField.setStyle("-fx-font-size: 14px;");
        searchField.textProperty().addListener((_, _, newValue) -> {
            displayedFriends.setAll(friends.filtered(
                friend -> friend.getUsername().toLowerCase().contains(newValue.toLowerCase()) ||
                friend.getFullName().toLowerCase().contains(newValue.toLowerCase())
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

        ListView<UserDTO> friendList = new ListView<>(displayedFriends);
        VBox.setVgrow(friendList, Priority.ALWAYS);

        friendList.setCellFactory(_ -> new ListCell<>() {
            protected void updateItem(UserDTO item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    BorderPane container = new BorderPane();
                    container.setStyle("-fx-background-color: #E8E8E8; -fx-padding: 10;");

                    Text nameText = new Text(item.getUsername());
                    nameText.setStyle("-fx-font-size: 14px; -fx-fill: #333;");
                    
                    String fullName = item.getFullName();
                    if (fullName == null || fullName.isEmpty()) {
                        fullName = "Chưa cập nhật";  
                    }

                    Text fullNameText = new Text(" [" + fullName + "]");
                    fullNameText.setStyle("-fx-font-size: 12px; -fx-fill: #777;");
                    HBox nameContainer = new HBox(5, nameText, fullNameText);
                    nameContainer.setAlignment(Pos.CENTER_LEFT);

                    container.setLeft(nameContainer);

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
                        boolean confirmDelete = confirmDeleteDialog(item.getUsername());
                        if (confirmDelete) {
                            try {
                                boolean success = friendBUS.deleteFriend(user.getUserId(), item.getUserId()); // Gọi phương thức xóa bạn
                                if (success) {
                                    friends.remove(item); // Xóa bạn khỏi danh sách
                                    displayedFriends.remove(item); // Cập nhật danh sách hiển thị
                                } else {
                                    showErrorDialog("Không thể xóa bạn. Vui lòng thử lại sau.");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                showErrorDialog("Đã xảy ra lỗi khi xóa bạn. Vui lòng thử lại sau.");
                            }
                        }
                    });
                

                    blockFriend.setOnAction(_ -> {
                        boolean confirmBlock = confirmBlockDialog(item.getUsername());
                        if (confirmBlock) {
                            try {
                                boolean success = friendBUS.blockFriend(user.getUserId(), item.getUserId()); // Gọi phương thức block
                                if (success) {
                                    friends.remove(item); // Xóa khỏi danh sách hiển thị
                                    displayedFriends.remove(item);
                                } else {
                                    showErrorDialog("Không thể block bạn. Vui lòng thử lại sau.");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                showErrorDialog("Đã xảy ra lỗi khi block bạn. Vui lòng thử lại sau.");
                            }
                        }
                    });
                

                    createGroup.setOnAction(_ -> {
                        CreateGroupPopup createGroupPopup = new CreateGroupPopup(user);
                        createGroupPopup.show(item.getUsername());
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
                displayedFriends.sort((f1, f2) -> f1.getUsername().compareToIgnoreCase(f2.getUsername()));
            } else if ("Z-A".equals(sortChoice)) {
                displayedFriends.sort((f1, f2) -> f2.getUsername().compareToIgnoreCase(f1.getUsername()));
            }
        });

        filterOptions.setOnAction(_ -> {
            String filterChoice = filterOptions.getValue();
            if ("Đang hoạt động".equals(filterChoice)) {
                displayedFriends.setAll(friends.filtered(UserDTO::isOnline));
            } else {
                displayedFriends.setAll(friends);
            }
        });

        content.getChildren().addAll(searchField, controls, friendList);
        return content;
    }



    private void showFriendInfoPopup(UserDTO friend) {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.initStyle(StageStyle.UTILITY);

        VBox content = new VBox(10);
        content.setStyle("-fx-padding: 20; -fx-alignment: center; -fx-background-color: white; -fx-border-color: gray; -fx-border-width: 1;");

        Label titleLabel = new Label("Thông tin tài khoản " + friend.getUsername());
        titleLabel.setFont(new Font("Arial", 16));
        titleLabel.setStyle("-fx-font-weight: bold;");

        Label fullnameLabel = new Label("Tên: " + friend.getFullName());
        Label addressLabel = new Label("Địa chỉ: " + friend.getAddress());
        Label genderLabel = new Label("Giới tính: " + 
            (user.getGender() == 'M' ? "Nam" : friend.getGender() == 'F' ? "Nữ" : ""));

        Label dobLabel = new Label("Ngày sinh: " + 
            (user.getDateOfBirth() != null ? friend.getDateOfBirth().toLocalDate() : "01/01/2000"));

        Button closeButton = new Button("Đóng");
        closeButton.setStyle("-fx-font-size: 14px;");
        closeButton.setOnAction(_ -> popupStage.close());

        content.getChildren().addAll(titleLabel, fullnameLabel, addressLabel, dobLabel, genderLabel, closeButton);

        Scene scene = new Scene(content, 300, 200);
        popupStage.setScene(scene);
        popupStage.showAndWait();
    }

    private boolean confirmDeleteDialog(String friendName) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận xóa bạn");
        alert.setHeaderText("Bạn có chắc chắn muốn xóa " + friendName + " khỏi danh sách bạn bè?");
        alert.setContentText("Hành động này không thể hoàn tác.");
    
        ButtonType confirmButton = new ButtonType("Xóa", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Hủy", ButtonBar.ButtonData.CANCEL_CLOSE);
    
        alert.getButtonTypes().setAll(confirmButton, cancelButton);
    
        return alert.showAndWait().filter(button -> button == confirmButton).isPresent();
    }
    
    private void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Lỗi");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private boolean confirmBlockDialog(String friendName) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận block");
        alert.setHeaderText("Bạn có chắc chắn muốn block " + friendName + "?");
        alert.setContentText("Người này sẽ không còn nhìn thấy bạn trong danh sách bạn bè.");
    
        ButtonType blockButton = new ButtonType("Block", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Hủy", ButtonBar.ButtonData.CANCEL_CLOSE);
    
        alert.getButtonTypes().setAll(blockButton, cancelButton);
    
        return alert.showAndWait().filter(button -> button == blockButton).isPresent();
    }
    
}       
