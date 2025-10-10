package dev.kiryao.feature.playzone

import dev.kiryao.core.data.HangmanDb

class InteractiveModeController(
    private var _state: InteractiveModeUiState = InteractiveModeUiState.Menu,
    private val hangmanDb: HangmanDb
) {

    fun setInteractiveModeState(state: InteractiveModeUiState) {
        _state = state
    }

    fun getInteractiveModeState(): InteractiveModeUiState = _state

    fun handlerEnteredLetter(letter: Char) {
        val currentState = _state
        if (currentState !is InteractiveModeUiState.Process) return

        val usedLetters = currentState.usedLetters + letter
        val newState = if (currentState.word.contains(letter, ignoreCase = true)) {
            currentState.copy(
                mask = updateMask(currentState, letter),
                usedLetters = usedLetters
            )
        } else {
            currentState.copy(
                health = currentState.health - 1,
                usedLetters = usedLetters
            )
        }
        checkGameState(newState)
    }

    fun checkUsedLetters(letter: Char): Boolean {
        val state = _state
        return state is InteractiveModeUiState.Process && state.usedLetters.contains(letter)
    }

    fun useHint(): Boolean {
        val state = _state
        if (state is InteractiveModeUiState.Process && state.hint == null) {
            val description = hangmanDb.getWordDescription(state.word)
            if (description != null) {
                _state = state.copy(hint = description)
                return true
            }
        }
        return false
    }

    fun getHealthDisplay(): String {
        val state = _state
        return if (state is InteractiveModeUiState.Process) {
            val hearts = "❤".repeat(state.health)
            "${state.health} $hearts"
        } else {
            "❤".repeat(7) + " 7"
        }
    }

    private fun checkGameState(newState: InteractiveModeUiState.Process) {
        _state = when {
            isWin(newState) -> InteractiveModeUiState.Win(word = newState.word)
            isFail(newState) -> InteractiveModeUiState.Fail(word = newState.word)
            else -> newState
        }
    }

    private fun isWin(state: InteractiveModeUiState.Process): Boolean =
        state.mask.equals(state.word, ignoreCase = true)

    private fun isFail(state: InteractiveModeUiState.Process): Boolean =
        state.health <= 0

    private fun updateMask(state: InteractiveModeUiState.Process, letter: Char): String {
        return state.word.mapIndexed { index, char ->
            if (char.equals(letter, ignoreCase = true)) char else state.mask[index]
        }.joinToString("")
    }
}