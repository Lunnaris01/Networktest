package Chat;

import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;

import java.io.*;
import java.net.Socket;

/**
 * Created by bjebb on 02.06.14.
 */
public class ChatClient extends Socket {
    ChatUI chatUI;
    Thread reciever;
    String ip;
    int port;
    public String name;

    protected SimpleStringProperty text = new SimpleStringProperty();

    DataInputStream input;
    public DataOutputStream output;

    public ChatClient(String name, String ip, int port,ChatUI chatUI) throws IOException {
        super(ip, port);
        this.ip = ip;
        this.port = port;
        this.name = name;
        this.chatUI = chatUI;
        this.reciever = initReciever();
        open();
        reciever.start();
    }

    public void send(String msg){
        try {
            output.writeUTF(msg);
            output.flush();
            System.out.println("[CLIENT] Sended!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Thread initReciever(){
         Task t= new Task() {
             @Override
             protected Void call() throws Exception {
                 while(true){
                     System.out.println("[CLIENT] Client listening...");
                     handleMsg(input.readUTF());
                     System.out.println("[CLIENT] Nachricht empfangen");
                 }
             }
         };
        return new Thread(t);
    }

    private void handleMsg(String msg){
        if(msg.charAt(0)!='§'){
            chatUI.addMsg(msg);
        }
        else {
            int cmdEnd = msg.indexOf(' ',1);
            int paramStart = msg.indexOf('%',cmdEnd+1);
            int paramEnd = msg.indexOf(' ',paramStart+1);
            switch (msg.substring(1,cmdEnd!=-1?cmdEnd:msg.length())){
                case "ADDED":
                    send("$NAME %"+name);break;
            }
        }
    }

    public void open() throws IOException{
        input = new DataInputStream(new BufferedInputStream(this.getInputStream()));
        output = new DataOutputStream(new BufferedOutputStream(this.getOutputStream()));
    }

    public void close() throws IOException{
        send("$EXIT");
        super.close();
    }

    public String getText() {
        return text.get();
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public SimpleStringProperty textProperty() {
        return text;
    }
}