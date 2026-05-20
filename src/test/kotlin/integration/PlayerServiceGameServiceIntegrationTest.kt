package integration

import application.GameService
import application.PlayerService
import application.PlayerStats
import application.PlayerStatsRegistry
import application.ShipPlacementRequest
import domain.game.event.GameEvent
import domain.game.event.ShotResult
import domain.value.Coords
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class PlayerServiceGameServiceIntegrationTest {
    private class InMemoryRegistry : PlayerStatsRegistry {
        var data: Map<String, PlayerStats> = emptyMap()

        override fun load(): Map<String, PlayerStats> = data

        override fun save(statsByNickname: Map<String, PlayerStats>) {
            data = statsByNickname.toMap()
        }
    }

    @Test
    fun `register players, place ships, start game, record hit and sink`() {
        val playerService = PlayerService(InMemoryRegistry())
        val gameService = GameService()

        val p1 = playerService.addPlayer("Anna")
        val p2 = playerService.addPlayer("Bob")

        val (correctFleet1, ships1) =
            gameService.addShips(listOf(ShipPlacementRequest(1, 1, "H", 1)))
        val (correctFleet2, ships2) =
            gameService.addShips(listOf(ShipPlacementRequest(9, 9, "H", 1)))
        assertTrue(correctFleet1 && correctFleet2)

        val game = gameService.startGame(p1, p2, ships1, ships2)

        val events = game.makeMove(Coords(9, 9))

        assertTrue(events.any { it is GameEvent.MoveMade && it.result == ShotResult.HIT })
        assertTrue(events.any { it is GameEvent.ShipSunk })
        assertTrue(events.any { it is GameEvent.GameFinished })
        val finished =
            events.filterIsInstance<GameEvent.GameFinished>().firstOrNull()
        assertTrue(finished != null && finished.winnerNickname == "Anna")
    }

    @Test
    fun `miss switches turn, hit on next move sinks last ship, current player becomes winner`() {
        val playerService = PlayerService(InMemoryRegistry())
        val gameService = GameService()

        val alpha = playerService.addPlayer("Anna")
        val beta = playerService.addPlayer("Bob")

        val (correct1, s1) = gameService.addShips(listOf(ShipPlacementRequest(1, 1, "H", 1)))
        val (correct2, s2) = gameService.addShips(listOf(ShipPlacementRequest(9, 9, "H", 1)))
        assertTrue(correct1 && correct2)

        val game = gameService.startGame(alpha, beta, s1, s2)

        val first = game.makeMove(Coords(5, 5))
        assertTrue(first.any { it is GameEvent.MoveMade && it.result == ShotResult.MISS })
        assertTrue(first.any { it is GameEvent.PlayerSwitched })

        val second = game.makeMove(Coords(1, 1))
        assertTrue(second.any { it is GameEvent.MoveMade && it.result == ShotResult.HIT })
        assertTrue(second.any { it is GameEvent.GameFinished })
        val winner =
            second.filterIsInstance<GameEvent.GameFinished>().first().winnerNickname
        assertTrue(winner == "Bob")
    }

    @Test
    fun `second shot at same cell returns invalid move`() {
        val playerService = PlayerService(InMemoryRegistry())
        val gameService = GameService()

        val p1 = playerService.addPlayer("A")
        val p2 = playerService.addPlayer("B")

        val (correct1, s1) =
            gameService.addShips(listOf(ShipPlacementRequest(9, 9, "H", 1)))
        val (correct2, s2) =
            gameService.addShips(listOf(ShipPlacementRequest(8, 8, "H", 1)))
        assertTrue(correct1 && correct2)

        val game = gameService.startGame(p1, p2, s1, s2)
        game.makeMove(Coords(5, 5))
        game.makeMove(Coords(3, 3))
        val repeat = game.makeMove(Coords(5, 5))

        assertTrue(repeat.any { it is GameEvent.InvalidMove })
    }
}
