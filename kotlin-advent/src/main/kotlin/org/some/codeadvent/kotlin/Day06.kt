package org.some.codeadvent.kotlin

import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.roundToLong
import kotlin.math.sqrt

object Day06 : Challenge {
    override val day = 6

    private val timesString: String
    private val distancesString: String
    init {
        val lines = loadResourceLines("data/day06-input.txt", "Could not load racing data")
        timesString = lines[0]
        distancesString = lines[1]
    }

    override fun runSimple() {
        val times = timesString.substringAfter(" ").trim().split("\\s+".toRegex()).map { it.toInt() }
        val distances = distancesString.substringAfter(" ").trim().split("\\s+".toRegex()).map { it.toInt() }

        require(times.size == distances.size) {
            "For some reason arrays for time and distance do not match: " +
                    "'times' has ${times.size} elements, 'distances' has ${distances.size} elements"
        }

        val scoreboard = times.zip(distances)
        println(scoreboard)
        val numberOfWaysToBeatRecords = scoreboard
            .map { marginOfErrorNaive(it.first, it.second) }
            .reduce { acc, number -> acc * number }
        println("Number of ways to beat race: $numberOfWaysToBeatRecords")
    }

    override fun runHard() {
        val timeKerninged = timesString.substringAfter(" ").replace(" ", "").toLong()
        val distanceKerninged = distancesString.substringAfter(" ").replace(" ", "").toLong()
        println("Big number of possibilities: ${marginOfError(timeKerninged, distanceKerninged)}")
    }

    private fun marginOfErrorNaive(time: Int, recordDistance: Int): Int {
        var acc = if (time % 2 == 0) 1 else 0
        var curr = (time - 1) / 2
        while (curr * (time - curr) > recordDistance) {
            acc += 2
            curr--
        }
        return acc
    }

    private fun marginOfError(time: Long, recordDistance: Long): Long {
        val discriminant = time * time - 4 * recordDistance
        require(discriminant >= 0) {
            "Discriminant for time=$time, distance=$recordDistance is negative, solution does not exist"
        }
        val lesserRoot = (time - sqrt(discriminant.toDouble())) / 2
        val biggerRoot = (time + sqrt(discriminant.toDouble())) / 2
        return (floor(biggerRoot) - ceil(lesserRoot) + 1).roundToLong()
    }
}
