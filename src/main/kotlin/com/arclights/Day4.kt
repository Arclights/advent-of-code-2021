package com.arclights

import com.arclights.Day4.part2

fun main() {
    part2()
}

object Day4 {

    fun part1() {
        val (numbersToDraw, boards) = parseData("day4.txt")
        val (board, drawnNumbers) = drawNumberAndGetWinningBoard(boards, numbersToDraw, emptyList())
        val score = calculateScore(board, drawnNumbers)
        println(score)
    }

    fun part2() {
        val (numbersToDraw, boards) = parseData("day4.txt")
        val (board, drawnNumbers) = drawNumberAndGetLosingBoard(boards, numbersToDraw, emptyList())
        val score = calculateScore(board, drawnNumbers)
        println(score)
    }

    fun calculateScore(board: Board, drawnNumbers: List<Int>) = board.cells
        .map { it.number }
        .filter { drawnNumbers.contains(it).not() }
        .sum()
        .let { it * drawnNumbers.last() }

    tailrec fun drawNumberAndGetWinningBoard(
        boards: List<Board>,
        numbersToDraw: List<Int>,
        previouslyDrawnNumbers: List<Int>
    ): Pair<Board, List<Int>> =
        when {
            numbersToDraw.isEmpty() -> throw IllegalStateException("No more numbers to draw")
            else -> {
                val drawnNumbers = previouslyDrawnNumbers.plus(numbersToDraw.take(1))
                val winningBoard = boards.firstOrNull { boardWon(it, drawnNumbers) }
                when {
                    winningBoard != null -> winningBoard to drawnNumbers
                    else -> drawNumberAndGetWinningBoard(boards, numbersToDraw.drop(1), drawnNumbers)
                }
            }
        }

    tailrec fun drawNumberAndGetLosingBoard(
        boards: List<Board>,
        numbersToDraw: List<Int>,
        previouslyDrawnNumbers: List<Int>
    ): Pair<Board, List<Int>> =
        when {
            numbersToDraw.isEmpty() -> throw IllegalStateException("No more numbers to draw")
            else -> {
                val drawnNumbers = previouslyDrawnNumbers.plus(numbersToDraw.take(1))
                val losingBoards = boards.filter { boardWon(it, drawnNumbers).not() }
                val winningBoard = boards.firstOrNull { boardWon(it, drawnNumbers) }
                when {
                    losingBoards.isEmpty() && winningBoard != null -> winningBoard to drawnNumbers
                    else -> drawNumberAndGetLosingBoard(losingBoards, numbersToDraw.drop(1), drawnNumbers)
                }
            }
        }

    private fun boardWon(board: Board, drawnNumbers: List<Int>): Boolean {
        val checkedCells = board.cells.filter { drawnNumbers.contains(it.number) }
        return hasFullRow(checkedCells) || hasFullColumn(checkedCells)
    }

    private fun hasFullRow(cells: List<Cell>) = cells.groupBy { it.x }.filter { it.value.size == 5 }.isNotEmpty()

    private fun hasFullColumn(cells: List<Cell>) = cells.groupBy { it.y }.filter { it.value.size == 5 }.isNotEmpty()

    private fun parseData(file: String): Pair<List<Int>, List<Board>> {
        val data = read(file)
        val numbersToDraw = data[0].split(",").map { it.toInt() }
        val boards = parseBoards(data.drop(1), emptyList())
        return numbersToDraw to boards
    }

    private tailrec fun parseBoards(boardData: List<String>, boards: List<Board>): List<Board> =
        when {
            boardData.isEmpty() -> boards
            else -> parseBoards(boardData.drop(6),
                parseBoard(boardData.drop(1).take(5)).let { boards.plus(it) })
        }

    private fun parseBoard(boardData: List<String>): Board =
        boardData
            .flatMapIndexed { y, row ->
                row.trim()
                    .split("\\s+".toRegex())
                    .map { it.trim() }
                    .map { it.toInt() }
                    .toList()
                    .mapIndexed { x, number -> Cell(x, y, number) }
            }
            .let { Board(it) }

    data class Cell(val x: Int, val y: Int, val number: Int)
    data class Board(val cells: List<Cell>)
}