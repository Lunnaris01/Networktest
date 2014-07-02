package Chat;

/**
 * This class is just used by the Server!
 * Created by bjebb on 05.06.14.
 */
public class Client {
    private boolean added = false;
    private boolean named = false;
    private ChatServerThread chatServerThread;
    private boolean admin = false;
    private String name;

    public Client(ChatServerThread socket, String name){
        this.chatServerThread = socket;
        this.name = name;
    }

    public Client(ChatServerThread chatServerThread) {
        this.chatServerThread = chatServerThread;
    }

    public boolean isAdded() {
        return added;
    }

    public void setAdded(boolean added) {
        this.added = added;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public ChatServerThread getChatServerThread() {
        return chatServerThread;
    }

    public void setNamed() {
        this.named = true;
    }

    public boolean isNamed() {
        return named;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
