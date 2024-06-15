package org.example;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class Main extends Application {

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
            GameScreen.switchToGameScreen(primaryStage);
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

        // 加載背景圖片
        String backgroundImagePath = Main.class.getResource("/piano.jpg").toExternalForm(); // 確保路徑正確
        BackgroundImage myBI = new BackgroundImage(new Image(backgroundImagePath),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
                new BackgroundSize(100, 100, true, true, false, true));
        root.setBackground(new Background(myBI));


        Scene startScene = new Scene(root);
        primaryStage.setScene(startScene);

        primaryStage.show();
    }

    public static Scene createMainMenuScene(Stage stage) {
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
            GameScreen.switchToGameScreen(stage);
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

        // 加載背景圖片
        String backgroundImagePath = Main.class.getResource("/piano.jpg").toExternalForm(); // 確保路徑正確
        BackgroundImage myBI = new BackgroundImage(new Image(backgroundImagePath),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
                new BackgroundSize(100, 100, true, true, false, true));
        root.setBackground(new Background(myBI));

        return new Scene(root);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
