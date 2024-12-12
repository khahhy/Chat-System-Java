package duck.dao;

import duck.dto.GroupMemberDTO;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

import java.util.List;


public class GroupMemberDAO {
    public List<GroupMemberDTO> getMembersByGroupId(int groupId) throws SQLException {
        List<GroupMemberDTO> members = new ArrayList<>();
        String query = "SELECT * FROM GroupMembers WHERE group_id = ? AND is_approved = true";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, groupId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                members.add(new GroupMemberDTO(
                        rs.getInt("group_id"),
                        rs.getInt("user_id"),
                        rs.getBoolean("is_admin"),
                        rs.getTimestamp("joined_at").toLocalDateTime(),
                        rs.getBoolean("is_approved")
                ));
            }
        }
        return members;
    }

    public boolean addMember(GroupMemberDTO member) throws SQLException {
        String query = "INSERT INTO GroupMembers (group_id, user_id, is_admin, joined_at, is_approved) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, member.getGroupId());
            stmt.setInt(2, member.getUserId());
            stmt.setBoolean(3, member.isAdmin());
            
            if (member.getJoinedAt() != null) {
                stmt.setTimestamp(4, Timestamp.valueOf(member.getJoinedAt()));
            } else {
                stmt.setNull(4, Types.TIMESTAMP); 
            }
    
            stmt.setBoolean(5, member.isApproved()); 
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean approveMember(int groupId, int userId) throws SQLException {
        String query = "UPDATE GroupMembers SET is_approved = true, joined_at = ? WHERE group_id = ? AND user_id = ? AND is_approved = false";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now())); 
            stmt.setInt(2, groupId);
            stmt.setInt(3, userId);
            return stmt.executeUpdate() > 0;
        }
    }

    

    public boolean removeMember(int groupId, int userId) throws SQLException {
        String query = "DELETE FROM GroupMembers WHERE group_id = ? AND user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, groupId);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        }
    }

    // Cập nhật quyền admin của một thành viên trong nhóm
    public boolean updateMemberAdminStatus(int groupId, int userId, boolean isAdmin) throws SQLException {
        String query = "UPDATE GroupMembers SET is_admin = ? WHERE group_id = ? AND user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setBoolean(1, isAdmin);
            stmt.setInt(2, groupId);
            stmt.setInt(3, userId);
            return stmt.executeUpdate() > 0;
        }
    }

    public List<String> getAdmin(int groupId) throws SQLException {
        List<String> mems = new ArrayList<>();
        String query = "SELECT gm.user_id, u.full_name, gm.joined_at " +
                   "FROM GroupMembers gm " +
                   "JOIN users u ON gm.user_id = u.user_id " +
                   "WHERE gm.group_id = ? AND gm.is_admin = true";

        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, groupId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                mems.add(rs.getString("full_name"));
            }
        }
        return mems;
    }

    public List<Integer> getAdminID(int groupId) throws SQLException {
        List<Integer> list = new ArrayList<Integer>();
        String query = "SELECT gm.user_id, u.full_name, gm.joined_at " +
                   "FROM GroupMembers gm " +
                   "JOIN users u ON gm.user_id = u.user_id " +
                   "WHERE gm.group_id = ? AND gm.is_admin = true";

        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, groupId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(rs.getInt("user_id"));
            }
        }
        return list;
    }

    public List<String> getMem(int groupId) throws SQLException {
        List<String> mems = new ArrayList<>();
        String query = "SELECT gm.user_id, u.full_name, gm.joined_at " +
                   "FROM GroupMembers gm " +
                   "JOIN users u ON gm.user_id = u.user_id " +
                   "WHERE gm.group_id = ? AND gm.is_admin = false AND gm.is_approved = true";

        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, groupId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                mems.add(rs.getString("full_name"));
            }
        }
        return mems;
    }

}
