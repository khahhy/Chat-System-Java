package duck.bus;

import duck.dao.DeletedMessageDAO;
import java.sql.SQLException;

public class DeletedMessageBUS {
    private final DeletedMessageDAO deletedMessageDAO;

    public DeletedMessageBUS() {
        this.deletedMessageDAO = new DeletedMessageDAO();
    }

    public boolean addDeletedMessage(int messageId, int userId) {
        try {
            return deletedMessageDAO.addDeletedMessage(messageId, userId);
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean checkDeletedMessage(int messageId, int userId) {
        try {
            return deletedMessageDAO.checkDeletedMessage(messageId, userId);
        } catch (SQLException e) {
            return false;
        }
    }
}
