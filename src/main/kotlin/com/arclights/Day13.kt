package com.arclights

import com.arclights.Day13.part2

fun main() {
    part2()
}

object Day13 {
    fun part1() {
        val (coordinates, folds) = readData("day13.txt")
        val (orientation, line) = folds.first()
        coordinates
            .fold(orientation, line)
            .count()
            .also { println(it) }
    }

    fun part2() {
        val (coordinates, folds) = readData("day13.txt")
        folds
            .fold(coordinates) { folded, (orientation, line) -> folded.fold(orientation, line) }
            .also { println(it.asString()) }
    }

    fun Set<Pair<Int, Int>>.fold(orientation: String, line: Int) = when (orientation) {
        "y" -> foldUp(line)
        else -> foldLeft(line)
    }

    private fun Set<Pair<Int, Int>>.foldUp(line: Int) = filter { it.second < line }
        .plus(filter { it.second > line }.map { it.first to 2 * line - it.second })
        .toSet()

    private fun Set<Pair<Int, Int>>.foldLeft(line: Int) = filter { it.first < line }
        .plus(filter { it.first > line }.map { 2 * line - it.first to it.second })
        .toSet()

    private fun Set<Pair<Int, Int>>.asString(): String {
        val maxX = maxOf { it.first }
        val maxY = maxOf { it.second }

        return (0..maxY).joinToString(separator = "\n") { y ->
            (0..maxX).joinToString(separator = "") { x ->
                if (contains(x to y)) {
                    "#"
                } else {
                    "."
                }
            }
        }
    }

    private fun readData(file: String) = read(file).let { lines ->
        val coordinates = lines.takeWhile { it != "" }
        val folds = lines.takeLastWhile { it != "" }

        val parsedCoordinates = coordinates
            .map { it.split(",") }
            .map { it[0].toInt() to it[1].toInt() }
            .toSet()
        val parsedFolds = folds
            .map { it.drop(11) }
            .map { it.split("=") }
            .map { it[0] to it[1].toInt() }

        parsedCoordinates to parsedFolds
    }
}