package duck.dao;

import duck.dto.GroupDTO;
import duck.dto.GroupMemberDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GroupDAO {

    // Lấy tất cả nhóm
    public List<GroupDTO> getAllGroups() throws SQLException {
        List<GroupDTO> groups = new ArrayList<>();
        String query = "SELECT * FROM groups";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                groups.add(new GroupDTO(
                        rs.getInt("group_id"),
                        rs.getString("group_name"),
                        rs.getTimestamp("created_at").toLocalDateTime()
                ));
            }
        }
        return groups;
    }

    // Thêm một nhóm mới
    public boolean addGroup(GroupDTO group) throws SQLException {
        String query = "INSERT INTO groups (group_name, created_at) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, group.getGroupName());
            stmt.setTimestamp(2, Timestamp.valueOf(group.getCreatedAt()));
            return stmt.executeUpdate() > 0;
        }
    }

    // Xóa một nhóm
    public boolean deleteGroup(int groupId) throws SQLException {
        String query = "DELETE FROM groups WHERE group_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, groupId);
            return stmt.executeUpdate() > 0;
        }
    }

}
