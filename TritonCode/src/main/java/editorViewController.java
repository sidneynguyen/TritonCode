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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.control.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class EditorViewController extends Application{
    @FXML
    private Button newButton;
    @FXML
    private Button saveButton;
    @FXML
    private Button openButton;
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

        testClient = new WebSocketController(new URI("ws://localhost:3000/code"));


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
                fileChooser.setInitialDirectory( new File("~/untitle.txt"));

                currentFile = createdFile;
                System.out.println(createdFile);
                try {
                    if (createdFile.createNewFile()){

                        System.out.println("Created File test.txt");
                    }else {
                        System.out.println("Dup");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                FileWriter writer = null;
                try {
                    writer = new FileWriter(createdFile);
                    writer.close();


                } catch (IOException e) {
                    e.printStackTrace();
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
        HBox buttonLayout = new HBox();
        buttonLayout.getChildren().addAll(newButton,saveButton,openButton);
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
        testClient.addMessageHandler(new WebSocketController.MessageHandler() {
            @Override
            public void handleMessage(String message) {
                System.out.println(message);
                receivedMessage(message);
            }

        });
        layout.setTop(buttonLayout);
        layout.setCenter(editor);


        Scene scene = new Scene(layout, 600, 400);
        layout.setPrefHeight(scene.getHeight());
        layout.setPrefWidth(scene.getWidth());



        primaryStage.setTitle("Triton Code");
        primaryStage.setScene(scene);



        primaryStage.show();


    }

    private void postToServer(String message){
        System.out.println("Posted");
        testClient.sendMessage(message+"\n");
    }
    private void receivedMessage(String message){
        editor.setText(message);
    }
    @FXML
    protected void handleNewButtonAction (ActionEvent event){

        System.out.println("New Button clicked");

    }


}
