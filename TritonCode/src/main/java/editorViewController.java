import  javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class editorViewController extends Application{
    @FXML
    private Button newButton;
    @FXML
    private Button saveButton;
    @FXML
    private Button openButton;
    @FXML
    private TextArea editor;

    public static void main(String[] args) {
        System.out.println("Hello World");

        launch(args );
    }

    @Override
    public void start (Stage primaryStage) throws Exception{

        Parent root = FXMLLoader.load(getClass().getResource("textEditor.fxml"));

        newButton = new Button("New File");

        newButton.setOnAction(new EventHandler<javafx.event.ActionEvent>() {
            @Override
            public void handle(javafx.event.ActionEvent event) {
                Path currentPath = Paths.get("");
                String pathString = currentPath.toAbsolutePath().toString();
                System.out.println(pathString);
                File file = new File(""+pathString+"/test.txt");
                try {
                    if (file.createNewFile()){

                        System.out.println("Created File test.txt");
                    }else {
                        System.out.println("Dup");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                FileWriter writer = null;
                try {
                    writer = new FileWriter(file);
                    writer.write("fdskjhfs\tdjf\njksadhf");
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
                System.out.println("save");
            }
        });

        openButton = new Button("Open");
        openButton.setOnAction(new EventHandler<javafx.event.ActionEvent>() {
            @Override
            public void handle(javafx.event.ActionEvent event) {
                FileChooser fileChooser = new FileChooser();
                File selectedFile = fileChooser.showOpenDialog(primaryStage);
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


        layout.setTop(buttonLayout);
        layout.setCenter(editor);


        Scene scene = new Scene(layout, 600, 400);
        layout.setPrefHeight(scene.getHeight());
        layout.setPrefWidth(scene.getWidth());



        primaryStage.setTitle("Triton Code");
        primaryStage.setScene(scene);



        primaryStage.show();


    }

    @FXML
    protected void handleNewButtonAction (ActionEvent event){

        System.out.println("New Button clicked");

    }


}
