package duck.dao;

import duck.dto.FriendRequestDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FriendRequestDAO {
    // lời mời đã gửi
    public List<FriendRequestDTO> getSentRequestsByUserId(int senderId) throws SQLException {
        List<FriendRequestDTO> requestList = new ArrayList<>();
        String query = "SELECT * FROM FriendRequests WHERE sender_id = ? AND status = 'pending'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, senderId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                requestList.add(new FriendRequestDTO(
                        rs.getInt("request_id"),
                        rs.getInt("sender_id"),
                        rs.getInt("receiver_id"),
                        rs.getString("status"),
                        rs.getTimestamp("created_at").toLocalDateTime()
                ));
            }
        }
        return requestList;
    }

    // lời mời đã nhận
    public List<FriendRequestDTO> getReceivedRequestsByUserId(int receiverId) throws SQLException {
        List<FriendRequestDTO> requestList = new ArrayList<>();
        String query = "SELECT * FROM FriendRequests WHERE receiver_id = ? AND status = 'pending'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, receiverId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                requestList.add(new FriendRequestDTO(
                        rs.getInt("request_id"),
                        rs.getInt("sender_id"),
                        rs.getInt("receiver_id"),
                        rs.getString("status"),
                        rs.getTimestamp("created_at").toLocalDateTime()
                ));
            }
        }
        return requestList;
    }

    // gửi lời mời kết bạn
    public boolean sendFriendRequest(FriendRequestDTO request) throws SQLException {
        String query = "INSERT INTO FriendRequests (sender_id, receiver_id, status, created_at) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, request.getSenderId());
            stmt.setInt(2, request.getReceiverId());
            stmt.setString(3, request.getStatus());
            stmt.setTimestamp(4, Timestamp.valueOf(request.getCreatedAt()));
            return stmt.executeUpdate() > 0;
        }
    }

    // update status
    public boolean updateFriendRequestStatus(int requestId, String status) throws SQLException {
        String query = "UPDATE FriendRequests SET status = ? WHERE request_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, status);
            stmt.setInt(2, requestId);
            return stmt.executeUpdate() > 0;
        }
    }

    // từ chối
    public boolean deleteFriendRequest(int requestId) throws SQLException {
        String query = "DELETE FROM FriendRequests WHERE request_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, requestId);
            return stmt.executeUpdate() > 0;
        }
    }

    // check user đã gửi lời mời kết bạn cho mình chưa
    public boolean hasSentRequest(int senderId, int receiverId) throws SQLException {
        String query = "SELECT COUNT(*) FROM FriendRequests WHERE sender_id = ? AND receiver_id = ? AND status = 'pending'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, senderId);
            stmt.setInt(2, receiverId);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    // check user đã đồng ý kết bạn chưa
    public boolean hasReceivedRequest(int senderId, int receiverId) throws SQLException {
        String query = "SELECT COUNT(*) FROM FriendRequests WHERE sender_id = ? AND receiver_id = ? AND status = 'pending'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, senderId);
            stmt.setInt(2, receiverId);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }
}
