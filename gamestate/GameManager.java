/** 
 * @author [Ethan Le] 
 * @version 1.0
 * CS151 Fall 2025 - Project 2
 */
package gamestate; 

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
	//private Label loginMessageLabel = new Label(""); // Shows success/failure message ("Log In" screen). 
	private TextField loginUsernameField = new TextField(); // Create a text field to enter username ("Log In" screen). 
	private TextField loginPasswordField = new TextField(); // Create a text field to enter password ("Log In" screen).
	//private Label createMessageLabel = new Label(""); // Shows success/failure message ("Create Account" screen). 
	private TextField createUsernameField = new TextField(); // Create a text field to enter username ("Create Account" screen). 
	private TextField createPasswordField = new TextField(); // Create a text field to enter password ("Create Account" screen).
	
	// Global Accounts List and Path (to .txt file) to hold all usernames and passwords:
	private List<String> allUsers; // Holds ALL players' username and password (in each index). 
	private final Path userAccountsPath = Paths.get("gamestate/user_accounts.txt"); // Get path to the location of the user_accounts.txt file (does NOT know details of the file yet). 

    // High score storage
    private List<String> allHighScores;
    private final Path highScoresPath = Paths.get("gamestate/high_score.txt");

    // Global Instance Variables to track current player:
	private String username; 
	private String password;

    @Override 
    public void start(Stage primaryStage) { 
    	
    	if (Files.exists(this.getAccsPath()) && Files.isReadable(this.getAccsPath())) // Make sure the data .txt files exist and are readable. 
    	{
    		// Store the primaryStage variable into the primaryStage Global Instance Variable:
    		this.setPrimaryStage(primaryStage);

            //Load high scores before building the main menu
            loadHighScores();
        	
        	// Call "setter" methods to create each of the Global Scenes: 
        	this.setIntroScreen(); // Create Scene object for this "Intro" screen to be displayed. 
        	this.setLoginScreen(); // Create Scene object for this "Login" screen to be displayed. 
        	this.setCreateAccScreen(); // Create Scene object for this "Create Account" screen to be displayed. 
        	this.loadHighScores(); //Create Scene object for high scores
            this.setMainScreen(); // Create Scene object for this "Main Menu" screen to be displayed.

            this.getPrimaryStage().setTitle("Awesome Game Paradise!"); // Title of the Stage (on "Intro" page).
        	this.getPrimaryStage().setScene(this.getIntroScreen()); // Set this "Intro" screen as the displayed scene. 
        	this.getPrimaryStage().show(); // Raise the stage curtains! 
    	}
    	
    	else // Print a message if any of the data .txt files do not exist or cannot be read. 
    	{
    		System.out.println("One or more of the data files does not exist or is not readable.");
    	}
    }
    
    /** Getter methods: **/
    public String getUsername()
    {
    	return this.username; // Return the current username in the program. 
    }
    
    public String getPassword()
    {
    	return this.password; // Return the current password in the program. 
    }
    
    public List<String> getUsersList()
    {
    	return this.allUsers; // Return the list of all existing users in the program. 
    }
    
    public Path getAccsPath()
    {
    	return this.userAccountsPath; // Return the path to the location of the user_accounts.txt file (does NOT know details of the file yet).  
    }
    
    public TextField getLoginUserField()
    {
    	return this.loginUsernameField; // Return the global loginUsernameField variable. 
    }
    
    public TextField getLoginPassField()
    {
    	return this.loginPasswordField; // Return the global loginPasswordField variable. 
    }
    
    public TextField getCreateUserField()
    {
    	return this.createUsernameField; // Return the global createUsernameField variable. 
    }
    
    public TextField getCreatePassField()
    {
    	return this.createPasswordField; // Return the global createPasswordField variable. 
    }
    
    public Stage getPrimaryStage()
    {
    	return this.primaryStage; // Return the primaryStage. 
    }
    
    /** Get the "Intro" Screen: **/
    public Scene getIntroScreen()
    {
    	return this.introScreen; // Return the global introScreen variable. 
    }
    
    /** Get the "Log In" Screen: **/
    public Scene getLoginScreen()
    {
    	return this.loginScreen; // Return the global loginScreen variable. 
    }
    
    /** Get the "Create Account" Screen: **/
    public Scene getCreateAccScreen()
    {
    	return this.createAccScreen; // Return the global createAccScreen variable. 
    }
    
    /** Get the "Main Menu" Screen: **/
    public Scene getMainScreen()
    {
    	return this.mainScreen; // Return the global mainScreen variable. 
    }


    /** getters for High Score retrieval **/
    public Path getHighScoresPath() {
        return this.highScoresPath;
    }

    public List<String> getHighScoresList() {
        return this.allHighScores;
    }


    /** Setter method to get high score **/
    public void setHighScoresList(List<String> scores) {
        this.allHighScores = scores;
    }


    /** Setter methods: **/
    public void setUsername(String username) 
    {
    	this.username = username; // Assign the sent username as the global username. 
    }
    
    public void setPassword(String password)
    {
    	this.password = password; // Assign the sent password as the global password. 
    }
    
    public void setUsersList(List<String> allUsers)
    {
    	this.allUsers = allUsers; // Assign the sent allUsers List as the global allUsers. 
    }
    
    public void setPrimaryStage(Stage primaryStage)
    {
    	// Store the primaryStage variable into the primaryStage Global Instance Variable:
    	this.primaryStage = primaryStage; 
    }



    //private helper method for high score
    private void loadHighScores() {
        allHighScores = new ArrayList<>();

        try {
            // If file does not exist, create an empty one
            if (!Files.exists(highScoresPath)) {
                Files.createFile(highScoresPath);
            }

            allHighScores.addAll(Files.readAllLines(highScoresPath));
        } catch (IOException e) {
            System.out.println("Error loading high scores: " + e.getMessage());
        }
    }

    private List<String> getTopScoresForGame(String gameCode, int limit) {
        List<String> scoresForGame = new ArrayList<>();

        for (String line : allHighScores) {
            String[] parts = line.split(",");
            if (parts.length == 3 && parts[1].equalsIgnoreCase(gameCode)) {
                // Format like: "alice - 1200"
                String username = parts[0];
                int score = Integer.parseInt(parts[2]);
                scoresForGame.add(username + " - " + score);
            }
        }

        // Sort descending by score
        scoresForGame.sort((a, b) -> {
            int scoreA = Integer.parseInt(a.substring(a.lastIndexOf('-') + 1).trim());
            int scoreB = Integer.parseInt(b.substring(b.lastIndexOf('-') + 1).trim());
            return Integer.compare(scoreB, scoreA); // highest first
        });

        // Trim to `limit` entries
        if (scoresForGame.size() > limit) {
            return scoresForGame.subList(0, limit);
        } else {
            return scoresForGame;
        }
    }


    /** Create and Set the "Intro" Screen: **/
    public void setIntroScreen() // Create Scene object for this "Intro" screen to be displayed. 
    {
    	// Set up UI components upon launching the game ("Intro" page): 
    	Button loginButton = new Button("Log In"); // "Log In" button for player to log into their account. 
    	Button createButton = new Button("Create Account"); // "Create Account" button for player to create a new account. 
    	
    	loginButton.setOnAction(e -> { // If the player has clicked the "Log In" button, 
    		this.getPrimaryStage().setScene(this.getLoginScreen()); // then set the primaryStage to the "Login" screen. 
    		this.getPrimaryStage().setTitle("Log in to your account"); // Title of the Stage (on "Login" page).
    	});
    	
    	createButton.setOnAction(e -> { // If the player has clicked the "Create Account" button, 
    		this.getPrimaryStage().setScene(this.getCreateAccScreen()); // then set the primaryStage to the "Create Account" screen. 
    		this.getPrimaryStage().setTitle("Create a new account"); // Title of the Stage (on "Create Account" page).
    	});
    	
    	VBox introLayout = new VBox(10, loginButton, createButton); // Format layout for "Intro" screen. 
		introLayout.setAlignment(Pos.CENTER); // Align the UI components of the "Intro" screen to the center. 
    	this.introScreen = new Scene(introLayout, 600, 600); // Create the Scene object with the VBox-centered layout and assign it to the global introScreen variable. 	
    }
    
    /** Create and Set the "Log In" Screen: **/
    public void setLoginScreen() // Create Scene object for this "Login" screen to be displayed. 
    {
    	// Set up UI components upon loading "Log In" screen: 
    	this.getLoginUserField().setPromptText("Enter existing username"); // Prompt text. 
		this.getLoginPassField().setPromptText("Enter password"); // Prompt text. 
		Button loginButton = new Button("Login"); // Button to submit account details to verify login. 
		Button backButton = new Button("Back"); // Button to go back to "Intro" screen.
        Label errorText = new Label(""); // Label to display error message logging in, if necessary.
		
		loginButton.setOnAction(e -> {
			
			String username = this.getLoginUserField().getText(); // Store the entered username into a String variable. 
			String password = this.getLoginPassField().getText(); // Store the entered password into a String variable. 
			
			// Try-catch block to handle potential IOException (file read/write failure):
			try {
				this.setUsersList(Files.readAllLines(this.getAccsPath())); // Read and put all existing players' username and password into the Global Accounts List (all of the lines, which are the details of the file). 
				
				for (String user : this.getUsersList())
				{
					if (user.equals(username + "\t" + password)) // If the player successfully logs in, set the scene to the "Main Menu" scene. 
					{
						this.setUsername(username); // Assign the entered username as the current username being used in the program. 
						this.setPassword(password); // Assign the entered password as the current password being used in the program. 
						this.getPrimaryStage().setScene(this.getMainScreen()); 
						this.getPrimaryStage().setTitle("Welcome, " + username + "! What game would you like to play today?"); // Change the primaryStage title to welcome the user. 
					    errorText.setText(""); // Clear out the error message (if any) if user successfully logs in.
                        return;
                    }
				}

                errorText.setText("Username and Password do not match! Try again!"); // Set the error message label if username and password do not match.
				
				//System.out.println("Failure!"); // For debugging. 
			}
			catch (IOException e1)
			{
				System.out.println("Error reading/writing file: " + e1.getMessage()); // Print message if we get an Input/Output exception with reading the .txt file. 
			}
		});
		
		backButton.setOnAction(e -> { // If the "Back" button was pressed,
            errorText.setText(""); // Clear out the error message (if any) if user returns to previous page.
			this.getPrimaryStage().setScene(this.getIntroScreen()); // then go back to the "Intro" screen. 
			this.getPrimaryStage().setTitle("Awesome Game Paradise!"); // Title of the Stage (on "Intro" page).
		});
		
		VBox loginLayout = new VBox(10, this.getLoginUserField(), this.getLoginPassField(), errorText, loginButton, backButton); // Format layout for "Log In" screen.
		
		loginLayout.setAlignment(Pos.CENTER); // Align the UI components of the "Log In" screen to the center. 
		this.loginScreen = new Scene(loginLayout, 600, 600); // Create Scene object for "Log In" screen to be displayed and assign it to the global loginScreen variable. 
    }
    
    /** Create and Set the "Create Account" Screen: **/
    public void setCreateAccScreen() // Create Scene object for this "Create Account" screen to be displayed. 
    {
    	// Set up UI components upon loading "Log In" screen: 
    	this.getCreateUserField().setPromptText("Enter new username"); // Prompt text. 
		this.getCreatePassField().setPromptText("Enter password (must be 6 characters long)"); // Prompt text.
		Button createAccButton = new Button("Create User"); // Button to submit account details to create the new account. 
		Button backButton = new Button("Back"); // Button to go back to "Intro" screen.
        Label guideText = new Label(""); // Label to be displayed if user needs help creating their account.
		
		createAccButton.setOnAction(e -> { // If the "Create User" button was pressed, 
			 
			String username = this.getCreateUserField().getText().strip(); // Store the entered username into a String variable.
			String password = this.getCreatePassField().getText().strip(); // Store the entered password into a String variable.

            if (password.length() >= 6)
            {
                guideText.setText(""); // Clear the guide label (if any) upon successful account creation.

                // Try-catch block to handle potential IOException (file read/write failure):
                try {
                    this.setUsersList(Files.readAllLines(this.getAccsPath())); // Read and put all existing players' username and password into the Global Accounts List (all of the lines, which are the details of the file).
                    this.getUsersList().add(username + "\t" + password); // Store the new username and its corresponding password to the next available index in the allUsers List.

                    Files.write(this.getAccsPath(), // Get the path to the location of the new/existing file we wish to create/modify.
                            this.getUsersList(), // Retrieve the List containing the different "lines" for each player's username and their respective password.
                            StandardOpenOption.CREATE, // Create the new user_accounts.txt file if it does not exist already.
                            StandardOpenOption.TRUNCATE_EXISTING); // Otherwise, overwrite the existing user_accounts.txt file every time a new account is created.

                    // Once the account has been validated and thus created,
                    this.setUsername(username); // Assign the entered username as the current username being used in the program.
                    this.setPassword(password); // Assign the entered password as the current password being used in the program.
                    this.getPrimaryStage().setScene(this.getMainScreen()); // After validating creation of new account, open the "Main Menu" screen.
                    this.getPrimaryStage().setTitle("Welcome, " + username + "! What game would you like to play today?"); // Change the primaryStage title to welcome the user.

                } catch (IOException e1) {
                    System.out.println("Error reading/writing file: " + e1.getMessage()); // Print message if we get an Input/Output exception with reading/writing the .txt file.
                }
            }

            else
            {
                guideText.setText("Password must be at least 6 characters long! Try again!"); // Set the guide label to inform user of password requirement.
            }

		});
		
		backButton.setOnAction(e -> { // If the "Back" button was pressed,
            guideText.setText(""); // Clear the guide label (if any) if user returns to previous page.
			this.getPrimaryStage().setScene(this.getIntroScreen()); // then go back to the "Intro" screen. 
			this.getPrimaryStage().setTitle("Awesome Game Paradise!"); // Title of the Stage (on "Intro" page).
		});
		
		VBox createAccLayout = new VBox(10, this.getCreateUserField(), this.getCreatePassField(), guideText, createAccButton, backButton); // Format layout for "Create Account" screen.
		
		createAccLayout.setAlignment(Pos.CENTER); // Align the UI components of the "Create Account" screen to the center. 
		this.createAccScreen = new Scene(createAccLayout, 600, 600); // Create Scene object for "Create Account" screen to be displayed and assign it to the global createAccScreen variable. 
    }

    /** Create and Set the "Main Menu" Screen: **/
    public void setMainScreen() // Create Scene object for this "Main Menu" screen to be displayed. 
    {
    	// Set up UI components upon loading "Main Menu" screen (the side providing the top 5 high scores):
    	Label highScoresBJ = new Label("Top 5 high scores for BlackJack:"); 
    	Label highScoresSnake = new Label("Top 5 high scores for Snake Game:");

        //Show the first 5 highest scores for BJ
        VBox bjBox = new VBox(5); //5 px space between rows
        bjBox.getChildren().add(highScoresBJ);
        for(String line : getTopScoresForGame("BJ", 5)){
            bjBox.getChildren().add(new Label(line));
        }

        //Show the first 5 highest scores for Snake
        VBox snakeBox = new VBox(5);
        snakeBox.getChildren().add(highScoresSnake);
        for(String line: getTopScoresForGame("SNAKE", 5)) {
            snakeBox.getChildren().add(new Label(line));
        }


    	
    	// Set up UI components upon loading "Main Menu" screen (the side providing game options):
    	Button launchBlackJack = new Button("Play BlackJack"); // Button to launch the BlackJack game. 
    	Button launchSnake = new Button("Play Snake Game"); // Button to launch the Snake game. 
    	Button empty1 = new Button("New Game TBA"); // Empty button for future game. 
    	Button empty2 = new Button("New Game TBA"); // Empty button for future game. 
    	Button logout = new Button("Logout"); // Button to log out and go back to "Intro" screen. 
    	
    	logout.setOnAction(e -> { // If the user has selected "Logout" button, 
    		this.setUsername(null); // Set current username to null. 
    		this.setPassword(null); // Set current password to null. 
    		this.getPrimaryStage().setScene(this.getIntroScreen()); // Go back to the "Intro" screen where it asked player to login or create an account. 
    		this.getPrimaryStage().setTitle("Awesome Game Paradise!"); // Title of the Stage (on "Intro" page).
    	});

        // Layout for the side providing the top 5 high scores:
        VBox scoresLayout = new VBox(20, bjBox, snakeBox);
        scoresLayout.setAlignment(Pos.CENTER);

    	// Layout for the side providing game options:
    	VBox gamesButtonsLayout = new VBox(10, launchBlackJack, launchSnake, empty1, empty2, logout); // Put components into a Vertical Box.
    	gamesButtonsLayout.setAlignment(Pos.CENTER); // Align UI components to the center.
    	
    	// Overall screen layout to hold the scoresLayout VBox and the gamesButtonsLayout VBox: 
    	HBox mainLayout = new HBox(100, scoresLayout, gamesButtonsLayout); // Put components into a Horizontal Box. 
    	mainLayout.setAlignment(Pos.CENTER);
    	this.mainScreen = new Scene(mainLayout, 600, 600); // Create Scene object for this "Main Menu" screen to be displayed and assign it to the global mainScreen variable. 
    }



    /** Main method to launch the JavaFX program: **/ 
    public static void main(String[] args)
    {
    	launch(args); // Allows you to run the JavaFX program. 
    }
}