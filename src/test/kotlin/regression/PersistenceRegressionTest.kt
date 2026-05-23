package regression

import application.GameAdministrationService
import application.PlayerService
import application.PlayerStats
import application.PlayerStatsRegistry
import org.junit.jupiter.api.Test
import persistence.Storage
import java.nio.file.Files
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PersistenceRegressionTest {
    private class InMemoryRegistry : PlayerStatsRegistry {
        var data: Map<String, PlayerStats> = emptyMap()

        override fun load(): Map<String, PlayerStats> = data

        override fun save(statsByNickname: Map<String, PlayerStats>) {
            data = statsByNickname.toMap()
        }
    }

    @Test
    fun `player stats still persist`() {
        val db = Files.createTempFile("testPersistence", ".db")
        Files.deleteIfExists(db)
        val store = Storage(db)
        val before = mapOf("A" to PlayerStats(3, 2, 1))
        store.save(before)
        assertEquals(before, store.load())
    }

    @Test
    fun `player service still records wins and losses`() {
        val registry = InMemoryRegistry()
        val service = PlayerService(registry)
        service.addPlayer("P1")
        service.addPlayer("P2")
        service.recordGameResult("P1", "P2", "P1", "P2")

        assertEquals(PlayerStats(1, 1, 0), service.getStats("P1"))
        assertEquals(PlayerStats(1, 0, 1), service.getStats("P2"))
    }

    @Test
    fun `game administration still rejects overlapping placements`() {
        val registry = InMemoryRegistry()
        val service = GameAdministrationService(PlayerService(registry))
        val lines =
            listOf(
                "PLAYER-A",
                "PLAYER-B",
                "PLACEMENT a 0 0 H 3",
                "PLACEMENT a 1 0 V 3",
            )
        val result = service.processLines(lines)
        assertTrue(result.logLines.any { it.startsWith("ERROR:") })
    }
}
