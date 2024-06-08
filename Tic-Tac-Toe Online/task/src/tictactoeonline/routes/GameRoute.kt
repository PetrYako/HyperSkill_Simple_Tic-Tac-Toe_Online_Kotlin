package tictactoeonline.routes

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import tictactoeonline.Player
import tictactoeonline.Game
import tictactoeonline.daos.DAOGames
import tictactoeonline.exceptions.CreationException
import tictactoeonline.exceptions.GameJoinException
import tictactoeonline.exceptions.GetGameStatusException
import tictactoeonline.exceptions.NoRightException
import tictactoeonline.jsonSettings
import tictactoeonline.dtos.game.*
import tictactoeonline.utils.InputParser

private val dao: DAOGames = DAOGames()

fun Routing.gameRoutes() {
    val myRealm = "auth-jwt"

    authenticate(myRealm) {
        route("/games") {
            get {
                val page = call.request.queryParameters["page"]?.toInt() ?: 0
                call.respond(dao.games(page, 10))
            }

            route("my") {
                get {
                    val page = call.request.queryParameters["page"]?.toInt() ?: 0
                    val principal = call.principal<JWTPrincipal>()
                    val email = principal!!.payload.getClaim("email").asString()
                    call.respond(dao.gamesMy(email, page, 10))
                }
            }
        }

        route("/game") {
            post {
                val body = call.receive<JsonObject>()
                val game = if (body.isNotEmpty()) {
                    val decodedBody: GameRB = jsonSettings.decodeFromJsonElement(body)
                    val principal = call.principal<JWTPrincipal>()
                    val email = principal!!.payload.getClaim("email").asString()
                    if (email != decodedBody.player1 && email != decodedBody.player2) {
                        throw CreationException()
                    }
                    Game(
                        Player(decodedBody.player1, "X"),
                        Player(decodedBody.player2, "O"),
                        InputParser.parseSize(decodedBody.size, 3 to 3),
                        decodedBody.private
                    )
                } else {
                    throw CreationException()
                }
                val dto = dao.createGame(game)
                call.respond(HttpStatusCode.OK, dto)
            }

            route("{game_id}/join/{token?}") {
                post {
                    val gameId = call.parameters["game_id"]?.toInt() ?: throw GameJoinException()
                    val game = dao.getGame(gameId) ?: throw GameJoinException()
                    if (game.firstPlayer.name.isEmpty() && game.secondPlayer.name.isEmpty()) {
                        throw GameJoinException()
                    }
                    val principal = call.principal<JWTPrincipal>()
                    val email = principal!!.payload.getClaim("email").asString()
                    if (game.private) {
                        val token = call.parameters["token"] ?: throw GameJoinException()
                        if (game.token != token) {
                            throw GameJoinException()
                        }
                    }
                    dao.joinGame(gameId, game, email)
                    call.respond(StatusDTO(GameStatus.JOINING_SUCCESS))
                }
            }

            route("{game_id}/status") {
                get {
                    val gameId = call.parameters["game_id"]?.toInt() ?: throw GetGameStatusException()
                    val game = dao.getGame(gameId) ?: throw GetGameStatusException()
                    val principal = call.principal<JWTPrincipal>()
                    val email = principal!!.payload.getClaim("email").asString()
                    if (email != game.firstPlayer.name && email != game.secondPlayer.name) {
                        throw GetGameStatusException()
                    }
                    call.respond(
                        GameStatusDTO(
                            gameId = gameId,
                            gameStatus = game.status,
                            field = game.field,
                            player1 = game.firstPlayer.name,
                            player2 = game.secondPlayer.name,
                            size = "${game.fieldSize.first}x${game.fieldSize.second}",
                            private = game.private,
                            token = game.token
                        )
                    )
                }
            }

            route("{game_id}/move") {
                post {
                    val gameId = call.parameters["game_id"]?.toInt() ?: throw NoRightException()
                    val game = dao.getGame(gameId) ?: throw NoRightException()
                    val principal = call.principal<JWTPrincipal>()
                    val email = principal!!.payload.getClaim("email").asString()
                    if (email != game.currentPlayer.name) {
                        throw NoRightException()
                    }
                    val body = call.receive<GameMoveRB>()
                    game.makeMove(body.move)
                    dao.updateGame(gameId, game)
                    call.respond(HttpStatusCode.OK, GameMoveDTO(MoveStatus.DONE))
                }
            }
        }
    }
}