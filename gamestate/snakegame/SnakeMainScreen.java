/**
 * @author [Phuong Hua] 
 * @version 1.0
 * CS151 Fall 2025 - Project 2
 */

package gamestate.snakegame;
import gamestate.GameManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class SnakeMainScreen {
    private Stage primaryStage;
    private String username;
    private GameManager gameManager;
    private Scene mainMenuScene;

    public SnakeMainScreen(Stage primaryStage, String username, GameManager gameManager) {
        this.primaryStage = primaryStage;
        this.username = username;
        this.gameManager = gameManager;
    }

    public void createMainMenuScene() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(50));
        root.setStyle("-fx-background-color: rgba(137,226,234,0.9);");

        Label title = new Label("SNAKE GAME");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 48));
        title.setStyle("-fx-text-fill: #f38fa9;");

        Button startButton = new Button("START GAME");
        styleButton(startButton," #f38fa9");
        startButton.setOnAction(e -> startGame());

        Button instructionsButton = new Button("INSTRUCTIONS");
        styleButton(instructionsButton, "#f38fa9");
        instructionsButton.setOnAction(e -> showInstructions());

        Button backButton = new Button("BACK TO MAIN MENU");
        styleButton(backButton, "#f38fa9");
        backButton.setOnAction(e -> backToMainMenu());

        root.getChildren().addAll(title, startButton, instructionsButton, backButton);
        this.mainMenuScene = new Scene(root, 800, 600);
    }

    public Scene getMainMenuScene() {
        return this.mainMenuScene; // Return the Main Menu Scene of the game.
    }

    private void startGame() {
        SnakeGamePane snakeGamePane = new SnakeGamePane(this.primaryStage, this.username, this.gameManager);
        this.primaryStage.setScene(snakeGamePane.createGameScene()); // Create the Scene object of the Snake Game Pane and set it as the current Scene.
        this.primaryStage.show();

    }

    private void showInstructions() {
        VBox root = new VBox(15);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: rgba(137,226,234,0.9);");

        Label title = new Label("HOW TO PLAY");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        title.setStyle("-fx-text-fill: #f38fa9;");

        String instructions =
                "🎮 CONTROLS:\n" +
                        "   • Use arrow keys (↑ ↓ ← →) to control the snake\n" +
                        "   • Press ESC to pause/resume the game\n\n" +

                        "🎯 OBJECTIVE:\n" +
                        "   • Eat the red food to grow longer\n" +
                        "   • Each food gives you 10 points\n\n" +

                        "⚠️ AVOID:\n" +
                        "   • Running into walls\n" +
                        "   • Running into yourself\n\n" +

                        "🏆 SCORING:\n" +
                        "   • Your score increases as you eat more food\n" +
                        "   • Try to beat the top 5 high scores!\n\n" +

                        "Game over when the snake crashes into a wall or itself.";

        Label instructionsLabel = new Label(instructions);
        instructionsLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 18));
        instructionsLabel.setStyle("-fx-text-fill:#2c3e50;");
        instructionsLabel.setWrapText(true);
        instructionsLabel.setMaxWidth(600);

        Button backButton = new Button("BACK TO MENU");
        styleButton(backButton, "#f38fa9");
        backButton.setOnAction(e ->
                primaryStage.setScene(getMainMenuScene())
        );

        root.getChildren().addAll(title, instructionsLabel, backButton);
        Scene instructionsScene = new Scene(root, 800, 600);
        primaryStage.setScene(instructionsScene);
    }

    // Note by Ethan Le: Helper method to style buttons (example: line 52).
    //gotcha
    private void styleButton(Button button, String color) {
        button.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        button.setPrefWidth(300);
        button.setPrefHeight(50);
        button.setStyle("-fx-background-color: " + color + ";" +" -fx-text-fill: white;"
                + " -fx-background-radius: 10;" + " -fx-cursor: hand;");

        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #ffffff;" +
                " -fx-text-fill: " + color + ";" +
                " -fx-background-radius: 10;" + " -fx-cursor: hand;"));

        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: " + color + ";" +
                " -fx-text-fill: white;" +
                " -fx-background-radius: 10;" + " -fx-cursor: hand;"));
    }

    private void backToMainMenu() {
        // Get the Main Menu screen from the Game Manager and set it as the current scene.
        primaryStage.setScene(this.gameManager.getMainScreen());
    }

}
