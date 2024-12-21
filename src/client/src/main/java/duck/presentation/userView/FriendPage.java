package duck.presentation.userView;

import duck.bus.FriendRequestBUS;
import duck.bus.DeletedMessageBUS;
import duck.bus.FriendBUS;
import duck.bus.UserBUS;
import duck.dto.UserDTO;
import duck.bus.GroupBUS;
import duck.dto.GroupDTO;
import duck.dto.MessageDTO;
import duck.bus.MessageBUS;

import duck.dto.FriendRequestDTO;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import javafx.util.Duration;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class FriendPage {
    private final UserBUS userBUS;
    private final FriendBUS friendBUS;
    private final GroupBUS groupBUS;
    private MessageBUS messageBUS;
    private final UserDTO user;

    private final FriendRequestBUS friendRequestBUS;

    private final ObservableList<UserDTO> users_list;
    private final FilteredList<UserDTO> filteredList;
    private final FilteredList<GroupDTO> filteredGroupList;
    private final FilteredList<MessageDTO> filteredChatList;
    private final ObservableList<GroupDTO> groups_list;
    private final ObservableList<MessageDTO> chat_list;
    private final BorderPane parent;
    public FriendPage(UserDTO user, BorderPane root) {
        parent = root;

        this.userBUS = new UserBUS();
        this.friendBUS = new FriendBUS();
        this.groupBUS = new GroupBUS();
        this.messageBUS = new MessageBUS();
        friendRequestBUS = new FriendRequestBUS();
        this.user = user;
        this.users_list = FXCollections.observableArrayList();
        loadUser();
        this.filteredList = new FilteredList<>(users_list, _ -> true);

        List<GroupDTO> groupDTOlist = groupBUS.getAllGroupsByUserId(user.getUserId());
        groups_list = FXCollections.observableArrayList(groupDTOlist);
        this.filteredGroupList = new FilteredList<>(groups_list, _ -> true);
        List<MessageDTO> messageDTOlist = messageBUS.getAllMessagesByUser(user.getUserId());
        chat_list = FXCollections.observableArrayList(messageDTOlist);
        this.filteredChatList = new FilteredList<>(chat_list, _ -> true);
    }    

    private void loadUser() {
    // Lấy danh sách tất cả người dùng
    List<UserDTO> allUsers = userBUS.searchUsers("", "", null);
    
    // Lấy danh sách người dùng bị người dùng hiện tại chặn
    List<UserDTO> blockedUsers = userBUS.getBlockedUsersByUserId(user.getUserId());

    // Lấy danh sách người dùng đã chặn người dùng hiện tại
    List<UserDTO> blockedByUsers = userBUS.getUsersWhoBlockedUser(user.getUserId());

    // Tạo tập hợp chứa tất cả các ID người dùng bị loại bỏ
    Set<Integer> excludedUserIds = new HashSet<>();
    blockedUsers.forEach(blocked -> excludedUserIds.add(blocked.getUserId()));
    blockedByUsers.forEach(blockedBy -> excludedUserIds.add(blockedBy.getUserId()));

    // Lọc danh sách
    for (UserDTO otherUser : allUsers) {
        // Nếu không phải người dùng hiện tại và không nằm trong danh sách bị loại bỏ
        if (otherUser.getUserId() != user.getUserId()
                && !excludedUserIds.contains(otherUser.getUserId())) {
            users_list.add(otherUser);
        }
    }
}


    public BorderPane getContent() {
        BorderPane root = new BorderPane();

        VBox leftMenu = createLeftMenu(root, user);
        root.setLeft(leftMenu);

        VBox defaultContent = new VBox();
        root.setCenter(defaultContent);

        return root;
    }

    private VBox createLeftMenu(BorderPane root, UserDTO user) {
        VBox leftMenu = new VBox(10);
        leftMenu.setStyle("-fx-background-color: #E7ECEF; -fx-padding: 10;");
        leftMenu.setPrefWidth(320);
    
        HBox searchBox = new HBox(10);
    
        TextField searchField = new TextField();
        searchField.setPromptText("Tìm kiếm...");
        searchField.setStyle("-fx-font-size: 14px;");
        searchField.setPrefWidth(200);
    
        Button backButton = new Button("Back");
        backButton.setStyle("-fx-font-size: 12px;");
        backButton.setVisible(false); // chưa tìm thì không hiển thị nút "Back"
        backButton.setOnAction(_ -> {
            resetLeftMenu(leftMenu);
            searchField.clear();
        });
    
        searchBox.getChildren().addAll(searchField, backButton);
    
        VBox searchResults = new VBox(10);
        searchResults.setStyle("-fx-padding: 10;");
        searchResults.setVisible(false);
        searchResults.setManaged(false);
        
        PauseTransition pause = new PauseTransition(Duration.millis(300));
        searchField.textProperty().addListener((_, _, newValue) -> {
            pause.setOnFinished(_ -> {
            if (newValue.isEmpty()) {
                Platform.runLater(() -> {
                    searchResults.setVisible(false);
                    searchResults.setManaged(false);
                    backButton.setVisible(false);
                    showDefaultMenu(leftMenu);
                });
                return;
            } 
            searchResults.setVisible(true);
            searchResults.setManaged(true);
            backButton.setVisible(true);
            hideDefaultMenu(leftMenu);
    
            searchResults.getChildren().clear();
           

            // Hiển thị kết quả tìm kiếm
            Platform.runLater(() -> {
                filteredList.filtered(item -> item.getUsername().toLowerCase().contains(newValue.toLowerCase())).stream().limit(10).toList()
                    .forEach(friend -> searchResults.getChildren().add(createSearchResult("Người dùng", friend)));
                
                filteredGroupList.filtered(item -> item.getGroupName().toLowerCase().contains(newValue.toLowerCase())).stream().limit(10).toList()
                    .forEach(group -> searchResults.getChildren().add(createSearchResult("Nhóm", group)));

                filteredChatList.filtered(item -> item.getContent().toLowerCase().contains(newValue.toLowerCase())).stream().limit(10).toList()
                    .forEach(message -> searchResults.getChildren().add(createSearchResult("Tin nhắn", message)));
            });
        });

        // Khởi động lại debounce mỗi khi có thay đổi trong `searchField`
        pause.playFromStart();
        });
    
        Button friendListButton = new Button("Danh sách bạn bè");
        friendListButton.setPrefWidth(200);
        friendListButton.setStyle("-fx-font-size: 14px;");
        friendListButton.setOnAction(_ -> root.setCenter(new FriendListView(user, parent).getContent()));
    
        Button groupListButton = new Button("Danh sách nhóm");
        groupListButton.setPrefWidth(200);
        groupListButton.setStyle("-fx-font-size: 14px;");
        groupListButton.setOnAction(_ -> root.setCenter(new GroupListView(user, parent).getContent()));
    
        Button friendRequestButton = new Button("Lời mời kết bạn");
        friendRequestButton.setPrefWidth(200);
        friendRequestButton.setStyle("-fx-font-size: 14px;");
        friendRequestButton.setOnAction(_ ->root.setCenter(new FriendRequestView(user).getContent()));
    
        Button groupInviteButton = new Button("Lời mời vào nhóm");
        groupInviteButton.setPrefWidth(200);
        groupInviteButton.setStyle("-fx-font-size: 14px;");
        groupInviteButton.setOnAction(_ -> root.setCenter(new GroupRequestView(user).getContent()));
    
        leftMenu.getChildren().addAll(searchBox, searchResults, friendListButton, groupListButton, friendRequestButton, groupInviteButton);
        return leftMenu;
    }
    
    
    
    private BorderPane createSearchResult(String tag, Object content) {
        BorderPane result = new BorderPane();
        result.setStyle("-fx-padding: 5; -fx-border-color: #CCCCCC; -fx-border-width: 1; -fx-background-color: #F9F9F9;");

        HBox info = new HBox(5);
        info.setStyle("-fx-alignment: center-left;");

        Label tagLabel = new Label("[" + tag + "]");
        tagLabel.setStyle("-fx-font-size: 10px; -fx-font-weight: bold;");

        Label contentLabel = new Label(content.toString());

        contentLabel.setStyle("-fx-font-size: 12px;");
        info.getChildren().addAll(tagLabel, contentLabel);
        result.setLeft(info);
    
        HBox options = new HBox(5);
        options.setStyle("-fx-alignment: center-right;");

        

        if ("Người dùng".equals(tag) && content instanceof UserDTO friend) {
            contentLabel.setText(friend.getUsername());
            boolean isFriend = friendBUS.isFriend(user.getUserId(), friend.getUserId());
            boolean hasSentRequest = friendRequestBUS.hasSentRequest(user.getUserId(), friend.getUserId());
            boolean hasReceivedRequest = friendRequestBUS.hasReceivedRequest(friend.getUserId(), user.getUserId());
            
            MenuButton optionsButton = new MenuButton();
            MenuItem chatOption = new MenuItem("Nhắn tin");
            chatOption.setOnAction(_ -> {
                parent.setCenter(new MessagePage(user, friend, null).getContent());
            });
            if (isFriend) {
                MenuItem groupOption = new MenuItem("Tạo nhóm");

                groupOption.setOnAction(_ -> {
                    CreateGroupPopup createGroupPopup = new CreateGroupPopup(user);
                    createGroupPopup.show(friend.getUsername());
                });

                optionsButton.getItems().addAll(chatOption, groupOption);

            } else if (hasSentRequest) {
                MenuItem actionButton = new MenuItem("Hủy lời mời kết bạn");
                
                actionButton.setOnAction(_ -> {
                    List<FriendRequestDTO> sentRequests = friendRequestBUS.getSentRequestsByUserId(user.getUserId());
                    FriendRequestDTO targetRequest = sentRequests.stream()
                            .filter(request -> request.getReceiverId() == friend.getUserId() && "pending".equals(request.getStatus()))
                            .findFirst()
                            .orElse(null);
        
                    if (targetRequest != null) {
                        boolean success = friendRequestBUS.deleteFriendRequest(targetRequest.getRequestId());
                        if (success) {
                            actionButton.setText("Kết bạn");
                        } 
                    }
                });

                optionsButton.getItems().addAll(chatOption, actionButton);
            } 
            else if (hasReceivedRequest) {
                MenuItem actionButton = new MenuItem("Đồng ý kết bạn");
                
                actionButton.setOnAction(_ -> {
                    List<FriendRequestDTO> sentRequests = friendRequestBUS.getSentRequestsByUserId(friend.getUserId());
                    FriendRequestDTO targetRequest = sentRequests.stream()
                            .filter(request -> request.getSenderId() == friend.getUserId() && "pending".equals(request.getStatus()))
                            .findFirst()
                            .orElse(null);
        
                    if (targetRequest != null) {
                        boolean success = friendRequestBUS.acceptFriendRequest(targetRequest.getRequestId(), friend.getUserId(), user.getUserId());
                        if (success) {
                            actionButton.setText("");
                            System.out.println(friend.getUsername() + "đã kết bạn với " + user.getUsername());
                        } else {
                            System.out.println("Không thể đồng ý kết bạn.");
                        }
                    }

                });
                optionsButton.getItems().addAll(chatOption, actionButton);

            } else if (!isFriend && !hasSentRequest && !hasReceivedRequest) {
                MenuItem actionButton = new MenuItem("Kết bạn");
                
                actionButton.setOnAction(_ -> {
                    boolean success = sendFrReq(friend);
                    if (success) {
                        actionButton.setText("Hủy lời mời kết bạn");     
                    }
                });

                optionsButton.getItems().addAll(chatOption, actionButton);

            }

            optionsButton.setStyle("-fx-font-size: 12px;");
            options.getChildren().add(optionsButton);
        }

        else if ("Nhóm".equals(tag) && content instanceof GroupDTO group) {
            MenuButton optionsButton = new MenuButton();
            MenuItem chatOption = new MenuItem("Nhắn tin");
            MenuItem viewInfo = new MenuItem("Xem thông tin");
            MenuItem leaveGroup = new MenuItem("Rời nhóm");
            
            chatOption.setOnAction(_ -> {
                parent.setCenter(new MessagePage(user, null, group).getContent());
            });

            viewInfo.setOnAction(_ -> {
                GroupListView groupInfoView = new GroupListView(user, parent);
                groupInfoView.showGroupInfoPopup(group);
            });

            leaveGroup.setOnAction(_ -> {
                GroupListView groupInfoView = new GroupListView(user, parent);
                groupInfoView.showConfirmLeavePopup(group);
            });

            optionsButton.getItems().addAll(chatOption, viewInfo, leaveGroup);
            optionsButton.setStyle("-fx-font-size: 12px;");
            options.getChildren().addAll(optionsButton);
        
        }
        else if ("Tin nhắn".equals(tag) && content instanceof MessageDTO message) {
            MenuButton optionsButton = new MenuButton();
            MenuItem viewChat = new MenuItem("Xem tin nhắn");
            MenuItem removeChat = new MenuItem("Xóa tin nhắn");
            
            viewChat.setOnAction(_ -> {
                MessagePage messagePage;
                
                if (message.getGroupId() != null) {
                    messagePage = new MessagePage(user, null, groupBUS.getGroupById(message.getGroupId()));
                } else if (message.getReceiverId() != null) {
                    if (message.getReceiverId() == user.getUserId()) {
                        messagePage = new MessagePage(user, userBUS.getUserById(message.getSenderId()), null);
                    } else {
                        messagePage = new MessagePage(user, userBUS.getUserById(message.getReceiverId()), null);
                    }
                } else {
                    return;
                }
            
                parent.setCenter(messagePage.getContent());
                Platform.runLater(() -> messagePage.scrollToMessage(message)); 
            });
            
            removeChat.setOnAction(_ -> {
                DeletedMessageBUS delBus = new DeletedMessageBUS();
                delBus.addDeletedMessage(message.getMessageId(), user.getUserId());
                
            });

            optionsButton.getItems().addAll(viewChat, removeChat);
            optionsButton.setStyle("-fx-font-size: 12px;");
            options.getChildren().addAll(optionsButton);
        
        }

        result.setRight(options);
        return result;
    }
    
    
    private boolean sendFrReq(UserDTO friend) {
        FriendRequestDTO newRequest = new FriendRequestDTO(
                0, user.getUserId(), friend.getUserId(), "pending", java.time.LocalDateTime.now()
        );
        return friendRequestBUS.sendFriendRequest(newRequest);
    }

    private void resetLeftMenu(VBox leftMenu) {
        leftMenu.getChildren().stream()
                .filter(node -> node instanceof VBox && ((VBox) node).isVisible())
                .forEach(node -> {
                    node.setVisible(false);
                    node.setManaged(false);
                });
        showDefaultMenu(leftMenu);
    }

    private void hideDefaultMenu(VBox leftMenu) {
        leftMenu.getChildren().stream()
                .filter(node -> node instanceof Button)
                .forEach(node -> node.setVisible(false));
    }

    private void showDefaultMenu(VBox leftMenu) {
        leftMenu.getChildren().stream()
                .filter(node -> node instanceof Button)
                .forEach(node -> node.setVisible(true));
    }

    
}
