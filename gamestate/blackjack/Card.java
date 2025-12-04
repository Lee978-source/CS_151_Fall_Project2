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
        SPADES("S"),
        HEARTS("H"),
        DIAMONDS("D"),
        CLUBS("C");

        private final String letter;

        Suit(String letter) {
            this.letter = letter;
        }

        public String getLetter() {
            return letter;
        }
    }
    // all the card ranks in a deck (13 total)
    public enum Rank {
        ACE(11,"A"), TWO(2,"2"), THREE(3,"3"), FOUR(4,"4"), FIVE(5,"5"),
        SIX(6,"6"), SEVEN(7,"7"), EIGHT(8,"8"), NINE(9,"9"), TEN(10,"10"), KING(10,"10"),
        JACK(10,"10"), QUEEN(10,"10");

        private final int value;
        private final String symbol;

        Rank(int value, String symbol) {
            this.value = value;
            this.symbol = symbol;
        }

        public int getValue() {
            return value;
        }

        public String getSymbol() {
            return symbol;
        }
    }

    // gets the name of the png names, ex. 2C = 2 of clubs
    public String getFileName() {
        return this.rank.getSymbol() + this.suit.getLetter() + ".png";
    }

    public int getNumValue() {
        return rank.getValue();
    }

    @Override
    public String toString() {
        return rank + " of " + suit;
    }

}
