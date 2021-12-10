package com.arclights

import com.arclights.Day10.part2

fun main() {
    part2()
}

object Day10 {
    fun part1() {
        val data = read("day10.txt")
        data.map(::calculateSyntaxErrorScore)
            .also { println(it) }
            .sum()
            .also { println(it) }
    }

    fun part2() {
        read("day10.txt")
            .asSequence()
            .map(::completeLine)
            .map { it.map(completionScore::getValue) }
            .map { it.fold(0L) { acc, curr -> acc * 5 + curr } }
            .filter { it > 0 }
            .sorted()
            .toList()
            .let { it.drop(it.size / 2).first() }
            .also { println(it) }

    }

    private tailrec fun calculateSyntaxErrorScore(
        input: String,
        expectedClosingChars: List<Char> = listOf()
    ): Int =
        when (input) {
            "" -> 0
            else -> {
                val first = input.first()
                val rest = input.drop(1)
                when (first) {
                    '(' -> calculateSyntaxErrorScore(rest, expectedClosingChars.plus(')'))
                    '[' -> calculateSyntaxErrorScore(rest, expectedClosingChars.plus(']'))
                    '{' -> calculateSyntaxErrorScore(rest, expectedClosingChars.plus('}'))
                    '<' -> calculateSyntaxErrorScore(rest, expectedClosingChars.plus('>'))
                    expectedClosingChars.last() -> calculateSyntaxErrorScore(rest, expectedClosingChars.dropLast(1))
                    else -> syntaxErrorScore.getValue(first)
                }
            }
        }

    private val syntaxErrorScore = mapOf(
        ')' to 3,
        ']' to 57,
        '}' to 1197,
        '>' to 25137
    )

    private val completionScore = mapOf(
        ')' to 1,
        ']' to 2,
        '}' to 3,
        '>' to 4
    )

    private fun completeLine(input: String, expectedClosingChars: List<Char> = listOf()): List<Char> = when (input) {
        "" -> expectedClosingChars.reversed()
        else -> {
            val first = input.first()
            val rest = input.drop(1)
            when (first) {
                '(' -> completeLine(rest, expectedClosingChars.plus(')'))
                '[' -> completeLine(rest, expectedClosingChars.plus(']'))
                '{' -> completeLine(rest, expectedClosingChars.plus('}'))
                '<' -> completeLine(rest, expectedClosingChars.plus('>'))
                expectedClosingChars.last() -> completeLine(rest, expectedClosingChars.dropLast(1))
                else -> listOf()
            }
        }
    }
}