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

    public SnakeMainScreen(Stage primaryStage, String username, GameManager gameManager) {
        this.primaryStage = primaryStage;
        this.username = username;
        this.gameManager = gameManager;
    }

    public Scene createMainMenuScene() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(50));
        root.setStyle("-fx-background-color: #324960ff;");

        Label title = new Label("SNAKE GAME");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 48));
        title.setStyle("-fx-text-fill: #c941b2ff;");

        Button startButton = new Button("START GAME");
        styleButton(startButton, "#27ae60ff");
        startButton.setOnAction(e -> startGame());

        Button instructionsButton = new Button("INSTRUCTIONS");
        styleButton(instructionsButton, "#2980b9ff");
        instructionsButton.setOnAction(e -> showInstructions());

        Button backButton = new Button("BACK TO MAIN MENU");
        styleButton(backButton, "#c0392bff");
        backButton.setOnAction(e -> backToMainMenu());

        root.getChildren().addAll(title, startButton, instructionsButton, backButton);
        Scene scene = new Scene(root, 800, 600);

        return scene; // Placeholder - Note by Ethan Le: make sure you have a return statement and are returning the Scene object.
    }

    private void startGame() {
        SnakeGamePane snakeGamePane = new SnakeGamePane(primaryStage, username, gameManager);
        primaryStage.setScene(snakeGamePane.getGameScene());
        primaryStage.show();

    }

    private void showInstructions() {
        VBox root = new VBox(15);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: #2C3E50;");

        Label title = new Label("HOW TO PLAY");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        title.setStyle("-fx-text-fill: #792cbcff;");

        String instructions =
                ""; //We will fill this in later with actual instructions together.

        Label instructionsLabel = new Label(instructions);
        instructionsLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
        instructionsLabel.setStyle("-fx-text-fill: white;");
        instructionsLabel.setWrapText(true);
        instructionsLabel.setMaxWidth(600);

        Button backButton = new Button("BACK TO MENU");
        styleButton(backButton, "#297cb4ff");
        backButton.setOnAction(e ->
                primaryStage.setScene(createMainMenuScene())
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
        // TODO: Return to Game Manager main menu
        System.out.println("Back to main menu (to be implemented with Game Manager)");
        // For now, just show this menu again
        primaryStage.setScene(createMainMenuScene());
    }

}
