package duck;

import java.io.*;
import java.net.*;
import java.util.*;

public class ServerApp {
    private static final int PORT = 12345; 
    private static final Map<Integer, ClientHandler> userIdToClient = new HashMap<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server đang chạy trên port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client kết nối: " + clientSocket.getInetAddress());

                ClientHandler handler = new ClientHandler(clientSocket);
                new Thread(handler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendToUser(String message, int receiverId) {
        synchronized (userIdToClient) {
            ClientHandler receiver = userIdToClient.get(receiverId);
            if (receiver != null) {
                receiver.sendMessage(message);
            }
        }
    }

    private static class ClientHandler implements Runnable {
        private final Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private int userId;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                String loginMessage = in.readLine();
                userId = Integer.parseInt(loginMessage);
                synchronized (userIdToClient) {
                    userIdToClient.put(userId, this);
                }

                String message;
                while ((message = in.readLine()) != null) {
                    // Tin nhắn định dạng: "receiverId|messageContent"
                    String[] parts = message.split("\\|", 2);
                    if (parts.length == 2) {
                        int receiverId = Integer.parseInt(parts[0]);
                        String messageContent = parts[1];
                        sendToUser(messageContent, receiverId);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                    synchronized (userIdToClient) {
                        userIdToClient.remove(userId);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void sendMessage(String message) {
            out.println(message);
        }
    }
}