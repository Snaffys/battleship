package domain.ship

import domain.value.Coords

interface Ship {
    fun hit(coords: Coords): Boolean

    fun isSunk(): Boolean

    fun getCells(): Set<Coords>
}
