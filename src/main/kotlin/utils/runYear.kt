package de.sschellhoff.utils

fun runYear(year: Int, mode: Day.RunMode, onlyLastDay: Boolean = false) {
    val days = getDaysForYear(year)
    if (onlyLastDay) {
        days.last().run(mode)
    } else {
        days.forEach { day ->
            day.run(mode)
        }
    }
}