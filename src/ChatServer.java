import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;
import java.util.concurrent.*;

public class ChatServer {

    private static final int PORT = 1234;

    public static Set<ClientHandler> clients =
            ConcurrentHashMap.newKeySet();

    public static BlockingQueue<Message> messageQueue =
            new LinkedBlockingQueue<>();

    public static ExecutorService pool =
            Executors.newFixedThreadPool(10);

    public static void main(String[] args) {

        System.out.println("Chat Server Started...");

        // Dispatcher thread
        new Thread(() -> {

            try {

                while (true) {

                    Message msg = messageQueue.take();

                    for (ClientHandler client : clients) {

                        if (client != msg.getSender()) {
                            client.sendMessage(msg.getText());
                        }
                    }
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }).start();

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {

            while (true) {

                Socket socket = serverSocket.accept();
                System.out.println("Client connected");

                ClientHandler handler = new ClientHandler(socket);

                clients.add(handler);

                pool.execute(handler);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}