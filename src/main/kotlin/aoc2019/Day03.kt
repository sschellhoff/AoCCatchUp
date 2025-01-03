package de.sschellhoff.aoc2019

import de.sschellhoff.utils.*
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class Day03 : Day<Long, Long>(3, 2019, 135, 410) {
    override fun part1(input: String, isTest: Boolean): Long {
        val (w1, w2) = input.lines().map { it.wire() }
        val distancesOfCrossesSorted = w1.cross(w2).map { Vector2.Zero.manhattanDistance(it) }.sorted()
        return if (distancesOfCrossesSorted.first() == 0L) distancesOfCrossesSorted[1] else distancesOfCrossesSorted.first()
    }

    override fun part2(input: String, isTest: Boolean): Long {
        val (w1, w2) = input.lines().map { it.wire() }
        val crosses = w1.cross(w2)
        val distances = crosses.map { p ->
            w1.lengthUntilHit(p) + w2.lengthUntilHit(p)
        }.sorted()
        return if (distances.first() == 0L) distances[1] else distances.first()
    }

    private fun Wire.cross(other: Wire): List<Vector2> = flatMap { l1 ->
        other.mapNotNull { l2 ->
            l1.cross(l2)
        }
    }

    private fun Wire.lengthUntilHit(position: Vector2): Long {
        var l = 0L
        forEach { line ->
            val isCrossing = if (line.t1 == Alignment.Horizontal) {
                verticalLine(position.y, position.y, position.x, Direction.NORTH)
            } else {
                horizontalLine(position.x, position.x, position.y, Direction.EAST)
            }.cross(line) != null
            if (isCrossing) {
                return l + when (line.t5) {
                    Direction.NORTH -> abs(position.y - line.t3)
                    Direction.SOUTH -> abs(position.y - line.t2)
                    Direction.EAST -> abs(position.x - line.t2)
                    Direction.WEST -> abs(position.x - line.t3)
                }
            } else {
                l += abs(line.t3 - line.t2)
            }
        }
        throw IllegalArgumentException()
    }

    private fun Line.cross(other: Line): Vector2? {
        if (t1 == other.t1) return null
        if (t1 == Alignment.Horizontal) return other.cross(this)
        if (other.t4 in (t2..t3) && t4 in (other.t2..other.t3)) {
            return Vector2(t4, other.t4)
        }
        return null
    }

    private fun String.wire(): Wire {
        val result = mutableListOf<Line>()
        split(",").fold(Vector2.Zero) { acc, m ->
            val amount = m.substring(1).toLong()
            val direction = if (m.startsWith("U")) {
                Direction.NORTH
            } else if (m.startsWith("D")) {
                Direction.SOUTH
            } else if (m.startsWith("L")) {
                Direction.WEST
            } else if (m.startsWith("R")) {
                Direction.EAST
            } else {
                throw IllegalArgumentException("$m is an invalid direction")
            }

            val next = acc.move(direction, amount)
            result.add(
                when (direction) {
                    Direction.NORTH, Direction.SOUTH -> verticalLine(acc.y, next.y, acc.x, direction)
                    Direction.WEST, Direction.EAST -> horizontalLine(acc.x, next.x, acc.y, direction)
                }
            )
            next
        }
        return result
    }

    private fun horizontalLine(x1: Long, x2: Long, y: Long, direction: Direction): Line =
        (Alignment.Horizontal then min(x1, x2) then max(x1, x2) then y then direction).also { check(it.t5 == Direction.EAST || it.t5 == Direction.WEST) }

    private fun verticalLine(y1: Long, y2: Long, x: Long, direction: Direction): Line =
        (Alignment.Vertical then min(y1, y2) then max(y1, y2) then x then direction).also { check(it.t5 == Direction.NORTH || it.t5 == Direction.SOUTH) }
}

private enum class Alignment {
    Horizontal, Vertical
}

private typealias Wire = List<Line>
private typealias Line = NTuple5<Alignment, Long, Long, Long, Direction>