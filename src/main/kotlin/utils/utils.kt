package de.sschellhoff.utils

import java.io.File

fun getInput(day: String, test: Boolean = false, suffix: String = "") = File("src/main/resources/Day$day${if (test) "_test" else ""}$suffix.txt").readText()

fun Long.toIntOrThrow(): Int {
    check(this in (Int.MIN_VALUE..Int.MAX_VALUE))
    return toInt()
}