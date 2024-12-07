package duck.presentation.adminView;

import javafx.scene.control.*;
import javafx.scene.layout.*;

public class Admin_Dashboard {

    public BorderPane getContent() {
        BorderPane root = new BorderPane();
        VBox leftMenu = createLeftMenu(root);
        root.setLeft(leftMenu);

        VBox defaultContent = new VBox();
        root.setCenter(defaultContent);

        return root;
    }

    private VBox createLeftMenu(BorderPane root) {
        VBox leftMenu = new VBox(10);
        leftMenu.setStyle("-fx-background-color: #E7ECEF; -fx-padding: 10;");
        leftMenu.setPrefWidth(250);

        Button userManagementButton = new Button("Quản lý người dùng");
        Button groupManagementButton = new Button("Quản lý nhóm chat");
        Button loginHistoryButton = new Button("Lịch sử đăng nhập");
        Button spamReportsButton = new Button("Báo cáo spam");
        Button friendButton = new Button("Số liệu bạn bè người dùng");
        Button registrationStatsButton = new Button("Thống kê đăng ký mới");
        Button activityStatsButton = new Button("Thống kê hoạt động");

        userManagementButton.setPrefWidth(200);
        groupManagementButton.setPrefWidth(200);
        loginHistoryButton.setPrefWidth(200);
        spamReportsButton.setPrefWidth(200);
        friendButton.setPrefWidth(200);
        registrationStatsButton.setPrefWidth(200);
        activityStatsButton.setPrefWidth(200);

        userManagementButton.setStyle("-fx-font-size: 14px;");
        groupManagementButton.setStyle("-fx-font-size: 14px;");
        loginHistoryButton.setStyle("-fx-font-size: 14px;");
        spamReportsButton.setStyle("-fx-font-size: 14px;");
        friendButton.setStyle("-fx-font-size: 14px;");
        registrationStatsButton.setStyle("-fx-font-size: 14px;");
        activityStatsButton.setStyle("-fx-font-size: 14px;");

       
        userManagementButton.setOnAction(_ -> root.setCenter(new Admin_manageUsers().getContent()));
        groupManagementButton.setOnAction(_ -> root.setCenter(new Admin_manageGroups().getContent()));
        loginHistoryButton.setOnAction(_ -> root.setCenter(new Admin_LoginHistory().getContent()));
        spamReportsButton.setOnAction(_ -> root.setCenter(new Admin_SpamReports().getContent()));
        friendButton.setOnAction(_ -> root.setCenter(new Admin_userFriend().getContent()));
        registrationStatsButton.setOnAction(_ -> root.setCenter(new Admin_newUsers().getContent()));
        activityStatsButton.setOnAction(_ -> root.setCenter(new Admin_userActivity().getContent()));

        leftMenu.getChildren().addAll(userManagementButton, groupManagementButton, loginHistoryButton, spamReportsButton, friendButton, registrationStatsButton,activityStatsButton);
        return leftMenu;
    }
}
