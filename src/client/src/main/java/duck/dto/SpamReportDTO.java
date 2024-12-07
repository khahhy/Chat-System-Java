package duck.dto;

import java.time.LocalDateTime;

public class SpamReportDTO {
    private int reportId;
    private int reporterId;
    private int reportedId;
    private String reason;
    private LocalDateTime createdAt;

    public SpamReportDTO(int reportId, int reporterId, int reportedId, String reason, LocalDateTime createdAt) {
        this.reportId = reportId;
        this.reporterId = reporterId;
        this.reportedId = reportedId;
        this.reason = reason;
        this.createdAt = createdAt;
    }

 
    public int getReportId() { return reportId; }
    public void setReportId(int reportId) { this.reportId = reportId; }

    public int getReporterId() { return reporterId; }
    public void setReporterId(int reporterId) { this.reporterId = reporterId; }

    public int getReportedId() { return reportedId; }
    public void setReportedId(int reportedId) { this.reportedId = reportedId; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
