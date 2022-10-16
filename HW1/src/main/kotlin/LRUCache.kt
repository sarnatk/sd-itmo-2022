interface LRUCache<K, V> {
    fun get(key: K): V?
    fun put(key: K, value: V)
    fun size(): Int
}
