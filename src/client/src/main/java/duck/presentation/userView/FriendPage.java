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
    private final ObservableList<FriendRequestDTO> friendRequests = FXCollections.observableArrayList();

    public FriendPage(UserDTO user) {
        this.user = user;
        loadFriendsFromDatabase();
        loadFriendRequestsFromDatabase();
    }

    // Lớp đại diện cho bạn bè trong danh sách
    public static class Friend {
        private final String name;
        private final boolean isFriend;
        private int userId;  // Thêm thuộc tính userId

        public Friend(int userId, String name, boolean isFriend) {
            this.name = name;
            this.isFriend = isFriend;
            this.userId = userId;
        }

        public String getName() {
            return name;
        }

        public boolean isFriend() {
            return isFriend;
        }

        public int getUserId() {
            return userId;  // Trả về userId
        }
    }

    // Phương thức để tải bạn bè từ database
    private void loadFriendsFromDatabase() {
        List<UserDTO> allUsers = UserBUS.getAllUsers(); // Lấy danh sách tất cả user từ bảng users
        List<FriendDTO> userFriends = UserBUS.getFriendsByUserId(user.getUserId()); // Lấy danh sách bạn từ bảng friend

        for (UserDTO otherUser : allUsers) {
            if (otherUser.getUserId() != user.getUserId()) { // Bỏ qua chính người dùng
                boolean isFriend = userFriends.stream()
                        .anyMatch(friend -> friend.getFriendId() == otherUser.getUserId());
                friends.add(new Friend(otherUser.getUserId(), otherUser.getFullName(), isFriend));
            }
        }
    }

    // Phương thức tải yêu cầu kết bạn từ database
    private void loadFriendRequestsFromDatabase() {
        // Tạo đối tượng FriendRequestBUS
        FriendRequestBUS friendRequestBUS = new FriendRequestBUS();
        
        // Gọi phương thức không tĩnh thông qua đối tượng
        List<FriendRequestDTO> friendRequestsFromDB = friendRequestBUS.getFriendRequestsByReceiverId(user.getUserId());
        
        // Cập nhật danh sách yêu cầu kết bạn
        friendRequests.setAll(friendRequestsFromDB);
        
        // Xử lý yêu cầu kết bạn (ví dụ: hiển thị trong giao diện)
        for (FriendRequestDTO request : friendRequestsFromDB) {
            System.out.println("Yêu cầu kết bạn từ " + request.getSenderId());
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
        backButton.setVisible(false); // chưa tìm thì k hiện back
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

        Button friendRequestButton = new Button("Yêu cầu kết bạn");
        friendRequestButton.setPrefWidth(200);
        friendRequestButton.setStyle("-fx-font-size: 14px;");
        friendRequestButton.setOnAction(_ -> showFriendRequests(root));

        leftMenu.getChildren().addAll(searchBox, searchResults, friendListButton, friendRequestButton);
        return leftMenu;
    }

    private void showFriendRequests(BorderPane root) {
        VBox requestContent = new VBox(10);
        requestContent.setStyle("-fx-padding: 10;");
    
        // Tạo một đối tượng FriendRequestBUS
        FriendRequestBUS friendRequestBUS = new FriendRequestBUS();
    
        // Tạo một đối tượng UserBUS
        UserBUS userBUS = new UserBUS();  // Tạo đối tượng UserBUS để gọi getUserById
    
        // Duyệt qua các yêu cầu kết bạn
        for (FriendRequestDTO request : friendRequests) {
            // Gọi getUserById thông qua đối tượng userBUS
            UserDTO sender = userBUS.getUserById(request.getSenderId()); 
    
            Button acceptButton = new Button("Chấp nhận");
            Button rejectButton = new Button("Từ chối");
    
            acceptButton.setOnAction(event -> {
                friendRequestBUS.acceptFriendRequest(request.getRequestId());
                friendRequests.remove(request);  // Cập nhật lại danh sách yêu cầu
            });
    
            rejectButton.setOnAction(event -> {
                friendRequestBUS.rejectFriendRequest(request.getRequestId());
                friendRequests.remove(request);  // Cập nhật lại danh sách yêu cầu
            });
    
            // Tạo HBox để chứa tên người gửi và các nút
            HBox requestBox = new HBox(10);
            requestBox.setStyle("-fx-padding: 5; -fx-border-color: #CCCCCC; -fx-border-width: 1; -fx-background-color: #F9F9F9;");
            requestBox.setAlignment(Pos.CENTER_LEFT);  // Căn tên người gửi ở giữa theo chiều dọc
    
            // Tên người gửi
            Label senderLabel = new Label(sender.getFullName());
            senderLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
            senderLabel.setAlignment(Pos.CENTER_LEFT);  // Căn tên người gửi theo chiều dọc
    
            // Tạo một Region chiếm không gian còn lại để đẩy nút sang phải
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
    
            // Tạo HBox cho các nút và căn nút sang phía cuối bên phải
            HBox buttonBox = new HBox(10);
            buttonBox.setAlignment(Pos.CENTER_RIGHT); // Căn nút sang bên phải
    
            // Thêm nút vào buttonBox
            buttonBox.getChildren().addAll(acceptButton, rejectButton);
    
            // Thêm tất cả vào requestBox
            requestBox.getChildren().addAll(senderLabel, spacer, buttonBox);
    
            // Thêm requestBox vào requestContent
            requestContent.getChildren().add(requestBox);
        }
    
        root.setCenter(requestContent);
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
        FriendRequestBUS friendRequestBUS = new FriendRequestBUS(); // Tạo đối tượng FriendRequestBUS
        
        if ("Người dùng".equals(tag) && content instanceof Friend friend) {
            contentLabel.setText(friend.getName());
            
            // Kiểm tra trạng thái hiện tại: Đã gửi yêu cầu kết bạn hay chưa
            boolean hasSentRequest = friendRequestBUS.hasSentRequest(user.getUserId(), friend.getUserId());
            boolean isFriend = friend.isFriend();
            
            // Cập nhật trạng thái nút
            if (isFriend) {
                actionButton.setText("Nhắn tin");
                actionButton.setOnAction(event -> {
                    System.out.println("Nhắn tin với " + friend.getName());
                    // Mở giao diện nhắn tin
                });
            } else if (hasSentRequest) {
                actionButton.setText("Hủy lời mời kết bạn");
                actionButton.setOnAction(event -> {
                    // Tìm yêu cầu kết bạn và xóa nó
                    List<FriendRequestDTO> sentRequests = friendRequestBUS.getSentRequestsByUserId(user.getUserId());
                    FriendRequestDTO targetRequest = sentRequests.stream()
                            .filter(request -> request.getReceiverId() == friend.getUserId() && "pending".equals(request.getStatus()))
                            .findFirst()
                            .orElse(null);
            
                    if (targetRequest != null) {
                        // Xóa yêu cầu kết bạn trong cơ sở dữ liệu
                        boolean success = friendRequestBUS.deleteFriendRequest(targetRequest.getRequestId());
                        if (success) {
                            // Cập nhật nút thành "Kết bạn"
                            actionButton.setText("Kết bạn");
                            System.out.println("Đã hủy lời mời kết bạn với " + friend.getName());
                        } else {
                            System.out.println("Không thể hủy lời mời kết bạn.");
                        }
                    } else {
                        System.out.println("Không tìm thấy yêu cầu kết bạn để hủy.");
                    }
                });
            } else {
                actionButton.setText("Kết bạn");
                actionButton.setOnAction(event -> {
                    // Tạo yêu cầu kết bạn mới và gửi
                    FriendRequestDTO newRequest = new FriendRequestDTO(
                            0,
                            user.getUserId(),
                            friend.getUserId(),
                            "pending",
                            java.time.LocalDateTime.now()
                    );
                    boolean success = friendRequestBUS.sendFriendRequest(newRequest);
                    if (success) {
                        // Cập nhật nút thành "Hủy lời mời kết bạn"
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
