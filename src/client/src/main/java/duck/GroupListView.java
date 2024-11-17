package duck;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

public class GroupListView {

    public VBox getContent() {
        VBox content = new VBox(10);
        content.setStyle("-fx-padding: 10;");

        // Thanh tìm kiếm
        TextField searchField = new TextField();
        searchField.setPromptText("Tìm nhóm...");
        searchField.setStyle("-fx-font-size: 14px;");

        // Danh sách nhóm
        ListView<String> groupList = new ListView<>();
        groupList.getItems().addAll("Nhóm A", "Nhóm B", "Nhóm C", "Nhóm D");
        VBox.setVgrow(groupList, Priority.ALWAYS);

        // Tùy chỉnh cell
        groupList.setCellFactory(_ -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    BorderPane container = new BorderPane();
                    container.setStyle("-fx-background-color: #E8E8E8; -fx-padding: 10;");
        

                    Text groupName = new Text(item);
                    groupName.setStyle("-fx-font-size: 14px; -fx-fill: #333;");
                    container.setLeft(groupName);

                    MenuButton optionsButton = new MenuButton();
                    optionsButton.getItems().addAll(
                        new MenuItem("Xem thông tin nhóm"),
                        new MenuItem("Rời nhóm"),
                        new MenuItem("Mời thành viên")
                    );
                    optionsButton.setStyle("-fx-font-size: 14px;");
                    container.setRight(optionsButton);
                    
                    setGraphic(container);
                }
            }
        });

        content.getChildren().addAll(searchField, groupList);
        return content;
    }
}
