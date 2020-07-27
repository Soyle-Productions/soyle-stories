package com.soyle.stories.theme.usecases

import com.soyle.stories.character.makeCharacter
import com.soyle.stories.common.shouldBe
import com.soyle.stories.entities.Theme
import com.soyle.stories.entities.theme.MajorCharacter
import com.soyle.stories.theme.ThemeDoesNotExist
import com.soyle.stories.theme.doubles.ThemeRepositoryDouble
import com.soyle.stories.theme.makeTheme
import com.soyle.stories.theme.themeDoesNotExist
import com.soyle.stories.theme.usecases.listAvailablePerspectiveCharacters.AvailablePerspectiveCharacters
import com.soyle.stories.theme.usecases.listAvailablePerspectiveCharacters.ListAvailablePerspectiveCharacters
import com.soyle.stories.theme.usecases.listAvailablePerspectiveCharacters.ListAvailablePerspectiveCharactersUseCase
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.math.exp

class ListAvailablePerspectiveCharactersUnitTest {

    private val themeId = Theme.Id()

    private var availablePerspectiveCharacters: AvailablePerspectiveCharacters? = null

    @Test
    fun `theme doesn't exist`() {
        assertThrows<ThemeDoesNotExist> {
            listAvailablePerspectiveCharacters()
        } shouldBe themeDoesNotExist(themeId.uuid)
    }

    @Test
    fun `no characters in theme`() {
        givenThemeExists()
        listAvailablePerspectiveCharacters()
        availablePerspectiveCharacters!! shouldBe {
            assertEquals(themeId.uuid, it.themeId)
            assertTrue(it.isEmpty())
        }
    }

    @Test
    fun `no major characters in theme`() {
        givenThemeExists(includedCharacterCount = 3)
        listAvailablePerspectiveCharacters()
        availablePerspectiveCharacters!! shouldBe {
            assertEquals(themeId.uuid, it.themeId)
            assertTrue(it.isEmpty())
        }
    }

    @Test
    fun `major characters in theme`() {
        givenThemeExists(majorCharacterCount = 4)
        listAvailablePerspectiveCharacters()
        availablePerspectiveCharacters!! shouldBe {
            assertEquals(themeId.uuid, it.themeId)
            assertFalse(it.isEmpty())
            assertEquals(4, it.size)
            val expectedCharacters = themeRepository.themes[themeId]!!.characters.filterIsInstance<MajorCharacter>()
                .associateBy { it.id.uuid }
            it.forEach {
                val expectedCharacter = expectedCharacters.getValue(it.characterId)
                assertEquals(expectedCharacter.name, it.characterName)
            }
        }
    }

    private val themeRepository = ThemeRepositoryDouble()

    private fun givenThemeExists(includedCharacterCount: Int = 0, majorCharacterCount: Int = 0) {
        themeRepository.themes[themeId] = makeTheme(themeId).let {
            (1..includedCharacterCount).fold(it) { a, b ->
                val character = makeCharacter()
                a.withCharacterIncluded(character.id, character.name, character.media)
            }
        }.let {
            (1..majorCharacterCount).fold(it) { a, b ->
                val character = makeCharacter()
                a.withCharacterIncluded(character.id, character.name, character.media)
                    .withCharacterPromoted(character.id)
            }
        }
    }

    private val useCase: ListAvailablePerspectiveCharacters = ListAvailablePerspectiveCharactersUseCase(themeRepository)
    private val output = object : ListAvailablePerspectiveCharacters.OutputPort {
        override suspend fun receiveAvailablePerspectiveCharacters(response: AvailablePerspectiveCharacters) {
            availablePerspectiveCharacters = response
        }
    }

    private fun listAvailablePerspectiveCharacters() = runBlocking {
        useCase.invoke(themeId.uuid, output)
    }

}