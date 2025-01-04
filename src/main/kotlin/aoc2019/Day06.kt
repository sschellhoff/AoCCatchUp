package de.sschellhoff.aoc2019

import de.sschellhoff.utils.Day

class Day06: Day<Long, Long>(6, 2019, 42,4, "", "_2") {
    override fun part1(input: String, isTest: Boolean): Long {
        return input.buildOrbitMap().countOrbits()
    }

    override fun part2(input: String, isTest: Boolean): Long {
        return input.buildOrbitMap().countOrbitsFromYouToSanta()
    }

    private fun Map<String, List<String>>.countOrbits(): Long {
        val indirectOrbits = mutableListOf<Pair<String, Long>>()
        indirectOrbits.addAll(get("COM")!!.map { it to 1 })
        var c = 0L
        while (indirectOrbits.isNotEmpty()) {
            val (objectInOrbit, orbitDepth) = indirectOrbits.removeLast()
            val newIndirectOrbits = get(objectInOrbit)
            c += orbitDepth
            if (newIndirectOrbits != null) {
                indirectOrbits.addAll(newIndirectOrbits.map { it to orbitDepth+1 })
            }
        }
        return c
    }

    private fun Map<String, List<String>>.countOrbitsFromYouToSanta(): Long {
        val fromYou = orbitsFrom("YOU")
        val fromSan = orbitsFrom("SAN")
        val branch = fromYou.find { it in fromSan }
        return fromYou.indexOf(branch) + fromSan.indexOf(branch) - 2L
    }

    private fun Map<String, List<String>>.orbitsFrom(target: String): List<String> {
        val pred = mutableMapOf<String, String>()
        val indirectOrbits = mutableListOf("COM")
        while (indirectOrbits.isNotEmpty()) {
            val p = indirectOrbits.removeLast()
            if (p == target) {
                return pred.pathFromPredecessors("COM", target)
            }
            val next = getOrDefault(p, emptyList())
            next.forEach { o ->
                pred[o] = p
            }
            indirectOrbits.addAll(next)
        }
        throw IllegalArgumentException("$target is not reachable")
    }

    private fun Map<String, String>.pathFromPredecessors(start: String, target: String): List<String> {
        var p: String? = target
        val result = mutableListOf<String>()
        while (p != null) {
            result.add(p)
            p = get(p)
        }
        return result
    }

    private fun String.buildOrbitMap(): Map<String, List<String>> {
        val result = mutableMapOf<String, MutableList<String>>()
        lines().forEach { line ->
            val (a, b) = line.split(")")
            result.getOrPut(a) { mutableListOf() }.add(b)
        }
        return result
    }
}