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

public class GroupRequestView {

    static class Group {
        private final String name;
        private final String admin;
        public Group(String name, String admin) {
            this.name = name;
            this.admin = admin;
        }
        public String getName() {return name;}
        public String getAdmin() {return admin;}
    }

    public VBox getContent() {
        VBox content = new VBox(10);
        content.setStyle("-fx-padding: 10;");

        ObservableList<Group> groups = FXCollections.observableArrayList(
            new Group("Nhóm 1", "Admin A"),
            new Group("Nhóm 2", "Admin B"),
            new Group("Nhóm 3", "Admin C"),
            new Group("Nhóm 4", "Admin D")
        );

        ObservableList<Group> displayedGroups = FXCollections.observableArrayList(groups);

        ComboBox<String> sortOptions = new ComboBox<>();
        sortOptions.getItems().addAll("A-Z", "Z-A");
        sortOptions.setValue("A-Z");
        sortOptions.setStyle("-fx-font-size: 14px;");

        ListView<Group> groupList = new ListView<>(displayedGroups);
        VBox.setVgrow(groupList, Priority.ALWAYS);

        groupList.setCellFactory(_ -> new ListCell<>() {
            @Override
            protected void updateItem(Group item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    BorderPane container = new BorderPane();
                    container.setStyle("-fx-background-color: #E8E8E8; -fx-padding: 10;");
        

                    Text groupName = new Text(item.getName());
                    groupName.setStyle("-fx-font-size: 14px; -fx-fill: #333;");
                    container.setLeft(groupName);

                    MenuButton optionsButton = new MenuButton();
                    MenuItem viewInfo = new MenuItem("Xem thông tin");
                    MenuItem accept = new MenuItem("Chấp nhận");
                    MenuItem reject = new MenuItem("Từ chối");

                    optionsButton.getItems().addAll(viewInfo, accept, reject);
                    optionsButton.setStyle("-fx-font-size: 14px;");

                    viewInfo.setOnAction(_ -> showGroupInfoPopup(item));

                    accept.setOnAction(_ -> {
                        groups.remove(item); 
                        displayedGroups.remove(item); 
                    });

                    reject.setOnAction(_ -> {
                        groups.remove(item); 
                        displayedGroups.remove(item); 
                    });

                    container.setRight(optionsButton);
                    
                    setGraphic(container);
                }
            }
        });

        sortOptions.setOnAction(_ -> {
            String sortChoice = sortOptions.getValue();
            if ("A-Z".equals(sortChoice)) {
                displayedGroups.sort((f1, f2) -> f1.getName().compareToIgnoreCase(f2.getName()));
            } else if ("Z-A".equals(sortChoice)) {
                displayedGroups.sort((f1, f2) -> f2.getName().compareToIgnoreCase(f1.getName()));
            }
        });


        content.getChildren().addAll(sortOptions, groupList);
        return content;
    }

    private void showGroupInfoPopup(Group group) {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.initStyle(StageStyle.TRANSPARENT);

        VBox content = new VBox(10);
        content.setStyle("-fx-padding: 20;");

        Label nameLabel = new Label("Tên nhóm: " + group.getName());
        nameLabel.setStyle("-fx-font-size: 16px;");

        Label adminLabel = new Label("Admin: " + group.getAdmin());
        adminLabel.setStyle("-fx-font-size: 16px;");

        Button closeButton = new Button("Đóng");
        closeButton.setStyle("-fx-font-size: 14px;");
        closeButton.setOnAction(_ -> popupStage.close());

        content.getChildren().addAll(nameLabel, adminLabel, closeButton);

        Scene scene = new Scene(content, 300, 200);
        popupStage.setScene(scene);
        popupStage.showAndWait();
    }
}
