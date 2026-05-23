package integration

import application.GameAdministrationService
import application.PlayerService
import application.PlayerStats
import application.PlayerStatsRegistry
import org.junit.jupiter.api.Test
import persistence.Storage
import java.nio.file.Files
import kotlin.test.assertTrue

class GameAdministrationStepsFileIntegrationTest {
    private class InMemoryRegistry : PlayerStatsRegistry {
        var data: Map<String, PlayerStats> = emptyMap()

        override fun load(): Map<String, PlayerStats> = data

        override fun save(statsByNickname: Map<String, PlayerStats>) {
            data = statsByNickname.toMap()
        }
    }

    @Test
    fun `processStepsFile missing file returns error line`() {
        val admin = GameAdministrationService(PlayerService(InMemoryRegistry()))
        val result = admin.processStepsFile("/nonexistent/battleshipSteps.txt")
        assertTrue(result.logLines.any { it.startsWith("ERROR:") })
    }

    @Test
    fun `processStepsFile runs lines from temp file`() {
        val temp = Files.createTempFile("testPlayerSteps", ".txt")
        Files.write(temp, listOf("PLAYER tempPlayer"))

        val admin = GameAdministrationService(PlayerService(InMemoryRegistry()))
        val result = admin.processStepsFile(temp.toString())

        assertTrue(result.logLines.any { it.contains("tempPlayer") })
        Files.deleteIfExists(temp)
    }

    @Test
    fun `processStepsFile persists registered player in db`() {
        val db = Files.createTempFile("testSteps", ".db")
        Files.deleteIfExists(db)
        val store = Storage(db)
        val playerService = PlayerService(store)
        val admin = GameAdministrationService(playerService)

        val temp = Files.createTempFile("testSteps", ".txt")
        Files.write(temp, listOf("PLAYER dbPlayer"))

        val result = admin.processStepsFile(temp.toString())

        assertTrue(result.logLines.any { it.contains("dbPlayer") })
        val playerServiceFilled = PlayerService(store)
        assertTrue(playerServiceFilled.getAll().any { it.nickname == "dbPlayer" })
        Files.deleteIfExists(temp)
    }
}
