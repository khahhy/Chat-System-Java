package duck.presentation.userView;

import duck.bus.UserBUS;
import duck.dto.UserDTO;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class PopupEditProfile {
    private final BorderPane mainRoot;
    private final UserBUS userBUS;
    private final UserDTO user;

    public PopupEditProfile(BorderPane mainRoot, UserDTO user) {
        this.mainRoot = mainRoot;
        this.userBUS = new UserBUS();
        this.user = user;
    }

    public void showEditPopup() {
        // Tạo Stage cho popup chỉnh sửa
        Stage editStage = new Stage();
        editStage.initModality(Modality.APPLICATION_MODAL);
        editStage.initStyle(StageStyle.TRANSPARENT);

        mainRoot.setEffect(new GaussianBlur(10));

        // Khung nội dung
        VBox content = new VBox(10);
        content.setStyle("-fx-background-color: #ffffff; -fx-border-radius: 10; -fx-padding: 15; "
                + "-fx-border-color: #cccccc; -fx-border-width: 1; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 0);");
        content.setPrefSize(300, 300);

        Label titleLabel = new Label("Chỉnh sửa thông tin");
        titleLabel.setFont(new Font("Arial", 16));
        titleLabel.setStyle("-fx-font-weight: bold;");


        TextField usernameField = new TextField(user.getUsername());
        TextField fullNameField = new TextField(user.getFullName() != null ? user.getFullName() : "");
        TextField addressField = new TextField(user.getAddress() != null ? user.getAddress() : "");

        DatePicker dobPicker = new DatePicker(
            user.getDateOfBirth() != null ? user.getDateOfBirth().toLocalDate() : null
        );
        
        ComboBox<String> genderBox = new ComboBox<>(FXCollections.observableArrayList("Nam", "Nữ"));
        String gender = user.getGender() == 'M' ? "Nam" : (user.getGender() == 'F' ? "Nữ" : null);
        genderBox.setValue(gender);

        TextField emailField = new TextField(user.getEmail());

        // Nút hủy
        Button cancelButton = new Button("Hủy");
        cancelButton.setStyle("-fx-background-color: #cccccc; -fx-text-fill: black; -fx-padding: 5 10; -fx-border-radius: 5;");
        cancelButton.setOnAction(_ -> editStage.close());

        // Nút lưu
        Button saveButton = new Button("Lưu");
        saveButton.setStyle("-fx-background-color: #6c63ff; -fx-text-fill: white; -fx-padding: 5 10; -fx-border-radius: 5;");
        saveButton.setOnAction(_ -> {
            user.setUsername(usernameField.getText().isEmpty() ? null : usernameField.getText());
            user.setFullName(fullNameField.getText().isEmpty() ? null : fullNameField.getText());
            user.setAddress(addressField.getText().isEmpty() ? null : addressField.getText());
        
            if (dobPicker.getValue() != null) 
                user.setDateOfBirth(dobPicker.getValue().atStartOfDay());
            else user.setDateOfBirth(null);
            
            if (genderBox.getValue() != null) 
                user.setGender(genderBox.getValue().equals("Nam") ? 'M' : 'F');
            else user.setGender('U'); 
            
            user.setEmail(emailField.getText());

            if (userBUS.updateUser(user)) {
                editStage.close();
            } else {
                System.out.println("Cập nhật thông tin không thành công");
            }
        });

        content.getChildren().addAll(titleLabel, usernameField, fullNameField, addressField, dobPicker, genderBox, emailField, saveButton, cancelButton);

        Scene editScene = new Scene(content);
        editScene.setFill(Color.TRANSPARENT);

        editStage.setScene(editScene);
        editStage.showAndWait(); // Đợi popup đóng
    }
}
