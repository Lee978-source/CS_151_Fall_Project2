package gamestate.blackjack;

import java.util.List;
import java.util.ArrayList;

public class Hand {
    private final List<Card> cards;
    private int value; //

    // constructor for empty hand
    public Hand() {
        this.cards = new ArrayList<>();
        this.value = 0;
    }

    // add card into hand
    public void addCard(Card card) {
        this.cards.add(card);
    }

    // calculate hand value using BJ rules
    public int calculateValue(boolean dealerCard) {
        int handValue = 0;
        int aceCount = 0;
        int startIndex;

        if(dealerCard) {
            startIndex = 1;    // one of the dealer cards will be hidden, so we count all other visible cards
        }
        else {
            startIndex = 0; // everyone but dealer will start with 0 index
        }
        // counts the current hand value
        for (int i = startIndex; i < cards.size(); i++) {
            Card card = cards.get(i);
            handValue += card.getNumValue();
            if (card.getRank() == Card.Rank.ACE) {
                aceCount++; // if ace is drawn, increase aceCount
            }
        }
        while (handValue > 21 && aceCount > 0) {
            handValue -= 10; // converts the ace to a 1 if the ace included makes the hand value over 21
            aceCount--;  // in case another ace is drawn
        }
        return handValue;
    }

    // keep for now so program works
    public int calculateValue() {
        return calculateValue(false);
    }

    public List<Card> getCards() {
        return cards;
    }

    // clears the hand
    public void clear() {
        this.cards.clear();
    }
    // can add more to this later
    @Override
    public String toString() {
        if (cards.isEmpty()) {
            return "Empty Hand";
        }
        return cards.toString();
    }
}
