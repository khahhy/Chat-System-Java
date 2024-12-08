package duck.bus;

import duck.dao.SpamReportDAO;
import duck.dto.SpamReportDTO;
import duck.dao.UserDAO;
import duck.dto.UserDTO;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import java.util.HashMap;
import java.util.Map;

public class SpamReportBUS {
    private SpamReportDAO spamReportDAO;
    private UserDAO userDAO;

    public SpamReportBUS() {
        this.spamReportDAO = new SpamReportDAO();
        this.userDAO = new UserDAO();
    }

    // username, time
    public List<Map<String, Object>> getSpamReports(String sortBy, LocalDateTime startTime, LocalDateTime endTime, String usernameFilter) {
        try {
            List<SpamReportDTO> reports = spamReportDAO.getSpamReports(sortBy, startTime, endTime, usernameFilter);
            List<Map<String, Object>> report_detail = new ArrayList<>();

            for (SpamReportDTO item : reports) {
                Map<String, Object> record = new HashMap<>();
                UserDTO user = userDAO.getUserById(item.getReportedId());

                record.put("reportedId", user.getUserId());
                record.put("username", user.getUsername());
                record.put("reportTime", item.getCreatedAt());

                report_detail.add(record);
            }

            return report_detail;
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    
    public boolean lockUser(int userId) {
        try {
            return spamReportDAO.lockUser(userId);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
