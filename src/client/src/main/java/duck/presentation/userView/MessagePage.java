package duck.presentation.userView;


import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import org.postgresql.translation.messages_bg;

import duck.dto.GroupDTO;
import duck.dto.MessageDTO;
import duck.bus.MessageBUS;
import duck.bus.SpamReportBUS;
import duck.dao.MessageDAO;
import duck.bus.DeletedMessageBUS;
import duck.bus.FriendBUS;
import duck.bus.GroupBUS;
import duck.bus.GroupMemberBUS;
import duck.bus.UserBUS;
import duck.dto.UserDTO;
import duck.dto.GroupMemberDTO;

import duck.presentation.MessageClient;

import javafx.application.Platform;
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
import javafx.scene.Node;


public class MessagePage {
    private final UserDTO user;
    private UserDTO opponent;
    private GroupDTO mainGroup;
    private UserBUS userBUS;
    private MessageBUS messageBUS;
    private GroupBUS groupBUS;
    private GroupMemberBUS groupMemBUS;
    private DeletedMessageBUS deletedMessageBUS;
    BorderPane root;

    
    VBox chatList;
    VBox chatContent;
    VBox userInfo;
    VBox messageContainer;

    private final ObservableList<Object> chatData;

    private MessageClient messageClient;

    public MessagePage(UserDTO user, UserDTO opponent, GroupDTO gr) {
        this.user = user;
        this.opponent = opponent;
        this.mainGroup = gr;

        try {
            this.messageClient = new MessageClient("localhost", 12345, user.getUserId(), this::handleIncomingMessage);
        } catch (IOException e) {
            e.printStackTrace();
 
        }

        this.groupMemBUS = new GroupMemberBUS();
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
        this.messageContainer = new VBox(10);

      
    }    



    private void handleIncomingMessage(String message) {
        Platform.runLater(() -> {
            MessageDTO newMessage = deserializeMessage(message); 
            if (newMessage != null) {
                addMessage(messageContainer, newMessage); 
               
            }
        });
    }

    private String serializeMessage(MessageDTO message) {
        return message.getSenderId() + "|" + message.getReceiverId() + "|" +
               message.getGroupId() + "|" + message.getContent() + "|" +
               message.getTimestamp();
    }

    private MessageDTO deserializeMessage(String message) {
        String[] parts = message.split("\\|");
        if (parts.length < 5) return null;
    
        int senderId = Integer.parseInt(parts[0]);
        Integer receiverId = parts[1].equals("null") ? null : Integer.parseInt(parts[1]);
        Integer groupId = parts[2].equals("null") ? null : Integer.parseInt(parts[2]);
        String content = parts[3];
        LocalDateTime timestamp = LocalDateTime.parse(parts[4]);
    
        return new MessageDTO(0, senderId, receiverId, groupId, content, timestamp, false);
    }
    

    private void sendMessageToServer(MessageDTO newMessage) {
        if (opponent != null) {
            messageClient.sendMessage(opponent.getUserId(), serializeMessage(newMessage));
                
        } else if (mainGroup != null) {
            List<GroupMemberDTO> listmem = groupMemBUS.getMembersByGroupId(mainGroup.getGroupId());
            for (GroupMemberDTO mem : listmem) {
                if (mem.getUserId() != user.getUserId()) 
                    messageClient.sendMessage(mem.getUserId(), serializeMessage(newMessage));
            }
            
        }
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
        messageContainer = new VBox(10); 
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

        Platform.runLater(() -> messagePane.setVvalue(1.0));

        TextField inputField = new TextField();
        inputField.setPromptText("Nhập tin nhắn...");
        inputField.setStyle("-fx-font-size: 14px;");
        inputField.setOnAction(_ -> {
            String message = inputField.getText();
            if (!message.isEmpty()) {
                if (opponent != null) {
                    MessageDTO newMessage = new MessageDTO(0, user.getUserId(), opponent.getUserId(), null, message, LocalDateTime.now(), false);
                    if (messageBUS.addMessage(newMessage)) {
                        sendMessageToServer(newMessage);
                        addMessage(messageContainer, newMessage);
                        inputField.clear();
                        scrollToBottom(messagePane);
                    }
                }

                if (mainGroup != null) {
                    MessageDTO newMessage = new MessageDTO(0, user.getUserId(), null, mainGroup.getGroupId(), message, LocalDateTime.now(), false);
                    if (messageBUS.addMessage(newMessage)) {
                        sendMessageToServer(newMessage);
                        addMessage(messageContainer, newMessage);
                        inputField.clear();
                        scrollToBottom(messagePane);
                    }
                }
            }
        });
        inputField.prefWidthProperty().bind(chatContentBox.widthProperty());
        HBox inputArea = new HBox(10, inputField);
        chatContentBox.getChildren().addAll(messagePane, inputArea);
        return chatContentBox;
    }

    private void scrollToBottom(ScrollPane scrollPane) {
        Platform.runLater(() -> scrollPane.setVvalue(1.0)); 
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
       
        Button spamButton = new Button("Báo Spam");
        spamButton.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-font-size: 14px;");
        spamButton.setOnAction(_ -> {
            SpamReportBUS spamReportBUS = new SpamReportBUS();
            boolean success = spamReportBUS.reportUser(user.getUserId(), opponent.getUserId(), "");
        
            Alert alert = new Alert(success ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
            alert.setTitle("Báo cáo Spam");
            alert.setHeaderText(success ? "Thành công!" : "Thất bại!");
            alert.setContentText(success ? "Báo cáo đã được gửi thành công." : "Có lỗi xảy ra khi gửi báo cáo.");
            alert.showAndWait();
        
        });
      
        Button deleteHistoryButton = new Button("Xóa lịch sử");
        deleteHistoryButton.setStyle("-fx-background-color: #FF4500; -fx-text-fill: white; -fx-font-size: 14px;");
        deleteHistoryButton.setOnAction(_ -> {
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Xác nhận xóa lịch sử");
            confirmAlert.setHeaderText("Bạn có chắc chắn muốn xóa toàn bộ lịch sử tin nhắn?");
            confirmAlert.setContentText("Hành động này không thể hoàn tác.");

            Optional<ButtonType> result = confirmAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                if (opponent != null) {
                    List<MessageDTO> allChat = messageBUS.getMessagesBetweenUsers(user.getUserId(), opponent.getUserId());
                    for (MessageDTO chat : allChat) {
                        deletedMessageBUS.addDeletedMessage(chat.getMessageId(), user.getUserId());
                    }
                    System.out.println("Lịch sử tin nhắn với " + opponent.getUsername() + " đã được xóa.");
                    root.setCenter(createChatContent());
                    root.setRight(createUserInfo());
                }
                // if (mainGroup != null) {
                //     List<MessageDTO> allChat = messageBUS.getMessagesInGroup(mainGroup.getGroupId());
                //     for (MessageDTO chat : allChat) {
                //         deletedMessageBUS.addDeletedMessage(chat.getMessageId(), user.getUserId());
                //     }
                //     System.out.println("Lịch sử tin nhắn trong nhóm " + mainGroup.getGroupName() + " đã được xóa.");
                // }
            } else {
                System.out.println("Hủy xóa lịch sử.");
            }
        });

        userInfoContainer.getChildren().addAll(avatar, userName, spamButton, deleteHistoryButton);
        return userInfoContainer;
    }



    private VBox createGroupInfo() {
        GroupListView groupAction = new GroupListView(user, null);
        VBox groupInfoContainer = new VBox(10);
        groupInfoContainer.setStyle("-fx-padding: 10; -fx-background-color: #F0F0F0;");
        ImageView avatar = new ImageView(new Image("/user.png"));
        avatar.setFitWidth(80);
        avatar.setFitHeight(80);

        Label groupName = new Label(mainGroup.getGroupName());
        groupName.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        Button infoButton = new Button("Thông tin nhóm");
        Button renameButton = new Button("Đổi tên nhóm");
        Button addMemberButton = new Button("Thêm thành viên");
        Button assignAdminButton = new Button("Gán quyền admin");
        Button removeMemberButton = new Button("Xóa thành viên");
        
        infoButton.setStyle("-fx-background-color: #274C77; -fx-text-fill: white; -fx-font-size: 14px;");
        renameButton.setStyle("-fx-background-color: #274C77; -fx-text-fill: white; -fx-font-size: 14px;");
        addMemberButton.setStyle("-fx-background-color: #274C77; -fx-text-fill: white; -fx-font-size: 14px;");
        assignAdminButton.setStyle("-fx-background-color: #274C77; -fx-text-fill: white; -fx-font-size: 14px;");
        removeMemberButton.setStyle("-fx-background-color: #274C77; -fx-text-fill: white; -fx-font-size: 14px;");
        
        infoButton.setPrefWidth(150);
        renameButton.setPrefWidth(150);
        addMemberButton.setPrefWidth(150);
        assignAdminButton.setPrefWidth(150);
        removeMemberButton.setPrefWidth(150);

        infoButton.setOnAction(_ -> {
            groupAction.showGroupInfoPopup(mainGroup);
        });

        renameButton.setOnAction(_ -> {
            groupAction.updateNameGroup(mainGroup);
        });
        addMemberButton.setOnAction(_ -> {
            groupAction.addMember(mainGroup);
        });
        removeMemberButton.setOnAction(_ -> {
            groupAction.removeMember(mainGroup);
        });
        assignAdminButton.setOnAction(_ -> {
            groupAction.updateAdmin(mainGroup);
        }); 

        Button deleteHistoryButton = new Button("Xóa lịch sử");
        deleteHistoryButton.setStyle("-fx-background-color: #FF4500; -fx-text-fill: white; -fx-font-size: 14px;");
        deleteHistoryButton.setOnAction(_ -> {
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Xác nhận xóa lịch sử");
            confirmAlert.setHeaderText("Bạn có chắc chắn muốn xóa toàn bộ lịch sử tin nhắn?");
            confirmAlert.setContentText("Hành động này không thể hoàn tác.");

            Optional<ButtonType> result = confirmAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                if (mainGroup != null) {
                    List<MessageDTO> allChat = messageBUS.getMessagesInGroup(mainGroup.getGroupId());
                    for (MessageDTO chat : allChat) {
                        deletedMessageBUS.addDeletedMessage(chat.getMessageId(), user.getUserId());
                    }
                    System.out.println("Lịch sử tin nhắn trong nhóm " + mainGroup.getGroupName() + " đã được xóa.");
                    root.setCenter(createChatContent());
                    root.setRight(createGroupInfo());
                }
            } else {
                System.out.println("Hủy xóa lịch sử.");
            }
        });

        
        groupInfoContainer.getChildren().addAll(groupName, infoButton, renameButton, addMemberButton, assignAdminButton, removeMemberButton, deleteHistoryButton);

        return groupInfoContainer;
    }


}
