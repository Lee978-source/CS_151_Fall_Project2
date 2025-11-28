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


    }

    public void userStand(){
        if (roundOver) {
            return;
        }

        user.stands = true;

        // Players take their turns
        runAiTurn(player1);
        runAiTurn(player2);

        // Dealer turn: hit on <= 17
        while (dealerHandValue <= 17) {
            dealerHandValue += drawCard();
        }
        dealerStands = true;

        // settle all three vs dealer
        settleRound();
    }

    /**
     * AI Logic
     * We want AI to hit while hand is < 16
     */
    private void runAiTurn(PlayerState ai){
        if(roundOver) return;

        while (ai.handValue < 16){
            ai.handValue += drawCard();
            if(ai.handValue > 21){
                //this is a bust
                break;
            }
        }
        ai.stands = true;
    }
    // Internal helper
    private int drawCard() {
        // 2-11 inclusive (treat 11 as Ace)
        return rand.nextInt(10) + 2;
    }

    private void checkUserBlackjackOnDeal() {
        if (roundOver) return;

        boolean userBJ   = (user.handValue == 21);
        boolean dealerBJ = (dealerHandValue == 21);

        if (userBJ || dealerBJ) {
            if (userBJ && !dealerBJ) {
                win(user);
                lastUserNumber = user.bet;
            } else if (!userBJ && dealerBJ) {
                lose(user);
                lastUserNumber = -user.bet;
            } else {
                push(user);
                lastUserNumber = 0;
            }
            roundOver = true;
        }
    }

    private void checkUserBust() {
        if (user.handValue > 21) {
            lose(user);
            lastUserNumber = -user.bet;
            roundOver = true;
        }
    }

    /**
     * After everyone is done, compare each non-dealer player vs dealer
     * and update balances.
     */
    private void settleRound() {
        if (roundOver) return;

        settleOnePlayer(user);
        settleOnePlayer(player1);
        settleOnePlayer(player2);

        roundOver = true;
    }

    private void settleOnePlayer(PlayerState p) {
        if (p.handValue > 21) {
            // bust – auto lose
            lose(p);
            if (p == user) lastUserNumber = -p.bet;
            return;
        }

        if (dealerHandValue > 21) {
            win(p);
            if (p == user) lastUserNumber = p.bet;
        } else if (p.handValue > dealerHandValue) {
            win(p);
            if (p == user) lastUserNumber = p.bet;
        } else if (p.handValue < dealerHandValue) {
            lose(p);
            if (p == user) lastUserNumber = -p.bet;
        } else {
            push(p);
            if (p == user) lastUserNumber = 0;
        }
    }

    private void win(PlayerState p) {
        p.balance += p.bet;
    }

    private void lose(PlayerState p) {
        p.balance -= p.bet;
    }

    private void push(PlayerState p) {
        // nothing changes
    }

    // ---------- getters for UI/debug ----------

    public int getUserHandValue()   {
        return user.handValue;
    }
    public int getAi1HandValue()    {
        return player1.handValue;
    }
    public int getAi2HandValue()    {
        return player2.handValue;
    }
    public int getDealerHandValue() {
        return dealerHandValue;
    }

    public int getUserBalance() {
        return user.balance;
    }
    public int getAi1Balance()  {
        return player1.balance;
    }
    public int getAi2Balance()  {
        return player2.balance;
    }

    public int getUserBet() {
        return user.bet;
    }

    public int getLastUserDelta() {
        return lastUserNumber;
    }

    public boolean isRoundOver() { return roundOver; }

}

