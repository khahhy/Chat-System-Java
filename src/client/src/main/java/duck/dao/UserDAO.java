package duck.dao;

import duck.dto.UserDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserDAO {

    // danh sách tất cả người dùng, lọc theo name, username, status, sắp xếp 
    public List<UserDTO> getAllUsers(String filter, String sortBy, Boolean isActive) throws SQLException {
        List<UserDTO> userList = new ArrayList<>();
        StringBuilder query = new StringBuilder("SELECT * FROM users WHERE (username LIKE ? OR full_name LIKE ?)");
    
        if (isActive != null) {
            query.append(" AND status = ?");
        }
    
        String[] validSortFields = {"username", "full_name", "created_at", "status"};
        if (Arrays.asList(validSortFields).contains(sortBy)) {
            query.append(" ORDER BY ").append(sortBy);
        } else {
            query.append(" ORDER BY username"); 
        }
    
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query.toString())) {
            stmt.setString(1, "%" + filter + "%");
            stmt.setString(2, "%" + filter + "%");
    
            if (isActive != null) {
                stmt.setBoolean(3, isActive); 
            }
    
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                userList.add(new UserDTO(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("full_name"),
                        rs.getString("address"),
                        rs.getTimestamp("date_of_birth").toLocalDateTime(),
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
            stmt.setTimestamp(4, Timestamp.valueOf(user.getDateOfBirth()));
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
        String query = "UPDATE users SET full_name = ?, address = ?, date_of_birth = ?, gender = ?, email = ?, password = ?, status = ?, is_online = ?, is_admin = ? WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, user.getFullName());
            stmt.setString(2, user.getAddress());
            stmt.setTimestamp(3, Timestamp.valueOf(user.getDateOfBirth()));
            stmt.setString(4, String.valueOf(user.getGender()));
            stmt.setString(5, user.getEmail());
            stmt.setString(6, user.getPassword());
            stmt.setBoolean(7, user.isStatus());
            stmt.setBoolean(8, user.isOnline());
            stmt.setBoolean(9, user.isAdmin());
            stmt.setInt(10, user.getUserId());
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
}
