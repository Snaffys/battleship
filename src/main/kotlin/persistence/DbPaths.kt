package persistence

import java.nio.file.Path
import java.nio.file.Paths

object DbPaths {
    fun defaultDbPath(): Path {
        val override = System.getProperty("battleship.db.path")?.trim().orEmpty()
        if (override.isNotEmpty()) {
            return Paths.get(override)
        }

        val home = System.getProperty("user.home")

        return Paths.get(home, ".battleship", "battleship.db")
    }
}
