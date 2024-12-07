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

    public boolean addGroup(GroupDTO group) {
        try {
            return groupDAO.addGroup(group);
        } catch (SQLException e) {
            e.printStackTrace();
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
}
