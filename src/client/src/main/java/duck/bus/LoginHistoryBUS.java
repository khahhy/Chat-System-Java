package duck.bus;

import duck.dao.LoginHistoryDAO;
import duck.dto.LoginHistoryDTO;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

public class LoginHistoryBUS {
    private LoginHistoryDAO loginHistoryDAO;

    public LoginHistoryBUS() {
        loginHistoryDAO = new LoginHistoryDAO();
    }

    // lịch sử đăng nhập của 1 user
    public List<LoginHistoryDTO> getLoginHistoryByUserId(int userId) {
        try {
            return loginHistoryDAO.getLoginHistoryByUserId(userId);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();  
        }
    }

    // toàn bộ lịch sử đăng nhập
    public List<LoginHistoryDTO> getAllLoginHistory() {
        try {
            return loginHistoryDAO.getAllLoginHistory();
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
