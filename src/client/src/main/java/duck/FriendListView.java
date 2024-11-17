package duck;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

public class FriendListView {

    public VBox getContent() {
        VBox content = new VBox(10);
        content.setStyle("-fx-padding: 10;");

        
        TextField searchField = new TextField();
        searchField.setPromptText("Tìm bạn...");
        searchField.setStyle("-fx-font-size: 14px;");

        
        HBox controls = new HBox(10);
        Button sortButton = new Button("Sắp xếp");
        Button filterOnlineButton = new Button("Đang online");
        sortButton.setStyle("-fx-font-size: 14px;");
        filterOnlineButton.setStyle("-fx-font-size: 14px;");
        controls.getChildren().addAll(sortButton, filterOnlineButton);

        
        ListView<String> friendList = new ListView<>();
        friendList.getItems().addAll("Nguyễn Văn A", "Trần Thị B", "Phạm Minh C", "Đỗ Quốc D");
        VBox.setVgrow(friendList, Priority.ALWAYS);

        
        friendList.setCellFactory(_ -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    
                    BorderPane container = new BorderPane();
                    container.setStyle("-fx-background-color: #E8E8E8; -fx-padding: 10;");
        
                   
                    Text nameText = new Text(item);
                    nameText.setStyle("-fx-font-size: 14px; -fx-fill: #333;");
                    container.setLeft(nameText);
        
                    // Nút MenuButton (bên phải)
                    MenuButton optionsButton = new MenuButton();
                    optionsButton.getItems().addAll(
                        new MenuItem("Xem thông tin"),
                        new MenuItem("Nhắn tin"),
                        new MenuItem("Xóa bạn")
                    );
                    optionsButton.setStyle("-fx-font-size: 14px;");
                    container.setRight(optionsButton);
        
                    // Đặt BorderPane làm thành phần của cell
                    setGraphic(container);
                }
            }
        });
        
        
        
        

        content.getChildren().addAll(searchField, controls, friendList);
        return content;
    }
}
