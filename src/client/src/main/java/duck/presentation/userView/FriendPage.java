package duck.presentation.userView;

import duck.bus.FriendRequestBUS;
import duck.bus.UserBUS;
import duck.dto.UserDTO;
import duck.dto.FriendDTO;
import duck.dto.FriendRequestDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;

public class FriendPage {

    private final UserDTO user;
    private final ObservableList<Friend> friends = FXCollections.observableArrayList();

    public FriendPage(UserDTO user) {
        this.user = user;
        loadFriendsFromDatabase();
    }

    // Lớp đại diện cho bạn bè trong danh sách
    public static class Friend {
        private final String name;
        private final boolean isFriend;
        private final boolean isBlocked; // Thêm thuộc tính trạng thái block
        private final int userId;

        public Friend(int userId, String name, boolean isFriend, boolean isBlocked) {
            this.userId = userId;
            this.name = name;
            this.isFriend = isFriend;
            this.isBlocked = isBlocked;
        }

        public String getName() {
            return name;
        }

        public boolean isFriend() {
            return isFriend;
        }

        public boolean isBlocked() {
            return isBlocked;
        }

        public int getUserId() {
            return userId;
        }
    }

    // Phương thức để tải bạn bè từ database
    private void loadFriendsFromDatabase() {
        List<UserDTO> allUsers = UserBUS.getAllUsers(); // Lấy danh sách tất cả người dùng
        List<FriendDTO> userFriends = UserBUS.getFriendsByUserId(user.getUserId()); // Lấy danh sách bạn bè
        List<UserDTO> blockedUsers = UserBUS.getBlockedUsersByUserId(user.getUserId()); // Lấy danh sách bị chặn
    
        for (UserDTO otherUser : allUsers) {
            if (otherUser.getUserId() != user.getUserId()) { // Bỏ qua chính người dùng
                boolean isFriend = userFriends.stream()
                        .anyMatch(friend -> friend.getFriendId() == otherUser.getUserId());
                boolean isBlocked = blockedUsers.stream()
                        .anyMatch(blocked -> blocked.getUserId() == otherUser.getUserId());
    
                friends.add(new Friend(otherUser.getUserId(), otherUser.getFullName(), isFriend, isBlocked));
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
    
            friends.filtered(item -> item.getName().toLowerCase().contains(newValue.toLowerCase()))
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
        UserBUS userBUS = new UserBUS();

        if ("Người dùng".equals(tag) && content instanceof Friend friend) {
            contentLabel.setText(friend.getName());
        
            boolean isBlocked = friend.isBlocked();
            boolean isFriend = friend.isFriend();
            boolean hasSentRequest = friendRequestBUS.hasSentRequest(user.getUserId(), friend.getUserId());
        
            if (isBlocked) {
                actionButton.setText("Hủy block");
                actionButton.setOnAction(event -> {
                    // Xóa bản ghi block khỏi cơ sở dữ liệu
                    boolean success = userBUS.removeFriend(user.getUserId(), friend.getUserId());
                    if (success) {
                        System.out.println("Đã hủy block " + friend.getName());
                        actionButton.setText("Kết bạn"); // Chuyển trạng thái nút
                    } else {
                        System.out.println("Không thể hủy block.");
                    }
                });
            } else if (isFriend) {
                actionButton.setText("Nhắn tin");
                actionButton.setOnAction(event -> {
                    System.out.println("Nhắn tin với " + friend.getName());
                });
            } else if (hasSentRequest) {
                actionButton.setText("Hủy lời mời kết bạn");
                actionButton.setOnAction(event -> {
                    List<FriendRequestDTO> sentRequests = friendRequestBUS.getSentRequestsByUserId(user.getUserId());
                    FriendRequestDTO targetRequest = sentRequests.stream()
                            .filter(request -> request.getReceiverId() == friend.getUserId() && "pending".equals(request.getStatus()))
                            .findFirst()
                            .orElse(null);
        
                    if (targetRequest != null) {
                        boolean success = friendRequestBUS.deleteFriendRequest(targetRequest.getRequestId());
                        if (success) {
                            actionButton.setText("Kết bạn");
                            System.out.println("Đã hủy lời mời kết bạn với " + friend.getName());
                        } else {
                            System.out.println("Không thể hủy lời mời kết bạn.");
                        }
                    }
                });
            } else {
                actionButton.setText("Kết bạn");
                actionButton.setOnAction(event -> {
                    FriendRequestDTO newRequest = new FriendRequestDTO(
                            0, user.getUserId(), friend.getUserId(), "pending", java.time.LocalDateTime.now()
                    );
                    boolean success = friendRequestBUS.sendFriendRequest(newRequest);
                    if (success) {
                        actionButton.setText("Hủy lời mời kết bạn");
                        System.out.println("Đã gửi lời mời kết bạn tới " + friend.getName());
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
