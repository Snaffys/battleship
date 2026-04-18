package application

import domain.model.Player
import domain.ship.ShipImpl
import domain.value.Coords
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class GameServiceTest {

    @Test
    fun `startGame should create Game`() {
        val service = GameService()

        val p1 = Player("1", "A")
        val p2 = Player("2", "B")

        val ships = listOf(
            ShipImpl(setOf(Coords(1, 1)))
        )

        val game = service.startGame(p1, p2, ships, ships)

        assertNotNull(game)
    }
}