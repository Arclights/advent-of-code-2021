package com.arclights

import com.arclights.Day6.part1

fun main() {
    part1()
}

object Day6 {

    fun part1() {
        val fish = readData("day6.txt")
        val result = simulateAndCount(fish, 256)
        println(result)
    }

    private fun simulateAndCount(fishes: List<LanternFish>, iterations: Int = 80, day: Int = 0): Long =
        fishes.fold(0L to mapOf<Pair<Day, Age>, Long>()) { (sum, lookup), currFish ->
            simulateAndCount(sum, lookup, currFish, iterations, day)
        }.first

    private fun simulateAndCount(
        sum: Long,
        lookup: Map<Pair<Day, Age>, Long>,
        fish: LanternFish,
        iterations: Int,
        day: Int
    ): Pair<Long, Map<Pair<Day, Age>, Long>> {
        val (count, newLookup) = lookup[day to fish.timer]?.let { it to lookup } ?: (simulateAndCount(
            fish,
            lookup,
            iterations,
            day + 1
        ).let { (count, newLookup) -> count to newLookup.plus((day to fish.timer) to count) })
        return (sum + count) to newLookup
    }

    private fun simulateAndCount(
        fish: LanternFish,
        lookup: Map<Pair<Day, Age>, Long>,
        iterations: Int = 80,
        day: Int = 0
    ): Pair<Long, Map<Pair<Day, Age>, Long>> = when (day) {
        iterations + 1 -> 1L to lookup
        else -> fish.age().fold(0L to lookup) { (sum, lookup), currFish ->
            simulateAndCount(sum, lookup, currFish, iterations, day)
        }
    }

    private fun readData(file: String) = read(file)[0].split(",").map { it.toInt() }.map { LanternFish(it) }

    data class LanternFish(val timer: Int)

    private fun LanternFish.age() = when (timer) {
        0 -> listOf(LanternFish(6), LanternFish(8))
        else -> listOf(LanternFish(timer - 1))
    }
}
typealias Day = Int
typealias Age = Int