package application

import domain.model.Player
import persistence.DbPaths
import persistence.GameHistoryStorage
import persistence.Storage

class PlayerService(
    private val registry: PlayerStatsRegistry =
        Storage(DbPaths.defaultDbPath()),
) {
    private val players = mutableListOf<Player>()
    private val stats = mutableMapOf<String, PlayerStats>()

    init {
        for ((nickname, playerStats) in registry.load()) {
            addToMemoryIfAbsent(nickname)
            stats[nickname] = playerStats
        }
    }

    fun addPlayer(nickname: String): Player {
        val trimmedNickname = nickname.trim()

        val existingNickname = players.firstOrNull { it.nickname.equals(trimmedNickname, true) }
        if (existingNickname != null) {
            return existingNickname
        }

        val player = Player(trimmedNickname)
        players.add(player)
        stats.putIfAbsent(trimmedNickname, PlayerStats())
        save()
        return player
    }

    fun recordGameResult(
        winnerNickname: String,
        loserNickname: String,
        firstPlayerNickname: String? = null,
        secondPlayerNickname: String? = null,
    ) {
        val winner = addPlayer(winnerNickname)
        val loser = addPlayer(loserNickname)

        val winnerStats = stats[winner.nickname] ?: PlayerStats()
        stats[winner.nickname] =
            winnerStats.copy(
                games = winnerStats.games + 1,
                wins = winnerStats.wins + 1,
            )

        val loserStats = stats[loser.nickname] ?: PlayerStats()
        stats[loser.nickname] =
            loserStats.copy(
                games = loserStats.games + 1,
                losses = loserStats.losses + 1,
            )

        if (firstPlayerNickname != null && secondPlayerNickname != null) {
            (registry as? GameHistoryStorage)?.addFinishedGame(
                firstPlayerNickname,
                secondPlayerNickname,
                winner.nickname,
            )
        }

        save()
    }

    fun getAll(): List<Player> {
        return players.toList()
    }

    fun getStats(nickname: String): PlayerStats {
        return stats[nickname] ?: PlayerStats()
    }

    fun getLeaderboard(): List<PlayerLeaderboardRow> {
        return players
            .map {
                PlayerLeaderboardRow(
                    it.nickname,
                    getStats(it.nickname),
                )
            }.sortedWith(
                compareByDescending<PlayerLeaderboardRow> { row -> row.stats.wins }
                    .thenByDescending { row -> row.stats.games }
                    .thenBy { row -> row.nickname.lowercase() },
            )
    }

    private fun save() {
        registry.save(stats)
    }

    private fun addToMemoryIfAbsent(nickname: String) {
        if (players.none { it.nickname.equals(nickname, true) }) {
            players.add(Player(nickname))
        }
    }
}
