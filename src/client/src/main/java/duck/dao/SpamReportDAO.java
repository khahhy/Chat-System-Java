package duck.dao;

import duck.dto.SpamReportDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SpamReportDAO {

    // tất cả báo cáo spam của một user
    public List<SpamReportDTO> getReportsByReporterId(int reporterId) throws SQLException {
        List<SpamReportDTO> reports = new ArrayList<>();
        String query = "SELECT * FROM spam_reports WHERE reporter_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, reporterId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                reports.add(new SpamReportDTO(
                        rs.getInt("report_id"),
                        rs.getInt("reporter_id"),
                        rs.getInt("reported_id"),
                        rs.getString("reason"),
                        rs.getTimestamp("created_at").toLocalDateTime()
                ));
            }
        }
        return reports;
    }

    // Lấy tất cả báo cáo spam của một người bị báo cáo
    public List<SpamReportDTO> getReportsByReportedId(int reportedId) throws SQLException {
        List<SpamReportDTO> reports = new ArrayList<>();
        String query = "SELECT * FROM spam_reports WHERE reported_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, reportedId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                reports.add(new SpamReportDTO(
                        rs.getInt("report_id"),
                        rs.getInt("reporter_id"),
                        rs.getInt("reported_id"),
                        rs.getString("reason"),
                        rs.getTimestamp("created_at").toLocalDateTime()
                ));
            }
        }
        return reports;
    }

    // Thêm một báo cáo spam mới
    public boolean addSpamReport(SpamReportDTO report) throws SQLException {
        String query = "INSERT INTO spam_reports (reporter_id, reported_id, reason, created_at) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, report.getReporterId());
            stmt.setInt(2, report.getReportedId());
            stmt.setString(3, report.getReason());
            stmt.setTimestamp(4, Timestamp.valueOf(report.getCreatedAt()));
            return stmt.executeUpdate() > 0;
        }
    }

    // Xóa một báo cáo spam theo reportId
    public boolean deleteSpamReport(int reportId) throws SQLException {
        String query = "DELETE FROM spam_reports WHERE report_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, reportId);
            return stmt.executeUpdate() > 0;
        }
    }

    // Xóa tất cả báo cáo spam của một người bị báo cáo
    public boolean deleteReportsByReportedId(int reportedId) throws SQLException {
        String query = "DELETE FROM spam_reports WHERE reported_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, reportedId);
            return stmt.executeUpdate() > 0;
        }
    }

    // Xóa tất cả báo cáo spam của một người báo cáo
    public boolean deleteReportsByReporterId(int reporterId) throws SQLException {
        String query = "DELETE FROM spam_reports WHERE reporter_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, reporterId);
            return stmt.executeUpdate() > 0;
        }
    }
}
