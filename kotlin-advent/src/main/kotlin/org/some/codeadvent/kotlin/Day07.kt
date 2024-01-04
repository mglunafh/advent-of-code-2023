package org.some.codeadvent.kotlin

import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Collectors

fun main() {
    val filename = Path.of("data/day07-test.txt")
    val lines = Files.lines(filename).toList()

    val draws = lines
        .map { it.split(" ") }
        .map { Bet(it[0], it[1].toInt()) }

    val totalWinnings = draws.sorted()
        .foldIndexed(0L) { ind, acc, bet -> acc + (ind + 1) * bet.bid }
    println("Total winnings in Camel Cards: $totalWinnings")

    val jokerDraws = lines
        .map { it.split(" ") }
        .map { JokerBet(it[0], it[1].toInt()) }

    val totalJokerWinnings = jokerDraws.sorted()
        .foldIndexed(0L) { ind, acc, bet ->
            val msg = String.format("%20s: %s","(${bet.type.toString().lowercase()}) ${bet.hand}", "$acc + ${ind + 1} * ${bet.bid}")
            println(msg)
            acc + (ind + 1) * bet.bid }
    println("Total winnings in Camel Cards (Joker): $totalJokerWinnings")
}

data class Bet(val hand: String, val bid: Int) : Comparable<Bet> {

    val type: HandType

    init {
        require(hand.length == 5) {
            "Wrong number of cards in the hard, should be five, got ${hand.length} instead"
        }
        require(bid > 0) {
            "Bid should be positive, got '$bid' instead"
        }
        type = determineType(hand)
    }

    override fun compareTo(other: Bet): Int {
        if (type != other.type) {
            return type.compareTo(other.type)
        }
        for (i in 0 until 5) {
            if (hand[i] != other.hand[i]) {
                return strength(hand[i]) - strength(other.hand[i])
            }
        }
        return 0
    }

    override fun toString(): String {
        return "Bet(hand=$hand, bid=$bid, type=$type)"
    }

    companion object {
        private fun determineType(hand: String): HandType {
            val amounts = hand.chars()
                .mapToObj { it }
                .collect(Collectors.groupingBy({ it.toChar() }, Collectors.counting()))
                .map { it.value.toInt() }

            return when {
                amounts.size == 5 -> HandType.HIGH_CARD
                amounts.contains(5) -> HandType.FIVE
                amounts.contains(4) -> HandType.FOUR
                amounts.contains(3) -> if (amounts.contains(2)) HandType.FULL_HOUSE else HandType.TRIPLE
                amounts.contains(2) && (amounts.size == 3) -> HandType.TWO_PAIRS
                amounts.contains(2) && (amounts.size == 4) -> HandType.PAIR
                else -> throw IllegalArgumentException("Don't know how to classify this hand: '$hand'")
            }
        }
    }
}

data class JokerBet(val hand: String, val bid: Int) : Comparable<JokerBet> {

    val type: HandType

    init {
        require(hand.length == 5) {
            "Wrong number of cards in the hard, should be five, got ${hand.length} instead"
        }
        require(bid > 0) {
            "Bid should be positive, got '$bid' instead"
        }
        type = determineType(hand)
    }

    override fun compareTo(other: JokerBet): Int {
        if (type != other.type) {
            return type.compareTo(other.type)
        }
        for (i in 0 until 5) {
            if (hand[i] != other.hand[i]) {
                return jokerStrength(hand[i]) - strength(other.hand[i])
            }
        }
        return 0
    }

    override fun toString(): String {
        return "JokerBet(hand=$hand, bid=$bid, type=$type)"
    }

    companion object {

        private const val JOKER = 'J'

        private fun determineType(hand: String): HandType {
            val cardsToAmounts = hand.chars()
                .mapToObj { it }
                .collect(Collectors.groupingBy({ it.toChar() }, Collectors.counting()))

            val jokers = cardsToAmounts.getOrDefault(JOKER, 0).toInt()
            val mostNonJokers = cardsToAmounts.filter { it.key != JOKER }.maxByOrNull { it.value }

            val adjustedCardsToAmounts = if (mostNonJokers == null) {
                mapOf(JOKER to 5)
            } else {
                mutableMapOf<Char, Int>().apply {
                    for ((key, value) in cardsToAmounts) {
                        if (key == JOKER) {
                            continue
                        } else if (key == mostNonJokers.key) {
                            put(key, value.toInt() + jokers)
                        } else {
                            put(key, value.toInt())
                        }
                    }
                }.toMap()
            }
            val amounts = adjustedCardsToAmounts.map { it.value }

            return when {
                amounts.size == 5 -> HandType.HIGH_CARD
                amounts.contains(5) -> HandType.FIVE
                amounts.contains(4) -> HandType.FOUR
                amounts.contains(3) -> if (amounts.contains(2)) HandType.FULL_HOUSE else HandType.TRIPLE
                amounts.contains(2) && (amounts.size == 3) -> HandType.TWO_PAIRS
                amounts.contains(2) && (amounts.size == 4) -> HandType.PAIR
                else -> throw IllegalArgumentException("Don't know how to classify this hand: '$hand'")
            }
        }
    }
}

enum class HandType(val value: Int) {
    HIGH_CARD(1), PAIR(2), TWO_PAIRS(3), TRIPLE(4), FULL_HOUSE(5), FOUR(6), FIVE(7)
}

fun strength(card: Char): Int {
    return when {
        card.isDigit() -> card.digitToInt()
        card == 'T' -> 10
        card == 'J' -> 11
        card == 'Q' -> 12
        card == 'K' -> 13
        card == 'A' -> 14
        else -> throw IllegalArgumentException("Symbol '$card' does not represent a card")
    }
}

fun jokerStrength(card: Char): Int {
    return when {
        card.isDigit() -> card.digitToInt()
        card == 'J' -> -1
        card == 'T' -> 10
        card == 'Q' -> 12
        card == 'K' -> 13
        card == 'A' -> 14
        else -> throw IllegalArgumentException("Symbol '$card' does not represent a card")
    }
}
