package de.sschellhoff.utils

data class Heap<DATA>(private val inOrder: (a: Long, b: Long) -> Boolean): PriorityQueue<DATA> {
    private val data: MutableList<Pair<DATA, Long>> = mutableListOf()

    override fun insert(element: DATA, cost: Long) {
        var currentIndex = data.size
        data.add(element to cost)

        while (currentIndex > 0 && inOrder(data[currentIndex].second, data[parent(currentIndex)].second)) {
            swap(currentIndex, parent(currentIndex))
            currentIndex = parent(currentIndex)
        }
    }

    override fun isEmpty(): Boolean = data.isEmpty()

    override fun isNotEmpty(): Boolean = data.isNotEmpty()

    override fun peek(): Pair<DATA, Long> = data.first()

    override fun peekOrNull(): Pair<DATA, Long>? = data.firstOrNull()

    override fun extract(): Pair<DATA, Long> = extractOrNull() ?: throw IllegalStateException()

    override fun extractOrNull(): Pair<DATA, Long>? {
        val result = data.firstOrNull() ?: return null
        if (data.size == 1) {
            data.removeLast()
            return result
        }
        data[0] = data.removeLast()
        heapify(0)
        return result
    }

    private fun parent(index: Int): Int = (index - 1) / 2

    private fun swap(a: Int, b: Int) {
        val t = data[a]
        data[a] = data[b]
        data[b] = t
    }

    private tailrec fun heapify(i: Int) {
        val left = leftChild(i)
        val right = rightChild(i)

        var toSwap = i
        if (left < data.size && inOrder(data[left].second, data[toSwap].second)) {
            toSwap = left
        }
        if (right < data.size && inOrder(data[right].second, data[toSwap].second)) {
            toSwap = right
        }
        if (i != toSwap) {
            swap(i, toSwap)
            heapify(toSwap)
        }
    }

    private fun leftChild(i: Int): Int = 2 * i + 1

    private fun rightChild(i: Int): Int = 2 * i + 2

    override fun toString(): String = data.toString()
}
