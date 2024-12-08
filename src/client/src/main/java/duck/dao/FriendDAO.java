package duck.dao;

import duck.dto.FriendDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FriendDAO {
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

    // cua admin
    public int getTotalFriends(int userId) throws SQLException {
        String query = "SELECT COUNT(*) AS totalFriends FROM friends WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("totalFriends");
            }
        }
        return 0; 
    }

    // cua admin
    public int getTotalFriendsOfFriends(int userId) throws SQLException {
        String query = "SELECT SUM(totalFriendCounts.totalFriends) AS totalFriendsOfAllFriends " +
                       "FROM ( " +
                       "  SELECT f2.user_id AS friendId, COUNT(f3.friend_id) AS totalFriends " +
                       "  FROM friends f1 " +
                       "  LEFT JOIN friends f2 ON f1.friend_id = f2.user_id " +
                       "  LEFT JOIN friends f3 ON f2.user_id = f3.user_id " +
                       "  WHERE f1.user_id = ? " +
                       "  GROUP BY f2.user_id " +
                       ") totalFriendCounts";
    
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("totalFriendsOfAllFriends");
            }
        }
        return 0; 
    }
    
    
}
