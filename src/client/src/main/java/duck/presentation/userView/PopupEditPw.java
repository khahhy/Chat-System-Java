package duck.presentation.userView;

import duck.dto.UserDTO;
import duck.dao.UserDAO;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class PopupEditPw {

    private final BorderPane mainRoot; // Tham chiếu đến BorderPane chính
    private final UserDTO user; // Thông tin người dùng (bao gồm userId, email, password)

    public PopupEditPw(BorderPane mainRoot, UserDTO user) {
        this.mainRoot = mainRoot;
        this.user = user; // Tham chiếu đối tượng userDTO
    }

    public void showEditPwPopup() {
        // Tạo Stage cho popup
        Stage editPwStage = new Stage();
        editPwStage.initModality(Modality.APPLICATION_MODAL); // Chặn tương tác với cửa sổ chính
        editPwStage.initStyle(StageStyle.TRANSPARENT);

        // Khung nội dung
        VBox content = new VBox(10);
        content.setStyle("-fx-background-color: #ffffff; -fx-border-radius: 10; -fx-padding: 15; "
                + "-fx-border-color: #cccccc; -fx-border-width: 1; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 0);");
        content.setPrefSize(350, 300);

        // Tiêu đề
        Label titleLabel = new Label("Đổi mật khẩu");
        titleLabel.setFont(new Font("Arial", 16));
        titleLabel.setStyle("-fx-font-weight: bold;");

        // Các trường nhập mật khẩu
        PasswordField oldPasswordField = new PasswordField();
        oldPasswordField.setPromptText("Nhập mật khẩu cũ");

        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPromptText("Nhập mật khẩu mới");

        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Xác nhận mật khẩu mới");

        // Thông báo lỗi
        Label errorLabel = new Label();
        errorLabel.setTextFill(Color.RED);
        errorLabel.setVisible(false);

        // Nút "Cancel"
        Button cancelButton = new Button("Hủy");
        cancelButton.setStyle("-fx-background-color: #cccccc; -fx-text-fill: black; -fx-padding: 5 10; -fx-border-radius: 5;");
        cancelButton.setOnAction(_ -> {
            editPwStage.close();
        });

        // Nút "Save"
        Button saveButton = new Button("Lưu");
        saveButton.setStyle("-fx-background-color: #6c63ff; -fx-text-fill: white; -fx-padding: 5 10; -fx-border-radius: 5;");
        saveButton.setOnAction(_ -> {
            String oldPassword = oldPasswordField.getText();
            String newPassword = newPasswordField.getText();
            String confirmPassword = confirmPasswordField.getText();

            // Kiểm tra mật khẩu cũ có đúng không
            UserDAO userDAO = new UserDAO();
            if (!userDAO.checkOldPassword(user.getUserId(), oldPassword)) {
                errorLabel.setText("Mật khẩu cũ không đúng.");
                errorLabel.setVisible(true);
                return;
            }

            // Kiểm tra mật khẩu mới và xác nhận
            if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                errorLabel.setText("Mật khẩu mới không được để trống.");
                errorLabel.setVisible(true);
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                errorLabel.setText("Mật khẩu mới và xác nhận không khớp.");
                errorLabel.setVisible(true);
                return;
            }

            // Cập nhật mật khẩu mới
            boolean isUpdated = userDAO.updatePassword(user.getUserId(), newPassword);

            if (isUpdated) {
                errorLabel.setText("Mật khẩu đã được thay đổi thành công.");
                errorLabel.setTextFill(Color.GREEN);
                errorLabel.setVisible(true);
            } else {
                errorLabel.setText("Lỗi khi cập nhật mật khẩu.");
                errorLabel.setVisible(true);
            }
        });

        // Thêm các thành phần vào VBox
        content.getChildren().addAll(
            titleLabel, 
            oldPasswordField, 
            newPasswordField, 
            confirmPasswordField, 
            errorLabel, 
            cancelButton, 
            saveButton
        );

        Scene editPwScene = new Scene(content);
        editPwScene.setFill(Color.TRANSPARENT);

        editPwStage.setScene(editPwScene);

        editPwStage.showAndWait();
    }
}
