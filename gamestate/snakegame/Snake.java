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

package gamestate.snakegame; // Change this to match your package

import gamestate.GameManager;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.ArrayList;

import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;


public class Snake {
    private static final int GRID_WIDTH = 30;
    private static final int GRID_HEIGHT = 20;
    private static final int POINTS_PER_FOOD = 10;
    private final List<Rectangle> snake = new ArrayList<>(); // Global List to hold all of the Snake's parts (individual squares binded together).
    private int currentSnakeHeadX; // Snake head's horizontal coordinate (grid format).
    private int currentSnakeHeadY; // Snake head's vertical coordinate (grid format).
    private static final int SNAKE_LENGTH_WIDTH = 30; // Define the length and width of every square of the Snake (length = width to make a square).

    //private Snake snake;
    private Food food;
    private int score;
    private boolean gameOver;
    private boolean paused;
    private String username;
    private GameManager gameManager; // Reference to GameManager

    public Snake()
    {
        Rectangle square = new Rectangle(SNAKE_LENGTH_WIDTH, SNAKE_LENGTH_WIDTH); // Create the head for the Snake.
        square.setFill(Color.GREEN); // Set the color of the Snake head (different from other Snake pieces).

        this.snake.add(square); // Add the head to the Snake.
    }

    // Getter method to retrieve the actual Snake's head piece:
    public Rectangle getSnakeHead()
    {
        return this.snake.getFirst(); // Return the Snake's head (first Rectangle of the List). 
    }

    // Getter method to retrieve the current GRID coordinate point of the Snake's head:
    public Point getSnakeHeadPos()
    {
        Rectangle head = this.snake.getFirst(); // Get the head of the Snake.

        // Get the GRID format of the Snake's head's current coordinates:
        this.currentSnakeHeadX = (int) ( head.getX() / SNAKE_LENGTH_WIDTH ); // Turn the head's pixel-formatted x-coordinate into GRID format.
        this.currentSnakeHeadY = (int) ( head.getY() / SNAKE_LENGTH_WIDTH ); // Turn the head's pixel-formatted x-coordinate into GRID format.

        return new Point(this.currentSnakeHeadX, this.currentSnakeHeadY); // Return the current GRID coordinates of the Snake's head as a Coordinate Point.
    }

    public void growSnake()
    {
        Rectangle square = new Rectangle(SNAKE_LENGTH_WIDTH, SNAKE_LENGTH_WIDTH); // Create a new piece to add to the Snake.
        square.setFill(Color.AQUA); // Set the color of the new Snake component.

        this.snake.add(square); // Add the square (make the Snake longer).
    }

    public void move(Scene snakeGamePane)
    {
        // To be filled in - Ethan Le
    }

    private void initGame() {
        //snake = new Snake();
        this.food = new Food();
        this.food.randomSpawn(GRID_WIDTH, GRID_HEIGHT, snake);
        score = 0;
        gameOver = false;
        paused = false;
    }

    public void update() {
        if (gameOver || paused) return;

        //snake.move(); // The actual call of the "move()" method would likely be in the SnakeGamePane class - Ethan Le

        // If the Snake's head reaches the same GRID position as the spawned Food sprite, make the Snake grow.
        if (this.getSnakeHeadPos().getX() == (this.food.getPosition().getX()) && this.getSnakeHeadPos().getY() == (this.food.getPosition().getY()))
        {
            snake.grow();
            score += POINTS_PER_FOOD;
            this.food.randomSpawn(GRID_WIDTH, GRID_HEIGHT, snake); // A new Food sprite's GRID positions are randomized and ready for spawning on the grid.
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
    public List<Rectangle> getSnake() { return this.snake; } // Return the ArrayList containing all of the current pieces of the Snake.
    public Food getFood() { return food; }
    public int getScore() { return score; }
    public boolean isGameOver() { return gameOver; }
    public boolean isPaused() { return paused; }
    public int getGridWidth() { return GRID_WIDTH; }
    public int getGridHeight() { return GRID_HEIGHT; }
    public static int getSnakeLengthWidth() { return SNAKE_LENGTH_WIDTH; }
    public String getUsername() { return username; }
}