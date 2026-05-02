package domain.board

import domain.value.Coords

object BoardUtils {
    const val WIDTH = 10
    const val HEIGHT = 10

    fun isOutOfBounds(coords: Coords): Boolean {
        return coords.x < 0 ||
            coords.y < 0 ||
            coords.x >= WIDTH ||
            coords.y >= HEIGHT
    }
}
