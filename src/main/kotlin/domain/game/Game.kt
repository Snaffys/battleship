package domain.game

import domain.game.event.GameEvent
import domain.value.Coords

interface Game {
    fun makeMove(coords: Coords): List<GameEvent>
}
