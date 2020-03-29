package com.soyle.stories.theme.usecases

import arrow.core.identity
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Theme
import com.soyle.stories.theme.*
import com.soyle.stories.theme.usecases.removeCharacterFromComparison.RemoveCharacterFromComparison
import com.soyle.stories.theme.usecases.removeCharacterFromComparison.RemoveCharacterFromComparisonUseCase
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class RemoveCharacterFromComparisonUnitTest {

    private val themeId = UUID.randomUUID()
    private val characterId = UUID.randomUUID()

    private lateinit var context: Context
    private var updatedTheme: Theme? = null
    private var deletedThemeId: Theme.Id? = null
    private var result: Any? = null

    @BeforeEach
    fun clear() {
        context = setupContext()
        result = null
    }

    @Test
    fun `theme does not exist`() {
        givenNoThemes()
        whenUseCaseIsExecuted()
        val result = result as ThemeDoesNotExist
        assertEquals(themeId, result.themeId)
    }

    @Test
    fun `character not in theme`() {
        givenThemeWith(themeId = themeId)
        whenUseCaseIsExecuted()
        val result = result as CharacterNotInTheme
        assertEquals(themeId, result.themeId)
        assertEquals(characterId, result.characterId)
    }

    @Test
    fun `character in theme`() {
        givenThemeWith(themeId = themeId, andCharacterIds = *arrayOf(characterId))
        whenUseCaseIsExecuted()
        val result = result as RemoveCharacterFromComparison.ResponseModel
        assertEquals(themeId, result.themeId)
        assertEquals(characterId, result.characterId)
    }

    @Test
    fun `persist theme`() {
        givenThemeWith(themeId = themeId, andCharacterIds = *arrayOf(characterId))
        whenUseCaseIsExecuted()
        assertPersisted()
    }

    @Test
    fun `last remaining character`() {
        givenThemeWith(themeId = themeId, andCharacterIds = *arrayOf(characterId))
        whenUseCaseIsExecuted()
        val result = result as RemoveCharacterFromComparison.ResponseModel
        assertTrue(result.themeDeleted)
    }

    @Test
    fun `last remaining character persisted`() {
        givenThemeWith(themeId = themeId, andCharacterIds = *arrayOf(characterId))
        whenUseCaseIsExecuted()
        assertNotNull(deletedThemeId)
    }

    @Test
    fun `multiple characters`() {
        val otherCharacterId = UUID.randomUUID()
        givenThemeWith(themeId = themeId, andCharacterIds = *arrayOf(characterId, otherCharacterId), andMajorCharacter = otherCharacterId)
        whenUseCaseIsExecuted()
        val result = result as RemoveCharacterFromComparison.ResponseModel
        assertFalse(result.themeDeleted)
    }

    @Test
    fun `multiple characters persisted`() {
        val otherCharacterId = UUID.randomUUID()
        givenThemeWith(themeId = themeId, andCharacterIds = *arrayOf(characterId, otherCharacterId), andMajorCharacter = otherCharacterId)
        whenUseCaseIsExecuted()
        val updatedTheme = updatedTheme!!
        assertNull(updatedTheme.getIncludedCharacterById(Character.Id(characterId)))
    }

    @Test
    fun `last remaining major character`() {
        givenThemeWith(themeId = themeId, andCharacterIds = *arrayOf(characterId, UUID.randomUUID()))
        whenUseCaseIsExecuted()
        val result = result as RemoveCharacterFromComparison.ResponseModel
        assertTrue(result.themeDeleted)
    }

    @Test
    fun `last remaining major character persisted`() {
        givenThemeWith(themeId = themeId, andCharacterIds = *arrayOf(characterId, UUID.randomUUID()))
        whenUseCaseIsExecuted()
        assertNotNull(deletedThemeId)
    }

    private fun givenNoThemes() = givenThemeWith()
    private fun givenThemeWith(themeId: UUID? = null, andMajorCharacter: UUID? = null, vararg andCharacterIds: UUID) {
        val theme = themeId?.let {
            val initialTheme = Theme(Theme.Id(themeId), "", emptyMap(), emptyMap())
            val themeWithCharacters = andCharacterIds.fold(initialTheme) { nextTheme, characterId ->
                nextTheme.includeCharacter(Character(Character.Id(characterId), UUID.randomUUID(), "Bob"))
                    .fold({ throw it }, ::identity)
            }
            andMajorCharacter?.let {
                themeWithCharacters.promoteCharacter(themeWithCharacters.getMinorCharacterById(Character.Id(andMajorCharacter))!!)
                    .fold({ throw it }, ::identity)
            } ?: themeWithCharacters
        }
        context = setupContext(
            initialThemes = listOfNotNull(theme),
            updateTheme = {
                updatedTheme = it
            },
            deleteTheme = {
                deletedThemeId = it.id
            }
        )
    }

    private fun whenUseCaseIsExecuted() {
        val useCase: RemoveCharacterFromComparison = RemoveCharacterFromComparisonUseCase(context)
        val output = object : RemoveCharacterFromComparison.OutputPort {
            override fun receiveRemoveCharacterFromComparisonFailure(failure: ThemeException) {
                result = failure
            }

            override fun receiveRemoveCharacterFromComparisonResponse(response: RemoveCharacterFromComparison.ResponseModel) {
                result = response
            }
        }
        runBlocking {
            useCase.invoke(themeId, characterId, output)
        }
    }

    private fun assertPersisted() {
        assertTrue(updatedTheme != null || deletedThemeId != null)
    }


}