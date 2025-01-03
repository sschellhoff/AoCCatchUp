package de.sschellhoff.aoc2021

import de.sschellhoff.utils.*

fun main() {
    Day25().run(Day.RunMode.BOTH)
}

class Day25 : Day<Long, Long>(25, 2021, 58, -1) {
    override fun part1(input: String, isTest: Boolean): Long {
        val grid = input.parse()
        var i = 1
        while(grid.step()) {
            i++
        }
        return i.toLong()
    }

    override fun part2(input: String, isTest: Boolean): Long {
        return -1
    }

    private fun Grid<Tile>.step(): Boolean {
        var moved = false
        this.find(Tile.East, Vector2i(1, 0)).forEach { pos ->
            this.set(pos, Tile.Free)
            this.set(pos.move(Direction.EAST) % this.size, Tile.East)
            moved = true
        }
        this.find(Tile.South, Vector2i(0, 1)).forEach { pos ->
            this.set(pos, Tile.Free)
            this.set(pos.move(Direction.SOUTH) % this.size, Tile.South)
            moved = true
        }
        return moved
    }

    private fun Grid<Tile>.find(type: Tile, freeOffset: Vector2i): List<Vector2i> {
        val result = mutableListOf<Vector2i>()
        this.forEachIndexed { x, y, t ->
            val next = Vector2i((x + freeOffset.x) % width, (y + freeOffset.y) % height)
            if (t == type && this.get(next) == Tile.Free) {
                result.add(Vector2i(x, y))
            }
        }
        return result
    }

    private fun Grid<Tile>.print() {
        var lastY = 0
        this.forEachIndexed { _, y, t ->
            when(t) {
                Tile.East -> print(">")
                Tile.South -> print("v")
                Tile.Free -> print(".")
            }
            if (y != lastY) {
                println()
                lastY = y
            }
        }
        println()
    }

    private enum class Tile {
        East,
        South,
        Free
    }

    private fun String.parse(): Grid<Tile> = Grid.fromString(this) {
        when (it) {
            '>' -> Tile.East
            'v' -> Tile.South
            '.' -> Tile.Free
            else -> throw IllegalArgumentException()
        }
    }
}