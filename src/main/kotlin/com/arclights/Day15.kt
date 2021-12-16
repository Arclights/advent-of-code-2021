package com.arclights

import com.arclights.Day15.part2

fun main() {
    part2()
}

object Day15 {
    fun part1() {
        val cave = readData("day15.txt")
        findPath(cave)
            .also { println(cave.asString(it)) }
            .also { println(it.sumOf { i -> cave.getValue(i) }) }
    }

    fun part2() {
        val cave = readData("day15.txt")
        val expandedCave = expandCave(cave)
        findPath(expandedCave)
            .also { println(expandedCave.asString(it)) }
            .also { println(it.sumOf { i -> expandedCave.getValue(i) }) }
    }

    private fun findPath(cave: Map<Pair<Int, Int>, Int>): List<Pair<Int, Int>> {
        val endCoord = cave.map { it.key }
            .let { coords -> coords.maxOf { it.first } to coords.maxOf { it.second } }
        val coords = cave.keys.toList()
        val gScore = coords.associateWith { Int.MAX_VALUE }.plus((0 to 0) to 0).toMutableMap()
        val fScore = mutableMapOf((0 to 0) to h(0 to 0, cave))
        return aStar(cave, endCoord, mutableSetOf(0 to 0), gScore, fScore)
    }

    private tailrec fun aStar(
        cave: Map<Pair<Int, Int>, Int>,
        endCoord: Pair<Int, Int>,
        openSet: MutableSet<Pair<Int, Int>>,
        gScore: MutableMap<Pair<Int, Int>, Int>,
        fScore: MutableMap<Pair<Int, Int>, Int>,
        cameFrom: MutableMap<Pair<Int, Int>, Pair<Int, Int>> = mutableMapOf()
    ): List<Pair<Int, Int>> {
        return when (val current = openSet.filter { fScore.containsKey(it) }.minByOrNull { fScore.getValue(it) }!!) {
            endCoord -> reconstructPath(current, cameFrom)
            else -> {
                val (updatedOpenSet, updatedGScore, updatedFScore, updatedCameFrom) = getNeighbours(current, cave)
                    .fold(
                        Quadruple(
                            openSet.apply { remove(current) },
                            gScore,
                            fScore,
                            cameFrom
                        )
                    ) { (updatedOpenSet, updatedGScore, updatedFScore, updatedCameFrom), neighbour ->
                        val tentativeGScore = gScore.getValue(current) + cave.getValue(neighbour)
                        when {
                            tentativeGScore < gScore.getValue(neighbour) -> Quadruple(
                                updatedOpenSet.apply { add(neighbour) },
                                updatedGScore.apply { put(neighbour, tentativeGScore) },
                                updatedFScore.apply { put(neighbour, tentativeGScore + h(neighbour, cave)) },
                                updatedCameFrom.apply { put(neighbour, current) }
                            )
                            else -> Quadruple(updatedOpenSet, updatedGScore, updatedFScore, updatedCameFrom)
                        }
                    }
                aStar(cave, endCoord, updatedOpenSet, updatedGScore, updatedFScore, updatedCameFrom)
            }
        }
    }

    private fun h(coord: Pair<Int, Int>, cave: Map<Pair<Int, Int>, Int>): Int {
        return getNeighbours(coord, cave).minOf { cave.getValue(it) }
    }

    private fun reconstructPath(
        current: Pair<Int, Int>,
        cameFrom: Map<Pair<Int, Int>, Pair<Int, Int>>
    ): List<Pair<Int, Int>> = when {
        cameFrom.containsKey(current).not() -> listOf()
        else -> {
            val next = cameFrom.getValue(current)
            reconstructPath(next, cameFrom).plus(current)
        }
    }

    private fun getNeighbours(coord: Pair<Int, Int>, cave: Map<Pair<Int, Int>, Int>) = setOf(
        getRightNeighbour(cave, coord),
        getLeftNeighbour(cave, coord),
        getUpperNeighbour(cave, coord),
        getLowerNeighbour(cave, coord)
    ).filterNotNull()

    private fun getRightNeighbour(cave: Map<Pair<Int, Int>, Int>, coord: Pair<Int, Int>) =
        getIfContainsCoord(cave, coord.first + 1 to coord.second)

    private fun getLeftNeighbour(cave: Map<Pair<Int, Int>, Int>, coord: Pair<Int, Int>) =
        getIfContainsCoord(cave, coord.first - 1 to coord.second)

    private fun getUpperNeighbour(cave: Map<Pair<Int, Int>, Int>, coord: Pair<Int, Int>) =
        getIfContainsCoord(cave, coord.first to coord.second - 1)

    private fun getLowerNeighbour(cave: Map<Pair<Int, Int>, Int>, coord: Pair<Int, Int>) =
        getIfContainsCoord(cave, coord.first to coord.second + 1)

    private fun getIfContainsCoord(cave: Map<Pair<Int, Int>, Int>, coord: Pair<Int, Int>) = when {
        cave.containsKey(coord) -> coord
        else -> null
    }

    private fun expandCave(cave: Map<Pair<Int, Int>, Int>): Map<Pair<Int, Int>, Int> {
        val width = cave.keys.maxOf { it.first } + 1
        val height = cave.keys.maxOf { it.second } + 1

        return cave.entries
            .flatMap { (coord, risk) ->
                (0 until 5).flatMap { dx ->
                    (0 until 5).map { dy ->
                        (coord.first + (width * dx) to coord.second + (height * dy)) to (risk + dx + dy).let {
                            if (it > 9) it.mod(
                                10
                            ) + 1 else it
                        }
                    }
                }
            }
            .also { it.groupBy { it.first }.mapValues { it.value.size } }
            .toMap()
    }

    private fun Map<Pair<Int, Int>, Int>.asString(visitedCoords: Collection<Pair<Int, Int>> = setOf()) =
        (0..maxOf { it.key.second }).joinToString(separator = "\n") { y ->
            (0..maxOf { it.key.first }).joinToString(separator = "\t") { x ->
                getValue(x to y).let { risk ->
                    if (visitedCoords.contains(x to y)) {
                        "[$risk]"
                    } else {
                        "$risk"
                    }
                }
            }
        }

    fun readData(file: String) = read(file)
        .flatMapIndexed { y, line -> line.mapIndexed { x, risk -> ((x to y) to risk.toString().toInt()) } }
        .toMap()

    data class Quadruple<A, B, C, D>(val a: A, val b: B, val c: C, val d: D)
}