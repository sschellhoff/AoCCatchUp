package de.sschellhoff.utils

interface PriorityQueue<DATA> {
    fun isEmpty(): Boolean
    fun isNotEmpty(): Boolean
    fun peek(): Pair<DATA, Long>
    fun peekOrNull(): Pair<DATA, Long>?
    fun insert(element: DATA, cost: Long)
    fun extract(): Pair<DATA, Long>
    fun extractOrNull(): Pair<DATA, Long>?

    companion object {
        fun <DATA> from(
            vararg elements: Pair<DATA, Long>,
            isInOrder: (a: Long, b: Long) -> Boolean
        ): PriorityQueue<DATA> =
            Heap<DATA>(isInOrder).also { heap -> elements.forEach { heap.insert(it.first, it.second) } }
    }
}
