package duck.presentation.userView;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class PopupProfile {

    private final BorderPane mainRoot; 

    public PopupProfile(BorderPane mainRoot) {
        this.mainRoot = mainRoot;
    }
    
    public void showPopup() {
        // Tạo một Stage để hiển thị popup
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL); // Chặn tương tác với cửa sổ chính
        popupStage.initStyle(StageStyle.TRANSPARENT); // Không có thanh tiêu đề

        mainRoot.setEffect(new GaussianBlur(10));

        
        VBox content = new VBox(10);
        content.setStyle("-fx-background-color: #ffffff; -fx-border-radius: 10; -fx-padding: 15; "
                + "-fx-border-color: #cccccc; -fx-border-width: 1; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 0);");
        content.setPrefSize(300, 300);

      
        Label titleLabel = new Label("Thông tin tài khoản");
        titleLabel.setFont(new Font("Arial", 16));
        titleLabel.setStyle("-fx-font-weight: bold;");

        
        Label genderLabel = new Label("Giới tính: Nam");
        Label dobLabel = new Label("Ngày sinh: 01/01/2000");
        Label phoneLabel = new Label("Số điện thoại: 0123456789");

        // Nút edit
        Button editButton = new Button("Chỉnh sửa thông tin");
        editButton.setStyle("-fx-background-color: #6c63ff; -fx-text-fill: white; -fx-padding: 5 10; -fx-border-radius: 5;");
        editButton.setOnAction(_ -> {
            new PopupEditProfile(mainRoot, genderLabel, dobLabel, phoneLabel).showEditPopup();
        });

        // Nút thoát
        Button closeButton = new Button("Thoát");
        closeButton.setStyle("-fx-background-color: #ff6f61; -fx-text-fill: white; -fx-padding: 5 10; -fx-border-radius: 5;");
        closeButton.setOnAction(_ -> {
            mainRoot.setEffect(null); 
            popupStage.close();
        });

        
        content.getChildren().addAll(titleLabel, genderLabel, dobLabel, phoneLabel, editButton, closeButton);

        Scene popupScene = new Scene(content);
        popupScene.setFill(Color.TRANSPARENT);

        popupStage.setScene(popupScene);
        popupStage.setOnHidden(_ -> mainRoot.setEffect(null)); 
        popupStage.showAndWait(); // Đợi đến khi nó đóng
    }
}
