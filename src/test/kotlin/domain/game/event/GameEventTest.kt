package domain.game.event

import domain.value.Coords
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class GameEventTest {
    @Test
    fun `MoveMade output contains hit`() {
        val e =
            GameEvent.MoveMade(
                Coords(1, 2),
                ShotResult.HIT,
                "A",
            )
        assertTrue(e.output().contains("hit"))
        assertTrue(e.output().contains("(1,2)"))
        assertTrue(e.output().contains("A"))
    }

    @Test
    fun `MoveMade output contains miss`() {
        val e =
            GameEvent.MoveMade(
                coords = Coords(0, 0),
                result = ShotResult.MISS,
                playerNickname = "B",
            )
        assertTrue(e.output().contains("miss"))
    }

    @Test
    fun `PlayerSwitched output`() {
        val e = GameEvent.PlayerSwitched("Next")
        assertTrue(e.output().contains("Next"))
    }

    @Test
    fun `GameFinished output`() {
        val e = GameEvent.GameFinished("Winner")
        assertTrue(e.output().contains("Winner"))
    }

    @Test
    fun `ShipSunk output`() {
        val e = GameEvent.ShipSunk("Player")
        assertTrue(e.output().contains("Player"))
        assertTrue(e.output().contains("sunk"))
    }

    @Test
    fun `InvalidMove output`() {
        val e = GameEvent.InvalidMove(reason = "bad")
        assertTrue(e.output().contains("bad"))
    }
}
