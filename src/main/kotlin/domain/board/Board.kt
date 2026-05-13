package domain.board

import domain.game.event.ShotResult
import domain.ship.Ship
import domain.value.Coords

interface Board {
    fun shoot(coords: Coords): ShotResult

    fun isAlreadyShot(coords: Coords): Boolean

    fun isOutOfBounds(coords: Coords): Boolean

    fun allShipsSunk(): Boolean

    fun getShips(): List<Ship>
}
