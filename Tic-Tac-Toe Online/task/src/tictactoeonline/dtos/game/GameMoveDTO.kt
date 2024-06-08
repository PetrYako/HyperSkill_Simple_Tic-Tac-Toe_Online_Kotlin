package tictactoeonline.dtos.game

import kotlinx.serialization.Serializable

@Serializable
data class GameMoveDTO(val status: MoveStatus)
