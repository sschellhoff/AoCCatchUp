package de.sschellhoff.aoc2019

import de.sschellhoff.utils.Day

fun main() {
    Day05().run(Day.RunMode.BOTH)
}

class Day05: Day<Int, Int>(5, 2019, 15314507, 652726) {
    override fun part1(input: String, isTest: Boolean): Int {
        val program = input.toIntCode()
        val vm = IntCodeVM()
        vm.run(program, intCodeInputOf(1))
        val output = vm.output()
        println(output)
        return output.last()
    }

    override fun part2(input: String, isTest: Boolean): Int {
        val program = input.toIntCode()
        val vm = IntCodeVM()
        vm.run(program, intCodeInputOf(5))
        val output = vm.output()
        println(output)
        return output.last()
    }
}