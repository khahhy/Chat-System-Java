package duck.presentation.userView;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.postgresql.translation.messages_bg;

import duck.dto.GroupDTO;
import duck.dto.MessageDTO;
import duck.bus.MessageBUS;
import duck.dao.MessageDAO;
import duck.bus.DeletedMessageBUS;
import duck.bus.FriendBUS;
import duck.bus.GroupBUS;
import duck.bus.UserBUS;
import duck.dto.UserDTO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;   
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MessagePage {
    private final UserDTO user;
    private UserDTO opponent;
    private GroupDTO mainGroup;
    private UserBUS userBUS;
    private MessageBUS messageBUS;
    private GroupBUS groupBUS;
    private DeletedMessageBUS deletedMessageBUS;
    BorderPane root;
    VBox chatList;
    VBox chatContent;
    VBox userInfo;

    private final ObservableList<Object> chatData;

    
    public MessagePage(UserDTO user, UserDTO opponent, GroupDTO gr) {
        this.user = user;
        this.opponent = opponent;
        this.mainGroup = gr;
        this.userBUS = new UserBUS();
        this.messageBUS = new MessageBUS();
        this.groupBUS = new GroupBUS();
        this.deletedMessageBUS = new DeletedMessageBUS();
        chatData = FXCollections.observableArrayList();
        loadChatData();

        root = new BorderPane();
        this.chatContent = new VBox();
        this.chatList = new VBox();   
        this.userInfo = new VBox();
    }    

    public BorderPane getContent() {
        root = new BorderPane();

        this.chatContent = createChatContent();  
        this.chatList = createChatList();
        
        if (opponent == null && mainGroup != null) 
            this.userInfo = createGroupInfo();
            
        if (mainGroup == null && opponent != null) 
            this.userInfo = createUserInfo(); 

        root.setLeft(chatList);
        root.setCenter(chatContent);
        root.setRight(userInfo);
        return root;
    }
    
    private void loadChatData() {
        MessageBUS messageBUS = new MessageBUS(); 
        List<UserDTO> friends = messageBUS.getFriendsFromMessage(user.getUserId()); 
        List<GroupDTO> groups = messageBUS.getGroupsFromMessage(user.getUserId());
        
        List<MessageDTO> chatParticipants = new ArrayList<>();
        for (UserDTO otherUser : friends) {
            MessageDTO lastMessage = messageBUS.getLastMessagesUsers(user.getUserId(), otherUser.getUserId());
            if (lastMessage != null ) {
                chatParticipants.add(lastMessage);            
            }
        }

        for (GroupDTO group : groups) {
            MessageDTO lastMessage = messageBUS.getLastMessagesGroup(group.getGroupId());
            if (lastMessage != null) {
                chatParticipants.add(lastMessage);
            }
        }

        chatParticipants.sort((cp1, cp2) -> cp2.getTimestamp().compareTo(cp1.getTimestamp()));
        
        for (MessageDTO cp : chatParticipants) {
            if (cp.getGroupId() == null) {
                if (cp.getSenderId() == user.getUserId()) {
                    chatData.add((UserDTO) userBUS.getUserById(cp.getReceiverId()));
                } else {
                    chatData.add((UserDTO) userBUS.getUserById(cp.getSenderId()));
                }
                
            } else if (cp.getReceiverId() == null) {
                chatData.add((GroupDTO) groupBUS.getGroupById(cp.getGroupId()));
            }
        }
    }

    private VBox createChatList() {
        VBox chatBox = new VBox(10);

        TextField searchField = new TextField();
        searchField.setPromptText("Tìm người nhắn tin...");
        searchField.setStyle("-fx-font-size: 14px;");

        ListView<Object> chatList = new ListView<>();
        for (Object item : chatData) {
            if (item instanceof UserDTO) {
                chatList.getItems().add((UserDTO) item);
            } else if (item instanceof GroupDTO) {
                chatList.getItems().add((GroupDTO) item);
            }
        }
        chatList.setCellFactory(_ -> new ListCell<>() {
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
            if (newValue != null) {
                if (newValue instanceof UserDTO otherUser) {
                    opponent = otherUser;
                    mainGroup = null;
                    userInfo = createUserInfo();
                }
                else if (newValue instanceof GroupDTO group) {
                    opponent = null;
                    mainGroup = group;
                    userInfo = createGroupInfo();
                }
                chatContent = createChatContent();
                root.setCenter(chatContent);
                root.setRight(userInfo);
                chatList.refresh();
            }
            
        });

        VBox.setVgrow(chatList, Priority.ALWAYS); 
        chatBox.getChildren().addAll(searchField, chatList);
        chatBox.setPrefWidth(250);
        chatBox.setStyle("-fx-padding: 10;");

        return chatBox;
    }

    private VBox createChatContent() {
        VBox chatContentBox = new VBox(10);
        chatContentBox.setStyle("-fx-padding: 10;");
        
        ScrollPane messagePane = new ScrollPane();
        VBox messageContainer = new VBox(10); 
        messageContainer.setStyle("-fx-padding: 10;");
        
        messagePane.setContent(messageContainer);
        messagePane.setFitToWidth(true);
        VBox.setVgrow(messagePane, Priority.ALWAYS);

        if (opponent != null) {
            List<MessageDTO> oldChat = messageBUS.getMessagesBetweenUsers(user.getUserId(), opponent.getUserId());
            for (MessageDTO chat : oldChat) {
                if (!deletedMessageBUS.checkDeletedMessage(chat.getMessageId(),user.getUserId())) {
                    addMessage(messageContainer, chat);
                }
            }   
        }

        if (mainGroup != null) {
            List<MessageDTO> oldChat = messageBUS.getMessagesInGroup(mainGroup.getGroupId());
            for (MessageDTO chat : oldChat) 
                addMessage(messageContainer, chat);
        }

        TextField inputField = new TextField();
        inputField.setPromptText("Nhập tin nhắn...");
        inputField.setStyle("-fx-font-size: 14px;");
        inputField.setOnAction(_ -> {
            String message = inputField.getText();
            if (!message.isEmpty()) {
                if (opponent != null) {
                    MessageDTO newMessage = new MessageDTO(0, user.getUserId(), opponent.getUserId(), null, message, LocalDateTime.now(), false);
                    if (messageBUS.addMessage(newMessage)) {
                        addMessage(messageContainer, newMessage);
                        inputField.clear();
                        messagePane.setVvalue(1.0); 
                    }
                }

                if (mainGroup != null) {
                    MessageDTO newMessage = new MessageDTO(0, user.getUserId(), null, mainGroup.getGroupId(), message, LocalDateTime.now(), false);
                    if (messageBUS.addMessage(newMessage)) {
                        addMessage(messageContainer, newMessage);
                        inputField.clear();
                        messagePane.setVvalue(1.0); 
                    }
                }
            }
        });
        inputField.prefWidthProperty().bind(chatContentBox.widthProperty());
        HBox inputArea = new HBox(10, inputField);
        chatContentBox.getChildren().addAll(messagePane, inputArea);
        return chatContentBox;
    }

    private void addMessage(VBox container, MessageDTO message) {
        VBox messageWrapper = new VBox(5); 
        messageWrapper.setStyle("-fx-padding: 5;");

        if (message.getSenderId() != user.getUserId()) {
            Label usernameLabel = new Label(userBUS.getUserById(message.getSenderId()).getUsername());
            usernameLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #555555;");
            messageWrapper.getChildren().add(usernameLabel);
        }

        HBox messageBox = new HBox();
        Label messageLabel = new Label(message.getContent());
       
        messageLabel.setStyle("-fx-padding: 10; -fx-background-color: "
            + ((message.getSenderId() == user.getUserId()) ? "#DCF8C6" : "#FFFFFF") + "; -fx-border-radius: 10; "
            + "-fx-background-radius: 10; -fx-font-size: 14px;");
        
        MenuButton optionsMenu = new MenuButton();
        MenuItem deleteItem = new MenuItem("Xóa tin nhắn");
        deleteItem.setOnAction(_ -> {
            if (deletedMessageBUS.addDeletedMessage(message.getMessageId(), user.getUserId())) {
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

        messageWrapper.getChildren().add(messageBox);
        container.getChildren().add(messageWrapper);
    }

    private VBox createUserInfo() {
        VBox userInfoContainer = new VBox(10);
        userInfoContainer.setStyle("-fx-padding: 10; -fx-background-color: #F0F0F0;");
        ImageView avatar = new ImageView(new Image("/user.png"));
        avatar.setFitWidth(80);
        avatar.setFitHeight(80);
       
        Label userName = new Label(opponent.getUsername());
        userName.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        TextField searchField = new TextField();
        searchField.setPromptText("Tìm tin nhắn...");
        searchField.setStyle("-fx-font-size: 14px;");
       
        Button spamButton = new Button("Báo Spam");
        spamButton.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-font-size: 14px;");
        spamButton.setOnAction(_ -> System.out.println("Báo cáo spam"));
      
        Button deleteHistoryButton = new Button("Xóa lịch sử");
        deleteHistoryButton.setStyle("-fx-background-color: #FF4500; -fx-text-fill: white; -fx-font-size: 14px;");
        deleteHistoryButton.setOnAction(_ -> System.out.println("Lịch sử đã bị xóa"));
        userInfoContainer.getChildren().addAll(avatar, userName, searchField, spamButton, deleteHistoryButton);
        return userInfoContainer;
    }



 
    private VBox createGroupInfo() {
        VBox groupInfoContainer = new VBox(10);
        groupInfoContainer.setStyle("-fx-padding: 10; -fx-background-color: #F0F0F0;");
        ImageView avatar = new ImageView(new Image("/user.png"));
        avatar.setFitWidth(80);
        avatar.setFitHeight(80);
        
        TextField searchField = new TextField();
        searchField.setPromptText("Tìm kiếm tin nhắn...");
        searchField.setStyle("-fx-font-size: 14px;");
        searchField.textProperty().addListener((_, _, _) -> {
        
        });

        Label groupName = new Label(mainGroup.getGroupName());
        groupName.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label adminLabel = new Label("Admin: " );
        adminLabel.setStyle("-fx-font-size: 14px;");        

        ComboBox<String> memberDropdown = new ComboBox<>();
        memberDropdown.getItems().addAll("group.getMembers()");
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

        /*renameButton.setOnAction(_ -> {
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
        }); */
        
        groupInfoContainer.getChildren().addAll(searchField, groupName, adminLabel, memberDropdown, renameButton, addMemberButton, assignAdminButton, removeMemberButton);

        return groupInfoContainer;
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
