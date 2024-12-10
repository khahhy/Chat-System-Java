package duck.dao;

import duck.dto.UserDTO;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserDAO {

    // danh sách tất cả người dùng, lọc theo name, username, online, sắp xếp 
    public List<UserDTO> getAllUsers(String filter, String sortBy, Boolean isOnline) throws SQLException {
        List<UserDTO> userList = new ArrayList<>();
        StringBuilder query = new StringBuilder("SELECT * FROM users WHERE (username LIKE ? OR full_name LIKE ?)");
    
        if (isOnline != null) {
            query.append(" AND is_online = ?");
        }
    
        String[] validSortFields = {"username", "full_name", "created_at", "is_online"};
        if (Arrays.asList(validSortFields).contains(sortBy)) {
            query.append(" ORDER BY ").append(sortBy);
        } else {
            query.append(" ORDER BY username"); 
        }
    
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query.toString())) {
            stmt.setString(1, "%" + filter + "%");
            stmt.setString(2, "%" + filter + "%");
    
            if (isOnline != null) {
                stmt.setBoolean(3, isOnline); 
            }
    
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                LocalDateTime dateOfBirth = null;
                Timestamp dobTimestamp = rs.getTimestamp("date_of_birth");
                if (dobTimestamp != null) {
                    dateOfBirth = dobTimestamp.toLocalDateTime();
                }
                userList.add(new UserDTO(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("full_name"),
                        rs.getString("address"),
                        dateOfBirth,
                        rs.getString("gender").charAt(0),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getBoolean("status"),
                        rs.getBoolean("is_online"),
                        rs.getTimestamp("created_at").toLocalDateTime(),
                        rs.getBoolean("is_admin") 
                ));
            }
        }
        return userList;
    }
    

    public boolean addUser(UserDTO user) throws SQLException {
        String query = "INSERT INTO users (username, full_name, address, date_of_birth, gender, email, password, status, is_online, is_admin) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getFullName());
            stmt.setString(3, user.getAddress());
            if (user.getDateOfBirth() != null) {
                stmt.setTimestamp(4, Timestamp.valueOf(user.getDateOfBirth()));
            } else {
                stmt.setNull(4, Types.TIMESTAMP);
            }
            stmt.setString(5, String.valueOf(user.getGender()));
            stmt.setString(6, user.getEmail());
            stmt.setString(7, user.getPassword());
            stmt.setBoolean(8, user.isStatus());
            stmt.setBoolean(9, user.isOnline());
            stmt.setBoolean(10, user.isAdmin()); 
            return stmt.executeUpdate() > 0;
        }
    }

  
    public boolean updateUser(UserDTO user) throws SQLException {
        String query = "UPDATE users SET full_name = ?, address = ?, date_of_birth = ?, gender = ?, email = ?, password = ?, status = ?, is_online = ?, is_admin = ?, username = ? WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, user.getFullName());
            stmt.setString(2, user.getAddress());
            if (user.getDateOfBirth() != null) {
                stmt.setTimestamp(3, Timestamp.valueOf(user.getDateOfBirth()));
            } else {
                stmt.setNull(3, Types.TIMESTAMP);
            }
            stmt.setString(4, String.valueOf(user.getGender()));
            stmt.setString(5, user.getEmail());
            stmt.setString(6, user.getPassword());
            stmt.setBoolean(7, user.isStatus());
            stmt.setBoolean(8, user.isOnline());
            stmt.setBoolean(9, user.isAdmin());
            stmt.setString(10, user.getUsername());
            stmt.setInt(11, user.getUserId());
            return stmt.executeUpdate() > 0;
        }
    }

   
    public boolean deleteUser(int userId) throws SQLException {
        String query = "DELETE FROM users WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            return stmt.executeUpdate() > 0;
        }
    }

    
    public boolean lockUnlockUser(int userId, boolean status) throws SQLException {
        String query = "UPDATE users SET status = ? WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setBoolean(1, status);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        }
    }

   
    public UserDTO getUserById(int userId) throws SQLException {
        String query = "SELECT * FROM users WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new UserDTO(
                    rs.getInt("user_id"),
                    rs.getString("username"),
                    rs.getString("full_name"),
                    rs.getString("address"),
                    rs.getTimestamp("date_of_birth") != null ? rs.getTimestamp("date_of_birth").toLocalDateTime() : null,
                    rs.getString("gender").charAt(0),
                    rs.getString("email"),
                    rs.getString("password"),
                    rs.getBoolean("status"),
                    rs.getBoolean("is_online"),
                    rs.getTimestamp("created_at").toLocalDateTime(),
                    rs.getBoolean("is_admin")
                );
            }
        }
        return null; 
    }

    public List<Map<String, Object>> getUserSignUp(LocalDateTime startDate, LocalDateTime endDate) throws SQLException {
        List<Map<String, Object>> userList = new ArrayList<>();
        String query;
        
        if (startDate == null && endDate == null) {
            query = "SELECT username, full_name, created_at FROM users";
        } else if (startDate == null) {
            query = "SELECT username, full_name, created_at FROM users WHERE created_at <= ?";
        } else if (endDate == null) {
            query = "SELECT username, full_name, created_at FROM users WHERE created_at >= ?";
        } else {
            query = "SELECT username, full_name, created_at FROM users WHERE created_at BETWEEN ? AND ?";
        }
    
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            if (startDate != null && endDate != null) {
                stmt.setTimestamp(1, Timestamp.valueOf(startDate));
                stmt.setTimestamp(2, Timestamp.valueOf(endDate));
            } else if (startDate != null) {
                stmt.setTimestamp(1, Timestamp.valueOf(startDate));
            } else if (endDate != null) {
                stmt.setTimestamp(1, Timestamp.valueOf(endDate));
            }
    
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Map<String, Object> user = new HashMap<>();
                user.put("username", rs.getString("username"));
                user.put("fullname", rs.getString("full_name"));
                user.put("createdAt", rs.getTimestamp("created_at").toLocalDateTime());
                userList.add(user);
            }
        }
    
        return userList;
    }
    

    public UserDTO getUserByEmail(String email) {
        UserDTO user = null;
        
        // Kết nối cơ sở dữ liệu
        try (Connection connection = DatabaseConnection.getConnection()) {
            // Truy vấn SQL để tìm người dùng theo email
            String query = "SELECT * FROM Users WHERE email = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, email);  // Set email vào câu truy vấn
                ResultSet resultSet = statement.executeQuery();
                
                // Nếu tìm thấy người dùng, khởi tạo đối tượng UserDTO
                if (resultSet.next()) {
                    int userId = resultSet.getInt("user_id");
                    String username = resultSet.getString("username");
                    String fullName = resultSet.getString("full_name");
                    String address = resultSet.getString("address");
                    
                    // Kiểm tra và xử lý trường hợp date_of_birth và created_at có thể null
                    LocalDateTime dateOfBirth = null;
                    if (resultSet.getTimestamp("date_of_birth") != null) {
                        dateOfBirth = resultSet.getTimestamp("date_of_birth").toLocalDateTime();
                    }
                    
                    char gender = resultSet.getString("gender").charAt(0);
                    String password = resultSet.getString("password");
                    boolean status = resultSet.getBoolean("status");
                    boolean isOnline = resultSet.getBoolean("is_online");
                    
                    LocalDateTime createdAt = null;
                    if (resultSet.getTimestamp("created_at") != null) {
                        createdAt = resultSet.getTimestamp("created_at").toLocalDateTime();
                    }
                    
                    boolean isAdmin = resultSet.getBoolean("is_admin");
                    
                    // Tạo đối tượng UserDTO
                    user = new UserDTO(userId, username, fullName, address, dateOfBirth, gender, email, password, status, isOnline, createdAt, isAdmin);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Xử lý lỗi kết nối cơ sở dữ liệu
        }
        
        return user;  // Trả về đối tượng UserDTO nếu tìm thấy, hoặc null nếu không tìm thấy
    }
    
    public boolean updatePasswordByEmail(String email, String newPassword) {
        // Kết nối với cơ sở dữ liệu và thực hiện câu lệnh SQL để cập nhật mật khẩu
        String updateQuery = "UPDATE users SET password = ? WHERE email = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {

            preparedStatement.setString(1, newPassword);  // Đặt mật khẩu mới
            preparedStatement.setString(2, email);  // Đặt email của người dùng

            int rowsUpdated = preparedStatement.executeUpdate();
            return rowsUpdated > 0;  // Trả về true nếu cập nhật thành công

        } catch (SQLException e) {
            e.printStackTrace();
            return false;  // Trả về false nếu có lỗi
        }
    }

    public boolean checkOldPassword(int userId, String oldPassword) {
        // Truy vấn cơ sở dữ liệu để lấy mật khẩu cũ của người dùng
        String query = "SELECT password FROM users WHERE user_id = ?";
        
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            ResultSet rs = statement.executeQuery();
            
            if (rs.next()) {
                String currentPassword = rs.getString("password");
                return currentPassword.equals(oldPassword);  // So sánh mật khẩu cũ
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;  // Nếu không tìm thấy người dùng hoặc mật khẩu không khớp
    }

    // Phương thức cập nhật mật khẩu mới của người dùng
    public boolean updatePassword(int userId, String newPassword) {
        String updateQuery = "UPDATE users SET password = ? WHERE user_id = ?";
        
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
            preparedStatement.setString(1, newPassword);
            preparedStatement.setInt(2, userId);

            int rowsUpdated = preparedStatement.executeUpdate();
            return rowsUpdated > 0;  // Trả về true nếu cập nhật thành công
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;  // Nếu có lỗi khi cập nhật
    }

    public List<Map<String, Object>> getActivities(LocalDateTime startDate, LocalDateTime endDate) throws SQLException {
        List<Map<String, Object>> activityList = new ArrayList<>();
        
        String query = "SELECT u.username, u.full_name, u.created_at, " +
               "       COALESCE(lh.login_count, 0) AS logins, " +
               "       COALESCE(mu.chat_user_count, 0) AS chatUsers, " +
               "       COALESCE(mg.chat_group_count, 0) AS chatGroups, " +
               "       (COALESCE(lh.login_count, 0) + COALESCE(mu.chat_user_count, 0) + COALESCE(mg.chat_group_count, 0)) AS totalActivities " +
               "FROM users u " +
               "LEFT JOIN ( " +
               "    SELECT user_id, COUNT(*) AS login_count " +
               "    FROM LoginHistory " +
               "    WHERE (CAST(? AS TIMESTAMP) IS NULL OR login_time >= CAST(? AS TIMESTAMP)) " +
               "      AND (CAST(? AS TIMESTAMP) IS NULL OR login_time <= CAST(? AS TIMESTAMP)) " +
               "    GROUP BY user_id " +
               ") lh ON u.user_id = lh.user_id " +
               "LEFT JOIN ( " +
               "    SELECT sender_id AS user_id, COUNT(DISTINCT receiver_id) AS chat_user_count " +
               "    FROM messages " +
               "    WHERE receiver_id IS NOT NULL AND (CAST(? AS TIMESTAMP) IS NULL OR timestamp >= CAST(? AS TIMESTAMP)) " +
               "      AND (CAST(? AS TIMESTAMP) IS NULL OR timestamp <= CAST(? AS TIMESTAMP)) " +
               "    GROUP BY sender_id " +
               ") mu ON u.user_id = mu.user_id " +
               "LEFT JOIN ( " +
               "    SELECT sender_id AS user_id, COUNT(DISTINCT group_id) AS chat_group_count " +
               "    FROM messages " +
               "    WHERE group_id IS NOT NULL AND (CAST(? AS TIMESTAMP) IS NULL OR timestamp >= CAST(? AS TIMESTAMP)) " +
               "      AND (CAST(? AS TIMESTAMP) IS NULL OR timestamp <= CAST(? AS TIMESTAMP)) " +
               "    GROUP BY sender_id " +
               ") mg ON u.user_id = mg.user_id";

    
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            if (startDate == null) {
                stmt.setNull(1, Types.TIMESTAMP);
                stmt.setNull(2, Types.TIMESTAMP);
            } else {
                stmt.setTimestamp(1, Timestamp.valueOf(startDate));
                stmt.setTimestamp(2, Timestamp.valueOf(startDate));
            }
            if (endDate == null) {
                stmt.setNull(3, Types.TIMESTAMP);
                stmt.setNull(4, Types.TIMESTAMP);
            } else {
                stmt.setTimestamp(3, Timestamp.valueOf(endDate));
                stmt.setTimestamp(4, Timestamp.valueOf(endDate));
            }
          
            if (startDate == null) {
                stmt.setNull(5, Types.TIMESTAMP);
                stmt.setNull(6, Types.TIMESTAMP);
            } else {
                stmt.setTimestamp(5, Timestamp.valueOf(startDate));
                stmt.setTimestamp(6, Timestamp.valueOf(startDate));
            }
          
            if (endDate == null) {
                stmt.setNull(7, Types.TIMESTAMP);
                stmt.setNull(8, Types.TIMESTAMP);
            } else {
                stmt.setTimestamp(7, Timestamp.valueOf(endDate));
                stmt.setTimestamp(8, Timestamp.valueOf(endDate));
            }
          
            if (startDate == null) {
                stmt.setNull(9, Types.TIMESTAMP);
                stmt.setNull(10, Types.TIMESTAMP);
            } else {
                stmt.setTimestamp(9, Timestamp.valueOf(startDate));
                stmt.setTimestamp(10, Timestamp.valueOf(startDate));
            }
          
            if (endDate == null) {
                stmt.setNull(11, Types.TIMESTAMP);
                stmt.setNull(12, Types.TIMESTAMP);
            } else {
                stmt.setTimestamp(11, Timestamp.valueOf(endDate));
                stmt.setTimestamp(12, Timestamp.valueOf(endDate));
            }
          
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Map<String, Object> activity = new HashMap<>();
                activity.put("username", rs.getString("username"));
                activity.put("fullname", rs.getString("full_name"));
                activity.put("logins", rs.getInt("logins"));
                activity.put("chatUsers", rs.getInt("chatUsers"));
                activity.put("chatGroups", rs.getInt("chatGroups"));
                activity.put("createdAt", rs.getTimestamp("created_at").toLocalDateTime());
                activity.put("totalActivities", rs.getInt("totalActivities"));
          
                activityList.add(activity);
            }
        }
        return activityList;
    }

    public List<UserDTO> getBlockedUsersByUserId(int userId) throws SQLException {
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
    
        } 
        return blockedUsers;
    }
    
}
