package console

import application.GameAdministrationService
import application.PlayerService

fun runBattleshipConsole() {
    ConsoleController().run()
}

class ConsoleController(
    private val playerService: PlayerService = PlayerService(),
    private val administrationService: GameAdministrationService =
        GameAdministrationService(playerService),
) {
    fun run() {
        println("Console administration mode")
        println("Commands:")
        println("  add <nickname>         - add player")
        println("  list                   - list players")
        println("  start <steps-file>     - process game steps file")
        println("  help                   - show commands")
        println("  exit                   - quit")
        while (true) {
            val input = readLine()?.trim()
            if (input.isNullOrBlank()) {
                continue
            }
            if (processCommand(input)) {
                return
            }
        }
    }

    fun processCommand(input: String): Boolean {
        val parts = input.split(Regex("\\s+"), limit = 2)
        val command = parts[0].lowercase()
        when (command) {
            "add" -> handleAdd(parts)
            "list" -> handleList()
            "start" -> handleStart(parts)
            "help" -> handleHelp()
            "exit" -> return true
            else -> println("Unknown command. Type 'help'.")
        }
        return false
    }

    private fun handleAdd(parts: List<String>) {
        val nickname = parts.getOrNull(1)?.trim().orEmpty()
        if (nickname.isEmpty()) {
            println("Usage: add <nickname>")
        } else {
            val player = playerService.addPlayer(nickname)
            println("Added player: ${player.nickname}")
        }
    }

    private fun handleList() {
        val players = playerService.getAll()
        if (players.isEmpty()) {
            println("No players yet")
        } else {
            var i = 0
            while (i < players.size) {
                val player = players[i]
                println("${i + 1}. ${player.nickname}")
                ++i
            }
        }
    }

    private fun handleStart(parts: List<String>) {
        val path = parts.getOrNull(1)?.trim().orEmpty()
        if (path.isEmpty()) {
            println("Usage: start <steps-file>")
        } else {
            val result = administrationService.processStepsFile(path)
            for (line in result.logLines) {
                println(line)
            }
            println("")
            println("Next: add <nickname> | list | start <steps-file> | help | exit")
        }
    }

    private fun handleHelp() {
        println("Commands: add <nickname> | list | start <steps-file> | help | exit")
    }
}
