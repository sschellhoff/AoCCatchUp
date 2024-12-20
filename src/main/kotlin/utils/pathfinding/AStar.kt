package de.sschellhoff.utils.pathfinding

import de.sschellhoff.utils.PriorityQueue

fun <NODE> aStar(
    start: NODE,
    isEnd: (node: NODE) -> Boolean,
    heuristic: (node: NODE) -> Long,
    getNextNodes: (node: NODE) -> List<EdgeInfo<NODE>>
): Pair<List<NODE>, Long>? {
    val pathInfo = mutableMapOf(start to NodeInfoAStar<NODE>(0, null))

    val openNodes = PriorityQueue.from(start to 0) { a, b -> a < b }
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
                pathInfo[nextNode.b] = NodeInfoAStar(cost = cost, predecessor = u)
                openNodes.insert(nextNode.b, cost + heuristic(nextNode.b))
            }
        }
    }
    return null
}

private data class NodeInfoAStar<NODE>(val cost: Long, val predecessor: NODE?)
