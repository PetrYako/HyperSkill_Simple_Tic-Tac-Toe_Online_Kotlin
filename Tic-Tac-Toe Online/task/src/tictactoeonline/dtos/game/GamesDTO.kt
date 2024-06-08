package tictactoeonline.dtos.game

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GamesDTO(
    val totalPages: Int,
    val totalElements: Long,
    val page: Int,
    val size: Int,
    val numberOfElements: Int,
    val content: List<Content>
) {
    @Serializable
    data class Content(
        @SerialName("game_id") val gameId: Int,
        val player1: String,
        val player2: String,
        val size: String,
        val private: Boolean,
    )
}
