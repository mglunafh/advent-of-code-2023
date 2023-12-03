package org.some.codeadvent.kotlin

import java.io.InputStreamReader

fun main() {

    val calibrationFile = "day01-input.txt"
    val resourceAsStream = object {}.javaClass.classLoader.getResourceAsStream(calibrationFile)
    val calibrationValues = resourceAsStream?.let { inputStream ->
        inputStream.use { InputStreamReader(it, Charsets.UTF_8).use { utf8stream -> utf8stream.readLines() } }
    } ?: throw IllegalStateException("file with the calibration values could not be loaded")

    val sum = calibrationValues
        .map(::recoverDigits)
        .sum()
    println("Sum: $sum")

    val funnySum = calibrationValues
        .map(::recoverFunnyDigits)
        .sum()
    println("Sum: $funnySum")
}

fun recoverDigits(line: String): Int {
    val firstDigit = line.first { it.isDigit() }.digitToInt()
    val lastDigit = line.last { it.isDigit() }.digitToInt()
    return 10 * firstDigit + lastDigit
}

val digits = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9",
    "one", "two", "three", "four", "five", "six", "seven", "eight", "nine")

fun recoverFunnyDigits(line: String): Int {
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

fun asNumber(token: String): Int = when (token) {
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
