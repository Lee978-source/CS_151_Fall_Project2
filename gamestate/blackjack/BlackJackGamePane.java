package gamestate.blackjack;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
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

    // UI pieces TODO update
    private Label balanceLabel;
    private Label userHandLabel;
    private Label dealerHandLabel;
    private Label statusLabel;
    private Label ai1HandLabel;
    private Label ai2HandLabel;


    private Button hitButton;
    private Button standButton;
    private Button nextRoundButton;   // hook this up after Hit/Stand

    private void playRemainingPlayersWithDelay() {
        // Disable controls while the AIs + dealer play
        hitButton.setDisable(true);
        standButton.setDisable(true);
        nextRoundButton.setDisable(true);

        // 1-second delay for each step
        PauseTransition p1 = new PauseTransition(Duration.seconds(1));
        PauseTransition p2 = new PauseTransition(Duration.seconds(1));
        PauseTransition p3 = new PauseTransition(Duration.seconds(1));

        // After 1 second → AI1
        p1.setOnFinished(e -> {
            engine.ai1Turn();
            refreshLabels();
            statusLabel.setText("Player 1 finished their turn.");
            p2.play();   // chain next step
        });

        // After 2 seconds total → AI2
        p2.setOnFinished(e -> {
            engine.ai2Turn();
            refreshLabels();
            statusLabel.setText("Player 2 finished their turn.");
            p3.play();   // chain next step
        });

        // After 3 seconds total → Dealer + settle
        p3.setOnFinished(e -> {
            engine.dealerTurnAndSettle();
            refreshLabels();
            statusLabel.setText("Round over: Earnings " + engine.getLastUserDelta());
            nextRoundButton.setDisable(false);  // now user can go to next round
        });

        p1.play();  // kick off the chain
    }


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
        statusLabel = new Label();
        ai1HandLabel = new Label();
        ai2HandLabel = new Label();

        VBox centerBox = new VBox(20,

                balanceLabel,
                userHandLabel,
                ai1HandLabel,      // NEW
                ai2HandLabel,      // NEW
                dealerHandLabel,
                statusLabel

        );
        centerBox.setAlignment(Pos.CENTER);
        root.setCenter(centerBox);

        // Bottom: just a Back button for now
        // TODO: Bottom: Back button to go back to the main menu
        Button backBtn = new Button("Back to Blackjack Menu");
        backBtn.setOnAction(e -> {
            BlackJackMainScreen mainScreen = new BlackJackMainScreen(primaryStage, username);
            mainScreen.createMainMenuScene();      // re-build its UI
        });


        hitButton = new Button("Hit");
        standButton = new Button("Stand");
        nextRoundButton = new Button("New Round");

        Button backButton = new Button("Back to Blackjack Menu");   // <- local
        HBox buttonRow = new HBox(10, hitButton, standButton, nextRoundButton);
        buttonRow.setAlignment(Pos.CENTER);

        VBox bottomBox = new VBox(10, buttonRow, backButton);
        bottomBox.setAlignment(Pos.CENTER);
        root.setBottom(bottomBox);

        hitButton.setOnAction(e -> {
            statusLabel.setText("You hit... waiting.");
            hitButton.setDisable(true);
            standButton.setDisable(true);

            PauseTransition pause = new PauseTransition(Duration.seconds(3));
            pause.setOnFinished(ev -> {
                // Apply the hit
                engine.userHit();
                refreshLabels();

                // Bust or hit 21, round is over
                if (engine.isRoundOver()) {
                    statusLabel.setText("Round over after your hit.");
                    // Lets AI1, AI2, Dealer play with delays
                    playRemainingPlayersWithDelay();
                } else {
                    // Still your turn
                    statusLabel.setText("Your turn again: Hit or Stand?");
                    hitButton.setDisable(false);
                    standButton.setDisable(false);
                }
            });
            pause.play();
        });

        standButton.setOnAction(e -> {
            engine.userStand();   // let engine finish dealer & AI, then settle
            refreshLabels();

            statusLabel.setText("You chose to stand.");
            playRemainingPlayersWithDelay();
        });




        engine.startNewRound(50);   // first round

        nextRoundButton.setOnAction(e -> {

            engine.startNewRound(50);
            statusLabel.setText("");

            //refresh all the totals on screen
            refreshLabels();

            hitButton.setDisable(false);
            standButton.setDisable(false);
            nextRoundButton.setDisable(true);
        });

        // back button handler to BlackJackMain screen
        backButton.setOnAction(e -> {
            BlackJackMainScreen main = new BlackJackMainScreen(primaryStage, username);
            main.createMainMenuScene();
            primaryStage.setScene(main.getMainMenuScene());
        });

        refreshLabels();

        return new Scene(root, 800, 600);
    }

    //add refresh helper
    private void refreshLabels() {
        balanceLabel.setText("Balance: $" + engine.getUserBalance());
        userHandLabel.setText("Your hand total: " + engine.getUserHandValue());
        dealerHandLabel.setText("Dealer hand total: " + engine.getDealerHandValue());
        ai1HandLabel.setText("Player 1 hand total: " + engine.getAi1HandValue());
        ai2HandLabel.setText("Player 2 hand total: " + engine.getAi2HandValue());

        if (engine.isRoundOver()) {
            statusLabel.setText("Round over: Earnings: " + engine.getLastUserDelta());
        }
    }
}
