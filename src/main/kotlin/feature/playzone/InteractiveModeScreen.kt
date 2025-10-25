package dev.kiryao.feature.playzone

import dev.kiryao.core.cliui.ConsoleGameView
import dev.kiryao.core.data.HangmanDb
import dev.kiryao.core.model.DifficultyLevel
import dev.kiryao.core.model.PathFile

class InteractiveModeScreen(
    private val hangmanDb: HangmanDb,
    private val interactiveModeController: InteractiveModeController,
    private val consoleGameView: ConsoleGameView
) {
    private var isPlayGame = true
    private var selectedCategory: PathFile? = null
    private var selectedLevel: DifficultyLevel? = null

    fun play() {
        do {
            when (val gameState = interactiveModeController.getInteractiveModeState()) {
                is InteractiveModeUiState.Menu -> handleMenuState()
                is InteractiveModeUiState.Category -> handleCategoryState()
                is InteractiveModeUiState.Difficulty -> handleDifficultyState()
                is InteractiveModeUiState.Process -> handleProcessState(gameState)
                is InteractiveModeUiState.Win -> handleWinState(gameState)
                is InteractiveModeUiState.Fail -> handleGameOverState(gameState)
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
                    interactiveModeController.setInteractiveModeState(InteractiveModeUiState.Category)
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
        interactiveModeController.setInteractiveModeState(InteractiveModeUiState.Difficulty)
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

        interactiveModeController.setInteractiveModeState(
            InteractiveModeUiState.Process(
                word = word,
                category = category.value,
                level = level,
                health = 7,
                hint = null
            )
        )
    }

    private fun handleProcessState(gameState: InteractiveModeUiState.Process) {
        val healthDisplay = interactiveModeController.getHealthDisplay()

        val hintText = if (gameState.hint != null) {
            val hintFrame = "-".repeat(11 + gameState.hint.length)
            hintFrame + "\nПодсказка: ${gameState.hint}\n" + hintFrame
        } else {
            "Для получения подсказки введите -> 'HELP' или 'SOS'"
        }

        consoleGameView.showGameState(
            mask = gameState.mask,
            health = gameState.health,
            healthDisplay = healthDisplay,
            usedLetters = gameState.usedLetters,
            hintText = hintText
        )

        val input = consoleGameView.userInput()

        if (input.equals("help", ignoreCase = true) || input.equals("sos", ignoreCase = true)) {
            consoleGameView.clearScreen()

            when {
                gameState.hint != null -> {
                    println(">> Подсказка уже на экране! <<")
                }
                else -> {
                    val hintActivated = interactiveModeController.useHint()
                    if (!hintActivated) {
                        consoleGameView.showNoHintAvailable()
                    }
                }
            }
            return
        }

        if (validateInput(input)) {
            val letter = input.first().uppercaseChar()
            consoleGameView.clearScreen()

            if (interactiveModeController.checkUsedLetters(letter)) {
                consoleGameView.showUsedLetter(letter)
            } else {
                interactiveModeController.handlerEnteredLetter(letter)
            }
        } else {
            consoleGameView.clearScreen()
            consoleGameView.showErrorInputLetter()
        }
    }

    private fun handleWinState(gameState: InteractiveModeUiState.Win) {
        var correctInput = false
        consoleGameView.showWin(word = gameState.word)

        do {
            when (consoleGameView.userInput()) {
                "" -> {
                    consoleGameView.clearScreen()
                    resetSelections()
                    interactiveModeController.setInteractiveModeState(InteractiveModeUiState.Category)
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

    private fun handleGameOverState(gameState: InteractiveModeUiState.Fail) {
        var correctInput = false
        consoleGameView.showGameOver(word = gameState.word)

        do {
            when (consoleGameView.userInput()) {
                "" -> {
                    consoleGameView.clearScreen()
                    resetSelections()
                    interactiveModeController.setInteractiveModeState(InteractiveModeUiState.Category)
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