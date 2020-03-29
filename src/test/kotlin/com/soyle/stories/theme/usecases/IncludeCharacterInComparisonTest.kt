package com.soyle.stories.theme.usecases

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.soyle.stories.character.CharacterDoesNotExist
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.CharacterArcSection
import com.soyle.stories.entities.Theme
import com.soyle.stories.theme.CharacterAlreadyIncludedInTheme
import com.soyle.stories.theme.ThemeDoesNotExist
import com.soyle.stories.theme.setupContext
import com.soyle.stories.theme.usecases.includeCharacterInComparison.IncludeCharacterInComparison
import com.soyle.stories.theme.usecases.includeCharacterInComparison.IncludeCharacterInComparisonUseCase
import com.soyle.stories.translators.asMinorCharacter
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.*

/**
 * Created by Brendan
 * Date: 2/27/2020
 * Time: 2:24 PM
 */
class IncludeCharacterInComparisonTest {

    private fun given(
        characters: List<Character>,
        themes: List<Theme>,
        updateTheme: (Theme) -> Unit = {},
        addNewArcSections: (List<CharacterArcSection>) -> Unit = {}
    ): (UUID, UUID) -> Either<*, IncludeCharacterInComparison.ResponseModel> {
        val context = setupContext(
            initialCharacters = characters,
            initialThemes = themes,
            updateTheme = updateTheme,
            addNewCharacterArcSections = addNewArcSections
        )
        val useCase: IncludeCharacterInComparison = IncludeCharacterInComparisonUseCase(
            context.characterRepository,
            context.themeRepository,
            context.characterArcSectionRepository
        )
        val output = object : IncludeCharacterInComparison.OutputPort {
            var result: Either<Exception, IncludeCharacterInComparison.ResponseModel>? = null
            override fun receiveIncludeCharacterInComparisonFailure(failure: Exception) {
                result = failure.left()
            }

            override fun receiveIncludeCharacterInComparisonResponse(response: IncludeCharacterInComparison.ResponseModel) {
                result = response.right()
            }
        }
        return { characterId, themeId ->
            runBlocking {
                useCase.invoke(characterId, themeId, output)
            }
            output.result!!
        }
    }

    val characterUUID = UUID.randomUUID()
    val projectUUID = UUID.randomUUID()
    val themeUUID = UUID.randomUUID()
    val characterName = "Bob the Builder"
    val character = Character(
        Character.Id(characterUUID), themeUUID, characterName
    )

    @Nested
    inner class GivenCharacterDoesNotExist {

        val useCase = given(emptyList(), emptyList())

        @Test
        fun `should output error`() {
            val (result) = useCase(characterUUID, themeUUID) as Either.Left
            assert(result is CharacterDoesNotExist)
        }

    }

    @Nested
    inner class GivenThemeDoesNotExist {

        val useCase = given(
            characters = listOf(character),
            themes = emptyList()
        )

        @Test
        fun `should output error`() {
            val (result) = useCase(characterUUID, themeUUID) as Either.Left
            assert(result is ThemeDoesNotExist)
        }

    }

    @Nested
    inner class GivenCharacterAlreadyInTheme {

        val useCase = given(
            characters = listOf(character),
            themes = listOf(
                Theme(
                    Theme.Id(themeUUID), "", mapOf(
                        Character.Id(characterUUID) to character.asMinorCharacter(
                            listOf()
                        )
                    ), mapOf()
                )
            )
        )

        @Test
        fun `should output error`() {
            val (result) = useCase(characterUUID, themeUUID) as Either.Left
            assert(result is CharacterAlreadyIncludedInTheme)
        }

    }

    @Nested
    inner class OnSuccess {

        val result = (given(
            characters = listOf(character),
            themes = listOf(
                Theme(
                    Theme.Id(
                        themeUUID
                    ), "", mapOf(), mapOf()
                )
            )
        ).invoke(characterUUID, themeUUID) as Either.Right).b

        @Test
        fun `should contain theme id`() {
            assertEquals(themeUUID, result.themeId)
        }

        @Test
        fun `should contain character id`() {
            assertEquals(characterUUID, result.characterId)
        }

        @Test
        fun `should contain list of characters in theme`() {
            assertEquals(1, result.includedCharacters.size)
        }

        @Test
        fun `should updated theme`() {
            var updatedTheme: Theme? = null
            given(
                characters = listOf(character),
                themes = listOf(
                    Theme(
                        Theme.Id(
                            themeUUID
                        ), "", mapOf(), mapOf()
                    )
                ),
                updateTheme = {
                    updatedTheme = it
                }
            ).invoke(characterUUID, themeUUID)
            updatedTheme!!

        }

        @Test
        fun `should create arc sections for thematic template sections`() {
            var updatedTheme: Theme? = null
            val addedSections = mutableListOf<CharacterArcSection>()
            given(
                characters = listOf(character),
                themes = listOf(
                    Theme(
                        Theme.Id(
                            themeUUID
                        ), "", mapOf(), mapOf()
                    )
                ),
                updateTheme = {
                    updatedTheme = it
                },
                addNewArcSections = {
                    addedSections.addAll(it)
                }
            ).invoke(characterUUID, themeUUID)
            assertEquals(
                updatedTheme!!.thematicTemplate.sections.size,
                addedSections.size
            )
        }

    }
}