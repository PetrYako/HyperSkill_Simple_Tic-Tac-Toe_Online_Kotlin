package tictactoeonline.dtos.game

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class GameStatus(val value: Int) {
    @SerialName("New game started")
    NEW_GAME(1),

    @SerialName("game not started")
    NOT_STARTED(0),

    @SerialName("1st player's move")
    FIRST_PLAYER_MOVE(2),

    @SerialName("2nd player's move")
    SECOND_PLAYER_MOVE(3),

    @SerialName("1st player won")
    FIRST_PLAYER_WON(4),

    @SerialName("2nd player won")
    SECOND_PLAYER_WON(5),

    @SerialName("Creating a game failed")
    CREATING_FAILED(-1),

    @SerialName("Joining the game failed")
    JOINING_FAILED(-1),

    @SerialName("Failed to get game status")
    GET_GAME_STATUS_FAILED(-1),

    @SerialName("Failed to make a move")
    MOVE_EXCEPTION(-1),

    @SerialName("You have no rights to make this move")
    NO_RIGHT_FOR_MOVE(-1),

    @SerialName("Joining the game succeeded")
    JOINING_SUCCESS(-1),

    @SerialName("draw")
    DRAW(6)
}