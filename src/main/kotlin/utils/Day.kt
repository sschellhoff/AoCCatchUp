package de.sschellhoff.utils

import java.io.File
import kotlin.time.measureTimedValue

abstract class Day<R1, R2>(private val day: Int, private val year: Int, private val testResultPart1: R1, private val testResultPart2: R2, private val testInputSuffixPart1: String = "", private val testInputSuffixPart2: String = "") {
    enum class RunMode {
        REAL, TEST, BOTH
    }
    enum class Part(val toInt: Int) {
        One(1), Two(2)
    }
    fun run(runMode: RunMode = RunMode.REAL) {
        val dayAsString = day.toString().padStart(2, '0')

        val resetColor = "\u001b[0m"
        val bold = "\u001b[1m"
        println("$bold--==:: Day $day ::==--$resetColor")
        runPart(runMode, dayAsString, Part.One)
        println("--------------------")
        runPart(runMode, dayAsString, Part.Two)
        println()
    }

    private fun runPart(runMode: RunMode, dayAsString: String, part: Part) {
        val yellowColor = "\u001B[33m"
        val resetColor = "\u001b[0m"
        if (runMode == RunMode.TEST || runMode == RunMode.BOTH) {
            val testInput = getInput(dayAsString, year, true, if (part == Part.One) testInputSuffixPart1 else testInputSuffixPart2)
            val (testResult, duration) = measureTimedValue {
                if (part == Part.One) part1(testInput, true) else part2(testInput, true)
            }
            printResult(testResult, if (part == Part.One) testResultPart1 else testResultPart2, part.toInt, true)
            println("$yellowColor$duration$resetColor")
        }

        if (runMode == RunMode.REAL || runMode == RunMode.BOTH) {
            val realInput = getInput(dayAsString, year)
            val (realResult, duration) = measureTimedValue {
                if (part == Part.One) part1(realInput, false) else part2(realInput, false)
            }
            printResult(realResult, null, part.toInt, false)
            println("$yellowColor$duration$resetColor")
        }
    }

    private fun printResult(result: Any?, testAgainst: Any?, part: Int, isTest: Boolean) {
        val greenColor = "\u001b[32m"
        val redColor = "\u001b[31m"
        val resetColor = "\u001b[0m"
        val printColor = when {
            testAgainst == null -> resetColor
            result == testAgainst -> greenColor
            else -> redColor
        }
        println("part $part${if (isTest) " (test)" else ""}: $printColor$result$resetColor")
    }

    protected abstract fun part1(input: String, isTest: Boolean): R1

    protected abstract fun part2(input: String, isTest: Boolean): R2
}

private fun getInput(day: String, year: Int, test: Boolean = false, suffix: String = "") = File("src/main/resources/Year$year/Day$day${if (test) "_test" else ""}$suffix.txt").readText()
