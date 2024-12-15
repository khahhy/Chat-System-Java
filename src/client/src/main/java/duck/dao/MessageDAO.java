package duck.dao;

import duck.dto.GroupDTO;
import duck.dto.MessageDTO;
import duck.dto.UserDTO;

import java.sql.*;
import java.time.LocalDateTime;
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
                        rs.getObject("receiver_id", Integer.class),
                        rs.getObject("group_id", Integer.class),
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
                        rs.getObject("receiver_id", Integer.class),
                        rs.getObject("group_id", Integer.class),
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
    
            if (message.getGroupId() != null) {
                stmt.setNull(2, java.sql.Types.INTEGER); 
                stmt.setInt(3, message.getGroupId());     
            } else {
                stmt.setInt(2, message.getReceiverId()); 
                stmt.setNull(3, java.sql.Types.INTEGER);  
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
                        rs.getObject("receiver_id", Integer.class),
                        rs.getObject("group_id", Integer.class),
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
                            userRs.getTimestamp("date_of_birth") != null ? rs.getTimestamp("date_of_birth").toLocalDateTime() : null,
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

    public List<GroupDTO> getGroupsFromMessages(int userId) throws SQLException {
        List<GroupDTO> groups = new ArrayList<>();
        String query = "SELECT DISTINCT g.group_id, g.group_name, g.created_at " +
                   "FROM groups g " +
                   "JOIN GroupMembers gm ON g.group_id = gm.group_id " +
                   "JOIN messages m ON m.group_id = g.group_id " +
                   "WHERE gm.user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
        
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int groupId = rs.getInt("group_id");
                String groupName = rs.getString("group_name");
                LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();
            
                GroupDTO group = new GroupDTO(groupId, groupName, createdAt);
                groups.add(group);
            }
        }
        return groups;
    }

    public MessageDTO getLastMessageBetweenUsers(int userId1, int userId2) throws SQLException {
        String query = "SELECT * FROM messages " +
                       "WHERE (sender_id = ? AND receiver_id = ?) OR (sender_id = ? AND receiver_id = ?) " +
                       "ORDER BY timestamp DESC LIMIT 1";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId1);
            stmt.setInt(2, userId2);
            stmt.setInt(3, userId2);
            stmt.setInt(4, userId1);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int messageId = rs.getInt("message_id");
                int senderId = rs.getInt("sender_id");
                Integer receiverId = rs.getObject("receiver_id", Integer.class); 
                Integer groupId = rs.getObject("group_id", Integer.class);  
                String content = rs.getString("content");
                LocalDateTime timestamp = rs.getTimestamp("timestamp").toLocalDateTime();
                boolean isEncrypted = rs.getBoolean("is_encrypted");  
                
                return new MessageDTO(messageId, senderId, receiverId, groupId, content, timestamp, isEncrypted);
            }
        }
        return null;
    }
    
    public MessageDTO getLastMessageInGroup(int groupId) throws SQLException {
        String query = "SELECT * FROM messages " +
                       "WHERE group_id = ? " +
                       "ORDER BY timestamp DESC LIMIT 1";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setObject(1, groupId, java.sql.Types.INTEGER);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int messageId = rs.getInt("message_id");
                int senderId = rs.getInt("sender_id");
                Integer receiverId = rs.getObject("receiver_id", Integer.class);  
                Integer groupIdResult = rs.getObject("group_id", Integer.class);
                String content = rs.getString("content");
                LocalDateTime timestamp = rs.getTimestamp("timestamp").toLocalDateTime();
                boolean isEncrypted = rs.getBoolean("is_encrypted");  
                
                return new MessageDTO(messageId, senderId, receiverId, groupIdResult, content, timestamp, isEncrypted);
            }
        }
        return null;
    }
    
    public List<MessageDTO> getAllMessagesByUser(int userId) throws SQLException {
        List<MessageDTO> messages = new ArrayList<>();
        String query = "SELECT DISTINCT m.* " +
                       "FROM messages m " +
                       "LEFT JOIN GroupMembers gm ON m.group_id = gm.group_id " +
                       "LEFT JOIN deletemessages dm ON m.message_id = dm.message_id AND dm.user_id = ? " +
                       "WHERE (m.sender_id = ? OR m.receiver_id = ? OR gm.user_id = ?) " +
                       "AND dm.message_id IS NULL " + // Loại bỏ các tin nhắn đã bị xóa bởi userId
                       "ORDER BY m.timestamp";
    
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId); // Liên kết với bảng deletemessages
            stmt.setInt(2, userId); // Tin nhắn do user này gửi
            stmt.setInt(3, userId); // Tin nhắn mà user này nhận
            stmt.setInt(4, userId); // Tin nhắn trong các nhóm mà user này tham gia
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                messages.add(new MessageDTO(
                    rs.getInt("message_id"),
                    rs.getInt("sender_id"),
                    rs.getObject("receiver_id", Integer.class),
                    rs.getObject("group_id", Integer.class),
                    rs.getString("content"),
                    rs.getTimestamp("timestamp").toLocalDateTime(),
                    rs.getBoolean("is_encrypted")
                ));
            }
        }
        return messages;
    }
    
    
}
