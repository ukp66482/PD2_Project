package org.example;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import java.io.IOException;
import java.io.File;


public class Main extends Application {
    private Pane playArea;
    private Rectangle[] triggers = new Rectangle[4];

    
    private double fallDistance = 550; // 方块的下落距离到触发区
    private double movementBit = 3; // 每次刷新移动的像素数
    private double fps = 10; // 每秒帧数
    private double fallTimeInSeconds = fallDistance / (movementBit * (1000/fps));

    private String filePath="src/main/resources/beatmap.txt";
    private String musicPath="src/main/resources/star.mp3";

    private Label timerLabel;
    private long startTime;

    private MediaPlayer mediaPlayer;
    private Timeline clock;

    private int totalBlocks = 0; // 总共生成的方块数
    private int hitBlocks = 0; // 玩家成功按到的方块数
    private Label scoreLabel; // 显示分数的标签

    @Override
    public void start(Stage primaryStage) {
        //BeatMapGen.beatMapGenerator();
        playArea = new Pane();

        setupTriggers();
        setupScoreLabel(); // 设置记分板
        timerLabel = new Label("00:00:00");
        timerLabel.setLayoutX(700); // 设置位置
        timerLabel.setLayoutY(20);
        timerLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: black; -fx-background-color: white;");
        playArea.getChildren().add(timerLabel);

        Scene scene = new Scene(playArea, 800, 600);
        scene.setOnKeyPressed(event -> handleKeyPress(event.getCode()));

        primaryStage.setTitle("Rhythm Game");
        primaryStage.setScene(scene);
        primaryStage.show();

        setupMediaPlayer();
        startTimerWithOffset();
        startGame();
    }


    private void setupTriggers() {
        triggers = new Rectangle[7];  // 初始化为 7 个触发区
        double triggerWidth = 800.0 / triggers.length;  // 平分窗口宽度
    
        for (int i = 0; i < triggers.length; i++) {
            Rectangle trigger = new Rectangle(i * triggerWidth, 550, triggerWidth, 50);
            trigger.setStroke(Color.BLUE);
            trigger.setFill(null);
            playArea.getChildren().add(trigger);
            triggers[i] = trigger;
        }
    }
    

    private void startGame() {
        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath));
    
            for (String line : lines) {
                String[] parts = line.split(" ");
                double timeInSeconds = Double.parseDouble(parts[0]);
                int columnIndex = Integer.parseInt(parts[1]);
    
                // 将下落时间加到指定的生成时间上
                double adjustedTimeInSeconds = timeInSeconds ;
    
                Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(adjustedTimeInSeconds), e -> generateBlock(columnIndex)));
                timeline.play();
            }
        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
            // 处理异常，例如显示警告或记录日志
        }
    }

    private void setupScoreLabel() {
        scoreLabel = new Label("Accuracy: 0%");
        scoreLabel.setLayoutX(600); // 设置位置
        scoreLabel.setLayoutY(50);
        scoreLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: black; -fx-background-color: white;");
        playArea.getChildren().add(scoreLabel);
    }
    
    private void updateScore(boolean hit) {
        totalBlocks++; // 总方块数始终增加
        if (hit) {
            hitBlocks++; // 如果按到方块，增加成功击中的计数
        }
        double accuracy = (double) hitBlocks / totalBlocks * 100;
        scoreLabel.setText(String.format("Accuracy: %.2f%%", accuracy)); // 更新标签显示得分
    }
    

    private void setupMediaPlayer() {
        String musicFile = musicPath;  // 音乐文件的路径
        Media media = new Media(new File(musicFile).toURI().toString());
        mediaPlayer = new MediaPlayer(media);
    }

    private void startTimerWithOffset() {
        // 设置计时器从 0 开始
        long startTime = System.currentTimeMillis();
        
        // 延迟开始音乐和计时器
        Timeline delayTimeline = new Timeline(new KeyFrame(Duration.seconds(fallTimeInSeconds), e -> {
            mediaPlayer.play(); // 播放音乐
            startRealTimeTimer(startTime); // 传入实际开始的时间
        }));
        delayTimeline.play();
    }
    private void startRealTimeTimer(long startTime) {
        Timeline realTimeTimeline = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            long currentTime = System.currentTimeMillis();
            long elapsedMillis = currentTime - startTime; // 从 startTime 开始计算已过时间
            timerLabel.setText(formatTime(elapsedMillis));
        }), new KeyFrame(Duration.seconds(1)));
        realTimeTimeline.setCycleCount(Timeline.INDEFINITE);
        realTimeTimeline.play();
    }

    private String formatTime(long millis) {
        long seconds = (millis / 1000) % 60;
        long minutes = (millis / (1000 * 60)) % 60;
        long hours = (millis / (1000 * 60 * 60)) % 24;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }




    private void generateBlock(int columnIndex) {
        double triggerWidth = 800.0 / triggers.length;
        Rectangle block = new Rectangle(columnIndex * triggerWidth, 0, triggerWidth, 50);
        block.setFill(Color.BLACK);
        block.setUserData("active");
        playArea.getChildren().add(block);
    
        Timeline fall = new Timeline(new KeyFrame(Duration.millis(fps), e -> {
            block.setY(block.getY() + movementBit);
            if (block.getY() > playArea.getHeight()) {
                if ("active".equals(block.getUserData())) {
                    block.setUserData("counted");
                    updateScore(false);
                }
                playArea.getChildren().remove(block);
            }
        }));
        fall.setCycleCount(Timeline.INDEFINITE);
        fall.play();
    }
    
    
    

    private void handleKeyPress(KeyCode key) {
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
                if (block.getX() >= index * (800.0 / triggers.length) && block.getX() < (index + 1) * (800.0 / triggers.length) && block.getY() + block.getHeight() >= triggers[index].getY()) {
                    block.setUserData("hit"); // 标记方块已被击中
                    playArea.getChildren().remove(block);
                    updateScore(true);
                    break;
                }
            }
        }
    }
    
    
    
    public static void main(String[] args) {
        launch(args);
    }
}