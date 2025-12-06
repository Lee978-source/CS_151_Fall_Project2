OVERVIEW:

This project simulates the BlackJack game and the Snake game. Users can create an account or log into an existing one to enter the program. Users can select either the BlackJack game or the Snake game and compete to beat the top 5 high scores for each game. BlackJack is controlled primarily by mouse, and Snake is controlled primarily by keyboard presses. 

DESIGN:

The project is split into two main packages: gamestate and resources. gamestate contains blackjack, snakegame, and cards packages, with the GameManager class being outside of those 3 aforementioned packages. The blackjack package contains a BlackJackEngine for the logic and rules of BlackJack; the BlackJackGamePane sets up the game itself using the Card, Deck, GameState, and Hand classes for additional logic; the BlackJackMainScreen creates the main screen of BlackJack prior to playing. The snakegame package contains Food and Snake classes for the game's logic, and the SnakeGamePane and SnakeMainScreen create the gameboard and the main screen for the game respectively. The GameManager handles user account validation, as well as initializing all of the main screen scenes of the program. 
TO RUN:

Clone the repository, open your terminal and type:
git clone https://github.com/Lee978-source/CS_151_Fall_Project2.git

cd CS_151_Fall_Project2

Compile the code from root of the repo:
mkdir bin

javac -d bin gamestate/GameManager.java

Run the program:
java -cp bin GameManager.Main

Follow the terminal prompts to create accounts, manage files, and edit files with PDF/document/slide/spreadsheet menu features to make changes to your file.
Ethan implemented the Food and GameManager classes and contributed to the Snake and SnakeGamePane classes

Phuong implemented the SnakeMainScreen and BlackJackMainScreen classes and contributed to the Snake and SnakeGamePane classes

Lordin contributed to the BlackJackGamePane, Card, Deck, GameState, and Hand classes

Brian implemented the BlackJackEngine class and contributed to the BlackJackGamePane, Card, Deck, GameState, and Hand classes
