package domain.board

import domain.game.event.ShotResult
import domain.ship.ShipImpl
import domain.value.Coords
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class BoardTest {
    @Test
    fun `shoot should return HIT`() {
        val ship = ShipImpl(setOf(Coords(1, 1)))
        val board = BoardImpl(listOf(ship))

        val result = board.shoot(Coords(1, 1))

        assertEquals(ShotResult.HIT, result)
    }

    @Test
    fun `shoot should return MISS`() {
        val board = BoardImpl(emptyList())

        val result = board.shoot(Coords(5, 5))

        assertEquals(ShotResult.MISS, result)
    }

    @Test
    fun `isAlreadyShot should work`() {
        val board = BoardImpl(emptyList())

        board.shoot(Coords(1, 1))

        assertTrue(board.isAlreadyShot(Coords(1, 1)))
    }

    @Test
    fun `allShipsSunk should be true when empty`() {
        val board = BoardImpl(emptyList())

        assertTrue(board.allShipsSunk())
    }

    @Test
    fun `getShips should return constructor ships`() {
        val ship = ShipImpl(setOf(Coords(1, 1)))
        val board = BoardImpl(listOf(ship))

        assertEquals(listOf(ship), board.getShips())
    }

    @Test
    fun `shoot should return MISS for out of bounds coordinates when called directly`() {
        val board = BoardImpl(emptyList())

        val result = board.shoot(Coords(11, 3))

        assertEquals(ShotResult.MISS, result)
    }
}
