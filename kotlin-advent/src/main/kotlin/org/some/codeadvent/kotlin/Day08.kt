package org.some.codeadvent.kotlin

import java.nio.file.Files
import java.nio.file.Path

fun main() {
    val filename = Path.of("data/day08-input.txt")
    val lines = Files.lines(filename).toList()

    val network = DesertNetwork(lines)

    println("Steps: ${network.followPath()}")
    println("Ghost steps: ${network.followGhostPath()}")
}

class DesertNetwork(lines: List<String>) {

    companion object {
        const val START = "AAA"
        const val FINISH = "ZZZ"
    }

    private val instructions: String
    private val nodes: Map<String, Pair<String, String>>
    private val startingPoints: List<String>
    private val finishingPoints: List<String>

    init {
        instructions = lines[0]
        nodes = mutableMapOf<String, Pair<String, String>>().apply {
            for (line in lines.drop(2)) {
                val (key, pairString) = line.split(" = ")
                val pair = pairString.trim('(', ')').split(", ")
                put(key, Pair(pair[0], pair[1]))
            }
        }.toMap()
        startingPoints = nodes.keys.filter { it.endsWith("A") }
        finishingPoints = nodes.keys.filter { it.endsWith("Z") }
    }

    fun followGhostPath(): Long {
        val counters = MutableList(startingPoints.size) { 0 }
        for (i in counters.indices) {
            val start = startingPoints[i]
            counters[i] = followPath(start)
        }
        return leastCommonMultiple(counters)
    }

    private fun followPath(start: String): Int {
        var currPos = start
        var counter = 0
        while (!currPos.endsWith("Z")) {
            for (dir in instructions) {
                val p = nodes[currPos]!!
                currPos = when (dir) {
                    'L' -> p.first
                    'R' -> p.second
                    else -> throw IllegalArgumentException("Unknown direction '$dir' in the instruction list")
                }
                counter++
            }
        }
        return counter
    }

    fun followPath(): Int {
        var currPos = START
        var counter = 0
        while (currPos != FINISH) {
            for (dir in instructions) {
                val p = nodes[currPos]!!
                currPos = when (dir) {
                    'L' -> p.first
                    'R' -> p.second
                    else -> throw IllegalArgumentException("Unknown direction '$dir' in the instruction list")
                }
                counter++
            }
        }
        return counter
    }

    // must be buggy in case of even numbers
    private fun leastCommonMultiple(numbers: List<Int>): Long {
        val reducedNumbers = MutableList(numbers.size) { numbers[it].toLong() }
        val commonFactors = mutableListOf<Long>(1)

        val maxNumber = numbers.max()
        var temp = 1

        while (3 * temp <= maxNumber) {
            temp += 2
            if (!isPrime(temp)) {
                continue
            }
            while (reducedNumbers.all { it.rem(temp) == 0L }) {
                commonFactors.add(temp.toLong())
                for (i in reducedNumbers.indices) {
                    reducedNumbers[i] = reducedNumbers[i] / temp
                }
            }
        }

        val commonFactor = commonFactors.fold(1L) { acc, l -> acc * l }
        return reducedNumbers.fold(commonFactor) {acc, l -> acc * l}
    }

    private fun isPrime(n: Int): Boolean {
        if (n <= 1) throw IllegalArgumentException("n must be greater than 1!")
        if (n == 2) return true
        if (n % 2 == 0) return false

        var temp = 3
        while (temp * temp <= n) {
            if (n % temp == 0) {
                return false
            }
            temp += 2
        }
        return true
    }
}