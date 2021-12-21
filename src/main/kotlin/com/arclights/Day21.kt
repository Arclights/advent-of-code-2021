package com.arclights

import com.arclights.Day21.part1

fun main() {
    part1()
}

object Day21 {
    fun part1() {
        val (player1, player2) = readData("day21.txt")
        println(player1)
        println(player2)
        play(player1, player2)
            .also { println(it) }
            .let { listOf(it.first, it.second).minOf { it.score } * it.third.nbrOfRolls() }
            .also { println(it) }
    }

    private tailrec fun play(
        playerToPlay: Player,
        otherPlayer: Player,
        die: Die = Die()
    ): Triple<Player, Player, Die> = when {
        maxOf(playerToPlay.score, otherPlayer.score) >= 1000 -> Triple(playerToPlay, otherPlayer, die)
        else -> {
            val rollSum = (1..3).sumOf { die.roll() }
//            println("Roll sum: $rollSum")
            play(otherPlayer, playerToPlay.moveTo(getRelativePosition(playerToPlay.position, rollSum)), die)
        }
    }

    private fun getRelativePosition(position: Int, steps: Int): Int = (position + steps).modTo1(11)

    class Die(
        private var generator: Sequence<Int> = generateSequence(1) { (it + 1).modTo1(101) },
        private var counter: Int = 0
    ) {
        fun roll(): Int = generator.first().also {
//            println("Roll: $it")
            generator = generator.drop(1)
            counter += 1
        }

        fun nbrOfRolls() = counter
        override fun toString(): String = counter.toString()
    }

    data class Player(val name: String, val position: Int = 0, val score: Int = 0) {
        fun moveTo(position: Int) = Player(name, position, score + position)
//            .also { println("${it.name} moves to space $position for a total score of ${it.score}") }
    }

//    private fun Int.modTo1(other: Int) = if (this >= other) this.mod(other) + 1 else this
    private fun Int.modTo1(other: Int):Int = if (this >= other) (this.mod(other) + (this/other)).modTo1(other) else this

    fun readData(file: String) = read(file).let {
        Player("Player 1", it[0].split(" ").last().toInt()) to Player("Player 2", it[1].split(" ").last().toInt())
    }
}