package tictactoeonline.daos

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import tictactoeonline.daos.models.Users
import tictactoeonline.dtos.auth.User

class DAOUsers {
    private fun resultRowToUser(row: ResultRow) = User(
        email = row[Users.email],
        password = row[Users.password]
    )

    fun createUser(user: User) = transaction {
        Users.insert {
            it[email] = user.email!!
            it[password] = user.password!!
        }
    }

    fun user(email: String): User? = transaction {
        Users
            .selectAll()
            .where { Users.email eq email }
            .map(::resultRowToUser)
            .singleOrNull()
    }
}