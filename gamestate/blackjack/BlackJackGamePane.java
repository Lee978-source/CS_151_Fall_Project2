package gamestate.blackjack;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class BlackJackGamePane {
    private final Stage primaryStage;
    private final String username;
    private final BlackJackEngine engine;

    // UI pieces we’ll update
    private Label balanceLabel;
    private Label userHandLabel;
    private Label dealerHandLabel;
    private Label statusLabel;

    public BlackJackGamePane(Stage primaryStage, String username) {
        this.primaryStage = primaryStage;
        this.username = username;

        this.engine = new BlackJackEngine(1000); // we’ll hook this up later
    }

    /** Builds and returns the Blackjack “game” scene (placeholder for now). */
    public Scene createGameScene() {
        //1. Start fresh round with fixed bet
        engine.startNewRound(50);
        //2. Root layout
        BorderPane root = new BorderPane();

        // Top: title
        Label title = new Label("Blackjack – playing as " + username);
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        root.setTop(title);
        BorderPane.setAlignment(title, Pos.CENTER);

        // Center: info labels
        balanceLabel = new Label();
        userHandLabel = new Label();
        dealerHandLabel = new Label();
        statusLabel = new Label("Round in progress...");

        VBox centerBox = new VBox(10, balanceLabel, userHandLabel, dealerHandLabel, statusLabel);
        centerBox.setAlignment(Pos.CENTER);
        root.setCenter(centerBox);

        // Bottom: just a Back button for now
        // TODO: Bottom: Back button to go back to the main menu
        Button backBtn = new Button("Back to Blackjack Menu");
        backBtn.setOnAction(e -> {
            BlackJackMainScreen mainScreen = new BlackJackMainScreen(primaryStage, username);
            mainScreen.createMainMenuScene();      // re-build its UI
        });


        HBox bottomBox = new HBox(backBtn);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setSpacing(10);
        root.setBottom(bottomBox);

        //3. Fill labels from engine
        refreshLabels();

        return new Scene(root, 800, 600);
    }

    //add refresh helper
    private void refreshLabels(){
        balanceLabel.setText("Balance: $" + engine.getUserBalance());
        userHandLabel.setText("Your hand total: " + engine.getUserHandValue());
        dealerHandLabel.setText("Dealer hand total: " + engine.getDealerHandValue());
    }
}
