package dev.kiryao

import kotlin.system.exitProcess

const val HIDDEN_SYMBOL = '*'

fun main(args: Array<String>) {
    if (args.size != 2) {
        println("Unknown word")
        exitProcess(1)
    }

    val (targetWord, userInput) = args

    val normalizedTarget = targetWord.trim().lowercase()
    val normalizedInput = userInput.trim().lowercase()

    if (normalizedTarget == normalizedInput) {
        println("$normalizedInput;POS")
        exitProcess(0)
    }

    val result = CharArray(normalizedTarget.length) { HIDDEN_SYMBOL }

    for (char in normalizedInput) {
        if (char in normalizedTarget) {
            for (i in normalizedTarget.indices) {
                if(normalizedTarget[i] == char && result[i] == HIDDEN_SYMBOL) {
                    result.openChar(char = char, index = i)
                }
            }
        }
    }

    val output = String(result)
    val messageResult = if (output == normalizedTarget) "POS" else "NEG"
    println("$output;$messageResult")
    exitProcess(0)
}

private fun CharArray.openChar(char: Char, index: Int) {
    this[index] = char
}