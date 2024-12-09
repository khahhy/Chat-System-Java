package duck.bus;

import duck.dao.UserDAO;
import duck.dto.FriendDTO;
import duck.dto.UserDTO;


import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

import java.util.List;
import java.util.Map;

public class UserBUS {
    private UserDAO userDAO;

    public UserBUS() {
        userDAO = new UserDAO();
    }

    // Tìm kiếm và lấy danh sách 
    public List<UserDTO> searchUsers(String filter, String sortBy, Boolean isOnline) {
        try {
            return userDAO.getAllUsers(filter, sortBy, isOnline);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>(); 
        }
    }

    public boolean addUser(UserDTO user) {
        try {
            return userDAO.addUser(user);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateUser(UserDTO user) {
        try {
            return userDAO.updateUser(user);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteUser(int userId) {
        try {
            return userDAO.deleteUser(userId);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    
    public boolean lockUnlockUser(int userId, boolean status) {
        try {
            return userDAO.lockUnlockUser(userId, status);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    
    public boolean updatePassword(int userId, String newPassword) {
        try {
            UserDTO user = userDAO.getAllUsers("", "user_id", null)
                    .stream()
                    .filter(u -> u.getUserId() == userId)
                    .findFirst()
                    .orElse(null);

            if (user == null) {
                return false; 
            }

            user.setPassword(newPassword);
            return userDAO.updateUser(user);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Map<String, Object>> getNewUsers(LocalDateTime startDate, LocalDateTime endDate) {
        try {
            return userDAO.getUserSignUp(startDate, endDate);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public UserDTO getUserByEmail(String email) {
        UserDAO userDAO = new UserDAO();
        return userDAO.getUserByEmail(email);
    }

    public List<FriendDTO> getFriendList(int userId) {
        try {
            return userDAO.getFriendsByUserId(userId);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public UserDTO getUserById(int userId) {
        try {
            return userDAO.getUserById(userId);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Map<String, Object>> getActivities(LocalDateTime startDate, LocalDateTime endDate) {
        try {
            return userDAO.getActivities(startDate, endDate);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }



}
