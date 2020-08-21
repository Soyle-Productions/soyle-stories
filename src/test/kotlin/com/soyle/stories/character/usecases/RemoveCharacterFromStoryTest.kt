package com.soyle.stories.character.usecases

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import com.soyle.stories.character.CharacterDoesNotExist
import com.soyle.stories.character.makeCharacter
import com.soyle.stories.character.usecases.removeCharacterFromStory.RemoveCharacterFromStory
import com.soyle.stories.character.usecases.removeCharacterFromStory.RemoveCharacterFromStoryUseCase
import com.soyle.stories.common.mustEqual
import com.soyle.stories.common.shouldBe
import com.soyle.stories.doubles.CharacterArcSectionRepositoryDouble
import com.soyle.stories.doubles.CharacterRepositoryDouble
import com.soyle.stories.doubles.ThemeRepositoryDouble
import com.soyle.stories.entities.*
import com.soyle.stories.entities.theme.SymbolicRepresentation
import com.soyle.stories.storyevent.characterDoesNotExist
import com.soyle.stories.theme.asCharacterArcSection
import com.soyle.stories.theme.makeTheme
import com.soyle.stories.theme.makeValueWeb
import com.soyle.stories.theme.repositories.CharacterArcSectionRepository
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

class RemoveCharacterFromStoryTest {

    // pre conditions
    private val themes = generateSequence { makeTheme() }
    private val character = makeCharacter()

    // input
    private val characterId = character.id.uuid

    // post conditions
    private var removedCharacter: Character.Id? = null
    private var updatedThemes: List<Theme>? = null
    private var removedArcSections: List<CharacterArcSection>? = null

    // output
    private var responseModel: RemoveCharacterFromStory.ResponseModel? = null

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

    }

    @Nested
    inner class `Happy Paths` {

        init {
            givenCharacter()
        }

        @AfterEach
        fun `check post conditions`() {
            assertEquals(character.id, removedCharacter)
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
            assertNull(removedArcSections)
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
            removedArcSections!!
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
    private val arcSectionRepository = CharacterArcSectionRepositoryDouble(onRemoveCharacterArcSections = {
        removedArcSections = it
    })

    private fun givenCharacter() {
        characterRepository.characters[character.id] = character
    }
    private fun givenANumberOfThemesIncludeCharacter(count: Int, asMajorCharacter: Boolean = false)
    {
        val themes = themes.take(count).map {
            it.withCharacterIncluded(character.id, character.name, character.media)
                .let {
                    if (asMajorCharacter) it.withCharacterPromoted(character.id)
                    else it
                }
        }.toList()
        themeRepository.themes.putAll(themes.associateBy { it.id })
        if (asMajorCharacter) {
            arcSectionRepository.characterArcSections.putAll(
                themes.flatMap {
                    it.characters.flatMap { it.thematicSections }
                }.map {
                    it.asCharacterArcSection(null)
                }.associateBy { it.id }
            )
        }
    }

    private fun removeCharacterFromStory()
    {
        val useCase: RemoveCharacterFromStory = RemoveCharacterFromStoryUseCase(
            characterRepository, themeRepository, arcSectionRepository
        )
        val output = object : RemoveCharacterFromStory.OutputPort {
            override suspend fun receiveRemoveCharacterFromStoryResponse(response: RemoveCharacterFromStory.ResponseModel) {
                responseModel = response
            }
        }
        runBlocking {
            useCase.invoke(characterId, output)
        }
    }

    private fun themeWithoutCharacter(theme: Theme)
    {
        assertFalse(theme.containsCharacter(character.id))

    }

}