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
import java.util.LinkedList;

import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;


public class Snake {
    private static final int GRID_WIDTH = 24;
    private static final int GRID_HEIGHT = 20;
    private static final int POINTS_PER_FOOD = 10;
    private final LinkedList<Point> snake = new LinkedList<>(); // Global List to hold all of the Snake's parts (each segment defined by their Positions).
    private Direction currentDirection = Direction.RIGHT; // Set the initial starting direction of the Snake.
    private Direction newDirection = Direction.RIGHT; // Set the new direction of the Snake.

    //private Snake snake;
    private Food food;
    private int score;
    private boolean gameOver;
    private boolean paused;
    private String username;
    private GameManager gameManager; // Reference to GameManager

    public enum Direction // Define constants for the Direction of the Snake's movement.
    {
        LEFT,
        RIGHT,
        DOWN,
        UP
    }

    public Snake(GameManager gameManager)
    {
        this.gameManager = gameManager; // Get the instance of the GameManager in order to read and write to the high score files.
        this.username = this.gameManager.getUsername(); // Store the current player's username for score recording when the game ends.
        this.paused =false;
    }

    // Helper method to ensure the Snake always starts with only 1 segment (the Head) and moves in the RIGHT direction:
    private void restartSnake()
    {
        this.currentDirection = Direction.RIGHT; // Set the initial starting direction of the Snake (needed in case the previous key press was a different Direction).
        this.newDirection = Direction.RIGHT; // Set the new direction of the Snake (needed in case the previous key press was a different Direction).

        this.snake.clear(); // If we are starting a new Snake game or restarting the Snake game after a Game Over, clear out any Snake's segments first.

        this.snake.add(new Point(5, 5)); // Then add a NEW head to the Snake (at GRID position x=5 and y=5) at the original starting point (Snake will always have 1 segment upon start/restart).
    }

    public void move(boolean grow) {
        this.currentDirection = this.newDirection; // Change the direction of the Snake if the user has pressed a key.

        Point snakeHeadOld = this.getSnakeHeadPos(); // Get the CURRENT position of the head of the Snake (it is the first piece that would change directions).
        Point snakeHeadNew = new Point(snakeHeadOld.x, snakeHeadOld.y); // Create a NEW position for where the Snake's head will end up next (next animation frame of the game).

        switch (this.currentDirection) // Repeats over and over as long as the game is running.
        {
            case LEFT:
                snakeHeadNew.x -= 1; // If the current direction of the Snake is now left, set the Snake's head to go 1 GRID cell to the left.
                break;
            case RIGHT:
                snakeHeadNew.x += 1; // If the current direction of the Snake is now right, set the Snake's head to go 1 GRID cell to the right.
                break;
            case DOWN:
                snakeHeadNew.y += 1; // If the current direction of the Snake is now down, set the Snake's head to go 1 GRID cell down (positive y-direction).
                break;
            case UP:
                snakeHeadNew.y -= 1; // If the current direction of the Snake is now up, set the Snake's head to go 1 GRID cell up (negative y-direction).
                break;
        }
        this.getSnakeSegments().addFirst(snakeHeadNew); // Shift all other snake segments in the list down the list, and add the head with the NEW position in front.

        if (!grow) { // If the boolean grow flag is TRUE, then we do NOT remove the tail segment so that the Snake grows upon eating food.
            this.getSnakeSegments().removeLast(); // Get rid of the CURRENT tail segment, otherwise Snake will keep extending in the current direction forever (the last segment must always be removed in every animation frame since there is a NEW head segment being added to the front in every animation frame).
        }
    }

    private void initGame() {

        this.restartSnake(); // Create the Snake with 1 segment.
        this.food = new Food(); // Create a new Food instance (only one per game run).
        this.food.randomSpawn(GRID_WIDTH, GRID_HEIGHT, snake); // Call the Food's randomSpawn method (sending the gameboard's total size and the Snake's segments' position Points) to determine where the Food sprite should spawn.
        score = 0;
        gameOver = false;
        paused = false;
    }

    // Method to determine if the Snake has hit any of the boundaries of the gameboard:
    public boolean collidesWithWall(int gridWidth, int gridHeight)
    {
        Point snakeHead = this.getSnakeHeadPos(); // Get the head of the Snake (it is the first piece that would touch the gameboard boundaries).

        if ( (snakeHead.x < 0) || (snakeHead.y < 0) || (snakeHead.x >= gridWidth) || (snakeHead.y >= gridHeight) )
        {
            return true; // Return "true" if the snake's head position is outside of the defined gameboard.
        }

        return false; // Otherwise, return "false" if the snake's head position is still within the defined gameboard.
    }

    // Method to determine if the Snake has hit itself:
    public boolean collidesWithSelf()
    {
        for (int i = 1; i < this.getSnakeSegments().size(); i++) {
            if (this.getSnakeHeadPos().equals(this.getSnakeSegments().get(i)))
            {
                return true; // Return "true" if the snake's head position is equal to the position of one of its segments (meaning collision with itself).
            }
        }

        return false; // Otherwise, return "false" if the snake's head position has not hit any of its segments.
    }

    // Method to change the direction of the Snake (depending on user key press):
    public void setNextDirection(Direction newDirection)
    {
        // Use if-else statement to ensure the Snake does not turn 180 degrees (can only make 90-degree turns):
        if ( !((newDirection == Direction.LEFT && this.currentDirection == Direction.RIGHT)
            || (newDirection == Direction.UP && this.currentDirection == Direction.DOWN)
            || (newDirection == Direction.RIGHT && this.currentDirection == Direction.LEFT)
            || (newDirection == Direction.DOWN && this.currentDirection == Direction.UP)) )
        {
            this.newDirection = newDirection; // If the pressed key does NOT turn the Snake 180 degrees, set the pressed key as the new direction.
        }
    }

    public void togglePause() {

        paused = !paused;
    }

    public void restart() {
        initGame();
        this.paused = false;
    }

    // Save high score in GameManager format: username,SNAKE,score
    public void saveHighScore(int score) { // Retrieve the player's new score from SnakeGamePane.java upon Game Over (collision).
        try {
            // Format: username,SNAKE,score
            String scoreEntry = username + ",SNAKE," + score; // Record the player's username and their new score.

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
            //gameManager.loadHighScores();

            System.out.println("High score saved: " + username + " - " + score);

        } catch (IOException e) {
            System.err.println("Error saving high score: " + e.getMessage());
        }
    }

    // Getters:
    // Getter method to retrieve ALL of the Snake's segments (via a List of Points):
    public LinkedList<Point> getSnakeSegments()
    {
        return this.snake; // Return the entire Snake (List of position Points).
    }

    // Getter method to retrieve the current GRID coordinate point of the Snake's head:
    public Point getSnakeHeadPos()
    {
        return this.snake.getFirst(); // Return the current GRID coordinates of the Snake's head as a Coordinate Point (get the 0th index containing the position of the Snake's head).
    }



    public Food getFood() { return food; }
    public int getScore() { return score; }
    public boolean isGameOver() { return gameOver; }
    public boolean isPaused() { return paused; }
}