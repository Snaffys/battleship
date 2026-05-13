package console

import application.GameService
import application.PlayerService
import application.ShipPlacementRequest
import domain.game.Game
import domain.game.event.GameEvent
import domain.model.Player
import domain.ship.ShipImpl
import domain.value.Coords

class ConsoleController {
    val playerService = PlayerService()
    val gameService = GameService()

    private lateinit var player1: Player
    private lateinit var player2: Player

    fun run() {
        val players = addPlayers()
        val (p1, p2) = choosePlayers(players)
        player1 = p1
        player2 = p2

        val shipSizes = listOf(4, 3, 3, 2, 2, 2, 1, 1, 1, 1)

        val ships1 = placeShips(p1.nickname, shipSizes)
        val ships2 = placeShips(p2.nickname, shipSizes)

        val game = gameService.startGame(p1, p2, ships1, ships2)

        gameLoop(game)
    }

    private fun addPlayers(): List<Player> {
        println("Add players (or type 'start')")
        while (true) {
            val input = readlnOrNull()?.trim()

            if (input.isNullOrBlank()) {
                println("Empty input")
                continue
            }

            if (input == "start") {
                val players = playerService.getAll()

                if (players.size < 2) {
                    println("Need at least 2 players to start game")
                    continue
                }

                return players
            }

            playerService.addPlayer(input)
            println("Added: $input")
        }
    }

    private fun choosePlayers(players: List<Player>): Pair<Player, Player> {
        println("Choose players:")
        while (true) {
            var i = 0
            while (i < players.size) {
                println(i.toString() + ": " + players[i].toString())
                ++i
            }

            println("Player 1 index:")
            val i1 = readlnOrNull()?.toIntOrNull()
            if (i1 == null) {
                println("Invalid input")
                continue
            }

            println("Player 2 index:")
            val i2 = readlnOrNull()?.toIntOrNull()
            if (i2 == null) {
                println("Invalid input")
                continue
            }

            if (i1 == i2) {
                println("Players must be different")
                continue
            }
            if (i1 < 0 || i1 >= players.size || i2 < 0 || i2 >= players.size) {
                println("Index out of range")
                continue
            }

            return Pair(players[i1], players[i2])
        }
    }

    private fun placeShips(
        name: String,
        shipSizes: List<Int>,
    ): List<ShipImpl> {
        while (true) {
            val placements = mutableListOf<ShipPlacementRequest>()
            for (size in shipSizes) {
                while (true) {
                    println("$name place ship size $size (x y [H]orizontally/[V]ertically)")

                    val input = readlnOrNull()
                    if (input == null) {
                        println("Invalid input")
                        continue
                    }

                    val parts = input.split(" ")
                    if (parts.size != 3) {
                        println("Invalid format")
                        continue
                    }

                    val x = parts[0].toIntOrNull()
                    val y = parts[1].toIntOrNull()
                    val dir = parts[2]

                    if (x == null || y == null) {
                        println("Invalid numbers")
                        continue
                    }

                    placements.add(ShipPlacementRequest(x, y, dir, size))
                    break
                }
            }

            val (success, ships) = gameService.addShips(placements)
            if (!success) {
                println("Invalid ship placement, try again from start")
                continue
            }

            return ships
        }
    }

    private fun gameLoop(game: Game) {
        println("Game started: ${player1.nickname} vs ${player2.nickname}")
        print("${player1.nickname} ")
        while (true) {
            println("Enter coordinates (x y):")

            val input = readlnOrNull()
            if (input == null) {
                println("Invalid input")
                continue
            }

            val parts = input.split(" ")
            if (parts.size != 2) {
                println("Enter 2 numbers")
                continue
            }

            val x = parts[0].toIntOrNull()
            val y = parts[1].toIntOrNull()
            if (x == null || y == null) {
                println("Invalid numbers")
                continue
            }

            val events = game.makeMove(Coords(x, y))

            var finished = false

            for (event in events) {
                when (event) {
                    is GameEvent.MoveMade -> {
                        println("${event.playerNickname}: ${event.result}")
                    }

                    is GameEvent.PlayerSwitched -> {
                        println("Next turn: ${event.nextPlayerNickname}")
                    }

                    is GameEvent.ShipSunk -> {
                        println("Ship sunk!")
                    }

                    is GameEvent.GameFinished -> {
                        println("Game ended!")
                        println("Winner: ${event.winnerNickname}")
                        finished = true
                    }

                    is GameEvent.InvalidMove -> {
                        println("Invalid move: ${event.reason}")
                    }
                }
            }
            if (finished) break
        }
    }
}
