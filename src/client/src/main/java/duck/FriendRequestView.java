package duck;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

public class FriendRequestView {

    public VBox getContent() {
        VBox content = new VBox(10);
        content.setStyle("-fx-padding: 10;");

        // Thanh tìm kiếm
        TextField searchField = new TextField();
        searchField.setPromptText("Tìm lời mời...");
        searchField.setStyle("-fx-font-size: 14px;");

        // Danh sách lời mời kết bạn
        ListView<String> friendRequests = new ListView<>();
        friendRequests.getItems().addAll("Nguyễn Văn E", "Lê Thị F", "Trần Văn G", "Hoàng Thị H");
        VBox.setVgrow(friendRequests, Priority.ALWAYS);

        // Tùy chỉnh cell
        friendRequests.setCellFactory(_ -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    BorderPane container = new BorderPane();
                    container.setStyle("-fx-background-color: #E8E8E8; -fx-padding: 10;");
                    

                    Text requestName = new Text(item);
                    requestName.setStyle("-fx-font-size: 14px; -fx-fill: #333;");
                    container.setLeft(requestName);
                    // Nút 3 chấm
                    MenuButton optionsButton = new MenuButton();
                    optionsButton.getItems().addAll(
                        new MenuItem("Chấp nhận"),
                        new MenuItem("Từ chối"),
                        new MenuItem("Xem hồ sơ")
                    );
                    container.setRight(optionsButton);

                    setGraphic(container);
                }
            }
        });

        content.getChildren().addAll(searchField, friendRequests);
        return content;
    }
}
