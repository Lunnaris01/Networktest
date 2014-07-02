package game;

import javafx.concurrent.Task;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by Julian on 15.06.2014.
 */
public class GameServer {
    ServerSocket server;
    LinkedList<GameServerThread> clients=new LinkedList<>();
    Thread connectionListener;

    public GameServer(int port) {
        try {
            server = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Server konnte nicht erstellt werden!");
        }
        connectionListener=initConnectionListerener();
        connectionListener.start();
    }

    private Thread initConnectionListerener() {
        Task task = new Task() {
            @Override
            protected Void call() throws Exception {
                while(true){
                    System.out.println("Waiting for Clients...");
                    Socket client = server.accept();
                    System.out.println("accepting...");
                    handleConnection(client);

                }
            }
        };
        return new Thread(task);
    }


    private void handleConnection(Socket connectedClient) {
        GameServerThread clientThread = new GameServerThread(connectedClient,this);
        clients.add(clientThread);
        System.out.println("Client hinzugefügt");
        System.out.println(clients.toString());
    }

    public void handleMsg(String s, GameServerThread gameServerThread) {
        if(gameServerThread==getActiveGameServerThread())
        switch (s.substring(0,3)){
            case "MAP":
                handleMap(s.substring(5));
                break;
            case "ACT":
                handleActors(s.substring(5));
                break;
            case "PRO":
                handleProjectiles(s.substring(5));
                break;
        }
    }

    private void handleProjectiles(String substring) {

    }

    private void handleActors(String substring) {

    }

    private GameServerThread getActiveGameServerThread() {
        return null;
    }

    private void handleMap(String substring) {
    }

    private void handleevent(String substring) {
    }
}
