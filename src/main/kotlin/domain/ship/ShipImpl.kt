package domain.ship

import domain.value.Coords

class ShipImpl(
    private val cells: Set<Coords>
) : Ship {
    private val hits = mutableSetOf<Coords>()

    override fun hit(coords: Coords): Boolean {
        if (coords in cells) {
            hits.add(coords)
            return true
        }
        return false
    }

    override fun isSunk(): Boolean =
        hits.containsAll(cells)

    override fun getCells(): Set<Coords> = cells
}