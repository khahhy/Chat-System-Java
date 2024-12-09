package duck.presentation.userView;

import duck.bus.UserBUS;
import duck.dto.UserDTO;
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
    private final BorderPane mainRoot;
    private final Label genderLabel;
    private final Label dobLabel;
    private final Label addressLabel;
    private final UserDTO user;

    public PopupEditProfile(BorderPane mainRoot, Label genderLabel, Label dobLabel, Label addressLabel, UserDTO user) {
        this.mainRoot = mainRoot;
        this.genderLabel = genderLabel;
        this.dobLabel = dobLabel;
        this.addressLabel = addressLabel;
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

        // Khởi tạo các trường nhập với dữ liệu từ user
        String currentGender = user.getGender() == 'M' ? "Nam" : user.getGender() == 'F' ? "Nữ" : "";
        TextField genderField = new TextField(currentGender);

        String currentDob = user.getDateOfBirth() != null ? user.getDateOfBirth().toLocalDate().toString() : "2000-01-01";
        TextField dobField = new TextField(currentDob);

        String currentAddress = user.getAddress() != null ? user.getAddress() : "Chưa có địa chỉ";
        TextField addressField = new TextField(currentAddress);

        // Nút hủy
        Button cancelButton = new Button("Hủy");
        cancelButton.setStyle("-fx-background-color: #cccccc; -fx-text-fill: black; -fx-padding: 5 10; -fx-border-radius: 5;");
        cancelButton.setOnAction(_ -> editStage.close());

        // Nút lưu
        Button saveButton = new Button("Lưu");
        saveButton.setStyle("-fx-background-color: #6c63ff; -fx-text-fill: white; -fx-padding: 5 10; -fx-border-radius: 5;");
        saveButton.setOnAction(e -> {
            // Cập nhật lại user
            user.setGender(genderField.getText().equalsIgnoreCase("Nam") ? 'M' : 
                           genderField.getText().equalsIgnoreCase("Nữ") ? 'F' : 'U');
            user.setDateOfBirth(java.time.LocalDate.parse(dobField.getText()).atStartOfDay());
            user.setAddress(addressField.getText());

            // Tạo đối tượng UserBUS và gọi phương thức updateUser()
            UserBUS userBUS = new UserBUS();
            boolean updateSuccess = userBUS.updateUser(user); // Gọi phương thức updateUser() từ đối tượng UserBUS
            if (updateSuccess) {
                // Cập nhật lại các Label trong PopupProfile
                genderLabel.setText("Giới tính: " + genderField.getText());
                dobLabel.setText("Ngày sinh: " + dobField.getText());
                addressLabel.setText("Địa chỉ: " + addressField.getText());
                
                // Đóng cửa sổ chỉnh sửa
                editStage.close();
            } else {
                // Hiển thị thông báo lỗi nếu cập nhật thất bại
                System.out.println("Cập nhật thông tin không thành công");
            }
        });

        content.getChildren().addAll(titleLabel, genderField, dobField, addressField, saveButton, cancelButton);

        Scene editScene = new Scene(content);
        editScene.setFill(Color.TRANSPARENT);

        editStage.setScene(editScene);
        editStage.setOnHidden(_ -> mainRoot.setEffect(null));
        editStage.showAndWait(); // Đợi popup đóng
    }
}
