package duck.dto;

import java.time.LocalDateTime;

public class GroupMemberDTO {
    private int groupId;
    private int userId;
    private boolean isAdmin;  
    private LocalDateTime joinedAt;
    private boolean isApproved;

    public GroupMemberDTO(int groupId, int userId, boolean isAdmin, LocalDateTime joinedAt, boolean isApproved) {
        this.groupId = groupId;
        this.userId = userId;
        this.isAdmin = isAdmin;
        this.joinedAt = joinedAt;
        this.isApproved = isApproved;
    }

    
    public int getGroupId() { return groupId; }
    public void setGroupId(int groupId) { this.groupId = groupId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public boolean isAdmin() { return isAdmin; }
    public void setAdmin(boolean admin) { isAdmin = admin; }

    public LocalDateTime getJoinedAt() { return joinedAt; }
    public void setJoinedAt(LocalDateTime joinedAt) { this.joinedAt = joinedAt; }

    public boolean isApproved() { return isApproved; }
    public void setApproved(boolean approved) { isApproved = approved; }
}
