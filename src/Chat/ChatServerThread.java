package Chat;

import java.io.*;
import java.net.Socket;

/**
 * Created by bjebb on 02.06.14.
 */
public class ChatServerThread extends Thread {
    private ChatServer serverSocket;
    private Socket client;
    private DataInputStream input;
    private DataOutputStream output;
    private boolean run = true;

    public ChatServerThread(ChatServer serverSocket, Socket client){
        super();
        this.serverSocket = serverSocket;
        this.client = client;
        open();
        this.start();
    }

    public void send(String msg)throws IOException{
            output.writeUTF(msg);
            output.flush();
            System.out.println("[SERVER] An Client geschickt");
    }

    @Override
    public void run(){
        boolean crashed = false;
        while(!crashed){
            try {
                System.out.println("[SERVER] Server listening...");
                serverSocket.handleMsg(this,input.readUTF());
            } catch (IOException e) {
                crashed = true;
                System.out.println("Verbindung abgebrochen");
                try {
                    this.close();
                    this.stop();
                } catch (IOException e1) {

                }
            }
        }
    }

    public void open(){
        try {
            input = new DataInputStream(new BufferedInputStream(client.getInputStream()));
            output = new DataOutputStream(new BufferedOutputStream(client.getOutputStream()));
        }
        catch(IOException e){
            System.out.println("Konnte Streams nicht öffnen");
        }
    }

    public void close() throws IOException{
        input.close();
        output.close();
    }

}
