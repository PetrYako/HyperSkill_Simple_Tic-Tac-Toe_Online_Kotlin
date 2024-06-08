package tictactoeonline.routes

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import tictactoeonline.daos.DAOUsers
import tictactoeonline.exceptions.AuthorizationException
import tictactoeonline.exceptions.RegistrationException
import tictactoeonline.dtos.auth.AuthDTO
import tictactoeonline.dtos.auth.AuthStatus
import tictactoeonline.dtos.auth.User

private val dao: DAOUsers = DAOUsers()

const val SECRET = "ut920BwH09AOEDx5"

fun Routing.authRoutes() {

    post("/signup") {
        val user = call.receive<User>()
        validateUser(user, RegistrationException("Validation error"))
        verifyUserExistence(user.email!!)
        dao.createUser(user)
        call.respond(HttpStatusCode.OK, AuthDTO(AuthStatus.SIGNED_UP))
    }

    post("/signin") {
        val user = call.receive<User>()
        validateUser(user, AuthorizationException())
        verifyUserCredentials(user)
        val token = generateJWTToken(user.email!!)
        call.respond(HttpStatusCode.OK, AuthDTO(AuthStatus.SIGNED_IN, token))
    }
}

fun validateUser(user: User, exception: Exception) {
    user.validate(exception)
}

fun verifyUserExistence(email: String) {
    if (dao.user(email) != null) {
        throw RegistrationException("Verification error")
    }
}

fun verifyUserCredentials(user: User) {
    val existingUser = dao.user(user.email!!)
    if (existingUser == null || existingUser.password != user.password) {
        throw AuthorizationException()
    }
}

fun generateJWTToken(email: String): String {
    return JWT.create()
        .withClaim("email", email)
        .sign(Algorithm.HMAC256(SECRET))
}