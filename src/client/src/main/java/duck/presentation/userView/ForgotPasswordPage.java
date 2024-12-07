package duck.presentation.userView;
import duck.presentation.ClientApp;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class ForgotPasswordPage extends VBox {

    public ForgotPasswordPage(ClientApp app) {
        
        Label title = new Label("Quên mật khẩu");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Label emaiLabel = new Label("Nhập email của bạn");
        TextField emailField = new TextField();
        emailField.setPromptText("Nhập email của bạn");
        emailField.setMaxWidth(250);
        Button submitButton = new Button("Gửi yêu cầu");
        submitButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: black; -fx-font-size: 14px; -fx-padding: 10px 20px; -fx-border-radius: 5px;");

        submitButton.setOnAction(e -> {
            app.showLoginPage();
        });

        this.getChildren().addAll(title, emaiLabel, emailField, submitButton);
        this.setSpacing(10);
        this.setStyle("-fx-alignment: center; -fx-padding: 20;");
    }
}
