package com.soyle.stories.theme.usecases

import com.soyle.stories.common.shouldBe
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Theme
import com.soyle.stories.theme.*
import com.soyle.stories.doubles.ThemeRepositoryDouble
import com.soyle.stories.theme.usecases.compareCharacters.CompareCharacters
import com.soyle.stories.theme.usecases.compareCharacters.CompareCharactersUseCase
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

class CompareCharactersTest {

    private val themeId = Theme.Id()

    private var comparedCharacters: CompareCharacters.ResponseModel? = null

    fun compareCharacters() = runBlocking {
        compareCharacters.invoke(themeId.uuid, null, output)
    }

    @Test
    fun `theme doesn't exist`() {
        assertThrows<ThemeDoesNotExist> {
            compareCharacters()
        }
    }

    @Test
    fun `no characters in theme`() {
        givenThemeExists()
        compareCharacters()
        comparedCharacters shouldBe compareCharactersResponse(themeId.uuid) {
            assertTrue(it.comparedCharacters.isEmpty())
        }
    }

    @Test
    fun `theme includes characters`() {
        givenThemeExists()
        givenThemeHasANumberOfCharacters(6)
        compareCharacters()
        comparedCharacters shouldBe compareCharactersResponse(themeId.uuid) {
            assertEquals(6, it.comparedCharacters.size)
            it.comparedCharacters.forEach {
                //theme().getIncludedCharacterById(Character.Id(it.characterId))
            }
        }
    }

    private val themeRepository = ThemeRepositoryDouble()
    private val compareCharacters: CompareCharacters = CompareCharactersUseCase(themeRepository)
    private val output = object : CompareCharacters.OutputPort {
        override fun receiveCharacterComparison(response: CompareCharacters.ResponseModel) {
            comparedCharacters = response
        }
        override fun receiveCompareCharactersFailure(error: ThemeException) = throw error
    }

    private fun theme() = themeRepository.themes.getValue(themeId)

    private fun givenThemeExists() {
        themeRepository.themes[themeId] = makeTheme(themeId)
    }

    private fun givenThemeHasANumberOfCharacters(characterCount: Int) {
        val theme = (1..characterCount).fold(themeRepository.themes.getValue(themeId)) { theme, i ->
            val characterId = Character.Id()
            val themeWithCharacter = theme.withCharacterIncluded(
                characterId,
                "Bob ${UUID.randomUUID().toString().take(3)}",
                null
            )
            if (i % 2 == 0) {
                themeWithCharacter.withCharacterPromoted(characterId)
            } else themeWithCharacter
        }
        themeRepository.themes[themeId] = theme
    }

    private fun compareCharactersResponse(expectedThemeId: UUID, additionalAssertions: (CompareCharacters.ResponseModel) -> Unit) = fun (actual: Any?) {
        actual as CompareCharacters.ResponseModel
        assertEquals(expectedThemeId, actual.themeId)
        additionalAssertions(actual)
    }

}