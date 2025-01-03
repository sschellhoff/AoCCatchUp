package de.sschellhoff.utils

import java.util.function.Predicate

fun String.getGridSize(): Pair<Long, Long> = lines().first().length.toLong() to lines().size.toLong()

data class Grid<T>(private val data: List<MutableList<T>>) {
    val width: Int = data.first().size
    val height: Int = data.size
    val size: Vector2i = Vector2i(width, height)

    fun forEach(action: (c: T) -> Unit) {
        data.forEach { line ->
            line.forEach { c ->
                action(c)
            }
        }
    }

    fun forEachIndexed(action: (x: Int, y: Int, c: T) -> Unit) {
        data.forEachIndexed { y, line ->
            line.forEachIndexed { x, c ->
                action(x, y, c)
            }
        }
    }

    fun inBounds(position: Vector2i): Boolean =
        position.x in 0..<width && position.y in 0..<height

    fun inBounds(x: Int, y: Int): Boolean =
        x in 0..<width && y in 0..<height

    fun findPositions(predicate: Predicate<T>): Set<Vector2i> {
        val result = mutableSetOf<Vector2i>()
        forEachIndexed { x: Int, y: Int, c ->
            if (predicate.test(c)) {
                result.add(Vector2i(x, y))
            }
        }
        return result
    }

    fun get(position: Vector2i): T = data[position.y][position.x]

    // TODO maybe think about mutable/immutable grid
    fun set(position: Vector2i, value: T) {
        data[position.y][position.x] = value
    }

    fun getNeighbours(position: Vector2i, predicate: Predicate<Vector2i>): List<Vector2i> {
        if(!inBounds(position)) {
            return emptyList()
        }

        return Direction.entries.map { direction ->
            position.move(direction)
        }.filter { inBounds(it) && predicate.test(it) }
    }

    fun count(t: T): Long {
        var counter = 0L
        forEachIndexed { _, _, c ->
            if (c == t) {
                counter += 1
            }
        }
        return counter
    }

    companion object {
        fun <T>fromString(input: String, toNode: (c: Char) -> T): Grid<T> =
            input.lines().map { line -> line.map { toNode(it) }.toMutableList() }.let { Grid(data = it) }

        fun <T>initialize(width: Int, height: Int, toNode: (x: Int, y: Int) -> T): Grid<T> {
            val data = mutableListOf<MutableList<T>>()
            (0..<height).forEach { y ->
                val line = mutableListOf<T>()
                (0..<width).forEach { x ->
                    line.add(toNode(x, y))
                }
                data.add(line)
            }
            return Grid(data = data)
        }
    }
}

fun String.findStartAndEndInCharGrid(cStart: Char = 'S', cEnd: Char = 'E'): Pair<Vector2i, Vector2i> {
    var start: Vector2i? = null
    var end: Vector2i? = null
    this.lines().forEachIndexed { y, line ->
        line.forEachIndexed { x, c ->
            if (c == cStart) {
                start = Vector2i(x, y)
            } else if (c == cEnd) {
                end = Vector2i(x, y)
            }
        }
    }
    checkNotNull(start)
    checkNotNull(end)
    return start!! to end!!
}