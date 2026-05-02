package domain.ship

import domain.value.Coords

class ShipImpl(
    private val cells: Set<Coords>,
) : Ship {
    private val hits = mutableSetOf<Coords>()

    override fun hit(coords: Coords): Boolean {
        if (coords in cells) {
            hits.add(coords)
            return true
        }
        return false
    }

    override fun isSunk(): Boolean {
        return hits.containsAll(cells)
    }

    override fun getCells(): Set<Coords> {
        return cells
    }
}
