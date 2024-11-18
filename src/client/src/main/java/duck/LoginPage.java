package duck;

import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
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

        Label usernameLabel = new Label("Tên đăng nhập:");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Nhập tên đăng nhập");
        usernameField.setMaxWidth(250);

        Label passwordLabel = new Label("Mật khẩu:");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Nhập mật khẩu");
        passwordField.setMaxWidth(250);

        Button loginButton = new Button("Đăng nhập");
        loginButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: black; -fx-font-size: 14px; -fx-padding: 10px 20px; -fx-border-radius: 5px;");
        loginButton.setOnAction(_ -> app.showHomePage()); 

        Hyperlink signUpLink = new Hyperlink("Chưa có tài khoản? Đăng ký");
        signUpLink.setOnAction(e -> app.showSignUpPage());
        loginPage.setStyle("-fx-padding: 20; -fx-alignment: center;");

        Hyperlink forgotPasswordLink = new Hyperlink("Quên mật khẩu?");
        forgotPasswordLink.setOnAction(e -> app.showForgotPasswordPage());


        loginPage.getChildren().addAll(label, usernameLabel, usernameField, passwordLabel, passwordField, loginButton, signUpLink, forgotPasswordLink);
        return loginPage;
    }
}
