package application

data class ShipPlacementRequest(
    val x: Int,
    val y: Int,
    val direction: String,
    val size: Int,
)
