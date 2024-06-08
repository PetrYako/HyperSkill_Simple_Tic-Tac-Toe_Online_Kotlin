package tictactoeonline.dtos.auth

import kotlinx.serialization.Serializable

@Serializable
data class AuthDTO(
    val status: AuthStatus,
    val token: String? = null
)
