package org.some.codeadvent.kotlin

object Day04 : Challenge {
    override val day = 4

    data class ScratchCard(val id: Int, val winningNumbers: List<Int>, val numbers: List<Int>) {

        val netWorthFunc: Int
            get() = numbers.filter { winningNumbers.contains(it) }.fold(1) { acc, _ -> 2 * acc } / 2

        val matches: Int
            get() = numbers.filter { winningNumbers.contains(it) }.size
    }

    private val lines = loadResourceLines("data/day04-input.txt", "Could not load file with the scratch cards")
    private val scratchCards = lines.map(::parseCard)

    override fun runSimple() {
        val sumOfScratchCardPoints = scratchCards.sumOf { it.netWorthFunc }
        println("Sum of points from scratch cards: $sumOfScratchCardPoints")
    }

    override fun runHard() {
        var amountOfCards = 0
        val additionalCopies = mutableMapOf<Int, Int>()
        scratchCards.forEach { card ->
            val id = card.id
            val matches = card.matches
            val amountToAdd = 1 + additionalCopies.getOrDefault(id, 0)
            for (i in id + 1..id + matches) {
                additionalCopies[i] = additionalCopies.getOrDefault(i, 0) + amountToAdd
            }
            amountOfCards += amountToAdd
        }
        println(additionalCopies)
        println("Amount of scratch cards: $amountOfCards")

    }

    private fun parseCard(line: String): ScratchCard {
        val split = line.split(":", "|")

        val cardNumber = split[0].substringAfterLast(" ").toInt()
        val winningNumbers = split[1].trim()
            .split(" ")
            .filter { it.isNotBlank() }
            .map { it.toInt() }
        val numbers = split[2].trim()
            .split(" ")
            .filter { it.isNotBlank() }
            .map { it.toInt() }
        return ScratchCard(cardNumber, winningNumbers, numbers)
    }
}
