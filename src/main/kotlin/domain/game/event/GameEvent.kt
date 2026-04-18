package domain.game.event

import domain.value.Coords

sealed class GameEvent {
    data class MoveMade(
        val coords: Coords,
        val result: ShotResult,
        val playerNickname: String
    ) : GameEvent()

    data class PlayerSwitched(
        val nextPlayerNickname: String
    ) : GameEvent()

    data class GameFinished(
        val winnerNickname: String
    ) : GameEvent()

    data class ShipSunk(
        val playerNickname: String
    ) : GameEvent()

    data class InvalidMove(
        val reason: String
    ) : GameEvent()
}