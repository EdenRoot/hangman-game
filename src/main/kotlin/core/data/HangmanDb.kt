package dev.kiryao.core.data

import dev.kiryao.core.model.DifficultyLevel
import dev.kiryao.core.model.PathFile
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.text.isNotEmpty
import kotlin.text.trim

class HangmanDb() {

    private val wordsByCategoryAndDifficulty =
        mutableMapOf<PathFile, Map<DifficultyLevel, List<String>>>()

    init {
        PathFile.values().forEach { pathFile ->
            loadCategory(pathFile)
        }
    }

    fun getRandomWord(category: PathFile, level: DifficultyLevel): String {
        val wordsByDifficulty = wordsByCategoryAndDifficulty[category].orEmpty()
        val words = wordsByDifficulty[level].orEmpty()

        return if (words.isNotEmpty()) {
            words.random()
        } else {
            val allWords = getAllWordsForCategory(category)
            if (allWords.isNotEmpty()) allWords.random() else "ошибка"
        }
    }

    private fun loadCategory(pathFile: PathFile) {
        val words = readFile(pathFile.value)
        wordsByCategoryAndDifficulty[pathFile] = splitWordsByDifficulty(words)
    }

    private fun splitWordsByDifficulty(words: List<String>): Map<DifficultyLevel, List<String>> {
        return mapOf(
            DifficultyLevel.EASY to words.filter { it.length <= 4 },
            DifficultyLevel.NORMAL to words.filter { it.length in 5..7 },
            DifficultyLevel.HARD to words.filter { it.length > 7 }
        )
    }

    private fun getAllWordsForCategory(category: PathFile): List<String> {
        val wordsByDifficulty = wordsByCategoryAndDifficulty[category].orEmpty()
        return wordsByDifficulty.values.flatten()
    }

    private fun readFile(pathFile: String): List<String> {
        val inputStream = ClassLoader.getSystemClassLoader().getResourceAsStream(pathFile)
        return inputStream?.let {
            val streamReader = InputStreamReader(it, "UTF-8")
            BufferedReader(streamReader).use { reader ->
                reader.readLines()
                    .map { it.trim() }
                    .filter { it.isNotEmpty() }
                    .map { line ->
                        val parts = line.split(":", limit = 2)
                        parts[0].trim()
                    }
                    .filter { it.isNotEmpty() }
            }
        } ?: listOf("ошибка")
    }
}