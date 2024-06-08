package tictactoeonline.dtos.game

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StatusDTO(@SerialName("status") val status: GameStatus)
