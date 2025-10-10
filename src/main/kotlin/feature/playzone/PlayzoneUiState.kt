package dev.kiryao.feature.playzone

sealed class PlayzoneUiState {
    object Menu : PlayzoneUiState()
    object Category : PlayzoneUiState()
    object Difficulty : PlayzoneUiState()

    data class Process(
        val word: String,
        val category: String? = null,
        val level: String? = null,
        val mask: String = word.replace(Regex("[А-яЁё]"), "_"),
        val health: Int = 7,
        val usedLetters: Set<Char> = emptySet()
    ) : PlayzoneUiState()

    data class Win(val word: String) : PlayzoneUiState()
    data class GameOver(val word: String) : PlayzoneUiState()
}