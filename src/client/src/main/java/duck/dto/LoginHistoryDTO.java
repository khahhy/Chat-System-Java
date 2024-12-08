package duck.dto;

import java.time.LocalDateTime;

public class LoginHistoryDTO {
    private int historyId;
    private int userId;
    private LocalDateTime loginTime; 
    private LocalDateTime logoutTime; 


    public LoginHistoryDTO(int historyId, int userId, LocalDateTime loginTime, LocalDateTime logoutTime) {
        this.historyId = historyId;
        this.userId = userId;
        this.loginTime = loginTime;
        this.logoutTime = logoutTime;
    }

   
    public int getHistoryId() { return historyId; }
    public void setHistoryId(int historyId) { this.historyId = historyId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public LocalDateTime getLoginTime() { return loginTime; }
    public void setLoginTime(LocalDateTime loginTime) { this.loginTime = loginTime; }

    public LocalDateTime getLogoutTime() { return logoutTime; }
    public void setLogoutTime(LocalDateTime logoutTime) { this.logoutTime = logoutTime; }

}
