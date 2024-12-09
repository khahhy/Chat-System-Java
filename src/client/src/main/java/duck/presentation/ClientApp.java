package duck.presentation;
import duck.presentation.adminView.*;
import duck.presentation.userView.*;
import duck.presentation.loginView.*;
import duck.bus.UserBUS;
import duck.dto.UserDTO;
import duck.dao.DatabaseConnection;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ClientApp extends Application {

    private Stage primaryStage; 

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        try {
            // check kết nối dữ liệu
            Connection connection = DatabaseConnection.getConnection();

            String query = "SELECT * FROM Users";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            System.out.println("Dữ liệu trong bảng Users:");
            while (resultSet.next()) {
                int userId = resultSet.getInt("user_id");
                String username = resultSet.getString("username");
                String fullName = resultSet.getString("full_name");
                String email = resultSet.getString("email");
                System.out.println("UserID: " + userId + ", Username: " + username + ", FullName: " + fullName + ", Email: " + email);
            }

            // Đóng kết nối
            resultSet.close();
            statement.close();
            connection.close();
            
            showLoginPage();
        } catch (SQLException e) {
            System.out.println("ket noi du lieu khong thanh cong: " + e.getMessage());
        }
        showLoginPage();

        primaryStage.setTitle("Chat System");
        primaryStage.show();
    }

    public void showLoginPage() {
        LoginPage loginPage = new LoginPage(this);
        Scene scene = new Scene(loginPage.getContent(), 1080, 720);
        scene.getStylesheets().add("style.css");
        primaryStage.setScene(scene);
    }

   
    public void showHomePage(UserDTO user) {
        HomePage homePage = new HomePage(this, user);
        Scene scene = new Scene(homePage.getContent(), 1080, 720);
        scene.getStylesheets().add("style.css");
        primaryStage.setScene(scene);
    }

    public void showAdminPage(UserDTO user) {
        AdminPage adminPage = new AdminPage(this, user);
        Scene scene = new Scene(adminPage.getContent(), 1080, 720);
        scene.getStylesheets().add("style.css");
        primaryStage.setScene(scene);
    }

    public void showSignUpPage() {
        LoginPage loginPage = new LoginPage(this);
        RegisterPage registerPage = new RegisterPage(primaryStage, loginPage.getContent());

        Scene scene = new Scene(registerPage, 1080, 720);
        scene.getStylesheets().add("style.css");
        primaryStage.setScene(scene);
    }

    public void showForgotPasswordPage() {
        ForgotPasswordPage forgotPasswordPage = new ForgotPasswordPage(this);

        // Tạo Scene mới cho trang quên mật khẩu
        Scene scene = new Scene(forgotPasswordPage, 1080, 720);
        scene.getStylesheets().add("style.css");
        primaryStage.setScene(scene);
    }

    
    public static void main(String[] args) {

        launch(args);
    }
}
