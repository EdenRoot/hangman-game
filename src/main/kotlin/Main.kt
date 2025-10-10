package dev.kiryao

import dev.kiryao.core.cliui.ConsoleGameView
import dev.kiryao.core.data.HangmanDb
import dev.kiryao.feature.nonInteractive.NonInteractiveModeController.processNonInteractiveMode
import dev.kiryao.feature.playzone.InteractiveModeController
import dev.kiryao.feature.playzone.InteractiveModeScreen

fun main(args: Array<String>) {
    if (args.size == 2) {
        processNonInteractiveMode(args[0], args[1])
    } else {
        val hangmanDb = HangmanDb()
        val interactiveModeController = InteractiveModeController(hangmanDb = hangmanDb)

        val game = InteractiveModeScreen(
            hangmanDb = hangmanDb,
            interactiveModeController = interactiveModeController,
            consoleGameView = ConsoleGameView()
        )

        game.play()
    }
}