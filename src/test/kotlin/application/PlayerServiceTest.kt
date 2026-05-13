package application

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class PlayerServiceTest {
    @Test
    fun `add player should store player`() {
        val service = PlayerService()

        val player = service.addPlayer("test")

        assertEquals(1, service.getAll().size)
        assertEquals("test", player.nickname)
    }
}
