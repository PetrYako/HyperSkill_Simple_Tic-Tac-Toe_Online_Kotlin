package tictactoeonline.dtos.game

import kotlinx.serialization.Serializable

@Serializable
data class GameRB(
    val player1: String,
    val player2: String,
    val size: String,
    val private: Boolean
)
