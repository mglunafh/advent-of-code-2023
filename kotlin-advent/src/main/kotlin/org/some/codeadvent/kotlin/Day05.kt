package org.some.codeadvent.kotlin

object Day05 : Challenge {

    override val day = 5

    private val almanacString = loadResourceAsString("data/day05-input.txt", "Could not load almanac")
    private val mappingsString = almanacString.split("\n\n")
    private val listOfMappings = mappingsString.drop(1).map(::parseMapping)
    private val seeds: List<Long>

    init {
        val initialCondition = mappingsString[0]
        seeds = initialCondition.substringAfter(" ").split(" ").map { it.toLong() }
    }

    override fun runSimple() {
        val lowestLocation = listOfMappings
            .fold(seeds) { acc, agriMap -> acc.map(agriMap::map) }
            .min()
        println("Lowest location to start seeding: $lowestLocation")
    }

    override fun runHard() {
        val seedRanges = seeds
            .zipWithNext()
            .filterIndexed { ind, _ -> ind % 2 == 0}
            .map { Range(it.first, it.second) }
        println(seedRanges)
        val locationRanges = listOfMappings
            .fold(seedRanges) {acc, agriMap -> acc.flatMap(agriMap::mapRange) }

        println("Lowest location to sart: ${locationRanges.minBy { it.start }}")
    }

    private fun parseMapping(mapString: String): AgriMap {
        val lines = mapString.split("\n")
        val header = lines[0].substringBefore(" ").split("-to-")
        val from = Entity.valueOf(header[0].uppercase())
        val to = Entity.valueOf(header[1].uppercase())

        val result = lines.drop(1)
            .map { it.split(" ") }
            .map { mapping(it) }
            .sortedBy { it.sourceStart }

        return AgriMap(from, to, result)
    }

    private fun mapping(record: List<String>): Mapping {
        if (record.size < 3) {
            throw IllegalArgumentException("Record $record contains less than 3 numbers")
        }
        val destStart = record[0].toLongOrNull()
        val sourceStart = record[1].toLongOrNull()
        val range = record[2].toLongOrNull()
        if (destStart == null || sourceStart == null || range == null) {
            throw IllegalArgumentException("Could not parse number from ${record[0]}, ${record[1]}, or ${record[2]}")
        }
        return Mapping(destStart, sourceStart, range)
    }

}

data class AgriMap(val from: Entity, val to: Entity, private val primaryMappings: List<Mapping>) {

    private val mappings: List<Mapping>

    init {
        val mappingsZipped = primaryMappings.sortedBy { it.sourceStart }.zipWithNext()
        val result = mutableListOf(mappingsZipped[0].first)

        for ((left, right) in mappingsZipped) {
            if (left.sourceStart + left.range < right.sourceStart) {
                val start = left.sourceStart + left.range
                val range = right.sourceStart - start
                val newMapping = Mapping(start, start, range)
                result.add(newMapping)
            }
            result.add(right)
        }
        mappings = result.toList()
    }

    fun map(value: Long): Long {
        val closestMapping = mappings
            .dropWhile {it.sourceStart + it.range <= value }
            .firstOrNull()
        return closestMapping?.let { if (value < it.sourceStart) value else it.destStart + (value - it.sourceStart) } ?: value
    }

    fun mapRange(range: Range): List<Range> {
        val result = mutableListOf<Range>()
        var start = range.start
        var r = range.range
        for (m in mappings) {
            if (range.start + range.range < m.sourceStart) {
                return listOf(range)
            }
            if (m.sourceStart + m.range < range.start) {
                continue
            }
            if (start >= m.sourceStart) {
                if (start + r <= m.sourceStart + m.range) {
                    val destRange = Range(m.destStart + (start - m.sourceStart), r)
                    result.add(destRange)
                    return result
                } else {
                    val rangeWithinMapping = m.sourceStart + m.range - start
                    val destRange = Range(m.destStart + (start - m.sourceStart), rangeWithinMapping)
                    start = m.sourceStart + m.range
                    r -= rangeWithinMapping
                    result.add(destRange)
                }
            }
        }
        result.add(Range(start, r))
        return result
    }
}

data class Mapping(val destStart: Long, val sourceStart: Long, val range: Long)

data class Range(val start: Long, val range: Long) {
    init {
        if (range < 1) {
            throw IllegalArgumentException("Range must be positive, got $range instead!")
        }
    }
}

enum class Entity {
    SEED, SOIL, FERTILIZER, WATER, LIGHT, TEMPERATURE, HUMIDITY, LOCATION
}
