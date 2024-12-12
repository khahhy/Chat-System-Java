package duck.presentation.userView;

import java.util.List;
import java.util.function.Consumer;

import org.postgresql.translation.messages_bg;

import duck.dto.MessageDTO;
import duck.bus.MessageBUS;
import duck.dao.MessageDAO;
import duck.bus.FriendBUS;
import duck.bus.UserBUS;
import duck.dto.UserDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;   
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MessagePage {
    private final UserDTO user;
    private UserDTO opponent;
    public MessagePage(UserDTO user) {
        this.user = user;
        this.opponent = user;
        loadFriend();
    }    

    // tạm để thể hiện u

    public class Group {
        private String name;
        private String admin;
        private List<String> members;

        public Group(String name, String admin, List<String> members) {
            this.name = name; this.admin = admin; this.members = members;
        }
        public String getName() {return name;}
        public String getAdmin() {return admin;}
        public List<String> getMembers() {return members;}
        public void setName(String n) {name = n;}
        public void setAdmin(String n) {admin = n;}
    }

    private final ObservableList<Object> chatData = FXCollections.observableArrayList(
        new Group("học java", "duck", List.of("duck", "Nguyễn Văn B", "Võ Thị Nhung", "Trần Nghĩa")),
       
        new Group("phòng 1", "khahhy", List.of("Trần Thị C", "khahhy", "Nguyễn E"))
    );

    private void loadFriend() {
        MessageBUS messageBUS = new MessageBUS(); 
        List<UserDTO> friends = messageBUS.getFriendsFromMessage(user.getUserId()); 
        for (UserDTO otherUser : friends) {
            if (otherUser.getUserId() != user.getUserId()) { 
                chatData.add(otherUser);
            }
        }
    }
    

    private VBox userInfoContainer = new VBox(10);
    private VBox chatContentContainer = new VBox(10);

    public BorderPane getContent() {
        BorderPane root = new BorderPane();

        // trái: danh sách chat + thanh tìm kiếm
        VBox chatList = createChatList();
        root.setLeft(chatList);

        // trung tâm: khung tin nhắn
        VBox chatContent = createChatContent();
        root.setCenter(chatContent);

        // phải: thông tin 
        VBox userInfo = createUserInfo();
        root.setRight(userInfo);
        return root;
    }

    private VBox createChatList() {
        VBox chatBox = new VBox(10);

        TextField searchField = new TextField();
        searchField.setPromptText("Tìm người nhắn tin...");
        searchField.setStyle("-fx-font-size: 14px;");

        ListView<Object> chatList = new ListView<>();
        for (Object item : chatData) {
            if (item instanceof UserDTO) {
                chatList.getItems().add(((UserDTO) item).getFullName());
            } else if (item instanceof Group) {
                chatList.getItems().add(((Group) item).getName());
            }
        }
        chatList.setCellFactory(_ -> new ListCell<>() {
            @Override
            protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);
    
                if (empty || item == null) {
                    setText(null);
                    setStyle(null); 
                } else {
                    setText(item.toString());
                    
                    if (chatList.getSelectionModel().getSelectedItem() == item) {
                        setStyle("-fx-background-color: #6096BA; -fx-text-fill: white; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-background-color: transparent; -fx-text-fill: black;");
                    }
                }
            }
        });
        chatList.getSelectionModel().selectedItemProperty().addListener((_, _, newValue) -> {
            updateUserInfo(newValue); 
            chatList.refresh();
        });

        VBox.setVgrow(chatList, Priority.ALWAYS); 

        chatBox.getChildren().addAll(searchField, chatList);
        chatBox.setPrefWidth(250);
        chatBox.setStyle("-fx-padding: 10;");

        return chatBox;
    }

    private VBox createChatContent() {
        VBox chatContent = new VBox(10);
        chatContentContainer.setStyle("-fx-padding: 10;");
        

        return chatContentContainer;
    }

    private void addMessage(VBox container, MessageDTO message) {
        HBox messageBox = new HBox();
        Label messageLabel = new Label(message.getContent());

       
        messageLabel.setStyle("-fx-padding: 10; -fx-background-color: "
            + ((message.getSenderId() == user.getUserId()) ? "#DCF8C6" : "#FFFFFF") + "; -fx-border-radius: 10; "
            + "-fx-background-radius: 10; -fx-font-size: 14px;");
        
        MenuButton optionsMenu = new MenuButton();
        MenuItem deleteItem = new MenuItem("Xóa tin nhắn");
        deleteItem.setOnAction(_ -> {
            MessageBUS temp = new MessageBUS();
            if (temp.deleteMessage(message.getMessageId())) {
                container.getChildren().remove(messageBox);
            }
        });
        optionsMenu.getItems().add(deleteItem);
        optionsMenu.setStyle("-fx-font-size: 12px; -fx-background-color: transparent;");

        if (message.getSenderId() == user.getUserId()) {
            messageBox.setStyle("-fx-alignment: center-right;"); // mình nhắn
            messageBox.getChildren().addAll(optionsMenu, messageLabel);
        } else {
            messageBox.setStyle("-fx-alignment: center-left;"); 
            messageBox.getChildren().addAll(messageLabel, optionsMenu);
        }

        container.getChildren().add(messageBox);
    }

    private VBox createUserInfo() {
        userInfoContainer.setStyle("-fx-padding: 10; -fx-background-color: #F0F0F0;");
        userInfoContainer.setPrefWidth(250);
        VBox.setVgrow(userInfoContainer, Priority.ALWAYS);
    
        return userInfoContainer;
    }


    private void updateUserInfo(Object selected) {
        userInfoContainer.getChildren().clear();
    
        if (selected instanceof String name) {
            for (Object item : chatData) {
                if (item instanceof UserDTO && ((UserDTO) item).getFullName().equals(name)) {
                    updateFriendInfo((UserDTO) item);
                    opponent = ((UserDTO) item);
                    chatContentContainer.getChildren().clear();
                    updateChat();
                    return;
                } else if (item instanceof Group && ((Group) item).getName().equals(name)) {
                    updateGroupInfo((Group) item);
                    return;
                }
            }
        }
    }

    private void updateChat() {
        ScrollPane messagePane = new ScrollPane();
        VBox messageContainer = new VBox(10); 
        messageContainer.setStyle("-fx-padding: 10;");

        messagePane.setContent(messageContainer);
        messagePane.setFitToWidth(true);
        VBox.setVgrow(messagePane, Priority.ALWAYS); 
        List<MessageDTO> messageData;
        MessageBUS temp = new MessageBUS();
        messageData = temp.getMessagesBetweenUsers(opponent.getUserId(), user.getUserId());
        for (MessageDTO message : messageData) {
            addMessage(messageContainer, message);
        }
        
        TextField inputField = new TextField();
        inputField.setPromptText("Nhập tin nhắn...");
        inputField.setStyle("-fx-font-size: 14px;");
        inputField.setOnAction(_ -> {
            String message = inputField.getText();
            if (!message.isEmpty()) {
                MessageDTO messageSend = new MessageDTO(user.getUserId(), opponent.getUserId(), message);
                if (temp.addMessage(messageSend)) {
                    addMessage(messageContainer, messageSend);
                }
                inputField.clear();
                messagePane.setVvalue(1.0); // Cuộn xuống đáy
            }
        });
        inputField.prefWidthProperty().bind(chatContentContainer.widthProperty()); // chiều rộng bằng nhau
       
        HBox inputArea = new HBox(10, inputField);
        chatContentContainer.getChildren().addAll(messagePane, inputArea);
    }
    
    private void updateFriendInfo(UserDTO friend) {
        Label userName = new Label(friend.getFullName());
        userName.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        TextField searchField = new TextField();
        searchField.setPromptText("Tìm kiếm tin nhắn...");
        searchField.setStyle("-fx-font-size: 14px;");
        searchField.setPrefWidth(250);
        searchField.textProperty().addListener((_, _, _) -> {
        
        });
        Button spamButton = new Button("Báo Spam");
        spamButton.setStyle("-fx-background-color: #274C77; -fx-text-fill: white; -fx-font-size: 14px;");
        spamButton.setPrefWidth(150);
    
        Button deleteHistoryButton = new Button("Xóa đoạn chat");
        deleteHistoryButton.setStyle("-fx-background-color: #274C77; -fx-text-fill: white; -fx-font-size: 14px;");
        deleteHistoryButton.setPrefWidth(150);
    
        userInfoContainer.getChildren().addAll(userName, searchField, spamButton, deleteHistoryButton);
    }
    
    private void updateGroupInfo(Group group) {
        Label groupName = new Label(group.getName());
        groupName.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
    
        Label adminLabel = new Label("Admin: " + group.getAdmin());
        adminLabel.setStyle("-fx-font-size: 14px;");
        
        TextField searchField = new TextField();
        searchField.setPromptText("Tìm kiếm tin nhắn...");
        searchField.setStyle("-fx-font-size: 14px;");
        searchField.setPrefWidth(250);
        searchField.textProperty().addListener((_, _, _) -> {
        
        });

        ComboBox<String> memberDropdown = new ComboBox<>();
        memberDropdown.getItems().addAll(group.getMembers());
        memberDropdown.setPromptText("Thành viên");
        
        
        Button renameButton = new Button("Đổi tên nhóm");
        Button addMemberButton = new Button("Thêm thành viên");
        Button assignAdminButton = new Button("Gán quyền admin");
        Button removeMemberButton = new Button("Xóa thành viên");
    
        renameButton.setStyle("-fx-background-color: #274C77; -fx-text-fill: white; -fx-font-size: 14px;");
        addMemberButton.setStyle("-fx-background-color: #274C77; -fx-text-fill: white; -fx-font-size: 14px;");
        assignAdminButton.setStyle("-fx-background-color: #274C77; -fx-text-fill: white; -fx-font-size: 14px;");
        removeMemberButton.setStyle("-fx-background-color: #274C77; -fx-text-fill: white; -fx-font-size: 14px;");
        
        renameButton.setPrefWidth(150);
        addMemberButton.setPrefWidth(150);
        assignAdminButton.setPrefWidth(150);
        removeMemberButton.setPrefWidth(150);

        renameButton.setOnAction(_ -> {
            showRenameGroupDialog(group.getName(), newName -> {
                group.setName(newName); 
                 
            });
        });
        addMemberButton.setOnAction(_ -> {
            showAddMemberDialog(newMembers -> {
                group.getMembers().addAll(newMembers); // Thêm thành viên mới
                // Cập nhật giao diện
            });
        });
        removeMemberButton.setOnAction(_ -> {
            showRemoveMemberDialog(group.getMembers(), removedMembers -> {
                group.getMembers().removeAll(removedMembers); // Xóa thành viên
            // Cập nhật giao diện
            });
        });
        assignAdminButton.setOnAction(_ -> {
            showAssignAdminDialog(group.getMembers(), newAdmin -> {
                group.setAdmin(newAdmin); // Gán quyền admin mới
              
            });
        });
        
        userInfoContainer.getChildren().addAll(groupName, searchField, adminLabel, memberDropdown, renameButton, addMemberButton, assignAdminButton, removeMemberButton);
    }








    // các tùy chọn vs nhóm nè
    //đổi tên
    private void showRenameGroupDialog(String currentName, Consumer<String> onSave) {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Đổi tên nhóm");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        Label instruction = new Label("Nhập tên mới:");
        TextField nameField = new TextField(currentName);

        HBox buttons = new HBox(10);
        Button saveButton = new Button("Save");
        Button cancelButton = new Button("Cancel");

        saveButton.setOnAction(_ -> {
            onSave.accept(nameField.getText());
            popupStage.close();
        });

        cancelButton.setOnAction(_ -> popupStage.close());

        buttons.getChildren().addAll(saveButton, cancelButton);
        buttons.setAlignment(Pos.CENTER_RIGHT);

        layout.getChildren().addAll(instruction, nameField, buttons);

        Scene scene = new Scene(layout, 300, 150);
        popupStage.setScene(scene);
        popupStage.showAndWait();
    }

    //thêm
    private void showAddMemberDialog(Consumer<List<String>> onSave) {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Thêm thành viên");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        Label instruction = new Label("Nhập tên thành viên:");
        TextField memberField = new TextField();
        ListView<String> newMembers = new ListView<>();
        Button addButton = new Button("Thêm");

        HBox buttons = new HBox(10);
        Button saveButton = new Button("Save");
        Button cancelButton = new Button("Cancel");

        buttons.getChildren().addAll(saveButton, cancelButton);
        buttons.setAlignment(Pos.CENTER_RIGHT);

        layout.getChildren().addAll(instruction, memberField, addButton, newMembers, buttons);

        Scene scene = new Scene(layout, 300, 300);
        popupStage.setScene(scene);
        popupStage.showAndWait();
    }


    // xóa
    private void showRemoveMemberDialog(List<String> currentMembers, Consumer<List<String>> onSave) {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Xóa thành viên");
    
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));
    
        Label instruction = new Label("Chọn thành viên để xóa:");
        ListView<String> membersList = new ListView<>();
        membersList.getItems().addAll(currentMembers);
        membersList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    
        HBox buttons = new HBox(10);
        Button saveButton = new Button("Save");
        Button cancelButton = new Button("Cancel");
    
        buttons.getChildren().addAll(saveButton, cancelButton);
        buttons.setAlignment(Pos.CENTER_RIGHT);
    
        layout.getChildren().addAll(instruction, membersList, buttons);
    
        Scene scene = new Scene(layout, 300, 300);
        popupStage.setScene(scene);
        popupStage.showAndWait();
    }

    //admin
    private void showAssignAdminDialog(List<String> currentMembers, Consumer<String> onSave) {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Gán quyền Admin");
    
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));
    
        Label instruction = new Label("Chọn thành viên để làm admin:");
        ListView<String> membersList = new ListView<>();
        membersList.getItems().addAll(currentMembers);
        membersList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    
        HBox buttons = new HBox(10);
        Button saveButton = new Button("Save");
        Button cancelButton = new Button("Cancel");
    
        buttons.getChildren().addAll(saveButton, cancelButton);
        buttons.setAlignment(Pos.CENTER_RIGHT);
    
        layout.getChildren().addAll(instruction, membersList, buttons);
    
        Scene scene = new Scene(layout, 300, 300);
        popupStage.setScene(scene);
        popupStage.showAndWait();
    }

}
