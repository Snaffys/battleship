package integration

import application.GameAdministrationService
import application.PlayerService
import application.PlayerStats
import org.junit.jupiter.api.Test
import persistence.Storage
import java.nio.file.Files
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PersistenceIntegrationTest {
    @Test
    fun `full administration flow persists stats and game history in db`() {
        val db = Files.createTempFile("testFullFlowDB", ".db")
        Files.deleteIfExists(db)

        val store = Storage(db)
        val playerService = PlayerService(store)
        val admin = GameAdministrationService(playerService)

        val lines =
            listOf(
                "PLAYER admin-a",
                "PLAYER admin-b",
                "PLACEMENT admin-a 0 0 H 4",
                "PLACEMENT admin-a 0 2 H 3",
                "PLACEMENT admin-a 0 4 H 3",
                "PLACEMENT admin-a 0 6 H 2",
                "PLACEMENT admin-a 3 6 H 2",
                "PLACEMENT admin-a 6 6 H 2",
                "PLACEMENT admin-a 9 0 V 1",
                "PLACEMENT admin-a 9 2 V 1",
                "PLACEMENT admin-a 9 4 V 1",
                "PLACEMENT admin-a 9 6 V 1",
                "PLACEMENT admin-b 0 0 H 4",
                "PLACEMENT admin-b 0 2 H 3",
                "PLACEMENT admin-b 0 4 H 3",
                "PLACEMENT admin-b 0 6 H 2",
                "PLACEMENT admin-b 3 6 H 2",
                "PLACEMENT admin-b 6 6 H 2",
                "PLACEMENT admin-b 9 0 V 1",
                "PLACEMENT admin-b 9 2 V 1",
                "PLACEMENT admin-b 9 4 V 1",
                "PLACEMENT admin-b 9 6 V 1",
                "START admin-a admin-b",
                "SHOT 0 0",
                "SHOT 1 0",
                "SHOT 2 0",
                "SHOT 3 0",
                "SHOT 0 2",
                "SHOT 1 2",
                "SHOT 2 2",
                "SHOT 0 4",
                "SHOT 1 4",
                "SHOT 2 4",
                "SHOT 0 6",
                "SHOT 1 6",
                "SHOT 3 6",
                "SHOT 4 6",
                "SHOT 6 6",
                "SHOT 7 6",
                "SHOT 9 0",
                "SHOT 9 2",
                "SHOT 9 4",
                "SHOT 9 6",
            )

        val result = admin.processLines(lines)

        assertTrue(result.logLines.none { it.startsWith("ERROR:") })
        assertEquals(PlayerStats(1, 1, 0), playerService.getStats("admin-a"))
        assertEquals(PlayerStats(1, 0, 1), playerService.getStats("admin-b"))

        val history = store.getFinishedGames(limit = 5)
        assertEquals(1, history.size)
        assertEquals("admin-a", history[0].winnerNickname)
        assertEquals("admin-a", history[0].firstPlayerNickname)
        assertEquals("admin-b", history[0].secondPlayerNickname)

        val playerServiceFilled = PlayerService(store)
        assertEquals(PlayerStats(1, 1, 0), playerServiceFilled.getStats("admin-a"))
        assertEquals(1, store.getFinishedGames(10).size)
    }
}
