package persistence

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.dao.id.LongIdTable

object PlayersTable : IntIdTable("players") {
    val nickname = varchar("nickname", 128).uniqueIndex()
    val games = integer("games").default(0)
    val wins = integer("wins").default(0)
    val losses = integer("losses").default(0)
}

object GameHistoryTable : LongIdTable("game_history") {
    val player1Nickname = varchar("player1_nickname", 128)
    val player2Nickname = varchar("player2_nickname", 128)
    val winnerNickname = varchar("winner_nickname", 128)
}
