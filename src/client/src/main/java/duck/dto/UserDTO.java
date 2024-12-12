package duck.dto;

import java.time.LocalDateTime;

public class UserDTO {
    private int userId;
    private String username;
    private String fullName;
    private String address;
    private LocalDateTime dateOfBirth;
    private char gender;
    private String email;
    private String password;
    private boolean status; //  active,  locked
    private boolean isOnline;
    private LocalDateTime createdAt;
    private boolean isAdmin; // Thêm cột isAdmin để xác định người dùng là quản trị viên hay không

   
    public UserDTO(int userId, String username, String fullName, String address, LocalDateTime dateOfBirth, 
                   char gender, String email, String password, boolean status, boolean isOnline, LocalDateTime createdAt, boolean isAdmin) {
        this.userId = userId;
        this.username = username;
        this.fullName = fullName;
        this.address = address;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.email = email;
        this.password = password;
        this.status = status;
        this.isOnline = isOnline;
        this.createdAt = createdAt;
        this.isAdmin = isAdmin; 
    }

   
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public LocalDateTime getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDateTime dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public char getGender() { return gender; }
    public void setGender(char gender) { this.gender = gender; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public boolean isStatus() { return status; }
    public void setStatus(boolean status) { this.status = status; }

    public boolean isOnline() { return isOnline; }
    public void setOnline(boolean online) { isOnline = online; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public boolean isAdmin() { return isAdmin; }  
    public void setAdmin(boolean isAdmin) { this.isAdmin = isAdmin; }  

    public String toString() { return username; }
}
