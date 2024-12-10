package duck.presentation.userView;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.util.List;

import duck.bus.FriendBUS;
import duck.bus.FriendRequestBUS;
import duck.bus.UserBUS;  // Import UserBUS
import duck.dto.FriendRequestDTO;
import duck.dto.UserDTO;

public class FriendRequestView {

    private final UserDTO user;
    private final ObservableList<FriendRequestDTO> friendRequests = FXCollections.observableArrayList();

    public FriendRequestView(UserDTO user) {
        this.user = user;
        loadFriendRequestsFromDatabase();
    }

    static class Request {
        private final String name;
        private final int requestId;
        private final int senderId;
        private final int receiverId;

        public Request(String name, int requestId, int senderId, int receiverId) {
            this.name = name;
            this.requestId = requestId;
            this.senderId = senderId;
            this.receiverId = receiverId;
        }

        public String getName() { return name; }
        public int getRequestId() { return requestId; }
        public int getSenderId() { return senderId; }
        public int getReceiverId() { return receiverId; }
    }

    public VBox getContent() {
        VBox content = new VBox(10);
        content.setStyle("-fx-padding: 10;");
    
        // Giả sử lấy dữ liệu yêu cầu từ cơ sở dữ liệu
        ObservableList<Request> displayedRequests = FXCollections.observableArrayList();
    
        // Thêm các yêu cầu vào danh sách hiển thị
        for (FriendRequestDTO friendRequest : friendRequests) {
            // Lấy tên người gửi từ UserBUS
            UserBUS userBUS = new UserBUS();
            UserDTO sender = userBUS.getUserById(friendRequest.getSenderId());
            String senderName = sender != null ? sender.getFullName() : "Tên người gửi không có sẵn";
            
            // Thêm yêu cầu vào danh sách hiển thị
            displayedRequests.add(new Request(senderName, friendRequest.getRequestId(), friendRequest.getSenderId(), friendRequest.getReceiverId()));
        }
    
        ComboBox<String> sortOptions = new ComboBox<>();
        sortOptions.getItems().addAll("A-Z", "Z-A");
        sortOptions.setValue("A-Z");
        sortOptions.setStyle("-fx-font-size: 14px;");
    
        ListView<Request> requestList = new ListView<>(displayedRequests);
        VBox.setVgrow(requestList, Priority.ALWAYS);
    
        // Tùy chỉnh cách hiển thị các yêu cầu trong ListView
        requestList.setCellFactory(_ -> new ListCell<>() {
            @Override
            protected void updateItem(Request item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    BorderPane container = new BorderPane();
                    container.setStyle("-fx-background-color: #E8E8E8; -fx-padding: 10;");
    
                    Text nameText = new Text(item.getName());
                    nameText.setStyle("-fx-font-size: 14px; -fx-fill: #333;");
                    container.setLeft(nameText);
    
                    MenuButton optionsButton = new MenuButton();
                    MenuItem accept = new MenuItem("Chấp nhận");
                    MenuItem reject = new MenuItem("Từ chối");
    
                    optionsButton.getItems().addAll(accept, reject);
                    optionsButton.setStyle("-fx-font-size: 14px;");
    
                    // Xử lý khi nhấn "Chấp nhận"
                    accept.setOnAction(_ -> {
                        FriendRequestBUS friendRequestBUS = new FriendRequestBUS();
                        boolean success = friendRequestBUS.acceptFriendRequest(item.getRequestId(), item.getSenderId(), item.getReceiverId());
                        if (success) {
                            // Xóa yêu cầu kết bạn khỏi màn hình và cơ sở dữ liệu
                            displayedRequests.remove(item);
                        } else {
                            System.out.println("Không thể chấp nhận yêu cầu.");
                        }
                    });
    
                    // Xử lý khi nhấn "Từ chối"
                    reject.setOnAction(_ -> {
                        FriendRequestBUS friendRequestBUS = new FriendRequestBUS();
                        boolean success = friendRequestBUS.rejectFriendRequest(item.getRequestId());
                        if (success) {
                            // Xóa yêu cầu kết bạn khỏi màn hình và cơ sở dữ liệu
                            displayedRequests.remove(item);
                        } else {
                            System.out.println("Không thể từ chối yêu cầu.");
                        }
                    });
    
                    container.setRight(optionsButton);
    
                    setGraphic(container);
                }
            }
        });
    
        sortOptions.setOnAction(_ -> {
            String sortChoice = sortOptions.getValue();
            if ("A-Z".equals(sortChoice)) {
                displayedRequests.sort((f1, f2) -> f1.getName().compareToIgnoreCase(f2.getName()));
            } else if ("Z-A".equals(sortChoice)) {
                displayedRequests.sort((f1, f2) -> f2.getName().compareToIgnoreCase(f1.getName()));
            }
        });
    
        content.getChildren().addAll(sortOptions, requestList);
        return content;
    }

    private void loadFriendRequestsFromDatabase() {
        // Tạo đối tượng FriendRequestBUS
        FriendRequestBUS friendRequestBUS = new FriendRequestBUS();
        
        // Gọi phương thức không tĩnh thông qua đối tượng
        List<FriendRequestDTO> friendRequestsFromDB = friendRequestBUS.getFriendRequestsByReceiverId(user.getUserId());
        
        // Lọc chỉ những yêu cầu có status là "pending"
        friendRequests.clear(); // Xóa danh sách hiện tại
        for (FriendRequestDTO request : friendRequestsFromDB) {
            if ("pending".equals(request.getStatus())) {
                friendRequests.add(request); // Thêm yêu cầu nếu có status là "pending"
            }
        }
        
        // Hiển thị thông tin về các yêu cầu kết bạn trong console (nếu cần)
        for (FriendRequestDTO request : friendRequests) {
            System.out.println("Yêu cầu kết bạn từ " + request.getSenderId());
        }
    }
    
}
