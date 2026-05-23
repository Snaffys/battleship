package persistence

data class FinishedGameRecord(
    val id: Long,
    val firstPlayerNickname: String,
    val secondPlayerNickname: String,
    val winnerNickname: String,
)

interface GameHistoryStorage {
    fun addFinishedGame(
        firstPlayerNickname: String,
        secondPlayerNickname: String,
        winnerNickname: String,
    )

    fun getFinishedGames(limit: Int = 10): List<FinishedGameRecord>
}
