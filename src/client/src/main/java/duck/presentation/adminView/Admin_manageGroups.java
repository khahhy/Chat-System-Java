package duck.presentation.adminView;

import duck.bus.GroupBUS;
import duck.bus.GroupMemberBUS;
import duck.dto.GroupDTO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class Admin_manageGroups {
    private GroupBUS groupBUS;
    private GroupMemberBUS groupMemBUS;
    List<GroupDTO> group_list;
    ObservableList<GroupDTO> groups;

    public Admin_manageGroups() {
        groupBUS = new GroupBUS();
        groupMemBUS = new GroupMemberBUS();
        group_list = groupBUS.getAllGroups();
        groups = FXCollections.observableArrayList(group_list);
    }
    

    public BorderPane getContent() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-padding: 20;");

        VBox groupList = new VBox(10);
        groupList.setStyle("-fx-padding: 10; -fx-background-color: #f9f9f9;");

        TextField searchField = new TextField();
        searchField.setPromptText("Tìm kiếm nhóm...");
        searchField.setPrefWidth(300);

        ComboBox<String> sortOptions = new ComboBox<>(FXCollections.observableArrayList("Tên A-Z", "Tên Z-A", "Thời gian tạo mới nhất", "Thời gian tạo cũ nhất"));
        sortOptions.setValue("Tên A-Z");

        
        ListView<GroupDTO> groupListView = new ListView<>(groups);
        groupListView.setCellFactory(_ -> new ListCell<>() {
            @Override
            protected void updateItem(GroupDTO group, boolean empty) {
                super.updateItem(group, empty);
                if (empty || group == null) {
                    setGraphic(null);
                } else {
                    
                    VBox container = new VBox(5);
                    container.setStyle("-fx-padding: 10; -fx-background-color: #e6f7ff; -fx-border-color: #b3d9ff; -fx-border-width: 1;");

                    Label nameLabel = new Label(group.getGroupName());
                    nameLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                    Label createdAtLabel = new Label("Ngày tạo: " + group.getCreatedAt().format(formatter));
                    createdAtLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #555;");

                    Button detailButton = new Button("Chi tiết");
                    detailButton.setStyle("-fx-font-size: 12px;");
                    detailButton.setOnAction(_ -> showGroupDetailsPopup(group));

                    container.getChildren().addAll(nameLabel, createdAtLabel, detailButton);
                    setGraphic(container);
                }
            }
        });

        
        searchField.textProperty().addListener((_, _, newValue) -> {
            groupListView.setItems(filterGroups(newValue));
        });

       
        sortOptions.setOnAction(_ -> {
            String sortChoice = sortOptions.getValue();
            groupListView.setItems(sortGroups(groups, sortChoice));
        });

        VBox.setVgrow(groupListView, Priority.ALWAYS);
        groupList.getChildren().addAll(searchField, sortOptions, groupListView);
        root.setCenter(groupList);

        return root;
    }

    private ObservableList<GroupDTO> filterGroups(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return groups;
        }
        String lowerKeyword = keyword.toLowerCase();
        return groups.filtered(group -> group.getGroupName().toLowerCase().contains(lowerKeyword));
    }

    private ObservableList<GroupDTO> sortGroups(ObservableList<GroupDTO> groups, String sortChoice) {
        ObservableList<GroupDTO> sortedGroups = FXCollections.observableArrayList(groups);
        switch (sortChoice) {
            case "Tên A-Z":
                sortedGroups.sort((g1, g2) -> g1.getGroupName().compareToIgnoreCase(g2.getGroupName()));
                break;
            case "Tên Z-A":
                sortedGroups.sort((g1, g2) -> g2.getGroupName().compareToIgnoreCase(g1.getGroupName()));
                break;
            case "Thời gian tạo mới nhất":
                sortedGroups.sort((g1, g2) -> g2.getCreatedAt().compareTo(g1.getCreatedAt()));
                break;
            case "Thời gian tạo cũ nhất":
                sortedGroups.sort((g1, g2) -> g1.getCreatedAt().compareTo(g2.getCreatedAt()));
                break;
        }
        return sortedGroups;
    }

    private void showGroupDetailsPopup(GroupDTO group) {
        List<String> admins = groupMemBUS.getAdmin(group.getGroupId());
        List<String> members = groupMemBUS.getMem(group.getGroupId());
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

        ListView<String> memberListView = new ListView<>(FXCollections.observableArrayList(members));
        memberListView.setPrefHeight(200);

        content.getChildren().addAll(
            groupNameLabel,
            createdAtLabel,
            new Label("Danh sách Admin:"),
            adminListView,
            new Label("Danh sách Thành viên:"),
            memberListView
        );

        Scene scene = new Scene(content, 400, 400);
        popup.setScene(scene);
        popup.showAndWait();
    }
}
