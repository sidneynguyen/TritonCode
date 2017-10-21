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
import javafx.stage.Stage;
import javafx.scene.control.*;

import java.awt.*;
import java.awt.event.ActionEvent;

public class editorViewController extends Application{
    @FXML
    private Button newButton;
    @FXML
    private Button saveButton;
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
                System.out.println("New FIle");
            }
        });

        saveButton = new Button("Save");
        saveButton.setOnAction(new EventHandler<javafx.event.ActionEvent>() {
            @Override
            public void handle(javafx.event.ActionEvent event) {
                System.out.println("save");
            }
        });
        HBox buttonLayout = new HBox();
        buttonLayout.getChildren().addAll(newButton,saveButton);
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
