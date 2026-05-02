package domain.board

import domain.game.event.ShotResult
import domain.ship.Ship
import domain.value.Coords

interface Board {
    fun shoot(coords: Coords): ShotResult

    fun allShipsSunk(): Boolean

    fun getShips(): List<Ship>

    fun isAlreadyShot(coords: Coords): Boolean
}
