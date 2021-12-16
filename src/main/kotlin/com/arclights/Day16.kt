package com.arclights

import com.arclights.Day16.part2
import kotlin.math.pow

fun main() {
    part2()
}

object Day16 {
    fun part1() {
        val input = read("day16.txt")[0]
        hexToBinary(input)
            .let { parseData(it) }
            .also { println(it.sumVersions()) }
    }

    fun part2() {
        val input = read("day16.txt")[0]
        hexToBinary(input)
            .let { parseData(it) }
            .also { println(it.calculate()) }
    }

    private fun parseData(data: String) = readPackage(data).first

    private fun readPackage(data: String): Pair<Package, String> {
        val version = binaryToDecimal(data.take(3)).toInt()
        val id = binaryToDecimal(data.drop(3).take(3)).toInt()
        val packageData = data.drop(6)
        return when (id) {
            4 -> readLiteral(packageData).letFirst { binaryToDecimal(it) }.letFirst { Literal(version, it) }
            else -> readOperator(packageData).letFirst { Operator(version, id, it) }
        }
    }

    private tailrec fun readLiteral(data: String, literal: String = ""): Pair<String, String> = when (data.first()) {
        '0' -> literal + data.drop(1).take(4) to data.drop(5)
        else -> readLiteral(data.drop(5), literal + data.drop(1).take(4))
    }

    private fun readOperator(input: String): Pair<List<Package>, String> = when (input.first()) {
        '0' -> readOperatorByLength(
            binaryToDecimal(input.drop(1).take(15)).toInt(),
            input.drop(16)
        )
        else -> readOperatorByNbrOfPackages(binaryToDecimal(input.drop(1).take(11)), input.drop(12))
    }

    private fun readOperatorByLength(length: Int, input: String): Pair<List<Package>, String> =
        readOperatorUntilEnd(input.take(length)).letSecond { input.drop(length) }

    private tailrec fun readOperatorUntilEnd(
        input: String,
        packages: List<Package> = listOf()
    ): Pair<List<Package>, String> = when {
        input.isEmpty() -> (packages to input)
        else -> {
            val (pack, remainingInput) = readPackage(input)
            readOperatorUntilEnd(remainingInput, packages.plus(pack))
        }
    }

    private fun readOperatorByNbrOfPackages(nbrOfPackages: Long, input: String): Pair<List<Package>, String> =
        (0 until nbrOfPackages)
            .fold(listOf<Package>() to input) { (packages, restOfInput), _ ->
                readPackage(restOfInput)
                    .letFirst { packages.plus(it) }
            }

    interface Package {
        fun sumVersions(): Int
        fun calculate(): Long
    }

    data class Literal(val version: Int, val value: Long) : Package {
        override fun sumVersions(): Int = version
        override fun calculate(): Long = value
    }

    data class Operator(val version: Int, val id: Int, val subPackages: List<Package>) : Package {
        override fun sumVersions(): Int = version + subPackages.sumOf { it.sumVersions() }
        override fun calculate(): Long = subPackages
            .map { it.calculate() }
            .let { calculatedValues ->
                with(calculatedValues) {
                    when (id) {
                        0 -> sum()
                        1 -> reduce(Long::times)
                        2 -> minOf { it }
                        3 -> maxOf { it }
                        5 -> if (get(0) > get(1)) 1 else 0
                        6 -> if (get(0) < get(1)) 1 else 0
                        7 -> if (get(0) == get(1)) 1 else 0
                        else -> throw IllegalStateException("Unknown id $id")
                    }
                }
            }
    }

    private fun hexToBinary(input: String) = input
        .map {
            when (it) {
                '0' -> "0000"
                '1' -> "0001"
                '2' -> "0010"
                '3' -> "0011"
                '4' -> "0100"
                '5' -> "0101"
                '6' -> "0110"
                '7' -> "0111"
                '8' -> "1000"
                '9' -> "1001"
                'A' -> "1010"
                'B' -> "1011"
                'C' -> "1100"
                'D' -> "1101"
                'E' -> "1110"
                'F' -> "1111"
                else -> throw IllegalArgumentException("Unknown char $it")
            }
        }
        .joinToString(separator = "")

    private fun binaryToDecimal(input: String) = input
        .map { it.toString().toInt() }
        .reversed()
        .mapIndexed { i, d -> d * 2.0.pow(i) }
        .sum()
        .toLong()

    private fun <A, B, C> Pair<A, B>.letFirst(letter: (A) -> C): Pair<C, B> = letter(first) to second
    private fun <A, B, C> Pair<A, B>.letSecond(letter: (B) -> C): Pair<A, C> = first to letter(second)
}