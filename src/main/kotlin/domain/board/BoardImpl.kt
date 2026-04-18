package domain.board

import domain.value.Coords
import domain.game.event.ShotResult
import domain.ship.Ship

class BoardImpl(
    private val ships: List<Ship>,
    private val width: Int = 10,
    private val height: Int = 10
) : Board {
    private val shots = mutableSetOf<Coords>()

    override fun shoot(coords: Coords): ShotResult {
        shots.add(coords)

        val hit = ships.any { it.hit(coords) }

        return if (hit) ShotResult.HIT else ShotResult.MISS
    }

    override fun isAlreadyShot(coords: Coords): Boolean = coords in shots

    override fun isOutOfBounds(coords: Coords): Boolean {
        return coords.x < 0 ||
                coords.y < 0 ||
                coords.x >= width ||
                coords.y >= height
    }

    override fun allShipsSunk(): Boolean =
        ships.all { it.isSunk() }

    override fun getShips(): List<Ship> = ships
}