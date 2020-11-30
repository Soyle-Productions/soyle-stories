package com.soyle.stories.character.usecases

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.soyle.stories.character.CharacterException
import com.soyle.stories.doubles.CharacterRepositoryDouble
import com.soyle.stories.character.repositories.CharacterRepository
import com.soyle.stories.character.usecases.buildNewCharacter.BuildNewCharacter
import com.soyle.stories.character.usecases.buildNewCharacter.BuildNewCharacterUseCase
import com.soyle.stories.characterarc.usecases.listAllCharacterArcs.CharacterItem
import com.soyle.stories.common.NonBlankString
import com.soyle.stories.common.shouldBe
import com.soyle.stories.common.str
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Project
import com.soyle.stories.entities.Theme
import com.soyle.stories.entities.theme.characterInTheme.StoryFunction
import com.soyle.stories.theme.*
import com.soyle.stories.doubles.ThemeRepositoryDouble
import com.soyle.stories.theme.usecases.includeCharacterInComparison.CharacterIncludedInTheme
import com.soyle.stories.theme.usecases.useCharacterAsOpponent.CharacterUsedAsOpponent
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*

/**
 * Created by Brendan
 * Date: 2/26/2020
 * Time: 1:03 PM
 */
class BuildNewCharacterTest {

    private val projectId = Project.Id()
    val providedName = NonBlankString.create("Character Name")!!

    private var createdCharacter: Character? = null

    fun given(addNewCharacter: (Character) -> Unit = {}): (NonBlankString) -> Either<*, CharacterItem> {
        val repo = object : CharacterRepository {
            override suspend fun addNewCharacter(character: Character) {
                createdCharacter = character
                addNewCharacter.invoke(character)
            }

            override suspend fun getCharacterById(characterId: Character.Id): Character? = null
            override suspend fun deleteCharacterWithId(characterId: Character.Id) = Unit
            override suspend fun updateCharacter(character: Character) {

            }

            override suspend fun listCharactersInProject(projectId: Project.Id): List<Character> {
                TODO("Not yet implemented")
            }
        }
        val useCase = BuildNewCharacterUseCase(repo, ThemeRepositoryDouble())
        val output = object : BuildNewCharacter.OutputPort {
            var result: Either<*, CharacterItem>? = null
            override fun receiveBuildNewCharacterFailure(failure: CharacterException) {
                result = failure.left()
            }

            override suspend fun receiveBuildNewCharacterResponse(response: CharacterItem) {
                result = response.right()
            }

            override suspend fun characterIncludedInTheme(response: CharacterIncludedInTheme) {
                error("Should not include character in theme when simply creating a new character")
            }

            override suspend fun characterIsOpponent(response: CharacterUsedAsOpponent) {
                error("Should not include character in theme when simply creating a new character")
            }
        }
        return {
            runBlocking {
                useCase(projectId.uuid, it, output)
            }
            output.result!!
        }
    }

    val useCase = given()

    @Test
    fun `character should have provided name`() {
        val character: CharacterItem = (useCase(providedName) as Either.Right).b
        assertEquals(projectId, createdCharacter!!.projectId)
        assertEquals(providedName.value, character.characterName)
    }

    @Test
    fun `new character should be persisted`() {
        val useCase = given()
        useCase(providedName)
        assertEquals(providedName, createdCharacter!!.name)
        assertNull(createdCharacter!!.media)
    }

    @Test
    fun `output character should have id from created character`() {
        val useCase = given()
        val (result) = useCase(providedName) as Either.Right
        assertEquals(createdCharacter!!.id.uuid, result.characterId)
        assertEquals(providedName.value, result.characterName)
        assertEquals(createdCharacter!!.media?.uuid, result.mediaId)
    }

    @Nested
    inner class `Include in Theme` {

        private val themeId = Theme.Id()

        private var updatedTheme: Theme? = null

        private var opponentCharacter: CharacterUsedAsOpponent? = null
        private var includedCharacterResult: CharacterIncludedInTheme? = null
        private var characterItemResult: CharacterItem? = null

        @Nested
        @TestInstance(TestInstance.Lifecycle.PER_CLASS)
        inner class `Theme doesn't exist` {

            init {
                assertThrows<ThemeDoesNotExist> {
                    buildCharacterToIncludeInTheme(providedName)
                } shouldBe themeDoesNotExist(themeId.uuid)
            }

            @Test
            fun `check character is not created`() {
                assertNull(createdCharacter)
            }

        }

        @Nested
        @TestInstance(TestInstance.Lifecycle.PER_CLASS)
        inner class `Theme exists` {

            init {
                givenTheme()
                buildCharacterToIncludeInTheme(providedName)
            }

            @Test
            fun `check character created with proper name`() {
                val createdCharacter = createdCharacter!!
                assertEquals(projectId, createdCharacter.projectId)
                assertEquals(providedName, createdCharacter.name)
            }

            @Test
            fun `check theme updated to include new character`() {
                val createdCharacter = createdCharacter!!
                val updatedTheme = updatedTheme!!
                assertTrue(updatedTheme.containsCharacter(createdCharacter.id))
                val minorCharacter = updatedTheme.getMinorCharacterById(createdCharacter.id)!!
                assertEquals(providedName.value, minorCharacter.name)
            }

            @Test
            fun `check character included event is output`() {
                val includedCharacterResult = includedCharacterResult!!
                assertEquals(themeId.uuid, includedCharacterResult.themeId)
                assertEquals(createdCharacter!!.id.uuid, includedCharacterResult.characterId)
                assertEquals(providedName.value, includedCharacterResult.characterName)
            }

        }

        @Nested
        inner class `And Use as Opponent to Character` {

            private val perspectiveCharacterId = Character.Id()

            @Test
            fun `perspective character not in theme`() {
                givenTheme()
                assertThrows<CharacterNotInTheme> {
                    buildCharacterToUseAsOpponent(providedName)
                } shouldBe characterNotInTheme(themeId.uuid, perspectiveCharacterId.uuid)
            }

            @Test
            fun `perspective character is not major character`() {
                givenTheme()
                givenThemeHasCharacter(perspectiveCharacterId)
                assertThrows<CharacterIsNotMajorCharacterInTheme> {
                    buildCharacterToUseAsOpponent(providedName)
                } shouldBe characterIsNotMajorCharacterInTheme(themeId.uuid, perspectiveCharacterId.uuid)
            }

            @Test
            fun `perspective character is major character`() {
                givenTheme()
                givenThemeHasCharacter(perspectiveCharacterId, asMajorCharacter = true)
                buildCharacterToUseAsOpponent(providedName)
                createdCharacter!!
                updatedTheme!! shouldBe {
                    assertEquals(
                        StoryFunction.Antagonist,
                        it.getMajorCharacterById(perspectiveCharacterId)!!
                            .getStoryFunctionsForCharacter(createdCharacter!!.id)
                    )
                }
                characterItemResult!!
                includedCharacterResult!!
                opponentCharacter!! shouldBe opponent(
                    createdCharacter!!.id.uuid,
                    providedName.value,
                    perspectiveCharacterId.uuid,
                    themeId.uuid,
                    false
                )
            }

            private fun buildCharacterToUseAsOpponent(name: NonBlankString) {
                val useCase: BuildNewCharacter =
                    BuildNewCharacterUseCase(
                        CharacterRepositoryDouble(
                            onAddNewCharacter = {
                                createdCharacter = it
                            }), themeRepository)
                val output = object : BuildNewCharacter.OutputPort {
                    override suspend fun receiveBuildNewCharacterResponse(response: CharacterItem) {
                        characterItemResult = response
                    }

                    override fun receiveBuildNewCharacterFailure(failure: CharacterException) {
                        throw failure
                    }

                    override suspend fun characterIncludedInTheme(response: CharacterIncludedInTheme) {
                        includedCharacterResult = response
                    }

                    override suspend fun characterIsOpponent(response: CharacterUsedAsOpponent) {
                        opponentCharacter = response
                    }
                }
                runBlocking {
                    useCase.createAndUseAsOpponent(name, themeId.uuid, perspectiveCharacterId.uuid, output)
                }
            }

        }

        private val themeRepository = ThemeRepositoryDouble(onUpdateTheme = {
            updatedTheme = it
        })

        private fun givenTheme() {
            themeRepository.themes[themeId] = makeTheme(themeId, projectId = projectId)
        }

        private fun givenThemeHasCharacter(characterId: Character.Id, asMajorCharacter: Boolean = false) {
            themeRepository.themes[themeId] = themeRepository.themes.getValue(themeId)
                .withCharacterIncluded(characterId, str(), null).let {
                    if (asMajorCharacter) it.withCharacterPromoted(characterId)
                    else it
                }
        }

        private fun buildCharacterToIncludeInTheme(name: NonBlankString) {
            val useCase: BuildNewCharacter = BuildNewCharacterUseCase(
                CharacterRepositoryDouble(
                    onAddNewCharacter = {
                        createdCharacter = it
                    }), themeRepository)
            val output = object : BuildNewCharacter.OutputPort {
                override suspend fun receiveBuildNewCharacterResponse(response: CharacterItem) {
                    characterItemResult = response
                }

                override fun receiveBuildNewCharacterFailure(failure: CharacterException) {
                    throw failure
                }

                override suspend fun characterIncludedInTheme(response: CharacterIncludedInTheme) {
                    includedCharacterResult = response
                }

                override suspend fun characterIsOpponent(response: CharacterUsedAsOpponent) {
                    opponentCharacter = response
                }
            }
            runBlocking {
                useCase.createAndIncludeInTheme(name, themeId.uuid, output)
            }
        }
    }

}