package de.sschellhoff.utils

import kotlin.math.abs

data class Vector2(val x: Long, val y: Long) {
    operator fun plus(other: Vector2): Vector2 = Vector2(x + other.x, y + other.y)
    operator fun minus(other: Vector2): Vector2 = Vector2(x - other.x, y - other.y)
    operator fun times(scalar: Long) = Vector2(x * scalar, y * scalar)

    infix fun distanceVectorTo(other: Vector2): Vector2 = other - this

    companion object {
        val Zero = Vector2(0, 0)
        val Up = Vector2(0, -1)
        val Down = Vector2(0, 1)
        val Left = Vector2(-1, 0)
        val Right = Vector2(1, 0)
    }
}

fun Vector2.move(direction: Direction): Vector2 = when (direction) {
    Direction.NORTH -> Vector2(x, y) + Vector2.Up
    Direction.EAST -> Vector2(x, y) + Vector2.Right
    Direction.SOUTH -> Vector2(x, y) + Vector2.Down
    Direction.WEST -> Vector2(x, y) + Vector2.Left
}

fun Vector2.move(direction: Direction, amount: Long) = when (direction) {
    Direction.NORTH -> Vector2(x, y) + Vector2.Up * amount
    Direction.EAST -> Vector2(x, y) + Vector2.Right * amount
    Direction.SOUTH -> Vector2(x, y) + Vector2.Down * amount
    Direction.WEST -> Vector2(x, y) + Vector2.Left * amount
}

data class Vector2i(val x: Int, val y: Int) {
    operator fun plus(other: Vector2i) = Vector2i(x + other.x, y + other.y)
    operator fun minus(other: Vector2i) = Vector2i(x - other.x, y - other.y)
    operator fun times(scalar: Int) = Vector2i(x * scalar, y * scalar)
    operator fun rem(other: Vector2i) = Vector2i(x % other.x, y % other.y)

    infix fun distanceVectorTo(other: Vector2i) = other - this

    companion object {
        val Zero = Vector2i(0, 0)
        val Up = Vector2i(0, -1)
        val Down = Vector2i(0, 1)
        val Left = Vector2i(-1, 0)
        val Right = Vector2i(1, 0)
    }
}

fun Vector2i.move(direction: Direction): Vector2i = when (direction) {
    Direction.NORTH -> Vector2i(x, y) + Vector2i.Up
    Direction.EAST -> Vector2i(x, y) + Vector2i.Right
    Direction.SOUTH -> Vector2i(x, y) + Vector2i.Down
    Direction.WEST -> Vector2i(x, y) + Vector2i.Left
}

fun Vector2.toVector2i(): Vector2i = Vector2i(x.toIntOrThrow(), y.toIntOrThrow())

fun Vector2i.toVector2() = Vector2(x.toLong(), y.toLong())

fun Vector2.manhattanDistance(other: Vector2): Long = (this - other).let { fromTo ->
    abs(fromTo.x) + abs(fromTo.y)
}

fun Vector2i.manhattanDistance(other: Vector2i): Int = (this - other).let { fromTo ->
    abs(fromTo.x) + abs(fromTo.y)
}