package org.some.codeadvent.kotlin

import java.nio.file.Files
import java.nio.file.Path

private const val BLUE = "blue"
private const val GREEN = "green"
private const val RED = "red"

private const val TOTAL_RED = 12
private const val TOTAL_GREEN = 13
private const val TOTAL_BLUE = 14


fun main() {
    val filename = Path.of("data/day02-input.txt")
    val lines = Files.lines(filename).toList()

    val gameRecords = lines.map { parseGameRecordSplit(it) }

    val sumOfValidGameIds = gameRecords
        .filter { isValidGame(it) }
        .sumOf { println(it); it.number }
    println(sumOfValidGameIds)

    val sumOfPowers = gameRecords
        .map { minimalPossibleBag(it) }
        .sumOf { it.red * it.green * it.blue }
    println(sumOfPowers)
}

fun minimalPossibleBag(game: GameRecord): Handful {
    var minimalRed = 0
    var minimalGreen = 0
    var minimalBlue = 0

    game.grabs.forEach {
        if (it.red > minimalRed) {
            minimalRed = it.red
        }
        if (it.green > minimalGreen) {
            minimalGreen = it.green
        }
        if (it.blue > minimalBlue) {
            minimalBlue = it.blue
        }
    }
    return Handful(red = minimalRed, green = minimalGreen, blue = minimalBlue)
}

fun parseGameRecordSplit(line: String): GameRecord {
    val (game, handfulsString) = line.split(": ")
    val handfuls = handfulsString.split("; ")
    
    val gameNumber = game.substring("Game ".length).toInt()
    val handfulsList = handfuls.map { str ->
        val cubes = str.split(", ")
        val map = mutableMapOf<String, Int>()
        cubes.forEach {
            val key = when  {
                it.endsWith(BLUE) -> BLUE
                it.endsWith(GREEN) -> GREEN
                it.endsWith(RED) -> RED
                else -> throw IllegalArgumentException("Unknown color '$it'")
            }
            map[key] = it.substringBefore(" ").toInt()
        }
        Handful(
            red = map.getOrDefault(RED, 0),
            green = map.getOrDefault(GREEN, 0),
            blue = map.getOrDefault(BLUE, 0))
    }
    return GameRecord(gameNumber, handfulsList)
}

fun isValidGame(game: GameRecord): Boolean = game.grabs.all(::isValidGrab)

fun isValidGrab(handful: Handful) = handful.red <= TOTAL_RED && handful.green <= TOTAL_GREEN && handful.blue <= TOTAL_BLUE

fun parseGameRecordRegex(line: String) {
    val (game, handfulsString) = line.split(":")
    val gameRegex = Regex("Game (?<num>\\d+)")
    val gameMatch = gameRegex.find(game)
    val gameNumber = gameMatch?.groups?.get("num")?.value!!.toInt()

    val handfuls = handfulsString.split("; ")
    val handfulRegex = Regex("((?<red>\\d+ red)|(?<green>\\d+ green)|(?<blue>\\d+ blue))")
    handfuls.forEach {
        val handfulMatch = handfulRegex.find(it)
        val groups = handfulMatch?.groups
        println(groups)
    }
}

fun regexGameRecord(line: String) {
    val regex = Regex("Game (\\d+): ((\\d+ \\w+)(, (\\d+ \\w+))*)+")
    val matchResult = regex.find(line)
    val groupValues = matchResult?.groupValues
    println(groupValues)
}

data class GameRecord(val number: Int, val grabs: List<Handful>)

data class Handful(val red: Int = 0, val green: Int = 0, val blue: Int = 0)