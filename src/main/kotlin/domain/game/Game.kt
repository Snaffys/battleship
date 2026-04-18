package domain.game

import domain.value.Coords
import domain.game.event.GameEvent

interface Game {
    fun makeMove(coords: Coords): List<GameEvent>
}