package duck.dto;

public class DeletedMessageDTO {
    private int messageId;
    private int userId;


    public DeletedMessageDTO(int messageId, int userId) {
        this.messageId = messageId;
        this.userId = userId;

    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
