package duck.presentation;

import javax.mail.*;
import javax.mail.internet.*;

import org.mindrot.jbcrypt.BCrypt;

import java.util.Properties;
import java.util.Random;

import duck.dao.UserDAO;

public class Email {
    // Phương thức tạo mã xác minh ngẫu nhiên
    private String generateVerificationCode() {
        Random random = new Random();
        int code = random.nextInt(900000) + 100000;  // Mã 6 chữ số ngẫu nhiên
        return String.valueOf(code);
    }

    // Phương thức gửi mã xác minh qua email
    // Phương thức gửi mã xác minh qua email và cập nhật mật khẩu
    public boolean sendVerificationCodeToEmail(String toEmail) {
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
            String hashedPassword = BCrypt.hashpw(verificationCode, BCrypt.gensalt());
        // Gửi email
            Transport.send(message);
            System.out.println("Email đã được gửi thành công!");

        // Cập nhật mật khẩu trong cơ sở dữ liệu
            UserDAO userDAO = new UserDAO();
            boolean isPasswordUpdated = userDAO.updatePasswordByEmail(toEmail, hashedPassword);

            return isPasswordUpdated;  // Trả về true nếu cập nhật mật khẩu thành công

        } catch (MessagingException e) {
            e.printStackTrace();
            return false;  // Trả về false nếu có lỗi
        }
    }
}
