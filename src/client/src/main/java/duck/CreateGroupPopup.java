package duck;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class CreateGroupPopup {
    public static class Friend {
        private final String name;
        private final boolean isOnline;

        public Friend(String name, boolean isOnline) {
            this.name = name;
            this.isOnline = isOnline;
        }

        public String getName() { return name;}

        public boolean isOnline() {return isOnline;}

        public String toString() {return name;}
    }

    // Tạo tạm
    private final ObservableList<Friend> friends = FXCollections.observableArrayList(
        new Friend("Nguyễn Văn A", true),
        new Friend("Trần Thị B", false),
        new Friend("Phạm Minh C", true),
        new Friend("Đỗ Quốc D", false)
    );

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

        ObservableList<Friend> selectedFriends = FXCollections.observableArrayList();
        ListView<Friend> friendListView = new ListView<>(friends);

        friendListView.setCellFactory(_ -> new ListCell<>() {
            @Override
            protected void updateItem(Friend friend, boolean empty) {
                super.updateItem(friend, empty);
                if (empty || friend == null) {
                    setGraphic(null);
                } else {
                    HBox container = new HBox(10);
                    container.setStyle("-fx-alignment: center-left;");

                    CheckBox checkBox = new CheckBox(friend.getName());
                    checkBox.setStyle("-fx-font-size: 14px;");

                    if (friend.getName().equals(selectedFriendName)) {
                        checkBox.setSelected(true);
                        selectedFriends.add(friend);
                    }

                    checkBox.selectedProperty().addListener((_, _, newValue) -> {
                        if (newValue) {
                            selectedFriends.add(friend);
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

            System.out.println("Tạo nhóm: " + groupName);
            System.out.println("Thành viên:");
            selectedFriends.forEach(friend -> System.out.println(" - " + friend.getName()));

            popupStage.close();
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
