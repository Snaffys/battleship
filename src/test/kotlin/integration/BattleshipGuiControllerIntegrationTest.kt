package integration

import application.PlayerService
import application.PlayerStats
import application.PlayerStatsRegistry
import gui.BattleshipGuiController
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BattleshipGuiControllerIntegrationTest {
    private class InMemoryRegistry : PlayerStatsRegistry {
        var data: Map<String, PlayerStats> = emptyMap()

        override fun load(): Map<String, PlayerStats> = data

        override fun save(statsByNickname: Map<String, PlayerStats>) {
            data = statsByNickname.toMap()
        }
    }

    @Test
    fun `controller should share player stats through registry`() {
        val registry = InMemoryRegistry()
        val service1 = PlayerService(registry)
        val controller1 = BattleshipGuiController(service1)
        controller1.addPlayer("UI-A")
        controller1.addPlayer("UI-B")
        service1.recordGameResult("UI-A", "UI-B")

        val service2 = PlayerService(registry)
        val controller2 = BattleshipGuiController(service2)
        val leaderboard = controller2.getLeaderboardText()

        assertTrue(leaderboard.isNotEmpty())
        assertTrue(leaderboard.first().contains("UI-A"))
        assertEquals(PlayerStats(1, 1, 0), service2.getStats("UI-A"))
    }
}
