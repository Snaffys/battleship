package console

import application.GameAdministrationService
import application.PlayerService
import application.PlayerStats
import application.PlayerStatsRegistry
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ConsoleControllerTest {
    private class InMemoryRegistry : PlayerStatsRegistry {
        var data: Map<String, PlayerStats> = emptyMap()

        override fun load(): Map<String, PlayerStats> = data

        override fun save(statsByNickname: Map<String, PlayerStats>) {
            data = statsByNickname.toMap()
        }
    }

    @Test
    fun `processCommand add should create player`() {
        val playerService = PlayerService(InMemoryRegistry())
        val controller = ConsoleController(playerService, GameAdministrationService(playerService))

        val shouldExit = controller.processCommand("add Alice")

        assertFalse(shouldExit)
        assertEquals(listOf("Alice"), playerService.getAll().map { it.nickname })
    }

    @Test
    fun `processCommand list should not exit`() {
        val playerService = PlayerService(InMemoryRegistry())
        playerService.addPlayer("Alice")
        playerService.addPlayer("Bob")
        val controller = ConsoleController(playerService, GameAdministrationService(playerService))

        val shouldExit = controller.processCommand("list")

        assertFalse(shouldExit)
    }

    @Test
    fun `processCommand unknown should not exit`() {
        val playerService = PlayerService(InMemoryRegistry())
        val controller = ConsoleController(playerService, GameAdministrationService(playerService))

        val shouldExit = controller.processCommand("unknown")

        assertFalse(shouldExit)
    }

    @Test
    fun `processCommand exit should return true`() {
        val playerService = PlayerService(InMemoryRegistry())
        val controller = ConsoleController(playerService, GameAdministrationService(playerService))

        val shouldExit = controller.processCommand("exit")

        assertTrue(shouldExit)
    }
}
