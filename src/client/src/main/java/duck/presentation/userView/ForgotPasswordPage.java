package duck.presentation.userView;

import duck.presentation.ClientApp;
import duck.dao.UserDAO;
import duck.dto.UserDTO;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;
import java.util.Random;

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
            String email = emailField.getText();
            UserDTO user = new UserDAO().getUserByEmail(email); // Kiểm tra email có tồn tại trong database

            if (user == null) {
                // Nếu không tìm thấy user
                Alert alert = new Alert(Alert.AlertType.ERROR, "Email không tồn tại trong hệ thống.");
                alert.showAndWait();
            } else {
                // Nếu email tồn tại, gửi mã xác minh
                boolean isSent = sendVerificationCodeToEmail(email);

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

    // Phương thức tạo mã xác minh ngẫu nhiên
    private String generateVerificationCode() {
        Random random = new Random();
        int code = random.nextInt(900000) + 100000;  // Mã 6 chữ số ngẫu nhiên
        return String.valueOf(code);
    }

    // Phương thức gửi mã xác minh qua email
    // Phương thức gửi mã xác minh qua email và cập nhật mật khẩu
private boolean sendVerificationCodeToEmail(String toEmail) {
    String verificationCode = generateVerificationCode();  // Tạo mã xác minh ngẫu nhiên

    // Thiết lập thông tin gửi email
    String SMTP_HOST = "smtp.gmail.com";  // Ví dụ với Gmail SMTP server
    String SMTP_PORT = "587";  // Cổng gửi email
    String EMAIL_FROM = "vohoangduc3012@gmail.com";  // Thay bằng email của bạn
    String EMAIL_PASSWORD = "ociq ccuz zkll bsnf";  // Mật khẩu của email (hoặc sử dụng App Password nếu dùng Gmail)

    Properties properties = new Properties();
    properties.put("mail.smtp.host", SMTP_HOST);
    properties.put("mail.smtp.port", SMTP_PORT);
    properties.put("mail.smtp.auth", "true");
    properties.put("mail.smtp.starttls.enable", "true");

    // Tạo phiên làm việc của email
    Session session = Session.getInstance(properties, new Authenticator() {
        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(EMAIL_FROM, EMAIL_PASSWORD);
        }
    });

    try {
        // Tạo đối tượng email
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(EMAIL_FROM));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
        message.setSubject("Mật khẩu mới của bạn");
        message.setText("Mật khẩu mới của bạn là: " + verificationCode);

        // Gửi email
        Transport.send(message);
        System.out.println("Email đã được gửi thành công!");

        // Cập nhật mật khẩu trong cơ sở dữ liệu
        UserDAO userDAO = new UserDAO();
        boolean isPasswordUpdated = userDAO.updatePasswordByEmail(toEmail, verificationCode);

        return isPasswordUpdated;  // Trả về true nếu cập nhật mật khẩu thành công

    } catch (MessagingException e) {
        e.printStackTrace();
        return false;  // Trả về false nếu có lỗi
    }
}

}
