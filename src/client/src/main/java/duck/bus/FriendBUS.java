package duck.bus;

import duck.dao.FriendDAO;
import duck.dao.UserDAO;
import duck.dto.FriendDTO;
import duck.dto.UserDTO;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import java.util.HashMap;
import java.util.Map;

public class FriendBUS {
    private FriendDAO friendDAO;
    private UserDAO userDAO;

    public FriendBUS() {
        friendDAO = new FriendDAO();
        userDAO = new UserDAO();
    }

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

    public List<Map<String, Object>> getFriendDetails() {
        try {
            List<UserDTO> users = userDAO.getAllUsers("", "", true);
            System.out.println(users.size());
            List<Map<String, Object>> friend_detail = new ArrayList<>();

            for (UserDTO user : users) {
                Map<String, Object> record = new HashMap<>();

                record.put("username", user.getUsername());
                record.put("fullname", user.getFullName());
                record.put("totalFriend", friendDAO.getTotalFriends(user.getUserId()));
                record.put("totalFrOfFr", friendDAO.getTotalFriendsOfFriends(user.getUserId()));
                record.put("createdAt", user.getCreatedAt());

                friend_detail.add(record);
            }
       
            return friend_detail;
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
