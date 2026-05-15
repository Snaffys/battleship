package integration

import application.GameAdministrationService
import org.junit.jupiter.api.Test
import java.nio.file.Files
import kotlin.test.assertTrue

class GameAdministrationStepsFileIntegrationTest {
    @Test
    fun `processStepsFile missing file returns error line`() {
        val admin = GameAdministrationService()
        val result = admin.processStepsFile("/nonexistent/battleshipSteps.txt")
        assertTrue(result.logLines.any { it.startsWith("ERROR:") })
    }

    @Test
    fun `processStepsFile runs lines from temp file`() {
        val temp = Files.createTempFile("playerSteps", ".txt")
        Files.write(temp, listOf("PLAYER tempPlayer"))

        val admin = GameAdministrationService()
        val result = admin.processStepsFile(temp.toString())

        assertTrue(result.logLines.any { it.contains("tempPlayer") })
        Files.deleteIfExists(temp)
    }
}
