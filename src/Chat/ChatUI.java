package Chat;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Created by bjebb on 01.06.14.
 */
public class ChatUI extends Application {
    ChatServer chatServer;
    ChatClient chatClient;
    ToggleButton startChatServer;
    Button startChatClient;
    Button disconnectChat;

    ScrollPane chatContainer;
    VBox chat;

    final String defaultIP = "127.0.0.1", defaultName = "NAME";
    final int defaultPort = 2342;

    @Override
    public void start(Stage stage) throws Exception {
        stage.setOnCloseRequest(event -> {
            try {
                if(chatServer != null)chatServer.close();
                if(chatClient != null)chatClient.close();
                stage.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        stage.setResizable(true);
        System.out.println("ChatUI wurde gestartet!");
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root,300,500);
        stage.setTitle("Chat");

        startChatServer = new ToggleButton("Start Server");
        startChatClient = new Button("Start Client");
        disconnectChat = new Button("Disconnect");
        disconnectChat.setDisable(true);
        HBox startButtons = new HBox(startChatServer,startChatClient,disconnectChat);

        TextField ip = new TextField(defaultIP);
        TextField port = new TextField(Integer.toString(defaultPort));
        port.setDisable(true);
        TextField name = new TextField(defaultName);
        HBox chatData = new HBox(name,ip,port);

        chat = new VBox();
        chatContainer = new ScrollPane(chat);
        chatContainer.setVvalue(1);
        chatContainer.setFocusTraversable(false);
        chatContainer.setOnMouseEntered(event -> {
            chatContainer.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        });
        chatContainer.setOnMouseExited(event -> {
            chatContainer.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        });

        TextField inputField = new TextField();

        //EventHandler for clicking on the "Start Server" Button
        startChatServer.setOnAction(event -> {
            try {
                chatServer = new ChatServer(defaultPort);
                System.out.println(chatServer.server.getLocalSocketAddress());
                startChatServer.setDisable(true);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Server konnte nicht gestartet werden");
            }
        });

        //EventHandler for clicking on the "Start Chat" Button
        startChatClient.setOnAction(event -> {
            try {
                chatClient = new ChatClient(name.getText(),ip.getText(),Integer.parseInt(port.getText()),this);
                startChatClient.setDisable(true);
                disconnectChat.setDisable(false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        disconnectChat.setOnAction(event -> {
            try {
                chatClient.close();
                disconnectChat.setDisable(true);
                startChatClient.setDisable(false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        //EventHandler for pressing Enter while in the InputField
        inputField.setOnKeyPressed(keyEvent -> {
            switch (keyEvent.getCode()) {
                case ENTER:
                    System.out.println("Enter pressed");
                    chatClient.send(inputField.getText());
                    inputField.clear();
            }
        });

        name.setOnKeyPressed(keyEvent -> {
            switch (keyEvent.getCode()){
                case ENTER:
                    chatClient.send("$NAME %" + name.getText());
            }
        });

        root.setTop(new VBox(startButtons, chatData));
        root.setCenter(chatContainer);
        root.setBottom(inputField);
        stage.setScene(scene);
        stage.show();
    }

    protected void addMsg(String s){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                chat.getChildren().add(new Label(s));
            }
        });
    }
}
