/** 
 * @author [Ethan Le] 
 * @version 1.0
 * CS151 Fall 2025 - Project 2
 */
package game_state; 

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.Scene;

public class GameManager extends Application {

    @Override 
    public void start(Stage primaryStage) {
        
    	// Set up UI components upon launching the game (intro page): 
    	Button loginButton = new Button("Log In"); // "Log In" button for player to log into their account. 
    	Button createButton = new Button("Create Account"); // "Create Account" button for player to create a new account. 
    	primaryStage.setTitle("Awesome Game Paradise!"); // Title of the Stage (on intro page). 
    
    	// UI components to be used interchangeably between "Log In" and "Create Account": 
    	Label messageLabel = new Label(); // Shows success/failure message. 
    	Button submit = new Button("Submit"); // Button for player to press after filling out username and password. 
    	TextField usernameField = new TextField(); // Create a text field to enter username. 
    	TextField passwordField = new TextField(); // Create a text field to enter password. 
    	
    	/** Commence respective actions for each button: **/
    	// Upon clicking the "Log In" button...
    	loginButton.setOnAction(e -> {
       		usernameField.setPromptText("Enter existing username"); // Prompt text. 
    		passwordField.setPromptText("Enter password"); // Prompt text. 
    		
    		VBox loginLayout = new VBox(10, usernameField, passwordField, submit); // Format layout for "Log In" screen. 
    		
    		primaryStage.setTitle("Log in to your account"); // Title of the Stage (Login page). 
    		Scene loginScene = new Scene(loginLayout, 600, 600); // Create Scene object for Login screen to be displayed. 
    		primaryStage.setScene(loginScene); // Set this Login screen as the displayed scene. 
    		
    	});
    	
    	// Upon clicking the "Create Account" button... 
    	createButton.setOnAction(e -> {
    		usernameField.setPromptText("Enter new username"); // Prompt text. 
    		passwordField.setPromptText("Enter password"); // Prompt text. 
    		
    		VBox createAccLayout = new VBox(10, usernameField, passwordField, submit); // Format layout for "Create Account" screen. 
    		
    		primaryStage.setTitle("Create a new account"); // Title of the Stage ("Create Account" page). 
    		Scene createAccScene = new Scene(createAccLayout, 600, 600); // Create Scene object for "Create Account" screen to be displayed. 
    		primaryStage.setScene(createAccScene); // Set this "Create Account" screen as the displayed scene. 
    	}); 
    	
    	// Once the user clicks "Submit" after entering their username and password... (continue)
    	submit.setOnAction(e -> {
			String name = usernameField.getText(); // Put the contents from the usernameField into a String. 
			String password = passwordField.getText(); // Put the contents from the passwordField into a String. 
			
		});
    	
    	VBox layout = new VBox(10, loginButton, createButton);  // Format layout for intro screen to choose between login or create account buttons. 
    	Scene scene = new Scene(layout, 600, 600); // Create Scene object for this intro screen to be displayed. 
    	primaryStage.setScene(scene); // Set this intro screen as the displayed scene. 
    	primaryStage.show(); // Raise the stage curtains! 
    }
    
    public static void main(String[] args)
    {
    	launch(args); // Allows you to run the JavaFX program. 
    }
} 