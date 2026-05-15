package application

data class PlayerStats(
    val games: Int = 0,
    val wins: Int = 0,
    val losses: Int = 0,
)

data class PlayerLeaderboardRow(
    val nickname: String,
    val stats: PlayerStats,
)
