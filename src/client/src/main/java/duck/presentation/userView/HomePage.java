package duck.presentation.userView;
import duck.presentation.ClientApp;
import duck.bus.UserBUS;
import duck.dto.UserDTO;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.image.ImageView;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class HomePage {
    private final ClientApp app; 
    private final UserDTO user;

    private final List<Button> sidebarButtons = new ArrayList<>();

    public HomePage(ClientApp app, UserDTO user) {
        this.app = app;
        this.user = user;
    }

    public BorderPane getContent() {
        BorderPane root = new BorderPane();

        
        VBox sidebar = createSidebar(root);
        root.setLeft(sidebar);

        root.setCenter(new MessagePage().getContent());

        return root;
    }

    private VBox createSidebar(BorderPane root) {
        VBox sidebar = new VBox(10);
        sidebar.setStyle("-fx-background-color: #6096BA; -fx-padding: 5;");
        sidebar.setPrefWidth(40);

        VBox mainButtons = new VBox(10);
        Button profileButton = createSidebarButton("/user.png", root, "profile");
        Button messageButton = createSidebarButton("/message.png", root, "message");
        Button friendButton = createSidebarButton("/friend.png", root, "friend");

        mainButtons.getChildren().addAll(profileButton, messageButton, friendButton);

        VBox footerButton = new VBox();
        Button settingsButton = createSidebarButton("/menu.png", root, "settings");
        footerButton.getChildren().add(settingsButton);
        VBox.setVgrow(mainButtons, javafx.scene.layout.Priority.ALWAYS);
        
        sidebarButtons.add(profileButton);
        sidebarButtons.add(messageButton);
        sidebarButtons.add(friendButton);
        sidebarButtons.add(settingsButton);

        sidebar.getChildren().addAll(mainButtons, footerButton);
        return sidebar;
    }

    private Button createSidebarButton(String image, BorderPane root, String pageName) {
        Button button = new Button();
        button.setMaxWidth(Double.MAX_VALUE);
        button.setStyle("-fx-background-color: transparent;");
        ImageView icon = new ImageView(image);
        icon.setFitWidth(24); 
        icon.setFitHeight(24); 
        button.setGraphic(icon);

        button.setOnAction(e -> {
            switch (pageName) {
                case "profile":
                    new PopupProfile(root, user).showPopup();
                    break;
                case "message":
                    root.setCenter(new MessagePage().getContent());
                    break;
                case "friend":
                    root.setCenter(new FriendPage().getContent());
                    break;
                case "settings":
                    footer_Dropdown(button, root);
                    break;
            }

            updateButtonStyles(button);
        });

        return button;
    }

    private void footer_Dropdown(Button button, BorderPane root) {
        ContextMenu settingsMenu = new ContextMenu();
    
        
        MenuItem editPasswordItem = new MenuItem("Edit Password");
        editPasswordItem.setOnAction(e -> {
            new PopupEditPw(root, user).showEditPwPopup();
        });
    
        
        MenuItem logoutItem = new MenuItem("Logout");
        logoutItem.setOnAction(_ -> {
            app.showLoginPage();   // app này là bên client app truyền vào ban đầu
        });
    
        settingsMenu.getItems().addAll(editPasswordItem, logoutItem);
    
        settingsMenu.show(button, javafx.geometry.Side.BOTTOM, 0, 0);
    }

    private void updateButtonStyles(Button activeButton) {
        for (Button button : sidebarButtons) {
            button.setStyle("-fx-background-color: transparent;");
        }
        
        activeButton.setStyle("-fx-background-color: #274C77; -fx-text-fill: white; -fx-font-weight: bold;");
    }
}
