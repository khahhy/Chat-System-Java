package duck.presentation.userView;

import duck.bus.FriendRequestBUS;
import duck.bus.FriendBUS;
import duck.bus.UserBUS;
import duck.dto.UserDTO;

import duck.dto.FriendRequestDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;

public class FriendPage {
    private final UserBUS userBUS;
    private final FriendBUS friendBUS;
    private final UserDTO user;
    private final ObservableList<UserDTO> filteredUsers;

    public FriendPage(UserDTO user) {
        this.userBUS = new UserBUS();
        this.friendBUS = new FriendBUS();
        this.user = user;
        this.filteredUsers = FXCollections.observableArrayList();
        loadUser();
    }    

    private void loadUser() {
        List<UserDTO> allUsers = userBUS.searchUsers("", "", null);
        List<UserDTO> blockedUsers = userBUS.getBlockedUsersByUserId(user.getUserId()); 
    
        for (UserDTO otherUser : allUsers) {
            if (otherUser.getUserId() != user.getUserId()) { 
                boolean isBlocked = blockedUsers.stream()
                        .anyMatch(blocked -> blocked.getUserId() == otherUser.getUserId());
                if (!isBlocked)
                    filteredUsers.add(otherUser);
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
    
        searchField.textProperty().addListener((_, _, newValue) -> {
            if (newValue.isEmpty()) {
                searchResults.setVisible(false);
                searchResults.setManaged(false);
                backButton.setVisible(false);
                showDefaultMenu(leftMenu);
                return;
            }
            searchResults.setVisible(true);
            searchResults.setManaged(true);
            backButton.setVisible(true);
            hideDefaultMenu(leftMenu);
    
            searchResults.getChildren().clear();
           
            filteredUsers.filtered(item -> item.getUsername().toLowerCase().contains(newValue.toLowerCase()))
                    .forEach(friend -> searchResults.getChildren().add(createSearchResult("Người dùng", friend)));
        });
    
        Button friendListButton = new Button("Danh sách bạn bè");
        friendListButton.setPrefWidth(200);
        friendListButton.setStyle("-fx-font-size: 14px;");
        friendListButton.setOnAction(_ -> root.setCenter(new FriendListView(user).getContent()));
    
        Button groupListButton = new Button("Danh sách nhóm");
        groupListButton.setPrefWidth(200);
        groupListButton.setStyle("-fx-font-size: 14px;");
        groupListButton.setOnAction(_ -> root.setCenter(new GroupListView().getContent()));
    
        Button friendRequestButton = new Button("Lời mời kết bạn");
        friendRequestButton.setPrefWidth(200);
        friendRequestButton.setStyle("-fx-font-size: 14px;");
        friendRequestButton.setOnAction(_ ->root.setCenter(new FriendRequestView(user).getContent()));
    
        Button groupInviteButton = new Button("Lời mời vào nhóm");
        groupInviteButton.setPrefWidth(200);
        groupInviteButton.setStyle("-fx-font-size: 14px;");
        groupInviteButton.setOnAction(_ -> root.setCenter(new GroupRequestView().getContent()));
    
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

        Label contentLabel = new Label();
        Button actionButton = new Button();
        FriendRequestBUS friendRequestBUS = new FriendRequestBUS();

        if ("Người dùng".equals(tag) && content instanceof UserDTO friend) {
            contentLabel.setText(friend.getUsername());
        
            //boolean isBlocked = friend.isBlocked();
            boolean isFriend = friendBUS.isFriend(user.getUserId(), friend.getUserId());
            boolean hasSentRequest = friendRequestBUS.hasSentRequest(user.getUserId(), friend.getUserId());
            boolean hasReceivedRequest = friendRequestBUS.hasReceivedRequest(friend.getUserId(), user.getUserId());
            /*if (isBlocked) {
                actionButton.setText("Hủy block");
                actionButton.setOnAction(_ -> {
                    // Xóa bản ghi block khỏi cơ sở dữ liệu
                    boolean success = friendBUS.deleteFriend(user.getUserId(), friend.getUserId());
                    if (success) {
                        System.out.println("Đã hủy block " + friend.getName());
                        actionButton.setText("Kết bạn"); // Chuyển trạng thái nút
                    } else {
                        System.out.println("Không thể hủy block.");
                    }
                });
            } */
            //else 
            if (isFriend) {
                actionButton.setText("Nhắn tin");
                actionButton.setOnAction(_ -> {
                    System.out.println("Nhắn tin với " + friend.getUsername());
                });
            } else if (hasSentRequest) {
                actionButton.setText("Hủy lời mời kết bạn");
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
                            System.out.println("Đã hủy lời mời kết bạn với " + friend.getUsername());
                        } else {
                            System.out.println("Không thể hủy lời mời kết bạn.");
                        }
                    }
                });
            } 
            else if (hasReceivedRequest) {
                actionButton.setText("Đồng ý kết bạn");
                actionButton.setOnAction(_ -> {
                    List<FriendRequestDTO> sentRequests = friendRequestBUS.getSentRequestsByUserId(friend.getUserId());
                    FriendRequestDTO targetRequest = sentRequests.stream()
                            .filter(request -> request.getReceiverId() == friend.getUserId() && "pending".equals(request.getStatus()))
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
            } else {
                actionButton.setText("Kết bạn");
                actionButton.setOnAction(_ -> {
                    FriendRequestDTO newRequest = new FriendRequestDTO(
                            0, user.getUserId(), friend.getUserId(), "pending", java.time.LocalDateTime.now()
                    );
                    boolean success = friendRequestBUS.sendFriendRequest(newRequest);
                    if (success) {
                        actionButton.setText("Hủy lời mời kết bạn");
                        System.out.println("Đã gửi lời mời kết bạn tới " + friend.getUsername());
                    } else {
                        System.out.println("Không thể gửi lời mời kết bạn.");
                    }
                });
            }
        }
        

        contentLabel.setStyle("-fx-font-size: 12px;");
        info.getChildren().addAll(tagLabel, contentLabel);
        result.setLeft(info);

        HBox options = new HBox(5);
        options.setStyle("-fx-alignment: center-right;");
        actionButton.setStyle("-fx-font-size: 11px;");
        options.getChildren().add(actionButton);

        result.setRight(options);
        return result;
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
