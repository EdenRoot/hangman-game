import dev.kiryao.core.data.HangmanDb
import dev.kiryao.feature.playzone.InteractiveModeController
import dev.kiryao.feature.playzone.InteractiveModeUiState
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.mockito.Mockito
import org.mockito.kotlin.whenever

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

    @Test
    fun `checkUsedLetters should return true for used letter`() {

        val gameState = createGameState(word = "КОТ").copy(usedLetters = setOf('К', 'О'))
        interactiveModeController.setInteractiveModeState(gameState)

        val result = interactiveModeController.checkUsedLetters('К')

        assertTrue(result)
    }

    @Test
    fun `checkUsedLetters should return false for new letter`() {

        val gameState = createGameState(word = "КОТ").copy(usedLetters = setOf('К', 'О'))
        interactiveModeController.setInteractiveModeState(gameState)

        val result = interactiveModeController.checkUsedLetters('Т')

        assertFalse(result)
    }

    @Test
    fun `checkUsedLetters should return false when not in Process state`() {

        interactiveModeController.setInteractiveModeState(InteractiveModeUiState.Menu)

        val result = interactiveModeController.checkUsedLetters('К')

        assertFalse(result)
    }

    @Test
    fun `useHint should return true and set hint when description exists`() {

        val gameState = createGameState(word = "КОТ")
        interactiveModeController.setInteractiveModeState(gameState)

        whenever(hangmanDb.getWordDescription("КОТ")).thenReturn("Домашнее животное")

        val result = interactiveModeController.useHint()

        assertTrue(result)
        val newState = interactiveModeController.getInteractiveModeState() as InteractiveModeUiState.Process
        assertEquals("Домашнее животное", newState.hint)
    }

    @Test
    fun `useHint should return false when no description exists`() {

        val gameState = createGameState(word = "КОТ")
        interactiveModeController.setInteractiveModeState(gameState)

        whenever(hangmanDb.getWordDescription("КОТ")).thenReturn(null)

        val result = interactiveModeController.useHint()

        assertFalse(result)
        val newState = interactiveModeController.getInteractiveModeState() as InteractiveModeUiState.Process
        assertNull(newState.hint)
    }

    @Test
    fun `useHint should return false when hint already used`() {

        val gameState = createGameState(word = "КОТ").copy(hint = "Уже использовано")
        interactiveModeController.setInteractiveModeState(gameState)

        val result = interactiveModeController.useHint()

        assertFalse(result)
    }

    @Test
    fun `should detect win when all letters guessed`() {

        val gameState = createGameState(word = "КОТ").copy(
            mask = "КОТ",
            health = 5
        )
        interactiveModeController.setInteractiveModeState(gameState)

        interactiveModeController.handlerEnteredLetter('X')

        assertTrue(interactiveModeController.getInteractiveModeState() is InteractiveModeUiState.Win)
    }

    @Test
    fun `should detect game over when health reaches zero`() {

        val gameState = createGameState(word = "КОТ", health = 1)
        interactiveModeController.setInteractiveModeState(gameState)

        interactiveModeController.handlerEnteredLetter('П')

        assertTrue(interactiveModeController.getInteractiveModeState() is InteractiveModeUiState.Fail)
    }

    @Test
    fun `complete game scenario - win`() {

        val gameState = createGameState(word = "КОТ")
        interactiveModeController.setInteractiveModeState(gameState)

        interactiveModeController.handlerEnteredLetter('К')
        interactiveModeController.handlerEnteredLetter('О')
        interactiveModeController.handlerEnteredLetter('Т')

        assertTrue(interactiveModeController.getInteractiveModeState() is InteractiveModeUiState.Win)
    }

    @Test
    fun `complete game scenario - game over`() {

        val gameState = createGameState(word = "КОТ", health = 3)
        interactiveModeController.setInteractiveModeState(gameState)

        interactiveModeController.handlerEnteredLetter('П')
        interactiveModeController.handlerEnteredLetter('Р')
        interactiveModeController.handlerEnteredLetter('И')

        assertTrue(interactiveModeController.getInteractiveModeState() is InteractiveModeUiState.Fail)
    }

    @Test
    fun `getHealthDisplay should show correct hearts and number`() {

        val gameState = createGameState(word = "КОТ", health = 3)
        interactiveModeController.setInteractiveModeState(gameState)

        val display = interactiveModeController.getHealthDisplay()

        assertEquals("3 ❤❤❤", display)
    }

    @Test
    fun `getHealthDisplay should show full health when not in Process state`() {

        interactiveModeController.setInteractiveModeState(InteractiveModeUiState.Menu)

        val display = interactiveModeController.getHealthDisplay()

        assertEquals("❤❤❤❤❤❤❤ 7", display)
    }
}