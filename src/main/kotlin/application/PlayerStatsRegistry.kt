package application

interface PlayerStatsRegistry {
    fun load(): Map<String, PlayerStats>

    fun save(statsByNickname: Map<String, PlayerStats>)
}