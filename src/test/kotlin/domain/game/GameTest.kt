package domain.game

import domain.board.BoardImpl
import domain.game.event.GameEvent
import domain.game.event.ShotResult
import domain.model.Player
import domain.ship.ShipImpl
import domain.value.Coords
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class GameTest {
    private val p1 = Player("1", "A")
    private val p2 = Player("2", "B")

    private fun shipAt(
        x: Int,
        y: Int,
    ) = ShipImpl(setOf(Coords(x, y)))

    @Test
    fun `move should register HIT`() {
        val game =
            GameImpl(
                p1,
                p2,
                BoardImpl(listOf(shipAt(1, 1))),
                BoardImpl(listOf(shipAt(2, 2))),
            )

        val events = game.makeMove(Coords(2, 2))

        val move = events.filterIsInstance<GameEvent.MoveMade>().first()

        assertEquals(ShotResult.HIT, move.result)
    }

    @Test
    fun `MISS should switch player`() {
        val game =
            GameImpl(
                p1,
                p2,
                BoardImpl(emptyList()),
                BoardImpl(emptyList()),
            )

        val events = game.makeMove(Coords(0, 0))

        assertTrue(
            events.any { it is GameEvent.PlayerSwitched },
        )
    }

    @Test
    fun `should detect ship sunk`() {
        val ship = ShipImpl(setOf(Coords(1, 1)))

        val game =
            GameImpl(
                p1,
                p2,
                BoardImpl(emptyList()),
                BoardImpl(listOf(ship)),
            )

        val events = game.makeMove(Coords(1, 1))

        assertTrue(
            events.any { it is GameEvent.ShipSunk },
        )
    }

    @Test
    fun `should finish game when all ships are sunk`() {
        val ship = ShipImpl(setOf(Coords(1, 1)))

        val game =
            GameImpl(
                p1,
                p2,
                BoardImpl(emptyList()),
                BoardImpl(listOf(ship)),
            )

        val events = game.makeMove(Coords(1, 1))

        assertTrue(
            events.any { it is GameEvent.GameFinished },
        )
    }

    @Test
    fun `already shot should return InvalidMove`() {
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

        assertTrue(
            events.any { it is GameEvent.InvalidMove },
        )
    }
}
