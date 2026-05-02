package application

import domain.game.Game
import domain.game.event.GameEvent
import domain.model.Player
import domain.value.Coords
import java.io.File

data class ProcessingResult(
    val logLines: List<String>,
)

private data class AdminContext(
    val placementsByNickname: MutableMap<String, MutableList<ShipPlacementRequest>> = mutableMapOf(),
    val players: MutableMap<String, Player> = mutableMapOf(),
    var firstPlayer: Player? = null,
    var secondPlayer: Player? = null,
    var game: Game? = null,
)

class GameAdministrationService(
    private val playerService: PlayerService = PlayerService(),
    private val gameService: GameService = GameService(),
) {
    fun processStepsFile(path: String): ProcessingResult {
        val file = File(path)
        if (!file.exists()) {
            return ProcessingResult(listOf("ERROR: Steps file does not exist: $path"))
        }
        val lines =
            try {
                file.readLines()
            } catch (e: Exception) {
                return ProcessingResult(
                    listOf("ERROR: Could not read steps file ($path): ${e.message ?: e.javaClass.simpleName}"),
                )
            }
        return processLines(lines)
    }

    fun processLines(lines: List<String>): ProcessingResult {
        val context = AdminContext()
        val logs = mutableListOf<String>()

        var lineNumber = 0

        for (sourceLine in lines) {
            val line = sourceLine.trim()
            if (line.isEmpty()) {
                continue
            }

            ++lineNumber
            logs.add("STEP $lineNumber: $line")

            val error = executeCommand(line, context, logs, lineNumber)
            if (error != null) {
                logs.add("ERROR: $error")
                return ProcessingResult(logs)
            }
        }

        return ProcessingResult(logs)
    }

    private fun executeCommand(
        line: String,
        context: AdminContext,
        logs: MutableList<String>,
        lineNumber: Int,
    ): String? {
        val parts = line.split(Regex("\\s+"))
        val command = parts.first().uppercase()

        return when (command) {
            "PLAYER" -> handlePlayer(parts, context, logs, lineNumber)
            "PLACEMENT" -> handlePlacement(parts, context, logs, lineNumber)
            "START" -> handleStart(parts, context, logs, lineNumber)
            "SHOT" -> handleShot(parts, context, logs, lineNumber)
            else -> "Unknown command on line $lineNumber: $line"
        }
    }

    private fun handlePlayer(
        parts: List<String>,
        context: AdminContext,
        logs: MutableList<String>,
        lineNumber: Int,
    ): String? {
        if (parts.size != 2) {
            return "Invalid PLAYER on line $lineNumber. Use: PLAYER <nickname>"
        }

        val nickname = parts[1]
        val player = playerService.addPlayer(nickname)
        context.players[player.nickname] = player

        logs.add("Player registered: ${player.nickname}")

        return null
    }

    private fun handlePlacement(
        parts: List<String>,
        context: AdminContext,
        logs: MutableList<String>,
        lineNumber: Int,
    ): String? {
        if (parts.size != 6) {
            return "Invalid PLACEMENT on line $lineNumber. Use: PLACEMENT <nickname> <x> <y> <H|V> <size>"
        }

        val nickname = parts[1]
        val x = parts[2].toIntOrNull() ?: return "Invalid x on line $lineNumber"
        val y = parts[3].toIntOrNull() ?: return "Invalid y on line $lineNumber"
        val direction = parts[4].uppercase()
        val size = parts[5].toIntOrNull() ?: return "Invalid size on line $lineNumber"

        if (direction != "H" && direction != "V") {
            return "Invalid direction on line $lineNumber: $direction"
        }

        if (size <= 0) {
            return "Ship size must be positive on line $lineNumber"
        }

        val placement = ShipPlacementRequest(x, y, direction, size)

        val placedShipsByNickname =
            context.placementsByNickname
                .getOrPut(nickname) { mutableListOf() }

        val updatedPlacement = placedShipsByNickname + placement

        val (layoutCorrect, _) = gameService.addShips(updatedPlacement)

        if (!layoutCorrect) {
            return "Invalid placement for $nickname on line $lineNumber (off board, overlapping, or too close)"
        }

        placedShipsByNickname.add(placement)

        logs.add("Placement accepted for $nickname: ($x,$y) $direction size=$size")

        return null
    }

    private fun handleStart(
        parts: List<String>,
        context: AdminContext,
        logs: MutableList<String>,
        lineNumber: Int,
    ): String? {
        if (parts.size != 3) {
            return "Invalid START on line $lineNumber. Use: START <player1> <player2>"
        }

        if (context.game != null) {
            return "Game already started before line $lineNumber"
        }

        val p1 = context.players[parts[1]] ?: playerService.addPlayer(parts[1])
        val p2 = context.players[parts[2]] ?: playerService.addPlayer(parts[2])

        if (p1.nickname.equals(p2.nickname, ignoreCase = true)) {
            return "Players must be different"
        }

        val placements1 = context.placementsByNickname[parts[1]] ?: emptyList()
        val placements2 = context.placementsByNickname[parts[2]] ?: emptyList()

        val ships1 = gameService.addShips(placements1).second
        val ships2 = gameService.addShips(placements2).second

        context.firstPlayer = p1
        context.secondPlayer = p2

        context.game = gameService.startGame(p1, p2, ships1, ships2)

        logs.add("Game started: ${p1.nickname} vs ${p2.nickname}")

        return null
    }

    private fun handleShot(
        parts: List<String>,
        context: AdminContext,
        logs: MutableList<String>,
        lineNumber: Int,
    ): String? {
        if (parts.size != 3) {
            return "Invalid SHOT on line $lineNumber. Use: SHOT <x> <y>"
        }

        val game = context.game ?: return "SHOT before START on line $lineNumber"

        val x = parts[1].toIntOrNull() ?: return "Invalid x on line $lineNumber"
        val y = parts[2].toIntOrNull() ?: return "Invalid y on line $lineNumber"

        val events = game.makeMove(Coords(x, y))

        for (event in events) {
            logs.add(event.output())

            if (event is GameEvent.GameFinished) {
                val winner = event.winnerNickname
                val loser =
                    when (winner) {
                        context.firstPlayer?.nickname -> context.secondPlayer?.nickname
                        context.secondPlayer?.nickname -> context.firstPlayer?.nickname
                        else -> null
                    }

                if (loser != null) {
                    playerService.recordGameResult(winner, loser)
                }
            }
        }

        return null
    }
}
