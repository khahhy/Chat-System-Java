package duck.dao;

import duck.dto.MessageDTO;
import duck.dto.UserDTO;

import java.sql.*;

import java.util.ArrayList;
import java.util.List;

public class MessageDAO {

    // tin nhắn giữa hai người dùng
    public List<MessageDTO> getMessagesBetweenUsers(int senderId, int receiverId) throws SQLException {
        List<MessageDTO> messages = new ArrayList<>();
        String query = "SELECT * FROM messages WHERE (sender_id = ? AND receiver_id = ?) OR (sender_id = ? AND receiver_id = ?) ORDER BY timestamp";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, senderId);
            stmt.setInt(2, receiverId);
            stmt.setInt(3, receiverId);
            stmt.setInt(4, senderId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                messages.add(new MessageDTO(
                        rs.getInt("message_id"),
                        rs.getInt("sender_id"),
                        rs.getInt("receiver_id"),
                        rs.getInt("group_id"),
                        rs.getString("content"),
                        rs.getTimestamp("timestamp").toLocalDateTime(),
                        rs.getBoolean("is_encrypted")
                ));
            }
        }
        return messages;
    }

    // tin nhắn trong một nhóm
    public List<MessageDTO> getMessagesInGroup(int groupId) throws SQLException {
        List<MessageDTO> messages = new ArrayList<>();
        String query = "SELECT * FROM messages WHERE group_id = ? ORDER BY timestamp";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, groupId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                messages.add(new MessageDTO(
                        rs.getInt("message_id"),
                        rs.getInt("sender_id"),
                        rs.getInt("receiver_id"),
                        rs.getInt("group_id"),
                        rs.getString("content"),
                        rs.getTimestamp("timestamp").toLocalDateTime(),
                        rs.getBoolean("is_encrypted")
                ));
            }
        }
        return messages;
    }

    public boolean addMessage(MessageDTO message) throws SQLException {
        String query = "INSERT INTO messages (sender_id, receiver_id, group_id, content, timestamp, is_encrypted) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, message.getSenderId());
            stmt.setInt(2, message.getReceiverId());
            if (message.getGroupId()==0) {
                stmt.setNull(3, Types.INTEGER);
            } else {
                stmt.setInt(3, message.getGroupId());
            }
            stmt.setString(4, message.getContent());
            stmt.setTimestamp(5, Timestamp.valueOf(message.getTimestamp()));
            stmt.setBoolean(6, message.isEncrypted());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean deleteMessage(int messageId) throws SQLException {
        String query = "DELETE FROM messages WHERE message_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, messageId);
            return stmt.executeUpdate() > 0;
        }
    }

    // xóa tất cả tin nhắn trong nhóm
    public boolean deleteMessagesInGroup(int groupId) throws SQLException {
        String query = "DELETE FROM messages WHERE group_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, groupId);
            return stmt.executeUpdate() > 0;
        }
    }

    // Tìm kiếm tin nhắn theo từ khóa
    public List<MessageDTO> searchMessages(int userId, String keyword) throws SQLException {
        List<MessageDTO> messages = new ArrayList<>();
        String query = "SELECT * FROM messages WHERE (sender_id = ? OR receiver_id = ?) AND content LIKE ? ORDER BY timestamp";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, userId);
            stmt.setString(3, "%" + keyword + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                messages.add(new MessageDTO(
                        rs.getInt("message_id"),
                        rs.getInt("sender_id"),
                        rs.getInt("receiver_id"),
                        rs.getInt("group_id"),
                        rs.getString("content"),
                        rs.getTimestamp("timestamp").toLocalDateTime(),
                        rs.getBoolean("is_encrypted")
                ));
            }
        }
        return messages;
    }
    
    public List<UserDTO> getFriendsFromMessage(int userId) throws SQLException {
        List<UserDTO> friends = new ArrayList<>();
    
        // Query to find all user IDs who have messages with the given userId
        String query = "SELECT DISTINCT CASE " +
                       "WHEN sender_id = ? THEN receiver_id " +
                       "WHEN receiver_id = ? THEN sender_id " +
                       "END AS friend_id " +
                       "FROM messages " +
                       "WHERE sender_id = ? OR receiver_id = ?";
    
        // Subquery results to fetch user details
        String userQuery = "SELECT * FROM users WHERE user_id = ?";
    
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, userId);
            stmt.setInt(3, userId);
            stmt.setInt(4, userId);
    
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int friendId = rs.getInt("friend_id");
    
                // Fetch user details for each friendId
                try (PreparedStatement userStmt = conn.prepareStatement(userQuery)) {
                    userStmt.setInt(1, friendId);
                    ResultSet userRs = userStmt.executeQuery();
                    if (userRs.next()) {
                        friends.add(new UserDTO(
                            userRs.getInt("user_id"),
                            userRs.getString("username"),
                            userRs.getString("full_name"),
                            userRs.getString("address"),
                            userRs.getTimestamp("date_of_birth").toLocalDateTime(),
                            userRs.getString("gender").charAt(0),
                            userRs.getString("email"),
                            userRs.getString("password"),
                            userRs.getBoolean("status"),
                            userRs.getBoolean("is_online"),
                            userRs.getTimestamp("created_at").toLocalDateTime(),
                            userRs.getBoolean("is_admin")
                        ));
                    }
                }
            }
        }
        return friends;
    }
    
}
