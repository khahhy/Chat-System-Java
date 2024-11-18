package duck;

import java.util.Map;

import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class LoginPage {

    private final Map<String, String> adminAccounts = Map.of(
        "admin1", "password123", "admin2", "adminPass"
    );

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

        TextField passwordTextField = new TextField();
        passwordTextField.setPromptText("Nhập mật khẩu");
        passwordTextField.setMaxWidth(250);
        passwordTextField.setVisible(false);

        passwordField.textProperty().bindBidirectional(passwordTextField.textProperty());

        // checkbox hiển thị mk
        CheckBox showPasswordCheckBox = new CheckBox("Hiển thị mật khẩu");
        showPasswordCheckBox.setOnAction(_ -> {
            if (showPasswordCheckBox.isSelected()) {
                passwordField.setVisible(false);
                passwordTextField.setVisible(true);
            } else {
                passwordField.setVisible(true);
                passwordTextField.setVisible(false);
            }
        });

        Button loginButton = new Button("Đăng nhập");
        loginButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: black; -fx-font-size: 14px; -fx-padding: 10px 20px; -fx-border-radius: 5px;");
        loginButton.setOnAction(_ -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();
        
            if (adminAccounts.containsKey(username) && adminAccounts.get(username).equals(password)) {
                showAdminOrUserChoicePopup(username);
            } else if (isValidUser(username, password)) {
                app.showHomePage(); 
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR); 
                alert.setTitle("Thông báo");
                alert.setHeaderText(null); 
                alert.setContentText("Sai tên đăng nhập hoặc mật khẩu!"); 
                alert.showAndWait();
            }
        });

        Hyperlink signUpLink = new Hyperlink("Chưa có tài khoản? Đăng ký");
        signUpLink.setOnAction(_ -> app.showSignUpPage());
        loginPage.setStyle("-fx-padding: 20; -fx-alignment: center;");

        Hyperlink forgotPasswordLink = new Hyperlink("Quên mật khẩu?");
        forgotPasswordLink.setOnAction(_ -> app.showForgotPasswordPage());


        loginPage.getChildren().addAll(label, usernameLabel, usernameField, passwordLabel, passwordField, loginButton, signUpLink, forgotPasswordLink);
        return loginPage;
    }



    private void showAdminOrUserChoicePopup(String username) {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Lựa chọn vai trò");

        VBox content = new VBox(10);
        content.setStyle("-fx-padding: 20; -fx-alignment: center;");

        Label message = new Label("Xin chào, " + username + "! Bạn muốn sử dụng chế độ nào?");
        message.setStyle("-fx-font-size: 16px;");

        Button adminButton = new Button("Chế độ Quản trị");
        adminButton.setStyle("-fx-font-size: 14px;");
        adminButton.setOnAction(_ -> {
            popupStage.close();
            app.showAdminPage();
        });
        adminButton.setPrefWidth(200);

        Button userButton = new Button("Chế độ Người dùng");
        userButton.setStyle("-fx-font-size: 14px;");
        userButton.setOnAction(_ -> {
            popupStage.close();
            app.showHomePage(); 
        });
        userButton.setPrefWidth(200);
        HBox buttonBox = new HBox(10, adminButton, userButton);
        buttonBox.setStyle("-fx-alignment: center;");

        content.getChildren().addAll(message, buttonBox);

        Scene scene = new Scene(content, 500, 200);
        popupStage.setScene(scene);
        popupStage.showAndWait();
    }

    private boolean isValidUser(String username, String password) {
        // tạm để chạy ui
        return username.equals("java") && password.equals("2024");
    }


}
