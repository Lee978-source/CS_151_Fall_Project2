/** 
 * @author [Phuong Hua] 
 * @version 1.0
 * CS151 Fall 2025 - Project 2
 */

// ============================================
// SnakeGame.java - UPDATED
// Now saves high scores in your GameManager format
// Package: (same as your snake package)
// ============================================

package snake; // Change this to match your package

import gamestate.GameManager;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class SnakeGame {
    private static final int GRID_WIDTH = 30;
    private static final int GRID_HEIGHT = 20;
    private static final int POINTS_PER_FOOD = 10;

    private Snake snake;
    private Food food;
    private int score;
    private boolean gameOver;
    private boolean paused;
    private String username;
    private GameManager gameManager; // Reference to GameManager

    public SnakeGame(String username, GameManager gameManager) {
        this.username = username;
        this.gameManager = gameManager;
        initGame();
    }

    private void initGame() {
        snake = new Snake(GRID_WIDTH / 2, GRID_HEIGHT / 2);
        food = new Food();
        food.spawn(GRID_WIDTH, GRID_HEIGHT, snake);
        score = 0;
        gameOver = false;
        paused = false;
    }

    public void update() {
        if (gameOver || paused) return;

        snake.move();

        if (snake.getHead().equals(food.getPosition())) {
            snake.grow();
            score += POINTS_PER_FOOD;
            food.spawn(GRID_WIDTH, GRID_HEIGHT, snake);
        }

        if (snake.collidesWithWall(GRID_WIDTH, GRID_HEIGHT) ||
                snake.collidesWithSelf()) {
            gameOver = true;
            saveHighScore();
        }
    }

    public void handleKeyPress(String key) {
        switch (key) {
            case "UP":
                snake.setDirection(Direction.UP);
                break;
            case "DOWN":
                snake.setDirection(Direction.DOWN);
                break;
            case "LEFT":
                snake.setDirection(Direction.LEFT);
                break;
            case "RIGHT":
                snake.setDirection(Direction.RIGHT);
                break;
            case "ESCAPE":
                togglePause();
                break;
        }
    }

    public void togglePause() {
        paused = !paused;
    }

    public void restart() {
        initGame();
    }

    // Save high score in GameManager format: username,SNAKE,score
    private void saveHighScore() {
        try {
            // Format: username,SNAKE,score
            String scoreEntry = username + ",SNAKE," + score;

            // Read existing scores
            List<String> allScores = Files.readAllLines(gameManager.getHighScoresPath());

            // Add new score
            allScores.add(scoreEntry);

            // Write all scores back to file
            Files.write(
                    gameManager.getHighScoresPath(),
                    allScores,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );

            // Reload high scores in GameManager
            gameManager.loadHighScores();

            System.out.println("High score saved: " + username + " - " + score);

        } catch (IOException e) {
            System.err.println("Error saving high score: " + e.getMessage());
        }
    }

    // Getters
    public Snake getSnake() { return snake; }
    public Food getFood() { return food; }
    public int getScore() { return score; }
    public boolean isGameOver() { return gameOver; }
    public boolean isPaused() { return paused; }
    public int getGridWidth() { return GRID_WIDTH; }
    public int getGridHeight() { return GRID_HEIGHT; }
    public String getUsername() { return username; }
}