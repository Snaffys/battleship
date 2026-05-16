package persistence

import application.PlayerStats
import application.PlayerStatsRegistry
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.nio.file.Files
import java.nio.file.Path

class Storage(dbFilePath: Path) : PlayerStatsRegistry, GameHistoryStorage {
    private val database: Database

    init {
        val parent = dbFilePath.parent
        if (parent != null && !Files.exists(parent)) {
            Files.createDirectories(parent)
        }

        val urlPath = dbFilePath.toAbsolutePath().normalize().toString()

        database = Database.connect("jdbc:sqlite:$urlPath", "org.sqlite.JDBC")

        transaction(database) {
            SchemaUtils.createMissingTablesAndColumns(PlayersTable, GameHistoryTable)
        }
    }

    override fun load(): Map<String, PlayerStats> {
        return transaction(database) {
            val result = mutableMapOf<String, PlayerStats>()

            val rows = PlayersTable.selectAll()

            for (row in rows) {
                val nickname = row[PlayersTable.nickname]

                val stats =
                    PlayerStats(
                        row[PlayersTable.games],
                        row[PlayersTable.wins],
                        row[PlayersTable.losses],
                    )
                result[nickname] = stats
            }

            result
        }
    }

    override fun save(statsByNickname: Map<String, PlayerStats>) {
        transaction(database) {
            for ((nickname, stats) in statsByNickname) {
                val existing = PlayersTable.select { PlayersTable.nickname eq nickname }.singleOrNull()

                if (existing == null) {
                    PlayersTable.insert {
                        it[PlayersTable.nickname] = nickname
                        it[PlayersTable.games] = stats.games
                        it[PlayersTable.wins] = stats.wins
                        it[PlayersTable.losses] = stats.losses
                    }
                } else {
                    PlayersTable.update({ PlayersTable.nickname eq nickname }) {
                        it[PlayersTable.games] = stats.games
                        it[PlayersTable.wins] = stats.wins
                        it[PlayersTable.losses] = stats.losses
                    }
                }
            }
        }
    }

    override fun addFinishedGame(
        firstPlayerNickname: String,
        secondPlayerNickname: String,
        winnerNickname: String,
    ) {
        transaction(database) {
            GameHistoryTable.insert {
                it[GameHistoryTable.player1Nickname] = firstPlayerNickname
                it[GameHistoryTable.player2Nickname] = secondPlayerNickname
                it[GameHistoryTable.winnerNickname] = winnerNickname
            }
        }
    }

    override fun getFinishedGames(limit: Int): List<FinishedGameRecord> {
        return transaction(database) {
            val result = mutableListOf<FinishedGameRecord>()

            val rows =
                GameHistoryTable.selectAll()
                    .orderBy(GameHistoryTable.id, SortOrder.DESC).limit(limit)

            for (row in rows) {
                val record =
                    FinishedGameRecord(
                        row[GameHistoryTable.id].value,
                        row[GameHistoryTable.player1Nickname],
                        row[GameHistoryTable.player2Nickname],
                        row[GameHistoryTable.winnerNickname],
                    )
                result.add(record)
            }

            result
        }
    }
}
