package duck.presentation.adminView;
import duck.dto.UserDTO;
import duck.presentation.ClientApp;
import duck.presentation.userView.PopupEditPw;
import duck.bus.LoginHistoryBUS;
import duck.bus.UserBUS;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.image.ImageView;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AdminPage {
    private final ClientApp app; 
    private final UserDTO user;
    private final List<Button> sidebarButtons = new ArrayList<>();
    private final LoginHistoryBUS lh;
    private final UserBUS userBUS;

    public AdminPage(ClientApp app, UserDTO user) {
        this.app = app;
        this.user = user;
        this.lh = new LoginHistoryBUS();
        this.userBUS = new UserBUS();
    }

    public BorderPane getContent() {
        BorderPane root = new BorderPane();

        
        VBox sidebar = createSidebar(root);
        root.setLeft(sidebar);

        root.setCenter(new Admin_Dashboard().getContent());

        return root;
    }

    private VBox createSidebar(BorderPane root) {
        VBox sidebar = new VBox(10);
        sidebar.setStyle("-fx-background-color: #6096BA; -fx-padding: 5;");
        sidebar.setPrefWidth(40);

        VBox mainButtons = new VBox(10);

        VBox footerButton = new VBox();
        Button settingsButton = createSidebarButton("/menu.png", root, "settings");
        footerButton.getChildren().add(settingsButton);
        VBox.setVgrow(mainButtons, javafx.scene.layout.Priority.ALWAYS);
        
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

        button.setOnAction(_ -> footer_Dropdown(button, root));

        return button;
    }

    private void footer_Dropdown(Button button, BorderPane root) {
        ContextMenu settingsMenu = new ContextMenu();
    
        
        MenuItem editPasswordItem = new MenuItem("Edit Password");
        editPasswordItem.setOnAction(_ -> {
            new PopupEditPw(root, user).showEditPwPopup();
        });
           
        MenuItem logoutItem = new MenuItem("Logout");
        logoutItem.setOnAction(_ -> {
            lh.updateLogoutTime(user.getUserId(), LocalDateTime.now());
            user.setOnline(false);
            userBUS.updateUser(user);
            app.showLoginPage();   // app này là bên client app truyền vào ban đầu
        });
    
        settingsMenu.getItems().addAll(editPasswordItem, logoutItem);
    
        settingsMenu.show(button, javafx.geometry.Side.BOTTOM, 0, 0);
    }
}
