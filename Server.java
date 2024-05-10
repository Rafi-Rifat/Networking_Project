
//Server
import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private static final int PORT = 12345;
    private static List<ClientHandler> clients = new ArrayList<>();
    private static Map<String, String> userCredentials = new HashMap<>();
    private static Map<String, Boolean> userStatus = new HashMap<>(); // Stores online status of users
    private static Map<String, Set<String>> blockedUsers = new HashMap<>(); //

    public static void main(String[] args) {
        // Adding user credentials (username, password)
        userCredentials.put("Rifat", "1");
        userCredentials.put("Rokon", "2");
        userCredentials.put("Asif", "3");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started. Waiting for clients to connect...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket);

                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clients.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void removeClient(ClientHandler client) {
        String username = client.getUsername();
        clients.remove(client);
        userStatus.remove(username); // Remove user from online status map
        broadcastMessage("\u001B[31m" + username + " has left the chat.\u001B[0m"); // Broadcast user leaving message
        broadcastOnlineUsers(); // Broadcast updated online users list
    }

    public static void broadcastMessage(String message) {
        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }

    public static void broadcastMessage(String message, ClientHandler sender) {
        String[] parts = message.split(": ", 2);
        String recipient = parts[0]; // Extract recipient username from message
        String content = parts[1]; // Extract message content
        if (blockedUsers.getOrDefault(recipient,
                Collections.emptySet()).contains(sender.getUsername())) {
            sender.sendMessage("\u001B[31mYou cannot send messages to " + recipient
                    + " as you have been blocked by him.\u001B[0m");
            return;
        }
        if (recipient.equals("broadcast")) {
            // Broadcast message to all clients except sender
            for (ClientHandler client : clients) {
                if (client != sender) {
                    // client.sendMessage(sender.getUsername() + ": " + content);
                    String messageToSend = "\u001B[44m" + sender.getUsername() + ":\u001B[0m" + "  " + content;
                    client.sendMessage(messageToSend);
                }
            }
        } else {
            // Send private message to specific client
            for (ClientHandler client : clients) {
                if (client.getUsername().equals(recipient)) {
                    // client.sendMessage(sender.getUsername() + " (private): " + content);
                    String messageToSend = "\u001B[44m" + sender.getUsername() + "(private):\u001B[0m" + "  " + content;
                    client.sendMessage(messageToSend);
                    return; // Send the message to the recipient and exit
                }
            }
            // If recipient not found, send a notification to the sender
            sender.sendMessage("\u001B[31mUser " + recipient + " not found or not online.\u001B[0m");
        }
    }

    // Broadcast updated online users list to all clients
    public static void broadcastOnlineUsers() {
        StringBuilder onlineUsers = new StringBuilder("Online users: ");
        for (Map.Entry<String, Boolean> entry : userStatus.entrySet()) {
            if (entry.getValue()) {
                // Prepend ANSI escape code for green text to the username
                onlineUsers.append(entry.getKey()).append(", ");
            }
        }
        String onlineUsersList = onlineUsers.toString().trim().replaceAll(", $", ""); // Remove trailing comma and space
        for (ClientHandler client : clients) {
            client.sendMessage(onlineUsersList);
        }
    }

    static class ClientHandler implements Runnable {
        private Socket clientSocket;
        private BufferedReader in;
        private PrintWriter out;
        private String username;
        private boolean isAuthenticated;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
            try {
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new PrintWriter(clientSocket.getOutputStream(), true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                // Perform user authentication
                if (!authenticateUser()) {
                    return; // If authentication fails, terminate the connection
                }
                // Set user online status to true
                userStatus.put(username, true);
                broadcastOnlineUsers(); // Broadcast updated online users list

                while (true) {
                    String message = in.readLine();
                    if (message == null) {
                        break;
                    }
                    System.out.println("Received from client " + username + ": " + message);
                    // Handle blocking/unblocking commands
                    if (message.startsWith("block ")) {
                        String blockedUser = message.substring("block ".length());
                        blockUser(blockedUser);
                        continue; // Skip message processing
                    } else if (message.startsWith("unblock ")) {
                        String unblockedUser = message.substring("unblock ".length());
                        unblockUser(unblockedUser);
                        continue; // Skip message processing
                    }
                    // Broadcast or send private message
                    Server.broadcastMessage(message, this);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // Set user online status to false
                userStatus.put(username, false);
                removeClient(this);
            }
        }

        public void sendMessage(String message) {
            out.println(message);
        }

        public boolean isAuthenticated() {
            return isAuthenticated;
        }

        public String getUsername() {
            return username;
        }

        private boolean authenticateUser() throws IOException {
            // Request username from the client
            out.println("\u001B[33mEnter username:\u001B[0m");
            String enteredUsername = in.readLine();
            if (enteredUsername == null) {
                return false;
            }

            // Request password from the client
            out.println("\u001B[33mEnter password:\u001B[0m");

            String enteredPassword = in.readLine();
            if (enteredPassword == null) {
                return false;
            }

            // Check if username and password match
            if (userCredentials.containsKey(enteredUsername) &&
                    userCredentials.get(enteredUsername).equals(enteredPassword)) {
                username = enteredUsername;
                // out.println("Authentication successful. Welcome, " + username + "!");
                out.println("\u001B[32mLogin Successful!\u001B[0m");
                isAuthenticated = true;
                // Initialize user online status to false
                userStatus.put(username, false);
                return true;
            } else {
                out.println("\u001B[31mLogin Failed!\u001B[0m");
                return false;
            }
        }

        private void blockUser(String blockedUser) {
            if (userCredentials.containsKey(blockedUser)) {
                blockedUsers.computeIfAbsent(username, k -> new HashSet<>()).add(blockedUser);
                sendMessage("\u001B[31m" + blockedUser + " is blocked!\u001B[0m");
            } else {
                sendMessage("User " + blockedUser + " does not exist.");
            }
        }

        private void unblockUser(String unblockedUser) {
            if (blockedUsers.containsKey(username) && blockedUsers.get(username).contains(unblockedUser)) {
                blockedUsers.get(username).remove(unblockedUser);
                sendMessage("\u001B[32m" + unblockedUser + " is unblocked now.\u001B[0m");
            } else {
                sendMessage("User " + unblockedUser + " is not blocked.");
            }
        }
    }
}
