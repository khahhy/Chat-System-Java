package duck.presentation.userView;


import java.time.format.DateTimeFormatter;
import java.util.List;

import duck.bus.GroupBUS;
import duck.bus.GroupMemberBUS;

import duck.dto.FriendDTO;
import duck.dto.GroupDTO;
import duck.dto.GroupMemberDTO;
import duck.dto.UserDTO;
import duck.bus.FriendBUS;
import duck.bus.UserBUS;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;

import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class GroupListView {
    private final UserDTO user;
    private final GroupMemberBUS groupMemBUS;
    private final GroupBUS groupBUS;
    private final List<GroupDTO> group_list;
    private final ObservableList<GroupDTO> groups;

    private final UserBUS userBUS;
    private final FriendBUS friendBUS;

    private final BorderPane parent;

    public GroupListView(UserDTO user, BorderPane root) {
        parent = root;

        this.user = user;
        groupMemBUS = new GroupMemberBUS();
        groupBUS = new GroupBUS();
        group_list = groupBUS.getAllGroupsByUserId(user.getUserId());
        groups = FXCollections.observableArrayList(group_list);

        userBUS = new UserBUS();
        friendBUS = new FriendBUS();
    }

    public VBox getContent() {
        VBox content = new VBox(10);
        content.setStyle("-fx-padding: 10;");

        ObservableList<GroupDTO> displayedGroups = FXCollections.observableArrayList(groups);


        TextField searchField = new TextField();
        searchField.setPromptText("Tìm nhóm...");
        searchField.setStyle("-fx-font-size: 14px;");
        searchField.textProperty().addListener((_, _, newValue) -> {
            displayedGroups.setAll(groups.filtered(
                group -> group.getGroupName().toLowerCase().contains(newValue.toLowerCase())
            ));
        });

        ComboBox<String> sortOptions = new ComboBox<>();
        sortOptions.getItems().addAll("A-Z", "Z-A");
        sortOptions.setValue("A-Z");
        sortOptions.setStyle("-fx-font-size: 14px;");

        ListView<GroupDTO> groupList = new ListView<>(displayedGroups);
        VBox.setVgrow(groupList, Priority.ALWAYS);

        groupList.setCellFactory(_ -> new ListCell<>() {
            @Override
            protected void updateItem(GroupDTO item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    BorderPane container = new BorderPane();
                    container.setStyle("-fx-background-color: #E8E8E8; -fx-padding: 10;");
        

                    Text groupName = new Text(item.getGroupName());
                    groupName.setStyle("-fx-font-size: 14px; -fx-fill: #333;");
                    container.setLeft(groupName);

                    MenuButton optionsButton = new MenuButton();
                    MenuItem groupChat = new MenuItem("Nhắn tin");
                    MenuItem viewInfo = new MenuItem("Xem thông tin");
                    MenuItem updateName = new MenuItem("Đổi tên nhóm");
                    MenuItem addMem = new MenuItem("Thêm thành viên");
                    MenuItem updateAdmin = new MenuItem("Gán quyền Admin");
                    MenuItem removeMem = new MenuItem("Xóa thành viên");
                    MenuItem leaveGroup = new MenuItem("Rời nhóm");

                    optionsButton.getItems().addAll(groupChat, viewInfo, updateName, addMem, updateAdmin, removeMem, leaveGroup);
                    optionsButton.setStyle("-fx-font-size: 14px;");

                    groupChat.setOnAction(_ -> parent.setCenter(new MessagePage(user, null, item).getContent()));

                    viewInfo.setOnAction(_ -> showGroupInfoPopup(item));

                    updateName.setOnAction(_ -> {
                        updateNameGroup(item);
                        groupList.refresh();
                    });

                    addMem.setOnAction(_ -> {
                        addMember(item);
                    });

                    updateAdmin.setOnAction(_ -> {
                        if (groupMemBUS.getAdminId(item.getGroupId()).contains(user.getUserId())) {
                            updateAdmin(item);
                        } else {
                            Alert successAlert = new Alert(Alert.AlertType.INFORMATION, "Chỉ quản trị viên nhóm chat dùng tính năng này!", ButtonType.OK);
                            successAlert.showAndWait();
                        }
                    });

                    removeMem.setOnAction(_ -> {
                        if (groupMemBUS.getAdminId(item.getGroupId()).contains(user.getUserId())) {
                            removeMember(item);
                        } else {
                            Alert successAlert = new Alert(Alert.AlertType.INFORMATION, "Chỉ quản trị viên nhóm chat dùng tính năng này!", ButtonType.OK);
                            successAlert.showAndWait();
                        }
                    });

                    leaveGroup.setOnAction(_ -> {
                        if (showConfirmLeavePopup(item)) {
                            groups.remove(item);
                            displayedGroups.remove(item);
                        }
                    });

                    container.setRight(optionsButton);
                    
                    setGraphic(container);
                }
            }
        });

        sortOptions.setOnAction(_ -> {
            String sortChoice = sortOptions.getValue();
            if ("A-Z".equals(sortChoice)) {
                displayedGroups.sort((f1, f2) -> f1.getGroupName().compareToIgnoreCase(f2.getGroupName()));
            } else if ("Z-A".equals(sortChoice)) {
                displayedGroups.sort((f1, f2) -> f2.getGroupName().compareToIgnoreCase(f1.getGroupName()));
            }
        });


        content.getChildren().addAll(searchField, sortOptions, groupList);
        return content;
    }

    public void showGroupInfoPopup(GroupDTO group) {
        List<String> admins = groupMemBUS.getAdmin(group.getGroupId());
        List<String> members = groupMemBUS.getMem(group.getGroupId());
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.initStyle(StageStyle.UTILITY);
        popup.setTitle("Chi tiết nhóm: " + group.getGroupName());

        VBox content = new VBox(10);
        content.setStyle("-fx-padding: 20;");

        Label groupNameLabel = new Label("Tên nhóm: " + group.getGroupName());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        Label createdAtLabel = new Label("Ngày tạo: " + group.getCreatedAt().format(formatter));


        ListView<String> adminListView = new ListView<>(FXCollections.observableArrayList(admins));
        adminListView.setPrefHeight(100);

        ListView<String> memberListView = new ListView<>(FXCollections.observableArrayList(members));
        memberListView.setPrefHeight(200);

        content.getChildren().addAll(
            groupNameLabel,
            createdAtLabel,
            new Label("Danh sách Admin:"),
            adminListView,
            new Label("Danh sách Thành viên:"),
            memberListView
        );

        Scene scene = new Scene(content, 400, 400);
        popup.setScene(scene);
        popup.showAndWait();
    }

    public boolean showConfirmLeavePopup(GroupDTO group) {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.initStyle(StageStyle.UTILITY);

        VBox content = new VBox(10);
        content.setStyle("-fx-padding: 20;");

        Label confirmLabel = new Label("Bạn có chắc muốn rời nhóm \"" + group.getGroupName() + "\"?");
        confirmLabel.setStyle("-fx-font-size: 14px;");

        Button yesButton = new Button("Có");
        Button noButton = new Button("Không");
        yesButton.setStyle("-fx-font-size: 14px;");
        noButton.setStyle("-fx-font-size: 14px;");

        HBox buttonContainer = new HBox(10, yesButton, noButton);
        buttonContainer.setStyle("-fx-alignment: center;");

        final boolean[] result = {false};
        yesButton.setOnAction(_ -> {
            result[0] = true;
            groupMemBUS.removeMember(group.getGroupId(), user.getUserId());
            List<GroupMemberDTO> remainMem = groupMemBUS.getMembersByGroupId(group.getGroupId());
            if (remainMem.size() == 0) groupBUS.deleteGroup(group.getGroupId());
            
            List<String> adminName = groupMemBUS.getAdmin(group.getGroupId());
            if (adminName.size() == 0)
                groupMemBUS.updateMemberAdminStatus(group.getGroupId(), remainMem.get(0).getUserId(), true);
            popupStage.close();
        });
        noButton.setOnAction(_ -> popupStage.close());

        content.getChildren().addAll(confirmLabel, buttonContainer);

        Scene scene = new Scene(content, 300, 150);
        popupStage.setScene(scene);
        popupStage.showAndWait();

        return result[0];
    }

    public void updateNameGroup(GroupDTO group) {
        Stage editStage = new Stage();
        editStage.initModality(Modality.APPLICATION_MODAL);
        editStage.initStyle(StageStyle.TRANSPARENT);

        VBox content = new VBox(10);
        content.setStyle("-fx-background-color: #ffffff; -fx-border-radius: 10; -fx-padding: 15; "
                + "-fx-border-color: #cccccc; -fx-border-width: 1; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 0);");
        content.setPrefSize(300, 300);

        Label titleLabel = new Label("Cập nhật tên nhóm");
        titleLabel.setFont(new Font("Arial", 16));
        titleLabel.setStyle("-fx-font-weight: bold;");


        TextField oldNameField = new TextField(group.getGroupName());
        oldNameField.setDisable(true);
        TextField newNameField = new TextField();
        newNameField.setPromptText("Nhập tên nhóm mới");

        Label errorLabel = new Label();
        errorLabel.setTextFill(Color.RED);
        errorLabel.setVisible(false);

        Button cancelButton = new Button("Hủy");
        cancelButton.setStyle("-fx-background-color: #cccccc; -fx-text-fill: black; -fx-padding: 5 10; -fx-border-radius: 5;");
        cancelButton.setOnAction(_ -> editStage.close());

        Button saveButton = new Button("Lưu");
        saveButton.setStyle("-fx-background-color: #6c63ff; -fx-text-fill: white; -fx-padding: 5 10; -fx-border-radius: 5;");
        saveButton.setOnAction(_ -> {
            String newName = newNameField.getText();

            if (newName.isEmpty()) {
                errorLabel.setText("Nhập tên nhóm mới.");
                errorLabel.setVisible(true);
                return;
            }
            if (newName.equals(oldNameField.getText())) {
                errorLabel.setText("Tên nhóm mới giống tên nhóm cũ.");
                errorLabel.setVisible(true);
                return;
            }

            group.setGroupName(newName);

            if (groupBUS.updateGroup(group)) {
                editStage.close();
            } else {
                System.out.println("Cập nhật thông tin không thành công");
            }
        });

        content.getChildren().addAll(titleLabel, oldNameField, newNameField, errorLabel, saveButton, cancelButton);

        Scene editScene = new Scene(content);
        editScene.setFill(Color.TRANSPARENT);

        editStage.setScene(editScene);
        editStage.showAndWait(); // Đợi popup đóng
    }

    public void addMember(GroupDTO group) {
        Stage editStage = new Stage();
        editStage.initModality(Modality.APPLICATION_MODAL);
        editStage.initStyle(StageStyle.TRANSPARENT);

        VBox content = new VBox(10);
        content.setStyle("-fx-background-color: #ffffff; -fx-border-radius: 10; -fx-padding: 15; "
                + "-fx-border-color: #cccccc; -fx-border-width: 1; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 0);");
        content.setPrefSize(300, 400);

        Label titleLabel = new Label("Thêm thành viên");
        titleLabel.setFont(new Font("Arial", 16));
        titleLabel.setStyle("-fx-font-weight: bold;");

        List<FriendDTO> list_fr = friendBUS.getFriendsByUserId(user.getUserId());
        List<GroupMemberDTO> groupMembers = groupMemBUS.getMembersByGroupId(group.getGroupId());

        List<FriendDTO> availableFriends = list_fr.stream()
        .filter(friend -> groupMembers.stream()
                .noneMatch(member -> member.getUserId() == friend.getFriendId()))
        .toList();

        ObservableList<FriendDTO> selectedFriends = FXCollections.observableArrayList();
        ListView<FriendDTO> friendListView = new ListView<>(FXCollections.observableArrayList(availableFriends));

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

        Label errorLabel = new Label();
        errorLabel.setTextFill(Color.RED);
        errorLabel.setVisible(false);

        Button saveButton = new Button("Save");
        saveButton.setStyle("-fx-font-size: 14px;");
        saveButton.setOnAction(_ -> {
            if (selectedFriends == null || selectedFriends.size() == 0) {
                errorLabel.setText("Thêm ít nhất một thành viên");
                errorLabel.setVisible(true);
                return;
            }

            try {
                for (FriendDTO friend : selectedFriends) {
                    GroupMemberDTO member = new GroupMemberDTO(group.getGroupId(), friend.getFriendId(), false, null, false);
                    groupMemBUS.addMember(member);
                }
        
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION, "Đã gửi yêu cầu vào nhóm", ButtonType.OK);
                successAlert.showAndWait();
                editStage.close();
            } catch (Exception e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, "Đã xảy ra lỗi! Vui lòng thử lại.", ButtonType.OK);
                alert.showAndWait();
            }
        });

        Button cancelButton = new Button("Hủy");
        cancelButton.setStyle("-fx-background-color: #cccccc; -fx-text-fill: black; -fx-padding: 5 10; -fx-border-radius: 5;");
        cancelButton.setOnAction(_ -> editStage.close());

        content.getChildren().addAll(titleLabel, friendListView, errorLabel, saveButton, cancelButton);

        Scene editScene = new Scene(content);
        editScene.setFill(Color.TRANSPARENT);

        editStage.setScene(editScene);
        editStage.showAndWait(); // Đợi popup đóng
    }

    public void updateAdmin(GroupDTO group) {
        Stage editStage = new Stage();
        editStage.initModality(Modality.APPLICATION_MODAL);
        editStage.initStyle(StageStyle.TRANSPARENT);

        VBox content = new VBox(10);
        content.setStyle("-fx-background-color: #ffffff; -fx-border-radius: 10; -fx-padding: 15; "
                + "-fx-border-color: #cccccc; -fx-border-width: 1; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 0);");
        content.setPrefSize(300, 400);

        Label titleLabel = new Label("Gán quyền quản trị viên");
        titleLabel.setFont(new Font("Arial", 16));
        titleLabel.setStyle("-fx-font-weight: bold;");

        List<GroupMemberDTO> groupMembers = groupMemBUS.getMembersByGroupId(group.getGroupId());

        ObservableList<GroupMemberDTO> selectedFriends = FXCollections.observableArrayList();
        ListView<GroupMemberDTO> friendListView = new ListView<>(FXCollections.observableArrayList(groupMembers));

        friendListView.setCellFactory(_ -> new ListCell<>() {
            protected void updateItem(GroupMemberDTO mem, boolean empty) {
                super.updateItem(mem, empty);
                if (empty || mem == null) {
                    setGraphic(null);
                } else {
                    UserDTO member = userBUS.getUserById(mem.getUserId());
                    HBox container = new HBox(10);
                    container.setStyle("-fx-alignment: center-left;");

                    CheckBox checkBox = new CheckBox(member.getUsername());
                    checkBox.setStyle("-fx-font-size: 14px;");
                   
                    if (mem.isAdmin()) {
                        checkBox.setSelected(true); 
                        if (!selectedFriends.contains(mem)) { 
                            selectedFriends.add(mem); 
                        } 
                    }
                    checkBox.selectedProperty().addListener((_, _, newValue) -> {
                        if (newValue) {
                            if (!selectedFriends.contains(mem)) {
                                selectedFriends.add(mem);
                            }
                        } else {
                            selectedFriends.remove(mem);
                        }
                    });

                    container.getChildren().add(checkBox);
                    setGraphic(container);
                }
            }
        });

        Label errorLabel = new Label();
        errorLabel.setTextFill(Color.RED);
        errorLabel.setVisible(false);

        Button saveButton = new Button("Save");
        saveButton.setStyle("-fx-font-size: 14px;");
        saveButton.setOnAction(_ -> {
            if (selectedFriends == null || selectedFriends.size() == 0) {
                errorLabel.setText("Ít nhất một quản trị viên");
                errorLabel.setVisible(true);
                return;
            }

            try {
                List<Integer> oldAdminId = groupMemBUS.getAdminId(group.getGroupId());
                for (int i = 0; i < oldAdminId.size(); i++) {
                    groupMemBUS.updateMemberAdminStatus(group.getGroupId(), oldAdminId.get(i), false);
                }
                for (int i = 0; i < selectedFriends.size(); i++) {
                    GroupMemberDTO mem = selectedFriends.get(i);
                    boolean isSelect = selectedFriends.contains(mem);
                    mem.setAdmin(isSelect);
                    groupMemBUS.updateMemberAdminStatus(group.getGroupId(), mem.getUserId(), isSelect);
                }
        
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION, "Đã gán quyền quản trị viên", ButtonType.OK);
                successAlert.showAndWait();
                editStage.close();
            } catch (Exception e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, "Đã xảy ra lỗi! Vui lòng thử lại.", ButtonType.OK);
                alert.showAndWait();
            }
        });

        Button cancelButton = new Button("Hủy");
        cancelButton.setStyle("-fx-background-color: #cccccc; -fx-text-fill: black; -fx-padding: 5 10; -fx-border-radius: 5;");
        cancelButton.setOnAction(_ -> editStage.close());

        content.getChildren().addAll(titleLabel, friendListView, errorLabel, saveButton, cancelButton);

        Scene editScene = new Scene(content);
        editScene.setFill(Color.TRANSPARENT);

        editStage.setScene(editScene);
        editStage.showAndWait(); // Đợi popup đóng
    }

    public void removeMember(GroupDTO group) {
        Stage editStage = new Stage();
        editStage.initModality(Modality.APPLICATION_MODAL);
        editStage.initStyle(StageStyle.TRANSPARENT);

        VBox content = new VBox(10);
        content.setStyle("-fx-background-color: #ffffff; -fx-border-radius: 10; -fx-padding: 15; "
                + "-fx-border-color: #cccccc; -fx-border-width: 1; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 0);");
        content.setPrefSize(300, 400);

        Label titleLabel = new Label("Xóa thành viên");
        titleLabel.setFont(new Font("Arial", 16));
        titleLabel.setStyle("-fx-font-weight: bold;");

        List<GroupMemberDTO> groupMembers = groupMemBUS.getMembersByGroupId(group.getGroupId());
        for (int i = 0; i < groupMembers.size(); i++) {
            if (groupMembers.get(i).getUserId() == user.getUserId()) groupMembers.remove(groupMembers.get(i));
        }
        ObservableList<GroupMemberDTO> selectedFriends = FXCollections.observableArrayList();
        ListView<GroupMemberDTO> friendListView = new ListView<>(FXCollections.observableArrayList(groupMembers));

        friendListView.setCellFactory(_ -> new ListCell<>() {
            protected void updateItem(GroupMemberDTO mem, boolean empty) {
                super.updateItem(mem, empty);
                if (empty || mem == null) {
                    setGraphic(null);
                } else {
                    UserDTO member = userBUS.getUserById(mem.getUserId());
                    HBox container = new HBox(10);
                    container.setStyle("-fx-alignment: center-left;");

                    CheckBox checkBox = new CheckBox(member.getUsername());
                    checkBox.setStyle("-fx-font-size: 14px;");
                   
                    checkBox.selectedProperty().addListener((_, _, newValue) -> {
                        if (newValue) {
                            if (!selectedFriends.contains(mem)) {
                                selectedFriends.add(mem);
                            }
                        } else {
                            selectedFriends.remove(mem);
                        }
                    });

                    container.getChildren().add(checkBox);
                    setGraphic(container);
                }
            }
        });

        Label errorLabel = new Label();
        errorLabel.setTextFill(Color.RED);
        errorLabel.setVisible(false);

        Button saveButton = new Button("Save");
        saveButton.setStyle("-fx-font-size: 14px;");
        saveButton.setOnAction(_ -> {
            if (selectedFriends == null || selectedFriends.size() == 0) {
                errorLabel.setText("Chọn ít nhất một thành viên");
                errorLabel.setVisible(true);
                return;
            }

            try {
                for (int i = 0; i < selectedFriends.size(); i++) {
                    GroupMemberDTO mem = selectedFriends.get(i);
                    boolean isSelect = selectedFriends.contains(mem);
                    if (isSelect) {
                        groupMemBUS.removeMember(group.getGroupId(), mem.getUserId());
                    }
                    
                }
        
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION, "Đã xóa thành viên", ButtonType.OK);
                successAlert.showAndWait();
                editStage.close();
            } catch (Exception e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, "Đã xảy ra lỗi! Vui lòng thử lại.", ButtonType.OK);
                alert.showAndWait();
            }
        });

        Button cancelButton = new Button("Hủy");
        cancelButton.setStyle("-fx-background-color: #cccccc; -fx-text-fill: black; -fx-padding: 5 10; -fx-border-radius: 5;");
        cancelButton.setOnAction(_ -> editStage.close());

        content.getChildren().addAll(titleLabel, friendListView, errorLabel, saveButton, cancelButton);

        Scene editScene = new Scene(content);
        editScene.setFill(Color.TRANSPARENT);

        editStage.setScene(editScene);
        editStage.showAndWait();
    }

}
