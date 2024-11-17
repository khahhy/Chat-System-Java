package duck;

import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

public class MessagePage {

    public BorderPane getContent() {
        BorderPane root = new BorderPane();

        // Giao diện phía trái: danh sách chat + thanh tìm kiếm
        VBox chatList = createChatList();
        root.setLeft(chatList);

        // Giao diện trung tâm: khung tin nhắn
        VBox chatContent = createChatContent();
        root.setCenter(chatContent);

        // Giao diện bên phải: thông tin người dùng
        VBox userInfo = createUserInfo();
        root.setRight(userInfo);

        // Thêm stylesheet
        root.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

        return root;
    }

    private VBox createChatList() {
        VBox chatBox = new VBox(10);

        // Thanh tìm kiếm
        TextField searchField = new TextField();
        searchField.setPromptText("Tìm người nhắn tin...");
        searchField.setStyle("-fx-font-size: 14px;");

        ListView<String> chatList = new ListView<>();
        chatList.getItems().addAll(
            "Bạn abc", "Trần Văn A", "Nguyễn Văn B", "Mom <3", "Nguyễn Thị Thư",
            "Võ Thị Nhung", "Nguyễn Tú", "Nguyễn Đoàn", "Trần Nghĩa",
            "Bạn 1", "Bạn 2", "Bạn 3", "Bạn 4"
        );
        VBox.setVgrow(chatList, Priority.ALWAYS); // Chiều dài tối đa

        chatBox.getChildren().addAll(searchField, chatList);
        chatBox.setPrefWidth(200);
        chatBox.setStyle("-fx-padding: 10;");

        return chatBox;
    }

    private VBox createChatContent() {
        VBox chatContent = new VBox(10);
        chatContent.setStyle("-fx-padding: 10;");

        // Danh sách tin nhắn
        ScrollPane messagePane = new ScrollPane();
        VBox messageContainer = new VBox(10); // Chứa các tin nhắn
        messageContainer.setStyle("-fx-padding: 10;");

        messagePane.setContent(messageContainer);
        messagePane.setFitToWidth(true);
        VBox.setVgrow(messagePane, Priority.ALWAYS); // Chiều dài tối đa

        // Thêm một số tin nhắn mẫu
        addMessage(messageContainer, "Bạn abc", "hi, đi ăn k", false);
        addMessage(messageContainer, "Bạn", "ăn gì", true);
        addMessage(messageContainer, "Bạn abc", "k biết", false);
        addMessage(messageContainer, "Bạn", "?", true);
        addMessage(messageContainer, "Bạn abc", "chọn đi", false);

        // Khung nhập tin nhắn
        TextField inputField = new TextField();
        inputField.setPromptText("Nhập tin nhắn...");
        inputField.setStyle("-fx-font-size: 14px;");
        inputField.setOnAction(_ -> {
            String message = inputField.getText();
            if (!message.isEmpty()) {
                addMessage(messageContainer, "Bạn", message, true);
                inputField.clear();
                messagePane.setVvalue(1.0); // Cuộn xuống đáy
            }
        });

        HBox inputArea = new HBox(10, inputField);
        chatContent.getChildren().addAll(messagePane, inputArea);

        return chatContent;
    }

    private void addMessage(VBox container, String sender, String message, boolean isUser) {
        HBox messageBox = new HBox();
        Label messageLabel = new Label(message);

        // Style cho tin nhắn
        messageLabel.setStyle("-fx-padding: 10; -fx-background-color: "
            + (isUser ? "#DCF8C6" : "#FFFFFF") + "; -fx-border-radius: 10; "
            + "-fx-background-radius: 10; -fx-font-size: 14px;");
        if (isUser) {
            messageBox.setStyle("-fx-alignment: center-right;"); // Tin nhắn của người dùng
        } else {
            messageBox.setStyle("-fx-alignment: center-left;"); // Tin nhắn của người khác
        }

        messageBox.getChildren().add(messageLabel);
        container.getChildren().add(messageBox);
    }

    private VBox createUserInfo() {
        VBox userInfo = new VBox(10);
        userInfo.setStyle("-fx-padding: 10; -fx-background-color: #F0F0F0;");

        // Avatar
        ImageView avatar = new ImageView(new Image("/user.png"));
        avatar.setFitWidth(80);
        avatar.setFitHeight(80);

        // Tên người dùng
        Label userName = new Label("Bạn abc");
        userName.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Thanh tìm kiếm tin nhắn
        TextField searchField = new TextField();
        searchField.setPromptText("Tìm tin nhắn...");
        searchField.setStyle("-fx-font-size: 14px;");

        // Nút báo cáo spam
        Button spamButton = new Button("Báo Spam");
        spamButton.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-font-size: 14px;");
        spamButton.setOnAction(_ -> System.out.println("Báo cáo spam"));

        // Nút xóa lịch sử
        Button deleteHistoryButton = new Button("Xóa lịch sử");
        deleteHistoryButton.setStyle("-fx-background-color: #FF4500; -fx-text-fill: white; -fx-font-size: 14px;");
        deleteHistoryButton.setOnAction(_ -> System.out.println("Lịch sử đã bị xóa"));

        userInfo.getChildren().addAll(avatar, userName, searchField, spamButton, deleteHistoryButton);

        return userInfo;
    }
}
