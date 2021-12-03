package com.arclights

import com.arclights.Day2.part2

fun main() {
    part2()
}

object Day2 {
    fun part1() = readData("day2.txt")
        .fold(0 to 0) { (x, y), (command, amount) ->
            when (command) {
                "forward" -> x + amount to y
                "up" -> x to y - amount
                "down" -> x to y + amount
                else -> throw IllegalArgumentException("Unknown command")
            }
        }
        .let { it.first * it.second }
        .let { println(it) }

    fun part2() = readData("day2.txt")
        .fold(Triple(0, 0, 0)) { (x, y, aim), (command, amount) ->
            when (command) {
                "forward" -> Triple(x + amount, y + aim * amount, aim)
                "up" -> Triple(x, y, aim - amount)
                "down" -> Triple(x, y, aim + amount)
                else -> throw IllegalArgumentException("Unknown command")
            }
        }
        .let { it.first * it.second }
        .let { println(it) }

    private fun readData(file: String) = read(file)
        .map { it.split(" ") }
        .map { it[0] to it[1].toInt() };
}