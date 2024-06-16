package org.example;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GameScreen {
    private static Pane playArea;
    private static Rectangle[] triggers = new Rectangle[4];
    private static List<Timeline> allTimelines = new ArrayList<>();
    private static double fallDistance = 550;
    private static double movementBit = 3;
    private static double fps = 10;
    private static double fallTimeInSeconds = fallDistance / (movementBit * (1000 / fps));
    private static String filePath = "src/main/resources/beatmap.txt";
    private static String musicPath = "src/main/resources/music.mp3";
    private static Label timerLabel;
    private static long startTime;
    private static MediaPlayer mediaPlayer;
    private static Timeline clock;
    private static int totalBlocks = 0;
    private static int hitBlocks = 0;
    private static int comboCount = 0;
    private static int maxComboCount = 0;
    private static int score = 0;
    private static Label accLabel;
    private static Label comboLabel;
    private static Label scoreLabel;

    public static void switchToGameScreen(Stage stage) {
        playArea = new Pane();

        setupTriggers();
        setupAccLabel();
        setupComboLabel();
        setupScoreLabel();
        timerLabel = new Label("00:00:00");
        timerLabel.setLayoutX(700);
        timerLabel.setLayoutY(20);
        timerLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: black; -fx-background-color: rgba(255, 255, 255, 0.7); -fx-background-insets: -5px; -fx-background-radius: 5px;");
        playArea.getChildren().add(timerLabel);

        Scene scene = new Scene(playArea, 800, 600);
        scene.setOnKeyPressed(event -> {
            handleKeyPress(event.getCode());
            if (event.getCode() == KeyCode.ESCAPE) {
                PauseMenu.pauseGame(stage, scene, mediaPlayer, clock, allTimelines);
            }
        });

        stage.setTitle("Rhythm Game");
        stage.setScene(scene);
        stage.show();

        setupMediaPlayer(stage, scene);
        startTimerWithOffset();
        startGame();
    }

    private static void setupTriggers() {
        triggers = new Rectangle[7];
        double triggerWidth = 800.0 / triggers.length;
        Color[] colors = {Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.BLUE, Color.INDIGO, Color.VIOLET};

        for (int i = 0; i < triggers.length; i++) {
            Rectangle background = new Rectangle(i * triggerWidth, 0, triggerWidth, 600);
            background.setFill(colors[i]);
            background.setOpacity(0.25);
            playArea.getChildren().add(background);
        }

        for (int i = 0; i < triggers.length; i++) {
            Rectangle trigger = new Rectangle(i * triggerWidth, 550, triggerWidth, 50);
            trigger.setStroke(Color.BLACK);
            trigger.setFill(Color.BLACK);
            trigger.setOpacity(0.3);
            playArea.getChildren().add(trigger);
            triggers[i] = trigger;
        }
    }

    private static void startGame() {
        score = 0;
        totalBlocks = 0;
        hitBlocks = 0;
        comboCount = 0;
        maxComboCount = 0;
        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath));

            for (String line : lines) {
                String[] parts = line.split(" ");
                double timeInSeconds = Double.parseDouble(parts[0]);
                int columnIndex = Integer.parseInt(parts[1]) - 1;

                double adjustedTimeInSeconds = timeInSeconds;

                Timeline generateTimeline = new Timeline(new KeyFrame(Duration.seconds(adjustedTimeInSeconds), e -> generateBlock(columnIndex)));
                generateTimeline.play();
                allTimelines.add(generateTimeline);
            }
        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
        }
    }

    private static void setupAccLabel() {
        accLabel = new Label("Accuracy: 0%");
        accLabel.setLayoutX(20);
        accLabel.setLayoutY(20);
        accLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: black; -fx-background-color: rgba(255, 255, 255, 0.7); -fx-background-insets: -5px; -fx-background-radius: 5px;");
        playArea.getChildren().add(accLabel);
    }

    private static void setupComboLabel() {
        comboLabel = new Label("Combo: 0");
        comboLabel.setLayoutX(20);
        comboLabel.setLayoutY(60);
        comboLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: black; -fx-background-color: rgba(255, 255, 255, 0.7); -fx-background-insets: -5px; -fx-background-radius: 5px;");
        playArea.getChildren().add(comboLabel);
    }

    private static void setupScoreLabel() {
        scoreLabel = new Label("Score: 0");
        scoreLabel.setLayoutX(20);
        scoreLabel.setLayoutY(100);
        scoreLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: black; -fx-background-color: rgba(255, 255, 255, 0.7); -fx-background-insets: -5px; -fx-background-radius: 5px;");
        playArea.getChildren().add(scoreLabel);
    }

    private static void updateScore(boolean hit) {
        totalBlocks++;
        if (hit) {
            hitBlocks++;
            comboCount++;
            score += 1 * (1 + comboCount / 10);
            if (comboCount > maxComboCount) {
                maxComboCount = comboCount;
            }
        } else {
            comboCount = 0;
        }
        double accuracy = (double) hitBlocks / totalBlocks * 100;
        accLabel.setText(String.format("Accuracy: %.2f%%", accuracy));
        comboLabel.setText(String.format("Combo: %d", comboCount));
        scoreLabel.setText(String.format("Score: %d", score));
    }

    private static void setupMediaPlayer(Stage stage, Scene scene) {
        String musicFile = musicPath;
        Media media = new Media(new File(musicFile).toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setOnEndOfMedia(() -> {
            mediaPlayer.stop();
            EndScreen.endGame(stage, scene, score);
        });
    }

    private static void startTimerWithOffset() {
        long startTime = System.currentTimeMillis();

        Timeline delayTimeline = new Timeline(new KeyFrame(Duration.seconds(fallTimeInSeconds), e -> {
            mediaPlayer.play();
            startRealTimeTimer(startTime);
        }));
        delayTimeline.play();
        allTimelines.add(delayTimeline);
    }

    private static void startRealTimeTimer(long startTime) {
        Timeline realTimeTimeline = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            long currentTime = System.currentTimeMillis();
            long elapsedMillis = currentTime - startTime;
            timerLabel.setText(formatTime(elapsedMillis));
        }), new KeyFrame(Duration.seconds(1)));
        realTimeTimeline.setCycleCount(Timeline.INDEFINITE);
        realTimeTimeline.play();
        allTimelines.add(realTimeTimeline);
        clock = realTimeTimeline;
    }

    private static String formatTime(long millis) {
        long seconds = (millis / 1000) % 60;
        long minutes = (millis / (1000 * 60)) % 60;
        long hours = (millis / (1000 * 60 * 60)) % 24;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    private static void generateBlock(int columnIndex) {
        double triggerWidth = 800.0 / triggers.length;
        Rectangle block = new Rectangle(columnIndex * triggerWidth, 0, triggerWidth, 50);
        block.setFill(Color.BLACK);
        block.setUserData("active");
        playArea.getChildren().add(block);

        Timeline fall = new Timeline(new KeyFrame(Duration.millis(fps), e -> {
            block.setY(block.getY() + movementBit);

            if (block.getY() >= triggers[columnIndex].getY() + triggers[columnIndex].getHeight()) {
                if ("active".equals(block.getUserData())) {
                    updateScore(false);
                    block.setUserData("missed");
                    block.setFill(Color.RED);
                }
            }
        }));
        fall.setCycleCount(Timeline.INDEFINITE);
        fall.play();
        allTimelines.add(fall);
    }

    private static void handleKeyPress(KeyCode key) {
        int index = switch (key) {
            case A -> 0;
            case S -> 1;
            case D -> 2;
            case SPACE -> 3;
            case J -> 4;
            case K -> 5;
            case L -> 6;
            default -> -1;
        };

        if (index != -1) {
            List<Rectangle> blocks = playArea.getChildren().stream()
                    .filter(n -> n instanceof Rectangle && n != triggers[index])
                    .map(n -> (Rectangle) n)
                    .collect(Collectors.toList());

            for (Rectangle block : blocks) {
                if ("active".equals(block.getUserData()) &&
                        block.getX() >= index * (800.0 / triggers.length) &&
                        block.getX() < (index + 1) * (800.0 / triggers.length) &&
                        block.getY() + block.getHeight() >= triggers[index].getY() &&
                        block.getY() <= triggers[index].getY() + triggers[index].getHeight()) {
                    block.setFill(Color.GRAY);
                    updateScore(true);
                    block.setUserData("hit");
                    return;
                }
            }
            updateScore(false);
        }
    }
}

