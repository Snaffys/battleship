package domain.game

import domain.board.Board
import domain.board.BoardUtils
import domain.game.event.GameEvent
import domain.game.event.ShotResult
import domain.model.Player
import domain.value.Coords

class GameImpl(
    private val player1: Player,
    private val player2: Player,
    private val board1: Board,
    private val board2: Board,
) : Game {
    private var currentPlayer: Player = player1

    override fun makeMove(coords: Coords): List<GameEvent> {
        val events = mutableListOf<GameEvent>()
        val opponentBoard = opponentBoardForCurrentPlayer()

        if (BoardUtils.isOutOfBounds(coords)) {
            return listOf(GameEvent.InvalidMove("Shot is out of board bounds"))
        }
        if (opponentBoard.isAlreadyShot(coords)) {
            return listOf(GameEvent.InvalidMove("Already shot here"))
        }

        val sunkBefore = opponentBoard.getShips().count { it.isSunk() }
        val result = opponentBoard.shoot(coords)

        addMoveMadeEvent(events, coords, result)
        addShipSunkEventIfNeeded(events, opponentBoard, sunkBefore)
        addMissPlayerSwitchIfNeeded(events, result)
        addGameFinishedIfAllShipsSunk(events, opponentBoard)

        return events
    }

    private fun addMissPlayerSwitchIfNeeded(
        events: MutableList<GameEvent>,
        result: ShotResult,
    ) {
        if (result == ShotResult.MISS) {
            currentPlayer =
                if (currentPlayer == player1) {
                    player2
                } else {
                    player1
                }

            events.add(
                GameEvent.PlayerSwitched(
                    currentPlayer.nickname,
                ),
            )
        }
    }

    private fun addGameFinishedIfAllShipsSunk(
        events: MutableList<GameEvent>,
        opponentBoard: Board,
    ) {
        if (opponentBoard.allShipsSunk()) {
            events.add(
                GameEvent.GameFinished(
                    currentPlayer.nickname,
                ),
            )
        }
    }

    private fun opponentBoardForCurrentPlayer(): Board {
        return if (currentPlayer == player1) {
            board2
        } else {
            board1
        }
    }

    private fun addMoveMadeEvent(
        events: MutableList<GameEvent>,
        coords: Coords,
        result: ShotResult,
    ) {
        events.add(
            GameEvent.MoveMade(
                coords,
                result,
                currentPlayer.nickname,
            ),
        )
    }

    private fun addShipSunkEventIfNeeded(
        events: MutableList<GameEvent>,
        opponentBoard: Board,
        sunkBefore: Int,
    ) {
        val sunkAfter = opponentBoard.getShips().count { it.isSunk() }
        if (sunkAfter > sunkBefore) {
            events.add(GameEvent.ShipSunk(currentPlayer.nickname))
        }
    }
}
