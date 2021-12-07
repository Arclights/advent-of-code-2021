package com.arclights

import com.arclights.Day7.part2
import kotlin.math.abs

fun main() {
    part2()
}

object Day7 {
    fun part1() {
        val positions = readData("day7.txt")
        val min = positions.minOrNull()!!
        val max = positions.maxOrNull()!!
        val (bestPositionToMoveTo, bestCost) = getBestPositionAndCost(positions, min, max, ::calcCost)
        println(bestPositionToMoveTo)
        println(bestCost)
    }

    fun part2() {
        val positions = readData("day7.txt")
        val min = positions.minOrNull()!!
        val max = positions.maxOrNull()!!
        val (bestPositionToMoveTo, bestCost) = getBestPositionAndCost(positions, min, max, ::calcCost2)
        println(bestPositionToMoveTo)
        println(bestCost)
    }

    private fun getBestPositionAndCost(
        positions: List<Int>,
        minPosition: Int,
        maxPosition: Int,
        costFunction: (Int, Int) -> Int
    ) =
        (minPosition..maxPosition).fold(-1 to Int.MAX_VALUE) { (bestPosToMoveTo, bestCost), posToMoveTo ->
            getCost(positions, posToMoveTo, costFunction).let { cost ->
                when {
                    cost < bestCost -> posToMoveTo to cost
                    else -> bestPosToMoveTo to bestCost
                }
            }
        }

    private fun getCost(positions: List<Int>, positionToMoveTo: Int, costFunction: (Int, Int) -> Int) =
        positions.fold(0) { cost, position ->
            cost + costFunction(position, positionToMoveTo)
        }

    private fun calcCost2(position: Int, positionToMoveTo: Int) =
        calcCost(position, positionToMoveTo).let { cost -> cost * (cost + 1) / 2 }

    private fun calcCost(position: Int, positionToMoveTo: Int) = abs(position - positionToMoveTo)

    private fun readData(file: String) = read(file)[0].split(",").map { it.toInt() }
}