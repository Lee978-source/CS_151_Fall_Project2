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
        this.snake.add(new Point(5, 5)); // Add the head (with a GRID position of x=5 and y=5) to the Snake.
    }

    public void growSnake()
    {
        Point tail = this.snake.getLast(); // Get the tail position Point of the Snake.

        Point newPiece = new Point(tail.x, tail.y); // Create a copy of the tail position Point.

        this.snake.add(newPiece); // Add the new piece to the end of the Snake.
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

        if (!grow) {
            this.getSnakeSegments().removeLast(); // Get rid of the CURRENT tail segment, otherwise Snake will keep extending in the current direction forever (the last segment must always be removed in every animation frame since there is a NEW head segment being added to the front in every animation frame).
        }
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

        this.move(paused); // Move the Snake.

        // If the Snake's head reaches the same GRID position as the spawned Food sprite, make the Snake grow.
        if (this.getSnakeHeadPos().getX() == (this.food.getPosition().getX()) && this.getSnakeHeadPos().getY() == (this.food.getPosition().getY()))
        {
            this.growSnake(); // Grow the Snake if the Snake's head has touched the Food sprite.
            score += POINTS_PER_FOOD;
            this.food.randomSpawn(GRID_WIDTH, GRID_HEIGHT, snake); // A new Food sprite's GRID positions are randomized and ready for spawning on the grid.
        }

        if (this.collidesWithWall(GRID_WIDTH, GRID_HEIGHT) ||
                this.collidesWithSelf()) {
            gameOver = true;
            saveHighScore();
        }
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
        for (int i = 1; i < this.getSnakeSegments().size(); i++)
        {
            if (this.getSnakeHeadPos().equals(this.getSnakeSegments().get(i)))
                return true; // Return "true" if the snake's head position is equal to the position of one of its segments (meaning collision with itself).
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
/*
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
*/
    public void togglePause() {
        paused = !paused;
    }

    public void restart() {
        initGame();
    }

    // Save high score in GameManager format: username,SNAKE,score
    public void saveHighScore() {
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
    public int getGridWidth() { return GRID_WIDTH; }
    public int getGridHeight() { return GRID_HEIGHT; }
    public String getUsername() { return username; }
}