package gamestate.blackjack;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class BlackJackGamePane {
    private final Stage primaryStage;
    private final String username;

    // later we’ll also keep a BlackJackEngine here
    // private final BlackJackEngine engine;

    public BlackJackGamePane(Stage primaryStage, String username) {
        this.primaryStage = primaryStage;
        this.username = username;
        // this.engine = new BlackJackEngine(1000); // we’ll hook this up later
    }

    /** Builds and returns the Blackjack “game” scene (placeholder for now). */
    public Scene createGameScene() {
        BorderPane root = new BorderPane();

        // Top: title
        Label title = new Label("Blackjack – playing as " + username);
        root.setTop(title);
        BorderPane.setAlignment(title, Pos.CENTER);

        // Center: placeholder text (later this will be cards, buttons, etc.)
        Label placeholder = new Label("Game screen placeholder – cards will go here");
        root.setCenter(placeholder);
        BorderPane.setAlignment(placeholder, Pos.CENTER);

        // Bottom: Back button to go back to Blackjack main menu
        Button backBtn = new Button("Back to Blackjack Menu");
        root.setBottom(backBtn);
        BorderPane.setAlignment(backBtn, Pos.CENTER);

        backBtn.setOnAction(e -> {
            // Go back to Blackjack main menu
            BlackJackMainScreen mainScreen = new BlackJackMainScreen(primaryStage, username);
            mainScreen.createMainMenuScene();   // this method already calls primaryStage.setScene(...)
        });

        return new Scene(root, 800, 600);
    }
}
