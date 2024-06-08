package tictactoeonline.daos

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import tictactoeonline.daos.models.Games
import tictactoeonline.daos.models.Users
import java.io.File
import java.nio.file.Paths

object DatabaseImpl {
    fun init() {
        val driverClassName = "org.h2.Driver"
        val jdbcURL = "jdbc:h2:file:./build/db.mv.db.mv.db"
        val database = Database.connect(jdbcURL, driverClassName)
        transaction(database) {
            SchemaUtils.drop(Users, Games)
            SchemaUtils.create(Users)
            SchemaUtils.create(Games)
        }
    }
}