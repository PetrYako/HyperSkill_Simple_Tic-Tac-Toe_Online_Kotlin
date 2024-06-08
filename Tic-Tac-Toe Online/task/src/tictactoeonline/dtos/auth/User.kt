package tictactoeonline.dtos.auth

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val email: String?,
    val password: String?
) {
    fun validate(e: Exception) {
        if (this.email.isNullOrEmpty() || this.password.isNullOrEmpty()) {
            throw e
        }
    }
}