package duck.dao;

import duck.dto.GroupDTO;

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

    public GroupDTO getGroupById(int groupId) throws SQLException {
        GroupDTO group = null;
        String query = "SELECT * FROM groups WHERE group_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, groupId);  // Gán giá trị groupId vào câu truy vấn
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                group = new GroupDTO(
                        rs.getInt("group_id"),
                        rs.getString("group_name"),
                        rs.getTimestamp("created_at").toLocalDateTime()
                );
            }
        }
        return group;
    }
    

    public int addGroup(GroupDTO group) throws SQLException {
        String query = "INSERT INTO groups (group_name, created_at) VALUES (?, ?) RETURNING group_id";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, group.getGroupName());
            stmt.setTimestamp(2, Timestamp.valueOf(group.getCreatedAt()));
    
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("group_id"); 
                } else {
                    throw new SQLException("Failed to create group, no ID obtained.");
                }
            }
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

    public boolean updateGroup(GroupDTO group) throws SQLException {
        String query = "UPDATE groups SET group_name = ?, created_at = ? WHERE group_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, group.getGroupName());
            stmt.setTimestamp(2, Timestamp.valueOf(group.getCreatedAt()));
            stmt.setInt(3, group.getGroupId());
            return stmt.executeUpdate() > 0;
        }
    }

    // lay gr theo user id
    public List<GroupDTO> getGroupsByUserId(int userId) throws SQLException {
        List<GroupDTO> groups = new ArrayList<>();
        String query = """
            SELECT g.group_id, g.group_name, g.created_at
            FROM groups g
            INNER JOIN groupmembers gm ON g.group_id = gm.group_id
            WHERE gm.user_id = ? AND gm.is_approved = true
        """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
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
    
    public List<GroupDTO> getGroupRequestByUserId(int userId) throws SQLException {
        List<GroupDTO> groups = new ArrayList<>();
        String query = """
            SELECT g.group_id, g.group_name, g.created_at
            FROM groups g
            INNER JOIN groupmembers gm ON g.group_id = gm.group_id
            WHERE gm.user_id = ? AND gm.is_approved = false
        """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
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

}   
