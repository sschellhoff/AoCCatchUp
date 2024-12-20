package de.sschellhoff.utils.pathfinding

import de.sschellhoff.utils.PriorityQueue

fun <NODE>dijkstra(start: NODE, isEnd: (node: NODE) -> Boolean, getNextNodes: (node: NODE) -> List<EdgeInfo<NODE>>): Pair<List<NODE>, Long>? {
    val pathInfo = mutableMapOf(start to NodeInfo<NODE>(0, null))

    val openNodes = PriorityQueue.from(start to 0) {a, b -> a < b}
    val closedNodes = mutableSetOf<NODE>()

    while (openNodes.isNotEmpty()) {
        val u = openNodes.extract().first
        val costSoFar = pathInfo[u]?.cost ?: throw IllegalStateException()
        closedNodes.add(u)
        if (isEnd(u)) {
            val path = mutableListOf(u)
            var current = pathInfo[u]?.predecessor
            while (current != null) {
                path.add(current)
                current = pathInfo[current]?.predecessor
            }
            return path to costSoFar
        }
        getNextNodes(u).forEach { nextNode ->
            val cost = nextNode.cost + costSoFar
            val currentCost = pathInfo[nextNode.b]?.cost
            if (!closedNodes.contains(nextNode.b) && (currentCost == null || currentCost > cost)) {
                pathInfo[nextNode.b] = NodeInfo(cost = cost, predecessor = u)
                openNodes.insert(nextNode.b, cost)
            }
        }
    }
    return null
}

private data class NodeInfo<NODE>(val cost: Long, val predecessor: NODE?)
data class EdgeInfo<NODE>(val a: NODE, val b: NODE, val cost: Long)
