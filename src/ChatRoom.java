import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

public class ChatRoom {
    private static int PORT;
    private final ConcurrentHashMap<String, ClientHandler> clients = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        System.out.println("Welcome! Please enter ChatRoom port:");
        Scanner scanner = new Scanner(System.in);
        PORT = scanner.nextInt();
        new ChatRoom();
    }

    public ChatRoom() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("ChatRoom Started!");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler client = new ClientHandler(clientSocket, this);
                new Thread(client).start();
            }
        } catch (IOException ex) {
            System.out.println("Server error: " + ex.getMessage());
        }
    }

    public void broadcast(String message, String excludeUser) {
        clients.forEach((name, client) -> {
            if (!name.equals(excludeUser)) {
                client.sendMsg(message);
            }
        });
    }

    public void privateMessage(String recipient, String message, String sender) {
        ClientHandler client = clients.get(recipient);
        if (client != null) {
            client.sendMsg("[Private from " + sender + "]: " + message);
        }
    }

    public void addClient(String name, ClientHandler client) {
        clients.put(name, client);
        broadcast(name + " joined the chat. Users in chat: " + clients.size(), null);
    }

    public void removeClient(String name) {
        clients.remove(name);
        broadcast(name + " left the chat. Users in chat: " + clients.size(), null);
    }
}
