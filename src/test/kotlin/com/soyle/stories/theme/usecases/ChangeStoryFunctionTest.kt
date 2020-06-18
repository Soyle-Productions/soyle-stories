/**
 * Created by Brendan
 * Date: 3/14/2020
 * Time: 10:00 AM
 */
package com.soyle.stories.theme.usecases

import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Project
import com.soyle.stories.entities.Theme
import com.soyle.stories.entities.theme.CharacterPerspective
import com.soyle.stories.entities.theme.MajorCharacter
import com.soyle.stories.entities.theme.MinorCharacter
import com.soyle.stories.entities.theme.StoryFunction
import com.soyle.stories.theme.CharacterIsNotMajorCharacterInTheme
import com.soyle.stories.theme.CharacterNotInTheme
import com.soyle.stories.theme.TestContext
import com.soyle.stories.theme.ThemeDoesNotExist
import com.soyle.stories.theme.usecases.changeStoryFunction.ChangeStoryFunction
import com.soyle.stories.theme.usecases.changeStoryFunction.ChangeStoryFunctionUseCase
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.*

class ChangeStoryFunctionTest {

    /*

    provided data:
    - themeId
    - perspectiveCharacterId
    - targetCharacterId
    - story function

    Happy Path:
    - get theme by id
    - get major character in theme with perspective character id
    - apply story function to target character for perspective character in theme
    - update theme

    Output data:
    - themeId
    - perspectiveCharacterId
    - targetCharacterId
    - story function

    Alt Paths:
    - theme does not exist: output failure
    - perspective character not in theme: output failure
    - perspective character not major character in theme: output failure
    - target character not in theme: output failure
    - target character already has story function for perspective character in theme: output success, no update

     */

    private fun theme(id: UUID? = null, characters: () -> List<Pair<UUID, List<String>?>> = { NoCharacters }) =
        (id ?: UUID.randomUUID()) to characters()

    private fun character(id: UUID? = null) = minorCharacter(id)
    private fun minorCharacter(id: UUID? = null) = (id ?: UUID.randomUUID()) to (null as List<String>?)
    private fun majorCharacter(id: UUID? = null, storyFunctions: () -> List<String> = { emptyList() }) =
        (id ?: UUID.randomUUID()) to storyFunctions()

    private val themeId: UUID = theme().first
    private val perspectiveCharacterId: UUID = character().first
    private val targetCharacterId: UUID = character().first
    private val storyFunction: String = ChangeStoryFunction.StoryFunction.Antagonist.name

    @Test
    fun `theme does not exist`() {
        val states = listOf(
            given(NoThemes),
            given(List(5) { theme() })
        )
        states.assertThatEach {
            output as ThemeDoesNotExist
            output.themeId.mustEqual(themeId) { "Output themeId does not match themeId" }

            wasNotPersisted()
        }
    }

    @Test
    fun `perspsective character not in theme`() {

        listOf(
            given(List(1) { theme(themeId) { NoCharacters } }),
            given(List(1) { theme(themeId) { List(5) { character() } } })
        ).assertThatEach {
            output as CharacterNotInTheme
            output.themeId.mustEqual(themeId) { "Output themeId does not match themeId" }
            output.characterId.mustEqual(perspectiveCharacterId) { "Output characterId does not match perspectiveCharacterId" }

            wasNotPersisted()
        }

    }

    @Test
    fun `target character not in theme`() {

        val states = listOf(
            given(List(1) { theme(themeId) { List(1) { character(perspectiveCharacterId) } } })
        )
        states.assertThatEach {
            output as CharacterNotInTheme
            output.themeId.mustEqual(themeId) { "Output themeId does not match themeId" }
            output.characterId.mustEqual(targetCharacterId) { "Output characterId does not match targetCharacterId" }

            wasNotPersisted()
        }

    }

    @Test
    fun `perspective character not a major character`() {

        val states = listOf(
            given(List(1) {
                theme(themeId) {
                    listOf(
                        minorCharacter(perspectiveCharacterId),
                        minorCharacter(targetCharacterId)
                    )
                }
            })
        )

        states.assertThatEach {
            output as CharacterIsNotMajorCharacterInTheme
            output.themeId.mustEqual(themeId) { "Output themeId does not match themeId" }
            output.characterId.mustEqual(perspectiveCharacterId) { "Output characterId does not match perspectiveCharacterId" }

            wasNotPersisted()
        }
    }

    @Test
    fun `target character already has story function`() {
        val states = listOf(
            given(List(1) {
                theme(themeId) {
                    listOf(
                        majorCharacter(perspectiveCharacterId) { listOf(storyFunction) },
                        minorCharacter(targetCharacterId)
                    )
                }
            })
        )
        states.assertThatEach {
            output as ChangeStoryFunction.ResponseModel
            output.themeId.mustEqual(themeId) { "Output themeId does not match themeId" }
            output.perspectiveCharacterId.mustEqual(perspectiveCharacterId) { "Output perspectiveCharacterId does not match perspectiveCharacterId" }
            output.targetCharacterId.mustEqual(targetCharacterId) { "Output targetCharacterId does not match targetCharacterId" }
            output.storyFunction.mustEqual(storyFunction) { "Output storyFunction does not match storyFunction" }

            wasNotPersisted()
        }
    }

    @Test
    fun `target character does not already have story function`() {
        val states = listOf(
            given(List(1) {
                theme(themeId) {
                    listOf(
                        majorCharacter(perspectiveCharacterId) { listOf() },
                        minorCharacter(targetCharacterId)
                    )
                }
            }),
            given(List(1) {
                theme(themeId) {
                    listOf(
                        majorCharacter(perspectiveCharacterId) { listOf("Ally") },
                        minorCharacter(targetCharacterId)
                    )
                }
            })
        )
        states.assertThatEach {
            output as ChangeStoryFunction.ResponseModel
            output.themeId.mustEqual(themeId) { "Output themeId does not match themeId" }
            output.perspectiveCharacterId.mustEqual(perspectiveCharacterId) { "Output perspectiveCharacterId does not match perspectiveCharacterId" }
            output.targetCharacterId.mustEqual(targetCharacterId) { "Output targetCharacterId does not match targetCharacterId" }
            output.storyFunction.mustEqual(storyFunction) { "Output storyFunction does not match storyFunction" }

            val persistedThemes = persistedData.filter { it.type == "updateTheme" }
            persistedThemes.size.mustEqual(1) { "Should have updated exactly one theme" }
            val persistedTheme = persistedThemes.single().data as Theme
            val persistedPerspectiveCharacter =
                persistedTheme.getMajorCharacterById(Character.Id(perspectiveCharacterId)) as MajorCharacter
            val persistedTargetCharacterStoryFunctions =
                persistedPerspectiveCharacter.getStoryFunctionsForCharacter(
                    Character.Id(targetCharacterId)
                )!!
            persistedTargetCharacterStoryFunctions.single()
                .mustEqual(StoryFunction.valueOf(storyFunction)) { "Story function was not applied." }

        }
    }


    inline val NoThemes
        get() = emptyList<Nothing>()
    inline val NoCharacters
        get() = emptyList<Nothing>()

    private fun given(themes: List<Pair<UUID, List<Pair<UUID, List<String>?>>>>): PreparedUseCase =
        PreparedUseCase(themes)

    inner class PreparedUseCase(
        themes: List<Pair<UUID, List<Pair<UUID, List<String>?>>>>
    ) {

        private val context = TestContext(
            initialThemes = themes.map { (themeUUID, characters) ->
                Theme(
                    Theme.Id(themeUUID), Project.Id(), "", listOf(),
                    "",
                    characters.associate { (characterUUID, functions) ->
                        val characterId = Character.Id(characterUUID)
                        val isMajorCharacter = functions != null
                        characterId to if (isMajorCharacter) MajorCharacter(
                            characterId,
                            "Bob",
                            "",
                            "",
                            listOf(),
                            CharacterPerspective(characters.filterNot { it.first == characterUUID }.associate {
                                Character.Id(it.first) to (functions?.map { StoryFunction.valueOf(it) }
                                    ?: emptyList<StoryFunction>())
                            }, mapOf())
                        ) else MinorCharacter(characterId, "Bob", "", "", listOf())
                    },
                    mapOf()
                )
            }
        )

        fun whenExecuted(): ChangeStoryFunctionAssertions {
            val output = object : ChangeStoryFunction.OutputPort {
                var result: Any? = null
                override fun receiveChangeStoryFunctionFailure(failure: Exception) {
                    result = failure
                }

                override fun receiveChangeStoryFunctionResponse(response: ChangeStoryFunction.ResponseModel) {
                    result = response
                }
            }

            val useCase: ChangeStoryFunction = ChangeStoryFunctionUseCase(context)

            runBlocking {
                val requestModel = ChangeStoryFunction.RequestModel(
                    themeId,
                    perspectiveCharacterId,
                    targetCharacterId,
                    ChangeStoryFunction.StoryFunction.valueOf(storyFunction)
                )
                useCase.invoke(requestModel, output)
            }

            return ChangeStoryFunctionAssertions(output.result, context.persistedItems)
        }

    }

    private fun List<PreparedUseCase>.assertThatEach(assertions: ChangeStoryFunctionAssertions.() -> Unit) {
        forEachIndexed { index, preparedUseCase ->
            preparedUseCase.whenExecuted().assertThat(index, assertions)
        }
    }

    class ChangeStoryFunctionAssertions(
        val output: Any?,
        val persistedData: List<TestContext.PersistenceLog>
    ) {

        fun assertThat(testNumber: Int? = null, assertions: ChangeStoryFunctionAssertions.() -> Unit) {
            try {
                assertions()
            } catch (t: Throwable) {
                if (testNumber != null) throw Error("Test [$testNumber] failed.  ${t.message}", t)
                else throw t
            }
        }

        fun Any?.mustEqual(value: Any?, message: () -> String = { "" }) = assertEquals(value, this) { message() }

        fun wasNotPersisted() {
            assert(persistedData.isEmpty()) { "No items should have been persisted.  $persistedData" }
        }

    }

}