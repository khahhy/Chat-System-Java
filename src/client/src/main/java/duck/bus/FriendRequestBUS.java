package duck.bus;

import duck.dao.FriendRequestDAO;
import duck.dto.FriendRequestDTO;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FriendRequestBUS {
    private FriendRequestDAO friendRequestDAO;

    public FriendRequestBUS() {
        friendRequestDAO = new FriendRequestDAO();
    }

    // Lời mời đã gửi
    public List<FriendRequestDTO> getSentRequestsByUserId(int senderId) {
        try {
            return friendRequestDAO.getSentRequestsByUserId(senderId);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // Lời mời đã nhận
    public List<FriendRequestDTO> getReceivedRequestsByUserId(int receiverId) {
        try {
            return friendRequestDAO.getReceivedRequestsByUserId(receiverId);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // Lấy các yêu cầu kết bạn của người nhận (receiverId)
    public List<FriendRequestDTO> getFriendRequestsByReceiverId(int receiverId) {
        try {
            return friendRequestDAO.getReceivedRequestsByUserId(receiverId); // Sử dụng phương thức đã có trong DAO
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // Gửi lời mời kết bạn
    public boolean sendFriendRequest(FriendRequestDTO request) {
        try {
            return friendRequestDAO.sendFriendRequest(request);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Cập nhật trạng thái yêu cầu kết bạn
    public boolean updateFriendRequestStatus(int requestId, String status) {
        try {
            return friendRequestDAO.updateFriendRequestStatus(requestId, status);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Xóa yêu cầu kết bạn
    public boolean deleteFriendRequest(int requestId) {
        try {
            return friendRequestDAO.deleteFriendRequest(requestId);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Kiểm tra đã gửi lời mời kết bạn hay chưa
    public boolean hasSentRequest(int senderId, int receiverId) {
        try {
            return friendRequestDAO.hasSentRequest(senderId, receiverId);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Kiểm tra đã nhận yêu cầu kết bạn hay chưa
    public boolean hasReceivedRequest(int senderId, int receiverId) {
        try {
            return friendRequestDAO.hasReceivedRequest(senderId, receiverId);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Chấp nhận yêu cầu kết bạn
    public boolean acceptFriendRequest(int requestId) {
        try {
            // Cập nhật trạng thái là "accepted"
            return friendRequestDAO.updateFriendRequestStatus(requestId, "accepted");
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Từ chối yêu cầu kết bạn
    public boolean rejectFriendRequest(int requestId) {
        try {
            // Cập nhật trạng thái là "rejected"
            return friendRequestDAO.updateFriendRequestStatus(requestId, "rejected");
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
