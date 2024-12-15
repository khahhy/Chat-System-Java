package duck.presentation;
import duck.presentation.adminView.*;
import duck.presentation.userView.*;
import duck.presentation.loginView.*;

import duck.dto.UserDTO;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ClientApp extends Application {

    private Stage primaryStage; 

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
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
