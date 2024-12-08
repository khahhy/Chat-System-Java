package duck.dao;

import duck.dto.SpamReportDTO;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SpamReportDAO {
    public List<SpamReportDTO> getSpamReports(String sortBy, LocalDateTime startTime, LocalDateTime endTime, String usernameFilter) throws SQLException {
        List<SpamReportDTO> spamReportList = new ArrayList<>();
        StringBuilder query = new StringBuilder(
                "SELECT sr.report_id, sr.reporter_id, sr.reported_id, sr.reason, sr.created_at, d.username AS reported_username " +
                "FROM SpamReports sr " +
                "JOIN users d ON sr.reported_id = d.user_id " +
                "WHERE 1=1");

        
        if (startTime != null && endTime != null) {
            query.append(" AND sr.created_at BETWEEN ? AND ?");
        }

        
        if (usernameFilter != null && !usernameFilter.isEmpty()) {
            query.append(" AND d.username LIKE ?");
        }

        
        if ("time".equalsIgnoreCase(sortBy)) {
            query.append(" ORDER BY sr.created_at DESC");
        } else if ("username".equalsIgnoreCase(sortBy)) {
            query.append(" ORDER BY d.username");
        } else {
            query.append(" ORDER BY sr.created_at DESC"); 
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query.toString())) {

            int paramIndex = 1;

            if (startTime != null && endTime != null) {
                stmt.setTimestamp(paramIndex++, Timestamp.valueOf(startTime));
                stmt.setTimestamp(paramIndex++, Timestamp.valueOf(endTime));
            }

            if (usernameFilter != null && !usernameFilter.isEmpty()) {
                stmt.setString(paramIndex++, "%" + usernameFilter + "%");
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                spamReportList.add(new SpamReportDTO(
                        rs.getInt("report_id"),
                        rs.getInt("reporter_id"),
                        rs.getInt("reported_id"),
                        rs.getString("reason"),
                        rs.getTimestamp("created_at").toLocalDateTime()
                ));
            }
        }

        return spamReportList;
    }

    
    public boolean lockUser(int userId) throws SQLException {
        String lockUserQuery = "UPDATE users SET status = false WHERE user_id = ?";
        String deleteReportsQuery = "DELETE FROM SpamReports WHERE reported_id = ?";
    
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement lockStmt = conn.prepareStatement(lockUserQuery);
             PreparedStatement deleteStmt = conn.prepareStatement(deleteReportsQuery)) {
    
            conn.setAutoCommit(false);
    
            lockStmt.setInt(1, userId);
            int lockResult = lockStmt.executeUpdate();
    
            deleteStmt.setInt(1, userId);
            int deleteResult = deleteStmt.executeUpdate();
            conn.commit();
    
            return lockResult > 0 && deleteResult >= 0;
        } catch (SQLException e) {
            e.printStackTrace();
            try (Connection conn = DatabaseConnection.getConnection()) {
                conn.rollback();
            }
            throw e;
        }
    }
    
}
