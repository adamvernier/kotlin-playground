/**
 * Cards.kt
 * Various classes and methods for working with a deck of cards.
 * @author Adam Vernier
 */
package cards

enum class Suit(val short : String) {
    HEART ("H"),
    SPADE ("S"),
    CLUB ("C"),
    DIAMOND ("D")
}

enum class Rank(val short: String, val value: Int) {
    ACE ("A", 1),
    TWO ("2", 2),
    THREE ("3", 3),
    FOUR ("4", 4),
    FIVE ("5", 5),
    SIX ("6", 6),
    SEVEN ("7",7),
    EIGHT ("8", 8),
    NINE ("9",9),
    TEN ("10", 10),
    JACK ("J", 10),
    QUEEN ("Q", 10),
    KING ("K", 10)

}

data class Card(val rank : Rank, val suit : Suit) : Comparable<Card> {

    override fun toString() : String {
        return "${rank.short}${suit.short}"
    }

    override fun compareTo(other: Card): Int {
        if (rank.ordinal > other.rank.ordinal) {
            return 1
        }
        if (rank.ordinal == other.rank.ordinal) {
            return 0
        }
        return -1
    }
}

/**
 * Class Deck
 * @param givenCards Optional list of cards to use. The list should already be shuffled. If no cards are given a new
 * deck will be created.
 */
class Deck(givenCards: List<Card>? = null) {

    /**
     * The card pile : fist cards in the collection are the top of the deck.
     */
    var cards : ArrayList<Card>

    init {
        //If no cards are given make a new deck
        if (givenCards == null) {
            cards = mutableListOf<Card>() as ArrayList<Card>
            makeDeck()
            shuffle()
        } else {
            cards = givenCards as ArrayList<Card>
        }
    }

    fun makeDeck() {
        for (suit in Suit.values()) {
            for (rank in Rank.values()) {
                cards.add(Card(rank, suit))
            }
        }
    }

    fun shuffle() {
        cards.shuffle()
    }

    fun printDeck() : String {
        return cards.joinToString()
    }

    fun getCardCount() : Int{
        return(cards.size)
    }

    fun deal(): Card? {
        return deal(cards)
    }

    fun deal(cardNum: Int): List<Card> {
        return deal(cards, cardNum)
    }

    fun split() : Pair<Deck, Deck> {
        val deck1  = Deck(deal(cards.size/2))
        return Pair(deck1, this)
    }

    fun addToBottom(vararg newCards: Card) {
        cards.addAll(newCards)
    }

    fun addToBottom(newCards : List<Card>) {
        cards.addAll(newCards)
    }
}

/**
 * Deal a single card. Null return means no cards left.
 */
fun deal(cards : ArrayList<Card>) : Card? {
    if (cards.isEmpty()) {
        return null
    }
    return deal(cards,1)[0]
}

/**
 * Deal a specific number of cards.
 * @param cardNum Number of cards to deal
 * @return List<Card> or empty list if the deck is empty. May return less than the requested number
 * of cards if there are not enough in the deck.
 */
fun deal(cards : ArrayList<Card>, cardNum : Int) : List<Card> {
    if (cards.isEmpty()) {
        return ArrayList()
    }

    var cardsToRemove = cardNum

    if (cardNum > cards.size) {
        cardsToRemove = cards.size
    }

    val ret = cards.take(cardsToRemove).toList()
    cards.removeAll(ret)

    return ret
}