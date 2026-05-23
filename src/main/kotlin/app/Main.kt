package app

import console.runBattleshipConsole
import gui.runBattleshipGui

fun main(args: Array<String>) {
    if (args.contains("--console")) {
        runBattleshipConsole()
    } else {
        runBattleshipGui()
    }
}
