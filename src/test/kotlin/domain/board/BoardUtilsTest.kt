package domain.board

import domain.value.Coords
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class BoardUtilsTest {
    @Test
    fun `in bounds corners`() {
        assertFalse(BoardUtils.isOutOfBounds(Coords(0, 0)))
        assertFalse(BoardUtils.isOutOfBounds(Coords(9, 9)))
    }

    @Test
    fun `out of bounds`() {
        assertTrue(BoardUtils.isOutOfBounds(Coords(-1, 0)))
        assertTrue(BoardUtils.isOutOfBounds(Coords(0, -1)))
        assertTrue(BoardUtils.isOutOfBounds(Coords(10, 0)))
        assertTrue(BoardUtils.isOutOfBounds(Coords(0, 10)))
    }
}
