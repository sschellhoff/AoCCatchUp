package de.sschellhoff.aoc2021

import de.sschellhoff.utils.Day
import de.sschellhoff.utils.NTuple3
import de.sschellhoff.utils.then

class Day24: Day<Long, Long>(24, 2021, 1L, 1L) {
    override fun part1(input: String, isTest: Boolean): Long {
        if (isTest)
            return 1
        return solve(input, false)
    }

    override fun part2(input: String, isTest: Boolean): Long {
        if (isTest)
            return 1
        return solve(input, true)
    }

    private fun solve(input: String, isPart2: Boolean): Long {
        val ns = mutableListOf<NTuple3<Boolean, Int, Int>>()
        input.lines().windowed(18, 18).forEach { elements ->
            ns.add(elements[4].endsWith("26") then elements[5].split(' ').last().toInt() then elements[15].split(' ').last().toInt())
        }
        val stack = mutableListOf<Pair<Int, Int>>()
        val solution = mutableListOf<Int>(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
        ns.forEachIndexed { i , n ->
            val inp = "I$i"
            val incX = n.t2
            val incY = n.t3
            if (n.t1) {
                val p = stack.removeLast()
                val i2 = p.first
                val a = p.second + incX
                val n2s = (1..9).filter { (it + a) in (1..9) }
                val n2 = if (isPart2) {
                    n2s.min()
                } else {
                    n2s.max()
                }
                solution[i2] = n2
                solution[i] = n2 + a
            } else {
                stack.add(i to incY)
                //println("push($inp + $incY)")
            }
        }
        return solution.joinToString("").toLong()
    }

    class ALU(private val input: String) {
        private var pInput = 0
        private val registers = mutableListOf<Long>(0, 0, 0, 0)

        fun execute(command: String) {
            val split = command.split(" ")
            when (split.first()) {
                "inp" -> inp(split[1]).also { check(split.size == 2) }
                "add" -> add(split[1], split[2]).also { check(split.size == 3) }
                "mul" -> mul(split[1], split[2]).also { check(split.size == 3) }
                "div" -> div(split[1], split[2]).also { check(split.size == 3) }
                "mod" -> mod(split[1], split[2]).also { check(split.size == 3) }
                "eql" -> eql(split[1], split[2]).also { check(split.size == 3) }
                else -> throw IllegalArgumentException(command)
            }
        }

        fun print() {
            println(registers)
        }

        private fun getAddress(p: String): Int = when (p) {
            "w" -> 0
            "x" -> 1
            "y" -> 2
            "z" -> 3
            else -> throw IllegalArgumentException("'$p'")
        }

        private fun set(p: String, value: Long) {
            registers[getAddress(p)] = value
        }

        private fun get(pv: String): Long = when (pv) {
            "w" -> registers[0]
            "x" -> registers[1]
            "y" -> registers[2]
            "z" -> registers[3]
            else -> pv.toLong()
        }

        private fun inp(p: String) {
            set(p, input[pInput++].digitToInt().toLong())
        }

        private fun add(a: String, b: String) {
            set(a, get(a) + get(b))
        }

        private fun mul(a: String, b: String) {
            set(a, get(a) * get(b))
        }

        private fun div(a: String, b: String) {
            set(a, get(a) / get(b))
        }

        private fun mod(a: String, b: String) {
            set(a, get(a) % get(b))
        }

        private fun eql(a: String, b: String) {
            set(a, if (get(a) == get(b)) 1 else 0)
        }
    }
}