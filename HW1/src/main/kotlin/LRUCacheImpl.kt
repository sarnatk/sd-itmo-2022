const val MAX_SIZE = 64

class LRUCacheImpl<K, V>(private val capacity: Int = MAX_SIZE) : LRUCache<K, V> {
    private inner class Node<K, V>(
        val key: K? = null,
        var value: V? = null,
        var prev: Node<K, V>? = null,
        var next: Node<K, V>? = null
    )

    // head and tail are fictive nodes
    // head's next is the real first node
    // tail's prev is the real last node
    private val head = Node<K, V>()
    private val tail = Node<K, V>()
    private val map = HashMap<K, Node<K, V>>()

    init {
        require(capacity > 0) { "Positive integer required for capacity" }
        head.next = tail
        tail.prev = head
    }

    override fun get(key: K): V? {
        val startSize = size()
        assert(startSize <= capacity)
        val startFirst = head.next

        val node = map[key]
        node?.let {
            pushFront(it)
            assert(head.next == node)
        }

        val finishSize = size()
        val finishFirst = head.next
        assert(finishSize == startSize)
        assert(node != null || finishFirst == startFirst)
        return node?.value
    }

    override fun put(key: K, value: V) {
        val startSize = size()
        assert(startSize <= capacity)
        val node = map[key]
        node?.let {
            it.value = value
            pushFront(it)
            assert(head.next == node)
            assert(size() == startSize)
            return
        }

        val newNode = Node(key, value, head, head.next)
        head.next!!.prev = newNode
        head.next = newNode
        map[key] = newNode
        assert(size() == startSize + 1)

        if (size() > capacity) {
            popBack()
            assert(size() == capacity)
        }

        assert(head.next == newNode)
        assert(size() <= capacity)
    }

    override fun size(): Int = map.size

    private fun pushFront(node: Node<K, V>) {
        node.next!!.prev = node.prev
        node.prev!!.next = node.next
        assert(node.next!!.prev!!.next == node.next)
        assert(node.prev!!.next!!.prev == node.prev)

        node.next = head.next
        node.prev = head
        head.next!!.prev = node
        head.next = node
        assert(node.next!!.prev != head)
    }

    private fun popBack() {
        val startSize = size()

        map.remove(tail.prev!!.key)
        tail.prev = tail.prev!!.prev
        tail.prev!!.next = tail

        assert(tail.prev!!.next == tail)
        assert(size() == startSize - 1)
    }
}
