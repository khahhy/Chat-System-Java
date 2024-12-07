package duck.bus;

import duck.dao.SpamReportDAO;
import duck.dto.SpamReportDTO;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SpamReportBUS {
    private SpamReportDAO spamReportDAO;

    public SpamReportBUS() {
        spamReportDAO = new SpamReportDAO();
    }

    public List<SpamReportDTO> getReportsByReporterId(int reporterId) {
        try {
            return spamReportDAO.getReportsByReporterId(reporterId);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

  
    public List<SpamReportDTO> getReportsByReportedId(int reportedId) {
        try {
            return spamReportDAO.getReportsByReportedId(reportedId);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public boolean addSpamReport(SpamReportDTO report) {
        try {
            return spamReportDAO.addSpamReport(report);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteSpamReport(int reportId) {
        try {
            return spamReportDAO.deleteSpamReport(reportId);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteReportsByReportedId(int reportedId) {
        try {
            return spamReportDAO.deleteReportsByReportedId(reportedId);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteReportsByReporterId(int reporterId) {
        try {
            return spamReportDAO.deleteReportsByReporterId(reporterId);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
