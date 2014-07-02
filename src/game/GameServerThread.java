package game;

import java.io.*;
import java.net.Socket;

/**
 * Created by Julian on 02.07.2014.
 */
public class GameServerThread extends Thread {
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    GameServer gameServer;

    public GameServerThread(Socket connectedClient, GameServer gameServer) {
        this.socket = connectedClient;
        this.gameServer = gameServer;
        try {
            out = new DataOutputStream(socket.getOutputStream());
            out.flush();
            in = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        boolean crashed = false;
        while (!crashed) {
            System.out.println("[SERVER] Server listening...");
            try {
                gameServer.handleMsg(in.readUTF(),this);
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }
}
