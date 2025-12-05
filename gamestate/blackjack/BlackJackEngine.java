package gamestate.blackjack;
import java.util.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

public class BlackJackEngine {
    //Logic for 1 human vs 2 ai, 1 dealer

    public static final String dealer = "Dealer";
    public static final String player = "User";
    public static final String ai1 = "AI Player 1";
    public static final String ai2 = "AI Player 2";

    private final Random rand = new Random();
    private final Gson gson; // for save state
    // game state
    private Deck deck;
    private final LinkedHashMap<String, Player> players;
    private Hand dealerHand;

    private int betAmount = 0;
    private int playerIndex = 0;
    private boolean roundOver = false;
    private int lastUserDelta = 0;
    private final List<String> activePlayers;

    private static class Player {
        Hand hand = new Hand();
        int balance;
        int bet;
        boolean hasStood = false;

        Player(int startBalance) {
            this.balance = startBalance;
        }
    }

    /**Everyone has the same starting balance **/
    public BlackJackEngine(int startingBalance){
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.deck = new Deck();
        this.dealerHand = new Hand();


        // initialize players in turn order
        this.players = new LinkedHashMap<>();
        players.put(player, new Player(startingBalance));
        players.put(ai1, new Player(startingBalance));
        players.put(ai2, new Player(startingBalance));

        this.activePlayers = new ArrayList<>(players.keySet());
    }

    // gson methods
    public String saveState() {
        LinkedHashMap<String, GameState.PlayerState> gsPlayerStates = new LinkedHashMap<>();
        for (Map.Entry<String, Player> entry : players.entrySet()) {
            Player p = entry.getValue();
            gsPlayerStates.put(entry.getKey(),
                    new GameState.PlayerState(p.hand, p.balance, p.bet, p.hasStood));
        }

        GameState gameState = new GameState(
                playerIndex,
                betAmount,
                roundOver,
                lastUserDelta,
                dealerHand,
                gsPlayerStates,
                deck
        );

        return gson.toJson(gameState);
    }

    public void loadState(String savedState) {
        if (savedState == null || savedState.isEmpty()) {
            System.out.println("No saved state to load. :(");
            return;
        }

        try {
            GameState loadedState = gson.fromJson(savedState, GameState.class);

            this.playerIndex = loadedState.currentPlayerIndex;
            this.betAmount = loadedState.betAmount;
            this.roundOver = loadedState.roundOver;
            this.lastUserDelta = loadedState.lastUserDelta;

            this.dealerHand = loadedState.dealerHand;
            this.deck = loadedState.deck;
            this.players.clear();
            this.activePlayers.clear();

            for (Map.Entry<String, GameState.PlayerState> entry : loadedState.playerStates.entrySet()) {
                String name = entry.getKey();
                GameState.PlayerState ps = entry.getValue();

                Player p = new Player(ps.balance);

                p.hand = ps.hand;
                p.balance = ps.balance;
                p.hasStood = ps.hasStood;

                this.players.put(name, p);
            }
            this.activePlayers.addAll(this.players.keySet());

            System.out.println("Game state loaded successfully!!!");

        } catch (JsonSyntaxException e) {
            System.err.println("Error parsing JSON state: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error loading game state: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // --------- round duration ------------
    /**
     * Start new round
     * For now: user chooses bet then AI bets are auto
     */

    public void startNewRound(int userBet) {
        if (userBet <= 0) {
            throw new IllegalArgumentException("Bet must be positive");
        }
        this.betAmount = userBet;
        lastUserDelta = 0;

        deck.initializeDeck();
        deck.shuffle();
        dealerHand.clear();
        roundOver = false;

        activePlayers.clear();

        // using a map helps manage the turns for the player and AI's
        for (Map.Entry<String, Player> entry : players.entrySet()) {
            Player p = entry.getValue();
            p.hand.clear();
            p.hasStood = false;
            p.bet = userBet;

            p.balance -= userBet;

            activePlayers.add(entry.getKey());
        }
        for (int i = 0; i < 2; i++) {
            for (String playerName : activePlayers) {
                players.get(playerName).hand.addCard(deck.drawCard());
            }
            dealerHand.addCard(deck.drawCard());
        }
        // if player gets blackjack for 1st two cards, stand
        Player user = players.get(player);
        if (user.hand.calculateValue() == 21) {
            user.hasStood = true;
            dealerTurnAndSettle();
            return;
        }
        playerIndex = 0;
        if (roundOver) {
            return;
        }
        if (!activePlayers.isEmpty() && !activePlayers.get(0).equals(player)) {
            runAiTurn(activePlayers.get(0));
        }
    }

    /**
     * Human Action
     */

    //User hits. Returns hand total
    public Card userHit(){
        if (isRoundOver() || !player.equals(getCurrentPlayer())) {
            return null;
        }

        Player user = players.get(player);
        Card newCard = deck.drawCard();
        user.hand.addCard(newCard);

        if (user.hand.calculateValue() >= 21) {
            user.hasStood = true;
            advanceTurn();
        }
        return newCard;
    }

    public void userStand(){
        if (isRoundOver() || !player.equals(getCurrentPlayer())) {
            return;
        }
        players.get(player).hasStood = true;
        advanceTurn();
    }

    public void dealerTurnAndSettle() {
        if (isRoundOver()) {
            return;
        }

        while (dealerHand.calculateValue() < 17) {
            dealerHand.addCard(deck.drawCard());
        }

        settleRound();
        roundOver = true;
    }

    /**
     * AI Logic
     * We want AI to hit while hand is <= 16
     */
    private void runAiTurn(String aiName){
        if (isRoundOver()) {
            return;
        }

        Player ai = players.get(aiName);
        Hand hand = ai.hand;

        while (hand.calculateValue() < 17) {
            hand.addCard(deck.drawCard());
        }
        ai.hasStood = true;
    }

    public String advanceTurn() {
        playerIndex++;

        while (playerIndex < activePlayers.size()) {
            String nextPlayerName = activePlayers.get(playerIndex);
            Player nextPlayer = players.get(nextPlayerName);

            if (!nextPlayer.hasStood && nextPlayer.hand.calculateValue() < 21) {
                if (nextPlayerName.equals(player)) {
                    return player;
                } else {
                    runAiTurn(nextPlayerName);
                }
            }
            playerIndex++;
        }
        dealerTurnAndSettle();
        return "Round End";
    }


    // finished the round by looking at each player status
    private Map<String, String> settleRound() {
        Map<String, String> results = new LinkedHashMap<>();
        int dealerScore = dealerHand.calculateValue();
        boolean dealerBust = dealerScore > 21;

        for (String name : players.keySet()) {
            Player p = players.get(name);
            int playerScore = p.hand.calculateValue();
            String resultText;
            int payoutAmount = 0;


            // bust if hand is higher than 21
            boolean playerBust = playerScore > 21;

            if (playerBust) {
                // bet already deducted
                resultText = "Bust! Loses $" + p.bet;
            } else if (p.hand.isBlackjack()) {
                if (dealerHand.isBlackjack()) {
                    payoutAmount = p.bet;
                    resultText = "Push! Bet returned.";
                } else {
                    payoutAmount = (int) (p.bet * 2.5);
                    resultText = "Blackjack! Wins $" + (int) (p.bet * 1.5);
                }
            } else if (dealerBust) {
                payoutAmount = p.bet * 2;
                resultText = "Dealer bust! Wins $" + p.bet;
            } else if (playerScore > dealerScore) {
                payoutAmount = p.bet * 2;
                resultText = "Win! Wins $" + p.bet;
            } else if (playerScore < dealerScore) {
                payoutAmount = 0;
                resultText = "Lose! Loses $" + p.bet;
            } else {
                payoutAmount = p.bet;
                resultText = "Push! Bet returned.";
            }

            p.balance += payoutAmount;

            results.put(name, resultText);

            if (name.equals(player)) {
                lastUserDelta = payoutAmount - p.bet;
            }
        }
        return results;
    }

    // ---------- getters for UI/debug ----------

    public Hand getHand(String playerName) {
        if (playerName.equals(dealer)) {
            return dealerHand;
        }
        Player p = players.get(playerName);

        if (p != null) {
            return p.hand;
        } else {
            return new Hand();
        }
    }

    public int getBalance(String playerName) {
        Player p = players.get(playerName);

        if (p != null) {
            return p.balance;
        } else {
            return 0;
        }
    }

    public String getCurrentPlayer() {
        if (isRoundOver() || playerIndex >= activePlayers.size()) {
            return "Round End!";
        }
        return activePlayers.get(playerIndex);
    }

    public int getDealerVisibleScore() {
        if (dealerHand.getCards().size() < 2) {
            return 0;
        }
        return dealerHand.calculateValue(true);
    }

    public int getLastUserDelta() {
        return lastUserDelta;
    }

    public boolean isRoundOver() {
        return roundOver;
    }

    public int getCurrentBet() {
        return betAmount;
    }
}

