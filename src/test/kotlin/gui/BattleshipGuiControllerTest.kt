package gui

import application.PlayerService
import application.PlayerStats
import application.PlayerStatsRegistry
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class BattleshipGuiControllerTest {
    private class InMemoryRegistry : PlayerStatsRegistry {
        var data: Map<String, PlayerStats> = emptyMap()

        override fun load(): Map<String, PlayerStats> = data

        override fun save(statsByNickname: Map<String, PlayerStats>) {
            data = statsByNickname.toMap()
        }
    }

    @Test
    fun `addPlayer should save and return players`() {
        val registry = InMemoryRegistry()
        val playerService = PlayerService(registry)
        val controller = BattleshipGuiController(playerService)

        controller.addPlayer("UIA")
        controller.addPlayer("UIB")

        val names = controller.getPlayers().map { it.nickname }
        assertEquals(listOf("UIA", "UIB"), names)
    }

    @Test
    fun `getLeaderboardText should return persisted stats`() {
        val registry = InMemoryRegistry()
        val playerService = PlayerService(registry)
        val controller = BattleshipGuiController(playerService)
        controller.addPlayer("Winner")
        controller.addPlayer("Loser")
        playerService.recordGameResult("Winner", "Loser")

        val leaderboard = controller.getLeaderboardText()
        assertTrue(leaderboard.any { it.contains("Winner: games=1, wins=1, losses=0") })
        assertTrue(leaderboard.any { it.contains("Loser: games=1, wins=0, losses=1") })
    }

    @Test
    fun `addPlayer should trim nickname and return null for blank input`() {
        val registry = InMemoryRegistry()
        val playerService = PlayerService(registry)
        val controller = BattleshipGuiController(playerService = playerService)

        controller.addPlayer("  UI-A  ")
        assertEquals(listOf("UI-A"), controller.getPlayers().map { it.nickname })

        assertNull(controller.addPlayer("   "))
    }
}
