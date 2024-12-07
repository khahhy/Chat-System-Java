package duck.dao;

import duck.dto.FriendDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FriendDAO {

    // list bạn bè của user
    public List<FriendDTO> getFriendsByUserId(int userId) throws SQLException {
        List<FriendDTO> friendList = new ArrayList<>();
        String query = "SELECT * FROM friends WHERE user_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                friendList.add(new FriendDTO(
                        rs.getInt("user_id"),
                        rs.getInt("friend_id"),
                        rs.getBoolean("is_blocked"),
                        rs.getTimestamp("created_at").toLocalDateTime()
                ));
            }
        }
        return friendList;
    }


    public boolean addFriend(FriendDTO friend) throws SQLException {
        String query = "INSERT INTO friends (user_id, friend_id, is_blocked, created_at) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, friend.getUserId());
            stmt.setInt(2, friend.getFriendId());
            stmt.setBoolean(3, friend.isBlocked());
            stmt.setTimestamp(4, Timestamp.valueOf(friend.getCreatedAt()));
            return stmt.executeUpdate() > 0;
        }
    }

    // block
    public boolean updateFriend(FriendDTO friend) throws SQLException {
        String query = "UPDATE friends SET is_blocked = ?, created_at = ? WHERE user_id = ? AND friend_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setBoolean(1, friend.isBlocked());
            stmt.setTimestamp(2, Timestamp.valueOf(friend.getCreatedAt()));
            stmt.setInt(3, friend.getUserId());
            stmt.setInt(4, friend.getFriendId());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean deleteFriend(int userId, int friendId) throws SQLException {
        String query = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, friendId);
            return stmt.executeUpdate() > 0;
        }
    }

    // check có là bạn bè k
    public boolean isFriend(int userId, int friendId) throws SQLException {
        String query = "SELECT COUNT(*) FROM friends WHERE user_id = ? AND friend_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, friendId);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }
}
