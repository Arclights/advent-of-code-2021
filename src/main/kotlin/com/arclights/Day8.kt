package com.arclights

import com.arclights.Day8.part2
import kotlin.math.pow

fun main() {
    part2()
}

object Day8 {
    fun part1() {
        val data = readData("day8.txt")
        val result = data
            .flatMap { (patterns, inputs) ->
                val wiresToSegments = getWiresToSegments(patterns)
                inputs.map { translateToDigit(it, wiresToSegments) }
            }
            .count { it in setOf(1, 4, 7, 8) }
        println(result)
    }

    fun part2() {
        val data = readData("day8.txt")
        val result = data.sumOf { (patterns, inputs) ->
            val wiresToSegments = getWiresToSegments(patterns)
            inputs.map { translateToDigit(it, wiresToSegments) }.toNumber()
        }
        println(result)
    }

    private fun translateToDigit(wires: Set<Char>, segments: Map<Char, Int>): Int = wires
        .map { segments.getValue(it) }
        .toSet()
        .let { segmentsToDigit.getValue(it) }

    private val segmentsToDigit = mapOf(
        setOf(1, 2, 3, 5, 6, 7) to 0,
        setOf(3, 6) to 1,
        setOf(1, 3, 4, 5, 7) to 2,
        setOf(1, 3, 4, 6, 7) to 3,
        setOf(2, 3, 4, 6) to 4,
        setOf(1, 2, 4, 6, 7) to 5,
        setOf(1, 2, 4, 5, 6, 7) to 6,
        setOf(1, 3, 6) to 7,
        setOf(1, 2, 3, 4, 5, 6, 7) to 8,
        setOf(1, 2, 3, 4, 6, 7) to 9
    )

    private fun getWiresToSegments(patterns: List<Set<Char>>) =
        pruneEasyCommonSegmentPossibilities(patterns)
            .let { pruneHardCommonSegmentPossibilities(it, patterns) }
            .map { (segment, possibilities) -> possibilities.first() to segment }
            .toMap()

    private fun pruneEasyCommonSegmentPossibilities(patterns: List<Set<Char>>) =
        patterns.mapNotNull { getEasySegmentPossibilities(it) }
            .flatMap { it.toList() }
            .groupBy(Pair<Int, Set<Char>>::first, Pair<Int, Set<Char>>::second)
            .mapValues { it.value.reduce(Set<Char>::intersect) }
            .let(::pruneEasyCommonSegmentsKnownToBeImpossible)

    private fun pruneEasyCommonSegmentsKnownToBeImpossible(segmentPossibilities: Map<Int, Set<Char>>) =
        segmentPossibilities
            .mapValues { entry ->
                segmentPossibilities.values.fold(entry.value) { sm, currSm ->
                    if (currSm.containsAll(sm).not()) sm.minus(currSm) else sm
                }
            }

    private fun pruneHardCommonSegmentPossibilities(
        segmentPossibilities: Map<Int, Set<Char>>,
        patterns: List<Set<Char>>
    ): Map<Int, Set<Char>> = segmentPossibilities
        .let { pruneTwoThreeFiveCommonSegmentPossibilities(it, patterns) }
        .let { pruneZeroSixNineCommonSegmentPossibilities(it, patterns) }

    private fun pruneTwoThreeFiveCommonSegmentPossibilities(
        segmentPossibilities: Map<Int, Set<Char>>,
        patterns: List<Set<Char>>
    ): Map<Int, Set<Char>> {
        val commonSegments = patterns.filter { it.size == 5 }.reduce(Set<Char>::intersect)
        return segmentPossibilities.mapValues { (segment, possibilities) ->
            when (segment) {
                1 -> possibilities.intersect(commonSegments)
                2 -> possibilities.minus(commonSegments)
                3 -> possibilities.minus(commonSegments)
                4 -> possibilities.intersect(commonSegments)
                5 -> possibilities.minus(commonSegments)
                6 -> possibilities.minus(commonSegments)
                7 -> possibilities.intersect(commonSegments)
                else -> possibilities
            }
        }
    }

    private fun pruneZeroSixNineCommonSegmentPossibilities(
        segmentPossibilities: Map<Int, Set<Char>>,
        patterns: List<Set<Char>>
    ): Map<Int, Set<Char>> {
        val commonSegments = patterns.filter { it.size == 6 }.reduce(Set<Char>::intersect)
        return segmentPossibilities.mapValues { (segment, possibilities) ->
            when (segment) {
                1 -> possibilities.intersect(commonSegments)
                2 -> possibilities.intersect(commonSegments)
                3 -> possibilities.minus(commonSegments)
                4 -> possibilities.minus(commonSegments)
                5 -> possibilities.minus(commonSegments)
                6 -> possibilities.intersect(commonSegments)
                7 -> possibilities.intersect(commonSegments)
                else -> possibilities
            }
        }
    }

    private fun getEasySegmentPossibilities(pattern: Set<Char>) = when (pattern.size) {
        2 -> mapOf(3 to pattern, 6 to pattern)
        3 -> mapOf(1 to pattern, 3 to pattern, 6 to pattern)
        4 -> mapOf(2 to pattern, 3 to pattern, 4 to pattern, 6 to pattern)
        7 -> mapOf(1 to pattern, 2 to pattern, 3 to pattern, 4 to pattern, 5 to pattern, 6 to pattern, 7 to pattern)
        else -> null
    }

    private fun List<Int>.toNumber() = mapIndexed { i, v -> v * 10.0.pow(this.size - i - 1) }.sum().toInt()

    private fun readData(file: String) = read(file)
        .map { it.split(" | ") }
        .map { it.map { part -> part.split(" ").map { wire -> wire.toSet() } } }
        .map { it[0] to it[1] }
}