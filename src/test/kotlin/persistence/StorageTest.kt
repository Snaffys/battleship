package persistence

import application.PlayerStats
import org.junit.jupiter.api.Test
import java.nio.file.Files
import kotlin.test.assertEquals

class StorageTest {
    @Test
    fun `save in and load from db`() {
        val db = Files.createTempFile("testSaveAndLoad", ".db")
        Files.deleteIfExists(db)
        val store = Storage(db)

        val stats =
            mapOf(
                "A" to PlayerStats(2, 1, 1),
                "B" to PlayerStats(1, 0, 1),
            )
        store.save(stats)

        assertEquals(stats, store.load())
    }

    @Test
    fun `addFinishedGame in and getFinishedGames from db`() {
        val db = Files.createTempFile("testBattleHistory", ".db")
        Files.deleteIfExists(db)
        val store = Storage(db)

        store.addFinishedGame("p1", "p2", "p1")
        store.addFinishedGame("p1", "p2", "p2")

        val recent = store.getFinishedGames(2)
        assertEquals(2, recent.size)
        assertEquals("p2", recent[0].winnerNickname)
        assertEquals("p1", recent[1].winnerNickname)
    }
}
