package tictactoeonline.utils

object InputParser {

    fun parseSize(input: String, default: Pair<Int, Int>): Pair<Int, Int> {
        val userSizeDirty = input.split("x")

        if (userSizeDirty.size >= 2) {
            val userSize = userSizeDirty[0].toIntOrNull() to userSizeDirty[1].toIntOrNull()

            val fieldSize = when {
                userSize.first == null || userSize.second == null -> default
                userSize.first!! < 3 && userSize.second!! < 3 -> default
                else -> userSize.first!! to userSize.second!!
            }

            return fieldSize
        }

        return default
    }
}