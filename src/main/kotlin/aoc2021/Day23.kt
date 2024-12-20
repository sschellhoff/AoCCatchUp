package de.sschellhoff.aoc2021

import de.sschellhoff.utils.Day
import de.sschellhoff.utils.pathfinding.EdgeInfo
import de.sschellhoff.utils.pathfinding.aStar
import java.util.*

class Day23: Day<Long, Long>(23, 2021, 12521, 44169) {
    override fun part1(input: String, isTest: Boolean): Long = input.toState().solve()

    override fun part2(input: String, isTest: Boolean): Long = input.toState(true).solve()

    private fun State.solve(): Long {
        val endState = State.finished()
        val h = { state: State -> state.heuristic() }
        val isEnd = { it: State -> it == endState }
        val (_, score) = aStar(this, isEnd, h) { currentState ->
            currentState.getNextStates().map { (nextState, cost) ->
                EdgeInfo(currentState, nextState, cost.toLong())
            }
        } ?: throw IllegalStateException()
        return score
    }
}

private fun String.toState(isPartTwo: Boolean = false): State {
    val r = """[ABCD]""".toRegex()
    val matches = if (isPartTwo) {
        val m = r.findAll(this).toList().map { it.value }.toMutableList()
        m.addAll(4, "DCBADBAC".toList().map { it.toString() })
        m
    } else {
        r.findAll(this).toList().map { it.value } + "ABCDABCD".toList().map { it.toString() }
    }
    check(matches.size == 16)
    val state = State.empty()
    matches.forEachIndexed { i, c ->
        val amphipod = when (c) {
            "A" -> Amphipod.Amber
            "B" -> Amphipod.Bronze
            "C" -> Amphipod.Copper
            "D" -> Amphipod.Desert
            else -> throw IllegalStateException()
        }
        state.set(i + 7, amphipod)
    }
    return state
}

private enum class Amphipod {
    Amber,
    Bronze,
    Copper,
    Desert;
}

private fun Amphipod?.toChar() = when (this) {
    Amphipod.Amber -> 'A'
    Amphipod.Bronze -> 'B'
    Amphipod.Copper -> 'C'
    Amphipod.Desert -> 'D'
    null -> '.'
}

private fun Amphipod?.toCost() = when(this) {
    Amphipod.Amber -> 1L
    Amphipod.Bronze -> 10L
    Amphipod.Copper -> 100L
    Amphipod.Desert -> 1000L
    null -> 0L
}

private data class State(private val data: BitSet) {
    fun get(i: Int): Amphipod? {
        val bits = listOf(data.get(i * 4), data.get(i * 4 + 1), data.get(i * 4 + 2), data.get(i * 4 + 3))
        check(bits.count { it } <= 1)
        if (bits[0]) return Amphipod.Amber
        if (bits[1]) return Amphipod.Bronze
        if (bits[2]) return Amphipod.Copper
        if (bits[3]) return Amphipod.Desert
        return null
    }

    fun set(i: Int, amphipod: Amphipod?) {
        (0..<4).forEach { di ->
            data.clear(i * 4 + di)
        }
        when (amphipod) {
            Amphipod.Amber -> data.set(i * 4)
            Amphipod.Bronze -> data.set(i * 4 + 1)
            Amphipod.Copper -> data.set(i * 4 + 2)
            Amphipod.Desert -> data.set(i * 4 + 3)
            null -> {}
        }
    }

    fun move(from: Int, target: Int): State {
        val next = this.copy(data = data.clone() as BitSet)
        next.set(target, get(from))
        next.set(from, null)
        return next
    }

    fun isFree(vararg i: Int) = i.all { get(it) == null }

    fun getNextStates(): List<Pair<State, Int>> = getNextPaths().map { move(it.start, it.target) to it.costF() }

    fun heuristic(): Long {
        val fromRoom = listOf(wrongAmphipodsInRoomA(), wrongAmphipodsInRoomB(), wrongAmphipodsInRoomC(), wrongAmphipodsInRoomD()).mapNotNull { result -> result?.let { get(it.first).toCost() } }.sum()

        val fromHallway = amphipodsInHallway().sumOf {
            when (it.second) {
                Amphipod.Amber -> 1L
                Amphipod.Bronze -> 10L
                Amphipod.Copper -> 100L
                Amphipod.Desert -> 1000L
            }
        }
        return fromRoom + fromHallway
    }

    fun getNextPaths(): List<Path> {
        val pathsFromHallway = amphipodsInHallway().mapNotNull { (i, a) ->
            when (a) {
                Amphipod.Amber -> fromHallwayToRoomA(i)
                Amphipod.Bronze -> fromHallwayToRoomB(i)
                Amphipod.Copper -> fromHallwayToRoomC(i)
                Amphipod.Desert -> fromHallwayToRoomD(i)
            }
        }
        if (pathsFromHallway.isNotEmpty()) {
            return pathsFromHallway
        }
        val pathsToHallway = listOf(
            wrongAmphipodsInRoomA()?.let { fromRoomAToHallway(start = it.first, costOutOfRoom = it.second) },
            wrongAmphipodsInRoomB()?.let { fromRoomBToHallway(start = it.first, costOutOfRoom = it.second) },
            wrongAmphipodsInRoomC()?.let { fromRoomCToHallway(start = it.first, costOutOfRoom = it.second) },
            wrongAmphipodsInRoomD()?.let { fromRoomDToHallway(start = it.first, costOutOfRoom = it.second) }
        ).mapNotNull { it }.flatten()
        return pathsToHallway
    }

    fun wrongAmphipodsInRoomA(): Pair<Int, Int>? =
        wrongAmphipodsInRoom(listOf(7, 11, 15, 19), Amphipod.Amber)

    fun wrongAmphipodsInRoomB(): Pair<Int, Int>? =
        wrongAmphipodsInRoom(listOf(8, 12, 16, 20), Amphipod.Bronze)

    private fun wrongAmphipodsInRoomC(): Pair<Int, Int>? =
        wrongAmphipodsInRoom(listOf(9, 13, 17, 21), Amphipod.Copper)

    private fun wrongAmphipodsInRoomD(): Pair<Int, Int>? =
        wrongAmphipodsInRoom(listOf(10, 14, 18, 22), Amphipod.Desert)

    private fun wrongAmphipodsInRoom(indices: List<Int>, amphipod: Amphipod): Pair<Int, Int>? {
        indices.forEachIndexed { index, i ->
            val a = get(i)
            if (a == amphipod) {
                return if(foreignAmphipodsInRoom(indices, amphipod)) i to index + 2 else null
            }
            if (a != null) {
                return i to index + 2
            }
        }
        return null
    }

    private fun foreignAmphipodsInRoom(indices: List<Int>, amphipod: Amphipod): Boolean =
        indices.any { a -> get(a).let { it != null && it != amphipod }  }

    private fun fromRoomAToHallway(start: Int, costOutOfRoom: Int): List<Path> = (0..6).mapNotNull { hallwayIndex ->
       canGoIntoRoomA(hallwayIndex).let {
           if (it.first && get(hallwayIndex) == null) {
               Path(start = start, target = hallwayIndex, length = it.second + costOutOfRoom, get(start)!!)
           } else null
       }
   }

    private fun fromRoomBToHallway(start: Int, costOutOfRoom: Int): List<Path> = (0..6).mapNotNull { hallwayIndex ->
        canGoIntoRoomB(hallwayIndex).let {
            if (it.first && get(hallwayIndex) == null) {
                Path(start = start, target = hallwayIndex, length = it.second + costOutOfRoom, get(start)!!)
            } else null
        }
    }

    private fun fromRoomCToHallway(start: Int, costOutOfRoom: Int): List<Path> = (0..6).mapNotNull { hallwayIndex ->
        canGoIntoRoomC(hallwayIndex).let {
            if (it.first && get(hallwayIndex) == null) {
                Path(start = start, target = hallwayIndex, length = it.second + costOutOfRoom, get(start)!!)
            } else null
        }
    }

    private fun fromRoomDToHallway(start: Int, costOutOfRoom: Int): List<Path> = (0..6).mapNotNull { hallwayIndex ->
        canGoIntoRoomD(hallwayIndex).let {
            if (it.first && get(hallwayIndex) == null) {
                Path(start = start, target = hallwayIndex, length = it.second + costOutOfRoom, get(start)!!)
            } else null
        }
    }

    fun amphipodsInHallway(): List<Pair<Int, Amphipod>> {
        return (0..6).mapNotNull {
            val a = get(it)
            if (a != null) it to a else null
        }
    }

    private fun fromHallwayToRoomA(i: Int): Path? = canGoIntoRoomA(i).let {
        if (it.first) {
            val (index, length) = firstTargetInA() ?: return null
            Path(i, index, length + it.second, Amphipod.Amber)
        } else null
    }

    // (way free, way cost)
    private fun canGoIntoRoomA(hallwayIndex: Int): Pair<Boolean, Int> = when (hallwayIndex) {
        0 -> isFree(1) to 1
        1, 2 -> true to 0
        3 -> isFree(2) to 2
        4 -> isFree(2, 3) to 4
        5 -> isFree(2, 3, 4) to 6
        6 -> isFree(2, 3, 4, 5) to 7
        else -> throw IllegalArgumentException()
    }

    // (way free, way cost)
    private fun canGoIntoRoomB(hallwayIndex: Int): Pair<Boolean, Int> = when (hallwayIndex) {
        0 -> isFree(1, 2) to 3
        1 -> isFree(2) to 2
        2, 3 -> true to 0
        4 -> isFree(3) to 2
        5 -> isFree(3, 4) to 4
        6 -> isFree(3, 4, 55) to 5
        else -> throw IllegalArgumentException()
    }

    // (way free, way cost)
    private fun canGoIntoRoomC(hallwayIndex: Int): Pair<Boolean, Int> = when (hallwayIndex) {
        0 -> isFree(1, 2, 3) to 5
        1 -> isFree(2, 3) to 4
        2 -> isFree(3) to 2
        3, 4 -> true to 0
        5 -> isFree(4) to 2
        6 -> isFree(4, 5) to 3
        else -> throw IllegalArgumentException()
    }

    // (way free, way cost)
    private fun canGoIntoRoomD(hallwayIndex: Int): Pair<Boolean, Int> = when (hallwayIndex) {
        0 -> isFree(1, 2, 3, 4) to 7
        1 -> isFree(2, 3, 4) to 6
        2 -> isFree(3, 4) to 4
        3 -> isFree(4) to 2
        4, 5 -> true to 0
        6 -> isFree(5) to 1
        else -> throw IllegalArgumentException()
    }

    private fun fromHallwayToRoomB(i: Int): Path? = canGoIntoRoomB(i).let { if (it.first) {
        val (index, length) = firstTargetInB() ?: return null
        Path(i, index, length + it.second, Amphipod.Bronze)
    } else null }

    private fun fromHallwayToRoomC(i: Int): Path? = canGoIntoRoomC(i).let { if (it.first) {
        val (index, length) = firstTargetInC() ?: return null
        Path(i, index, length + it.second, Amphipod.Copper)
    } else null }

    private fun fromHallwayToRoomD(i: Int): Path? = canGoIntoRoomD(i).let { if (it.first) {
        val (index, length) = firstTargetInD() ?: return null
        Path(i, index, length + it.second, Amphipod.Desert)
    } else null }

    fun firstTargetInA(): Pair<Int, Int>? = findTargetInRoom(listOf(19, 15, 11, 7), Amphipod.Amber)
    fun firstTargetInB(): Pair<Int, Int>? = findTargetInRoom(listOf(20, 16, 12, 8), Amphipod.Bronze)
    fun firstTargetInC(): Pair<Int, Int>? = findTargetInRoom(listOf(21, 17, 13, 9), Amphipod.Copper)
    fun firstTargetInD(): Pair<Int, Int>? = findTargetInRoom(listOf(22, 18, 14, 10), Amphipod.Desert)

    // returns Index, Length
    private fun findTargetInRoom(indices: List<Int>, amphipod: Amphipod): Pair<Int, Int>? {
        indices.forEachIndexed { index, i ->
            val a = get(i) ?: return i to ((indices.size - index) + 1)
            if (a != amphipod) {
                return null
            }
        }
        return null
    }

    fun print() {
        println("#############")
        print("#")
        // hallwayp
        print(get(0).toChar())
        print(get(1).toChar())
        print(".")
        print(get(2).toChar())
        print(".")
        print(get(3).toChar())
        print(".")
        print(get(4).toChar())
        print(".")
        print(get(5).toChar())
        print(get(6).toChar())
        println("#")
        print("###")
        // line 1
        print(get(7).toChar())
        print("#")
        print(get(8).toChar())
        print("#")
        print(get(9).toChar())
        print("#")
        print(get(10).toChar())
        println("###")
        print("  #")
        // line 2
        print(get(11).toChar())
        print("#")
        print(get(12).toChar())
        print("#")
        print(get(13).toChar())
        print("#")
        print(get(14).toChar())
        println("#")
        print("  #")
        // line 3
        print(get(15).toChar())
        print("#")
        print(get(16).toChar())
        print("#")
        print(get(17).toChar())
        print("#")
        print(get(18).toChar())
        println("#")
        print("  #")
        // line 3
        print(get(19).toChar())
        print("#")
        print(get(20).toChar())
        print("#")
        print(get(21).toChar())
        print("#")
        print(get(22).toChar())
        println("#")
        println("  #########  ")
    }

    data class Path(val start: Int, val target: Int, val length: Int, val amphipod: Amphipod) {
        fun costF() = length * when (amphipod) {
            Amphipod.Amber -> 1
            Amphipod.Bronze -> 10
            Amphipod.Copper -> 100
            Amphipod.Desert -> 1000
        }
    }

    companion object {
        fun empty(): State = State(BitSet(23 * 4))

        fun finished(): State {
            val state = empty()
            state.set(7, Amphipod.Amber)
            state.set(11, Amphipod.Amber)
            state.set(15, Amphipod.Amber)
            state.set(19, Amphipod.Amber)
            state.set(8, Amphipod.Bronze)
            state.set(12, Amphipod.Bronze)
            state.set(16, Amphipod.Bronze)
            state.set(20, Amphipod.Bronze)
            state.set(9, Amphipod.Copper)
            state.set(13, Amphipod.Copper)
            state.set(17, Amphipod.Copper)
            state.set(21, Amphipod.Copper)
            state.set(10, Amphipod.Desert)
            state.set(14, Amphipod.Desert)
            state.set(18, Amphipod.Desert)
            state.set(22, Amphipod.Desert)
            return state
        }
    }
}