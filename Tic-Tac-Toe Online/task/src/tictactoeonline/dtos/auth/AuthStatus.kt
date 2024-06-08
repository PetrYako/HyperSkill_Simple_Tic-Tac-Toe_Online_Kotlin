package tictactoeonline.dtos.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class AuthStatus {
    @SerialName("Signed Up")
    SIGNED_UP,

    @SerialName("Signed In")
    SIGNED_IN,

    @SerialName("Authorization failed")
    AUTH_FAILED,

    @SerialName("Registration failed")
    REG_FAILED
}