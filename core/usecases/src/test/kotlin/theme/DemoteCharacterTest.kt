package com.soyle.stories.usecase.theme

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.CharacterArc
import com.soyle.stories.domain.character.CharacterArcSection
import com.soyle.stories.domain.character.makeCharacter
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.theme.*
import com.soyle.stories.domain.theme.characterInTheme.MinorCharacter
import com.soyle.stories.usecase.repositories.CharacterArcRepositoryDouble
import com.soyle.stories.usecase.repositories.ThemeRepositoryDouble
import com.soyle.stories.usecase.theme.demoteMajorCharacter.DemoteMajorCharacter
import com.soyle.stories.usecase.theme.demoteMajorCharacter.DemoteMajorCharacterUseCase
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.*

class DemoteCharacterTest {

    /*

    provided data:
    - themeId
    - characterId

    Happy path:
    - get theme by id
    - get major character from theme with characterId
    - demote major character within theme
    - update theme

    output:
    - themeId
    - characterId

    alternative paths:
    - theme with id does not exist: failure
    - character not in theme: failure
    - character not a major character: failure

     */

    private fun theme() = UUID.randomUUID()
    private fun character() = UUID.randomUUID()
    private fun minorCharacter(givenId: UUID = UUID.randomUUID()) = givenId to false
    private fun majorCharacter(givenId: UUID = UUID.randomUUID()) = givenId to true

    val themeId: UUID = theme()
    val characterId: UUID = character()

    private fun given(
        themeIds: List<UUID>,
        includedCharacters: Map<UUID, List<Pair<UUID, Boolean>>> = emptyMap()
    ): PreppedUseCase {
        return PreppedUseCase(themeIds, includedCharacters)
    }

    @Test
    fun `no theme exists _ output failure`() {
        listOf(
            given(emptyList()),
            given(List(5) { theme() })
        ).forEach {
            it.whenExecuted().assert {
                if (error !is ThemeDoesNotExist && error != null) throw error
                error as ThemeDoesNotExist
                assertEquals(themeId, error.themeId)

                assertNeverPersisted()
            }
        }
    }

    @Test
    fun `character not in theme _ output failure`() {
        listOf(
            given(List(1) { themeId }, includedCharacters = mapOf(themeId to emptyList())),
            given(List(1) { themeId }, includedCharacters = mapOf(themeId to List(5) { minorCharacter() }))
        ).forEach {
            it.whenExecuted().assert {
                if (error !is CharacterNotInTheme && error != null) throw error
                error as CharacterNotInTheme
                assertEquals(themeId, error.themeId)
                assertEquals(characterId, error.characterId)

                assertNeverPersisted()
            }
        }
    }

    @Test
    fun `character in theme as minor character _ output failure`() {
        listOf(
            given(List(1) { themeId }, includedCharacters = mapOf(themeId to List(1) { minorCharacter(characterId) })),
            given(
                List(1) { themeId },
                includedCharacters = mapOf(themeId to List(4) { minorCharacter() } + minorCharacter(characterId))),
            given(
                List(1) { themeId },
                includedCharacters = mapOf(themeId to List(4) { majorCharacter() } + minorCharacter(characterId)))
        ).forEach {
            it.whenExecuted().assert {
                if (error !is CharacterIsNotMajorCharacterInTheme && error != null) throw error
                error as CharacterIsNotMajorCharacterInTheme
                assertEquals(themeId, error.themeId)
                assertEquals(characterId, error.characterId)

                assertNeverPersisted()
            }
        }
    }

    @Test
    fun `character in theme as major character`() {
        listOf(
            given(
                List(1) { themeId },
                includedCharacters = mapOf(themeId to List(4) { majorCharacter() } + majorCharacter(characterId)))
        ).forEach {
            it.whenExecuted().assert {
                if (error != null) throw error
                output as DemoteMajorCharacter.ResponseModel
                assertEquals(themeId, output.themeId)
                assertEquals(characterId, output.characterId)
                assertOutputContainsAllSections()

                assertNoNewThemesCreated()
                val updatedTheme = updatedThemes.single() as Theme
                updatedTheme.getMinorCharacterById(Character.Id(characterId)) as MinorCharacter

                assertCharacterArcDeleted()
            }
        }
    }

    @Test
    fun `only major character remaining in theme`() {
        listOf(
            given(List(1) { themeId }, includedCharacters = mapOf(themeId to List(1) { majorCharacter(characterId) })),
            given(
                List(1) { themeId },
                includedCharacters = mapOf(themeId to List(4) { minorCharacter() } + majorCharacter(characterId)))
        ).forEach {
            it.whenExecuted().assert {
                if (error != null) throw error
                output as DemoteMajorCharacter.ResponseModel
                assertTrue(output.themeRemoved)
            }
        }
    }

    inner class PreppedUseCase(themeIds: List<UUID>, includedCharacters: Map<UUID, List<Pair<UUID, Boolean>>>) {

        private val initialThemes = themeIds.map { uuid ->
            val charactersToInclude = includedCharacters[uuid]?.map {
                makeCharacter(
                    Character.Id(it.first),
                    Project.Id()
                ) to it.second
            }
            val theme = makeTheme(Theme.Id(uuid))
            if (charactersToInclude.isNullOrEmpty()) return@map theme
            charactersToInclude.fold(theme) { currentTheme, (character, promote) ->
                val themeWithCharacter =
                    currentTheme.withCharacterIncluded(character.id, character.displayName.value, character.media)
                if (promote) themeWithCharacter.withCharacterPromoted(character.id)
                else themeWithCharacter

            }
        }

        fun whenExecuted(): DemoteCharacterAssertions {
            val output = object : DemoteMajorCharacter.OutputPort {
                var result: Any? = null
                var failure: Exception? = null
                override fun receiveDemoteMajorCharacterFailure(failure: Exception) {
                    this.failure = failure
                }

                override fun receiveDemoteMajorCharacterResponse(response: DemoteMajorCharacter.ResponseModel) {
                    result = response
                }
            }
            val initialCharacterArcs = initialThemes.flatMap { theme ->
                theme.characters.map {
                    CharacterArc.planNewCharacterArc(
                        it.id,
                        theme.id,
                        theme.name
                    )
                }
            }
            var removedCharacterArc: Any? = null
            val removedSections = mutableListOf<CharacterArcSection>()
            val updatedThemes = mutableListOf<Theme>()
            val deletedThemes = mutableListOf<Theme>()

            val themeRepository = ThemeRepositoryDouble(onUpdateTheme = updatedThemes::add, onDeleteTheme = deletedThemes::add)
            initialThemes.forEach(themeRepository::givenTheme)
            val characterArcRepository = CharacterArcRepositoryDouble(onRemoveCharacterArc = {
                removedCharacterArc = it.themeId.uuid to it.characterId.uuid
            }, onUpdateCharacterArc = { arc ->
                val original = initialCharacterArcs.find { it.id == arc.id }!!
                val removedIds = original.arcSections.map { it.id }.toSet() - (arc.arcSections.map { it.id }.toSet())
                removedSections.addAll(original.arcSections.filter { it.id in removedIds })
            })
            initialCharacterArcs.forEach(characterArcRepository::givenCharacterArc)

            runBlocking {
                DemoteMajorCharacterUseCase(themeRepository, characterArcRepository).invoke(themeId, characterId, output)
            }
            return DemoteCharacterAssertions(
                themeId,
                characterId,
                output.failure,
                output.result,
                initialCharacterArcs.find { it.themeId.uuid == themeId },
                emptyList(),
                updatedThemes,
                deletedThemes,
                removedSections,
                removedCharacterArc
            )
        }
    }

    class DemoteCharacterAssertions(
        val themeId: UUID,
        val characterId: UUID,
        val error: Throwable?,
        val output: Any?,
        val initialCharacterArc: CharacterArc?,
        val addedThemes: List<Any?>,
        val updatedThemes: List<Any?>,
        val removedThemes: List<Any?>,
        val removedSections: List<Any?>,
        val removedCharacterArc: Any?
    ) {

        val sectionIdsThatShouldBeRemoved = initialCharacterArc
            ?.arcSections
            ?.map { it.id }
            ?.toSet()

        fun assert(block: DemoteCharacterAssertions.() -> Unit) {
            block()
        }

        fun assertNeverPersisted() {
            assertNoNewThemesCreated()
            assert(updatedThemes.isEmpty()) { "Theme was updated $updatedThemes" }
        }

        fun assertNoNewThemesCreated() {
            assert(addedThemes.isEmpty()) { "Theme was added $addedThemes" }
        }

        fun assertOutputContainsAllSections() {
            output as DemoteMajorCharacter.ResponseModel
            assertEquals(
                sectionIdsThatShouldBeRemoved?.size ?: 0,
                output.removedCharacterArcSections.size
            )
        }
        fun assertCharacterArcDeleted() {
            removedCharacterArc as Pair<*, *>
            assertEquals(themeId, removedCharacterArc.first)
            assertEquals(characterId, removedCharacterArc.second)
        }
    }

}