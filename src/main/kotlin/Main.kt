package dev.kiryao

import dev.kiryao.core.cliui.ConsoleGameView
import dev.kiryao.core.data.HangmanDb
import dev.kiryao.feature.nonInteractive.NonInteractiveMode.processNonInteractiveMode
import dev.kiryao.feature.playzone.PlayzoneLogic
import dev.kiryao.feature.playzone.PlayzoneScreen

const val HIDDEN_SYMBOL = '*'

fun main(args: Array<String>) {
    if (args.size == 2) {
        processNonInteractiveMode(args[0], args[1])
    } else {

        val game = PlayzoneScreen(
            hangmanDb = HangmanDb(),
            playzoneLogic = PlayzoneLogic(),
            consoleGameView = ConsoleGameView()
        )

        game.loop()
    }
}