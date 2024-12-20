package de.sschellhoff

import de.sschellhoff.utils.Day
import de.sschellhoff.utils.getDaysForYear

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
fun main() {
    getDaysForYear(2021).forEach { it.run(Day.RunMode.BOTH) }
}