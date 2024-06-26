package org.some.codeadvent.kotlin

import kotlin.math.max
import kotlin.math.min

object Day03 : Challenge {

    override val day = 3

    private val lines = loadResourceLines("data/day03-input.txt", "Could not load file with schematics")
    private val schematics = Array(lines.size) { lines[it].toCharArray() }

    data class NumberEntry(val row: Int, val start: Int, val end: Int, val value: Int)


    override fun runSimple() {
        val sum = sumOfPartNumbers()
        println("Sum of all part numbers: $sum")
    }

    override fun runHard() {
        val numberPositions = lines
            .mapIndexed { ind, list -> numberPositions(list, ind) }

        var sumOfGearRatios = 0
        for (i in schematics.indices) {
            for (j in schematics[i].indices) {
                if (schematics[i][j] != '*') {
                    continue
                }
                val numbersAround = getNumbersAround(numberPositions, i, j)
                if (numbersAround.size == 2) {
                    sumOfGearRatios += numbersAround[0] * numbersAround[1]
                }
            }
        }
        println("Sum of gear ratios: $sumOfGearRatios")
    }

    private fun getNumbersAround(numberPositions: List<List<NumberEntry>>, row: Int, column: Int): List<Int> {
        val result = mutableListOf<Int>()
        if (row > 0) {
            val upperRow = numberPositions[row - 1]
            for (entry in upperRow) {
                if (entry.end == column - 1 || entry.start == column + 1 || (entry.start <= column && column <= entry.end)) {
                    result.add(entry.value)
                }
            }
        }
        if (row < schematics.size - 1) {
            val lowerRow = numberPositions[row + 1]
            for (entry in lowerRow) {
                if (entry.end == column - 1 || entry.start == column + 1 || (entry.start <= column && column <= entry.end)) {
                    result.add(entry.value)
                }
            }
        }
        for (entry in numberPositions[row]) {
            if (entry.end == column - 1 || entry.start == column + 1) {
                result.add(entry.value)
            }
        }
        return result.toList()
    }

    private fun symbols(line: String): Set<Char> {
        return line.toCharArray()
            .filter { !it.isDigit() && it != '.'}
            .toSet()
    }

    private fun numberPositions(line: String, lineIndex: Int): List<NumberEntry> {
        val entryResult = mutableListOf<NumberEntry>()

        var atDigit = false
        var indexStart = 0
        var indexEnd = 0
        for (i in line.indices) {
            if (line.elementAt(i).isDigit()) {
                if (!atDigit) {
                    indexStart = i
                    atDigit = true
                }
                indexEnd = i
            }
            else {
                if (atDigit) {
                    val value = line.substring(indexStart, indexEnd + 1).toInt()
                    val entry = NumberEntry(lineIndex, indexStart, indexEnd, value)
                    entryResult.add(entry)
                    atDigit = false
                }
            }
        }
        if (atDigit) {
            val value = line.substring(indexStart, indexEnd + 1).toInt()
            val entry = NumberEntry(lineIndex, indexStart, indexEnd, value)
            entryResult.add(entry)
        }
        return entryResult.toList()
    }

    private fun sumOfPartNumbers(): Int {
        val partNumberSymbols = lines
            .map(::symbols)
            .flatten()
            .toSet()

        val numberPositions = lines
            .mapIndexed { ind, list -> ind to numberPositions(list, ind) }
            .filter { it.second.isNotEmpty() }
            .map { it.second }
            .flatten()

        val partNumbers = mutableListOf<Int>()

        entry@for (entry in numberPositions) {
            val upperRow = max(entry.row - 1, 0)
            val lowerRow = min(entry.row + 1, schematics.size - 1)
            val leftColumn = max(entry.start - 1, 0)
            val rightColumn = min(entry.end + 1, schematics[0].size - 1)

            if (upperRow != entry.row) {
                for (j in leftColumn..rightColumn) {
                    if (partNumberSymbols.contains(schematics[upperRow][j])) {
                        partNumbers.add(entry.value)
                        continue@entry
                    }
                }
            }
            if (lowerRow != entry.row) {
                for (j in leftColumn..rightColumn) {
                    if (partNumberSymbols.contains(schematics[lowerRow][j])) {
                        partNumbers.add(entry.value)
                        continue@entry
                    }
                }
            }
            if (leftColumn != entry.start && partNumberSymbols.contains(schematics[entry.row][leftColumn])) {
                partNumbers.add(entry.value)
                continue@entry
            }
            if (rightColumn != entry.end && partNumberSymbols.contains(schematics[entry.row][rightColumn])) {
                partNumbers.add(entry.value)
                continue@entry
            }
        }
        return partNumbers.sum()
    }
}
