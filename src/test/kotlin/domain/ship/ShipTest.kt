package domain.ship

import domain.value.Coords
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ShipTest {
    @Test
    fun `hit should return true when coords match`() {
        val ship = ShipImpl(setOf(Coords(1, 1)))

        val result = ship.hit(Coords(1, 1))

        assertTrue(result)
    }

    @Test
    fun `hit should return false when miss`() {
        val ship = ShipImpl(setOf(Coords(1, 1)))

        val result = ship.hit(Coords(2, 2))

        assertFalse(result)
    }

    @Test
    fun `ship should sink when all cells hit`() {
        val ship = ShipImpl(setOf(Coords(1, 1), Coords(1, 2)))

        ship.hit(Coords(1, 1))
        ship.hit(Coords(1, 2))

        assertTrue(ship.isSunk())
    }

    @Test
    fun `getCells returns construction cells`() {
        val cells = setOf(Coords(0, 0), Coords(1, 0))
        val ship = ShipImpl(cells)
        assertEquals(cells, ship.getCells())
    }
}
