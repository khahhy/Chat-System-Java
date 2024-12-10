package duck.dao;

import duck.dto.LoginHistoryDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

public class LoginHistoryDAO {

    // 1 user
    public List<LoginHistoryDTO> getLoginHistoryByUserId(int userId) throws SQLException {
        List<LoginHistoryDTO> loginHistoryList = new ArrayList<>();
        String query = "SELECT lh.history_id, lh.user_id, lh.login_time, lh.logout_time " +
                       "FROM LoginHistory lh " +
                       "WHERE lh.user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                loginHistoryList.add(new LoginHistoryDTO(
                        rs.getInt("history_id"),
                        rs.getInt("user_id"),
                        rs.getTimestamp("login_time").toLocalDateTime(),
                        rs.getTimestamp("logout_time") != null ? rs.getTimestamp("logout_time").toLocalDateTime() : null
                ));
            }
        }
        return loginHistoryList;
    }

    // tất cả user
    public List<LoginHistoryDTO> getAllLoginHistory() throws SQLException {
        List<LoginHistoryDTO> loginHistoryList = new ArrayList<>();
        String query = "SELECT lh.history_id, lh.user_id, lh.login_time, lh.logout_time " +
                       "FROM LoginHistory lh " +
                       "ORDER BY lh.login_time";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                loginHistoryList.add(new LoginHistoryDTO(
                        rs.getInt("history_id"),
                        rs.getInt("user_id"),
                        rs.getTimestamp("login_time").toLocalDateTime(),
                        rs.getTimestamp("logout_time") != null ? rs.getTimestamp("logout_time").toLocalDateTime() : null
                ));
            }
        }
        return loginHistoryList;
    }

    public boolean addLoginHistory(LoginHistoryDTO loginHistory) throws SQLException {
        String query = "INSERT INTO LoginHistory (user_id, login_time, logout_time) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, loginHistory.getUserId());
            stmt.setTimestamp(2, Timestamp.valueOf(loginHistory.getLoginTime()));
            if (loginHistory.getLogoutTime() != null) {
                stmt.setTimestamp(3, Timestamp.valueOf(loginHistory.getLogoutTime()));
            } else {
                stmt.setNull(3, Types.TIMESTAMP);  
            }
            return stmt.executeUpdate() > 0;
        }
    }

    
    public boolean updateLogoutTime(int userId, LocalDateTime logoutTime) throws SQLException {
        String query = "UPDATE LoginHistory " +
                   "SET logout_time = ? " +
                   "WHERE history_id = (" +
                   "    SELECT history_id " +
                   "    FROM LoginHistory " +
                   "    WHERE user_id = ? " +
                   "    ORDER BY login_time DESC LIMIT 1" +
                   ")";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setTimestamp(1, Timestamp.valueOf(logoutTime));
                stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        }
    }

   
    public boolean deleteLoginHistory(int historyId) throws SQLException {
        String query = "DELETE FROM LoginHistory WHERE history_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, historyId);
            return stmt.executeUpdate() > 0;
        }
    }

    
}
