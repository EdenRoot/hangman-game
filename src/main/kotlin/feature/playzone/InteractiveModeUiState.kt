package dev.kiryao.feature.playzone

import dev.kiryao.core.model.DifficultyLevel

sealed class InteractiveModeUiState {
    object Menu : InteractiveModeUiState()
    object Category : InteractiveModeUiState()
    object Difficulty : InteractiveModeUiState()

    data class Process(
        val word: String,
        val category: String? = null,
        val level: DifficultyLevel? = null,
        val mask: String = word.replace(Regex("[А-яЁё]"), "_"),
        val health: Int = 7,
        val usedLetters: Set<Char> = emptySet(),
        val hint: String? = null
    ) : InteractiveModeUiState()

    data class Win(val word: String) : InteractiveModeUiState()
    data class Fail(val word: String) : InteractiveModeUiState()
}