import java.io.*;
import java.net.Socket;

public class ChatClient {

    private static final String SERVER = "localhost";
    private static final int PORT = 1234;

    public static void main(String[] args) {

        try {

            Socket socket = new Socket(SERVER, PORT);

            BufferedReader keyboard =
                    new BufferedReader(new InputStreamReader(System.in));

            PrintWriter out =
                    new PrintWriter(socket.getOutputStream(), true);

            BufferedReader in =
                    new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // receive messages
            new Thread(() -> {

                try {

                    String message;

                    while ((message = in.readLine()) != null) {
                        System.out.println(message);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }).start();

            // send messages
            String userInput;

            while ((userInput = keyboard.readLine()) != null) {
                out.println(userInput);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}