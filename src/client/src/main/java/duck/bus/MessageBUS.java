package duck.bus;

import duck.dao.MessageDAO;
import duck.dto.MessageDTO;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MessageBUS {
    private MessageDAO messageDAO;

    public MessageBUS() {
        messageDAO = new MessageDAO();
    }

    // giữa hai người dùng
    public List<MessageDTO> getMessagesBetweenUsers(int senderId, int receiverId) {
        try {
            return messageDAO.getMessagesBetweenUsers(senderId, receiverId);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();  
        }
    }

    // trong một nhóm
    public List<MessageDTO> getMessagesInGroup(int groupId) {
        try {
            return messageDAO.getMessagesInGroup(groupId);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();  
        }
    }

    public boolean addMessage(MessageDTO message) {
        try {
            return messageDAO.addMessage(message);
        } catch (SQLException e) {
            e.printStackTrace();
            return false; 
        }
    }

    public boolean deleteMessage(int messageId) {
        try {
            return messageDAO.deleteMessage(messageId);
        } catch (SQLException e) {
            e.printStackTrace();
            return false; 
        }
    }

    public boolean deleteMessagesInGroup(int groupId) {
        try {
            return messageDAO.deleteMessagesInGroup(groupId);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;  
        }
    }

    public List<MessageDTO> searchMessages(int userId, String keyword) {
        try {
            return messageDAO.searchMessages(userId, keyword);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();  
        }
    }
}
