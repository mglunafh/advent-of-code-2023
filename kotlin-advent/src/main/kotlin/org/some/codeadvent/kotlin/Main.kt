package org.some.codeadvent.kotlin

import java.io.InputStreamReader

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        println("Empty list of arguments.")
        println("Please enter the day from 1 to 25.")
        return
    }
    if (args.size > 2) {
        println("Too many arguments.")
        println("The 1st argument -- challenge number, the 2nd optional argument -- '-h' / '--hard' ")
        return
    }

    val day = args[0].toIntOrNull()
    if (day == null) {
        println("Could not understand the day number from '${args[0]}'.")
        return
    }
    val hard = args.size == 2 && args[1] in listOf("-h", "--hard")

    println("Day $day, ${if (hard) "difficult" else "simple"}")

    // everything gets initialized
    val challenges = listOf(Day01, Day02, Day03, Day04, Day05, Day06, Day07, Day08, Day09).associateBy { it.day }
    val challenge = challenges[day]
    challenge?.run {
        if (hard) runHard() else runSimple()
    }
}

interface Challenge {
    val day: Int
    fun runSimple()
    fun runHard()

    fun loadResourceAsString(resourceName: String, errorMsg: String): String {
        return javaClass.classLoader.getResourceAsStream(resourceName)
            ?.let { istream -> istream.use { InputStreamReader(it, Charsets.UTF_8).use { utf8stream -> utf8stream.readText() } } }
            ?: throw IllegalStateException(errorMsg)
    }

    fun loadResourceLines(resourceName: String, errorMsg: String): List<String> {
        return javaClass.classLoader.getResourceAsStream(resourceName)
            ?.let { istream -> istream.use { InputStreamReader(it, Charsets.UTF_8).use { utf8stream -> utf8stream.readLines() } } }
            ?: throw IllegalStateException(errorMsg)
    }
}
