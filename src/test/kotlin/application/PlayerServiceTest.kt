package application

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class PlayerServiceTest {
    private class InMemoryRegistry : PlayerStatsRegistry {
        var data: Map<String, PlayerStats> = emptyMap()

        override fun load(): Map<String, PlayerStats> = data

        override fun save(statsByNickname: Map<String, PlayerStats>) {
            data = statsByNickname.toMap()
        }
    }

    @Test
    fun `add player should store player`() {
        val registry = InMemoryRegistry()
        val service = PlayerService(registry)

        val player = service.addPlayer("test")

        assertEquals(1, service.getAll().size)
        assertEquals("test", player.nickname)
        assertTrue(registry.data.containsKey("test"))
    }

    @Test
    fun `recordGameResult should update both players stats`() {
        val registry = InMemoryRegistry()
        val service = PlayerService(registry)
        service.addPlayer("A")
        service.addPlayer("B")

        service.recordGameResult("A", "B")

        assertEquals(PlayerStats(1, 1, 0), service.getStats("A"))
        assertEquals(PlayerStats(1, 0, 1), service.getStats("B"))
    }

    @Test
    fun `service should load players from registry on startup`() {
        val registry = InMemoryRegistry()
        registry.data = mapOf("savedPlayer" to PlayerStats(3, 2, 1))

        val service = PlayerService(registry)

        assertTrue(service.getAll().any { it.nickname == "savedPlayer" })
        assertEquals(PlayerStats(3, 2, 1), service.getStats("savedPlayer"))
    }

    @Test
    fun `getLeaderboard should sort by wins descending`() {
        val registry = InMemoryRegistry()
        val service = PlayerService(registry)
        service.addPlayer("Low")
        service.addPlayer("High")
        service.recordGameResult("High", "Low")

        val rows = service.getLeaderboard()
        assertEquals("High", rows[0].nickname)
        assertEquals("Low", rows[1].nickname)
    }
}
