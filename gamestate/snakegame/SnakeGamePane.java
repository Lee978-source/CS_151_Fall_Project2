/** 
 * @author [Phuong Hua] 
 * @version 1.0
 * CS151 Fall 2025 - Project 2
 */

package gamestate.snakegame;

import gamestate.snakegame.Snake;
import javafx.stage.Stage;

import javax.swing.*;

public class SnakeGamePane {

    public SnakeGamePane(Stage primaryStage, String username)
    {
        // To be filled in
    }
    public static void main(String[] args) throws Exception {
        int boardWidth = 800;
        int boardHeight = 800;

        JFrame frame = new JFrame("Snake Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(boardWidth, boardHeight);
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setLocation(null);

        Snake snake = new Snake(boardWidth, boardHeight);
        //frame.add(snake);
        frame.pack();
    }
}
