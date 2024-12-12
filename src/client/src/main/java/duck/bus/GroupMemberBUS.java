package duck.bus;

import duck.dao.GroupMemberDAO;
import duck.dto.GroupMemberDTO;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GroupMemberBUS {
    private GroupMemberDAO groupMemberDAO;

    public GroupMemberBUS() {
        groupMemberDAO = new GroupMemberDAO();
    }

    // thành viên trong nhóm
    public List<GroupMemberDTO> getMembersByGroupId(int groupId) {
        try {
            return groupMemberDAO.getMembersByGroupId(groupId);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // thêm thành viên 
    public boolean addMember(GroupMemberDTO member) {
        try {
            return groupMemberDAO.addMember(member);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean approveMember(int groupId, int userId) {
        try {
            return groupMemberDAO.approveMember(groupId, userId);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // xóa thành viên 
    public boolean removeMember(int groupId, int userId) {
        try {
            return groupMemberDAO.removeMember(groupId, userId);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // update admin
    public boolean updateMemberAdminStatus(int groupId, int userId, boolean isAdmin) {
        try {
            return groupMemberDAO.updateMemberAdminStatus(groupId, userId, isAdmin);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Integer> getAdminId(int groupId) {
        try {
            return groupMemberDAO.getAdminID(groupId);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>(); 
        }
    }
    
    public List<String> getAdmin(int groupId) {
        try {
            return groupMemberDAO.getAdmin(groupId);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>(); 
        }
    }

    public List<String> getMem(int groupId) {
        try {
            return groupMemberDAO.getMem(groupId);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>(); 
        }
    }
}
