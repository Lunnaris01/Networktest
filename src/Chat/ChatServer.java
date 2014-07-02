package Chat;

import javafx.concurrent.Task;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

/**
 * Created by bjebb on 30.05.14.
 */
public class ChatServer extends ServerSocket{
    private ChatServer chatServer = this;
    public ServerSocket server;
    private LinkedList<Client> clients = new LinkedList<>();
    private Thread ListenerForNewClients;
    private boolean listenForNewClients;

    //final Prefixes
    private final String NONE = "";
    private final String CCMD = "§";
    private final String SERVER = "[SERVER]: ";
    private final String ADMIN = "[ADMIN]";

    private String[] help = {
            "$NAME %name    : rename to 'name'",
            "$SHOW          : returns all active clients",
            "$PRIV %name msg: send msg to name",
            "$TIME          : returns the time in HH:mm",
            "$EXIT          : exits the Client"
    };

    public ChatServer(int port) throws IOException {
        super(port);
        this.server = this;
        this.ListenerForNewClients = initListener();

        listenForNewClients = true;
        ListenerForNewClients.start();
    }

    protected void handleMsg(ChatServerThread client, String msg){
        Client c = getClient(client);
        System.out.println(c!=null);
        System.out.println(c.isAdded());
        if(c!=null && c.isAdded()){
            if(!msg.isEmpty()) {
                System.out.println("[SERVER] Eingegangende Nachricht: " + msg);

                if (!msg.startsWith("$")) {
                    sendToAll(client,msg);
                } else {
                    msg=msg.toUpperCase();
                    int cmdEnd = msg.indexOf(' ',1);
                    int paramStart = msg.indexOf('%',cmdEnd+1);
                    int paramEnd = msg.indexOf(' ',paramStart+1);

                    switch (msg.substring(1,cmdEnd!=-1?cmdEnd:msg.length())) {
                        case "NAME":
                            try{rename(c, msg.substring(paramStart+1));}catch(NullPointerException ne){
                                System.out.println("EXCEPTION!");
                            };break;
                        case "SHOW":
                            sendToClient(SERVER, c, printClients());break;
                        case "PRIV":
                            try{sendToClient(
                                    c.getName()+":",
                                    msg.substring(paramStart+1,paramEnd),
                                    msg.substring(paramEnd));
                            }
                            catch(NullPointerException e){
                                sendToClient(SERVER,c,"Unknown Client!");
                            }
                            break;
                        case "TIME":
                            sendToClient(SERVER, c, new SimpleDateFormat("HH:mm").format(new Date())); break;
                        case "HELP":
                            sendToClient(SERVER, c, returnHelp());break;
                        case "EXIT":
                            removeClient(c);break;
                        default:
                            sendToClient(SERVER, c, "Unknown Command!");
                    }
                }
            }
        }
    }

    //accept waits until a connection from a client is received, so it would block a whole Thread
    private Thread initListener(){
        Task task = new Task() {
            @Override
            protected Void call() throws Exception {
                while(listenForNewClients){
                    System.out.println("Waiting for Clients...");
                    Socket client = server.accept();
                    System.out.println("accepting...");
                    ChatServerThread ct = new ChatServerThread(chatServer,client);
                    System.out.println("Client hinzugefügt");
                    Client c = new Client(ct);
                    if(client.getInetAddress().getCanonicalHostName().equals("localhost")){
                        c.setAdmin(true);
                    }
                    clients.add(c);
                    c.setAdded(true);
                    System.out.println("Client added to List");
                    sendToClient(CCMD, c, "ADDED");
                }
                return null;
            }
        };
        return new Thread(task);
    }

    public void listen(boolean in){listenForNewClients = in;}

    public Client getClient(ChatServerThread chatServerThread){
        for(int i = 0; i<clients.size();i++){
            if(clients.get(i).getChatServerThread() == chatServerThread){
                return clients.get(i);
            }
        }
        return null;
    }

    public Client getClient(String name){
        for(int i = 0; i<clients.size();i++){
            if(clients.get(i).getName().equals(name)){
                return clients.get(i);
            }
        }
        return null;
    }

    private void removeClient(Client c){
        clients.remove(c);
        sendToAll(SERVER,c.getName()+" leaved!");
        try {
            c.getChatServerThread().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * sends a message to all clients, with the name of a client as Prefix
     * @param client
     * @param msg the message to send
     */
    private void sendToAll(ChatServerThread client, String msg) {
        for (Client c : clients) {
            try {
                c.getChatServerThread().send(getClient(client).getName() + ": " + msg);
            } catch (IOException e) {
                removeClient(c);
                e.printStackTrace();
            }
        }
    }

    /**
     * sends a message to all Clients
     * @param prefix adds a Prefix
     * @param msg the message to send
     */
    private void sendToAll(String prefix, String msg){
        for (Client c : clients) {
            try {
                c.getChatServerThread().send(prefix+msg);
            } catch (IOException e) {
                removeClient(c);
                e.printStackTrace();
            }
        }
    }

    /**
     * Sends a Message to a single Client
     * @param prefix adds a Prefix
     * @param c the desired receiver
     * @param msg the message to send
     */
    private void sendToClient(String prefix, Client c, String msg){
        try {
            c.getChatServerThread().send(prefix+msg);
        } catch (IOException e) {
            removeClient(c);
            e.printStackTrace();
        }
    }

    /**
     * sends a Message to a single Client by name
     * @param prefix adds a Prefix
     * @param name the name of the Client
     * @param msg the message to send
     */
    private void sendToClient(String prefix, String name, String msg)throws NullPointerException {
        Client c = getClient(name);
        if(c!=null) {
            try {
                c.getChatServerThread().send(prefix + msg);
            } catch (IOException e) {
                removeClient(c);
                e.printStackTrace();
            }
        }
        else{
            throw new NullPointerException("Client not found!");
        }
    }

    /**
     * Get all connected Clients
     * @return all connected Clients
     */
    private String printClients(){
        String out="Connected Users:";
        for(Client c:clients){
            out += ("\n"+c.getName());
        }
        return out;
    }

    /**
     * rename a client
     * @param client the client you want to rename
     * @param name the new name
     */
    private void rename(Client client, String name){
        if(!client.isNamed()){
            client.setName(name);
            client.setNamed();
            if(!client.isAdmin()){
                sendToAll(SERVER, name + " joined!");
            }
            else{
                sendToAll(SERVER,name+ADMIN+" joined!");
            }
        }
        else{
            sendToAll(SERVER, client.getName() + " renamed to: " + name);
            client.setName(name);
        }
    }

    private String returnHelp(){
        String out = "All Commands: \n";
        for(int i=0;i<help.length;i++){
            out += help[i]+"\n";
        }
        return out;
    }

    @Override
    public void close(){
        try {
            for(Client c:clients){
                c.getChatServerThread().close();
            }
            ListenerForNewClients.stop();
            super.close();
            System.out.println("Server closed");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
