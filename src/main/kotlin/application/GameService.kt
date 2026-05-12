package application

import domain.board.BoardImpl
import domain.board.BoardUtils
import domain.game.Game
import domain.game.GameImpl
import domain.model.Player
import domain.ship.ShipImpl
import domain.value.Coords

class GameService {
    fun startGame(
        player1: Player,
        player2: Player,
        ships1: List<ShipImpl>,
        ships2: List<ShipImpl>,
    ): Game {
        val board1 = BoardImpl(ships1)
        val board2 = BoardImpl(ships2)

        return GameImpl(
            player1,
            player2,
            board1,
            board2,
        )
    }

    fun addShips(placements: List<ShipPlacementRequest>): Pair<Boolean, List<ShipImpl>> {
        val ships = mutableListOf<ShipImpl>()
        val occupied = mutableSetOf<Coords>()

        for (p in placements) {
            if (p.size <= 0) {
                return false to emptyList()
            }

            val coords = mutableSetOf<Coords>()
            if (!fillCoordsForPlacement(p, coords)) {
                return false to emptyList()
            }

            for (c in coords) {
                if (!notHovering(c.x, c.y, occupied)) {
                    return false to emptyList()
                }
            }

            ships.add(ShipImpl(coords))
            occupied.addAll(coords)
        }

        return true to ships
    }

    private fun fillCoordsForPlacement(
        placement: ShipPlacementRequest,
        coords: MutableSet<Coords>,
    ): Boolean {
        val direction = placement.direction.uppercase()

        var i = 0
        while (i < placement.size) {
            val c =
                when (direction) {
                    "H" -> Coords(placement.x + i, placement.y)
                    "V" -> Coords(placement.x, placement.y + i)
                    else -> return false
                }

            if (BoardUtils.isOutOfBounds(c)) {
                return false
            }

            coords.add(c)
            ++i
        }

        return true
    }

    private fun notHovering(
        x: Int,
        y: Int,
        occupied: Set<Coords>,
    ): Boolean {
        var dx = -1
        while (dx <= 1) {
            var dy = -1
            while (dy <= 1) {
                val c = Coords(x + dx, y + dy)
                if (c in occupied) {
                    return false
                }
                ++dy
            }
            ++dx
        }
        return true
    }
}
