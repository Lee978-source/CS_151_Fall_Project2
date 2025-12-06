package gamestate.blackjack;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.TextField;
import javafx.geometry.Insets;
import java.util.List;
// for file I/O
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class BlackJackGamePane {
    private final Stage primaryStage;
    private final String username;
    private final BlackJackEngine engine;

    private static final String SAVE_FILE_NAME = "blackjack_save_state.json";

    private static final int CARD_WIDTH = 70;
    private static final int MIN_BET = 50; // min bet

    // score interface
    private Label balanceLabel;
    private Label userScoreLabel;
    private Label dealerScoreLabel;
    private Label statusLabel;
    private Label ai1ScoreLabel;
    private Label ai2ScoreLabel;

    // player interface
    private HBox userCardBox;
    private HBox dealerCardBox;
    private HBox ai1CardBox;
    private HBox ai2CardBox;

    // betting interface
    private HBox bettingInputPanel;
    private TextField betAmountField;
    private Button placeBetButton;

    private Button hitButton;
    private Button standButton;
    private Button nextRoundButton;

    // save/load buttons
    private Button saveButton;
    private Button loadButton;

    private BlackJackMainScreen mainScreen;

    public BlackJackGamePane(Stage primaryStage, String username, BlackJackMainScreen mainScreen) {
        this.primaryStage = primaryStage;
        this.username = username;
        this.mainScreen = mainScreen;
        // Start balance: 1000
        this.engine = new BlackJackEngine(1000);
    }

    /** Builds and returns the Blackjack "game" scene */
    public Scene createGameScene() {

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #006400;");
        root.setPadding(new Insets(20));

        // title
        Label title = new Label("Blackjack – playing as " + username);
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #ffffff;");
        root.setTop(title);
        BorderPane.setAlignment(title, Pos.CENTER);

        balanceLabel = new Label();
        statusLabel = new Label();
        statusLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #f1c40f;");
        balanceLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #ffffff;");

        // initialize score
        userScoreLabel = new Label();
        dealerScoreLabel = new Label();
        ai1ScoreLabel = new Label();
        ai2ScoreLabel = new Label();
        String scoreLabelStyle = "-fx-text-fill: #ecf0f1;";
        userScoreLabel.setStyle(scoreLabelStyle);
        dealerScoreLabel.setStyle(scoreLabelStyle);
        ai1ScoreLabel.setStyle(scoreLabelStyle);
        ai2ScoreLabel.setStyle(scoreLabelStyle);

        userCardBox = new HBox(10);
        dealerCardBox = new HBox(10);
        ai1CardBox = new HBox(10);
        ai2CardBox = new HBox(10);

        userCardBox.setAlignment(Pos.CENTER);
        dealerCardBox.setAlignment(Pos.CENTER);
        ai1CardBox.setAlignment(Pos.CENTER);
        ai2CardBox.setAlignment(Pos.CENTER);

        VBox dealerDisplay = createPlayerDisplay(BlackJackEngine.dealer, dealerScoreLabel, dealerCardBox);
        VBox userDisplay = createPlayerDisplay(BlackJackEngine.player, userScoreLabel, userCardBox);
        VBox ai1Display = createPlayerDisplay(BlackJackEngine.ai1, ai1ScoreLabel, ai1CardBox);
        VBox ai2Display = createPlayerDisplay(BlackJackEngine.ai2, ai2ScoreLabel, ai2CardBox);

        HBox playerRow = new HBox(30, ai1Display, userDisplay, ai2Display);
        playerRow.setAlignment(Pos.CENTER);

        VBox centerBox = new VBox(20, dealerDisplay, playerRow, statusLabel);
        centerBox.setAlignment(Pos.CENTER);
        root.setCenter(centerBox);

        String whiteButtonStyle = "-fx-background-color: white; -fx-text-fill: black; -fx-padding: 8 16; -fx-font-weight: bold; -fx-border-color: black; -fx-border-width: 1;";

        Button backBtn = new Button("Back to Blackjack Menu");

        backBtn.setStyle(whiteButtonStyle);
        backBtn.setOnAction(e -> {
            this.primaryStage.setScene(this.mainScreen.getMainMenuScene());
        });

        hitButton = new Button("Hit");
        standButton = new Button("Stand");
        nextRoundButton = new Button("New Round");

        // initialize button
        saveButton = new Button("Save Game");
        loadButton = new Button("Load Game");

        // white buttons style
        hitButton.setStyle(whiteButtonStyle);
        standButton.setStyle(whiteButtonStyle);
        nextRoundButton.setStyle(whiteButtonStyle);
        saveButton.setStyle(whiteButtonStyle);
        loadButton.setStyle(whiteButtonStyle);

        bettingInputPanel = createBettingInputPanel();

        HBox gameControlsRow = new HBox(15, hitButton, standButton, nextRoundButton);
        gameControlsRow.setAlignment(Pos.CENTER);

        HBox saveRow = new HBox(15, saveButton, loadButton);
        saveRow.setAlignment(Pos.CENTER);

        VBox bottomBox = new VBox(15, balanceLabel, bettingInputPanel, gameControlsRow, saveRow, backBtn);
        bottomBox.setAlignment(Pos.CENTER);
        root.setBottom(bottomBox);

        // action handlers
        hitButton.setOnAction(e -> {
            engine.userHit();
            refreshLabels();

            if (engine.isRoundOver()) {
                setControls(false, false, true);
            } else if (!engine.getCurrentPlayer().equals(BlackJackEngine.player)) {
                setControls(false, false, true);
            }
        });

        standButton.setOnAction(e -> {
            engine.userStand();
            refreshLabels();
            setControls(false, false, true);
        });

        nextRoundButton.setOnAction(e -> startBettingPhase());
        placeBetButton.setOnAction(e -> handlePlaceBet());

        saveButton.setOnAction(e -> handleSaveGame());
        loadButton.setOnAction(e -> handleLoadGame());

        // initial game state
        startBettingPhase();

        return new Scene(root, 800, 850);
    }

    private HBox createBettingInputPanel() {
        Label betLabel = new Label("Place Bet (Min $" + MIN_BET + "):");
        betLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

        betAmountField = new TextField(String.valueOf(MIN_BET));
        betAmountField.setMaxWidth(80);

        placeBetButton = new Button("Place Bet");
        // style the buttons to white with black text
        placeBetButton.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black; -fx-border-width: 1;");

        HBox panel = new HBox(10, betLabel, betAmountField, placeBetButton);
        panel.setAlignment(Pos.CENTER);
        panel.setPadding(new Insets(10, 0, 10, 0));
        return panel;
    }

    private void handlePlaceBet() {
        try {
            int betAmount = Integer.parseInt(betAmountField.getText().trim());
            if (betAmount < MIN_BET) {
                statusLabel.setText("Minimum bet is $" + MIN_BET + ".");
                return;
            }
            if (betAmount > engine.getBalance(BlackJackEngine.player)) {
                statusLabel.setText("Insufficient funds! Balance: $" + engine.getBalance(BlackJackEngine.player) + ".");
                return;
            }

            engine.startNewRound(betAmount);
            statusLabel.setText("Bet placed: $" + betAmount + ". Your turn! Hit or Stand?");

            // switch control to game mode
            setControls(true, false, false);

            refreshLabels(); // refresh UI to show cards

        } catch (NumberFormatException e) {
            statusLabel.setText("Please enter a valid number for your bet.");
        } catch (IllegalStateException e) {
            statusLabel.setText("Cannot start round. " + e.getMessage());
        }
    }

    // methods for GSON
    // saves the game state JSON string
    private static boolean saveGameToFile(String jsonState) {
        try (FileWriter file = new FileWriter(SAVE_FILE_NAME)) {
            file.write(jsonState);
            file.flush();
            return true;
        } catch (IOException e) {
            System.err.println("Error saving game state: " + e.getMessage());
            return false;
        }
    }

    // loads the game state JSON string
    private static String loadGameFromFile() {
        try {
            File file = new File(SAVE_FILE_NAME);
            if (!file.exists()) {
                System.out.println("No save file found.");
                return null;
            }
            // reads the content from the file
            return Files.readString(Paths.get(SAVE_FILE_NAME));
        } catch (IOException e) {
            System.err.println("Error loading game state: " + e.getMessage());
            return null;
        }
    }

    // handler for Save button
    private void handleSaveGame() {
        String savedJson = engine.saveState();

        boolean success = saveGameToFile(savedJson);

        if (success) {
            statusLabel.setText("Game state saved successfully!");
        } else {
            statusLabel.setText("Error: Could not save game state.");
        }
    }

    // handler for Load button
    private void handleLoadGame() {
        String loadedJson = loadGameFromFile();

        if (loadedJson != null) {
            engine.loadState(loadedJson);

            refreshLabels();

            if (engine.isRoundOver()) {
                setControls(false, false, true);
                statusLabel.setText("Game loaded! Round finished. Press 'New Round' to continue.");
            } else if (engine.getCurrentPlayer().equals(BlackJackEngine.player)) {
                setControls(true, false, false);
                statusLabel.setText("Game loaded! It is your turn. Hit or Stand?");
            } else {
                setControls(false, false, true);
                statusLabel.setText("Game loaded! Press 'New Round' to continue.");
            }

        } else {
            statusLabel.setText("Error: Could not load save data.");
        }
    }


    // helper methods to set game state
    private void setControls(boolean hitStandEnabled, boolean bettingEnabled, boolean nextRoundEnabled) {
        hitButton.setDisable(!hitStandEnabled);
        standButton.setDisable(!hitStandEnabled);
        nextRoundButton.setDisable(!nextRoundEnabled);

        bettingInputPanel.setVisible(bettingEnabled);
        bettingInputPanel.setManaged(bettingEnabled);

        saveButton.setDisable(false);
        loadButton.setDisable(false);
    }


    private void startBettingPhase() {
        // clear all card visuals
        dealerCardBox.getChildren().clear();
        userCardBox.getChildren().clear();
        ai1CardBox.getChildren().clear();
        ai2CardBox.getChildren().clear();

        dealerScoreLabel.setText("Dealer Total: 0");
        userScoreLabel.setText("Hand Total: 0");
        ai1ScoreLabel.setText("Hand Total: 0");
        ai2ScoreLabel.setText("Hand Total: 0");

        if (engine.getBalance(BlackJackEngine.player) < MIN_BET) {
            statusLabel.setText("Not enough balance to start a new round! Game Over! GG's");
            setControls(false, false, false);
            return;
        }

        balanceLabel.setText("Balance (" + username + "): $" + engine.getBalance(BlackJackEngine.player));
        statusLabel.setText("Place your bet to start the round.");

        // allow betting
        setControls(false, true, false);
    }

    // helper to display box for player
    private VBox createPlayerDisplay(String playerName, Label scoreLabel, HBox cardBox) {
        Label title = new Label(playerName);
        // title size for player names
        title.setStyle("-fx-font-weight: bold; -fx-text-fill: #ecf0f1; -fx-font-size: 20px;");
        cardBox.setAlignment(Pos.CENTER);
        VBox display = new VBox(5, title, scoreLabel, cardBox);
        display.setAlignment(Pos.CENTER);
        return display;
    }

    private ImageView createImageView(String path) {
        ImageView img = new ImageView();
        try {
            Image image = new Image(getClass().getResourceAsStream(path));
            img.setImage(image);
            img.setFitWidth(CARD_WIDTH);
            img.setPreserveRatio(true);
        } catch (Exception e) {
            System.err.println("Image could not be loaded at path: " + path);
            img.setFitWidth(CARD_WIDTH);
            img.setFitHeight(CARD_WIDTH * 1.4);
        }
        return img;
    }

    // helper method to display cards in container
    private void displayHand(HBox cardBox, Hand hand, boolean hideFirstCard) {
        cardBox.getChildren().clear();
        List<Card> cards = hand.getCards();

        for (int i = 0; i < cards.size(); i++) {
            Card card = cards.get(i);
            ImageView cardView;

            if (hideFirstCard && i == 0) {
                cardView = createImageView("../cards/red_back.png");
            } else {
                String fileName = card.getFileName();
                cardView = createImageView("../cards/" + fileName);
            }
            cardBox.getChildren().add(cardView);
        }
    }

    // helper method to update all labels and displays based on game state
    private void refreshLabels() {
        // update player hands and hand scores
        Hand userHand = engine.getHand(BlackJackEngine.player);
        userScoreLabel.setText("Hand Total: " + userHand.calculateValue());
        displayHand(userCardBox, userHand, false);

        Hand ai1Hand = engine.getHand(BlackJackEngine.ai1);
        ai1ScoreLabel.setText("Hand Total: " + ai1Hand.calculateValue());
        displayHand(ai1CardBox, ai1Hand, false);

        Hand ai2Hand = engine.getHand(BlackJackEngine.ai2);
        ai2ScoreLabel.setText("Hand Total: " + ai2Hand.calculateValue());
        displayHand(ai2CardBox, ai2Hand, false);

        if (engine.isRoundOver()) {
            dealerScoreLabel.setText("Dealer Total: " + engine.getHand(BlackJackEngine.dealer).calculateValue());
            displayHand(dealerCardBox, engine.getHand(BlackJackEngine.dealer), false);

            int delta = engine.getLastUserDelta();
            String resultText;

            if (delta > 0) {
                resultText = "Win! (+$" + delta + ")";
            } else if (delta < 0) {
                resultText = "Lose! (-$" + Math.abs(delta) + ")";
            } else {
                resultText = "Push! Bet is returned";
            }

            statusLabel.setText("Round Over! Your Result: " + resultText);

            // ensure next round is enabled
            setControls(false, false, true);

        } else if (!userHand.getCards().isEmpty()) {
            dealerScoreLabel.setText("Total: " + engine.getDealerVisibleScore());
            displayHand(dealerCardBox, engine.getHand(BlackJackEngine.dealer), true);

            boolean userTurn = engine.getCurrentPlayer().equals(BlackJackEngine.player);
            if (userTurn) {
                statusLabel.setText("Current Turn: " + engine.getCurrentPlayer() + ". Hit or Stand?");
                setControls(true, false, false);
            }
        }
        // updates the balance after the round is complete (or whenever refreshed)
        balanceLabel.setText("Balance (" + username + "): $" + engine.getBalance(BlackJackEngine.player));
    }
}