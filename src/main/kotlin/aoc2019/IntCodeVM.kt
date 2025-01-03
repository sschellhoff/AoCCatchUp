package de.sschellhoff.aoc2019

class IntCodeVM {
    private var program = mutableListOf<Int>()
    private var ip: Int = 0

    fun run(program: List<Int>): Int {
        this.program = program.toMutableList()
        ip = 0

        while (true) {
            val opCode = fetch()
            when (execute(opCode)) {
                is CycleResult.Halt -> return this.program.first()
                is CycleResult.Jump4Bit -> ip += 4
            }
        }
    }

    private fun fetch(): Int = program[ip]

    private fun execute(opCode: Int): CycleResult {
        if (opCode == 99) {
            return CycleResult.Halt
        }
        if (opCode == 1) {
            return addition()
        }
        if (opCode == 2) {
            return multiplication()
        }
        throw IllegalStateException(opCode.toString())
    }

    private fun addition(): CycleResult.Jump4Bit {
        val a = read(operand1())
        val b = read(operand2())
        val c = operand3()
        set(c, a + b)
        return CycleResult.Jump4Bit
    }

    private fun multiplication(): CycleResult.Jump4Bit {
        val a = read(operand1())
        val b = read(operand2())
        val c = operand3()
        set(c, a * b)
        return CycleResult.Jump4Bit
    }

    private fun operand1() = program[ip + 1]
    private fun operand2() = program[ip + 2]
    private fun operand3() = program[ip + 3]

    private fun read(p: Int): Int {
        return program[p]
    }

    private fun set(p: Int, value: Int) {
        program[p] = value
    }

    sealed interface CycleResult {
        data object Halt: CycleResult
        data object Jump4Bit: CycleResult
    }
}