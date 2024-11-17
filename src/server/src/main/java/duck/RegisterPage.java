package duck;

import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RegisterPage extends VBox {

    public RegisterPage(Stage stage, VBox loginPage, VBox homePage) {
        // Giao diện đăng ký
        Label title = new Label("Đăng ký");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Tên đăng ký");
        Button registerButton = new Button("Đăng ký");
        Button goToLoginButton = new Button("Quay lại Đăng nhập");

        // Sự kiện
        registerButton.setOnAction(e -> {
            if (homePage != null) {
                stage.getScene().setRoot(homePage); // Chuyển đến Home Page
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
        this.getChildren().addAll(title, usernameField, registerButton, goToLoginButton);
        this.setSpacing(10);
        this.setStyle("-fx-alignment: center; -fx-padding: 20;");
    }
}
