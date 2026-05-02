package domain.board

import domain.game.event.ShotResult
import domain.ship.Ship
import domain.value.Coords

class BoardImpl(
    private val ships: List<Ship>,
) : Board {
    private val shots = mutableSetOf<Coords>()

    override fun shoot(coords: Coords): ShotResult {
        shots.add(coords)
        val hit = ships.any { it.hit(coords) }

        return if (hit) ShotResult.HIT else ShotResult.MISS
    }

    override fun allShipsSunk(): Boolean {
        return ships.all { it.isSunk() }
    }

    override fun getShips(): List<Ship> {
        return ships
    }

    override fun isAlreadyShot(coords: Coords): Boolean {
        return coords in shots
    }
}
