package system

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.File
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit

class BattleshipConsoleMainSystemTest {
    companion object {
        private val fullGameLines =
            listOf(
                "PLAYER admin-a",
                "PLAYER admin-b",
                "PLACEMENT admin-a 0 0 H 4",
                "PLACEMENT admin-a 0 2 H 3",
                "PLACEMENT admin-a 0 4 H 3",
                "PLACEMENT admin-a 0 6 H 2",
                "PLACEMENT admin-a 3 6 H 2",
                "PLACEMENT admin-a 6 6 H 2",
                "PLACEMENT admin-a 9 0 V 1",
                "PLACEMENT admin-a 9 2 V 1",
                "PLACEMENT admin-a 9 4 V 1",
                "PLACEMENT admin-a 9 6 V 1",
                "PLACEMENT admin-b 0 0 H 4",
                "PLACEMENT admin-b 0 2 H 3",
                "PLACEMENT admin-b 0 4 H 3",
                "PLACEMENT admin-b 0 6 H 2",
                "PLACEMENT admin-b 3 6 H 2",
                "PLACEMENT admin-b 6 6 H 2",
                "PLACEMENT admin-b 9 0 V 1",
                "PLACEMENT admin-b 9 2 V 1",
                "PLACEMENT admin-b 9 4 V 1",
                "PLACEMENT admin-b 9 6 V 1",
                "START admin-a admin-b",
                "SHOT 0 0",
                "SHOT 1 0",
                "SHOT 2 0",
                "SHOT 3 0",
                "SHOT 0 2",
                "SHOT 1 2",
                "SHOT 2 2",
                "SHOT 0 4",
                "SHOT 1 4",
                "SHOT 2 4",
                "SHOT 0 6",
                "SHOT 1 6",
                "SHOT 3 6",
                "SHOT 4 6",
                "SHOT 6 6",
                "SHOT 7 6",
                "SHOT 9 0",
                "SHOT 9 2",
                "SHOT 9 4",
                "SHOT 9 6",
            )
    }

    private fun runConsole(
        commands: List<String>,
        delayMs: Long = 200,
    ): String {
        val classpath =
            System.getProperty("battleship.test.runtime.classpath")
                ?: error("Run tests with Gradle")

        val javaBin =
            File(System.getProperty("java.home"), "bin/java").absolutePath

        val process =
            ProcessBuilder(
                javaBin,
                "-cp",
                classpath,
                "app.MainKt",
                "--console",
            )
                .redirectErrorStream(true)
                .start()

        Thread.sleep(500)

        val writer = process.outputStream.bufferedWriter(StandardCharsets.UTF_8)

        for (command in commands) {
            writer.write(command)
            writer.newLine()
            writer.flush()
            Thread.sleep(delayMs)
        }

        writer.close()

        val finished = process.waitFor(120, TimeUnit.SECONDS)

        assertTrue(finished, "Process should exit")

        return process.inputStream.bufferedReader().readText()
    }

    @Test
    fun `help, add, list, exit session`() {
        val output =
            runConsole(
                listOf(
                    "help",
                    "add Player3",
                    "list",
                    "exit",
                ),
            )

        assertTrue(output.contains("Console administration mode"), output)
        assertTrue(output.contains("Commands: add <nickname>"), output)
        assertTrue(output.contains("Added player: Player3"), output)
        assertTrue(output.contains("Player1"), output)
        assertTrue(output.contains("Player2"), output)
    }

    @Test
    fun `full game run and produce winner`() {
        val gameFile = File.createTempFile("game", ".txt")

        gameFile.writeText(fullGameLines.joinToString("\n"))

        val output =
            runConsole(
                listOf(
                    "start ${gameFile.absolutePath}",
                    "exit",
                ),
                5000,
            )

        assertTrue(!output.contains("ERROR:"), output)
        assertTrue(output.contains("Game started: admin-a vs admin-b"), output)
        assertTrue(output.contains("Game finished. Winner: admin-a"), output)
    }
}
