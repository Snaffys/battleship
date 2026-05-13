package integration

import domain.board.BoardImpl
import domain.game.GameImpl
import domain.game.event.GameEvent
import domain.game.event.ShotResult
import domain.model.Player
import domain.ship.ShipImpl
import domain.value.Coords
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class GameIntegrationTest {
    private val p1 = Player("1", "A")
    private val p2 = Player("2", "B")

    private fun ship(
        x: Int,
        y: Int,
    ) = ShipImpl(setOf(Coords(x, y)))

    @Test
    fun `miss should switch player`() {
        val game =
            GameImpl(
                p1,
                p2,
                BoardImpl(emptyList()),
                BoardImpl(emptyList()),
            )

        val events = game.makeMove(Coords(0, 0))

        assertTrue(events.any { it is GameEvent.MoveMade && it.result == ShotResult.MISS })
        assertTrue(events.any { it is GameEvent.PlayerSwitched })
    }

    @Test
    fun `hit should not switch player`() {
        val game =
            GameImpl(
                p1,
                p2,
                BoardImpl(emptyList()),
                BoardImpl(listOf(ship(1, 1))),
            )

        val events = game.makeMove(Coords(1, 1))

        assertTrue(events.any { it is GameEvent.MoveMade && it.result == ShotResult.HIT })
        assertTrue(events.none { it is GameEvent.PlayerSwitched })
    }

    @Test
    fun `should detect repeated shot`() {
        val game =
            GameImpl(
                p1,
                p2,
                BoardImpl(emptyList()),
                BoardImpl(emptyList()),
            )

        game.makeMove(Coords(1, 1))
        game.makeMove(Coords(1, 1))
        val events = game.makeMove(Coords(1, 1))

        assertTrue(events.any { it is GameEvent.InvalidMove })
    }

    @Test
    fun `should detect ship sunk`() {
        val game =
            GameImpl(
                p1,
                p2,
                BoardImpl(emptyList()),
                BoardImpl(listOf(ship(1, 1))),
            )

        val events = game.makeMove(Coords(1, 1))

        assertTrue(events.any { it is GameEvent.ShipSunk })
    }

    @Test
    fun `should finish game when all ships sunk`() {
        val game =
            GameImpl(
                p1,
                p2,
                BoardImpl(emptyList()),
                BoardImpl(listOf(ship(1, 1))),
            )

        val events = game.makeMove(Coords(1, 1))

        assertTrue(events.any { it is GameEvent.GameFinished })
    }
}
