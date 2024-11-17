package duck;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class SettingPage {
    public VBox getContent() {
        VBox settingsPage = new VBox(10);
        settingsPage.setStyle("-fx-padding: 10; -fx-alignment: center;");

        Label label = new Label("Cài đặt");
        label.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Button privacyButton = new Button("Chính sách riêng tư");
        privacyButton.setOnAction(e -> System.out.println("Xem/chỉnh sửa cài đặt riêng tư."));

        Button accountSettingsButton = new Button("Cài đặt tài khoản");
        accountSettingsButton.setOnAction(e -> System.out.println("Cài đặt tài khoản người dùng."));

        settingsPage.getChildren().addAll(label, privacyButton, accountSettingsButton);
        return settingsPage;
    }
}
