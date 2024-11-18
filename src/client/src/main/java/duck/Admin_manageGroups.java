package duck;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class Admin_manageGroups {

    public class Group {
        private String name;
        private LocalDateTime createdAt;
        private List<String> members;
        private List<String> admins;

        public Group(String name, LocalDateTime createdAt, List<String> members, List<String> admins) {
            this.name = name;
            this.createdAt = createdAt;
            this.members = members;
            this.admins = admins;
        }
        public String getName() {return name;}
        public LocalDateTime getCreatedAt() {return createdAt;}
        public List<String> getMembers() {return members;}
        public List<String> getAdmins() {return admins;}
    }

    private final ObservableList<Group> groups = FXCollections.observableArrayList(
        new Group("học java", LocalDateTime.of(2023, 11, 1, 10, 0), 
                  Arrays.asList("Nguyễn Văn A", "Trần Thị B", "Phạm Minh C"), 
                  Arrays.asList("Nguyễn Văn A")),
        new Group("22clc02", LocalDateTime.of(2023, 10, 25, 15, 30), 
                  Arrays.asList("Đỗ Quốc D", "Nguyễn Văn E"), 
                  Arrays.asList("Đỗ Quốc D")),
        new Group("khtn", LocalDateTime.of(2023, 9, 15, 9, 0), 
                  Arrays.asList("Phạm Minh C", "Trần Thị B"), 
                  Arrays.asList("Phạm Minh C"))
    );

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

        
        ListView<Group> groupListView = new ListView<>(groups);
        groupListView.setCellFactory(_ -> new ListCell<>() {
            @Override
            protected void updateItem(Group group, boolean empty) {
                super.updateItem(group, empty);
                if (empty || group == null) {
                    setGraphic(null);
                } else {
                    
                    VBox container = new VBox(5);
                    container.setStyle("-fx-padding: 10; -fx-background-color: #e6f7ff; -fx-border-color: #b3d9ff; -fx-border-width: 1;");

                    Label nameLabel = new Label(group.getName());
                    nameLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

                    Label createdAtLabel = new Label("Ngày tạo: " + group.getCreatedAt());
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

    private ObservableList<Group> filterGroups(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return groups;
        }
        String lowerKeyword = keyword.toLowerCase();
        return groups.filtered(group -> group.getName().toLowerCase().contains(lowerKeyword));
    }

    private ObservableList<Group> sortGroups(ObservableList<Group> groups, String sortChoice) {
        ObservableList<Group> sortedGroups = FXCollections.observableArrayList(groups);
        switch (sortChoice) {
            case "Tên A-Z":
                sortedGroups.sort((g1, g2) -> g1.getName().compareToIgnoreCase(g2.getName()));
                break;
            case "Tên Z-A":
                sortedGroups.sort((g1, g2) -> g2.getName().compareToIgnoreCase(g1.getName()));
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

    private void showGroupDetailsPopup(Group group) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.initStyle(StageStyle.UTILITY);
        popup.setTitle("Chi tiết nhóm: " + group.getName());

        VBox content = new VBox(10);
        content.setStyle("-fx-padding: 20;");

        Label groupNameLabel = new Label("Tên nhóm: " + group.getName());
        Label createdAtLabel = new Label("Ngày tạo: " + group.getCreatedAt());

        ListView<String> adminListView = new ListView<>(FXCollections.observableArrayList(group.getAdmins()));
        adminListView.setPrefHeight(100);

        ListView<String> memberListView = new ListView<>(FXCollections.observableArrayList(group.getMembers()));
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
