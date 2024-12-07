package duck.presentation.userView;

import javafx.scene.Scene;
import javafx.scene.control.Button;
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

    private final BorderPane mainRoot; // Tham chiếu đến BorderPane chính
    private final Label genderLabel; // Tham chiếu đến Label giới tính
    private final Label dobLabel; // Tham chiếu đến Label ngày sinh
    private final Label phoneLabel; // Tham chiếu đến Label số điện thoại

    public PopupEditProfile(BorderPane mainRoot, Label genderLabel, Label dobLabel, Label phoneLabel) {
        this.mainRoot = mainRoot;
        this.genderLabel = genderLabel;
        this.dobLabel = dobLabel;
        this.phoneLabel = phoneLabel;
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

        
        TextField genderField = new TextField("Nam");
        TextField dobField = new TextField("01/01/2000");
        TextField phoneField = new TextField("0123456789");

        // hủy
        Button cancelButton = new Button("Hủy");
        cancelButton.setStyle("-fx-background-color: #cccccc; -fx-text-fill: black; -fx-padding: 5 10; -fx-border-radius: 5;");
        cancelButton.setOnAction(_ -> editStage.close());

        // Nút lưu
        Button saveButton = new Button("Lưu");
        saveButton.setStyle("-fx-background-color: #6c63ff; -fx-text-fill: white; -fx-padding: 5 10; -fx-border-radius: 5;");
        saveButton.setOnAction(e -> {
            // cập nhật lại trong profile lun
            genderLabel.setText("Giới tính: " + genderField.getText());
            dobLabel.setText("Ngày sinh: " + dobField.getText());
            phoneLabel.setText("Số điện thoại: " + phoneField.getText());

            editStage.close();
        });

        content.getChildren().addAll(titleLabel, genderField, dobField, phoneField, saveButton, cancelButton);

        Scene editScene = new Scene(content);
        editScene.setFill(Color.TRANSPARENT);

        editStage.setScene(editScene);
        editStage.setOnHidden(_ -> mainRoot.setEffect(null));
        editStage.showAndWait(); // đợi popup đóng
    }
}
