/**
 * @author [Phuong Hua] 
 * @version 1.0
 * CS151 Fall 2025 - Project 2
 */

package gamestate.snakegame;

import gamestate.GameManager;
import javafx.stage.Stage;
import javax.swing.*;
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
import javafx.stage.Stage;
import java.awt.Point;

public class SnakeGamePane {

    private static final int cellSize =25;
    private static final int gameSpeed = 150_000_000;

    private Stage primaryStage;
    private String username;
    private GameManager gameManager;
    private Snake game;
    private Food food;
    private Canvas canvas;
    private GraphicsContext gc;
    private AnimationTimer gameLoop;
    private long lastUpdate = 0;
    private Label scoreLabel;
    private Label pausedLabel;

    private boolean paused;
    private int score;
    private boolean gameOver;

    public SnakeGamePane(Stage primaryStage, String username, GameManager gameManager)
    {
        // To be filled in
        this.primaryStage = primaryStage;
        this.username = username;
        this.gameManager = gameManager;
        this.game = new Snake();
        this.food = new Food(); // Instantiate a new Food object (allows us to access methods to create and spawn the Food sprite).
    }

    public Scene createGameScene() {
        BorderPane root = new BorderPane();

        root.setStyle("-fx-background-color: #1e272eff;");

        canvas = new Canvas(600, 500);
        int gridWidth = (int) (this.canvas.getWidth() / cellSize); // Convert the pixels of the canvas into grid-coordinates (ex: 600 / 25 = 24 grids horizontally).
        int gridHeight = (int) (this.canvas.getHeight() / cellSize); // Convert the pixels of the canvas into grid-coordinates (ex: 500 / 25 = 20 grids vertically).

        food.randomSpawn(gridWidth, gridHeight, this.game.getSnakeSegments()); // Call the Food's randomSpawn method (sending the gameboard's total size and the Snake's segments' position Points) to determine where the Food sprite should spawn.

        gc = canvas.getGraphicsContext2D();
        root.setCenter(canvas);

        HBox topBar = new HBox(20);
        topBar.setPadding(new Insets(10));
        topBar.setAlignment(Pos.CENTER);

        scoreLabel = new Label("Score: 0");
        scoreLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        scoreLabel.setTextFill(Color.web("#f39c12ff"));
        topBar.getChildren().add(scoreLabel);

        root.setTop(topBar);

        Scene scene = new Scene(root, 600, 550);

        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.UP) {
                game.setNextDirection(Snake.Direction.UP);
            } else if (e.getCode() == KeyCode.DOWN) {
                game.setNextDirection(Snake.Direction.DOWN);
            } else if (e.getCode() == KeyCode.LEFT) {
                game.setNextDirection(Snake.Direction.LEFT);
            } else if (e.getCode() == KeyCode.RIGHT) {
                game.setNextDirection(Snake.Direction.RIGHT);
            } else if (e.getCode() == KeyCode.P) {
                togglePause();
            }
        });

        this.game.restart();
        this.startGameLoop();
        return scene;
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

                        Point foodPos = food.getPosition();
                        Point head = game.getSnakeHeadPos();
                        boolean foodAte= (head.x == foodPos.x &&head.y == foodPos.y);
                        game.move(foodAte);

                        if(foodAte) {
                            score = score + 10;
                             scoreLabel.setText(("Your score: " + score));
                            food.randomSpawn(gridWidth,gridHeight,game.getSnakeSegments());
                        }
                        if(game.collidesWithWall(gridWidth,gridHeight) || game.collidesWithSelf()) {
                            gameOver = true;
                            game.saveHighScore();
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

    private void handleKeyPress(KeyCode code) {
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
    }

    private void togglePause() {
        paused = !paused;
        pausedLabel.setVisible(paused);
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
        Label gameOverLabel = new Label("GAME OVER! Final Score: " + game.getScore());
        gameOverLabel.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        gameOverLabel.setTextFill(Color.web("#c0392bff"));

        StackPane overlay = new StackPane(gameOverLabel);
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");
        overlay.setPrefSize(canvas.getWidth(), canvas.getHeight());

        ((BorderPane) canvas.getParent()).setCenter(overlay);
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
        game.restart();
        lastUpdate = 0;
        primaryStage.setScene(createGameScene());
    }

    private void returnToSnakeMenu() {
        if (gameLoop != null) {
            gameLoop.stop();
        }
        SnakeMainScreen mainScreen = new SnakeMainScreen(primaryStage, username, gameManager);
        primaryStage.setScene(mainScreen.getMainMenuScene());
        primaryStage.setTitle("Snake Game Menu");
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
