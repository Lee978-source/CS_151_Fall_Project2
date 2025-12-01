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
}
