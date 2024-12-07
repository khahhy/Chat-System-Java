package duck.dto;

import java.time.LocalDateTime;

public class FriendDTO {
    private int userId;
    private int friendId;
    private boolean isBlocked;
    private LocalDateTime createdAt;

    public FriendDTO(int userId, int friendId, boolean isBlocked, LocalDateTime createdAt) {
        this.userId = userId;
        this.friendId = friendId;
        this.isBlocked = isBlocked;
        this.createdAt = createdAt;
    }

    
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getFriendId() { return friendId; }
    public void setFriendId(int friendId) { this.friendId = friendId; }

    public boolean isBlocked() { return isBlocked; }
    public void setBlocked(boolean blocked) { isBlocked = blocked; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
