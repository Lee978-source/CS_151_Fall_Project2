package gamestate.snakegame;
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

    public SnakeMainScreen(Stage primaryStage, String username) {
        this.primaryStage = primaryStage;
        this.username = username;
    }

    public Scene createMainMenuScene() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(50));
        root.setStyle("-fx-background-color: #2C3E50;");
    }

    private void startGame() {

    }

    private void showInstructions() {
        VBox root = new VBox(15);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: #2C3E50;");

        Label title = new Label("HOW TO PLAY");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        title.setStyle("-fx-text-fill: #2ECC71;");

        String instructions =
                "";

        Label instructionsLabel = new Label(instructions);
        instructionsLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
        instructionsLabel.setStyle("-fx-text-fill: white;");
        instructionsLabel.setWrapText(true);
        instructionsLabel.setMaxWidth(600);

        Button backButton = new Button("BACK TO MENU");
        styleButton(backButton, "#3498DB");
        backButton.setOnAction(e ->
                primaryStage.setScene(createMainMenuScene())
        );

        root.getChildren().addAll(title, instructionsLabel, backButton);
        Scene instructionsScene = new Scene(root, 800, 600);
        primaryStage.setScene(instructionsScene);
    }

    private void backToMainMenu() {
        // TODO: Return to Game Manager main menu
        System.out.println("Back to main menu (to be implemented with Game Manager)");
        // For now, just show this menu again
        primaryStage.setScene(createMainMenuScene());
    }

}
