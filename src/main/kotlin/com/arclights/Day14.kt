package com.arclights

import com.arclights.Day14.part1

fun main() {
    part1()
}

object Day14 {
    fun part1() {
        val (template, insertions) = readData("day14.txt")
        println(template)
        println(insertions)
        insert(template, insertions, iterations = 40)
            .first
            .let { it.plus(template.first() to (it[template.first()] ?: 0L) + 1L) }
            .map { it.value }
            .let {
                val max = it.maxOrNull()!!
                val min = it.minOrNull()!!
                max - min
            }
            .also { println(it) }
    }

    private fun insert(
        template: String,
        insertions: Map<String, String>,
        iterations: Int = 10,
        iteration: Int = 0,
        elementCountLookup: Map<Pair<String, Int>, Map<Char, Long>> = mapOf()
    ): Pair<Map<Char, Long>, Map<Pair<String, Int>, Map<Char, Long>>> = when (iteration) {
        iterations -> getElementCount(template.drop(1)) to elementCountLookup
        else -> {
            template
                .windowed(2)
                .fold(mapOf<Char, Long>() to elementCountLookup) { (count, updatedLookup), pair ->
                    lookupOrInsert(pair, iteration, insertions, iterations, updatedLookup)
                        .let { (elementCount, updatedLookup) ->
                            count.merge(elementCount) to updatedLookup.update(
                                pair,
                                iteration,
                                elementCount
                            )
                        }
                }
        }
    }

    private fun lookupOrInsert(
        pair: String,
        iteration: Int,
        insertions: Map<String, String>,
        iterations: Int,
        lookup: Map<Pair<String, Int>, Map<Char, Long>>
    ) = lookup[pair to iteration]?.let { it to lookup }
        ?: mutate(pair, insertions)
            .let { mutatedPair ->
                insert(
                    mutatedPair,
                    insertions,
                    iterations,
                    iteration + 1,
                    lookup
                )
            }

    private fun getElementCount(template: String) = template.groupBy { it }.mapValues { it.value.size.toLong() }

    private fun Map<Char, Long>.merge(m: Map<Char, Long>) = (keys + m.keys).associateWith {
        listOfNotNull(
            this[it],
            m[it]
        ).reduce(Long::plus)
    }

    private fun Map<Pair<String, Int>, Map<Char, Long>>.update(
        pair: String,
        iteration: Int,
        elementCount: Map<Char, Long>
    ) = plus((pair to iteration) to elementCount)

    private fun mutate(pair: String, insertions: Map<String, String>) =
        insertions[pair]?.let { toInsert -> "${pair[0]}$toInsert${pair[1]}" } ?: pair

    private fun readData(file: String) = read(file).let { data ->
        val template = data.first()
        val insertions = data
            .drop(2)
            .map { it.split(" -> ") }
            .associate { it[0] to it[1] }

        template to insertions
    }
}