package de.sschellhoff.utils

import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.stream.Collectors

fun getDaysForYear(year: Int): List<Day<*, *>> = findAllClassesUsingClassLoader("de.sschellhoff.aoc$year").mapNotNull {
    if (it == null || it.superclass != Day::class.java) {
        null
    } else {
        it.name to it
    }
}.sortedBy { it.first }.map {
    it.second.constructors.first().newInstance() as Day<*, *>
}


fun findAllClassesUsingClassLoader(packageName: String): Set<Class<*>?> {
    val stream = ClassLoader.getSystemClassLoader()
        .getResourceAsStream(packageName.replace("[.]".toRegex(), "/"))
    checkNotNull(stream)
    val reader = BufferedReader(InputStreamReader(stream))
    return reader.lines()
        .filter { line: String -> line.endsWith(".class") }
        .map { line: String -> getClass(line, packageName) }
        .collect(Collectors.toSet())
}

private fun getClass(className: String, packageName: String): Class<*>? = try {
    Class.forName(
        (packageName + "."
                + className.substring(0, className.lastIndexOf('.')))
    )
} catch (e: ClassNotFoundException) {
    null
}