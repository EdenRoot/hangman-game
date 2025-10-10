package dev.kiryao.feature.playzone

import dev.kiryao.core.cliui.ConsoleGameView
import dev.kiryao.core.data.HangmanDb
import dev.kiryao.core.model.DifficultyLevel
import dev.kiryao.core.model.PathFile

class PlayzoneScreen(
    private val hangmanDb: HangmanDb,
    private val playzoneLogic: PlayzoneLogic,
    private val consoleGameView: ConsoleGameView
) {
    private var isPlayGame = true
    private var selectedCategory: PathFile? = null
    private var selectedLevel: DifficultyLevel? = null

    fun loop() {
        do {
            when (val gameState = playzoneLogic.getPlayzoneState()) {
                is PlayzoneUiState.Menu -> handleMenuState()
                is PlayzoneUiState.Category -> handleCategoryState()
                is PlayzoneUiState.Difficulty -> handleDifficultyState()
                is PlayzoneUiState.Process -> handleProcessState(gameState)
                is PlayzoneUiState.Win -> handleWinState(gameState)
                is PlayzoneUiState.GameOver -> handleGameOverState(gameState)
            }
        } while (isPlayGame)
    }

    private fun handleMenuState() {
        var isExitMenu = false
        consoleGameView.showOnBoarding()

        do {
            when (consoleGameView.userInput()) {
                "" -> {
                    consoleGameView.clearScreen()
                    playzoneLogic.setPlayzoneState(PlayzoneUiState.Category)
                    isExitMenu = true
                }
                "exit" -> {
                    consoleGameView.showGoodbye()
                    isExitMenu = true
                    isPlayGame = false
                }
                else -> {
                    consoleGameView.showErrorInput()
                }
            }
        } while (!isExitMenu)
    }

    private fun handleCategoryState() {
        var correctInput = false
        consoleGameView.showChoseCategory()

        do {
            correctInput = when (val input = consoleGameView.userInput()) {
                "п", "1", -> { selectedCategory = PathFile.DICTIONARY_NATURE; true }
                "ж", "2" -> { selectedCategory = PathFile.DICTIONARY_ANIMAL; true }
                "о", "3" -> { selectedCategory = PathFile.DICTIONARY_CLOTHES; true }
                "х", "4" -> { selectedCategory = PathFile.DICTIONARY_HOBBY; true }
                "е", "5" -> { selectedCategory = PathFile.DICTIONARY_FOOD; true }
                "", "с", "6" -> { selectedCategory = PathFile.random(); true }
                else -> { consoleGameView.showErrorInput(); false }
            }
        } while (!correctInput)

        consoleGameView.clearScreen()
        playzoneLogic.setPlayzoneState(PlayzoneUiState.Difficulty)
    }

    private fun handleDifficultyState() {
        var correctInput = false
        consoleGameView.showChoseDifficulty()

        do {
            correctInput = when (val input = consoleGameView.userInput()) {
                "л", "1" -> { selectedLevel = DifficultyLevel.EASY; true }
                "о", "2" -> { selectedLevel = DifficultyLevel.NORMAL; true }
                "т", "3" -> { selectedLevel = DifficultyLevel.HARD; true }
                "", "с", "4" -> { selectedLevel = DifficultyLevel.random(); true }
                else -> { consoleGameView.showErrorInput(); false }
            }
        } while (!correctInput)

        consoleGameView.clearScreen()
        startNewGame()
    }

    private fun startNewGame() {
        val category = selectedCategory ?: PathFile.random()
        val level = selectedLevel ?: DifficultyLevel.random()
        val word = hangmanDb.getRandomWord(category, level)

        playzoneLogic.setPlayzoneState(
            PlayzoneUiState.Process(
                word = word,
                category = category.value,
                level = level
            )
        )
    }

    private fun handleProcessState(gameState: PlayzoneUiState.Process) {
        val healthDisplay = playzoneLogic.getHealthDisplay()

        consoleGameView.showGameState(
            mask = gameState.mask,
            health = gameState.health,
            healthDisplay = healthDisplay,
            usedLetters = gameState.usedLetters
        )

        val input = consoleGameView.userInput()

        if (validateInput(input)) {
            val letter = input.first().uppercaseChar()
            consoleGameView.clearScreen()

            if (playzoneLogic.checkUsedLetters(letter)) {
                consoleGameView.showUsedLetter(letter)
            } else {
                playzoneLogic.handlerEnteredLetter(letter)
            }
        } else {
            consoleGameView.clearScreen()
            consoleGameView.showErrorInputLetter()
        }
    }

    private fun handleWinState(gameState: PlayzoneUiState.Win) {
        var correctInput = false
        consoleGameView.showWin(word = gameState.word)

        do {
            when (consoleGameView.userInput()) {
                "" -> {
                    consoleGameView.clearScreen()
                    resetSelections()
                    playzoneLogic.setPlayzoneState(PlayzoneUiState.Menu)
                    correctInput = true
                }
                "exit" -> {
                    consoleGameView.showGoodbye()
                    correctInput = true
                    isPlayGame = false
                }
                else -> {
                    consoleGameView.showErrorInput()
                }
            }
        } while (!correctInput)
    }

    private fun handleGameOverState(gameState: PlayzoneUiState.GameOver) {
        var correctInput = false
        consoleGameView.showGameOver(word = gameState.word)

        do {
            when (consoleGameView.userInput()) {
                "" -> {
                    consoleGameView.clearScreen()
                    resetSelections()
                    playzoneLogic.setPlayzoneState(PlayzoneUiState.Menu)
                    correctInput = true
                }
                "exit" -> {
                    consoleGameView.showGoodbye()
                    correctInput = true
                    isPlayGame = false
                }
                else -> {
                    consoleGameView.showErrorInput()
                }
            }
        } while (!correctInput)
    }

    private fun resetSelections() {
        selectedCategory = null
        selectedLevel = null
    }

    private fun validateInput(input: String): Boolean =
        input.length == 1 && input.first().isLetter()
}