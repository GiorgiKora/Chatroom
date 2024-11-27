import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientHandler implements Runnable {
    private final ChatRoom chatRoom;
    private final PrintWriter outMessage;
    private final Scanner inMessage;
    private String userName;

    public ClientHandler(Socket socket, ChatRoom chatRoom) throws IOException {
        this.chatRoom = chatRoom;
        this.outMessage = new PrintWriter(socket.getOutputStream(), true);
        this.inMessage = new Scanner(socket.getInputStream());
        this.userName = "User_" + socket.getPort();
        chatRoom.addClient(userName, this);
    }

    @Override
    public void run() {
        sendMsg("Welcome to the chat, " + userName + "!");
        try {
            while (inMessage.hasNext()) {
                String clientMessage = inMessage.nextLine();

                if (clientMessage.startsWith("/name ")) {
                    String newName = clientMessage.substring(6).trim();
                    chatRoom.removeClient(userName);
                    userName = newName;
                    chatRoom.addClient(userName, this);
                } else if (clientMessage.startsWith("/private ")) {
                    String[] parts = clientMessage.split(" ", 3);
                    if (parts.length >= 3) {
                        String recipient = parts[1];
                        String privateMessage = parts[2];
                        chatRoom.privateMessage(recipient, privateMessage, userName);
                    }
                } else if (clientMessage.equalsIgnoreCase("/quit")) {
                    break;
                } else {
                    chatRoom.broadcast(userName + ": " + clientMessage, userName);
                }
            }
        } finally {
            chatRoom.removeClient(userName);
        }
    }

    public void sendMsg(String msg) {
        outMessage.println(msg);
    }
}
