package gamestate.blackjack;

public class Card {
    private final Suit suit;
    private final Rank rank;

    public Card(Suit suit, Rank rank) {
        this.suit = suit;
        this.rank = rank;
    }

    public Suit getSuit() {
        return suit;
    }

    public Rank getRank() {
        return rank;
    }

    // all card suits in a deck (4 total)
    public enum Suit {
        SPADES,
        HEARTS,
        DIAMONDS,
        CLUBS,
    }
    // all the card ranks in a deck (13 total)
    public enum Rank {
        ACE(11), TWO(2), THREE(3), FOUR(4), FIVE(5),
        SIX(6), SEVEN(7), EIGHT(8), NINE(9), TEN(10), KING(10),
        JACK(10), QUEEN(10);

        private final int value;

        Rank(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    @Override
    public String toString() {
        return rank + " of " + suit;
    }

}
