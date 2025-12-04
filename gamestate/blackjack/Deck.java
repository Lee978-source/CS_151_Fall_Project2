package gamestate.blackjack;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class Deck {
    private final List<Card> cards;

    public Deck() {
        cards = new ArrayList();
        initializeDeck();
        shuffle();
    }

    public void initializeDeck() {
        cards.clear(); // clear any cards from previous rounds
        // iterate all possible suits
        for (Card.Suit suit : Card.Suit.values()) {
            // iterate all possible ranks
            for (Card.Rank rank : Card.Rank.values()) {
                cards.add(new Card(suit, rank));
            }
        }
    }

    // shuffle deck
    public void shuffle() {
        Collections.shuffle(cards);
    }

    public Card drawCard() {
        if (cards.isEmpty()) {
            return null;
        }
        return cards.remove(cards.size() - 1); // remove card being drawn from existing deck
    }
}
