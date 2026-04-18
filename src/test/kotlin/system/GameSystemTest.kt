package system

import application.GameService
import application.PlayerService
import application.ShipPlacementRequest
import domain.value.Coords
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class GameSystemTest {

    @Test
    fun `full game flow should work`() {
        val playerService = PlayerService()
        val gameService = GameService()

        val p1 = playerService.addPlayer("A")
        val p2 = playerService.addPlayer("B")

        val ships1 = listOf(
            ShipPlacementRequest(1, 1, "H", 1)
        )

        val ships2 = listOf(
            ShipPlacementRequest(2, 2, "H", 1)
        )

        val (ok1, s1) = gameService.addShips(ships1)
        val (ok2, s2) = gameService.addShips(ships2)

        assertTrue(ok1)
        assertTrue(ok2)

        val game = gameService.startGame(p1, p2, s1, s2)

        val events1 = game.makeMove(Coords(1, 1))
        val events2 = game.makeMove(Coords(0, 0))

        assertTrue(events1.isNotEmpty())
        assertTrue(events2.isNotEmpty())
    }

    @Test
    fun `invalid ship placement should fail`() {
        val gameService = GameService()

        val result = gameService.addShips(
            listOf(
                ShipPlacementRequest(0, 0, "X", 3)
            )
        )

        assertTrue(!result.first)
    }

    @Test
    fun `overlapping ships should fail`() {
        val gameService = GameService()

        val result = gameService.addShips(
            listOf(
                ShipPlacementRequest(0, 0, "H", 3),
                ShipPlacementRequest(1, 0, "V", 3)
            )
        )

        assertTrue(!result.first)
    }
}