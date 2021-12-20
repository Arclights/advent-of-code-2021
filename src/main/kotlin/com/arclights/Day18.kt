package com.arclights

import com.arclights.Day18.part1
import com.arclights.Day18.part2
import kotlin.math.ceil
import kotlin.math.max

fun main() {
    part1()
    part2()
}

object Day18 {
    fun part1() {
        readData("day18.txt")
            .reduce { acc, curr -> (acc + curr).reduce() }
            .also { println(it.magnitude()) }
    }

    fun part2() {
        readData("day18.txt")
            .pairPermutations()
            .fold(0) { largestMagnitude, numbers ->
                max(
                    (numbers.first + numbers.second).reduce().magnitude(),
                    largestMagnitude
                )
            }
            .also { println(it) }
    }

    private fun Number.reduce(): Number = applyReduction()
        .let { reduced ->
            if (reduced != this) {
                reduced.reduce()
            } else {
                reduced
            }
        }

    private fun Number.applyReduction(): Number {
        val exploded = explode()
        return if (exploded != this) {
            exploded
        } else {
            split()
        }
    }

    interface Number {
        fun explode(): Number
        fun explode2(
            level: Int = 1
        ): Triple<Number, RegularNumber?, RegularNumber?>

        fun split(): Number
        fun magnitude(): Int

        fun addToRightMostRegularNumber(number: RegularNumber?): Number
        fun addToLeftMostRegularNumber(number: RegularNumber?): Number
        operator fun plus(number: Number): Number
    }

    data class RegularNumber(val value: Int) : Number {
        companion object {
            val ZERO = RegularNumber(0)
        }

        override fun explode(): Number = explode2().first
        override fun explode2(level: Int): Triple<Number, RegularNumber?, RegularNumber?> = Triple(this, null, null)

        override fun split(): Number = when {
            value >= 10 -> PairNumber(RegularNumber(value / 2), RegularNumber(ceil(value.div(2.0)).toInt()))
            else -> this
        }

        override fun magnitude(): Int = value

        override fun addToRightMostRegularNumber(number: RegularNumber?): Number = number?.let { this + it } ?: this
        override fun addToLeftMostRegularNumber(number: RegularNumber?): Number = number?.let { this + it } ?: this

        override fun plus(number: Number) =
            if (number is RegularNumber) RegularNumber(number.value + value) else throw IllegalArgumentException("Not a regular number $number")

        override fun toString(): String = "$value"
    }

    data class PairNumber(val left: Number, val right: Number) : Number {
        override fun explode(): Number = explode2().first

        override fun explode2(level: Int): Triple<Number, RegularNumber?, RegularNumber?> = when (level) {
            5 -> {
                Triple(
                    RegularNumber.ZERO,
                    left as RegularNumber,
                    right as RegularNumber
                )
            }
            else -> {
                val explodedLeft = left.explode2(level + 1)
                    .let {
                        Triple(
                            PairNumber(it.first, right.addToLeftMostRegularNumber(it.third)),
                            it.second,
                            null
                        )
                    }

                if (explodedLeft.first != this) {
                    explodedLeft
                } else {
                    right.explode2(level + 1)
                        .let {
                            Triple(
                                PairNumber(left.addToRightMostRegularNumber(it.second), it.first),
                                null,
                                it.third
                            )
                        }
                }
            }
        }

        override fun split(): Number {
            val splitLeft = left.split()
            return if (splitLeft != left) {
                PairNumber(splitLeft, right)
            } else {
                PairNumber(left, right.split())
            }
        }

        override fun magnitude(): Int = 3 * left.magnitude() + 2 * right.magnitude()

        override fun addToRightMostRegularNumber(number: RegularNumber?): Number =
            number?.let { PairNumber(left, right.addToRightMostRegularNumber(number)) } ?: this

        override fun addToLeftMostRegularNumber(number: RegularNumber?): Number =
            number?.let { PairNumber(left.addToLeftMostRegularNumber(it), right) } ?: this

        override fun plus(number: Number): Number =
            if (number is PairNumber)
                PairNumber(this, number)
            else
                throw IllegalArgumentException("Not a pair number $number")

        override fun toString(): String = "[$left, $right]"
    }

    private fun readData(file: String) = read(file)
        .map { parseNumber(it) }

    private fun parseNumber(number: String): Number = when {
        number.first() == '[' -> split(number.drop(1).dropLast(1))
            .let { PairNumber(parseNumber(it.first), parseNumber(it.second)) }
        number.first().isDigit() -> RegularNumber(number.toInt())
        else -> throw IllegalArgumentException("Don't know what to do with $number")
    }

    private fun split(input: String, depth: Int = 0, splitAt: Int = 0): Pair<String, String> {
        return when {
            depth == 0 && input[splitAt] == ',' -> input.substring(0, splitAt) to input.substring(splitAt + 1)
            else -> when (input[splitAt]) {
                '[' -> split(input, depth + 1, splitAt + 1)
                ']' -> split(input, depth - 1, splitAt + 1)
                else -> split(input, depth, splitAt + 1)
            }
        }
    }
}