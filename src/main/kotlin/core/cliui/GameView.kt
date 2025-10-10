package dev.kiryao.core.cliui

interface GameView {
    fun showOnBoarding()
    fun showGoodbye()
    fun showChoseCategory()
    fun showChoseDifficulty()
    fun showErrorInput()
    fun showGameState(mask: String, health: Int, healthDisplay: String, usedLetters: Set<Char>, hintText: String)
    fun showHint(description: String)
    fun showNoHintAvailable()
    fun showErrorInputLetter()
    fun showWin(word: String)
    fun showGameOver(word: String)
    fun userInput(): String
    fun showUsedLetter(letter: Char)
    fun clearScreen()
}
