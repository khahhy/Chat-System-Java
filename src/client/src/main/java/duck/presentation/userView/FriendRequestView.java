package duck.presentation.userView;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.util.List;

import duck.bus.FriendRequestBUS;
import duck.bus.UserBUS;  // Import UserBUS
import duck.dto.FriendRequestDTO;
import duck.dto.UserDTO;

public class FriendRequestView {
    private final UserDTO user;
    private final UserBUS userBUS;
    private final FriendRequestBUS friendReqBUS;
    private final List<FriendRequestDTO> list_req;
    private final ObservableList<FriendRequestDTO> friendRequests;

    public FriendRequestView(UserDTO user) {
        this.user = user;
        this.userBUS = new UserBUS();
        this.friendReqBUS = new FriendRequestBUS();
        this.list_req = friendReqBUS.getReceivedRequestsByUserId(user.getUserId());
        friendRequests = FXCollections.observableArrayList(list_req);
        
    }

    public VBox getContent() {
        VBox content = new VBox(10);
        content.setStyle("-fx-padding: 10;");
    
        ObservableList<FriendRequestDTO> displayedRequests = FXCollections.observableArrayList();
        
        TextField searchField = new TextField();
        searchField.setPromptText("Tìm lời mời...");
        searchField.setStyle("-fx-font-size: 14px;");
        searchField.textProperty().addListener((_, _, newValue) -> {
            displayedRequests.setAll(friendRequests.filtered(
                friendRequests -> userBUS.getUserById(friendRequests.getSenderId()).getUsername().toLowerCase().contains(newValue.toLowerCase()) ||
                userBUS.getUserById(friendRequests.getSenderId()).getUsername().toLowerCase().contains(newValue.toLowerCase())
            ));
        });

        ComboBox<String> sortOptions = new ComboBox<>();
        sortOptions.getItems().addAll("A-Z", "Z-A");
        sortOptions.setValue("A-Z");
        sortOptions.setStyle("-fx-font-size: 14px;");
    
        ListView<FriendRequestDTO> requestList = new ListView<>(friendRequests);
        VBox.setVgrow(requestList, Priority.ALWAYS);
    
        requestList.setCellFactory(_ -> new ListCell<>() {
            protected void updateItem(FriendRequestDTO item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    BorderPane container = new BorderPane();
                    container.setStyle("-fx-background-color: #E8E8E8; -fx-padding: 10;");

                    Text nameText = new Text(userBUS.getUserById(item.getSenderId()).getUsername());
                    nameText.setStyle("-fx-font-size: 14px; -fx-fill: #333;");
                    
                    String fullName = userBUS.getUserById(item.getSenderId()).getFullName();
                    if (fullName == null || fullName.isEmpty()) {
                        fullName = "Chưa cập nhật";  
                    }

                    Text fullNameText = new Text(" [" + fullName + "]");
                    fullNameText.setStyle("-fx-font-size: 12px; -fx-fill: #777;");
                    HBox nameContainer = new HBox(5, nameText, fullNameText);
                    nameContainer.setAlignment(Pos.CENTER_LEFT);

                    container.setLeft(nameContainer);
    
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
                displayedRequests.sort((f1, f2) -> userBUS.getUserById(f1.getSenderId()).getUsername().compareToIgnoreCase(userBUS.getUserById(f2.getSenderId()).getUsername()));
            } else if ("Z-A".equals(sortChoice)) {
                displayedRequests.sort((f1, f2) -> userBUS.getUserById(f2.getSenderId()).getUsername().compareToIgnoreCase(userBUS.getUserById(f1.getSenderId()).getUsername()));
            }
        });
    
        content.getChildren().addAll( searchField, sortOptions, requestList);
        return content;
    }
    
}
