/**
 * Created by Brendan
 * Date: 2/27/2020
 * Time: 10:31 PM
 */
package com.soyle.stories.character.usecases

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import com.soyle.stories.character.CharacterDoesNotExist
import com.soyle.stories.character.makeCharacter
import com.soyle.stories.entities.*
import com.soyle.stories.theme.repositories.CharacterArcSectionRepository
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.*

class RemoveCharacterFromStoryTest {

    private fun given(
        characters: List<Character>,
        themes: List<Theme>,
		characterArcSections: List<CharacterArcSection> = emptyList(),
        deleteCharacterWithId: (Character.Id) -> Unit = {},
        updateThemes: (List<Theme>) -> Unit = {},
        deleteThemes: (List<Theme>) -> Unit = {},
        deleteCharacterArcSections: (List<CharacterArcSection>) -> Unit = {}
    ): (UUID) -> Either<*, com.soyle.stories.character.usecases.removeCharacterFromStory.RemoveCharacterFromStory.ResponseModel> {
        val repo = object : com.soyle.stories.character.repositories.CharacterRepository,
            com.soyle.stories.character.repositories.ThemeRepository, CharacterArcSectionRepository {
            override suspend fun addNewCharacter(character: Character) = Unit
            override suspend fun getCharacterById(characterId: Character.Id): Character? =
                characters.find { it.id == characterId }

            override suspend fun deleteCharacterWithId(characterId: Character.Id) =
                deleteCharacterWithId.invoke(characterId)

            override suspend fun updateCharacter(character: Character) {

            }

            override suspend fun listCharactersInProject(projectId: Project.Id): List<Character> {
                TODO("Not yet implemented")
            }

            override suspend fun getThemesWithCharacterIncluded(characterId: Character.Id): List<Theme> =
                themes.filter {
                    it.containsCharacter(characterId)
                }

            override suspend fun updateThemes(themes: List<Theme>) = updateThemes.invoke(themes)
            override suspend fun deleteThemes(themes: List<Theme>) = deleteThemes.invoke(themes)
            override suspend fun addNewCharacterArcSections(characterArcSections: List<CharacterArcSection>) {
                TODO("Not yet implemented")
            }

            override suspend fun getCharacterArcSectionById(characterArcSectionId: CharacterArcSection.Id): CharacterArcSection? {
                TODO("Not yet implemented")
            }

            override suspend fun getCharacterArcSectionsById(characterArcSectionIds: Set<CharacterArcSection.Id>): List<CharacterArcSection> {
                TODO("Not yet implemented")
            }

            override suspend fun getCharacterArcSectionsForCharacter(characterId: Character.Id): List<CharacterArcSection> = characterArcSections.filter { it.characterId == characterId }
            override suspend fun getCharacterArcSectionsForTheme(themeId: Theme.Id): List<CharacterArcSection> {
                TODO("Not yet implemented")
            }

            override suspend fun getCharacterArcSectionsForCharacterInTheme(
                characterId: Character.Id,
                themeId: Theme.Id
            ): List<CharacterArcSection> {
                TODO("Not yet implemented")
            }

            override suspend fun removeArcSections(sections: List<CharacterArcSection>) {
                deleteCharacterArcSections.invoke(sections)
            }

            override suspend fun updateCharacterArcSection(characterArcSection: CharacterArcSection) {
                TODO("Not yet implemented")
            }

            override suspend fun getThemeById(id: Theme.Id): Theme? {
                TODO("Not yet implemented")
            }
        }
        val useCase: com.soyle.stories.character.usecases.removeCharacterFromStory.RemoveCharacterFromStory =
            com.soyle.stories.character.usecases.removeCharacterFromStory.RemoveCharacterFromStoryUseCase(
                repo,
                repo,
                repo
            )
        val output = object : com.soyle.stories.character.usecases.removeCharacterFromStory.RemoveCharacterFromStory.OutputPort {
            var result: Either<Exception, com.soyle.stories.character.usecases.removeCharacterFromStory.RemoveCharacterFromStory.ResponseModel>? = null
            override fun receiveRemoveCharacterFromStoryFailure(failure: Exception) {
                result = failure.left()
            }

            override fun receiveRemoveCharacterFromStoryResponse(response: com.soyle.stories.character.usecases.removeCharacterFromStory.RemoveCharacterFromStory.ResponseModel) {
                result = response.right()
            }
        }
        return {
            runBlocking {
                useCase.invoke(it, output)
            }
            output.result!!
        }
    }

    val characterUUID = UUID.randomUUID()

    @Nested
    inner class GivenCharacterDoesNotExist {

        val useCase = given(emptyList(), emptyList())

        @Test
        fun `should output character does not exist error`() {
            val (result) = useCase.invoke(characterUUID) as Either.Left
            result as CharacterDoesNotExist
            assertEquals(characterUUID, result.characterId)
        }

    }

    @Nested
    inner class OnSuccess {

        fun buildCharacter() = makeCharacter(
            Character.Id(UUID.randomUUID()),
            Project.Id(),
            "Character Name"
        )

        val character = makeCharacter(
            Character.Id(characterUUID),
          Project.Id(),
            "Character Name"
        )

        val themeWithOnlyCharacter = (Theme.takeNoteOf(Project.Id(), "").flatMap {
            it.withCharacterIncluded(
                character.id,
                character.name,
                character.media
            ).right()
        } as Either.Right).b
        val themesWithCharacter = listOf(
            (Theme.takeNoteOf(Project.Id(), "").flatMap {
                it.withCharacterIncluded(
                    character.id,
                    character.name,
                    character.media
                ).right()
            }
                .flatMap {
                    val character1 = buildCharacter()
                    it.withCharacterIncluded(character1.id, character1.name, character1.media).right()
                } as Either.Right).b,
            (Theme.takeNoteOf(Project.Id(), "").flatMap {
                it.withCharacterIncluded(
                    character.id,
                    character.name,
                    character.media
                ).right()
            }
                .flatMap {
                    val character1 = buildCharacter()
                    it.withCharacterIncluded(character1.id, character1.name, character1.media).right()
                } as Either.Right).b
        )
        val allThemesWithCharacter = (themesWithCharacter + themeWithOnlyCharacter).onEach {
            assertTrue(it.containsCharacter(character.id))
        }
        val themesWithoutCharacter = listOf(
            (Theme.takeNoteOf(Project.Id(), "") as Either.Right).b,
            (Theme.takeNoteOf(Project.Id(), "") as Either.Right).b
        )
        val useCase = given(
            listOf(character),
            themes = themesWithCharacter + themesWithoutCharacter
        )

        @Test
        fun `output should include provided character id`() {
            val (result) = useCase(characterUUID) as Either.Right
            assertEquals(characterUUID, result.characterId)
        }

        @Test
        fun `output should include ids of affected themes`() {
            val (result) = useCase(characterUUID) as Either.Right
            assertEquals(themesWithCharacter.map { it.id.uuid }.toSet(), result.affectedThemeIds.toSet())
        }

        @Test
        fun `character should be deleted`() {
            var deletedCharacterId: UUID? = null
            val useCase = given(
                listOf(character),
                themes = allThemesWithCharacter + themesWithoutCharacter,
                deleteCharacterWithId = {
                    deletedCharacterId = it.uuid
                }
            )
            useCase(characterUUID)
            assertEquals(characterUUID, deletedCharacterId)
        }

        @Test
        fun `themes with multiple characters should be updated`() {
            var updatedThemes: List<Theme>? = null
            val useCase = given(
                listOf(character),
                themes = allThemesWithCharacter + themesWithoutCharacter,
                updateThemes = {
                    updatedThemes = it
                }
            )
            val (result) = useCase(characterUUID) as Either.Right
            assertEquals(
                themesWithCharacter.map { it.id }.toSet(),
                updatedThemes!!.map { it.id }.toSet()
            )
            updatedThemes!!.forEach {
                assertFalse(it.containsCharacter(character.id))
            }
            assertEquals(themesWithCharacter.map { it.id.uuid }.toSet(), result.affectedThemeIds.toSet())
        }

        @Test
        fun `themes with only character should be deleted`() {
            var deletedThemes: List<Theme>? = null
            val useCase = given(
                listOf(character),
                themes = allThemesWithCharacter + themesWithoutCharacter,
                deleteThemes = {
                    deletedThemes = it
                }
            )
            val (result) = useCase(characterUUID) as Either.Right
            assertEquals(setOf(themeWithOnlyCharacter.id), deletedThemes!!.map { it.id }.toSet())
            deletedThemes!!.forEach {
                assertFalse(it.containsCharacter(character.id))
            }
            assertEquals(setOf(themeWithOnlyCharacter.id.uuid), result.removedThemeIds.toSet())
        }

        @Test
        fun `character arc sections for character should be deleted`() {
			val sections = List(10) {
				CharacterArcSection.planNewCharacterArcSection(
					character.id, Theme.Id(UUID.randomUUID()), CharacterArcTemplateSection(
						CharacterArcTemplateSection.Id(
							UUID.randomUUID()
						), ""
					)
				)
			}
            val deletedSections = mutableListOf<CharacterArcSection>()
            given(
                listOf(character),
                themes = allThemesWithCharacter + themesWithoutCharacter,
                characterArcSections = sections,
				deleteCharacterArcSections = {
                    deletedSections.addAll(it)
                }
            ).invoke(characterUUID)
			assertEquals(sections.map { it.id }.toSet(), deletedSections.map { it.id }.toSet())
        }

    }

}