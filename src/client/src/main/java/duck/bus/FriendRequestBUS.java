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

    // lời mời đã gửi
    public List<FriendRequestDTO> getSentRequestsByUserId(int senderId) {
        try {
            return friendRequestDAO.getSentRequestsByUserId(senderId);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();  
        }
    }

    // lời mời đã nhận
    public List<FriendRequestDTO> getReceivedRequestsByUserId(int receiverId) {
        try {
            return friendRequestDAO.getReceivedRequestsByUserId(receiverId);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();  
        }
    }

    // gửi lời mời 
    public boolean sendFriendRequest(FriendRequestDTO request) {
        try {
            return friendRequestDAO.sendFriendRequest(request);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;  
        }
    }

    // update status 
    public boolean updateFriendRequestStatus(int requestId, String status) {
        try {
            return friendRequestDAO.updateFriendRequestStatus(requestId, status);
        } catch (SQLException e) {
            e.printStackTrace();
            return false; 
        }
    }

    
    public boolean deleteFriendRequest(int requestId) {
        try {
            return friendRequestDAO.deleteFriendRequest(requestId);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;  
        }
    }

    // check xem đã gửi lời mời cho người khác chưa
    public boolean hasSentRequest(int senderId, int receiverId) {
        try {
            return friendRequestDAO.hasSentRequest(senderId, receiverId);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;  
        }
    }

    // check xem đã nhận lời mời từ người khác chưa
    public boolean hasReceivedRequest(int senderId, int receiverId) {
        try {
            return friendRequestDAO.hasReceivedRequest(senderId, receiverId);
        } catch (SQLException e) {
            e.printStackTrace();
            return false; 
        }
    }
}
