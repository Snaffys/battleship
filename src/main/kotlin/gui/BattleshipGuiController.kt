package gui

import application.PlayerService
import domain.model.Player

class BattleshipGuiController(
    private val playerService: PlayerService = PlayerService(),
) {
    fun addPlayer(nickname: String): Player? {
        val trimmedNickname = nickname.trim()
        if (trimmedNickname.isEmpty()) {
            return null
        }
        return playerService.addPlayer(trimmedNickname)
    }

    fun getPlayers(): List<Player> {
        return playerService.getAll()
    }

    fun getLeaderboardText(): List<String> {
        return playerService.getLeaderboard().map {
            "${it.nickname}: games=${it.stats.games}, wins=${it.stats.wins}, losses=${it.stats.losses}"
        }
    }
}
