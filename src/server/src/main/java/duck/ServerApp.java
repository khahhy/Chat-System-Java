package duck;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ServerApp extends Application {

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

   
    public void showHomePage() {
        HomePage homePage = new HomePage(this);
        Scene scene = new Scene(homePage.getContent(), 1080, 720);
        scene.getStylesheets().add("style.css");
        primaryStage.setScene(scene);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
