package application

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GameAdministrationServiceTest {
    private class InMemoryRegistry : PlayerStatsRegistry {
        var data: Map<String, PlayerStats> = emptyMap()

        override fun load(): Map<String, PlayerStats> {
            return data
        }

        override fun save(statsByNickname: Map<String, PlayerStats>) {
            data = statsByNickname.toMap()
        }
    }

    @Test
    fun `processLines should execute commands and update stats`() {
        val registry = InMemoryRegistry()
        val playerService = PlayerService(registry)
        val service = GameAdministrationService(playerService)

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

        val result = service.processLines(lines)

        assertTrue(result.logLines.none { it.startsWith("ERROR:") })
        assertTrue(result.logLines.any { it.contains("Game finished. Winner: admin-a") })
        assertEquals(PlayerStats(1, 1, 0), playerService.getStats("admin-a"))
        assertEquals(PlayerStats(1, 0, 1), playerService.getStats("admin-b"))
    }

    @Test
    fun `processLines should fail on invalid placement`() {
        val service = GameAdministrationService()
        val lines =
            listOf(
                "PLAYER a",
                "PLAYER b",
                "PLACEMENT a 0 0 H 3",
                "PLACEMENT a 1 0 V 3",
            )
        val result = service.processLines(lines)
        assertTrue(result.logLines.any { it.startsWith("ERROR:") })
        assertTrue(result.logLines.any { it.contains("line 4") }, result.logLines.joinToString("\n"))
    }
}
