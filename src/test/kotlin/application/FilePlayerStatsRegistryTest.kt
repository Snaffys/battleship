package application

import org.junit.jupiter.api.Test
import java.nio.file.Files
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FilePlayerStatsRegistryTest {
    @Test
    fun `save and load should persist player stats`() {
        val tempFile = Files.createTempFile("battleshipStats", ".csv")
        val registry = FilePlayerStatsRegistry(tempFile)

        val input =
            mapOf(
                "Alice" to PlayerStats(5, 3, 2),
                "Bob" to PlayerStats(7, 4, 3),
            )

        registry.save(input)
        val loaded = registry.load()
        val rows = Files.readAllLines(tempFile)

        assertEquals(input, loaded)
        assertTrue(rows.isNotEmpty())
        assertEquals("nickname;games;wins;losses", rows.first())
    }

    @Test
    fun `load should return empty when file does not exist`() {
        val tempFile = Files.createTempFile("battleshipMissing", ".csv")
        Files.deleteIfExists(tempFile)
        val registry = FilePlayerStatsRegistry(tempFile)
        assertEquals(emptyMap(), registry.load())
    }

    @Test
    fun `load should skip malformed rows`() {
        val tempFile = Files.createTempFile("battleshipBad", ".csv")
        Files.write(
            tempFile,
            listOf(
                "nickname;games;wins;losses",
                "Good;1;1;0",
                "badRow",
                "AlsoGood;2;0;2",
            ),
        )
        val loaded = FilePlayerStatsRegistry(tempFile).load()
        assertEquals(2, loaded.size)
        assertEquals(PlayerStats(1, 1, 0), loaded["Good"])
        assertEquals(PlayerStats(2, 0, 2), loaded["AlsoGood"])
    }

    @Test
    fun `save should order rows by nickname ignoring case`() {
        val tempFile = Files.createTempFile("battleshipSort", ".csv")
        val registry = FilePlayerStatsRegistry(tempFile)
        registry.save(
            mapOf(
                "bob" to PlayerStats(1, 0, 1),
                "Alice" to PlayerStats(2, 1, 1),
            ),
        )
        val rows = Files.readAllLines(tempFile)
        assertEquals("nickname;games;wins;losses", rows[0])
        assertEquals("Alice;2;1;1", rows[1])
        assertEquals("bob;1;0;1", rows[2])
    }
}
