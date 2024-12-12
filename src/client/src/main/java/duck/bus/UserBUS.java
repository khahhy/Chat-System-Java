    package duck.bus;

    import duck.dao.DatabaseConnection;
    import duck.dao.UserDAO;
    import duck.dto.UserDTO;

    import java.sql.Connection;
    import java.sql.PreparedStatement;
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

        /*public static List<FriendDTO> getFriendsByUserId(int userId) {
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
        }*/
        
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
        
        
        public List<UserDTO> getBlockedUsersByUserId(int userId) {
            try {
                return userDAO.getBlockedUsersByUserId(userId);
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        }
        
        public boolean checkExistEmail(String email) {
            return userDAO.checkExistEmail(email); 
        }

    }
