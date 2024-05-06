// import java.io.*;
// import java.net.*;
// import java.util.Scanner;

// public class Client {
//     private static final String SERVER_IP = "localhost";
//     private static final int SERVER_PORT = 12345;

//     public static void main(String[] args) {
//         try (
//                 Socket socket = new Socket(SERVER_IP, SERVER_PORT);
//                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
//                 Scanner scanner = new Scanner(System.in)) {
//             Thread receivingThread = new Thread(() -> {
//                 try {
//                     while (true) {
//                         String message = in.readLine();
//                         if (message == null) {
//                             break;
//                         } else if (message.startsWith("Online users: ")) {
//                             String[] onlineUsers = message.substring("Online users: ".length()).split(", ");
//                             System.out.println("Online users:");
//                             for (String user : onlineUsers) {
//                                 System.out.println(user);
//                             }
//                         } else {
//                             System.out.println(message);
//                         }
//                     }
//                 } catch (IOException e) {
//                     e.printStackTrace();
//                 }
//             });
//             receivingThread.start();

//             while (true) {
//                 String message = scanner.nextLine();
//                 out.println(message);
//             }
//         } catch (IOException e) {
//             e.printStackTrace();
//         }
//     }
// }
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
                            System.out.println("Online users:");
                            for (String user : onlineUsers) {
                                System.out.println(user);
                            }
                        } else if (message.startsWith("FILE:")) {
                            // Handle file sharing
                            String[] parts = message.split(": ", 3);
                            String sender = parts[1]; // Extract sender username
                            String fileName = parts[2]; // Extract file name
                            saveFile(sender, fileName, in);
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
                String message = scanner.nextLine();
                if (message.startsWith("SEND ")) {
                    // Send file command format: SEND recipient_username file_path
                    String[] parts = message.split(" ", 3);
                    String recipient = parts[1];
                    String filePath = parts[2];
                    sendFile(out, recipient, filePath);
                } else {
                    out.println(message);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void sendFile(PrintWriter out, String recipient, String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists() || file.isDirectory()) {
                System.out.println("File not found: " + filePath);
                return;
            }
            out.println("FILE: " + recipient + ": " + filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void saveFile(String sender, String fileName, BufferedReader in) {
        try {
            FileOutputStream fos = new FileOutputStream(fileName);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            byte[] bytes = new byte[1024];
            int count;
            while ((count = in.read()) > 0) {
                bos.write(bytes, 0, count);
            }
            bos.flush();
            bos.close();
            System.out.println("File received from " + sender + ": " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
