package duck;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

public class GroupRequestView {

    public VBox getContent() {
        VBox content = new VBox(10);
        content.setStyle("-fx-padding: 10;");

        // Thanh tìm kiếm
        TextField searchField = new TextField();
        searchField.setPromptText("Tìm lời mời nhóm...");
        searchField.setStyle("-fx-font-size: 14px;");

        // Danh sách lời mời vào nhóm
        ListView<String> groupRequests = new ListView<>();
        groupRequests.getItems().addAll("Nhóm X", "Nhóm Y", "Nhóm Z");
        VBox.setVgrow(groupRequests, Priority.ALWAYS);

        // Tùy chỉnh cell
        groupRequests.setCellFactory(_ -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    BorderPane container = new BorderPane();
                    container.setStyle("-fx-background-color: #E8E8E8; -fx-padding: 10;");
        

                    Text requestGroupName = new Text(item);
                    requestGroupName.setStyle("-fx-font-size: 14px; -fx-fill: #333;");
                    container.setLeft(requestGroupName);
                    // Nút 3 chấm
                    MenuButton optionsButton = new MenuButton();
                    optionsButton.getItems().addAll(
                        new MenuItem("Chấp nhận"),
                        new MenuItem("Từ chối"),
                        new MenuItem("Xem chi tiết nhóm")
                    );
                    optionsButton.setStyle("-fx-font-size: 14px;");
                    container.setRight(optionsButton);

                    setGraphic(container);
                }
            }
        });

        content.getChildren().addAll(searchField, groupRequests);
        return content;
    }
}
