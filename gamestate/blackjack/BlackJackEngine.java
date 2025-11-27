package gamestate.blackjack;
import java.util.Random;

public class BlackJackEngine {
    //Logic for 1 human vs 2 ai, 1 dealer

    //Stores player state
    private static class PlayerState{
        int handValue = 0;
        boolean stands = false;
        int balance = 0;
        int bet = 0;
    }

    private final Random rand = new Random();

    // Players
    private final PlayerState user = new PlayerState();
    private final PlayerState player1 = new PlayerState();
    private final PlayerState player2 = new PlayerState();

    private int dealerHandValue = 0;
    private boolean dealerStands = false;
    private boolean roundOver = false;

    private int lastUserNumber = 0;

    /**Everyone has the same starting balance **/
    public BlackJackEngine(int startingBalance){
        user.balance = startingBalance;
        player1.balance = startingBalance;
        player2.balance = startingBalance;

    }

    // --------- round duration ------------
    /**
     * Start new round
     * For now: user chooses bet then AI bets are auto
     */

    public void startNewRound(int userBet) {
        if (userBet <= 0) throw new IllegalArgumentException("Bet must be positive");
        if (userBet > user.balance) throw new IllegalArgumentException("Bet cannot exceed user balance");

        // you can later make AI bets different if you want
        if (userBet > player1.balance || userBet > player2.balance) {
            throw new IllegalArgumentException("One of the AI players cannot afford this bet");
        }

        //reset the flags
        roundOver = false;
        dealerStands = false;
        lastUserNumber = 0;

        resetPlayer(user, userBet);
        resetPlayer(player1, userBet);
        resetPlayer(player2, userBet);

        dealerHandValue = drawCard() + drawCard();

        // initial deal
        user.handValue = drawCard() + drawCard();
        player1.handValue  = drawCard() + drawCard();
        player2.handValue  = drawCard() + drawCard();

        // basic initial blackjack check (only for user vs dealer for now)
        checkUserBlackjackOnDeal();

    }

    private void resetPlayer(PlayerState p, int bet){
        p.handValue = 0;
        p.stands = false;
        p.bet = bet;
    }

    /**
     * Human Action
     */

    //User hits. Returns hand total
    public int userHit(){
        if (roundOver || user.stands) return user.handValue;

        user.handValue += drawCard();

        //dealer "auto plays
        if (!dealerStands && dealerHandValue < 17) {
            dealerHandValue += drawCard();
            if (dealerHandValue >= 17) {
                dealerStands = true;
            }
        }

        checkUserBust();
        return user.handValue;

        /**
         * User stands, AIs and dealer finish drawing
         */

        public void userStand(){
            if (roundOver) {
                return;
            }

            user.stands = true;

            // Players take their turns
            runPlayer1Turn(Player1);
            runPlayer2Turn(Player2);

            // Dealer turn: hit on <= 16
            while (dealerHandValue <= 16) {
                dealerHandValue += drawCard();
            }
            dealerStands = true;

            // settle all three vs dealer
            settleRound();
        }

    }
}

