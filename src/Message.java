public class Message {

    private String text;
    private ClientHandler sender;

    public Message(String text, ClientHandler sender) {
        this.text = text;
        this.sender = sender;
    }

    public String getText() {
        return text;
    }

    public ClientHandler getSender() {
        return sender;
    }
}