// import java.io.*;
// import java.net.*;
// import java.util.*;

// public class Server {
//     private static final int PORT = 12345;
//     private static List<ClientHandler> clients = new ArrayList<>();
//     private static Map<String, String> userCredentials = new HashMap<>();
//     private static Map<String, Boolean> userStatus = new HashMap<>(); // Stores online status of users

//     public static void main(String[] args) {
//         // Adding user credentials (username, password)
//         userCredentials.put("Rifat", "password1");
//         userCredentials.put("Rokon", "password2");
//         userCredentials.put("Asif", "password3");

//         try (ServerSocket serverSocket = new ServerSocket(PORT)) {
//             System.out.println("Server started. Waiting for clients to connect...");

//             while (true) {
//                 Socket clientSocket = serverSocket.accept();
//                 System.out.println("New client connected: " + clientSocket);

//                 ClientHandler clientHandler = new ClientHandler(clientSocket);
//                 clients.add(clientHandler);
//                 new Thread(clientHandler).start();
//             }
//         } catch (IOException e) {
//             e.printStackTrace();
//         }
//     }

//     public static void removeClient(ClientHandler client) {
//         String username = client.getUsername();
//         clients.remove(client);
//         userStatus.remove(username); // Remove user from online status map
//         broadcastMessage(username + " has left the chat."); // Broadcast user leaving message
//         broadcastOnlineUsers(); // Broadcast updated online users list
//     }

//     public static void broadcastMessage(String message) {
//         for (ClientHandler client : clients) {
//             client.sendMessage(message);
//         }
//     }

//     public static void broadcastMessage(String message, ClientHandler sender) {
//         String[] parts = message.split(": ", 2);
//         String recipient = parts[0]; // Extract recipient username from message
//         String content = parts[1]; // Extract message content

//         if (recipient.equals("broadcast")) {
//             // Broadcast message to all clients except sender
//             for (ClientHandler client : clients) {
//                 if (client != sender) {
//                     client.sendMessage(sender.getUsername() + ": " + content);
//                 }
//             }
//         } else {
//             // Send private message to specific client
//             for (ClientHandler client : clients) {
//                 if (client.getUsername().equals(recipient)) {
//                     client.sendMessage(sender.getUsername() + " (private): " + content);
//                     return; // Send the message to the recipient and exit
//                 }
//             }
//             // If recipient not found, send a notification to the sender
//             sender.sendMessage("User " + recipient + " not found or not online.");
//         }
//     }

//     // Broadcast updated online users list to all clients
//     public static void broadcastOnlineUsers() {
//         StringBuilder onlineUsers = new StringBuilder("Online users: ");
//         for (Map.Entry<String, Boolean> entry : userStatus.entrySet()) {
//             if (entry.getValue()) {
//                 onlineUsers.append(entry.getKey()).append(", ");
//             }
//         }
//         String onlineUsersList = onlineUsers.toString().trim().replaceAll(", $", ""); // Remove trailing comma and space
//         for (ClientHandler client : clients) {
//             client.sendMessage(onlineUsersList);
//         }
//     }

//     static class ClientHandler implements Runnable {
//         private Socket clientSocket;
//         private BufferedReader in;
//         private PrintWriter out;
//         private String username;
//         private boolean isAuthenticated;

//         public ClientHandler(Socket socket) {
//             this.clientSocket = socket;
//             try {
//                 in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
//                 out = new PrintWriter(clientSocket.getOutputStream(), true);
//             } catch (IOException e) {
//                 e.printStackTrace();
//             }
//         }

//         @Override
//         public void run() {
//             try {
//                 // Perform user authentication
//                 if (!authenticateUser()) {
//                     return; // If authentication fails, terminate the connection
//                 }
//                 // Set user online status to true
//                 userStatus.put(username, true);
//                 broadcastOnlineUsers(); // Broadcast updated online users list

//                 while (true) {
//                     String message = in.readLine();
//                     if (message == null) {
//                         break;
//                     }
//                     System.out.println("Received from client " + username + ": " + message);

//                     // Broadcast or send private message
//                     Server.broadcastMessage(message, this);
//                 }
//             } catch (IOException e) {
//                 e.printStackTrace();
//             } finally {
//                 try {
//                     clientSocket.close();
//                 } catch (IOException e) {
//                     e.printStackTrace();
//                 }
//                 // Set user online status to false
//                 userStatus.put(username, false);
//                 removeClient(this);
//             }
//         }

//         public void sendMessage(String message) {
//             out.println(message);
//         }

//         public boolean isAuthenticated() {
//             return isAuthenticated;
//         }

//         public String getUsername() {
//             return username;
//         }

//         private boolean authenticateUser() throws IOException {
//             // Request username from the client
//             out.println("Enter username:");
//             String enteredUsername = in.readLine();
//             if (enteredUsername == null) {
//                 return false;
//             }

//             // Request password from the client
//             out.println("Enter password:");
//             String enteredPassword = in.readLine();
//             if (enteredPassword == null) {
//                 return false;
//             }

//             // Check if username and password match
//             if (userCredentials.containsKey(enteredUsername) &&
//                     userCredentials.get(enteredUsername).equals(enteredPassword)) {
//                 username = enteredUsername;
//                 out.println("Authentication successful. Welcome, " + username + "!");
//                 isAuthenticated = true;
//                 // Initialize user online status to false
//                 userStatus.put(username, false);
//                 return true;
//             } else {
//                 out.println("Authentication failed. Invalid username or password.");
//                 return false;
//             }
//         }
//     }
// }
import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private static final int PORT = 12345;
    private static List<ClientHandler> clients = new ArrayList<>();
    private static Map<String, String> userCredentials = new HashMap<>();
    private static Map<String, Boolean> userStatus = new HashMap<>(); // Stores online status of users

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
        broadcastMessage(username + " has left the chat."); // Broadcast user leaving message
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

        if (recipient.equals("broadcast")) {
            // Broadcast message to all clients except sender
            for (ClientHandler client : clients) {
                if (client != sender) {
                    client.sendMessage(sender.getUsername() + ": " + content);
                }
            }
        } else {
            // Send private message to specific client
            for (ClientHandler client : clients) {
                if (client.getUsername().equals(recipient)) {
                    client.sendMessage(sender.getUsername() + " (private): " + content);
                    return; // Send the message to the recipient and exit
                }
            }
            // If recipient not found, send a notification to the sender
            sender.sendMessage("User " + recipient + " not found or not online.");
        }
    }

    // Broadcast updated online users list to all clients
    public static void broadcastOnlineUsers() {
        StringBuilder onlineUsers = new StringBuilder("Online users: ");
        for (Map.Entry<String, Boolean> entry : userStatus.entrySet()) {
            if (entry.getValue()) {
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

                    if (message.startsWith("FILE:")) {
                        // Handle file sharing
                        String[] parts = message.split(": ", 3);
                        String recipient = parts[1]; // Extract recipient username
                        String filePath = parts[2]; // Extract file path
                        sendFile(recipient, filePath);
                    } else {
                        // Broadcast or send private message
                        Server.broadcastMessage(message, this);
                    }
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
            out.println("Enter username:");
            String enteredUsername = in.readLine();
            if (enteredUsername == null) {
                return false;
            }

            // Request password from the client
            out.println("Enter password:");
            String enteredPassword = in.readLine();
            if (enteredPassword == null) {
                return false;
            }

            // Check if username and password match
            if (userCredentials.containsKey(enteredUsername) &&
                    userCredentials.get(enteredUsername).equals(enteredPassword)) {
                username = enteredUsername;
                out.println("Authentication successful. Welcome, " + username + "!");
                isAuthenticated = true;
                // Initialize user online status to false
                userStatus.put(username, false);
                return true;
            } else {
                out.println("Authentication failed. Invalid username or password.");
                return false;
            }
        }

        private void sendFile(String recipient, String filePath) {
            try {
                File file = new File(filePath);
                if (!file.exists() || file.isDirectory()) {
                    sendMessage("File not found: " + filePath);
                    return;
                }
                byte[] fileBytes = new byte[(int) file.length()];
                FileInputStream fis = new FileInputStream(file);
                BufferedInputStream bis = new BufferedInputStream(fis);
                bis.read(fileBytes, 0, fileBytes.length);

                for (ClientHandler client : clients) {
                    if (client.getUsername().equals(recipient)) {
                        client.sendMessage("FILE: " + username + ": " + file.getName());
                        client.sendBytes(fileBytes);
                        return;
                    }
                }
                sendMessage("User " + recipient + " not found or not online.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void sendBytes(byte[] myByteArray) throws IOException {
            out.write(0);
            out.flush();
        }
    }
}
