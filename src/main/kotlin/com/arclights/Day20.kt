package com.arclights

import com.arclights.Day20.part2
import kotlin.math.pow

fun main() {
    part2()
}

object Day20 {
    fun part1() {
        val (algorithm, inputImage) = readData("day20.txt")
        println("Original:\n${inputImage.asString()}\n")
        enhance(algorithm, inputImage, 2)
            .also { println("Result:\n${it.asString()}") }
            .count { it.value == '#' }
            .also { println(it) }
    }

    fun part2() {
        val (algorithm, inputImage) = readData("day20.txt")
        println("Original:\n${inputImage.asString()}\n")
        enhance(algorithm, inputImage, 50)
            .also { println("Result:\n${it.asString()}") }
            .count { it.value == '#' }
            .also { println(it) }
    }

    private tailrec fun enhance(
        algorithm: String,
        inputImage: Map<Coord, Char>,
        iterations: Int,
        iteration: Int = 0,
        infinityPixel: Char = '.'
    ): Map<Coord, Char> = when (iteration) {
        iterations -> inputImage
        else -> {
            val enhancedImage = inputImage
                .pad(infinityPixel)
                .let { paddedImage ->
                    paddedImage.map { (coord, _) ->
                        enhance(
                            algorithm,
                            paddedImage,
                            coord,
                            infinityPixel
                        )
                    }
                }
                .removeEdges()
                .toMap()
            enhance(
                algorithm,
                enhancedImage,
                iterations,
                iteration + 1,
                calculateInfinityPixel(algorithm, infinityPixel)
            )
        }
    }

    private fun calculateInfinityPixel(algorithm: String, infinityPixel: Char) =
        infinityPixel.toString().repeat(9).map { pixelToBit(it) }
            .let { binaryToDecimal(it) }
            .let { algorithm[it] }

    private fun enhance(algorithm: String, image: Map<Coord, Char>, coord: Coord, infinitePixel: Char) =
        getNeighbours(coord)
            .plus(coord)
            .map { c -> c to (image[c] ?: infinitePixel) }
            .let {
                val algorithmIndex = imageToInt(it)
                val newPixel = algorithm[algorithmIndex]
                coord to newPixel
            }

    private fun imageToInt(image: List<Pair<Coord, Char>>): Int = image.groupBy { it.first.y }
        .values
        .flatMap { line -> line.sortedBy { it.first.x }.map { it.second }.map { pixelToBit(it) } }
        .let { binaryToDecimal(it) }

    private fun pixelToBit(pixel: Char) = if (pixel == '#') 1 else 0

    private fun binaryToDecimal(input: List<Int>) = input
        .reversed()
        .mapIndexed { i, d -> d * 2.0.pow(i) }
        .sum()
        .toInt()

    private fun getNeighbours(coord: Coord) = setOf(
        getNorthWestNeighbour(coord),
        getNorthNeighbour(coord),
        getNorthEastNeighbour(coord),
        getWestNeighbour(coord),
        getEastNeighbour(coord),
        getSouthWestNeighbour(coord),
        getSouthNeighbour(coord),
        getSouthEastNeighbour(coord)
    )

    private fun getNorthWestNeighbour(coord: Coord) = Coord(coord.x - 1, coord.y - 1)
    private fun getNorthNeighbour(coord: Coord) = Coord(coord.x, coord.y - 1)
    private fun getNorthEastNeighbour(coord: Coord) = Coord(coord.x + 1, coord.y - 1)
    private fun getWestNeighbour(coord: Coord) = Coord(coord.x - 1, coord.y)
    private fun getEastNeighbour(coord: Coord) = Coord(coord.x + 1, coord.y)
    private fun getSouthWestNeighbour(coord: Coord) = Coord(coord.x - 1, coord.y + 1)
    private fun getSouthNeighbour(coord: Coord) = Coord(coord.x, coord.y + 1)
    private fun getSouthEastNeighbour(coord: Coord) = Coord(coord.x + 1, coord.y + 1)

    private fun List<Pair<Coord, Char>>.removeEdges(borderSize: Int = 1): List<Pair<Coord, Char>> {
        val maxX = maxOf { it.first.x }
        val maxY = maxOf { it.first.y }
        return filter { (it.first.x < borderSize || it.first.x > maxX - borderSize || it.first.y < borderSize || it.first.y > maxY - borderSize).not() }.map {
            Coord(
                it.first.x - 1,
                it.first.y - 1
            ) to it.second
        }
    }

    private fun Map<Coord, Char>.pad(infinityPixel: Char): Map<Coord, Char> {
        val paddingMagnitude = 2
        val doublePaddingMagnitude = paddingMagnitude * 2
        val maxX = maxOf { it.key.x }
        val maxY = maxOf { it.key.y }
        val padding = listOf(
            (0 until paddingMagnitude).flatMap { y -> (0..maxX + doublePaddingMagnitude).map { x -> Coord(x, y) } },
            (maxY + paddingMagnitude + 1..maxY + doublePaddingMagnitude).flatMap { y ->
                (0..maxX + doublePaddingMagnitude).map { x ->
                    Coord(
                        x,
                        y
                    )
                }
            },
            (0 until maxY + doublePaddingMagnitude).flatMap { y ->
                (0 until paddingMagnitude).map { x ->
                    Coord(
                        x,
                        y
                    )
                }
            },
            (0 until maxY + doublePaddingMagnitude).flatMap { y ->
                (maxX + paddingMagnitude + 1..maxX + paddingMagnitude * 2).map { x ->
                    Coord(
                        x,
                        y
                    )
                }
            },
        )
            .flatten()
            .toSet()
            .map { it to infinityPixel }
        val shifted = entries.map { Coord(it.key.x + paddingMagnitude, it.key.y + paddingMagnitude) to it.value }
        return shifted.plus(padding).toMap()
    }

    fun readData(file: String) = read(file)
        .let { lines ->
            val algorithm = lines.first()
            val inputImage =
                lines.drop(2).flatMapIndexed { y, line -> line.mapIndexed { x, pixel -> Coord(x, y) to pixel } }.toMap()
            algorithm to inputImage
        }

    private fun Map<Coord, Char>.asString() = entries.groupBy { it.key.y }
        .entries
        .sortedBy { it.key }
        .map { it.value }
        .joinToString("\n") { it.sortedBy { it.key.x }.joinToString("") { it.value.toString() } }
}