package duck.dao;

import duck.dto.MessageDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import duck.dto.DeletedMessageDTO;

public class DeletedMessageDAO {
    public boolean addDeletedMessage(int messageId, int userId) throws SQLException {
        String query = "INSERT INTO deletemessages (message_id, user_id) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, messageId);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean checkDeletedMessage(int messageId, int userId) throws SQLException {
    String query = "SELECT COUNT(*) FROM deletemessages WHERE message_id = ? AND user_id = ?";
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(query)) {
        stmt.setInt(1, messageId);
        stmt.setInt(2, userId);
        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1) > 0; // Lấy giá trị của COUNT(*) từ cột đầu tiên
            }
        }
    }
    return false; // Nếu không có kết quả, trả về false
}

}
