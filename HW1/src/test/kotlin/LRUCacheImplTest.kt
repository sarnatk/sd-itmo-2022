import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class LRUCacheImplTest {

    private val defaultLRUCache = LRUCacheImpl<Int?, Int?>() // max size declared as const val 64
    private val size1LRUCache = LRUCacheImpl<Int?, Int?>(1)
    private val size3LRUCache = LRUCacheImpl<Int, String>(3)

    @Test
    fun `invalid capacity`() {
        assertThrows(IllegalArgumentException::class.java) {
            LRUCacheImpl<Int, Int>(0)
        }
    }

    @Test
    fun `simple actions`() {
        assertEquals(null, defaultLRUCache.get(1))
        defaultLRUCache.put(12, 34)
        defaultLRUCache.put(1, 42)
        defaultLRUCache.put(100, 1)
        assertEquals(34, defaultLRUCache.get(12))
        assertEquals(42, defaultLRUCache.get(1))
        assertEquals(1, defaultLRUCache.get(100))
        defaultLRUCache.put(5, 5)
        assertEquals(5, defaultLRUCache.get(5))
        assertEquals(34, defaultLRUCache.get(12))
        assertEquals(42, defaultLRUCache.get(1))
        assertEquals(1, defaultLRUCache.get(100))
    }

    @Test
    fun `same queries`() {
        defaultLRUCache.put(1, 123)
        defaultLRUCache.put(2, 456)
        defaultLRUCache.put(3, 789)
        assertEquals(123, defaultLRUCache.get(1))
        assertEquals(123, defaultLRUCache.get(1))
        assertEquals(123, defaultLRUCache.get(1))
        assertEquals(456, defaultLRUCache.get(2))
        assertEquals(123, defaultLRUCache.get(1))
    }

    @Test
    fun `null parameters`() {
        defaultLRUCache.put(null, 0)
        defaultLRUCache.put(0, null)
        assertEquals(0, defaultLRUCache.get(null))
        assertEquals(null, defaultLRUCache.get(0))
        assertEquals(null, defaultLRUCache.get(1))
    }

    @Test
    fun `overflow size 1`() {
        size1LRUCache.put(0, 0)
        assertEquals(0, size1LRUCache.get(0))
        assertEquals(1, size1LRUCache.size())
        size1LRUCache.put(1, 1)
        assertNull(size1LRUCache.get(0))
        assertEquals(1, size1LRUCache.get(1))
        assertEquals(1, size1LRUCache.size())
        size1LRUCache.put(2, 2)
        assertNull(size1LRUCache.get(0))
        assertNull(size1LRUCache.get(1))
        assertEquals(2, size1LRUCache.get(2))
        assertEquals(1, size1LRUCache.size())
    }

    @Test
    fun `key overwrite`() {
        size1LRUCache.put(0, 1)
        assertEquals(1, size1LRUCache.get(0))
        assertEquals(1, size1LRUCache.size())
        size1LRUCache.put(0, 2)
        assertEquals(2, size1LRUCache.get(0))
        assertEquals(1, size1LRUCache.size())
        size1LRUCache.put(0, 1)
        assertEquals(1, size1LRUCache.get(0))
        assertEquals(1, size1LRUCache.size())
    }

    @Test
    fun `overflow not touched`() {
        size3LRUCache.put(1, "1")
        size3LRUCache.put(2, "2")
        size3LRUCache.put(3, "3")
        size3LRUCache.put(4, "4")
        assertEquals(3, size3LRUCache.size())
        assertNull(size3LRUCache.get(1))
        size3LRUCache.put(1, "1")
        assertEquals(3, size3LRUCache.size())
        assertNull(size3LRUCache.get(2))
        size3LRUCache.put(2, "2")
        assertEquals(3, size3LRUCache.size())
        assertNull(size3LRUCache.get(3))
        size3LRUCache.put(3, "3")
        assertEquals(3, size3LRUCache.size())
        assertNull(size3LRUCache.get(4))
        assertEquals("3", size3LRUCache.get(3))
        assertEquals("2", size3LRUCache.get(2))
        assertEquals("1", size3LRUCache.get(1))
    }

    @Test
    fun `overflow touched`() {
        size3LRUCache.put(1, "1")
        size3LRUCache.put(2, "2")
        size3LRUCache.put(3, "3")
        assertEquals("1", size3LRUCache.get(1))
        size3LRUCache.put(4, "4")
        assertEquals(3, size3LRUCache.size())
        assertEquals("1", size3LRUCache.get(1))
        assertNull(size3LRUCache.get(2))
        size3LRUCache.put(2, "2")
        assertEquals(3, size3LRUCache.size())
        assertEquals("1", size3LRUCache.get(1))
        assertNull(size3LRUCache.get(3))
    }
}
