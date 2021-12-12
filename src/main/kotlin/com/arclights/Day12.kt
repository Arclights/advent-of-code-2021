package com.arclights

import com.arclights.Day12.part2

fun main() {
    part2()
}

object Day12 {
    fun part1() {
        val connections = readData("day12.txt")
        findPaths(connections, ::isVisitableCave)
            .count()
            .also { println(it) }
    }

    fun part2() {
        val connections = readData("day12.txt")
        findPaths(connections, ::isVisitableCave2)
            .count()
            .also { println(it) }
    }

    private fun findPaths(
        connections: List<Pair<String, String>>,
        isVisitableCave: (String, List<String>) -> Boolean,
        previousCave: String = "start",
        visitedCaves: List<String> = listOf("start")
    ): List<List<String>> = when (previousCave) {
        "end" -> listOf(visitedCaves)
        else -> {
            getConnectionsFromCave(connections, previousCave)
                .filter { isUsableConnection(it, previousCave, visitedCaves, isVisitableCave) }
                .flatMap { it.toList() }
                .toSet()
                .minus(previousCave)
                .flatMap { findPaths(connections, isVisitableCave, it, visitedCaves.plus(it)) }
        }
    }

    private fun getConnectionsFromCave(connections: List<Pair<String, String>>, cave: String) =
        connections.filter { it.first == cave || it.second == cave }

    private fun isUsableConnection(
        connection: Pair<String, String>,
        previousCave: String,
        visitedCaves: List<String>,
        isVisitableCave: (String, List<String>) -> Boolean
    ) = when (previousCave) {
        connection.first -> isVisitableCave(connection.second, visitedCaves)
        else -> isVisitableCave(connection.first, visitedCaves)
    }

    private fun isVisitableCave(cave: String, visitedCaves: List<String>) =
        isBigCave(cave) || visitedCaves.contains(cave).not()

    private fun isVisitableCave2(cave: String, visitedCaves: List<String>) =
        isBigCave(cave)
                || visitedCaves.contains(cave).not()
                || noSmallCaveVisitedTwice(visitedCaves) && notVisitedTwice(cave, visitedCaves) && cave != "start"

    private fun noSmallCaveVisitedTwice(visitedCaves: List<String>) = visitedCaves
        .filter { isBigCave(it).not() }
        .groupBy { it }
        .values
        .map { it.size }
        .none { it > 1 }

    private fun notVisitedTwice(cave: String, visitedCaves: List<String>) = visitedCaves
        .groupBy { it }
        .get(cave)
        ?.size
        ?.let { it < 2 }
        ?: true

    private fun isBigCave(cave: String) = cave.uppercase() == cave

    private fun readData(file: String) = read(file).map { it.split("-") }.map { it[0] to it[1] }
}