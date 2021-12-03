package com.arclights

import com.arclights.Day1.part2

fun main() {
    part2()
}

object Day1 {
    fun part1() = read("day1.txt")
        .map { it.toInt() }
        .windowed(2)
        .sumOf { if (it[0] < it[1]) 1L else 0L }
        .let { println(it) }

    fun part2() = read("day1.txt")
        .asSequence()
        .map { it.toInt() }
        .windowed(3)
        .map { it.sum() }
        .windowed(2)
        .sumOf { if (it[0] < it[1]) 1L else 0L }
        .let { println(it) }
}