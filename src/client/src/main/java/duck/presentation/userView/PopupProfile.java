package duck.presentation.userView;

import duck.dto.UserDTO;
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
    private final UserDTO user;
    private final BorderPane mainRoot; 

    public PopupProfile(BorderPane mainRoot, UserDTO user) {
        this.mainRoot = mainRoot;
        this.user = user;
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

        Label usernameLabel = new Label("Tên đăng nhập: " + user.getUsername());
        Label fullnameLabel = new Label("Tên: " + user.getFullName());
        Label addressLabel = new Label("Địa chỉ: " + user.getAddress());
        Label genderLabel = new Label("Giới tính: " + 
            (user.getGender() == 'M' ? "Nam" : user.getGender() == 'F' ? "Nữ" : ""));

        Label dobLabel = new Label("Ngày sinh: " + 
            (user.getDateOfBirth() != null ? user.getDateOfBirth().toLocalDate() : "01/01/2000"));

        Label emailLabel = new Label("Email: " + user.getEmail());

        Button editButton = new Button("Chỉnh sửa thông tin");
        editButton.setStyle("-fx-background-color: #6c63ff; -fx-text-fill: white; -fx-padding: 5 10; -fx-border-radius: 5;");
        editButton.setOnAction(_ -> {
            new PopupEditProfile(mainRoot, user).showEditPopup();
            refreshView(usernameLabel, fullnameLabel, addressLabel, genderLabel, dobLabel, emailLabel);
        });

        // Nút thoát
        Button closeButton = new Button("Thoát");
        closeButton.setStyle("-fx-background-color: #ff6f61; -fx-text-fill: white; -fx-padding: 5 10; -fx-border-radius: 5;");
        closeButton.setOnAction(_ -> {
            mainRoot.setEffect(null); 
            popupStage.close();
        });

        
        content.getChildren().addAll(titleLabel, usernameLabel, fullnameLabel, addressLabel, genderLabel, dobLabel, emailLabel, editButton, closeButton);

        Scene popupScene = new Scene(content);
        popupScene.setFill(Color.TRANSPARENT);

        popupStage.setScene(popupScene);
        popupStage.setOnHidden(_ -> mainRoot.setEffect(null)); 
        popupStage.showAndWait(); // Đợi đến khi nó đóng
    }

    public void refreshView(Label usernameLabel, Label fullnameLabel, Label addressLabel, Label genderLabel, Label dobLabel, Label emailLabel) {
        usernameLabel.setText("Username: " + user.getUsername());
        fullnameLabel.setText("Fullname: " + user.getFullName());
        addressLabel.setText("Địa chỉ: " + user.getAddress());
        genderLabel.setText("Giới tính: " + 
            (user.getGender() == 'M' ? "Nam" : user.getGender() == 'F' ? "Nữ" : ""));
        dobLabel.setText("Ngày sinh: " + 
            (user.getDateOfBirth() != null ? user.getDateOfBirth().toLocalDate() : "01/01/2000"));
        emailLabel.setText("Email: " + user.getEmail());
    }
    
}
