package tictactoeonline.dtos.game

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GameStatusDTO(
    @SerialName("game_id") val gameId: Int? = null,
    @SerialName("game_status") val gameStatus: GameStatus,
    val field: List<Array<String>>? = null,
    val player1: String? = null,
    val player2: String? = null,
    val size: String? = null,
    val private: Boolean = false,
    val token: String = ""
)
