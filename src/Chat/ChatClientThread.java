package Chat;

import java.io.IOException;

/**
 * kann eventuell gelöscht werden
 * Created by bjebb on 02.06.14.
 */
public class ChatClientThread extends Thread {
    ChatClient chatClient;

    public ChatClientThread(ChatClient chatClient) {
        this.chatClient = chatClient;
        this.start();
    }

    public void run(){
        while (true){
            try {
                System.out.println(chatClient.input.readUTF());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
