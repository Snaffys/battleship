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

        val opponentBoard =
            if (currentPlayer == player1) {
                board2
            } else {
                board1
            }

        if (BoardUtils.isOutOfBounds(coords)) {
            return listOf(GameEvent.InvalidMove("Shot is out of board bounds"))
        }

        if (opponentBoard.isAlreadyShot(coords)) {
            return listOf(GameEvent.InvalidMove("Already shot here"))
        }

        val sunkBefore = opponentBoard.getShips().count { it.isSunk() }
        val result = opponentBoard.shoot(coords)

        events.add(
            GameEvent.MoveMade(
                coords = coords,
                result = result,
                playerNickname = currentPlayer.nickname,
            ),
        )

        val sunkAfter = opponentBoard.getShips().count { it.isSunk() }
        if (sunkAfter > sunkBefore) {
            events.add(GameEvent.ShipSunk(currentPlayer.nickname))
        }

        if (result == ShotResult.MISS) {
            currentPlayer =
                if (currentPlayer == player1) {
                    player2
                } else {
                    player1
                }

            events.add(
                GameEvent.PlayerSwitched(
                    nextPlayerNickname = currentPlayer.nickname,
                ),
            )
        }

        if (opponentBoard.allShipsSunk()) {
            events.add(
                GameEvent.GameFinished(
                    winnerNickname = currentPlayer.nickname,
                ),
            )
        }

        return events
    }
}
