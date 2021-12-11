package com.arclights

import com.arclights.Day11.part2

fun main() {
    part2()
}

object Day11 {
    fun part1() {
        val data = readData("day11.txt")
        println(iterate(data, 100))
    }

    fun part2() {
        val octopuses = readData("day11.txt")
        println(findSync(octopuses))
    }

    private fun iterate(
        octopuses: Map<Pair<Int, Int>, Int>,
        iterations: Int = 100,
        iteration: Int = 0,
        flashes: Int = 0
    ): Int =
        when (iteration) {
            iterations -> flashes
            else -> octopuses
                .mapValues { it.value + 1 }
                .let(::affectNeighbours)
                .let(::flash)
                .let { updatedOctopuses ->
                    iterate(
                        updatedOctopuses,
                        iterations,
                        iteration + 1,
                        flashes + countNewFlashes(updatedOctopuses)
                    )
                }
        }

    private fun findSync(octopuses: Map<Pair<Int, Int>, Int>, iteration: Int = 1): Int = octopuses
        .mapValues { it.value + 1 }
        .let(::affectNeighbours)
        .let(::flash)
        .let { updatedOctopuses ->
            when (countNewFlashes(updatedOctopuses)) {
                updatedOctopuses.size -> iteration
                else -> findSync(updatedOctopuses, iteration + 1)
            }
        }

    private fun affectNeighbours(octopuses: Map<Pair<Int, Int>, Int>) =
        octopuses.entries.fold(octopuses) { updatedOctopuses, (coord, energy) ->
            affectNeighbours(
                coord,
                energy,
                updatedOctopuses
            )
        }

    private fun affectNeighbours(
        coord: Pair<Int, Int>,
        energy: Int,
        octopuses: Map<Pair<Int, Int>, Int>
    ): Map<Pair<Int, Int>, Int> = when (energy) {
        10 -> {
            getNeighbours(
                coord,
                octopuses
            ).let { neighbours ->
                neighbours.fold(octopuses) { updatedOctopuses, neighbour ->
                    updatedOctopuses
                        .plus(neighbour to updatedOctopuses.getValue(neighbour) + 1)
                        .let { affectNeighbours(neighbour, it.getValue(neighbour), it) }
                }
            }
        }
        else -> octopuses
    }

    private fun flash(octopuses: Map<Pair<Int, Int>, Int>) = octopuses.mapValues {
        when {
            it.value > 9 -> 0
            else -> it.value
        }
    }

    private fun countNewFlashes(octopuses: Map<Pair<Int, Int>, Int>) = octopuses.entries.count { it.value == 0 }

    private fun getNeighbours(coord: Pair<Int, Int>, octopuses: Map<Pair<Int, Int>, Int>): Set<Pair<Int, Int>> = setOf(
        getIfContainsKey(octopuses, coord.first + 1 to coord.second),
        getIfContainsKey(octopuses, coord.first to coord.second + 1),
        getIfContainsKey(octopuses, coord.first to coord.second - 1),
        getIfContainsKey(octopuses, coord.first - 1 to coord.second),
        getIfContainsKey(octopuses, coord.first + 1 to coord.second + 1),
        getIfContainsKey(octopuses, coord.first - 1 to coord.second - 1),
        getIfContainsKey(octopuses, coord.first + 1 to coord.second - 1),
        getIfContainsKey(octopuses, coord.first - 1 to coord.second + 1)
    )
        .filterNotNull()
        .toSet()

    private fun getIfContainsKey(data: Map<Pair<Int, Int>, Int>, coord: Pair<Int, Int>) = when {
        data.containsKey(coord) -> coord
        else -> null
    }

    private fun readData(file: String): Map<Pair<Int, Int>, Int> =
        read(file)
            .flatMapIndexed { y, line ->
                line.map(Char::toString).map(String::toInt).mapIndexed { x, v -> (x to y) to v }
            }
            .toMap()
}