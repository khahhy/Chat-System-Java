package duck.presentation.userView;

import java.time.LocalDateTime;
import java.util.List;

import duck.bus.GroupBUS;
import duck.bus.GroupMemberBUS;
import duck.dto.GroupDTO;
import duck.dto.GroupMemberDTO;
import duck.dto.UserDTO;
import duck.dto.FriendDTO;
import duck.bus.UserBUS;
import duck.bus.FriendBUS;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class CreateGroupPopup {
    private final UserDTO user;
    private final UserBUS userBUS;
    private final FriendBUS friendBUS;
    private final GroupMemberBUS groupMemBUS;
    private final GroupBUS groupBUS;
    private final List<FriendDTO> friend_list;
    private final ObservableList<FriendDTO> friends;

    public CreateGroupPopup(UserDTO user) {
        this.user = user;
        userBUS = new UserBUS();
        friendBUS = new FriendBUS();
        groupMemBUS = new GroupMemberBUS();
        groupBUS = new GroupBUS();
        friend_list = friendBUS.getFriendsByUserId(user.getUserId());
        friends = FXCollections.observableArrayList(friend_list);
    }

    public void show(String selectedFriendName) {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.initStyle(StageStyle.UTILITY);

        VBox content = new VBox(10);
        content.setStyle("-fx-padding: 20; -fx-background-color: #FFFFFF;");

        Label titleLabel = new Label("Tạo nhóm mới");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        TextField groupNameField = new TextField();
        groupNameField.setPromptText("Nhập tên nhóm...");
        groupNameField.setStyle("-fx-font-size: 14px;");

        Label membersLabel = new Label("Chọn thành viên:");
        membersLabel.setStyle("-fx-font-size: 14px;");

        ObservableList<FriendDTO> selectedFriends = FXCollections.observableArrayList();
        ListView<FriendDTO> friendListView = new ListView<>(friends);

        friendListView.setCellFactory(_ -> new ListCell<>() {
            protected void updateItem(FriendDTO friend, boolean empty) {
                super.updateItem(friend, empty);
                if (empty || friend == null) {
                    setGraphic(null);
                } else {
                    String friend_name = userBUS.getUserById(friend.getFriendId()).getUsername();
                    HBox container = new HBox(10);
                    container.setStyle("-fx-alignment: center-left;");

                    CheckBox checkBox = new CheckBox(friend_name);
                    checkBox.setStyle("-fx-font-size: 14px;");

                    if (friend_name.equals(selectedFriendName)) { 
                        checkBox.setSelected(true); 
                        if (!selectedFriends.contains(friend)) { 
                            selectedFriends.add(friend); 
                        } 
                    }
                    checkBox.selectedProperty().addListener((_, _, newValue) -> {
                        if (newValue) {
                            if (!selectedFriends.contains(friend)) {
                                selectedFriends.add(friend);
                            }
                        } else {
                            selectedFriends.remove(friend);
                        }
                    });

                    container.getChildren().add(checkBox);
                    setGraphic(container);
                }
            }
        });

        Button saveButton = new Button("Save");
        saveButton.setStyle("-fx-font-size: 14px;");
        saveButton.setOnAction(_ -> {
            String groupName = groupNameField.getText();
            if (groupName.isBlank()) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Tên nhóm không được để trống!", ButtonType.OK);
                alert.showAndWait();
                return;
            }

            if (selectedFriends == null || selectedFriends.size() == 0) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Thêm ít nhất một thành viên!", ButtonType.OK);
                alert.showAndWait();
                return;
            }

            try {
                GroupDTO group = new GroupDTO(0, groupName, LocalDateTime.now());
                int groupId = groupBUS.addGroup(group);
        
                GroupMemberDTO adminMember = new GroupMemberDTO(groupId, user.getUserId(), true, LocalDateTime.now(), true);
                groupMemBUS.addMember(adminMember);
        
                for (FriendDTO friend : selectedFriends) {
                    GroupMemberDTO member = new GroupMemberDTO(groupId, friend.getFriendId(), false, null, false);
                    groupMemBUS.addMember(member);
                }
        
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION, "Nhóm \"" + groupName + "\" đã được tạo thành công!", ButtonType.OK);
                successAlert.showAndWait();
                popupStage.close();
            } catch (Exception e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, "Đã xảy ra lỗi khi tạo nhóm! Vui lòng thử lại.", ButtonType.OK);
                alert.showAndWait();
            }
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.setStyle("-fx-font-size: 14px;");
        cancelButton.setOnAction(_ -> popupStage.close());

        HBox buttonBox = new HBox(10, saveButton, cancelButton);
        buttonBox.setStyle("-fx-alignment: center-right;");

        content.getChildren().addAll(titleLabel, groupNameField, membersLabel, friendListView, buttonBox);

        Scene scene = new Scene(content, 400, 500);
        popupStage.setScene(scene);
        popupStage.showAndWait();
    }
}
