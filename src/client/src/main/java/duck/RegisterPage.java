package duck;

import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RegisterPage extends VBox {

    public RegisterPage(Stage stage, VBox loginPage, BorderPane homePage) {
        // Giao diện đăng ký
        Label title = new Label("Đăng ký");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Label usernameLabel = new Label("Tên đăng nhập:");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Tên đăng ký");
        usernameField.setMaxWidth(250);

        Label passwordLabel = new Label("Mật khẩu:");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Nhập mật khẩu");
        passwordField.setMaxWidth(250);

        Label confirmPasswordLabel = new Label("Xác nhận mật khẩu:");
        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Nhập lại mật khẩu");
        confirmPasswordField.setMaxWidth(250);

        Button registerButton = new Button("Đăng ký");
        registerButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: black; -fx-font-size: 14px; -fx-padding: 10px 20px; -fx-border-radius: 5px;");

        Button goToLoginButton = new Button("Quay lại Đăng nhập");
        goToLoginButton.setStyle("-fx-text-fill: #1E90FF; -fx-font-size: 14px; -fx-background-color: transparent; -fx-underline: true;");

        // Sự kiện
        registerButton.setOnAction(e -> {
            if (homePage != null) {
                stage.getScene().setRoot(loginPage); // Chuyển đến Home Page
            } else {
                System.out.println("Error: homePage is null");
            }
        });

        goToLoginButton.setOnAction(e -> {
            if (loginPage != null) {
                stage.getScene().setRoot(loginPage); // Quay lại Login Page
            } else {
                System.out.println("Error: loginPage is null");
            }
        });

        // Sắp xếp các thành phần
        this.getChildren().addAll(title, usernameLabel, usernameField, passwordLabel, passwordField, confirmPasswordLabel, confirmPasswordField, registerButton, goToLoginButton);
        this.setSpacing(10);
        this.setStyle("-fx-alignment: center; -fx-padding: 20;");
    }
}
