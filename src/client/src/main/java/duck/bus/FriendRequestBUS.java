package duck.bus;

import duck.dao.FriendRequestDAO;
import duck.dto.FriendDTO;
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
    // Cập nhật trạng thái yêu cầu kết bạn và thêm vào bảng friends
    // Cập nhật trạng thái yêu cầu kết bạn và thêm vào bảng friends
    public boolean acceptFriendRequest(int requestId, int senderId, int receiverId) {
        try {
            // Cập nhật trạng thái yêu cầu là "accepted"
            boolean statusUpdated = friendRequestDAO.updateFriendRequestStatus(requestId, "accepted");
            
            if (statusUpdated) {
                // Sau khi yêu cầu kết bạn được chấp nhận, thêm vào bảng friends
                FriendBUS friendBUS = new FriendBUS();
                FriendDTO friend1 = new FriendDTO(senderId, receiverId); // Người gửi kết bạn
                FriendDTO friend2 = new FriendDTO(receiverId, senderId); // Người nhận kết bạn
                return friendBUS.addFriend(friend1) && friendBUS.addFriend(friend2);
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean rejectFriendRequest(int requestId) {
        try {
            // Cập nhật trạng thái yêu cầu là "rejected"
            return friendRequestDAO.updateFriendRequestStatus(requestId, "rejected");
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    
}
