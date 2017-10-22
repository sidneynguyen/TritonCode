import  javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.List;

public class EditorViewController extends Application{
    @FXML
    private Button newButton;
    @FXML
    private Button saveButton;
    @FXML
    private Button openButton;
    @FXML
    private Button startButton;
    @FXML
    private Button connectButton;
    @FXML
    private TextArea editor;

    private WebSocketController testClient;

    private File currentFile;
    public static void main(String[] args) throws Exception{
        System.out.println("Hello World");

        launch(args );

    }

    @Override
    public void start (Stage primaryStage) throws Exception{

        //testClient = new WebSocketController(new URI("3a31f62f.ngrok.io"));


//        testClient.sendMessage("START:1234\nabc");
//        System.out.println("START:1234\nabc");
//        //Thread.sleep(1000);
//        testClient.sendMessage("DOCUMENT\n1234\n4321\n1234\nIx,R3,\n");
//        System.out.println("DOCUMENT\n1234\n4321\n1234\nIx,R3,\n");


        Parent root = FXMLLoader.load(getClass().getResource("textEditor.fxml"));

        newButton = new Button("New File");

        newButton.setOnAction(new EventHandler<javafx.event.ActionEvent>() {
            @Override
            public void handle(javafx.event.ActionEvent event) {
                FileChooser fileChooser = new FileChooser();
                File createdFile = fileChooser.showSaveDialog(primaryStage);
                fileChooser.setInitialDirectory(new File("~/untitle.txt"));

                currentFile = createdFile;
                System.out.println(createdFile);
                if (createdFile != null) {
                    try {
                        if (createdFile.createNewFile()) {

                            System.out.println("Created File test.txt");
                        } else {
                            System.out.println("Dup");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

//                FileWriter writer = null;
//                try {
//                    writer = new FileWriter(createdFile);
//                    writer.close();
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }


                }
            }
        });

        saveButton = new Button("Save");
        saveButton.setOnAction(new EventHandler<javafx.event.ActionEvent>() {
            @Override
            public void handle(javafx.event.ActionEvent event) {
                if (currentFile != null){
                    FileWriter writer = null;
                    try {
                        writer = new FileWriter(currentFile);
                        writer.write(editor.getText());
                        writer.close();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        openButton = new Button("Open");
        openButton.setOnAction(new EventHandler<javafx.event.ActionEvent>() {
            @Override
            public void handle(javafx.event.ActionEvent event) {
                FileChooser fileChooser = new FileChooser();
                File selectedFile = fileChooser.showOpenDialog(primaryStage);
                currentFile = selectedFile;
                if (selectedFile != null){
                    try {
                        String text = "";
                        List<String> lines = Files.readAllLines(selectedFile.toPath());
                        for(int i = 0; i < lines.size(); i++) {
                            if(i == 0) {
                                text = lines.get(i);
                            } else {
                                text = text + "\n" + lines.get(i);
                            }
                        }
                        editor.setText(text);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        });

        startButton = new Button("Start");
        startButton.setOnAction(new EventHandler<javafx.event.ActionEvent>() {
            @Override
            public void handle(javafx.event.ActionEvent event) {
                start(editor.getText());
            }
        });

        connectButton = new Button("Connect");
        connectButton.setOnAction(new EventHandler<javafx.event.ActionEvent>() {
            @Override
            public void handle(javafx.event.ActionEvent event) {
                connect(editor.getText());
            }
        });

        HBox buttonLayout = new HBox();
        buttonLayout.getChildren().addAll(newButton,saveButton,openButton, startButton, connectButton);
        BorderPane layout = new BorderPane();

        editor = new TextArea();
        editor.setPrefSize(layout.getWidth(), layout.getHeight());
        editor.setWrapText(true);

        editor.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                postToServer(editor.getText());
            }
        });
        /*testClient.addMessageHandler(new WebSocketController.MessageHandler() {
            @Override
            public void handleMessage(String message) {
                System.out.println(message);
                receivedMessage(message);
            }

        });*/
        WebView chatWindow = new WebView();
        WebEngine webEngine = chatWindow.getEngine();
        webEngine.load("http://d638a152.ngrok.io");
        chatWindow.setPrefHeight(editor.getPrefHeight());
        chatWindow.setPrefWidth(150);
        layout.setTop(buttonLayout);
        layout.setCenter(editor);
        layout.setRight(chatWindow);



        Scene scene = new Scene(layout, 600, 400);
        layout.setPrefHeight(scene.getHeight());
        layout.setPrefWidth(scene.getWidth());



        primaryStage.setTitle("Triton Code");
        primaryStage.setScene(scene);



        primaryStage.show();


    }

    public String getEditorText() {
        return editor.getText();
    }

    private void start(String content) {
        try {
            testClient = new WebSocketController(new URI("ws://localhost:3000/code"), currentFile.getName(), content, this);
            testClient.addMessageHandler(new WebSocketController.MessageHandler() {
                @Override
                public void handleMessage(String message) {
                    System.out.println(message);
                    //receivedMessage(message);
                }

            });
            testClient.sendMessage("START:" + currentFile.getName() + "\n" + content);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void connect(String content) {
        try {
            testClient = new WebSocketController(new URI("ws://localhost:3000/code"), currentFile.getName(), content, this);
            testClient.addMessageHandler(new WebSocketController.MessageHandler() {
                @Override
                public void handleMessage(String message) {
                    System.out.println(message);
                    //receivedMessage(message);
                }

            });
            testClient.sendMessage("CONNECT\n" + currentFile.getName() + "\n");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void postToServer(String message){
        //System.out.println("Posted");

        //testClient.sendMessage(message+"\n");
        testClient.sendEdit(editor.getText());
    }

    public void receivedMessage(String message) {
        int position = editor.getCaretPosition();
        editor.setText(message);
        editor.positionCaret(position);
    }

    @FXML
    protected void handleNewButtonAction (ActionEvent event){

        System.out.println("New Button clicked");

    }


}
