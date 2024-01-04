package org.some.codeadvent.kotlin

import java.nio.file.Files
import java.nio.file.Path

fun main() {
    val filename = Path.of("data/day07-input.txt")
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
        .foldIndexed(0L) { ind, acc, bet -> acc + (ind + 1) * bet.bid }
    println("Total winnings in Camel Cards (Joker): $totalJokerWinnings")
}

data class Bet(val hand: String, val bid: Int) : Comparable<Bet> {

    val type: HandType

    init {
        require(hand.length == 5) {
            "Wrong number of cards in the hand, should be five, got ${hand.length} instead"
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

        private fun determineType(hand: String): HandType {
            val amounts = hand.toCharArray()
                .groupBy { it }
                .map { it.value.size }

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
            "Wrong number of cards in the hand, should be five, got ${hand.length} instead"
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
                return jokerStrength(hand[i]) - jokerStrength(other.hand[i])
            }
        }
        return 0
    }

    override fun toString(): String {
        return "JokerBet(hand=$hand, bid=$bid, type=$type)"
    }

    companion object {

        private const val JOKER = 'J'

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

        private fun determineType(hand: String): HandType {
            val cardsToAmounts = hand.toCharArray()
                .groupBy { it }
                .mapValues { it.value.size }

            val jokers = cardsToAmounts.getOrDefault(JOKER, 0)
            val mostNonJokers = cardsToAmounts.filter { it.key != JOKER }.maxByOrNull { it.value }

            val adjustedCardsToAmounts = when {
                jokers == 0 -> cardsToAmounts.mapValues { it.value }
                mostNonJokers == null -> mapOf(JOKER to 5)
                else -> mutableMapOf<Char, Int>().apply {
                    for ((key, value) in cardsToAmounts) {
                        if (key == JOKER) {
                            continue
                        } else if (key == mostNonJokers.key) {
                            put(key, value + jokers)
                        } else {
                            put(key, value)
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

enum class HandType {
    HIGH_CARD, PAIR, TWO_PAIRS, TRIPLE, FULL_HOUSE, FOUR, FIVE
}
