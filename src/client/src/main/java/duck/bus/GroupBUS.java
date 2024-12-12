package duck.bus;

import duck.dao.GroupDAO;
import duck.dto.GroupDTO;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GroupBUS {
    private GroupDAO groupDAO;

    public GroupBUS() {
        groupDAO = new GroupDAO();
    }

    public List<GroupDTO> getAllGroups() {
        try {
            return groupDAO.getAllGroups();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public GroupDTO getGroupById(int group_id) {
        try {
            return groupDAO.getGroupById(group_id);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public int addGroup(GroupDTO group) {
        try {
            return groupDAO.addGroup(group);
        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm nhóm: " + e.getMessage());
            return -1; 
        }
    }

    public boolean updateGroup(GroupDTO group) {
        try {
            return groupDAO.updateGroup(group);
        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm nhóm: " + e.getMessage());
            return false; 
        }
    }
    

    public boolean deleteGroup(int groupId) {
        try {
            return groupDAO.deleteGroup(groupId);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<GroupDTO> getAllGroupsByUserId(int user_id) {
        try {
            return groupDAO.getGroupsByUserId(user_id);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<GroupDTO> getAllGroupRequestByUserId(int user_id) {
        try {
            return groupDAO.getGroupRequestByUserId(user_id);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
