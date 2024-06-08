package tictactoeonline.dtos.game

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GameDTO(
    @SerialName("game_id") val gameId: Int,
    val status: GameStatus,
    val player1: String = "Player1",
    val player2: String = "Player2",
    val size: String = "3x3",
    val private: Boolean,
    val token: String = "",
)
