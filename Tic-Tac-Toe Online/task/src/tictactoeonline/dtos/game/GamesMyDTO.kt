package tictactoeonline.dtos.game

import kotlinx.serialization.Serializable

@Serializable
data class GamesMyDTO(
    val totalPages: Int,
    val totalElements: Long,
    val page: Int,
    val size: Int,
    val numberOfElements: Int,
    val content: List<GameStatusDTO>
)
