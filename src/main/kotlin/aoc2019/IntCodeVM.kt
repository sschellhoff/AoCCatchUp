package de.sschellhoff.aoc2019

class IntCodeVM {
    private var program = mutableListOf<Int>()
    private var ip: Int = 0
    private var input: IntCodeInput = emptyIntCodeInput()
    private val outputBuffer: MutableList<Int> = mutableListOf()

    fun output(): List<Int> = outputBuffer

    fun run(program: List<Int>, input: IntCodeInput = emptyIntCodeInput()): Int {
        this.program = program.toMutableList()
        ip = 0
        this.input = input
        outputBuffer.clear()

        while (true) {
            val opCode = fetch()
            when (val r = execute(opCode)) {
                is CycleResult.Halt -> return this.program.first()
                is CycleResult.Jump2 -> ip += 2
                is CycleResult.Jump3 -> ip += 3
                is CycleResult.Jump4 -> ip += 4
                is CycleResult.JumpTo -> ip = r.ip
            }
        }
    }

    private fun fetch(): Int = program[ip] % 100

    private fun execute(opCode: Int): CycleResult = when(opCode) {
        99 -> CycleResult.Halt
        1 -> addition()
        2 -> multiplication()
        3 -> readInput()
        4 -> writeOutput()
        5 -> jumpIfTrue()
        6 -> jumpIfFalse()
        7 -> execLessThan()
        8 -> execEquals()
        else -> throw IllegalStateException("$opCode at $ip")
    }

    private fun addition(): CycleResult.Jump4 {
        val a = read(parameter(1))
        val b = read(parameter(2))
        val c = parameter(3)
        set(c, a + b)
        return CycleResult.Jump4
    }

    private fun multiplication(): CycleResult.Jump4 {
        val a = read(parameter(1))
        val b = read(parameter(2))
        val c = parameter(3)
        set(c, a * b)
        return CycleResult.Jump4
    }

    private fun readInput(): CycleResult.Jump2 {
        val a = parameter(1)
        set(a, input.read())
        return CycleResult.Jump2
    }

    private fun writeOutput(): CycleResult.Jump2 {
        val a = parameter(1)
        outputBuffer.add(read(a))
        return CycleResult.Jump2
    }

    private fun jumpIfTrue(): CycleResult = if (read(parameter(1)) != 0) {
        CycleResult.JumpTo(read(parameter(2)))
    } else {
        CycleResult.Jump3
    }

    private fun jumpIfFalse(): CycleResult = if (read(parameter(1)) == 0) {
        CycleResult.JumpTo(read(parameter(2)))
    } else {
        CycleResult.Jump3
    }

    private fun execLessThan(): CycleResult.Jump4 {
        val a = read(parameter(1))
        val b = read(parameter(2))
        val c = parameter(3)
        val value = if (a < b) 1 else 0
        set(c, value)
        return CycleResult.Jump4
    }


    private fun execEquals(): CycleResult {
        val a = read(parameter(1))
        val b = read(parameter(2))
        val c = parameter(3)
        val value = if (a == b) 1 else 0
        set(c, value)
        return CycleResult.Jump4
    }

    private fun operand(n: Int): Int = (ip + n).also { require(n in 1..3) }

    private fun read(p: Int): Int {
        return program[p]
    }

    private fun set(p: Int, value: Int) {
        program[p] = value
    }

    sealed interface CycleResult {
        data object Halt: CycleResult
        data object Jump4: CycleResult
        data object Jump2: CycleResult
        data object Jump3: CycleResult
        data class JumpTo(val ip: Int): CycleResult
    }

    private fun parameterMode(n: Int): Int = when(n) {
        1 -> 100
        2 -> 1000
        3 -> 10000
        else -> throw IllegalArgumentException()
    }.let { ((read(ip) % (it * 10)) / it) }

    private fun parameter(n: Int): Int = when(parameterMode(n)) {
        0 -> read(operand(n))
        1 -> operand(n)
        else -> throw IllegalArgumentException("Illegal parametermode '${parameterMode(n)}'")
    }
}

interface IntCodeInput {
    fun read(): Int
}

fun emptyIntCodeInput(): IntCodeInput = IntCodeListInput(emptyList())

data class IntCodeListInput(private val data: List<Int>): IntCodeInput {
    private var p = 0

    override fun read(): Int = data[p++]
}

fun intCodeInputOf(vararg data: Int): IntCodeInput = IntCodeListInput(data.toList())

fun String.toIntCode(): List<Int> = split(",").map { it.toInt() }