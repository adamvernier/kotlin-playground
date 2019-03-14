/**
 * Cribbage.kt
 * Cribbage hand counter.
 * @author Adam Vernier
 */
import cards.Card
import cards.Deck
import cards.Rank
import cards.Suit
import util.itertools.combinations

interface Rule {
    fun check(cards : List<Card>) : RuleResult
}

data class RuleResult(val points: Int, val description : String)

object CountPairs : Rule {
    override fun check(cards : List<Card>): RuleResult {
        //2 of same rank = 2 points
        //3 of same rank = 6 points
        //4 of same rank = 12 points

        var totalPoints = 0
        val resultString = StringBuilder()
        resultString.append("pairs: ")

        for (r in Rank.values()) {
           var numFound = 0;
           for (c in cards) {
               if (r == c.rank) {
                   numFound++
               }
           }
            when (numFound) {
                2 -> {
                    totalPoints += 2
                    resultString.append("two ${r} ")
                }
                3 -> {
                    totalPoints += 6
                    resultString.append("three ${r} ")
                }
                4 -> {
                    totalPoints += 12
                    resultString.append("four ${r} ")
                }
            }
        }

        return RuleResult(totalPoints, resultString.toString())

    }
}

object Count15s : Rule {
    override fun check(cards : List<Card>): RuleResult {

        var num15s = 0;
        val description = StringBuilder()
        description.append("15s: ")

        for (i in 1..cards.size) {
            for (j in cards.combinations(i)) {
                val comboTotal = j.sumBy { it.rank.value }
                if (comboTotal == 15) {
                    num15s++
                    description.append(" $j ")
                }
            }

        }

        return RuleResult(2 * num15s, "${description.toString()}")
    }
}

object CountRuns : Rule {
    override fun check(cards : List<Card>): RuleResult {

        val runs = mutableListOf<List<Card>>()
        val description = StringBuilder()
        for (i in cards.size.downTo(3)) {
            for (seq in cards.combinations(i)) {

                val possibleRun = seq.toList().sortedBy { it.rank.ordinal }
                if (isRun(possibleRun)) {
                    if (runs.isEmpty()) {
                        runs.add(possibleRun)
                    } else {
                        var runAlreadyCounted = false
                        for (addedRun in runs) {
                            runAlreadyCounted = addedRun.containsAll(possibleRun)
                            if (runAlreadyCounted) {
                                break
                            }
                        }
                        if (!runAlreadyCounted) {
                            runs.add(possibleRun)
                        }
                    }
                }
            }
        }

        var points = 0
        description.append("runs: ")
        for (run in runs) {
            description.append("${run} ")
            points += run.size
        }

        return RuleResult(points, "${description.toString()}")
    }
}

object CheckFlush : Rule {
    override fun check(cards : List<Card>): RuleResult {

        var hasFourFlush = false;
        var hasFiveFlush = false;
        val description = StringBuilder()
        description.append("flush: ")

        var flushSuit : Suit? = null

        for (s in Suit.values()) {
            var numFound = 0
            for (c in cards) {
                if (s == c.suit) {
                    numFound++
                }
            }

            if (numFound == 4) {
                hasFourFlush = true
                flushSuit = s
                break
            }

            if (numFound == 5) {
                hasFiveFlush = true
                flushSuit = s
                break
            }
        }

        var points = 0
        if (hasFiveFlush) {
            points = 5
            description.append("five card ${flushSuit}")
        } else if (hasFourFlush) {
            points = 4
            description.append("four card ${flushSuit}")
        }

        return RuleResult(points, "${description.toString()}")
    }
}


fun isRun(possibleRun : List<Card>) : Boolean {
    var ret = true
    var index = 0
    while (ret && index < possibleRun.size - 1) {
        ret = possibleRun[index].rank.ordinal == possibleRun[index + 1].rank.ordinal - 1
        index++
    }
    return ret
}

val rules : List<Rule> = listOf(CountPairs, Count15s, CountRuns, CheckFlush)

fun checkRules(cards : List<Card>) : RuleResult {
    var score = 0
    var reason = StringBuilder()
    for (rule in rules) {
        with (rule.check(cards)) {
            score += points
            reason.appendln(description)
        }
    }
    return RuleResult(score, reason.toString())
}

fun main(args: Array<String>) {
    while (true) {
        val cribDeck = Deck()
        //println(cribDeck.printDeck())
        //println(cribDeck.getCardCount())
        var hand = cribDeck.deal(6)
        println("hand=${hand}")
        val start = System.currentTimeMillis()
        println("Enter points: ")


        var userPoints : Int? = null

        while (userPoints == null) {
            try {
                val userPointsStr = readLine()
                userPoints = userPointsStr!!.toInt()
            } catch (nfx: NumberFormatException) {
                println("not a number")
            }
        }

        val end = System.currentTimeMillis()

        val result = checkRules(hand)
        println("score=${result.points} \ndetails:\n${result.description}")

        val seconds = (end - start)/1000

        val deviation = Math.abs(result.points - userPoints)

        println("You took $seconds seconds, deviation = $deviation")

    }
}