
//Client
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) {
        try (
                Socket socket = new Socket(SERVER_IP, SERVER_PORT);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                Scanner scanner = new Scanner(System.in)) {
            Thread receivingThread = new Thread(() -> {
                try {
                    while (true) {
                        String message = in.readLine();
                        if (message == null) {
                            break;
                        } else if (message.startsWith("Online users: ")) {
                            String[] onlineUsers = message.substring("Online users: ".length()).split(", ");
                            System.out.println("\u001B[42mOnline users:\u001B[0m");
                            for (String user : onlineUsers) {
                                System.out.println(user);
                            }
                        } else {
                            System.out.println(message);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            receivingThread.start();

            while (true) {
                String input = scanner.nextLine();
                if (input.startsWith("/block ")) {
                    String[] parts = input.split(" ", 2);
                    String blockedUser = parts[1];
                    out.println("/block " + blockedUser);
                } else if (input.startsWith("/unblock ")) {
                    String[] parts = input.split(" ", 2);
                    String unblockedUser = parts[1];
                    out.println("/unblock " + unblockedUser);
                } else {
                    out.println(input);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}