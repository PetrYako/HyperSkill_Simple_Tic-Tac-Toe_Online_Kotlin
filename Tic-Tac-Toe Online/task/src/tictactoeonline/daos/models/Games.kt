package tictactoeonline.daos.models

import org.jetbrains.exposed.sql.Table
import kotlin.math.sqrt


object Games : Table() {
    val id = integer("id").autoIncrement()
    val status = integer("status")
    val field = text("field")
    val player1 = varchar("player1", 50)
    val player2 = varchar("player2", 50)
    val size = varchar("size", 10)
    val private = bool("private")
    val token = varchar("token", 100)

    fun fieldToString(field: List<Array<String>>): String {
        var fieldString = "|"
        for (row in field) {
            for (cell in row) {
                fieldString += cell
            }
            fieldString += "|"
        }
        return fieldString
    }

    fun stringToField(fieldString: String): List<Array<String>> {
        val list = fieldString.split("|").toMutableList()
        list.removeFirst()
        list.removeLast()
        val field = List(list.size) {
            val l = list[it].split("").toMutableList()
            l.removeFirst()
            l.removeLast()
            l.toTypedArray()
        }
        return field
    }
}