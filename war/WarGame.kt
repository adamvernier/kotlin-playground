/**
 * WarGame.kt
 * A war card game simulator.
 * @author Adam Vernier
 */

//number of cards to include facedown in a war
const val NUM_FACEDOWN = 3

const val LOG_BATTLES = false

enum class Suit(val short : String) {
    HEART ("H"),
    SPADE ("S"),
    CLUB ("C"),
    DIAMOND ("D")
}

enum class Rank(val short: String) {
    TWO ("2"),
    THREE ("3"),
    FOUR ("4"),
    FIVE ("5"),
    SIX ("6"),
    SEVEN ("7"),
    EIGHT ("8"),
    NINE ("9"),
    TEN ("10"),
    JACK ("J"),
    QUEEN ("Q"),
    KING ("K"),
    ACE ("A")
}

data class Card(val rank : Rank, val suit : Suit) : Comparable<Card> {

    override fun toString() : String {
        return "${rank.short}${suit.short}"
    }

    override fun compareTo(other: Card): Int {
        if (other == null) return 1
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

/**
 * Battle. This method will run a battle, wars will recursively call another battle.
 * @param first Player1 deck
 * @param second Player2 deck
 * @param winnings Card accumulator for war winnings
 * @return Possible string identifying the game winner
 *
 */
fun battle(first : Deck, second : Deck, winnings : ArrayList<Card> = ArrayList()) : String? {
    val firstCard = first.deal()
    val secondCard = second.deal()

    if (firstCard == null) {
        return "Player 2 wins!"
    }

    if (secondCard == null) {
        return "Player 1 wins!"
    }

    if (firstCard != null && secondCard != null) {
        if (LOG_BATTLES) println("${firstCard} vs ${secondCard}")

        when (firstCard.compareTo(secondCard)) {
            1 -> {
                if (LOG_BATTLES) println("first wins the battle")
                winnings.add(firstCard)
                winnings.add(secondCard)
                handleWinnings("First", first, winnings)
            }
            -1 -> {
                if (LOG_BATTLES) println("second wins the battle")
                winnings.add(firstCard)
                winnings.add(secondCard)
                handleWinnings("Second", second, winnings)
            }
            else -> {
                if (winnings.isEmpty()) {
                    println("war!  (${firstCard} vs ${secondCard})")
                } else {
                    println("multi war!!  (${firstCard} vs ${secondCard})")
                }
                winnings.add(firstCard)
                winnings.add(secondCard)

                for (i in 1..NUM_FACEDOWN) {
                    val firstFaceDownCard = first.deal()
                    if (firstFaceDownCard == null) {
                        return "Player 2 wins!"
                    } else {
                        winnings.add(firstFaceDownCard)
                    }

                    val secondFaceDownCard = second.deal()
                    if (secondFaceDownCard == null) {
                        return "Player 1 wins!"
                    } else {
                        winnings.add(secondFaceDownCard)
                    }
                }

                return battle(first, second, winnings)

            }
        }
    }

    //result of a regular battle (non-war) is null
    return null
}

fun handleWinnings(winnerName: String, winner : Deck, winnings : ArrayList<Card>) {
    if (winnings.size > 2) {
        println("$winnerName wins the war winnings=$winnings")
    }
    winnings.shuffle();
    winner.addToBottom(winnings)
    winnings.clear()
}

fun main(args: Array<String>) {
    val warDeck = Deck()
    println(warDeck.printDeck())
    println(warDeck.getCardCount())
    val (first, second) = warDeck.split()

    println("first= [${first.getCardCount()}]    ${first.printDeck()}")
    println("second= [${second.getCardCount()}]    ${second.printDeck()}")

    var round = 1

    while (true) {
        println("Round $round : ")
        println("first= [${first.getCardCount()}]    ${first.printDeck()}")
        println("second= [${second.getCardCount()}]    ${second.printDeck()}")
        val result  = battle(first, second)
        if (result != null) {
            println(result)
            break
        }
        round++
    }

    println("first= [${first.getCardCount()}]    ${first.printDeck()}")
    println("second= [${second.getCardCount()}]    ${second.printDeck()}")

}