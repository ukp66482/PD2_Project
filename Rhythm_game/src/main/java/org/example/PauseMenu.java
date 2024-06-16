package org.example;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.animation.Timeline;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;

public class PauseMenu {

    public static void pauseGame(Stage stage, Scene gameScene, MediaPlayer mediaPlayer, Timeline clock, List<Timeline> allTimelines) {
        mediaPlayer.pause();
        if (clock != null) {
            clock.pause();
        }
        allTimelines.forEach(timeline -> {
            if (timeline.getStatus() == Timeline.Status.RUNNING) {
                timeline.pause();
            }
        });

        VBox pauseMenu = new VBox(20);
        pauseMenu.setAlignment(Pos.CENTER);

        Button continueButton = new Button("繼續遊戲");
        Button mainMenuButton = new Button("回到主選單");
        continueButton.setMinSize(200, 60);
        continueButton.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        mainMenuButton.setMinSize(200, 60);
        mainMenuButton.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        continueButton.setOnAction(event -> {
            mediaPlayer.play();
            if (clock != null) {
                clock.play();
            }
            allTimelines.forEach(timeline -> {
                if (timeline.getStatus() == Timeline.Status.PAUSED) {
                    timeline.play();
                }
            });
            stage.setScene(gameScene);
        });

        mainMenuButton.setOnAction(event -> {
            // 停止所有的Timeline
            allTimelines.forEach(Timeline::stop);
            allTimelines.clear();  // 清空列表

            // 重置時間標籤
            GameScreen.resetTimerLabel();

            stage.setScene(Main.createMainMenuScene(stage));
        });

        pauseMenu.getChildren().addAll(continueButton, mainMenuButton);

        Scene pauseScene = new Scene(new StackPane(pauseMenu), 800, 600);
        stage.setScene(pauseScene);
    }
}

