package duck.presentation.userView;

import java.time.format.DateTimeFormatter;
import java.util.List;

import duck.bus.GroupBUS;
import duck.bus.GroupMemberBUS;
import duck.dto.GroupDTO;
import duck.dto.UserDTO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class GroupRequestView {
    private final UserDTO user;
    private final GroupMemberBUS groupMemBUS;
    private final GroupBUS groupBUS;
    private final List<GroupDTO> group_list;
    private final ObservableList<GroupDTO> groups;

    public GroupRequestView(UserDTO user) {
        this.user = user;
        groupMemBUS = new GroupMemberBUS();
        groupBUS = new GroupBUS();
        group_list = groupBUS.getAllGroupRequestByUserId(user.getUserId());
        groups = FXCollections.observableArrayList(group_list);
    }


    public VBox getContent() {
        VBox content = new VBox(10);
        content.setStyle("-fx-padding: 10;");


        ObservableList<GroupDTO> displayedGroups = FXCollections.observableArrayList(groups);

        ComboBox<String> sortOptions = new ComboBox<>();
        sortOptions.getItems().addAll("A-Z", "Z-A");
        sortOptions.setValue("A-Z");
        sortOptions.setStyle("-fx-font-size: 14px;");

        ListView<GroupDTO> groupList = new ListView<>(displayedGroups);
        VBox.setVgrow(groupList, Priority.ALWAYS);

        groupList.setCellFactory(_ -> new ListCell<>() {
            protected void updateItem(GroupDTO item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    BorderPane container = new BorderPane();
                    container.setStyle("-fx-background-color: #E8E8E8; -fx-padding: 10;");
        

                    Text groupName = new Text(item.getGroupName());
                    groupName.setStyle("-fx-font-size: 14px; -fx-fill: #333;");
                    container.setLeft(groupName);

                    MenuButton optionsButton = new MenuButton();
                    MenuItem viewInfo = new MenuItem("Xem thông tin");
                    MenuItem accept = new MenuItem("Chấp nhận");
                    MenuItem reject = new MenuItem("Từ chối");

                    optionsButton.getItems().addAll(viewInfo, accept, reject);
                    optionsButton.setStyle("-fx-font-size: 14px;");

                    viewInfo.setOnAction(_ -> showGroupInfoPopup(item));

                    accept.setOnAction(_ -> {
                        if (groupMemBUS.approveMember(item.getGroupId(), user.getUserId())) {
                            groups.remove(item); 
                            displayedGroups.remove(item); 
                        }
                    });

                    reject.setOnAction(_ -> {
                        if (groupMemBUS.removeMember(item.getGroupId(), user.getUserId())) {
                            groups.remove(item); 
                            displayedGroups.remove(item); 
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
                displayedGroups.sort((f1, f2) -> f1.getGroupName().compareToIgnoreCase(f2.getGroupName()));
            } else if ("Z-A".equals(sortChoice)) {
                displayedGroups.sort((f1, f2) -> f2.getGroupName().compareToIgnoreCase(f1.getGroupName()));
            }
        });


        content.getChildren().addAll(sortOptions, groupList);
        return content;
    }

    private void showGroupInfoPopup(GroupDTO group) {
        List<String> admins = groupMemBUS.getAdmin(group.getGroupId());
        
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.initStyle(StageStyle.UTILITY);
        popup.setTitle("Chi tiết nhóm: " + group.getGroupName());

        VBox content = new VBox(10);
        content.setStyle("-fx-padding: 20;");

        Label groupNameLabel = new Label("Tên nhóm: " + group.getGroupName());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        Label createdAtLabel = new Label("Ngày tạo: " + group.getCreatedAt().format(formatter));


        ListView<String> adminListView = new ListView<>(FXCollections.observableArrayList(admins));
        adminListView.setPrefHeight(100);

        content.getChildren().addAll(
            groupNameLabel,
            createdAtLabel,
            new Label("Danh sách Admin:"),
            adminListView           
        );

        Scene scene = new Scene(content, 400, 400);
        popup.setScene(scene);
        popup.showAndWait();
    }
}
