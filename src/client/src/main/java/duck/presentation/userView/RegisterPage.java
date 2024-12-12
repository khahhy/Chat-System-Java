package duck.presentation.userView;

import org.mindrot.jbcrypt.BCrypt;
import javafx.scene.layout.VBox;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import duck.bus.UserBUS;
import duck.dto.UserDTO;

public class RegisterPage extends VBox {

    public RegisterPage(Stage stage, VBox loginPage) {
        // Giao diện đăng ký
        Label title = new Label("Đăng ký");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Label usernameLabel = new Label("Tên đăng nhập:");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Tên đăng ký");
        usernameField.setMaxWidth(250);

        Label emailLabel = new Label("Email:");
        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        emailField.setMaxWidth(250);

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

        // Sự kiện đăng ký
        registerButton.setOnAction(e -> {
            String username = usernameField.getText();
            String email = emailField.getText();
            String password = passwordField.getText();
            String confirmPassword = confirmPasswordField.getText();

            // Kiểm tra xem các trường có trống không
            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Vui lòng điền đầy đủ thông tin!");
                alert.showAndWait();
                return;
            }

            // Kiểm tra mật khẩu khớp
            if (!password.equals(confirmPassword)) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Mật khẩu không khớp!");
                alert.showAndWait();
                return;
            }

            UserBUS temp = new UserBUS();
            if (temp.checkExistEmail(email)) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Email đã tồn tại");
                alert.showAndWait();
                return;
            }
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
            // Tạo đối tượng UserDTO
            UserDTO userDTO = new UserDTO(
                0, // userId
                username,
                "", // fullName
                "", // address
                null, // dateOfBirth
                'U', // gender (default: not specified)
                email,
                hashedPassword,
                true, // status
                false, // isOnline
                null, // createdAt
                false // isAdmin
            );

            // Tạo đối tượng UserBUS và gọi phương thức đăng ký
            UserBUS userBUS = new UserBUS();
            boolean isRegistered = userBUS.addUser(userDTO);

            Alert alert;
            if (isRegistered) {
                alert = new Alert(Alert.AlertType.INFORMATION, "Đăng ký thành công!");
                alert.showAndWait();

                // Sau khi đăng ký thành công, quay lại trang đăng nhập
                if (loginPage != null) {
                    stage.getScene().setRoot(loginPage); // Quay lại Login Page
                }
            } else {
                alert = new Alert(Alert.AlertType.ERROR, "Đăng ký thất bại. Vui lòng thử lại!");
                alert.showAndWait();
            }
        });

        // Sự kiện quay lại trang đăng nhập
        goToLoginButton.setOnAction(e -> {
            if (loginPage != null) {
                stage.getScene().setRoot(loginPage); // Quay lại Login Page
            } else {
                System.out.println("Error: loginPage is null");
            }
        });

        // Sắp xếp các thành phần
        this.getChildren().addAll(title, usernameLabel, usernameField, emailLabel, emailField, passwordLabel, passwordField, confirmPasswordLabel, confirmPasswordField, registerButton, goToLoginButton);
        this.setSpacing(10);
        this.setStyle("-fx-alignment: center; -fx-padding: 20;");
    }
}
