/**
 * @author [Ethan Le]
 * @version 1.0
 * CS151 Fall 2025 - Project 2
 */
package gamestate.snakegame;

import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import java.awt.Point;
import java.util.List;
import java.util.Random;


public class Food {

    private final Circle food; // Variable for the Food sprite to be displayed on SnakeGamePane.java.
    private static final int FOOD_RADIUS = 10; // Define the radius of the Food sprite.
    private static final Random random = new Random(); // Create Random object to randomly generate numbers.
    private int x; // Horizontal axis.
    private int y; // Vertical axis.

    // Constructor to make a Food instance.
    public Food()
    {
        this.food = new Circle(FOOD_RADIUS, Color.RED); // Create a new Food sprite with the specified radius and color.
    }

    public Circle getFood()
    {
        return this.food; // Return the Food sprite (to be displayed in SnakeGamePane.java).
    }

    // Method to randomize spawn position of the Food sprite:
    public void randomSpawn(int gridWidth, int gridHeight, List<Rectangle> snake)
    {
        boolean successfulSpawn = false; // Boolean flag to mark whether or not the Food spawn is successful.

        while (successfulSpawn != true) // This loop will continue until we find a valid Food spawn position.
        {
            boolean snakeCollision = false; // Boolean flag to mark if the random Food spawn position hits the Snake (it should be false in order to officially use this new position to spawn the Food sprite).
            int foodX = random.nextInt(gridWidth); // Generate any number from 0 (inclusive) to the gridWidth (x-axis) boundary (exclusive).
            int foodY = random.nextInt(gridHeight); // Generate any number from 0 (inclusive) to the gridHeight (y-axis) boundary (exclusive).

            for (int i = 0; i < snake.size(); i++) // Loop through all current existing body pieces of the Snake and check if any of their positions are colliding with the new Food spawn position.
            {
                Rectangle bodyPiece = snake.get(i); // Put the next Snake body piece into a variable.

                int bodyPieceXPos = (int) (bodyPiece.getX() / Snake.getSnakeLengthWidth()); // Use division to take the pixel-coordinate of bodyPiece (ex. 500) and divide it by the GRID size of the bodyPiece (30 pixels horizontally and vertically) to get the GRID-COORDINATE of the bodyPiece.
                int bodyPieceYPos = (int) (bodyPiece.getY() / Snake.getSnakeLengthWidth()); // Use division to take the pixel-coordinate of bodyPiece (ex. 500) and divide it by the GRID size of the bodyPiece (30 pixels horizontally and vertically) to get the GRID-COORDINATE of the bodyPiece.

                if ( (bodyPieceXPos == foodX) && (bodyPieceYPos == foodY) ) // Check if the grid-coordinates of the new Food spawn location are the exact same as any of the Snake's body pieces.
                {
                    snakeCollision = true; // If yes, then the food would collide with the Snake (we do NOT want that).
                    break; // Break out of the for-loop so we can try generating new coordinates for the Food.
                }
            }

            if (snakeCollision == false) // If the new coordinates for the Food do NOT collide with the Snake, then make it official by updating this.x and this.y.
            {
                this.x = foodX; // Use the newly generated x-coordinate (grid format) for the Food.
                this.y = foodY; // Use the newly generated y-coordinate (grid format) for the Food.
                successfulSpawn = true; // Set the successfulSpawn flag to true to note that we have found a valid Food spawn position (break out of the while-loop).
            }
        }
    }

    public Point getPosition()
    {
        return new Point(this.x, this.y); // Return the randomized defined position (grid format) of where the Food sprite should spawn.
    }

    // Method to put the Food sprite in the CENTER of the GRID:
    private void centerFood()
    {
        int leftOfCell = this.x * SnakeGamePane.getCellSize(); // Convert the grid-coordinate to pixel-format.
        int topOfCell = this.y * SnakeGamePane.getCellSize(); // Convert the grid-coordinate to pixel-format.

        // Place the Food sprite at the CENTER of the cell it is in:
        food.setCenterX(leftOfCell / 2.0); // Center the pixel x-coordinate.
        food.setCenterY(topOfCell / 2.0); // Center the pixel y-coordinate.
    }
}