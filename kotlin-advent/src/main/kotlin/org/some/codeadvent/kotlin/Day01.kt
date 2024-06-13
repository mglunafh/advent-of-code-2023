package org.some.codeadvent.kotlin

object Day01: Challenge {
    override val day = 1

    private val calibrationValues: List<String>

    init {
        calibrationValues = loadResourceLines("day01-input.txt", "File with the calibration values could not be loaded")
    }

    override fun runSimple() {
        val sum = calibrationValues
            .map(::recoverDigits)
            .sum()
        println("Sum: $sum")
    }

    override fun runHard() {
        val funnySum = calibrationValues
            .map(::recoverFunnyDigits)
            .sum()
        println("Sum: $funnySum")
    }

    private fun recoverDigits(line: String): Int {
        val firstDigit = line.first { it.isDigit() }.digitToInt()
        val lastDigit = line.last { it.isDigit() }.digitToInt()
        return 10 * firstDigit + lastDigit
    }

    private val digits = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9",
        "one", "two", "three", "four", "five", "six", "seven", "eight", "nine")

    private fun recoverFunnyDigits(line: String): Int {
        var firstDigit = 0
        outer@for (i in line.indices) {
            for (token in digits) {
                if (line.startsWith(token, i)) {
                    firstDigit = asNumber(token)
                    break@outer
                }
            }
        }
        var lastDigit =  0
        outer@for (i in line.indices.reversed()) {
            for (token in digits) {
                if (line.startsWith(token, i)) {
                    lastDigit = asNumber(token)
                    break@outer
                }
            }
        }
        return 10 * firstDigit + lastDigit
    }

    private fun asNumber(token: String): Int = when (token) {
        "1", "one"      -> 1
        "2", "two"      -> 2
        "3", "three"    -> 3
        "4", "four"     -> 4
        "5", "five"     -> 5
        "6", "six"      -> 6
        "7", "seven"    -> 7
        "8", "eight"    -> 8
        "9", "nine"     -> 9
        else -> throw IllegalArgumentException("Could not decipher a digit from token '$token'")
    }
}
