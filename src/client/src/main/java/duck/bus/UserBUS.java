package duck.bus;

import duck.dao.UserDAO;
import duck.dto.UserDTO;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserBUS {
    private UserDAO userDAO;

    public UserBUS() {
        userDAO = new UserDAO();
    }

    // Tìm kiếm và lấy danh sách 
    public List<UserDTO> searchUsers(String filter, String sortBy, Boolean isActive) {
        try {
            return userDAO.getAllUsers(filter, sortBy, isActive);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>(); 
        }
    }

    public boolean addUser(UserDTO user) {
        try {
            return userDAO.addUser(user);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateUser(UserDTO user) {
        try {
            return userDAO.updateUser(user);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
