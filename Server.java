import java.io.*;
import java.net.*;

public class ChatServer {
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(12345)) {
            System.out.println("Server started. Waiting for client...");
            Socket socket = serverSocket.accept();
            System.out.println("Client connected.");

           
            new Thread(() -> {
                try {
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String msg;
                    while ((msg = in.readLine()) != null) {
                        System.out.println("Client: " + msg);
                    }
                } catch (IOException e) {
                    System.out.println("Connection closed.");
                }
            }).start();


            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            String msgOut;
            while ((msgOut = userInput.readLine()) != null) {
                out.println(msgOut);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
