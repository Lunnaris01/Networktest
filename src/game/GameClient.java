package game;

import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by Julian on 15.06.2014.
 */
public class GameClient {
    Socket socket;
    InputStream in;
    OutputStream out;
    Scene preGameClientScene;

    public GameClient(Socket client) {
        this.socket=client;
        try {
            out = socket.getOutputStream();
            out.flush();
            in = socket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }



    public Scene getPreGameClientScene(){
        return preGameClientScene;
    }
}