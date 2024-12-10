package duck.bus;

import duck.dao.DatabaseConnection;
import duck.dao.FriendDAO;
import duck.dao.UserDAO;
import duck.dto.FriendDTO;
import duck.dto.UserDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

    public static List<UserDTO> getAllUsers() {
        List<UserDTO> users = new ArrayList<>();
        String query = "SELECT * FROM users";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                UserDTO user = new UserDTO(
                    resultSet.getInt("user_id"),
                    resultSet.getString("username"),
                    resultSet.getString("full_name"),
                    resultSet.getString("address"),
                    resultSet.getTimestamp("date_of_birth").toLocalDateTime(),
                    resultSet.getString("gender").charAt(0),
                    resultSet.getString("email"),
                    resultSet.getString("password"),
                    resultSet.getBoolean("status"),
                    resultSet.getBoolean("is_online"),
                    resultSet.getTimestamp("created_at").toLocalDateTime(),
                    resultSet.getBoolean("is_admin")
                );
                users.add(user);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return users;
    }

    public static List<FriendDTO> getFriendsByUserId(int userId) {
        List<FriendDTO> friends = new ArrayList<>();
        String query = "SELECT * FROM friends WHERE user_id = ?";
    
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
    
            preparedStatement.setInt(1, userId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    FriendDTO friend = new FriendDTO(
                        resultSet.getInt("user_id"),
                        resultSet.getInt("friend_id"),
                        resultSet.getBoolean("is_blocked"),
                        resultSet.getTimestamp("created_at").toLocalDateTime()
                    );
                    friends.add(friend);
                }
            }
    
        } catch (Exception e) {
            e.printStackTrace();
        }
    
        return friends;
    }
    
    public boolean removeFriend(int userId, int friendId) {
        try {
            // Gọi DAO hoặc logic để xóa bạn trong cơ sở dữ liệu
            FriendDAO friendDAO = new FriendDAO();
            return friendDAO.deleteFriend(userId, friendId);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean blockFriend(int userId, int friendId) {
        try {
            FriendDAO friendDAO = new FriendDAO();
            return friendDAO.blockFriend(userId, friendId);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean unblockFriend(int userId, int friendId) {
        String query = "UPDATE friends SET is_blocked = false WHERE user_id = ? AND friend_id = ?";
        
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
    
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, friendId);
    
            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0; // Trả về true nếu cập nhật thành công
    
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    
    public static List<UserDTO> getBlockedUsersByUserId(int userId) {
        List<UserDTO> blockedUsers = new ArrayList<>();
        String query = "SELECT u.* FROM users u "
                     + "JOIN friends f ON u.user_id = f.friend_id "
                     + "WHERE f.user_id = ? AND f.is_blocked = true";
    
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
    
            preparedStatement.setInt(1, userId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    UserDTO user = new UserDTO(
                        resultSet.getInt("user_id"),
                        resultSet.getString("username"),
                        resultSet.getString("full_name"),
                        resultSet.getString("address"),
                        resultSet.getTimestamp("date_of_birth").toLocalDateTime(),
                        resultSet.getString("gender").charAt(0),
                        resultSet.getString("email"),
                        resultSet.getString("password"),
                        resultSet.getBoolean("status"),
                        resultSet.getBoolean("is_online"),
                        resultSet.getTimestamp("created_at").toLocalDateTime(),
                        resultSet.getBoolean("is_admin")
                    );
                    blockedUsers.add(user);
                }
            }
    
        } catch (Exception e) {
            e.printStackTrace();
        }
    
        return blockedUsers;
    }
    
}
