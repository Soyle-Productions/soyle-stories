package com.soyle.stories.theme.usecases

import com.soyle.stories.character.makeCharacter
import com.soyle.stories.common.shouldBe
import com.soyle.stories.doubles.ThemeRepositoryDouble
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Theme
import com.soyle.stories.entities.theme.StoryFunction
import com.soyle.stories.theme.*
import com.soyle.stories.theme.usecases.removeCharacterAsOpponent.RemoveCharacterAsOpponent
import com.soyle.stories.theme.usecases.removeCharacterAsOpponent.RemoveCharacterAsOpponentUseCase
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class RemoveCharacterAsOpponentUnitTest {

    // preconditions
    private val theme = makeTheme()
    private val perspectiveCharacter = makeCharacter()
    private val opponentCharacter = makeCharacter()

    // input data
    private val themeId = theme.id.uuid
    private val perspectiveCharacterId = perspectiveCharacter.id.uuid
    private val opponentId = opponentCharacter.id.uuid

    // post conditions
    private var updatedTheme: Theme? = null

    // output
    private var responseModel: RemoveCharacterAsOpponent.ResponseModel? = null

    @Nested
    inner class Degenerates {

        private inline fun <reified T: Throwable> degenerate(): T
        {
            val t = assertThrows<T> { removeCharacterAsOpponent() }
            assertNull(updatedTheme)
            assertNull(responseModel)
            return t
        }

        @Test
        fun `theme doesn't exist`() {
            degenerate<ThemeDoesNotExist>() shouldBe themeDoesNotExist(themeId)
        }

        @Test
        fun `perspective character not in theme`() {
            givenTheme()
            degenerate<CharacterNotInTheme>() shouldBe characterNotInTheme(themeId, perspectiveCharacterId)
        }

        @Test
        fun `perspective character is not major character`() {
            givenTheme()
            givenThemeHasCharacter(perspectiveCharacter)
            degenerate<CharacterIsNotMajorCharacterInTheme>() shouldBe
                    characterIsNotMajorCharacterInTheme(themeId, perspectiveCharacterId)
        }

        @Test
        fun `opponent character does not have a story function`() {
            givenTheme()
            givenThemeHasCharacter(perspectiveCharacter, asMajorCharacter = true)
            degenerate<CharacterIsNotAnOpponentOfPerspectiveCharacter>() shouldBe
                    characterIsNotAnOpponentOfPerspectiveCharacter(themeId, opponentId, perspectiveCharacterId)
        }

        @Test
        fun `opponent character is not opponent of perspective character`() {
            givenTheme()
            givenThemeHasCharacter(perspectiveCharacter, asMajorCharacter = true)
            givenThemeHasCharacter(opponentCharacter)
            givenCharacterHasStoryFunction(opponentCharacter, perspectiveCharacter, StoryFunction.Ally)
            degenerate<CharacterIsNotAnOpponentOfPerspectiveCharacter>() shouldBe
                    characterIsNotAnOpponentOfPerspectiveCharacter(themeId, opponentId, perspectiveCharacterId)
        }

    }

    @Nested
    inner class `Happy Paths` {

        init {
            givenTheme()
            givenThemeHasCharacter(perspectiveCharacter, asMajorCharacter = true)
            givenThemeHasCharacter(opponentCharacter)
        }

        @AfterEach
        fun `check post conditions`() {
            val updatedStoryFunction = updatedTheme!!
                .getMajorCharacterById(perspectiveCharacter.id)!!
                .getStoryFunctionsForCharacter(opponentCharacter.id)
            assertNull(updatedStoryFunction)
        }

        @AfterEach
        fun `check output`() {
            responseModel!!.characterRemovedAsOpponent.shouldBe {
                assertEquals(themeId, it.themeId)
                assertEquals(opponentId, it.characterId)
                assertEquals(perspectiveCharacterId, it.opponentOfCharacterId)
            }
        }

        @Test
        fun `character is opponent`() {
            givenCharacterIsOpponent(opponentCharacter, perspectiveCharacter)
            removeCharacterAsOpponent()
        }

        @Test
        fun `character is main antagonist`() {
            givenCharacterHasStoryFunction(opponentCharacter, perspectiveCharacter, StoryFunction.MainAntagonist)
            removeCharacterAsOpponent()
        }

    }

    private val themeRepository = ThemeRepositoryDouble(onUpdateTheme = {
        updatedTheme = it
    })

    private fun givenTheme()
    {
        themeRepository.themes[theme.id] = theme
    }

    private fun givenThemeHasCharacter(character: Character, asMajorCharacter: Boolean = false)
    {
        themeRepository.themes[theme.id] = themeRepository.themes.getValue(theme.id)
            .withCharacterIncluded(character.id, character.name, character.media)
            .let {
                if (asMajorCharacter) it.withCharacterPromoted(character.id)
                else it
            }
    }
    private fun givenCharacterHasStoryFunction(character: Character, perspectiveCharacter: Character, storyFunction: StoryFunction)
    {
        themeRepository.themes[theme.id] = themeRepository.themes.getValue(theme.id)
            .withCharacterAsStoryFunctionForMajorCharacter(character.id, storyFunction, perspectiveCharacter.id)
    }
    private fun givenCharacterIsOpponent(opponent: Character, perspectiveCharacter: Character)
    {
        givenCharacterHasStoryFunction(opponent, perspectiveCharacter, StoryFunction.Antagonist)
    }

    private fun removeCharacterAsOpponent()
    {
        val useCase: RemoveCharacterAsOpponent = RemoveCharacterAsOpponentUseCase(themeRepository)
        val output = object : RemoveCharacterAsOpponent.OutputPort {
            override suspend fun removedCharacterAsOpponent(response: RemoveCharacterAsOpponent.ResponseModel) {
                responseModel = response
            }
        }
        runBlocking {
            useCase.invoke(
                RemoveCharacterAsOpponent.RequestModel(
                themeId, perspectiveCharacterId, opponentId
            ), output)
        }
    }

}