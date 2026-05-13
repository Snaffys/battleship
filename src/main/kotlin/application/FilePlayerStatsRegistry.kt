package application

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class FilePlayerStatsRegistry(private val filePath: Path = defaultPath()) : PlayerStatsRegistry {
    private val header = "nickname;games;wins;losses"

    override fun load(): Map<String, PlayerStats> {
        if (!Files.exists(filePath)) {
            return emptyMap()
        }

        val rows = Files.readAllLines(filePath)
        val result = mutableMapOf<String, PlayerStats>()

        for ((index, row) in rows.withIndex()) {
            if (index == 0 && row.trim().equals(header, true)) {
                continue
            }

            mergeStatsFromCsvRow(row, result)
        }

        return result
    }

    private fun mergeStatsFromCsvRow(
        row: String,
        result: MutableMap<String, PlayerStats>,
    ) {
        val parts = row.split(";")

        if (parts.size != 4) {
            return
        }

        val nickname = parts[0]
        val games = parts[1].toIntOrNull() ?: return
        val wins = parts[2].toIntOrNull() ?: return
        val losses = parts[3].toIntOrNull() ?: return

        result[nickname] = PlayerStats(games, wins, losses)
    }

    override fun save(statsByNickname: Map<String, PlayerStats>) {
        ensureDirectoryExists(filePath.parent)

        val sortedEntries = sortedEntriesFrom(statsByNickname)
        val lines = csvLinesFrom(sortedEntries)

        val allLines = mutableListOf<String>()
        allLines.add(header)
        allLines.addAll(lines)

        Files.write(filePath, allLines)
    }

    private fun sortedEntriesFrom(statsByNickname: Map<String, PlayerStats>): MutableList<Map.Entry<String, PlayerStats>> {
        val entries = statsByNickname.entries.toList()
        val sortedEntries = mutableListOf<Map.Entry<String, PlayerStats>>()

        for (entry in entries) {
            sortedEntries.add(entry)
        }

        sortedEntries.sortBy { it.key.lowercase() }

        return sortedEntries
    }

    private fun csvLinesFrom(sortedEntries: List<Map.Entry<String, PlayerStats>>): MutableList<String> {
        val lines = mutableListOf<String>()

        for (entry in sortedEntries) {
            val nickname = entry.key
            val stats = entry.value

            val line =
                nickname + ";" + stats.games + ";" +
                    stats.wins + ";" + stats.losses

            lines.add(line)
        }

        return lines
    }

    private fun ensureDirectoryExists(path: Path?) {
        if (path == null) {
            return
        }

        if (!Files.exists(path)) {
            Files.createDirectories(path)
        }
    }

    companion object {
        private fun defaultPath(): Path {
            val home = System.getProperty("user.home")

            return Paths.get(home, ".battleship", "player-stats.csv")
        }
    }
}
