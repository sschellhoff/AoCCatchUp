package de.sschellhoff.aoc2019

import de.sschellhoff.utils.Day

fun main() {
    Day02().run(Day.RunMode.BOTH)
}

class Day02: Day<Int, Int>(2, 2019, 2, 1) {
    override fun part1(input: String, isTest: Boolean): Int {
        val vm = IntCodeVM()
        val program = input.parse().toMutableList()
        if (!isTest) {
            program[1] = 12
            program[2] = 2
        }
        return vm.run(program)
    }

    override fun part2(input: String, isTest: Boolean): Int {
        val vm = IntCodeVM()
        val program = input.parse().toMutableList()
        if (isTest) {
            return 1
        }
        (0..99).forEach { noun ->
            (0..99).forEach { verb ->
                program[1] = noun
                program[2] = verb
                if (vm.run(program) == 19690720) {
                    return 100 * noun + verb
                }
            }
        }
        return -1
    }

    private fun String.parse(): List<Int> = split(",").map { it.toInt() }
}