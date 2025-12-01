/**
 * @author [Ethan Le]
 * @version 1.0
 * CS151 Fall 2025 - Project 2
 */
package gamestate.snakegame;

import java.awt.Point;
import java.util.List;
import java.util.Random;

public class Food {

    private static final Random random = new Random(); // Create Random object to randomly generate numbers.
    private int currentFoodX; // Food's horizontal coordinate (grid format).
    private int currentFoodY; // Food's vertical coordinate (grid format).

    // Constructor to make a Food instance.
    public Food()
    {
        // Empty - constructor allows other files to create Food instance to access method to determine Food sprite's spawn position.
    }

    // Method to randomize spawn position of the Food sprite:
    public void randomSpawn(int gridWidth, int gridHeight, List<Point> snake) // Parameters take the TOTAL number of GRIDS in the gameboard horizontally and vertically, and the Snake segments themselves (as a List of position Points).
    {
        boolean successfulSpawn = false; // Boolean flag to mark whether or not the Food spawn is successful.

        while (successfulSpawn != true) // This loop will continue until we find a valid Food spawn position.
        {
            boolean snakeCollision = false; // Boolean flag to mark if the random Food spawn position hits the Snake (it should be false in order to officially use this new position to spawn the Food sprite).
            int newFoodX = random.nextInt(gridWidth); // Use the gameboard's TOTAL number of grids along the HORIZONTAL axis to generate any number from 0 (inclusive) to the gridWidth (x-axis) boundary (exclusive).
            int newFoodY = random.nextInt(gridHeight); // Use the gameboard's TOTAL number of grids along the VERTICAL axis to generate any number from 0 (inclusive) to the gridHeight (y-axis) boundary (exclusive).

            for (int i = 0; i < snake.size(); i++) // Loop through all current existing body pieces of the Snake and check if any of their positions are colliding with the new Food spawn position.
            {
                Point bodyPiece = snake.get(i); // Put the next Snake body piece (the body piece's current position Point) into a variable.

                // Get the GRID format of the Snake's body piece's current coordinates:
                int bodyPieceXPos = bodyPiece.x; // Get the GRID x-COORDINATE of the bodyPiece.
                int bodyPieceYPos = bodyPiece.y; // Get the GRID y-COORDINATE of the bodyPiece.

                if ( (bodyPieceXPos == newFoodX) && (bodyPieceYPos == newFoodY) ) // Check if the grid-coordinates of the new Food spawn location are the exact same as any of the Snake's body pieces.
                {
                    snakeCollision = true; // If yes, then the food would collide with the Snake (we do NOT want that).
                    break; // Break out of the for-loop so we can try generating new GRID coordinates for the Food.
                }
            }

            if (snakeCollision == false) // If the new coordinates for the Food do NOT collide with the Snake, then make it official by updating this.x and this.y.
            {
                this.currentFoodX = newFoodX; // Use the newly generated x-coordinate (grid format) for the Food.
                this.currentFoodY = newFoodY; // Use the newly generated y-coordinate (grid format) for the Food.
                successfulSpawn = true; // Set the successfulSpawn flag to true to note that we have found a valid Food spawn position (break out of the while-loop).
            }
        }
    }

    public Point getPosition()
    {
        return new Point(this.currentFoodX, this.currentFoodY); // Return the randomized officially-defined position (grid format) of where the Food sprite should spawn.
    }

}