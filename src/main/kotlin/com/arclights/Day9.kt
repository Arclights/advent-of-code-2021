package com.arclights

import com.arclights.Day9.part2

fun main() {
    part2()
}

object Day9 {
    fun part1() {
        val data = readData("day9.txt")
        println(data)
        val result = getLowPoints(data).also { println(it) }.also { println(it.map { v -> v + 1 }) }.sumOf { it + 1 }
        println(result)
    }

    fun part2() {
        val data = readData("day9.txt")
        getBasins(data)
            .sortedBy(Set<Pair<Int, Int>>::size)
            .takeLast(3)
            .map { it.size }
            .reduce(Int::times)
            .also { println(it) }

    }

    private fun getBasins(data: Map<Pair<Int, Int>, Int>): Set<Set<Pair<Int, Int>>> =
        data
            .keys
            .toList()
            .fold(setOf<Set<Pair<Int, Int>>>() to data) { (foundBasins, remainingData), coord ->
                if (foundBasins.flatten().contains(coord).not()) {
                    foundBasins.plusElement(getBasin(data, coord)) to remainingData.minus(coord)
                } else
                    foundBasins to remainingData.minus(coord)
            }
            .first

    private fun getBasin(data: Map<Pair<Int, Int>, Int>, vararg coords: Pair<Int, Int>): Set<Pair<Int, Int>> =
        when {
            coords.isEmpty() -> setOf()
            else -> coords
                .filter { data.getValue(it) < 9 }
                .let { lowCoords ->
                    val neighbours = getNeighbours(data, *lowCoords.toTypedArray())
                    val lowNeighbours = neighbours.filter { data.getValue(it) < 9 }
                    getBasin(data.minus(lowCoords.toSet()), *lowNeighbours.toTypedArray()).plus(coords).toSet()
                }
        }

    private fun getLowPoints(data: Map<Pair<Int, Int>, Int>): List<Int> {
        return data.mapNotNull { (coord, value) ->
            getIfLowerThanNeighbour(
                data,
                coord,
                value
            )
        }
    }

    private fun getIfLowerThanNeighbour(
        data: Map<Pair<Int, Int>, Int>,
        coord: Pair<Int, Int>,
        value: Int
    ): Int? =
        getNeighbours(data, coord)
            .map { data.getValue(it) }
            .let { neighbours ->
                neighbours.minOrNull()?.let { lowestNeighbour -> if (lowestNeighbour > value) value else null }
            }

    private fun getNeighbours(data: Map<Pair<Int, Int>, Int>, vararg coords: Pair<Int, Int>) =
        coords.flatMap { coord ->
            setOf(
                getRightNeighbour(data, coord),
                getLeftNeighbour(data, coord),
                getUpperNeighbour(data, coord),
                getLowerNeighbour(data, coord)
            )
        }
            .minus(coords.toSet())
            .filterNotNull()
            .toSet()

    private fun getRightNeighbour(data: Map<Pair<Int, Int>, Int>, coord: Pair<Int, Int>) =
        getIfContainsKey(data, coord.first + 1 to coord.second)

    private fun getLeftNeighbour(data: Map<Pair<Int, Int>, Int>, coord: Pair<Int, Int>) =
        getIfContainsKey(data, coord.first - 1 to coord.second)

    private fun getUpperNeighbour(data: Map<Pair<Int, Int>, Int>, coord: Pair<Int, Int>) =
        getIfContainsKey(data, coord.first to coord.second - 1)

    private fun getLowerNeighbour(data: Map<Pair<Int, Int>, Int>, coord: Pair<Int, Int>) =
        getIfContainsKey(data, coord.first to coord.second + 1)

    private fun getIfContainsKey(data: Map<Pair<Int, Int>, Int>, coord: Pair<Int, Int>) = when {
        data.containsKey(coord) -> coord
        else -> null
    }

    private fun readData(file: String): Map<Pair<Int, Int>, Int> =
        read(file).flatMapIndexed { y, line ->
            line.map(Char::toString).map(String::toInt).mapIndexed { x, v -> (x to y) to v }
        }
            .toMap()
}
