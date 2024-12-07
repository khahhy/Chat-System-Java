package duck.dto;

import java.time.LocalDateTime;

public class LoginHistoryDTO {
    private int historyId;
    private int userId;
    private LocalDateTime loginTime; 
    private LocalDateTime logoutTime; 

    private String username;   
    private String fullName;

    public LoginHistoryDTO(int historyId, int userId, LocalDateTime loginTime, LocalDateTime logoutTime, String username, String fullName) {
        this.historyId = historyId;
        this.userId = userId;
        this.loginTime = loginTime;
        this.logoutTime = logoutTime;
        this.username = username;
        this.fullName = fullName;
    }

   
    public int getHistoryId() { return historyId; }
    public void setHistoryId(int historyId) { this.historyId = historyId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public LocalDateTime getLoginTime() { return loginTime; }
    public void setLoginTime(LocalDateTime loginTime) { this.loginTime = loginTime; }

    public LocalDateTime getLogoutTime() { return logoutTime; }
    public void setLogoutTime(LocalDateTime logoutTime) { this.logoutTime = logoutTime; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
}
