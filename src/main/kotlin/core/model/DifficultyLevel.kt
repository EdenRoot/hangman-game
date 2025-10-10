package dev.kiryao.core.model

enum class DifficultyLevel() {
    EASY(), NORMAL(), HARD();

    companion object {
        fun random(): DifficultyLevel = values().random()
    }
}