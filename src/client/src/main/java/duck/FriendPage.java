package duck;

import javafx.scene.control.*;
import javafx.scene.layout.*;

public class FriendPage {

    public BorderPane getContent() {
        BorderPane root = new BorderPane();

        // Giao diện bên trái
        VBox leftMenu = createLeftMenu(root);
        root.setLeft(leftMenu);

        // Giao diện bên phải (nội dung)
        VBox defaultContent = new VBox(new Label("Chọn một mục từ menu bên trái."));
        root.setCenter(defaultContent);

        return root;
    }

    private VBox createLeftMenu(BorderPane root) {
        VBox leftMenu = new VBox(10);
        leftMenu.setStyle("-fx-background-color: #F4F4F4; -fx-padding: 10;");
        leftMenu.setPrefWidth(200);

        // Thanh tìm kiếm
        TextField searchField = new TextField();
        searchField.setPromptText("Tìm kiếm...");
        searchField.setStyle("-fx-font-size: 14px;");

        // Các nút
        Button friendListButton = new Button("Danh sách bạn bè");
        Button groupListButton = new Button("Danh sách nhóm");
        Button friendRequestsButton = new Button("Lời mời kết bạn");
        Button groupRequestsButton = new Button("Lời mời vào nhóm");

        friendListButton.setPrefWidth(180);
        groupListButton.setPrefWidth(180);
        friendRequestsButton.setPrefWidth(180);
        groupRequestsButton.setPrefWidth(180);

        friendListButton.setStyle("-fx-font-size: 14px;");
        groupListButton.setStyle("-fx-font-size: 14px;");
        friendRequestsButton.setStyle("-fx-font-size: 14px;");
        groupRequestsButton.setStyle("-fx-font-size: 14px;");

        // Sự kiện nhấn nút: Cập nhật nội dung bên phải
        friendListButton.setOnAction(_ -> root.setCenter(new FriendListView().getContent()));
        groupListButton.setOnAction(_ -> root.setCenter(new GroupListView().getContent()));
        friendRequestsButton.setOnAction(_ -> root.setCenter(new FriendRequestView().getContent()));
        groupRequestsButton.setOnAction(_ -> root.setCenter(new GroupRequestView().getContent()));

        leftMenu.getChildren().addAll(searchField, friendListButton, groupListButton, friendRequestsButton, groupRequestsButton);
        return leftMenu;
    }
}
