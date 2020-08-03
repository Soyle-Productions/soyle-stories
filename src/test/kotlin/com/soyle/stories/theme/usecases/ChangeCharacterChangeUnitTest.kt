package com.soyle.stories.theme.usecases

import com.soyle.stories.character.makeCharacter
import com.soyle.stories.common.shouldBe
import com.soyle.stories.common.str
import com.soyle.stories.doubles.ThemeRepositoryDouble
import com.soyle.stories.entities.Theme
import com.soyle.stories.theme.*
import com.soyle.stories.theme.usecases.changeCharacterChange.ChangeCharacterChange
import com.soyle.stories.theme.usecases.changeCharacterChange.ChangeCharacterChangeUseCase
import com.soyle.stories.theme.usecases.changeCharacterChange.ChangedCharacterChange
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ChangeCharacterChangeUnitTest {

    // preconditions
    private val character = makeCharacter()
    private val theme = makeTheme()
        .withCharacterIncluded(character.id, character.name, character.media)
        .withCharacterPromoted(character.id)

    // input
    private val themeId = theme.id.uuid
    private val characterId = character.id.uuid
    private val providedCharacterChange = "Character Change ${str()}"

    // post-conditions
    private var updatedTheme: Theme? = null

    // output
    private var responseModel: ChangeCharacterChange.ResponseModel? = null

    @Nested
    inner class Degenerates {

        private inline fun <reified T : Throwable> degenerate(): T {
            val t = assertThrows<T> { changeCharacterChange() }
            assertNull(updatedTheme)
            assertNull(responseModel)
            return t
        }

        @Test
        fun `theme doesn't exist`() {
            givenThemeDoesNotExist()
            degenerate<ThemeDoesNotExist>() shouldBe themeDoesNotExist(themeId)
        }

        @Test
        fun `character not in theme`() {
            givenThemeDoesNotContainCharacter()
            degenerate<CharacterNotInTheme>() shouldBe characterNotInTheme(themeId, characterId)
        }

        @Test
        fun `character is not a major character`() {
            givenCharacterIsNotAMajorCharacter()
            degenerate<CharacterIsNotMajorCharacterInTheme>() shouldBe
                    characterIsNotMajorCharacterInTheme(themeId, characterId)
        }

    }

    @Test
    fun `happy path`() {
        changeCharacterChange()
        assertEquals(providedCharacterChange, updatedTheme!!.getMajorCharacterById(character.id)!!.characterChange)
        responseModel!!.changedCharacterChange shouldBe ::changedCharacterChange
    }

    private val themeRepository = ThemeRepositoryDouble(onUpdateTheme = {
        updatedTheme = it
    })

    init {
        themeRepository.themes[theme.id] = theme
    }

    private fun givenThemeDoesNotExist() {
        themeRepository.themes.remove(theme.id)
    }

    private fun givenThemeDoesNotContainCharacter() {
        themeRepository.themes[theme.id] = theme.withoutCharacter(character.id)
    }

    private fun givenCharacterIsNotAMajorCharacter() {
        themeRepository.themes[theme.id] = theme.withoutCharacter(character.id)
            .withCharacterIncluded(character.id, character.name, character.media)
    }

    private fun changeCharacterChange() {
        val useCase: ChangeCharacterChange = ChangeCharacterChangeUseCase(themeRepository)
        val output = object : ChangeCharacterChange.OutputPort {
            override suspend fun characterChangeChanged(response: ChangeCharacterChange.ResponseModel) {
                responseModel = response
            }
        }
        runBlocking {
            useCase.invoke(ChangeCharacterChange.RequestModel(themeId, characterId, providedCharacterChange), output)
        }
    }

    private fun changedCharacterChange(changedCharacterChange: ChangedCharacterChange)
    {
        assertEquals(themeId, changedCharacterChange.themeId)
        assertEquals(characterId, changedCharacterChange.characterId)
        assertEquals(providedCharacterChange, changedCharacterChange.characterChange)
    }

}