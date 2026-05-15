package console

import application.GameAdministrationService
import application.PlayerService
import application.PlayerStats
import application.PlayerStatsRegistry
import org.junit.jupiter.api.Test
import java.nio.file.Files
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
        assertEquals(listOf("Alice", "Bob"), playerService.getAll().map { it.nickname })
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

    @Test
    fun `processCommand help should not exit`() {
        val playerService = PlayerService(InMemoryRegistry())
        val controller = ConsoleController(playerService, GameAdministrationService(playerService))

        val shouldExit = controller.processCommand("help")

        assertFalse(shouldExit)
    }

    @Test
    fun `processCommand list when empty should not exit`() {
        val playerService = PlayerService(InMemoryRegistry())
        val controller = ConsoleController(playerService, GameAdministrationService(playerService))

        val shouldExit = controller.processCommand("list")

        assertFalse(shouldExit)
        assertTrue(playerService.getAll().isEmpty())
    }

    @Test
    fun `processCommand start should run administration from file`() {
        val temp = Files.createTempFile("consoleSteps", ".txt")
        Files.write(temp, listOf("PLAYER sys-a"))

        val playerService = PlayerService(InMemoryRegistry())
        val controller = ConsoleController(playerService, GameAdministrationService(playerService))

        val shouldExit = controller.processCommand("start ${temp.toAbsolutePath()}")

        assertFalse(shouldExit)
        assertEquals(listOf("sys-a"), playerService.getAll().map { it.nickname })
        Files.deleteIfExists(temp)
    }

    @Test
    fun `processCommand start without path should not exit`() {
        val playerService = PlayerService(InMemoryRegistry())
        val controller = ConsoleController(playerService, GameAdministrationService(playerService))

        val shouldExit = controller.processCommand("start")

        assertFalse(shouldExit)
        assertTrue(playerService.getAll().isEmpty())
    }

    @Test
    fun `processCommand add without nickname should not create player`() {
        val playerService = PlayerService(InMemoryRegistry())
        val controller = ConsoleController(playerService, GameAdministrationService(playerService))

        val shouldExit = controller.processCommand("add")

        assertFalse(shouldExit)
        assertTrue(playerService.getAll().isEmpty())
    }
}
