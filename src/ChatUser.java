import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ChatUser {
    private Socket socket;
    private PrintWriter outMessage;
    private Scanner inMessage;
    private String userName;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to the Chat! Please enter server address:");
        String address = scanner.nextLine();

        System.out.println("Enter server port:");
        int port = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        new ChatUser(address, port);
    }

    public ChatUser(String address, int port) {
        while (true) {
            try {
                // Connect to the server
                this.socket = new Socket(address, port);
                this.outMessage = new PrintWriter(socket.getOutputStream(), true);
                this.inMessage = new Scanner(socket.getInputStream());

                System.out.println("Connected to the chat server!");
                System.out.print("Enter your username: ");
                Scanner scanner = new Scanner(System.in);
                this.userName = scanner.nextLine();
                outMessage.println("/name " + userName);

                // Start listening for messages
                new Thread(this::listenForMessages).start();

                // Allow user to send messages
                sendMessageLoop(scanner);

                break;
            } catch (IOException ex) {
                System.out.println("Failed to connect to server. Try again.");
                System.out.print("Enter server address: ");
                Scanner scanner = new Scanner(System.in);
                address = scanner.nextLine();
                System.out.print("Enter server port: ");
                port = scanner.nextInt();
                scanner.nextLine(); // Consume newline
            }
        }
    }

    private void listenForMessages() {
        while (inMessage.hasNextLine()) {
            String serverMessage = inMessage.nextLine();
            System.out.println(serverMessage);
        }
    }

    private void sendMessageLoop(Scanner scanner) {
        System.out.println("Welcome to the chat, " + userName + "!");
        System.out.println("Commands:");
        System.out.println("/name [newName] - Change your username");
        System.out.println("/private [username] [message] - Send a private message");
        System.out.println("/quit - Leave the chat");

        while (true) {
            String message = scanner.nextLine();
            if (message.equalsIgnoreCase("/quit")) {
                outMessage.println("/quit");
                System.out.println("You left the chat.");
                closeConnection();
                break;
            }
            outMessage.println(message);
        }
    }

    private void closeConnection() {
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException ex) {
            System.out.println("Error closing connection: " + ex.getMessage());
        }
    }
}
