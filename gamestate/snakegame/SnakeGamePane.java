/**
 * @author [Phuong Hua, Ethan Le]
 * @version 1.0
 * CS151 Fall 2025 - Project 2
 */

package gamestate.snakegame;

import gamestate.GameManager;
import javafx.stage.Stage;
import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.awt.Point;

public class SnakeGamePane {

    private static final int cellSize =25;
    private static final int gameSpeed = 150_000_000;

    private Stage primaryStage;
    private String username;
    private GameManager gameManager;
    private final Snake game;
    private Canvas canvas;
    private GraphicsContext gc;
    private AnimationTimer gameLoop;
    private long lastUpdate = 0;
    private Label scoreLabel;

    private boolean paused;
    private int score;
    private boolean gameOver;

    private MediaPlayer snakeMusic;

    public SnakeGamePane(Stage primaryStage, String username, GameManager gameManager)
    {
        // To be filled in
        this.primaryStage = primaryStage;
        this.username = username;
        this.gameManager = gameManager;
        this.game = new Snake(this.gameManager);
    }

    public Scene createGameScene() {
        BorderPane root = new BorderPane();

        root.setStyle("-fx-background-color: #f38fa9;");

        canvas = new Canvas(750, 500);

        canvas.setFocusTraversable(true);
        gc = canvas.getGraphicsContext2D();

        BorderPane borders = new BorderPane(canvas); // Create BorderPane to wrap around the gameboard.
        borders.setStyle("-fx-border-color: #f38fa9;" + // Border color is Pink.
                "-fx-border-width: 10;" +   // Border width is 10 pixels.
                "-fx-background-color: #1e272eff;"); // Gameboard color is Black.

       VBox pauseMenu = createPauseMenu();
        pauseMenu.setVisible(false);

        StackPane centerPane = new StackPane(canvas,pauseMenu);
        root.setCenter(centerPane);

        HBox topBar = new HBox(20);
        topBar.setPadding(new Insets(10));
        topBar.setAlignment(Pos.CENTER);

        scoreLabel = new Label("Score: 0");
        scoreLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        scoreLabel.setTextFill(Color.web("#FFDA03"));

        Button quitButton = new Button("Quit");
        quitButton.setOnAction(e -> returnToSnakeMenu());
        quitButton.setStyle("-fx-background-color: #eeeeee; -fx-text-fill: black;");

        Button mainMenuButton = new Button("Back to Main Menu");
        mainMenuButton.setOnAction(e -> returnToMainMenu());
        mainMenuButton.setStyle("-fx-background-color: #eeeeee; -fx-text-fill: black;");
        topBar.getChildren().addAll(scoreLabel,quitButton, mainMenuButton);

        root.setTop(topBar);

        Scene scene = new Scene(root, 800, 600);

        root.requestFocus();
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.UP) {
                game.setNextDirection(Snake.Direction.UP);
            } else if (e.getCode() == KeyCode.DOWN) {
                game.setNextDirection(Snake.Direction.DOWN);
            } else if (e.getCode() == KeyCode.LEFT) {
                game.setNextDirection(Snake.Direction.LEFT);
            } else if (e.getCode() == KeyCode.RIGHT) {
                game.setNextDirection(Snake.Direction.RIGHT);
            } else if (e.getCode() == KeyCode.ESCAPE) {
                togglePauseMenu(pauseMenu);
            }
        });

        this.game.restart();
        if(game.isPaused()) {
            game.togglePause();
        }
        paused =false;
        this.startGameLoop();

        this.cueSnakeMusic(); // Play the Snake music when the Snake game starts.

        return scene;
    }

    private VBox createPauseMenu() {
        VBox pauseBox = new VBox(20);
        pauseBox.setAlignment(Pos.CENTER);
        pauseBox.setStyle("-fx-background-color: rgba(137,226,234,0.9)");

        Label pauseLabel = new Label("PAUSED");
        pauseLabel.setFont(Font.font("Arial", FontWeight.BOLD,48));
        pauseLabel.setTextFill(Color.web("FFDA03"));

        Label resumeNotification = new Label("Please click ESCAPE to resume the game!");
        resumeNotification.setFont(Font.font("Arial", FontWeight.BOLD,24));
        resumeNotification.setTextFill(Color.web("FFDA03"));

        Button restartButton = new Button("RESTART");
        styleButton(restartButton, "#f38fa9");
        restartButton.setOnAction(e -> restart());

        Button snakeMenuButton = new Button("SNAKE MENU");
        styleButton(snakeMenuButton, "#f38fa9");
        snakeMenuButton.setOnAction(e -> returnToSnakeMenu());

        Button mainMenuButton = new Button("MAIN MENU");
        styleButton(mainMenuButton, "#f38fa9");
        mainMenuButton.setOnAction(e -> returnToMainMenu());

        pauseBox.getChildren().addAll(
                pauseLabel,
                resumeNotification,
                restartButton,
                snakeMenuButton,
                mainMenuButton
        );

        return pauseBox;
    }


    public void startGameLoop() {
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (now - lastUpdate >= gameSpeed) {
                    if (!game.isPaused() && !game.isGameOver()) {
                        int gridWidth = (int) (canvas.getWidth() / cellSize);
                        int gridHeight = (int) (canvas.getHeight() / cellSize);

                        Point foodPos = game.getFood().getPosition();
                        Point head = game.getSnakeHeadPos();
                        boolean foodAte= (head.x == foodPos.x &&head.y == foodPos.y);
                        game.move(foodAte);

                        if(foodAte) {
                            score = score + 10;
                            scoreLabel.setText(("Your score: " + score));
                            game.getFood().randomSpawn(gridWidth,gridHeight,game.getSnakeSegments());
                        }
                        if(game.collidesWithWall(gridWidth,gridHeight) || game.collidesWithSelf()) {
                            gameOver = true;
                            game.saveHighScore(score); // Send the player's new score to the Snake.java class to record it in the high score files.
                            gameManager.loadHighScores(); // Load the UPDATED recorded high scores from the files and put them into the GameManager's global scores List.
                            gameManager.setMainScreen(); // Refresh the Main Menu scene to print the high scores from the GameManager's UPDATED global scores List after a new score is recorded.
                            showGameOver();
                            return;
                        }
                       // game.update();
                        render();
                    }
                    lastUpdate = now;
                }
            }
        };
        gameLoop.start();
    }

    private void render() {
        gc.setFill(Color.web("#1e272eff"));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Draw Snake
        gc.setFill(Color.web("#27ae60ff"));
        for (Point p : game.getSnakeSegments()) {
            gc.fillRect(p.getX() * cellSize, p.getY() * cellSize, cellSize, cellSize);
        }

        // Draw Food
        gc.setFill(Color.web("#e74c3cff"));
        Point foodPos = game.getFood().getPosition(); // Get the position of the Food sprite.
        gc.fillOval(foodPos.getX() * cellSize, foodPos.getY() * cellSize, cellSize, cellSize); // Render the Food sprite as an oval.
    }

    private void togglePauseMenu(VBox pauseMenu) {
        game.togglePause();
        paused = game.isPaused();
        pauseMenu.setVisible(paused);
    }

    private void showGameOver() {
        gameLoop.stop();
        VBox gameOverBox = new VBox(20);
        gameOverBox.setAlignment(Pos.CENTER);
        gameOverBox.setStyle("-fx-background-color: rgba(137,226,234,0.9);");

        Label gameOverLabel = new Label("GAME OVER! Final Score: " + score);
        gameOverLabel.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        gameOverLabel.setTextFill(Color.web("#f38fa9"));

        Button restartButton = new Button("RESTART");
        styleButton(restartButton, "#f38fa9");
        restartButton.setOnAction(e -> restart());

        Button snakeMenuButton = new Button("MAIN MENU");
        styleButton(snakeMenuButton," #f38fa9");
        snakeMenuButton.setOnAction(e -> returnToSnakeMenu());

        gameOverBox.getChildren().addAll(gameOverLabel,restartButton,snakeMenuButton);

        StackPane overlay = new StackPane(gameOverBox);
        overlay.setPrefSize(canvas.getWidth(), canvas.getHeight());

        BorderPane root = (BorderPane) canvas.getScene().getRoot();
        StackPane centerPane = (StackPane) root.getCenter();
        centerPane.getChildren().add(overlay);
    }

    private void styleButton(Button button, String color) {
        button.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        button.setPrefWidth(250);
        button.setPrefHeight(50);
        button.setStyle(
                "-fx-background-color: " + color + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 10;" +
                        "-fx-cursor: hand;"
        );

        button.setOnMouseEntered(e ->
                button.setStyle(
                        "-fx-background-color: derive(" + color + ", -20%);" +
                                "-fx-text-fill: white;" +
                                "-fx-background-radius: 10;" +
                                "-fx-cursor: hand;"
                )
        );

        button.setOnMouseExited(e ->
                button.setStyle(
                        "-fx-background-color: " + color + ";" +
                                "-fx-text-fill: white;" +
                                "-fx-background-radius: 10;" +
                                "-fx-cursor: hand;"
                )
        );
    }

    private void restart() {
        score = 0;
        gameOver =false;
        paused = false;
        lastUpdate = 0;

        if (this.snakeMusic != null) // If the Snake music is still playing, stop it upon restarting so that the music can restart when reloading the game again.
        {
            this.snakeMusic.stop();
        }

        primaryStage.setScene(createGameScene());
    }

    private void returnToSnakeMenu() {
        if (gameLoop != null) {
            gameLoop.stop();
        }

        if (this.snakeMusic != null) // If the Snake music is still playing, stop it upon returning to the Snake Game Menu.
        {
            this.snakeMusic.stop();
        }

        SnakeMainScreen mainScreen = new SnakeMainScreen(primaryStage, username, gameManager);
        mainScreen.createMainMenuScene();
        primaryStage.setScene(mainScreen.getMainMenuScene());
    }

    private void returnToMainMenu() {
        if (gameLoop != null) {
            gameLoop.stop();
        }

        if (this.snakeMusic != null) // If the Snake music is still playing, stop it upon returning to the Main Menu of the program.
        {
            this.snakeMusic.stop();
        }

        primaryStage.setScene(gameManager.getMainScreen());
    }

    private void cueSnakeMusic()
    {
        // Always get rid of any existing MediaPlayers first to avoid duplicates:
        if (this.snakeMusic != null) // If the Snake music is currently playing, stop it (so we do not accidentally have multiple copies playing when calling this method again in future runs).
        {
            this.snakeMusic.stop();
            this.snakeMusic.dispose(); // Get rid of the MediaPlayer in the case we need to reset.
        }

        // Always create a new MediaPlayer to play the soundtrack:
        Media fileMusic = new Media(getClass().getResource("/audio/snakeMusic.mp3").toString()); // Get the Snake music file as a String.
        this.snakeMusic = new MediaPlayer(fileMusic); // Turn the string of the data of the Snake music file into playable media.

        this.snakeMusic.setCycleCount(MediaPlayer.INDEFINITE); // Keep playing the music for as long until we call stop().
        this.snakeMusic.play(); // Play the Snake music now.
    }

    public static int getCellSize()
    {
        return cellSize;
    }
}
