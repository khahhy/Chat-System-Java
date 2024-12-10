package duck.presentation.loginView;
import duck.dao.DatabaseConnection;
import duck.dto.LoginHistoryDTO;
import duck.dto.UserDTO;
import duck.bus.LoginHistoryBUS;
import duck.bus.UserBUS;
import duck.presentation.ClientApp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Optional;

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
    private final ClientApp app; 
    private final LoginHistoryBUS loginHistoryBUS;
    private final UserBUS userBUS;

    public LoginPage(ClientApp app) {
        this.app = app;
        this.loginHistoryBUS = new LoginHistoryBUS();
        this.userBUS = new UserBUS();
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
                
                    // Kiểm tra xem tài khoản có phải admin không
            Optional<UserDTO> adminOpt = isAdmin(username, password);
            if (adminOpt.isPresent()) {
                UserDTO adminUser = adminOpt.get();
                showAdminOrUserChoicePopup(username, adminUser); // Hiển thị popup lựa chọn vai trò.
            } else {
                // Kiểm tra xem tài khoản có hợp lệ không (user thông thường)
                Optional<UserDTO> userOpt = isValidUser(username, password);
                if (userOpt.isPresent()) {
                    UserDTO user = userOpt.get(); // Lấy thông tin người dùng từ Optional.
                    loginHistoryBUS.addLoginHistory(new LoginHistoryDTO(0, user.getUserId(), LocalDateTime.now(), null));
                    user.setOnline(true);
                    userBUS.updateUser(user);
                    app.showHomePage(user); // Chuyển đến trang chủ với đối tượng UserDTO.
                } else {
                    // Thông báo lỗi khi đăng nhập không hợp lệ.
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Thông báo");
                    alert.setHeaderText(null);
                    alert.setContentText("Sai tên đăng nhập hoặc mật khẩu!");
                    alert.showAndWait();
                }
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



    private void showAdminOrUserChoicePopup(String username, UserDTO user) {
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
            loginHistoryBUS.addLoginHistory(new LoginHistoryDTO(0, user.getUserId(), LocalDateTime.now(), null));
            user.setOnline(true);
            userBUS.updateUser(user);
            app.showAdminPage(user);
        });
        adminButton.setPrefWidth(200);

        Button userButton = new Button("Chế độ Người dùng");
        userButton.setStyle("-fx-font-size: 14px;");
        userButton.setOnAction(_ -> {
            popupStage.close();
            loginHistoryBUS.addLoginHistory(new LoginHistoryDTO(0, user.getUserId(), LocalDateTime.now(), null));
            user.setOnline(true);
            userBUS.updateUser(user);
            app.showHomePage(user);
        });
        userButton.setPrefWidth(200);
        HBox buttonBox = new HBox(10, adminButton, userButton);
        buttonBox.setStyle("-fx-alignment: center;");

        content.getChildren().addAll(message, buttonBox);

        Scene scene = new Scene(content, 500, 200); 
        popupStage.setScene(scene);
        popupStage.showAndWait();
    }

    private Optional<UserDTO> isValidUser(String username, String password) {
        String query = "SELECT user_id, username, full_name, address, date_of_birth, gender, email, password, status, is_online, created_at, is_admin " +
                               "FROM Users WHERE username = ? AND password = ? AND status = TRUE";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
                    
            stmt.setString(1, username);
            stmt.setString(2, password); // Nếu mật khẩu được băm, xử lý băm trước khi so sánh.
                    
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                LocalDateTime dateOfBirth = null;
                if (rs.getTimestamp("date_of_birth") != null) {
                    dateOfBirth = rs.getTimestamp("date_of_birth").toLocalDateTime();
                }
            
                String genderStr = rs.getString("gender");
                char gender = (genderStr != null && !genderStr.isEmpty()) ? genderStr.charAt(0) : 'U'; // 'U' cho unknown
            
                UserDTO user = new UserDTO (
                    rs.getInt("user_id"),
                    rs.getString("username"),
                    rs.getString("full_name"),
                    rs.getString("address"),
                    dateOfBirth,
                    gender,
                    rs.getString("email"),
                    rs.getString("password"),
                    rs.getBoolean("status"),
                    rs.getBoolean("is_online"),
                    rs.getTimestamp("created_at").toLocalDateTime(),
                    rs.getBoolean("is_admin")
                );
                return Optional.of(user); // Trả về đối tượng UserDTO nếu hợp lệ.
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty(); // Trả về Optional.empty() nếu không tìm thấy tài khoản.
    }
            
    private Optional<UserDTO> isAdmin(String username, String password) {
        String query = "SELECT user_id, username, full_name, address, date_of_birth, gender, email, password, status, is_online, created_at, is_admin " +
                               "FROM Users WHERE username = ? AND password = ? AND status = TRUE AND is_admin = TRUE";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
                    
            stmt.setString(1, username);
            stmt.setString(2, password); // Nếu mật khẩu được băm, xử lý băm trước khi so sánh.
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                LocalDateTime dateOfBirth = null;
                if (rs.getTimestamp("date_of_birth") != null) {
                    dateOfBirth = rs.getTimestamp("date_of_birth").toLocalDateTime();
                }
            
                String genderStr = rs.getString("gender");
                char gender = (genderStr != null && !genderStr.isEmpty()) ? genderStr.charAt(0) : 'U'; 
            
                UserDTO adminUser = new UserDTO(
                    rs.getInt("user_id"),
                    rs.getString("username"),
                    rs.getString("full_name"),
                    rs.getString("address"),
                    dateOfBirth,
                    gender,
                    rs.getString("email"),
                    rs.getString("password"),
                    rs.getBoolean("status"),
                    rs.getBoolean("is_online"),
                    rs.getTimestamp("created_at").toLocalDateTime(),
                    rs.getBoolean("is_admin")
                );
                return Optional.of(adminUser);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty(); 
    }
}
