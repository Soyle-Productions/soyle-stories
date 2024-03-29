package com.soyle.stories.usecase.character

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.CharacterArc
import com.soyle.stories.domain.character.makeCharacter
import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.shouldBe
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.domain.theme.makeTheme
import com.soyle.stories.usecase.character.removeCharacterFromStory.RemoveCharacterFromStory
import com.soyle.stories.usecase.character.removeCharacterFromStory.RemoveCharacterFromStoryUseCase
import com.soyle.stories.usecase.repositories.CharacterArcRepositoryDouble
import com.soyle.stories.usecase.repositories.CharacterRepositoryDouble
import com.soyle.stories.usecase.repositories.ThemeRepositoryDouble
import com.soyle.stories.usecase.storyevent.characterDoesNotExist
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class RemoveCharacterFromStoryTest {

    // pre conditions
    private val themes = generateSequence { makeTheme() }
    private val character = makeCharacter()

    // input
    private val characterId = character.id.uuid

    // post conditions
    private var removedCharacter: Character.Id? = null
    private var updatedThemes: List<Theme>? = null
    private var removedCharacterArcs: List<CharacterArc>? = null

    // output
    private var responseModel: RemoveCharacterFromStory.ResponseModel? = null
    private var confirmationRequest: RemoveCharacterFromStory.ConfirmationRequest? = null

    @Nested
    inner class Degenerates {

        private inline fun <reified T : Throwable> degenerate(): T
        {
            val t = assertThrows<T> {
                removeCharacterFromStory()
            }
            assertNull(removedCharacter)
            assertNull(updatedThemes)
            assertNull(responseModel)
            return t
        }

        @Test
        fun `character doesn't exist`() {
            degenerate<CharacterDoesNotExist>() shouldBe characterDoesNotExist(characterId)
        }

        @Test
        fun `removal not yet confirmed`() {
            characterRepository.givenCharacter(character)
            removeCharacterFromStory(confirmed = false)
            assertNull(responseModel)
            confirmationRequest!!.let {
                it.characterId.mustEqual(character.id)
                it.characterName.mustEqual(character.name.value)
            }
        }

    }

    @Nested
    inner class `Happy Paths` {

        init {
            characterRepository.givenCharacter(character)
        }

        @AfterEach
        fun `check post conditions`() {
            assertEquals(character.id, removedCharacter)
            assertNull(confirmationRequest)
        }

        @AfterEach
        fun `check output`() {
            responseModel!!.removedCharacter shouldBe {
                assertEquals(characterId, it.characterId)
            }
        }

        @Test
        fun `character unused anywhere else`() {
            removeCharacterFromStory()
            assertNull(updatedThemes)
            assertTrue(responseModel!!.removedCharacterFromThemes.isEmpty())
        }

        @Test
        fun `character included in a theme`() {
            givenANumberOfThemesIncludeCharacter(1)
            removeCharacterFromStory()
            updatedThemes!!.single() shouldBe {
                it.id.mustEqual(themeRepository.themes.keys.single())
                it shouldBe ::themeWithoutCharacter
            }
            assertNull(removedCharacterArcs)
            responseModel!!.removedCharacterFromThemes.single().shouldBe {
                assertEquals(themeRepository.themes.keys.first().uuid, it.themeId)
                assertEquals(characterId, it.characterId)
            }
        }

        @Test
        fun `character is major character in theme`() {
            givenANumberOfThemesIncludeCharacter(1, asMajorCharacter = true)
            removeCharacterFromStory()
            updatedThemes!! shouldBe {
                assertEquals(themeRepository.themes.values.first().id, it.single().id)
                it.single() shouldBe ::themeWithoutCharacter
            }
            removedCharacterArcs!!
            responseModel!!.removedCharacterFromThemes.single().shouldBe {
                assertEquals(themeRepository.themes.keys.first().uuid, it.themeId)
                assertEquals(characterId, it.characterId)
            }
        }

        /*


        @Test
        fun `one opposition value has entity`() {
            givenThemes(withEntity = true)
            removeSymbolicItemFromAllThemes()
            updatedThemes shouldBe listOfThemesOfSize(1)
            result shouldBe responseModel(1)
        }

        @Test
        fun `all oppositions per value web per theme should be output`() {
            givenThemes(themeCount = 3, valueWebCount = 4, oppositionCount = 5, withEntity = true)
            removeSymbolicItemFromAllThemes()
            updatedThemes shouldBe listOfThemesOfSize(3)
            result shouldBe responseModel(60)
        }

        @Test
        fun `only oppositions with entity output`() {
            givenThemes(themeCount = 2, valueWebCount = 3, oppositionCount = 4, withEntity = true)
            givenThemes(themeCount = 3, valueWebCount = 4, oppositionCount = 5, withEntity = false)
            removeSymbolicItemFromAllThemes()
            updatedThemes shouldBe listOfThemesOfSize(2)
            result shouldBe responseModel(24)
        }
         */

        //@Test
        fun `character is symbolic item`() {
            //givenThemes(themeCount = 1, valueWebCount = 1, oppositionCount = 1, withCharacter = true)
            /*
            given 1 theme with 1 value web with 1 opposition with the character as a symbolic item
            removeCharacterFromStory()
            expect the 1 theme to be updated
                and the value web with the opposition to no longer have the character as a symbolic item
            expect 1 SymbolicItemRemoved event in the output
                and the event should have the theme id, value web id, opposition id, and the character id
             */

            /*
            given 2 themes with 1 value web with 1 opposition with the character as a symbolic item
            removeCharacterFromStory()
            expect the 2 themes to be updated
                and the value webs with the oppositions to no longer have the character as a symbolic item
            expect 2 SymbolicItemRemoved event in the output
                and the event should have the theme id, value web id, opposition id, and the character id
             */

/*
            givenTheThemes(List(1) {
                val symbolicItem = SymbolicRepresentation(character.id.uuid, character.name)
                val valueWeb = makeValueWeb()
                makeTheme().withValueWeb(valueWeb.withRepresentationOf(symbolicItem, valueWeb.oppositions.first().id))
            })
            removeCharacterFromStory()
            updatedThemes!!.let {

            }*/
            // the character should not be a symbolic item in any themes
            // there should be a SymbolicItemRemoved event in the output
        }

    }

    private val themeRepository = ThemeRepositoryDouble(onUpdateTheme = {
        updatedThemes = updatedThemes?.plus(it) ?: listOf(it)
    })
    private val characterRepository = CharacterRepositoryDouble(onDeleteCharacterWithId = {
        removedCharacter = it
    })
    private val characterArcRepository = CharacterArcRepositoryDouble(onRemoveCharacterArc = {
        removedCharacterArcs = removedCharacterArcs?.plus(it) ?: listOf(it)
    })

    private fun givenANumberOfThemesIncludeCharacter(count: Int, asMajorCharacter: Boolean = false)
    {
        val themes = themes.take(count).map {
            it.withCharacterIncluded(character.id, character.name.value, character.media)
                .let {
                    if (asMajorCharacter) it.withCharacterPromoted(character.id)
                    else it
                }
        }.toList()
        themeRepository.themes.putAll(themes.associateBy { it.id })
        if (asMajorCharacter) {
            themes.flatMap { theme ->
                theme.characters.map {
                    CharacterArc.planNewCharacterArc(it.id, theme.id, theme.name)
                }
            }.forEach {
                characterArcRepository.givenCharacterArc(it)
            }
        }
    }

    private fun removeCharacterFromStory(confirmed: Boolean = true)
    {
        val useCase: RemoveCharacterFromStory = RemoveCharacterFromStoryUseCase(
            characterRepository, themeRepository, characterArcRepository
        )
        val output = object : RemoveCharacterFromStory.OutputPort {
            override suspend fun confirmDeleteCharacter(request: RemoveCharacterFromStory.ConfirmationRequest) {
                confirmationRequest = request
            }
            override suspend fun receiveRemoveCharacterFromStoryResponse(response: RemoveCharacterFromStory.ResponseModel) {
                responseModel = response
            }
        }
        runBlocking {
            useCase.invoke(characterId, confirmed, output)
        }
    }

    private fun themeWithoutCharacter(theme: Theme)
    {
        assertFalse(theme.containsCharacter(character.id))

    }

}