package tictactoeonline

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import tictactoeonline.daos.DatabaseImpl
import tictactoeonline.exceptions.*
import tictactoeonline.dtos.auth.AuthDTO
import tictactoeonline.dtos.auth.AuthStatus
import tictactoeonline.dtos.game.*
import tictactoeonline.routes.authRoutes
import tictactoeonline.routes.gameRoutes

@OptIn(ExperimentalSerializationApi::class)
val jsonSettings = Json {
    ignoreUnknownKeys = true
    encodeDefaults = true
    explicitNulls = false
}

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module(testing: Boolean = false) {
    val myRealm = "auth-jwt"
    val secret = "ut920BwH09AOEDx5"

    install(ContentNegotiation) {
        json(jsonSettings)
    }
    install(StatusPages) {
        exception<ImpossibleMoveException> { _: ImpossibleMoveException ->
            call.respond(HttpStatusCode.BadRequest, GameMoveDTO(MoveStatus.ERROR))
        }
        exception<RegistrationException> { e: RegistrationException ->
            call.respond(HttpStatusCode.Forbidden, AuthDTO(AuthStatus.REG_FAILED))
        }
        exception<AuthorizationException> { _: AuthorizationException ->
            call.respond(HttpStatusCode.Forbidden, AuthDTO(AuthStatus.AUTH_FAILED))
        }
        exception<CreationException> { _: CreationException ->
            call.respond(HttpStatusCode.Forbidden, StatusDTO(GameStatus.CREATING_FAILED))
        }
        exception<GameJoinException> { _: GameJoinException ->
            call.respond(HttpStatusCode.Forbidden, StatusDTO(GameStatus.JOINING_FAILED))
        }
        exception<GetGameStatusException> { _: GetGameStatusException ->
            call.respond(HttpStatusCode.Forbidden, StatusDTO(GameStatus.GET_GAME_STATUS_FAILED))
        }
        exception<NoRightException> { _: NoRightException ->
            call.respond(HttpStatusCode.Forbidden, StatusDTO(GameStatus.NO_RIGHT_FOR_MOVE))
        }
    }
    install(Authentication) {
        jwt(myRealm) {
            realm = "Access to API"
            verifier(
                JWT
                    .require(Algorithm.HMAC256(secret))
                    .build()
            )
            validate { jwtCredential -> JWTPrincipal(jwtCredential.payload) }
            challenge { _, _ -> call.respond(HttpStatusCode.Unauthorized, AuthDTO(AuthStatus.AUTH_FAILED)) }
        }
    }

    DatabaseImpl.init()

    routing {
        authRoutes()
        gameRoutes()
    }
}