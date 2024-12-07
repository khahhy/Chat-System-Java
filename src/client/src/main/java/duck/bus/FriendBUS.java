package duck.bus;

import duck.dao.FriendDAO;
import duck.dto.FriendDTO;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FriendBUS {
    private FriendDAO friendDAO;

    public FriendBUS() {
        friendDAO = new FriendDAO();
    }

    // danh sách bạn bè của user
    public List<FriendDTO> getFriendsByUserId(int userId) {
        try {
            return friendDAO.getFriendsByUserId(userId);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();  
        }
    }

    public boolean addFriend(FriendDTO friend) {
        try {
            return friendDAO.addFriend(friend);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;  
        }
    }

    public boolean updateFriend(FriendDTO friend) {
        try {
            return friendDAO.updateFriend(friend);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;  
        }
    }

    public boolean deleteFriend(int userId, int friendId) {
        try {
            return friendDAO.deleteFriend(userId, friendId);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;  
        }
    }

    public boolean isFriend(int userId, int friendId) {
        try {
            return friendDAO.isFriend(userId, friendId);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;  
        }
    }
}
