package com.arclights

data class Coord(val x: Int, val y: Int)

fun <T> List<T>.pairPermutations(): List<Pair<T, T>> = flatMap { item -> minus(item).map { item to it } }