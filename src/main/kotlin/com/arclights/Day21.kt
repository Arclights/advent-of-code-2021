package com.arclights

import com.arclights.Day21.part2
import kotlin.system.exitProcess

fun main() {
    part2()
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

    fun part2() {
        val (player1, player2) = readData("day21ex.txt")
        println(player1)
        println(player2)
        playQuantum(player1, player2)
            .also { println(it.second) }
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

//    private fun playQuantum(
//        playerToPlay: Player,
//        otherPlayer: Player,
//        depth: Int = 0,
////        winnerLookup: MutableMap<Pair<Int, Int>, Map<String, Long>> = mutableMapOf()
//        winnerLookup: MutableMap<LookupKey, Map<String, Long>> = mutableMapOf()
////    ): Pair<MutableMap<Pair<Int, Int>, Map<String, Long>>, Map<String, Long>> = when {
//    ): Pair<MutableMap<LookupKey, Map<String, Long>>, Map<String, Long>> = when {
//        playerToPlay.score >= 21 -> winnerLookup to mapOf(playerToPlay.name to 1L, otherPlayer.name to 0L).also { println("${playerToPlay.name} winning at $depth") }
//        otherPlayer.score >= 21 -> winnerLookup to mapOf(playerToPlay.name to 0L, otherPlayer.name to 1L).also { println("${otherPlayer.name} winning at $depth") }
//        else -> (1..3).fold(winnerLookup to mapOf(playerToPlay.name to 0, otherPlayer.name to 0)) { state1, roll1 ->
//            (1..3).fold(state1) { state2, roll2 ->
//                (1..3).fold(state2) { state3, roll3 ->
//                    val (updatedLookup, updatedWinnings) = state3
//                    val score = roll1 + roll2 + roll3
////                    if (depth==7) exitProcess(1)
//                    println("depth: $depth\tscore:$score")
////                    winnerLookup[score to depth]?.let { winnerLookup to it }?.also { println("Hit score=$score depth=$depth: ${it.second}") }?:
//                    winnerLookup[LookupKey(score , depth,playerToPlay.score,otherPlayer.score)]?.let { winnerLookup to it }?.also { println("Hit score=$score depth=$depth: ${it.second}") }?:
//                    (playQuantum(
//                        otherPlayer,
//                        playerToPlay.moveTo(getRelativePosition(playerToPlay.position, score)),
//                        depth + 1,
//                        updatedLookup
//                    ).let { (updatedLookup, winnings) ->
//                        updatedLookup to updatedWinnings.merge(winnings)
//                    }.also { (updatedMap, winnings) ->
////                        updatedMap[score to depth] = winnings
//                        updatedMap[LookupKey(score , depth,playerToPlay.score,otherPlayer.score)] = winnings
//                    })
////                        .also { println("Aggregated: ${it.first}")}
//                }
//            }
//        }
//    }

    private fun playQuantum(
        playerToPlay: Player,
        otherPlayer: Player,
        depth: Int = 0,
//        winnerLookup: MutableMap<Pair<Int, Int>, Map<String, Long>> = mutableMapOf()
        winnerLookup: MutableMap<LookupKey2, Map<String, Long>> = mutableMapOf()
//    ): Pair<MutableMap<Pair<Int, Int>, Map<String, Long>>, Map<String, Long>> = when {
    ): Pair<MutableMap<LookupKey2, Map<String, Long>>, Map<String, Long>> = when {
        playerToPlay.score >= 21 -> winnerLookup to mapOf(playerToPlay.name to 1L, otherPlayer.name to 0L).also { println("${playerToPlay.name} winning at $depth") }
        otherPlayer.score >= 21 -> winnerLookup to mapOf(playerToPlay.name to 0L, otherPlayer.name to 1L).also { println("${otherPlayer.name} winning at $depth") }
        else -> winnerLookup[LookupKey2(depth,playerToPlay.score,otherPlayer.score)]?.let { winnerLookup to it }?.also { println("Hit depth=$depth: ${it.second}") }?:
        ((1..3).fold(winnerLookup to mapOf(playerToPlay.name to 0L, otherPlayer.name to 0L)) { state1, roll1 ->
            (1..3).fold(state1) { state2, roll2 ->
                (1..3).fold(state2) { state3, roll3 ->
                    val (updatedLookup, updatedWinnings) = state3
                    val score = roll1 + roll2 + roll3
//                    if (depth==7) exitProcess(1)
                    println("depth: $depth\tscore:$score")
                    playQuantum(
                        otherPlayer,
                        playerToPlay.moveTo(getRelativePosition(playerToPlay.position, score)),
                        depth + 1,
                        updatedLookup
                    ).let { (updatedLookup, winnings) ->
                        updatedLookup to updatedWinnings.merge(winnings)
                    }
//                        .also { println("Aggregated: ${it.first}")}
                }
            }
        }.also { (updatedMap, winnings) ->
//                        updatedMap[score to depth] = winnings
            updatedMap[LookupKey2(depth,playerToPlay.score,otherPlayer.score)] = winnings
        }
                )
    }

    data class LookupKey(val score:Int, val depth: Int, val playingPlayerScore:Int, val otherPlayerScore:Int)
    data class LookupKey2(val depth: Int, val playingPlayerScore:Int, val otherPlayerScore:Int)

    private fun Map<String, Long>.merge(m: Map<String, Long>) =
        mapValues { (playerName, winning) -> winning + m.getValue(playerName) }

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
    private fun Int.modTo1(other: Int): Int =
        if (this >= other) (this.mod(other) + (this / other)).modTo1(other) else this

    fun readData(file: String) = read(file).let {
        Player("Player 1", it[0].split(" ").last().toInt()) to Player("Player 2", it[1].split(" ").last().toInt())
    }
}