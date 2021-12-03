package com.arclights

import com.arclights.Day3.part2
import kotlin.math.pow

fun main() {
    part2()
}

object Day3 {
    fun part1() = read("day3.txt")
        .map { it.map(Char::digitToInt) }
        .let { rows ->
            rows.reduce { sum, number -> sum.zip(number, Int::plus) }
                .let { sum -> sum.map { if (it * 2 > rows.size) 1 else 0 } }
        }
        .let { gamma -> gamma to gamma.map { if (it == 1) 0 else 1 } }
        .let { (gamma, epsilon) -> toInt(gamma) to toInt(epsilon) }
        .let { (gamma, epsilon) -> gamma * epsilon }
        .let { println(it) }

    fun part2() = read("day3.txt")
        .map { it.map(Char::digitToInt) }
        .let { input ->
            val oxygen = toInt(findRating(input, Int::equals))
            val co2 = toInt(findRating(input, Boolean.negate(Int::equals)))
            println(oxygen * co2)
        }

    fun findRating(input: List<List<Int>>, commonBitComparator: (Int, Int) -> Boolean) =
        findRating(input, 0, commonBitComparator)

    fun findRating(input: List<List<Int>>, pos: Int, commonBitComparator: (Int, Int) -> Boolean): List<Int> =
        when (input.size) {
            1 -> input[0]
            else -> {
                val mostCommonBitInPos = findMostCommonBitInPos(input, pos)
                findRating(
                    input.filter { commonBitComparator(it[pos], mostCommonBitInPos) },
                    pos + 1,
                    commonBitComparator
                )
            }
        }

    fun findMostCommonBitInPos(input: List<List<Int>>, pos: Int) = input
        .fold(0) { sum, row -> sum + row[pos] }
        .let {
            when {
                it * 2 > input.size -> 1
                it * 2 == input.size -> 1
                else -> 0
            }
        }

    private fun toInt(bits: List<Int>) =
        bits.reversed().foldIndexed(0.0) { i, sum, bit -> sum + 2.0.pow(i) * bit }.toInt()
}

fun <A, B> Boolean.Companion.negate(operation: (A, B) -> Boolean) = { a: A, b: B -> operation(a, b).not() }
