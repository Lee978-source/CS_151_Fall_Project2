/**
 * @author [Phuong Hua]
 * @version 1.0
 * CS151 Fall 2025 - Project 2
 */

package gamestate.blackjack;

import gamestate.GameManager;
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

public class BlackJackMainScreen extends GameManager {


    private Stage primaryStage;
    private String username;
    private Scene mainMenuScene;
    private Scene gameScene;
    private BlackJackEngine engine;
    private Button backButton;
    private GameManager gameManager;
    // User interface for game screen


    public BlackJackMainScreen(Stage primaryStage, String username, GameManager gameManager) {
        this.primaryStage = primaryStage;
        this.username = username;
        this.gameManager = gameManager;
    }

    public void createMainMenuScene() {
        Label titleLabel = new Label("BLACKJACK");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 36));

        Button newGameButton = new Button("Start New Game");
        newGameButton.setPrefWidth(200);
        newGameButton.setOnAction(e -> {
            BlackJackGamePane gamePane = new BlackJackGamePane(primaryStage, username, this, this.gameManager);
            Scene gameScene = gamePane.createGameScene();
            primaryStage.setScene(gameScene);
        });

        this.backButton = new Button("Back to Main Menu");
        backButton.setPrefWidth(200);

        backButton.setOnAction(e -> {
            primaryStage.setScene(this.gameManager.getMainScreen());
        });

        VBox layout = new VBox(20, titleLabel, newGameButton, backButton);
        layout.setAlignment(Pos.CENTER);

        this.mainMenuScene = new Scene(layout, 800, 600);
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

    private VBox createPlayerDisplay(String playerName, boolean isDealer) {
        Label nameLabel = new Label(playerName);
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        Label handLabel = new Label("Hand: --");
        handLabel.setFont(Font.font("Arial", 14));
        
        VBox cardDisplay = new VBox(5);
        cardDisplay.setAlignment(Pos.CENTER);
        cardDisplay.setMinHeight(100);
        
        if (isDealer) {
            VBox box = new VBox(10, nameLabel, handLabel, cardDisplay);
            box.setAlignment(Pos.CENTER);
            box.setStyle("-fx-border-color: black; -fx-border-width: 2; -fx-padding: 10;");
            return box;
        } else {
            Label balanceLabel = new Label("Balance: $1000");
            balanceLabel.setFont(Font.font("Arial", 14));
            
            VBox box = new VBox(10, nameLabel, handLabel, balanceLabel, cardDisplay);
            box.setAlignment(Pos.CENTER);
            box.setStyle("-fx-border-color: black; -fx-border-width: 2; -fx-padding: 10;");
            return box;
        }
    }

    public Button backToMainMenu() { // new
        return this.backButton;
    }

    public Scene getMainMenuScene() {
        return this.mainMenuScene;
    }

    public Scene getGameScene() {
        return this.gameScene;
    }
}