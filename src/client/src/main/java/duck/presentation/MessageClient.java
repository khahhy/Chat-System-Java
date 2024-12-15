package duck.presentation;

import java.io.*;
import java.net.*;
import java.util.function.Consumer;

public class MessageClient {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private Consumer<String> onMessageReceived;

    public MessageClient(String host, int port, int userId, Consumer<String> onMessageReceived) throws IOException {
        this.socket = new Socket(host, port);
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.onMessageReceived = onMessageReceived;

        out.println(userId);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
        new Thread(() -> {
            try {
                String message;
                while ((message = in.readLine()) != null) {
                    onMessageReceived.accept(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void sendMessage(int receiverId, String message) {
        String formattedMessage = receiverId + "|" + message;
        out.println(formattedMessage);
    }

    public void close() throws IOException {
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
        if (out != null) {
            out.close();
        }
        if (in != null) {
            in.close();
        }
    }
    
}
