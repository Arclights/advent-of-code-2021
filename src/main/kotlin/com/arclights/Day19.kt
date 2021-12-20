package com.arclights

import com.arclights.Day19.part1

fun main() {
    part1()
}

object Day19 {
    fun part1() {
//        val data = readData("day19ex_2.txt")[0]
//        println(data)
//        println(data.getOrientations().also { println(it.size) })
//            .joinToString(separator = "\n") { "Scanner ${it.nbr}\n${it.beacons.joinToString(separator = "\n") { "${it.x},${it.y},${it.z}" }}" })


//        val coords1 = """
//-618,-824,-621
//-537,-823,-458
//-447,-329,318
//404,-588,-901
//544,-627,-890
//528,-643,409
//-661,-816,-575
//390,-675,-793
//423,-701,434
//-345,-311,381
//459,-707,401
//-485,-357,347
//        """.trimIndent()
//            .trim()
//            .split("\n")
//            .let { parseCoords(it) }
//        println(coords1.getRelativeDistances())
//        val coord1RelDists = coords1.getRelativeDistances()
//
//        val coords2 = """
//                    686,422,578
//                    605,423,415
//                    515,917,-361
//                    -336,658,858
//                    -476,619,847
//                    -460,603,-452
//                    729,430,532
//                    -322,571,750
//                    -355,545,-477
//                    413,935,-424
//                    -391,539,-444
//                    553,889,-390
//                """.trimIndent()
//            .trim()
//            .split("\n")
//            .let { parseCoords(it) }
//        println(coords2.getRelativeDistances())
//        val coord2RelDists = coords2.getRelativeDistances()

        val data = readData("day19ex_6.txt")
        val scanner0 = data[0]
        val scanner1 = data[1]
        val scanner0Orientations = scanner0.getOrientations()
        val scanner1Orientations = scanner1.getOrientations()
        val scanner0RelDists = scanner0Orientations.map { it.beacons.getRelativeDistances() }
        val scanner1RelDists = scanner1Orientations.map { it.beacons.getRelativeDistances() }
        scanner0RelDists.flatMap { s0rd->scanner1RelDists.map { s1rd-> s0rd to s1rd} }
            .also { println(it.size) }
            .map { it.first.plus(it.second).groupBy { it.second }.filter { it.value.size >= 2 } }
            .filter { it.size>=12 }
//            .also { println(it) }
            .also { println(it.size) }
            .map { it.flatMap { it.value.flatMapIndexed{i,v-> listOf( i.toString() to v.first.first,i.toString() to v.first.second)} }.toSet().groupBy { it.first } }
            .also { println(it) }
            .also { it.forEach{ println("${it["0"]?.size} ${it["1"]?.size}")} }
//        scanner0RelDists.map { s0rd->scanner1RelDists.map { s1rd-> s0rd.plus(s1rd).groupBy { it.second }.filter { it.value.size >= 2 }}.filter { it.size>=12 } }.filter { it.isEmpty().not() }
//            .also { println(it) }
//            .also { println(it.size)}
//        scanner1.beacons.getRelativeDistances().plus(scanner0.beacons.getRelativeDistances()).groupBy { it.second }.filter { it.value.size >= 2 }
//            .also { println(it) }

//        println(coord1RelDists.plus(coord2RelDists).groupBy { it.second }.mapValues { it.value.size }
//            .filter { it.value != 1 })
    }

    private fun List<Coord3D>.getRelativeDistances() = pairPermutations()
        .map { (coord1, coord2) ->
            (coord1 to coord2) to
                    Triple(
                        coord1.x - coord2.x,
                        coord1.y - coord2.y,
                        coord1.z - coord2.z
                    )
        }

    private fun Scanner.getOrientations() = listOf(
        beacons/*.also { println("Original") }*/.getRotations(),
        beacons/*.also { println("1 left") }*/.map { Coord3D(it.z, it.y, it.x * -1) }.getRotations(),
        beacons/*.also { println("2 left") }*/.map { Coord3D(it.x * -1, it.y, it.z * -1) }.getRotations(),
        beacons/*.also { println("1 right") }*/.map { Coord3D(it.z * -1, it.y, it.x) }.getRotations(),
        beacons/*.also { println("up") }*/.map { Coord3D(it.x, it.z * -1, it.y) }.getRotations(),
        beacons/*.also { println("down") }*/.map { Coord3D(it.x, it.z, it.y * -1) }.getRotations(),
    )
        .flatten()
        .map { Scanner(nbr, it) }

    private fun List<Coord3D>.getRotations() = listOf(
        this,/*.also { println("\tOriginal") }.also { println(it.asString()) }*/
        map {
            Coord3D(
                it.y,
                it.x * -1,
                it.z
            )
        },//.also { println("\t90 counter clockwise") }.also { println(it.asString()) },
        map { Coord3D(it.x * -1, it.y * -1, it.z) },//.also { println("\t180") }.also { println(it.asString()) },
        map { Coord3D(it.y * -1, it.x, it.z) },//.also { println("\t90 clockwise") }.also { println(it.asString()) }
    )

    data class Scanner(val nbr: Int, val beacons: List<Coord3D>)
    data class Coord3D(val x: Int, val y: Int, val z: Int)

    fun readData(file: String) = read(file)
        .joinToString(separator = "\n")
        .split("\n\n")
        .mapIndexed { i, block ->
            block
                .split("\n")
                .drop(1)
                .let { parseCoords(it) }
                .let { Scanner(i, it) }
        }

    private fun parseCoords(coords: List<String>): List<Coord3D> = coords.map {
        it
            .split(",")
            .let { coord -> Coord3D(coord[0].toInt(), coord[1].toInt(), coord[2].toInt()) }
    }

    private fun List<Coord3D>.asString() = joinToString(separator = "\n") { "\t${it.x},${it.y},${it.z}" }
}