package com.soyle.stories.theme.usecases

import arrow.core.identity
import com.soyle.stories.character.makeCharacter
import com.soyle.stories.characterarc.usecases.deleteCharacterArc.DeletedCharacterArc
import com.soyle.stories.common.shouldBe
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Project
import com.soyle.stories.entities.Theme
import com.soyle.stories.theme.*
import com.soyle.stories.doubles.CharacterRepositoryDouble
import com.soyle.stories.doubles.ThemeRepositoryDouble
import com.soyle.stories.theme.usecases.removeCharacterFromComparison.RemoveCharacterFromComparison
import com.soyle.stories.theme.usecases.removeCharacterFromComparison.RemoveCharacterFromComparisonUseCase
import com.soyle.stories.theme.usecases.removeCharacterFromComparison.RemovedCharacterFromTheme
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

class RemoveCharacterFromComparisonUnitTest {

    private val themeId = Theme.Id()
    private val characterId = Character.Id()

    private lateinit var context: Context
    private var updatedTheme: Theme? = null
    private var deletedCharacterArcId: Pair<Theme.Id, Character.Id>? = null
    private var result: Any? = null

    private var removedCharacter: RemovedCharacterFromTheme? = null
    private var deletedCharacterArc: DeletedCharacterArc? = null

    @BeforeEach
    fun clear() {
        context = setupContext()
        updatedTheme = null
        result = null
        deletedCharacterArcId = null
    }

    @Test
    fun `theme does not exist`() {
        assertThrows<ThemeDoesNotExist> {
            removeCharacterFromComparison()
        } shouldBe themeDoesNotExist(themeId.uuid)
    }

    @Test
    fun `character not in theme`() {
        givenThemeWith()
        assertThrows<CharacterNotInTheme> {
            removeCharacterFromComparison()
        } shouldBe characterNotInTheme(themeId.uuid, characterId.uuid)
    }

    @Test
    fun `character in theme`() {
        givenThemeWith(andCharacterIds = *arrayOf(characterId.uuid))
        removeCharacterFromComparison()
        updatedTheme shouldBe {
            it as Theme
            assertEquals(themeId, it.id)
            assertTrue(it.characters.isEmpty())
        }
        removedCharacter shouldBe {
            it as RemovedCharacterFromTheme
            assertEquals(themeId.uuid, it.themeId)
            assertEquals(characterId.uuid, it.characterId)
        }
        assertNull(deletedCharacterArc)
    }

    @Test
    fun `major character in theme`() {
        givenThemeWith(andCharacterIds = *arrayOf(characterId.uuid), andMajorCharacterIds = listOf(
            characterId.uuid
        ))
        removeCharacterFromComparison()
        deletedCharacterArc shouldBe {
            it as DeletedCharacterArc
            assertEquals(themeId.uuid, it.themeId)
            assertEquals(characterId.uuid, it.characterId)
        }
    }

    private val themeRepository = ThemeRepositoryDouble(onUpdateTheme = {
        updatedTheme = it
    })
    private val characterRepository = CharacterRepositoryDouble()

    private fun givenThemeWith(andMajorCharacterIds: List<UUID> = emptyList(), vararg andCharacterIds: UUID) {
        themeRepository.themes[themeId] = makeTheme(themeId)
        themeRepository.themes[themeId] = andCharacterIds
            .fold(themeRepository.themes[themeId]!!) { nextTheme, characterId ->
                val character = makeCharacter(Character.Id(characterId), Project.Id(), "Bob")
                characterRepository.characters[character.id] = character
                nextTheme.withCharacterIncluded(character.id, character.name, character.media)
            }
        themeRepository.themes[themeId] = andMajorCharacterIds
            .fold(themeRepository.themes[themeId]!!) { nextTheme, characterId ->
                nextTheme.withCharacterPromoted(Character.Id(characterId))
            }
    }

    private fun removeCharacterFromComparison() {
        val useCase: RemoveCharacterFromComparison = RemoveCharacterFromComparisonUseCase(themeRepository, characterRepository)
        val output = object : RemoveCharacterFromComparison.OutputPort {
            override suspend fun receiveRemoveCharacterFromComparisonResponse(response: RemovedCharacterFromTheme) {
                assertNull(removedCharacter) // should not receive multiple removed characters
                removedCharacter = response
                result = response
            }

            override suspend fun characterArcDeleted(response: DeletedCharacterArc) {
                assertNull(deletedCharacterArc) // should not receive multiple deleted character arcs
                deletedCharacterArc = response
            }
        }
        runBlocking {
            useCase.invoke(themeId.uuid, characterId.uuid, output)
        }
    }

    private fun assertPersisted() {
        assertTrue(updatedTheme != null)
    }


}