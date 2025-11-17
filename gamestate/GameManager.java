/** 
 * @author [Ethan Le] 
 * @version 1.0
 * CS151 Fall 2025 - Project 2
 */
package game_state; 

import java.util.ArrayList; // To create ArrayList objects. 
import java.util.List; // To create List objects. 
import java.io.IOException;
import java.nio.file.Files; // To allow file writing. 
import java.nio.file.Path; // To create an object that locates a file in the system. 
import java.nio.file.Paths; // To retrieve the path to a file via URI or path string. 
import java.nio.file.StandardOpenOption; // To allow the creation or overwriting of a file. 

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.Scene;

public class GameManager extends Application {

	// Global Instance Variables (Global Scenes for the primaryStage to swap between -- persist across the entire GameManager.java) -- recall OOP:
	private Stage primaryStage; // Create Global Instance Variable for the stage itself -- makes it easier to reference it when instantiating the Scenes. 
	private Scene introScreen; 
	private Scene loginScreen; 
	private Scene createAccScreen; 
	private Scene mainScreen; 
	
	// Global Instance UI components to be used in "Log In" and "Create Account" screens (need separate UI components for each one because a node can have ONLY one parent): 
	private Label loginMessageLabel = new Label(""); // Shows success/failure message ("Log In" screen). 
	private TextField loginUsernameField = new TextField(); // Create a text field to enter username ("Log In" screen). 
	private TextField loginPasswordField = new TextField(); // Create a text field to enter password ("Log In" screen).
	private Label createMessageLabel = new Label(""); // Shows success/failure message ("Create Account" screen). 
	private TextField createUsernameField = new TextField(); // Create a text field to enter username ("Create Account" screen). 
	private TextField createPasswordField = new TextField(); // Create a text field to enter password ("Create Account" screen).
	
	// Global Accounts List and Path (to .txt file) to hold all usernames and passwords:
	private List<String> allUsers; // Holds ALL players' username and password (in each index). 
	private Path userAccountsPath = Paths.get("game_state/user_accounts.txt"); // Get path to the location of the user_accounts.txt file (does NOT know details of the file yet). 
	
	// Global Instance Variables to track current player:
	private String username; 
	private String password; 
	
    @Override 
    public void start(Stage primaryStage) { 
    	
    	if (Files.exists(this.userAccountsPath) && Files.isReadable(this.userAccountsPath)) // Make sure the data .txt files exist and are readable. 
    	{
    		// Store the primaryStage variable into the primaryStage Global Instance Variable:
        	this.primaryStage = primaryStage; 
        	
        	// Call "getter" methods to retrieve each of the Global Scenes: 
        	this.introScreen = getIntroScreen(); // Create Scene object for this "Intro" screen to be displayed. 
        	this.loginScreen = getLoginScreen(); // Create Scene object for this "Login" screen to be displayed. 
        	this.createAccScreen = getCreateAccScreen(); // Create Scene object for this "Create Account" screen to be displayed. 
        	this.mainScreen = getMainScreen(); // Create Scene object for this "Main Menu" screen to be displayed. 
        	
        	this.primaryStage.setTitle("Awesome Game Paradise!"); // Title of the Stage (on "Intro" page).
        	this.primaryStage.setScene(this.introScreen); // Set this "Intro" screen as the displayed scene. 
        	this.primaryStage.show(); // Raise the stage curtains! 
    	}
    	
    	else // Print a message if any of the data .txt files do not exist or cannot be read. 
    	{
    		System.out.println("One or more of the data files does not exist or is not readable.");
    	}
    }
    
    /** Create and get the "Intro" Screen: **/
    public Scene getIntroScreen()
    {
    	// Set up UI components upon launching the game ("Intro" page): 
    	Button loginButton = new Button("Log In"); // "Log In" button for player to log into their account. 
    	Button createButton = new Button("Create Account"); // "Create Account" button for player to create a new account. 
    	
    	loginButton.setOnAction(e -> { // If the player has clicked the "Log In" button, 
    		this.primaryStage.setScene(this.loginScreen); // then set the primaryStage to the "Login" screen. 
    		this.primaryStage.setTitle("Log in to your account"); // Title of the Stage (on "Login" page).
    	});
    	
    	createButton.setOnAction(e -> { // If the player has clicked the "Create Account" button, 
    		this.primaryStage.setScene(this.createAccScreen); // then set the primaryStage to the "Create Account" screen. 
    		this.primaryStage.setTitle("Create a new account"); // Title of the Stage (on "Create Account" page).
    	});
    	
    	VBox introLayout = new VBox(10, loginButton, createButton); // Format layout for "Intro" screen. 
		introLayout.setAlignment(Pos.CENTER); // Align the UI components of the "Intro" screen to the center. 
    	Scene introScene = new Scene(introLayout, 600, 600); // Create the Scene object with the VBox-centered layout. 
    	
    	return introScene; // Return the introScene to be assigned to this.introScreen Global Scene. 
    }
    
    /** Create and get the "Log In" Screen: **/
    public Scene getLoginScreen()
    {
    	// Set up UI components upon loading "Log In" screen: 
    	this.loginUsernameField.setPromptText("Enter existing username"); // Prompt text. 
		this.loginPasswordField.setPromptText("Enter password"); // Prompt text. 
		Button loginButton = new Button("Login"); // Button to submit account details to verify login. 
		Button backButton = new Button("Back"); // Button to go back to "Intro" screen. 
		
		loginButton.setOnAction(e -> {
			
			String username = this.loginUsernameField.getText(); // Store the entered username into a String variable. 
			String password = this.loginPasswordField.getText(); // Store the entered password into a String variable. 
			
			// Try-catch block to handle potential IOException (file read/write failure):
			try {
				this.allUsers = Files.readAllLines(this.userAccountsPath); // Read and put all existing players' username and password into the Global Accounts List (all of the lines, which are the details of the file). 
				
				for (String user : this.allUsers)
				{
					if (user.equals(username + "\t" + password)) // If the player successfully logs in, set the scene to the "Main Menu" scene. 
					{
						this.username = username; // Assign the entered username as the current username being used in the program. 
						this.password = password; // Assign the entered password as the current password being used in the program. 
						this.primaryStage.setScene(this.mainScreen); 
						this.primaryStage.setTitle("Welcome, " + username + "! What game would you like to play today?"); // Change the primaryStage title to welcome the user. 
					}
				}
				
				//System.out.println("Failure!"); // For debugging. 
			}
			catch (IOException e1)
			{
				System.out.println("Error reading/writing file: " + e1.getMessage()); // Print message if we get an Input/Output exception with reading the .txt file. 
			}
		});
		
		backButton.setOnAction(e -> { // If the "Back" button was pressed, 
			this.primaryStage.setScene(this.introScreen); // then go back to the "Intro" screen. 
			this.primaryStage.setTitle("Awesome Game Paradise!"); // Title of the Stage (on "Intro" page).
		});
		
		VBox loginLayout = new VBox(10, this.loginUsernameField, this.loginPasswordField, loginButton, backButton); // Format layout for "Log In" screen. 
		
		loginLayout.setAlignment(Pos.CENTER); // Align the UI components of the "Log In" screen to the center. 
		Scene loginScene = new Scene(loginLayout, 600, 600); // Create Scene object for "Log In" screen to be displayed. 
    	
    	return loginScene; // Return the loginScene to be assigned to this.loginScene Global Scene. 
    }
    
    /** Create and get the "Create Account" Screen: **/
    public Scene getCreateAccScreen()
    {
    	// Set up UI components upon loading "Log In" screen: 
    	this.createUsernameField.setPromptText("Enter new username"); // Prompt text. 
		this.createPasswordField.setPromptText("Enter password"); // Prompt text. 
		Button createAccButton = new Button("Create User"); // Button to submit account details to create the new account. 
		Button backButton = new Button("Back"); // Button to go back to "Intro" screen. 
		
		createAccButton.setOnAction(e -> { // If the "Create User" button was pressed, 
			 
			String username = this.createUsernameField.getText(); // Store the entered username into a String variable. 
			String password = this.createPasswordField.getText(); // Store the entered password into a String variable. 
			
			// Try-catch block to handle potential IOException (file read/write failure): 
			try {
				this.allUsers = Files.readAllLines(this.userAccountsPath); // Read and put all existing players' username and password into the Global Accounts List (all of the lines, which are the details of the file). 
				this.allUsers.add(username + "\t" + password); // Store the new username and its corresponding password to the next available index in the allUsers List. 
				
				Files.write(this.userAccountsPath, // Get the path to the location of the new/existing file we wish to create/modify. 
						this.allUsers, // Retrieve the List containing the different "lines" for each player's username and their respective password. 
						StandardOpenOption.CREATE, // Create the new user_accounts.txt file if it does not exist already. 
						StandardOpenOption.TRUNCATE_EXISTING); // Otherwise, overwrite the existing user_accounts.txt file every time a new account is created. 
				
				// Once the account has been validated and thus created, 
				this.username = username; // Assign the entered username as the current username being used in the program. 
				this.password = password; // Assign the entered password as the current password being used in the program. 
				this.primaryStage.setScene(this.mainScreen); // After validating creation of new account, open the "Main Menu" screen. 
				this.primaryStage.setTitle("Welcome, " + username + "! What game would you like to play today?"); // Change the primaryStage title to welcome the user. 
				
			} catch (IOException e1) {
				System.out.println("Error reading/writing file: " + e1.getMessage()); // Print message if we get an Input/Output exception with reading/writing the .txt file. 
			} 
		});
		
		backButton.setOnAction(e -> { // If the "Back" button was pressed, 
			this.primaryStage.setScene(this.introScreen); // then go back to the "Intro" screen. 
			this.primaryStage.setTitle("Awesome Game Paradise!"); // Title of the Stage (on "Intro" page).
		});
		
		VBox createAccLayout = new VBox(10, this.createUsernameField, this.createPasswordField, createAccButton, backButton); // Format layout for "Create Account" screen. 
		
		createAccLayout.setAlignment(Pos.CENTER); // Align the UI components of the "Create Account" screen to the center. 
		Scene createAccScene = new Scene(createAccLayout, 600, 600); // Create Scene object for "Create Account" screen to be displayed. 
    	
    	return createAccScene; // Return the createAccScene to be assigned to this.loginScene Global Scene. 
    }
    
    /** Create and get the "Main Menu" Screen: **/
    public Scene getMainScreen()
    {
    	// Set up UI components upon loading "Main Menu" screen (the side providing the top 5 high scores):
    	Label highScoresBJ = new Label("Top 5 high scores for BlackJack:"); 
    	Label highScoresSnake = new Label("Top 5 high scores for Snake Game:"); 
    	
    	// Set up UI components upon loading "Main Menu" screen (the side providing game options):
    	Button launchBlackJack = new Button("Play BlackJack"); // Button to launch the BlackJack game. 
    	Button launchSnake = new Button("Play Snake Game"); // Button to launch the Snake game. 
    	Button empty1 = new Button("New Game TBA"); // Empty button for future game. 
    	Button empty2 = new Button("New Game TBA"); // Empty button for future game. 
    	Button logout = new Button("Logout"); // Button to log out and go back to "Intro" screen. 
    	
    	logout.setOnAction(e -> { // If the user has selected "Logout" button, 
    		this.username = null; // Set current username to null. 
    		this.password = null; // Set current password to null. 
    		this.primaryStage.setScene(this.introScreen); // Go back to the "Intro" screen where it asked player to login or create an account. 
    		this.primaryStage.setTitle("Awesome Game Paradise!"); // Title of the Stage (on "Intro" page).
    	});
    	
    	// Layout for the side providing the top 5 high scores:
    	VBox scoresLayout = new VBox(10, highScoresBJ, highScoresSnake); // Put components into a Vertical Box. 
    	scoresLayout.setAlignment(Pos.CENTER); // Align the UI components to the center. 
    	
    	// Layout for the side providing game options: 
    	VBox gamesButtonsLayout = new VBox(10, launchBlackJack, launchSnake, empty1, empty2, logout); // Put components into a Vertical Box. 
    	gamesButtonsLayout.setAlignment(Pos.CENTER); // Align UI components to the center. 
    	
    	// Overall screen layout to hold the scoresLayout VBox and the gamesButtonsLayout VBox: 
    	HBox mainLayout = new HBox(100, scoresLayout, gamesButtonsLayout); // Put components into a Horizontal Box. 
    	mainLayout.setAlignment(Pos.CENTER);
    	Scene mainScreen = new Scene(mainLayout, 600, 600); // Create Scene object for this "Main Menu" screen to be displayed. 
    	
    	return mainScreen; // Return the mainScreen to be assigned to this.mainScreen Global Scene. 
    }
   
    /** Main method to launch the JavaFX program: **/ 
    public static void main(String[] args)
    {
    	launch(args); // Allows you to run the JavaFX program. 
    }
}