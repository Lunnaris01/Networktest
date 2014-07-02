package game;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import javax.swing.*;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by Julian on 15.06.2014.
 */
public class PreUI extends Application {

    Button bserver;
    Button bconnect;
    String ip="localhost";
    int port = 25568;
    GameServer gameServer;


    @Override
    public void start(Stage primaryStage) throws Exception {
        VBox root= new VBox();
        HBox hBox1=new HBox();
        bserver=new Button("Server");
        bconnect=new Button("Connect");
        hBox1.getChildren().addAll(bserver,bconnect);
        primaryStage.setOnCloseRequest(event -> {
            try {
                gameServer.server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        root.getChildren().addAll(hBox1);

        bconnect.setOnAction(event -> {
            try {
                GameClient gameClient = new GameClient(new Socket(ip,port));
                //primaryStage.setScene(gameClient.getGameClientScene());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        bserver.setOnAction(event -> {
        gameServer = new GameServer(port);

        });









        Scene scene= new Scene(root,300,300);
        primaryStage.setScene(scene);
        primaryStage.show();

    }
}
