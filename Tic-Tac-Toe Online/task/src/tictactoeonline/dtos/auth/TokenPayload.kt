package tictactoeonline.dtos.auth

import kotlinx.serialization.Serializable

@Serializable
data class TokenPayload(val email: String)
