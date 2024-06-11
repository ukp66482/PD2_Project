package org.example;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {

        BorderPane root = new BorderPane();

        // Create a HBox to hold the buttons and lines
        HBox buttonBox = new HBox(0); // spacing = 0
        buttonBox.setAlignment(Pos.BOTTOM_LEFT);
        buttonBox.setStyle("-fx-background-color: rgba(255,255,255,0);");

        Button btn1 = new Button();
        Button btn2 = new Button();
        Button btn3 = new Button();
        Button btn4 = new Button();

        Line line1 = new Line();
        Line line2 = new Line();
        Line line3 = new Line();
        Line line4 = new Line();

        String buttonStyle =
                "-fx-background-color: rgba(255,255,255,0);" +
                        "-fx-border-color: black;" +
                        "-fx-border-width: 1px;" +
                        "-fx-text-fill: transparent;";

        String buttonPressedStyle =
                "-fx-background-color: rgb(255,0,0);" +
                        "-fx-border-color: black;" +
                        "-fx-border-width: 1px;" +
                        "-fx-text-fill: transparent;";

        btn1.setStyle(buttonStyle);
        btn2.setStyle(buttonStyle);
        btn3.setStyle(buttonStyle);
        btn4.setStyle(buttonStyle);

        // Bind the button size to the scene size
        btn1.minWidthProperty().bind(Bindings.divide(primaryStage.widthProperty(), 5));
        btn2.minWidthProperty().bind(Bindings.divide(primaryStage.widthProperty(), 5));
        btn3.minWidthProperty().bind(Bindings.divide(primaryStage.widthProperty(), 5));
        btn4.minWidthProperty().bind(Bindings.divide(primaryStage.widthProperty(), 5));

        btn1.minHeightProperty().bind(Bindings.divide(primaryStage.heightProperty(), 10));
        btn2.minHeightProperty().bind(Bindings.divide(primaryStage.heightProperty(), 10));
        btn3.minHeightProperty().bind(Bindings.divide(primaryStage.heightProperty(), 10));
        btn4.minHeightProperty().bind(Bindings.divide(primaryStage.heightProperty(), 10));

        // Bind the line height to the scene height
        line1.endYProperty().bind(primaryStage.heightProperty());
        line2.endYProperty().bind(primaryStage.heightProperty());
        line3.endYProperty().bind(primaryStage.heightProperty());
        line4.endYProperty().bind(primaryStage.heightProperty());

        buttonBox.getChildren().addAll(btn1, line1, btn2, line2, btn3, line3, btn4, line4);

        VBox vBox = new VBox();
        vBox.setAlignment(Pos.BOTTOM_CENTER);
        vBox.getChildren().add(buttonBox);

        root.setBottom(vBox);

        Scene scene = new Scene(root, 1000, 800, Color.BLACK);

        // Set up key event handlers
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.A) {
                btn1.setStyle(buttonPressedStyle);
            } else if (e.getCode() == KeyCode.S) {
                btn2.setStyle(buttonPressedStyle);
            } else if (e.getCode() == KeyCode.K) {
                btn3.setStyle(buttonPressedStyle);
            } else if (e.getCode() == KeyCode.L) {
                btn4.setStyle(buttonPressedStyle);
            }
        });

        scene.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.A) {
                btn1.setStyle(buttonStyle);
            } else if (e.getCode() == KeyCode.S) {
                btn2.setStyle(buttonStyle);
            } else if (e.getCode() == KeyCode.K) {
                btn3.setStyle(buttonStyle);
            } else if (e.getCode() == KeyCode.L) {
                btn4.setStyle(buttonStyle);
            }
        });

        primaryStage.setTitle("Rhythm Game");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Request focus on the scene to receive key events
        scene.getRoot().requestFocus();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
