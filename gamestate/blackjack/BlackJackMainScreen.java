/**
 * @author [Phuong Hua]
 * @version 1.0
 * CS151 Fall 2025 - Project 2
 */

package gamestate.blackjack;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class BlackJackMainScreen {

    private Stage primaryStage;
    private String username;
    private Scene mainMenuScene;
    private Scene gameScene;
    private BlackJackEngine engine;

    // User interface for game screen



    public BlackJackMainScreen(Stage primaryStage, String username) {
        this.primaryStage = primaryStage;
        this.username = username;
    }

    /**
     * Create the Blackjack main menu scene with New Game and Load Game options
     */
    public void createMainMenuScene() {
        Label titleLabel = new Label("BLACKJACK");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 36));

        Button newGameButton = new Button("Start New Game");
        newGameButton.setPrefWidth(200);
        newGameButton.setOnAction(e -> startNewGame());

        Button backButton = new Button("Back to Main Menu");
        backButton.setPrefWidth(200);
        backButton.setOnAction(e -> returnToMainMenu());

        VBox layout = new VBox(20, titleLabel, newGameButton, backButton);
        layout.setAlignment(Pos.CENTER);

        this.mainMenuScene = new Scene(layout, 800, 600);
    }

    private void startNewGame() {
        this.engine = new BlackJackEngine(1000);
        createGameScene();
        primaryStage.setScene(gameScene);
        primaryStage.setTitle("Blackjack - " + username);
    }

    private void createGameScene() {
        BorderPane root = new BorderPane();
        //Grid showing all players
        GridPane playersGrid = new GridPane();
        playersGrid.setHgap(20);
        playersGrid.setVgap(20);
        playersGrid.setAlignment(Pos.CENTER);

        // Dealer

        //AI Player 1

        //AI Player 2

        //User

        //Controls
    }

    private void returnToMainMenu(){
        primaryStage.setScene(mainMenuScene);
    }
    // Getters
    public Scene getMainMenuScene() {
        return this.mainMenuScene;
    }

    public Scene getGameScene() {
        return this.gameScene;
    }
}