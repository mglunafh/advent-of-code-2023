package org.some.codeadvent.kotlin

object Day02 : Challenge {
    override val day = 2

    data class GameRecord(val number: Int, val grabs: List<Handful>)

    data class Handful(val red: Int = 0, val green: Int = 0, val blue: Int = 0)

    private val lines = loadResourceLines("data/day02-input.txt", "file with the 'Cube Conundrum' could not be loaded")
    private val gameRecords = lines.map { parseGameRecordSplit(it) }

    override fun runSimple() {
        val sumOfValidGameIds = gameRecords
            .filter { isValidGame(it) }
            .sumOf { println(it); it.number }
        println("Sum of IDs of the valid games: $sumOfValidGameIds")
    }

    override fun runHard() {
        val sumOfPowers = gameRecords
            .map { minimalPossibleBag(it) }
            .sumOf { it.red * it.green * it.blue }
        println("Sum of powers of possible bags: $sumOfPowers")
    }

    private fun minimalPossibleBag(game: GameRecord): Handful {
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

    private const val BLUE = "blue"
    private const val GREEN = "green"
    private const val RED = "red"

    private const val TOTAL_RED = 12
    private const val TOTAL_GREEN = 13
    private const val TOTAL_BLUE = 14

    private fun isValidGame(game: GameRecord): Boolean = game.grabs.all(::isValidGrab)

    private fun isValidGrab(handful: Handful) = handful.red <= TOTAL_RED && handful.green <= TOTAL_GREEN && handful.blue <= TOTAL_BLUE

    private fun parseGameRecordSplit(line: String): GameRecord {
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
}
