package de.sschellhoff.aoc2019

import de.sschellhoff.utils.Day

class Day04: Day<Int, Int>(4, 2019, 369, 288) {
    override fun part1(input: String, isTest: Boolean): Int {
        return input.parse().count { test(it) }
    }

    override fun part2(input: String, isTest: Boolean): Int {
        return input.parse().count { test2(it) }
    }

    private fun test(i: Long): Boolean {
        var valid = false
        "$i".zipWithNext { a, b ->
            if (b < a) return false
            if (a == b) valid = true
        }
        return valid
    }

    private fun test2(i: Long): Boolean {
        if (!test(i))
            return false
        var valid = false
        "$i".windowed(4, 1) { ns ->
            val (a, b, c, d) = ns.toList()
            if (a != b && b == c && c != d) valid = true
        }
        if (!valid) {
            val s = i.toString()
            return (s[0] == s[1] && s[1] != s[2]) || (s[s.length-1] == s[s.length-2] && s[s.length-2] != s[s.length-3])
        }
        return true
    }

    private fun String.parse(): LongRange = split("-").let { (s, e) -> s.toLong()..e.toLong() }
}