package gamestate.blackjack;

import java.util.LinkedHashMap;

public class GameState {

    // engine state
    public int currentPlayerIndex;
    public int betAmount;
    public boolean roundOver;
    public int lastUserDelta;

    // game objects
    public Hand dealerHand;
    public LinkedHashMap<String, PlayerState> playerStates;
    public Deck deck;

    // constructor for saving state
    public GameState(int currentPlayerIndex, int betAmount, boolean roundOver,
                     int lastUserDelta, Hand dealerHand,
                     LinkedHashMap<String, PlayerState> playerStates, Deck deck) {
        this.currentPlayerIndex = currentPlayerIndex;
        this.betAmount = betAmount;
        this.roundOver = roundOver;
        this.lastUserDelta = lastUserDelta;
        this.dealerHand = dealerHand;
        this.playerStates = playerStates;
        this.deck = deck;
    }

    // gson requires empty constructor
    public GameState() {}

    public static class PlayerState {
        public Hand hand;
        public int balance;
        public int bet;
        public boolean hasStood;

        public PlayerState(Hand hand, int balance, int bet, boolean hasStood) {
            this.hand = hand;
            this.balance = balance;
            this.bet = bet;
            this.hasStood = hasStood;
        }

        public PlayerState() {}
    }
}