package tictactoeonline.dtos.game

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class MoveStatus {
    @SerialName("Move done")
    DONE,

    @SerialName("Incorrect or impossible move")
    ERROR
}