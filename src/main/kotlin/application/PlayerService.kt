package application

import domain.model.Player
import java.util.UUID

class PlayerService {
    private val players = mutableListOf<Player>()

    fun addPlayer(nickname: String): Player {
        val player = Player(UUID.randomUUID().toString(), nickname)
        players.add(player)
        return player
    }

    fun getAll(): List<Player> = players
}
