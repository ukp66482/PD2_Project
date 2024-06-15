package org.example;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.io.IOException;
import java.io.File;

public class Main extends Application {
    private Pane playArea;
    private Rectangle[] triggers = new Rectangle[4];
    private List<Timeline> allTimelines = new ArrayList<>(); // 保存所有 Timeline，包括方塊下落和生成

    private double fallDistance = 550; // 方塊的下落距離到觸發區
    private double movementBit = 3; // 每次刷新移動的像素數
    private double fps = 10; // 每秒幀數
    private double fallTimeInSeconds = fallDistance / (movementBit * (1000/fps));

    private String filePath="src/main/resources/beatmap.txt";
    private String musicPath="src/main/resources/star.mp3";

    private Label timerLabel;
    private long startTime;

    private MediaPlayer mediaPlayer;
    private Timeline clock;

    private int totalBlocks = 0; // 總共生成的方塊數
    private int hitBlocks = 0; // 玩家成功按到的方塊數
    private int comboCount = 0; // 當前連擊數
    private int maxComboCount = 0;
    private int score = 0;
    private Label accLabel; // 顯示分數的標籤
    private Label comboLabel; // 顯示連擊數的標籤
    private Label scoreLabel;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("別踩白塊兒");

        // 創建按鈕
        Button startGameButton = new Button("開始遊戲");
        Button loadMusicButton = new Button("讀取音樂");
        startGameButton.setMinSize(200, 60);
        loadMusicButton.setMinSize(200, 60);
        startGameButton.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        loadMusicButton.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        // 設置按鈕的動作事件
        startGameButton.setOnAction(event -> {
            System.out.println("遊戲開始!");
            switchToGameScreen(primaryStage);
        });

        loadMusicButton.setOnAction(event -> {
            BeatMapGen.beatMapGenerator();
        });

        VBox vbox = new VBox(20, startGameButton, loadMusicButton);
        vbox.setAlignment(Pos.CENTER);

        // 創建一個 StackPane 使 VBox 居中
        StackPane root = new StackPane(vbox);
        root.setAlignment(Pos.CENTER);
        root.setPrefSize(800, 600);

        Scene startScene = new Scene(root);
        primaryStage.setScene(startScene);

        primaryStage.show();
    }

    private void pauseGame(Stage stage, Scene gameScene) {
        mediaPlayer.pause(); // 暫停音樂
        if (clock != null) {
            clock.pause(); // 暫停計時器
        }
        allTimelines.forEach(timeline -> {
            if (timeline.getStatus() == Timeline.Status.RUNNING) {
                timeline.pause(); // 暫停運行中的 Timeline
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
            mediaPlayer.play(); // 繼續音樂
            if (clock != null) {
                clock.play(); // 繼續計時器
            }
            allTimelines.forEach(timeline -> {
                if (timeline.getStatus() == Timeline.Status.PAUSED) {
                    timeline.play(); // 繼續之前暫停的 Timeline
                }
            });
            stage.setScene(gameScene); // 返回遊戲場景
        });

        mainMenuButton.setOnAction(event -> {
            stage.setScene(createMainMenuScene(stage)); // 返回主選單
        });

        pauseMenu.getChildren().addAll(continueButton, mainMenuButton);

        Scene pauseScene = new Scene(new StackPane(pauseMenu), 800, 600);
        stage.setScene(pauseScene);
    }

    private void switchToGameScreen(Stage stage) {
        playArea = new Pane();

        setupTriggers();
        setupAccLabel(); // 設置記分板
        setupComboLabel(); // 設置連擊標籤
        setupScoreLabel();
        timerLabel = new Label("00:00:00");
        timerLabel.setLayoutX(700); // 設置位置
        timerLabel.setLayoutY(20);
        timerLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: black; -fx-background-color: rgba(255, 255, 255, 0.7); -fx-background-insets: -5px; -fx-background-radius: 5px;");
        playArea.getChildren().add(timerLabel);

        Scene scene = new Scene(playArea, 800, 600);
        scene.setOnKeyPressed(event -> {
            handleKeyPress(event.getCode());
            if (event.getCode() == KeyCode.ESCAPE) {
                pauseGame(stage, scene); // 當按下 ESC 鍵時，暫停遊戲
            }
        });

        stage.setTitle("Rhythm Game");
        stage.setScene(scene);
        stage.show();

        setupMediaPlayer(stage, scene); // 傳遞 stage 和 scene 參數
        startTimerWithOffset();
        startGame();
    }

    private Scene createMainMenuScene(Stage stage) {
        // 創建主選單場景的邏輯，與 start 方法中的主選單邏輯類似
        Button startGameButton = new Button("開始遊戲");
        Button loadMusicButton = new Button("讀取音樂");
        startGameButton.setMinSize(200, 60);
        loadMusicButton.setMinSize(200, 60);
        startGameButton.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        loadMusicButton.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        startGameButton.setOnAction(event -> {
            System.out.println("遊戲開始!");
            switchToGameScreen(stage);
        });

        loadMusicButton.setOnAction(event -> {
            BeatMapGen.beatMapGenerator();
        });

        VBox vbox = new VBox(20, startGameButton, loadMusicButton);
        vbox.setAlignment(Pos.CENTER);

        StackPane root = new StackPane(vbox);
        root.setAlignment(Pos.CENTER);
        root.setPrefSize(800, 600);

        return new Scene(root);
    }

    private void setupTriggers() {
        triggers = new Rectangle[7]; // 初始化為 7 個觸發區
        double triggerWidth = 800.0 / triggers.length; // 平分窗口寬度
        Color[] colors = {Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.BLUE, Color.INDIGO, Color.VIOLET}; // 定義顏色陣列

        // 添加背景矩形
        for (int i = 0; i < triggers.length; i++) {
            Rectangle background = new Rectangle(i * triggerWidth, 0, triggerWidth, 600); // 設置矩形高度以覆蓋整個畫面
            background.setFill(colors[i]); // 設置不同的背景顏色
            background.setOpacity(0.25); // 設置透明度，讓背景顏色不會太亮
            playArea.getChildren().add(background);
        }

        // 添加觸發區域矩形
        for (int i = 0; i < triggers.length; i++) {
            Rectangle trigger = new Rectangle(i * triggerWidth, 550, triggerWidth, 50); // 設置觸發區域的矩形
            trigger.setStroke(Color.BLACK); // 設置邊框顏色
            trigger.setFill(Color.BLACK); // 設置填充顏色
            trigger.setOpacity(0.3);
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
                int columnIndex = Integer.parseInt(parts[1]) - 1;
    
                // 將下落時間加到指定的生成時間上
                double adjustedTimeInSeconds = timeInSeconds;
    
                Timeline generateTimeline = new Timeline(new KeyFrame(Duration.seconds(adjustedTimeInSeconds), e -> generateBlock(columnIndex)));
                generateTimeline.play();
                allTimelines.add(generateTimeline); // 保存每個方塊生成的 Timeline
            }
        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
            // 處理異常，例如顯示警告或記錄日誌
        }
    }

    private void setupAccLabel() {
        accLabel = new Label("Accuracy: 0%");
        accLabel.setLayoutX(20); // 設置位置
        accLabel.setLayoutY(20);
        accLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: black; -fx-background-color: rgba(255, 255, 255, 0.7); -fx-background-insets: -5px; -fx-background-radius: 5px;");
        playArea.getChildren().add(accLabel);
    }

    private void setupComboLabel() {
        comboLabel = new Label("Combo: 0");
        comboLabel.setLayoutX(20); // 設置位置
        comboLabel.setLayoutY(60);
        comboLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: black; -fx-background-color: rgba(255, 255, 255, 0.7); -fx-background-insets: -5px; -fx-background-radius: 5px;");
        playArea.getChildren().add(comboLabel);
    }

    private void setupScoreLabel() {
        scoreLabel = new Label("Score: 0");
        scoreLabel.setLayoutX(20); // 設置位置
        scoreLabel.setLayoutY(100);
        scoreLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: black; -fx-background-color: rgba(255, 255, 255, 0.7); -fx-background-insets: -5px; -fx-background-radius: 5px;");
        playArea.getChildren().add(scoreLabel);
    }

    private void updateScore(boolean hit) {
        totalBlocks++; // 總方塊數始終增加
        if (hit) {
            hitBlocks++; // 如果按到方塊，增加成功擊中的計數
            comboCount++; // 連擊數增加
            score += 1 * (1 + comboCount / 10); // 按照公式計算分數
            if (comboCount > maxComboCount) {
                maxComboCount = comboCount; // 更新最大連擊數
            }
        } else {
            comboCount = 0; // 連擊數重置
        }
        double accuracy = (double) hitBlocks / totalBlocks * 100;
        accLabel.setText(String.format("Accuracy: %.2f%%", accuracy)); // 更新標籤顯示得分
        comboLabel.setText(String.format("Combo: %d", comboCount)); // 更新連擊標籤
        scoreLabel.setText(String.format("Score: %d", score)); // 更新分數顯示
    }

    private void setupMediaPlayer(Stage stage, Scene scene) {
        String musicFile = musicPath; // 音樂文件的路徑
        Media media = new Media(new File(musicFile).toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setOnEndOfMedia(() -> {
            mediaPlayer.stop();
            endGame(stage, scene); // 音樂播放完畢時結束遊戲
        });
    }

    private void startTimerWithOffset() {
        // 設置計時器從 0 開始
        long startTime = System.currentTimeMillis();

        // 延遲開始音樂和計時器
        Timeline delayTimeline = new Timeline(new KeyFrame(Duration.seconds(fallTimeInSeconds), e -> {
            mediaPlayer.play(); // 播放音樂
            startRealTimeTimer(startTime); // 傳入實際開始的時間
        }));
        delayTimeline.play();
        allTimelines.add(delayTimeline); // 保存延遲的 Timeline
    }

    private void startRealTimeTimer(long startTime) {
        Timeline realTimeTimeline = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            long currentTime = System.currentTimeMillis();
            long elapsedMillis = currentTime - startTime; // 從 startTime 開始計算已過時間
            timerLabel.setText(formatTime(elapsedMillis));
        }), new KeyFrame(Duration.seconds(1)));
        realTimeTimeline.setCycleCount(Timeline.INDEFINITE);
        realTimeTimeline.play();
        allTimelines.add(realTimeTimeline); // 保存計時器 Timeline
        clock = realTimeTimeline; // 保存計時器 Timeline
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
        block.setUserData("active"); // 初始化為 active 狀態
        playArea.getChildren().add(block);
    
        Timeline fall = new Timeline(new KeyFrame(Duration.millis(fps), e -> {
            block.setY(block.getY() + movementBit);
    
            // 檢查方塊是否到達觸發區域底部
            if (block.getY() >= triggers[columnIndex].getY() + triggers[columnIndex].getHeight()) {
                if ("active".equals(block.getUserData())) {
                    // 方塊未被擊中，重置連擊數
                    updateScore(false);
                    block.setUserData("missed"); // 標記方塊已經錯過
                    block.setFill(Color.RED); // 可選：將未擊中的方塊顏色設置為紅色
                }
            }
        }));
        fall.setCycleCount(Timeline.INDEFINITE);
        fall.play();
        allTimelines.add(fall); // 保存方塊的 Timeline
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
                if ("active".equals(block.getUserData()) &&
                    block.getX() >= index * (800.0 / triggers.length) &&
                    block.getX() < (index + 1) * (800.0 / triggers.length) &&
                    block.getY() + block.getHeight() >= triggers[index].getY() &&
                    block.getY() <= triggers[index].getY() + triggers[index].getHeight()) {
                    block.setFill(Color.GRAY);
                    updateScore(true);
                    block.setUserData("hit"); // 標記方塊已被擊中
                    return; // 找到並處理一個方塊後立即返回
                }
            }
            updateScore(false); // 如果沒有找到匹配的方塊，更新分數並重置連擊數
        }
    }

    private void endGame(Stage stage, Scene gameScene) {
        VBox endMenu = new VBox(20);
        endMenu.setAlignment(Pos.CENTER);

        Label finalScoreLabel = new Label(String.format("Final Score: %d", score));
        finalScoreLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        Button mainMenuButton = new Button("回到主選單");
        mainMenuButton.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        mainMenuButton.setOnAction(event -> stage.setScene(createMainMenuScene(stage)));

        endMenu.getChildren().addAll(finalScoreLabel, mainMenuButton);

        Scene endScene = new Scene(new StackPane(endMenu), 800, 600);
        stage.setScene(endScene);
    }

    public static void main(String[] args) {
        launch(args);
    }
}