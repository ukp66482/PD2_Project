package org.example;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class EndScreen {

    public static void endGame(Stage stage, Scene gameScene, int score) {
        VBox endMenu = new VBox(20);
        endMenu.setAlignment(Pos.CENTER);

        Label finalScoreLabel = new Label(String.format("Final Score: %d", score));
        finalScoreLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        Button mainMenuButton = new Button("回到主選單");
        mainMenuButton.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        mainMenuButton.setOnAction(event -> stage.setScene(Main.createMainMenuScene(stage)));

        endMenu.getChildren().addAll(finalScoreLabel, mainMenuButton);

        Scene endScene = new Scene(new StackPane(endMenu), 800, 600);
        stage.setScene(endScene);
    }
}
