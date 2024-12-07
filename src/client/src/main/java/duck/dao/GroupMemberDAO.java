package duck.dao;

import duck.dto.GroupMemberDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GroupMemberDAO {
    public List<GroupMemberDTO> getMembersByGroupId(int groupId) throws SQLException {
        List<GroupMemberDTO> members = new ArrayList<>();
        String query = "SELECT * FROM group_members WHERE group_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, groupId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                members.add(new GroupMemberDTO(
                        rs.getInt("group_id"),
                        rs.getInt("user_id"),
                        rs.getBoolean("is_admin"),
                        rs.getTimestamp("joined_at").toLocalDateTime()
                ));
            }
        }
        return members;
    }

    public boolean addMember(GroupMemberDTO member) throws SQLException {
        String query = "INSERT INTO group_members (group_id, user_id, is_admin, joined_at) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, member.getGroupId());
            stmt.setInt(2, member.getUserId());
            stmt.setBoolean(3, member.isAdmin());
            stmt.setTimestamp(4, Timestamp.valueOf(member.getJoinedAt()));
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean removeMember(int groupId, int userId) throws SQLException {
        String query = "DELETE FROM group_members WHERE group_id = ? AND user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, groupId);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        }
    }

    // Cập nhật quyền admin của một thành viên trong nhóm
    public boolean updateMemberAdminStatus(int groupId, int userId, boolean isAdmin) throws SQLException {
        String query = "UPDATE group_members SET is_admin = ? WHERE group_id = ? AND user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setBoolean(1, isAdmin);
            stmt.setInt(2, groupId);
            stmt.setInt(3, userId);
            return stmt.executeUpdate() > 0;
        }
    }
}
