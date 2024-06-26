package org.some.codeadvent.kotlin

object Day09 : Challenge {
    override val day = 9

    private val lines = loadResourceLines("data/day09-input.txt", "Could not find weather report data")

    override fun runSimple() {
        val records = lines.map { line -> line.split(" ").map { it.toInt()} }
        val sum = records
            .map { finDiffScheme(it) }
            .fold(Pair(0, 0)) { acc, p -> Pair(acc.first + p.first, acc.second + p.second) }
        println("OASIS report, sum of extrapolations: $sum")
    }

    override fun runHard() {
        runSimple()
    }

    private fun finDiffScheme(record: List<Int>): Pair<Int, Int> {
        val table = mutableListOf<List<Int>>()
        var temp = MutableList(record.size) { record[it] }
        table.add(temp)

        while (temp.isNotEmpty() && temp.any { it != 0 }) {
            val nextRow = MutableList(temp.size - 1) { temp[it + 1] - temp [it] }
            table.add(nextRow)
            temp = nextRow
        }

        return table.reversed().fold(Pair(0, 0)) { (back, forw), diff -> Pair(diff.first() - back, diff.last() + forw) }
    }
}
