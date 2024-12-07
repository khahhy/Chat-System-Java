package duck.dto;

import java.time.LocalDateTime;

public class GroupDTO {
    private int groupId;
    private String groupName;
    private LocalDateTime createdAt;

    public GroupDTO(int groupId, String groupName, LocalDateTime createdAt) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.createdAt = createdAt;
    }

   
    public int getGroupId() { return groupId; }
    public void setGroupId(int groupId) { this.groupId = groupId; }

    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
