package tictactoeonline

import tictactoeonline.exceptions.ImpossibleMoveException
import tictactoeonline.exceptions.NoRightException
import tictactoeonline.dtos.game.GameStatus

data class Player(var name: String, val move: String)

class Game(
    val firstPlayer: Player,
    val secondPlayer: Player,
    val fieldSize: Pair<Int, Int>,
    val private: Boolean
) {
    var status: GameStatus = GameStatus.NOT_STARTED
    var token: String = ""

    var currentPlayer: Player = firstPlayer

    var field: List<Array<String>> = List(fieldSize.first) {
        Array(fieldSize.second) { " " }
    }

    fun makeMove(move: String) {
        if (status == GameStatus.FIRST_PLAYER_WON || status == GameStatus.SECOND_PLAYER_WON || status == GameStatus.DRAW) {
            throw NoRightException()
        }
        try {
            val (x, y) = move.replace("[()]".toRegex(), "").split(",").map { it.toInt() }
            if (field[x - 1][y - 1].isNotBlank()) {
                throw Exception()
            } else {
                field[x - 1][y - 1] = currentPlayer.move
            }

            if (isWin()) {
                status = when (currentPlayer) {
                    firstPlayer -> GameStatus.FIRST_PLAYER_WON
                    secondPlayer -> GameStatus.SECOND_PLAYER_WON
                    else -> throw Exception()
                }
                return
            }

            currentPlayer = if (currentPlayer == firstPlayer) {
                status = GameStatus.SECOND_PLAYER_MOVE
                secondPlayer
            } else {
                status = GameStatus.FIRST_PLAYER_MOVE
                firstPlayer
            }

            if (field.flatMap { it.asList() }.all { it.isNotBlank() }) {
                status = GameStatus.DRAW
                return
            }
        } catch (e: Exception) {
            throw ImpossibleMoveException()
        }
    }

    private fun isWin(): Boolean {
        return isRowWin() || isColumnWin() || isDiagonalWin()
    }

    private fun isRowWin(): Boolean {
        if (field[0].size == 1) {
            return false
        }

        val size = field.size

        for (i in 0 until size) {
            for (j in 0..field[0].size - 3) {
                if ((0..2).all { k -> field[i][j + k] == currentPlayer.move }) {
                    return true
                }
            }
        }

        return false
    }

    private fun isColumnWin(): Boolean {
        if (field.size == 1) {
            return false
        }

        val size = field.size

        for (j in 0 until field[0].size) {
            for (i in 0..size - 3) {
                if ((0..2).all { k -> field[i + k][j] == currentPlayer.move }) {
                    return true
                }
            }
        }

        return false
    }

    private fun isDiagonalWin(): Boolean {
        if (field.size == 1 || field[0].size == 1) {
            return false
        }

        val rows = field.size
        val cols = field[0].size

        for (i in 0..rows - 3) {
            for (j in 0..cols - 3) {
                if ((0..2).all { k -> field[i + k][j + k] == currentPlayer.move }) {
                    return true
                }
            }
        }

        for (i in 0..rows - 3) {
            for (j in 2 until cols) {
                if ((0..2).all { k -> field[i + k][j - k] == currentPlayer.move }) {
                    return true
                }
            }
        }

        return false
    }
}