package application

import domain.value.Coords
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GameServiceTest {
    @Test
    fun `addShips should fail when ship goes out of bounds horizontally`() {
        val service = GameService()

        val (correct, ships) =
            service.addShips(
                listOf(
                    ShipPlacementRequest(9, 0, "H", 2),
                ),
            )

        assertFalse(correct)
        assertTrue(ships.isEmpty())
    }

    @Test
    fun `addShips should fail when ship goes out of bounds vertically`() {
        val service = GameService()

        val (correct, ships) =
            service.addShips(
                listOf(
                    ShipPlacementRequest(0, 9, "V", 2),
                ),
            )

        assertFalse(correct)
        assertTrue(ships.isEmpty())
    }

    @Test
    fun `addShips should create ships for valid horizontal and vertical placements`() {
        val service = GameService()

        val (correct, ships) =
            service.addShips(
                listOf(
                    ShipPlacementRequest(0, 0, "H", 3),
                    ShipPlacementRequest(5, 5, "V", 2),
                ),
            )

        assertTrue(correct)
        assertEquals(2, ships.size)
        assertTrue(ships.any { ship -> ship.getCells() == setOf(Coords(0, 0), Coords(1, 0), Coords(2, 0)) })
        assertTrue(ships.any { ship -> ship.getCells() == setOf(Coords(5, 5), Coords(5, 6)) })
    }

    @Test
    fun `addShips should fail when direction is invalid`() {
        val service = GameService()

        val (correct, ships) = service.addShips(listOf(ShipPlacementRequest(0, 0, "X", 2)))

        assertFalse(correct)
        assertTrue(ships.isEmpty())
    }

    @Test
    fun `addShips should fail when ships touch diagonally`() {
        val service = GameService()

        val (correct, ships) =
            service.addShips(
                listOf(
                    ShipPlacementRequest(0, 0, "H", 1),
                    ShipPlacementRequest(1, 1, "H", 1),
                ),
            )

        assertFalse(correct)
        assertTrue(ships.isEmpty())
    }
}
