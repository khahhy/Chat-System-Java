package duck;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class LoginPage {
    private final ClientApp app; 

    public LoginPage(ClientApp app) {
        this.app = app;
    }

    public VBox getContent() {
        VBox loginPage = new VBox(10);
        loginPage.setStyle("-fx-padding: 20; -fx-alignment: center;");

        Label label = new Label("Đăng nhập");
        label.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Button loginButton = new Button("Đăng nhập");
        loginButton.setOnAction(_ -> app.showHomePage()); 

        loginPage.getChildren().addAll(label, loginButton);
        return loginPage;
    }
}
