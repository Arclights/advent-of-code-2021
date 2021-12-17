package com.arclights

import com.arclights.Day17.part2
import kotlin.math.abs

fun main() {
    part2()
}

object Day17 {
    fun part1() {
        val targetArea = readData("day17.txt")
        findTrajectories(targetArea)
            .maxByOrNull { trajectory -> trajectory.maxOf { it.y } }!!
            .also { printDiagram(targetArea, it) }
            .let { trajectory -> trajectory.maxOf { it.y } }
            .also { println(it) }
    }

    fun part2() {
        val targetArea = readData("day17.txt")
        findTrajectories(targetArea)
            .also { println(it.count()) }
    }

    private fun findTrajectories(targetArea: Pair<IntRange, IntRange>): List<List<Coord>> {
        val xVelocityRange = (0..targetArea.first.last)
        val yVelocityRange = (targetArea.second.first..abs(targetArea.first.first / 2))
        return yVelocityRange
            .flatMap { yVelocity ->
                xVelocityRange.mapNotNull { xVelocity ->
                    calculateTrajectory(
                        xVelocity,
                        yVelocity,
                        targetArea
                    )
                }
            }

    }

    private tailrec fun calculateTrajectory(
        xVelocity: Int,
        yVelocity: Int,
        targetArea: Pair<IntRange, IntRange>,
        pos: Coord = Coord(0, 0),
        pastPositions: List<Coord> = listOf()
    ): List<Coord>? {
        val updatedPos = Coord(pos.x + xVelocity, pos.y + yVelocity)
        val updatedXVelocity = if (xVelocity > 0) xVelocity - 1 else if (xVelocity == 0) 0 else xVelocity + 1
        val updatedYVelocity = yVelocity - 1
        return when {
            targetArea.hit(updatedPos) -> pastPositions.plus(updatedPos)
            targetArea.missed(updatedPos) -> null
            else -> calculateTrajectory(
                updatedXVelocity,
                updatedYVelocity,
                targetArea,
                updatedPos,
                pastPositions.plus(updatedPos)
            )
        }
    }

    private fun Pair<IntRange, IntRange>.hit(pos: Coord) = pos.x in first && pos.y in second

    private fun Pair<IntRange, IntRange>.missed(pos: Coord) = pos.x > first.last || pos.y < second.first

    private fun printDiagram(targetArea: Pair<IntRange, IntRange>, trajectory: List<Coord> = listOf()) {
        data class DiagramCoord(val x: Int, val y: Int, val symbol: Char) {
            override fun equals(other: Any?): Boolean =
                if (other is DiagramCoord) other.x == x && other.y == y else false

            override fun hashCode(): Int = x.hashCode() + y.hashCode()
        }

        val start = DiagramCoord(0, 0, 'S')
        val targetAreaCoords = targetArea.first.flatMap { x -> targetArea.second.map { y -> DiagramCoord(x, y, 'T') } }
        val trajectoryCoords = trajectory.map { DiagramCoord(it.x, it.y, '#') }.toSet()

        val definedCoords = trajectoryCoords.plus(start).plus(targetAreaCoords)

        val minX = definedCoords.minOf { it.x }
        val maxX = definedCoords.maxOf { it.x }
        val minY = definedCoords.minOf { it.y }
        val maxY = definedCoords.maxOf { it.y }
        val background = (minX..maxX).flatMap { x -> (minY..maxY).map { y -> DiagramCoord(x, y, '.') } }.toSet()

        definedCoords.plus(background)
            .groupBy { it.y }
            .entries
            .sortedBy { it.key }
            .reversed()
            .joinToString(separator = "\n") { (_, coord) ->
                coord.sortedBy { it.x }.joinToString(separator = "") { it.symbol.toString() }
            }
            .let { println(it) }

    }

    private fun readData(file: String) = read(file)[0]
        .drop(13)
        .split(", ")
        .let { parseRange(it[0]) to parseRange(it[1]) }

    private fun parseRange(range: String) = range
        .drop(2)
        .split("..")
        .let { it[0].toInt()..it[1].toInt() }
}