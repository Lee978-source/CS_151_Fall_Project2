/**
 * @author [Phuong Hua] 
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

        root.setStyle("-fx-background-color: #1e272eff;");

        canvas = new Canvas(600, 500);
        int gridWidth = (int) (this.canvas.getWidth() / cellSize);
        int gridHeight = (int) (this.canvas.getHeight() / cellSize);
        gc = canvas.getGraphicsContext2D();

       VBox pauseMenu = createPauseMenu();
        pauseMenu.setVisible(false);

        StackPane centerPane = new StackPane(canvas,pauseMenu);
        root.setCenter(centerPane);

        HBox topBar = new HBox(20);
        topBar.setPadding(new Insets(10));
        topBar.setAlignment(Pos.CENTER);

        scoreLabel = new Label("Score: 0");
        scoreLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        scoreLabel.setTextFill(Color.web("#f39c12ff"));

        Button quitButton = new Button("Quit");
        quitButton.setOnAction(e -> returnToSnakeMenu());
        quitButton.setStyle("-fx-background-color: #d368d2; -fx-text-fill: white;");
        topBar.getChildren().addAll(scoreLabel,quitButton);

        root.setTop(topBar);

        Scene scene = new Scene(root, 600, 550);
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

        this.game.restart(); // Key: the game must be initialized first so we can create the Food instance (only one Food instance should be created per game run), THEN we are able to spawn the actual Food sprite correctly using that created instance in the next line.
        if(game.isPaused()) {
            game.togglePause();
        }
        paused =false;
        this.startGameLoop();
        return scene;
    }

    private VBox createPauseMenu() {
        VBox pauseBox = new VBox(20);
        pauseBox.setAlignment(Pos.CENTER);
        pauseBox.setStyle("-fx-background-color: rgba(137,226,234,0.9)");

        Label pauseLabel = new Label("PAUSED");
        pauseLabel.setFont(Font.font("Trebuchet MS", FontWeight.BOLD,48));
        pauseLabel.setTextFill(Color.BLACK);

        Button resumeButton = new Button("RESUME");
        styleButton(resumeButton, "#27ae60");
        resumeButton.setOnAction(e -> {
            game.togglePause();
            paused = false;
            pauseBox.setVisible(false);
        });

        Button restartButton = new Button("RESTART");
        styleButton(restartButton, "#3498db");
        restartButton.setOnAction(e -> restart());

        Button snakeMenuButton = new Button("SNAKE MENU");
        styleButton(snakeMenuButton, "#e67e22");
        snakeMenuButton.setOnAction(e -> returnToSnakeMenu());

        Button mainMenuButton = new Button("MAIN MENU");
        styleButton(mainMenuButton, "#c0392b");
        mainMenuButton.setOnAction(e -> returnToMainMenu());

        pauseBox.getChildren().addAll(
                pauseLabel,
                resumeButton,
                restartButton,
                snakeMenuButton,
                mainMenuButton
        );

        return pauseBox;
    }

    private HBox createTopBar() {
        HBox topBar = new HBox(20);
        topBar.setPadding(new Insets(10));
        topBar.setAlignment(Pos.CENTER);

        scoreLabel = new Label("Score: 0");
        scoreLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        scoreLabel.setTextFill(Color.web("#f39c12ff"));
        topBar.getChildren().add(scoreLabel);

        return topBar;
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

    private void updateScore() {
        scoreLabel.setText("Score: " + game.getScore());
    }

    /*private void handleKeyPress(KeyCode code) {
        switch (code) {
            case UP:
                game.setNextDirection(Snake.Direction.UP);
                break;
            case DOWN:
                game.setNextDirection(Snake.Direction.DOWN);
                break;
            case LEFT:
                game.setNextDirection(Snake.Direction.LEFT);
                break;
            case RIGHT:
                game.setNextDirection(Snake.Direction.RIGHT);
                break;
            case P:
                togglePause();
                break;
            default:
                break;
        }
    }*/

    private void togglePauseMenu(VBox pauseMenu) {
        paused = !paused;
        game.togglePause();
        pauseMenu.setVisible(paused);
    }
/*
    private void saveHighScore(){
        try {
            String scoreEntry = username + " Snake " + score;
            List<String> allScores = File
         }
    }
*/
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
        styleButton(restartButton," #f38fa9");
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

        primaryStage.setScene(createGameScene());
    }

    private void returnToSnakeMenu() {
        if (gameLoop != null) {
            gameLoop.stop();
        }
        SnakeMainScreen mainScreen = new SnakeMainScreen(primaryStage, username, gameManager);
        mainScreen.createMainMenuScene();
        primaryStage.setScene(mainScreen.getMainMenuScene());
    }

    private void returnToMainMenu() {
        if (gameLoop != null) {
            gameLoop.stop();
        }
        primaryStage.setScene(gameManager.getMainScreen());
        //primaryStage.setTitle("Welcome, " + username + "! What game would you like to play today?");
    }

    public static int getCellSize()
    {
        return cellSize;
    }
}
