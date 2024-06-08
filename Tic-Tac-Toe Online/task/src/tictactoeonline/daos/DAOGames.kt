package tictactoeonline.daos

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import tictactoeonline.Game
import tictactoeonline.Player
import tictactoeonline.daos.models.Games
import tictactoeonline.dtos.game.*
import tictactoeonline.utils.InputParser

class DAOGames {
    private fun resultRowToGamesStatusDTO(row: ResultRow) = GameStatusDTO(
        gameId = row[Games.id],
        player1 = row[Games.player1],
        player2 = row[Games.player2],
        size = row[Games.size],
        private = row[Games.private],
        gameStatus = if (row[Games.player1].isBlank() || row[Games.player2].isBlank()) GameStatus.NOT_STARTED else GameStatus.values()
            .find { it.value == row[Games.status] }!!,
        field = Games.stringToField(row[Games.field]),
        token = row[Games.token]
    )

    private fun resultRowToGamesDTO(row: ResultRow) = GamesDTO.Content(
        gameId = row[Games.id],
        player1 = row[Games.player1],
        player2 = row[Games.player2],
        size = row[Games.size],
        private = row[Games.private]
    )

    private fun resultRowToGameDTO(row: ResultRow, isNewGame: Boolean = false) = GameDTO(
        gameId = row[Games.id],
        status = if (isNewGame) GameStatus.NEW_GAME else GameStatus.values().find { it.value == row[Games.status] }!!,
        player1 = row[Games.player1],
        player2 = row[Games.player2],
        size = row[Games.size],
        private = row[Games.private],
        token = row[Games.token],
        //field = Games.stringToField(row[Games.field])
    )

    private fun resultRowToGame(row: ResultRow): Game {
        val g = Game(
            firstPlayer = Player(row[Games.player1], "X"),
            secondPlayer = Player(row[Games.player2], "O"),
            fieldSize = InputParser.parseSize(row[Games.size], 3 to 3),
            private = row[Games.private]
        )
        g.field = Games.stringToField(row[Games.field])
        g.status = GameStatus.values().find { it.value == row[Games.status] }!!
        g.token = row[Games.token]
        return g
    }

    fun createGame(game: Game): GameDTO = transaction {
        Games.insert {
            it[status] = GameStatus.FIRST_PLAYER_MOVE.value
            it[field] = fieldToString(game.field)
            it[player1] = game.firstPlayer.name
            it[player2] = game.secondPlayer.name
            it[size] = "${game.fieldSize.first}x${game.fieldSize.second}"
            it[private] = game.private
            it[token] = if (game.private) getRandomString(32) else ""
        }
            .resultedValues?.singleOrNull()?.let { resultRowToGameDTO(it, true) }!!
    }

    fun games(pageNumber: Int, pageSize: Int): GamesDTO = transaction {
        val totalElements = Games.selectAll().count()

        val totalPages = if (totalElements % pageSize == 0L) {
            (totalElements / pageSize).toInt()
        } else {
            (totalElements / pageSize + 1).toInt()
        }

        val offset = pageNumber * pageSize
        val page = Games
            .selectAll()
            .limit(pageSize, offset.toLong())
            .map(::resultRowToGamesDTO)

        GamesDTO(totalPages, totalElements, pageNumber, pageSize, page.size, page)
    }

    fun gamesMy(email: String, pageNumber: Int, pageSize: Int): GamesMyDTO = transaction {
        val totalElements = Games.selectAll()
            .where { Games.player1 eq email }
            .orWhere { Games.player2 eq email }
            .count()

        val totalPages = if (totalElements % pageSize == 0L) {
            (totalElements / pageSize).toInt()
        } else {
            (totalElements / pageSize + 1).toInt()
        }

        val offset = pageNumber * pageSize
        val page = Games
            .selectAll()
            .where { Games.player1 eq email }
            .orWhere { Games.player2 eq email }
            .limit(pageSize, offset.toLong())
            .map(::resultRowToGamesStatusDTO)

        GamesMyDTO(totalPages, totalElements, pageNumber, pageSize, page.size, page)
    }

    fun getGame(gameId: Int): Game? {
        return transaction {
            Games
                .selectAll()
                .where { Games.id eq gameId }
                .map(::resultRowToGame)
                .singleOrNull()
        }
    }

    fun joinGame(id: Int, game: Game, email: String) = transaction {
        Games.update({ Games.id eq id }) {
            it[player1] = game.firstPlayer.name.ifBlank { email }
            it[player2] = game.secondPlayer.name.ifBlank { email }
            it[status] = GameStatus.FIRST_PLAYER_MOVE.value
        }
    }

    fun updateGame(id: Int, game: Game) {
        Games.update({ Games.id eq id }) {
            it[status] = game.status.value
            it[field] = fieldToString(game.field)
        }
    }

    private fun getRandomString(length: Int): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }
}