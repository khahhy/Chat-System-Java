package duck.presentation.userView;
import duck.presentation.Email;
import duck.presentation.ClientApp;
import duck.dao.UserDAO;
import duck.dto.UserDTO;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;


public class ForgotPasswordPage extends VBox {
    public Email sendEmail = new Email();
    public ForgotPasswordPage(ClientApp app) {

        Label title = new Label("Quên mật khẩu");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Label emaiLabel = new Label("Nhập email của bạn");
        TextField emailField = new TextField();
        emailField.setPromptText("Nhập email của bạn");
        emailField.setMaxWidth(250);
        Button submitButton = new Button("Gửi yêu cầu");
        submitButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: black; -fx-font-size: 14px; -fx-padding: 10px 20px; -fx-border-radius: 5px;");

        submitButton.setOnAction(_ -> {
            String email = emailField.getText();
            UserDTO user = new UserDAO().getUserByEmail(email); // Kiểm tra email có tồn tại trong database

            if (user == null) {
                // Nếu không tìm thấy user
                Alert alert = new Alert(Alert.AlertType.ERROR, "Email không tồn tại trong hệ thống.");
                alert.showAndWait();
            } else {
                // Nếu email tồn tại, gửi mã xác minh
                boolean isSent = sendEmail.sendVerificationCodeToEmail(email);

                Alert alert;
                if (isSent) {
                    alert = new Alert(Alert.AlertType.INFORMATION, "Mã xác minh đã được gửi đến email của bạn.");
                    alert.showAndWait();

                    // Sau khi gửi mã thành công, chuyển về trang đăng nhập
                    app.showLoginPage();
                } else {
                    alert = new Alert(Alert.AlertType.ERROR, "Không thể gửi email. Vui lòng thử lại.");
                    alert.showAndWait();
                }
            }
        });

        this.getChildren().addAll(title, emaiLabel, emailField, submitButton);
        this.setSpacing(10);
        this.setStyle("-fx-alignment: center; -fx-padding: 20;");
    }

    

}
