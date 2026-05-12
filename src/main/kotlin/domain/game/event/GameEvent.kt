package domain.game.event

import domain.value.Coords

sealed class GameEvent {
    abstract fun output(): String

    data class MoveMade(
        val coords: Coords,
        val result: ShotResult,
        val playerNickname: String,
    ) : GameEvent() {
        override fun output(): String {
            val resultText =
                when (result) {
                    ShotResult.HIT -> "hit"
                    ShotResult.MISS -> "miss"
                }
            return "$playerNickname shoots (${coords.x},${coords.y}): $resultText"
        }
    }

    data class PlayerSwitched(val nextPlayerNickname: String) : GameEvent() {
        override fun output(): String {
            return "Turn switched to $nextPlayerNickname"
        }
    }

    data class GameFinished(val winnerNickname: String) : GameEvent() {
        override fun output(): String {
            return "Game finished. Winner: $winnerNickname"
        }
    }

    data class ShipSunk(val playerNickname: String) : GameEvent() {
        override fun output(): String {
            return "$playerNickname sunk a ship"
        }
    }

    data class InvalidMove(val reason: String) : GameEvent() {
        override fun output(): String {
            return "Invalid move: $reason"
        }
    }
}
