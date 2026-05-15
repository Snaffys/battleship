package integration

import application.GameService
import application.PlayerService
import application.PlayerStats
import application.PlayerStatsRegistry
import application.ShipPlacementRequest
import domain.game.event.GameEvent
import domain.value.Coords
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class GameplayStatsIntegrationTest {
    private class InMemoryRegistry : PlayerStatsRegistry {
        var data: Map<String, PlayerStats> = emptyMap()

        override fun load(): Map<String, PlayerStats> = data

        override fun save(statsByNickname: Map<String, PlayerStats>) {
            data = statsByNickname.toMap()
        }
    }

    @Test
    fun `players statistics saved after game`() {
        val registry = InMemoryRegistry()
        val playerService = PlayerService(registry)
        val gameService = GameService()

        val alice = playerService.addPlayer("Alice")
        val bob = playerService.addPlayer("Bob")

        val (correctAlice, shipsAlice) =
            gameService.addShips(listOf(ShipPlacementRequest(2, 2, "H", 1)))
        val (correctBob, shipsBob) =
            gameService.addShips(listOf(ShipPlacementRequest(8, 8, "H", 1)))
        assertTrue(correctAlice && correctBob)

        val game = gameService.startGame(alice, bob, shipsAlice, shipsBob)
        val ending = game.makeMove(Coords(8, 8))

        val finished = ending.filterIsInstance<GameEvent.GameFinished>().first()
        assertEquals("Alice", finished.winnerNickname)

        val winnerName = finished.winnerNickname
        val loserName = "Bob"
        playerService.recordGameResult(winnerName, loserName)

        assertEquals(PlayerStats(1, 1, 0), playerService.getStats("Alice"))
        assertEquals(PlayerStats(1, 0, 1), playerService.getStats("Bob"))
        assertTrue(registry.data.containsKey("Alice") && registry.data.containsKey("Bob"))
    }
}
