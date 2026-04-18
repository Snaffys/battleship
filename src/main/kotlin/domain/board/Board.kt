package domain.board

import domain.value.Coords
import domain.game.event.ShotResult
import domain.ship.Ship

interface Board {

    fun shoot(coords: Coords): ShotResult

    fun isAlreadyShot(coords: Coords): Boolean

    fun isOutOfBounds(coords: Coords): Boolean

    fun allShipsSunk(): Boolean

    fun getShips(): List<Ship>
}