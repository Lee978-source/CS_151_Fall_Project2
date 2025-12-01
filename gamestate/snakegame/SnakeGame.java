/** 
 * @author [Phuong Hua] 
 * @version 1.0
 * CS151 Fall 2025 - Project 2
 */

 package gamestate.snakegame;

import gamestate.GameManager;
//import io and nio packages if needed

public class SnakeGame {

private static final int Grid_Width = 30;
    private static final int Grid_Height = 20;
    public static final int Points_Per_Food= 10;

    private Snake snake;
    private GameManager gameManager;
    private Food food;
    private int score;
    private boolean gameOver;
    private bolean paused;
    private String username;

    public SnakeGame(String username, GameManager gameManager) {
        this.username = username;
        this.gameManager = gameManager;
        // Initialize game state
    }
}
