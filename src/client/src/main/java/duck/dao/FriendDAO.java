package duck.dao;

import duck.dto.FriendDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FriendDAO {
    public List<FriendDTO> getFriendsByUserId(int userId) throws SQLException {
        List<FriendDTO> friendList = new ArrayList<>();
        String query = "SELECT * FROM friends WHERE user_id = ? and is_blocked = false";
        
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
        String sql = "DELETE FROM friends WHERE (user_id = ? AND friend_id = ?) OR (user_id = ? AND friend_id = ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, friendId);
            stmt.setInt(3, friendId);
            stmt.setInt(4, userId);
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
    
    public boolean blockFriend(int userId, int friendId) throws SQLException {
        String deleteSql = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";
        String updateSql = "UPDATE friends SET is_blocked = true WHERE user_id = ? AND friend_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false); // Bắt đầu transaction
    
            // Xóa dòng user_id=friendId và friend_id=userId
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                deleteStmt.setInt(1, friendId);
                deleteStmt.setInt(2, userId);
                deleteStmt.executeUpdate();
            }
    
            // Cập nhật dòng user_id=userId và friend_id=friendId
            try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                updateStmt.setInt(1, userId);
                updateStmt.setInt(2, friendId);
                updateStmt.executeUpdate();
            }
    
            conn.commit(); // Hoàn tất transaction
            return true;
        } 
    }
    
}
