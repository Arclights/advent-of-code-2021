package com.arclights

import com.arclights.Day5.part2
import kotlin.math.abs
import kotlin.math.max

fun main() {
    part2()
}

object Day5 {

    fun part1() {
        val lines = readData("day5.txt").filter { isHorizontalOrVertical(it) }.toSet()
        val overlaps = findOverlaps(lines)
        println(overlaps.toSet().size)
    }

    fun part2() {
        val lines = readData("day5.txt")
        val overlaps = findOverlaps(lines)
        println(overlaps.toSet().size)
    }

    private fun isHorizontalOrVertical(line: Line) = line.start.x == line.end.x || line.start.y == line.end.y

    private fun findOverlaps(lines: Set<Line>) =
        lines.flatMap { line1 -> lines.minus(line1).flatMap { line2 -> getOverlaps(line1, line2) } }.toSet()

    private fun getOverlaps(line1: Line, line2: Line): Set<Coordinate> {
        val line1Coords = getCoordsOnLine(line1)
        val line2Coords = getCoordsOnLine(line2)
        val ol = line1Coords.intersect(line2Coords)
        return ol
    }

    private fun getCoordsOnLine(line: Line): Set<Coordinate> {
        val nbrOfCoordinates = max(abs(line.start.x - line.end.x), abs(line.start.y - line.end.y)) + 1
        val xCoords = getCoordinatesInOneDimension(line.start.x, line.end.x, nbrOfCoordinates)
        val yCoords = getCoordinatesInOneDimension(line.start.y, line.end.y, nbrOfCoordinates)
        return xCoords.zip(yCoords) { x, y -> Coordinate(x, y) }.toSet()
    }

    private fun getCoordinatesInOneDimension(start: Int, end: Int, nbrOfCoordinates: Int): List<Int> = when (start) {
        end -> List(nbrOfCoordinates) { start }
        else -> getRange(start, end).toList()
    }

    private fun getRange(start: Int, end: Int) = when {
        start > end -> start downTo end
        else -> start..end
    }


    private fun readData(file: String) = read(file)
        .map { it.split(" -> ") }
        .map {
            it.map { c ->
                c.split(",")
                    .map { coord -> coord.toInt() }
                    .let { coord -> Coordinate(coord[0], coord[1]) }
            }
        }
        .map { Line(it[0], it[1]) }
        .toSet()

    data class Coordinate(val x: Int, val y: Int)
    data class Line(val start: Coordinate, val end: Coordinate)
}