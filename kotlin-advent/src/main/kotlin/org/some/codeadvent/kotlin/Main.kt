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
    }

    val day = args[0].toIntOrNull()
    val hard = args.size == 2 && args[1] in listOf("-h", "--hard")

    println("Day $day, ${if (hard) "difficult" else "simple"}")

    val challenge = when (day) {
        1 -> Day01
        2 -> Day02
        3 -> Day03
        4 -> Day04
        else -> null
    }

    if (challenge != null) {
        if (hard) challenge.runHard() else challenge.runSimple()
    }
}

interface Challenge {
    val day: Int
    fun runSimple()
    fun runHard()

    fun loadResourceLines(resourceName: String, errorMsg: String): List<String> {
        return javaClass.classLoader.getResourceAsStream(resourceName)
            ?.let { istream -> istream.use { InputStreamReader(it, Charsets.UTF_8).use { utf8stream -> utf8stream.readLines() } } }
            ?: throw IllegalStateException(errorMsg)
    }
}
