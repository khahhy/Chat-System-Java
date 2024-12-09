package duck.bus;

import duck.dao.LoginHistoryDAO;
import duck.dto.LoginHistoryDTO;
import duck.dao.UserDAO;
import duck.dto.UserDTO;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

import java.util.HashMap;
import java.util.Map;

public class LoginHistoryBUS {
    private LoginHistoryDAO loginHistoryDAO;
    private UserDAO userDAO;

    public LoginHistoryBUS() {
        loginHistoryDAO = new LoginHistoryDAO();
        userDAO = new UserDAO();
    }

    // 1 user
    public List<LoginHistoryDTO> getLoginHistoryByUserId(int userId) {
        try {
            return loginHistoryDAO.getLoginHistoryByUserId(userId);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();  
        }
    }

    // feature của admin: giờ, username, fullname
    public List<Map<String, Object>> getAllLoginHistory() {
        try {
            List<LoginHistoryDTO> histories = loginHistoryDAO.getAllLoginHistory();
            List<Map<String, Object>> history_detail = new ArrayList<>();

            for (LoginHistoryDTO item : histories) {
                Map<String, Object> record = new HashMap<>();
                UserDTO user = userDAO.getUserById(item.getUserId());
                record.put("userid", item.getUserId());
                record.put("loginTime", item.getLoginTime());
                record.put("username", user.getUsername());
                record.put("fullname", user.getFullName());

                history_detail.add(record);
            }

            return history_detail;

        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();  
        }
    }

    public boolean addLoginHistory(LoginHistoryDTO loginHistory) {
        try {
            return loginHistoryDAO.addLoginHistory(loginHistory);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;  
        }
    }

    public boolean updateLogoutTime(int historyId, LocalDateTime logoutTime) {
        try {
            return loginHistoryDAO.updateLogoutTime(historyId, logoutTime);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;  
        }
    }

   
    public boolean deleteLoginHistory(int historyId) {
        try {
            return loginHistoryDAO.deleteLoginHistory(historyId);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;  
        }
    }
}
