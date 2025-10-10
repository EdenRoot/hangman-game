import dev.kiryao.core.data.HangmanDb
import dev.kiryao.feature.playzone.InteractiveModeController
import dev.kiryao.feature.playzone.InteractiveModeUiState
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.mockito.Mockito

class InteractiveModeControllerTest {

    private lateinit var hangmanDb: HangmanDb
    private lateinit var interactiveModeController: InteractiveModeController

    @BeforeEach
    fun setUp() {
        hangmanDb = Mockito.mock(HangmanDb::class.java)
        interactiveModeController = InteractiveModeController(hangmanDb = hangmanDb)
    }

    private fun createGameState(word: String = "КОТ", health: Int = 7): InteractiveModeUiState.Process {
        return InteractiveModeUiState.Process(
            word = word,
            mask = word.map { if (it.isLetter()) '_' else it }.joinToString(""),
            health = health,
            usedLetters = emptySet()
        )
    }

    @Test
    fun `handlerEnteredLetter should update mask when correct letter entered`() {

        val gameState = createGameState(word = "КОТ")
        interactiveModeController.setInteractiveModeState(gameState)

        interactiveModeController.handlerEnteredLetter('К')

        val newState = interactiveModeController.getInteractiveModeState()
        assertTrue(newState is InteractiveModeUiState.Process)

        val processState = newState as InteractiveModeUiState.Process
        assertAll(
            { assertEquals("К__", processState.mask) },
            { assertEquals(setOf('К'), processState.usedLetters) },
            { assertEquals(7, processState.health) }
        )
    }

    @Test
    fun `handlerEnteredLetter should decrease health when wrong letter entered`() {

        val gameState = createGameState(word = "КОТ")
        interactiveModeController.setInteractiveModeState(gameState)

        interactiveModeController.handlerEnteredLetter('П')

        val newState = interactiveModeController.getInteractiveModeState()
        assertTrue(newState is InteractiveModeUiState.Process)

        val processState = newState as InteractiveModeUiState.Process
        assertAll(
            { assertEquals("___", processState.mask) },
            { assertEquals(setOf('П'), processState.usedLetters) },
            { assertEquals(6, processState.health) }
        )
    }

    @Test
    fun `handlerEnteredLetter should handle multiple occurrences of letter`() {

        val gameState = createGameState(word = "МОЛОКО")
        interactiveModeController.setInteractiveModeState(gameState)

        interactiveModeController.handlerEnteredLetter('О')

        val newState = interactiveModeController.getInteractiveModeState()
        assertTrue(newState is InteractiveModeUiState.Process)

        val processState = newState as InteractiveModeUiState.Process
        assertEquals("_О_О_О", processState.mask)
    }

    @Test
    fun `handlerEnteredLetter should be case insensitive`() {

        val gameState = createGameState(word = "Кот")
        interactiveModeController.setInteractiveModeState(gameState)

        interactiveModeController.handlerEnteredLetter('к')

        val newState = interactiveModeController.getInteractiveModeState()
        assertTrue(newState is InteractiveModeUiState.Process)

        val processState = newState as InteractiveModeUiState.Process
        assertEquals("К__", processState.mask)
    }
}