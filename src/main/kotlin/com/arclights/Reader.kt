package com.arclights

fun read(fileName: String) = object {}.javaClass.getResource("/$fileName")!!.readText().lines()