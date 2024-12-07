package duck.presentation.userView;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

public class FriendRequestView {

    
    static class Request {
        private final String name;
        public Request(String name) {
            this.name = name;
        }
        public String getName() {return name;}
    }

    public VBox getContent() {
        VBox content = new VBox(10);
        content.setStyle("-fx-padding: 10;");

        ObservableList<Request> requests = FXCollections.observableArrayList(
            new Request("Nguyễn Văn A"),
            new Request("Trần Thị B"),
            new Request("Phạm Minh C"),
            new Request("Đỗ Quốc D")
        );
        
        ObservableList<Request> displayedRequests = FXCollections.observableArrayList(requests);
        
        ComboBox<String> sortOptions = new ComboBox<>();
        sortOptions.getItems().addAll("A-Z", "Z-A");
        sortOptions.setValue("A-Z");
        sortOptions.setStyle("-fx-font-size: 14px;");

        ListView<Request> requestList = new ListView<>(displayedRequests);
        VBox.setVgrow(requestList, Priority.ALWAYS);

        
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

                    accept.setOnAction(_ -> {
                        requests.remove(item); 
                        displayedRequests.remove(item); 
                    });

                    reject.setOnAction(_ -> {
                        requests.remove(item); 
                        displayedRequests.remove(item); 
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
}
