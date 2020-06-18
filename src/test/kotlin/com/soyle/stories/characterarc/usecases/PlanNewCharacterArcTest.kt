package com.soyle.stories.characterarc.usecases

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.soyle.stories.character.CharacterDoesNotExist
import com.soyle.stories.entities.*
import com.soyle.stories.theme.repositories.CharacterArcSectionRepository
import com.soyle.stories.theme.usecases.promoteMinorCharacter.PromoteMinorCharacterUseCase
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.*

/**
 * Created by Brendan
 * Date: 2/26/2020
 * Time: 3:16 PM
 */
class PlanNewCharacterArcTest {

    val projectId = Project.Id()

    fun given(
        characters: List<Character>,
        addNewTheme: (Theme) -> Unit = {},
        addNewCharacterArc: (CharacterArc) -> Unit = {},
        addNewCharacterArcSections: (List<CharacterArcSection>) -> Unit = {}
    ): (UUID, String) -> Either<*, *> {
        val repo = object : com.soyle.stories.characterarc.repositories.CharacterRepository,
            com.soyle.stories.characterarc.repositories.ThemeRepository, com.soyle.stories.theme.repositories.ThemeRepository,
            com.soyle.stories.theme.repositories.CharacterArcRepository, CharacterArcSectionRepository {
            val themes = mutableMapOf<Theme.Id, Theme>()
            override suspend fun listCharactersInProject(projectId: Project.Id): List<Character> = characters
            override suspend fun getCharacterById(characterId: Character.Id): Character? =
                characters.find { it.id == characterId }

            override suspend fun addNewTheme(theme: Theme) {
                addNewTheme.invoke(theme)
                themes[theme.id] = theme
            }

            override suspend fun getThemeById(themeId: Theme.Id): Theme? = themes[themeId]
            override suspend fun updateTheme(theme: Theme) {
                addNewTheme.invoke(theme)
                themes[theme.id] = theme
            }

            override suspend fun addTheme(theme: Theme) {
                TODO("Not yet implemented")
            }

            override suspend fun addNewCharacterArc(characterArc: CharacterArc) =
                addNewCharacterArc.invoke(characterArc)

            override suspend fun getCharacterArcByCharacterAndThemeId(
                characterId: Character.Id,
                themeId: Theme.Id
            ): CharacterArc? = null

            override suspend fun listCharacterArcsForTheme(themeId: Theme.Id): List<CharacterArc> = emptyList()
            override suspend fun addNewCharacterArcSections(characterArcSections: List<CharacterArcSection>) {
                addNewCharacterArcSections.invoke(characterArcSections)
            }

            override suspend fun getCharacterArcSectionById(characterArcSectionId: CharacterArcSection.Id): CharacterArcSection? = null
            override suspend fun getCharacterArcSectionsById(characterArcSectionIds: Set<CharacterArcSection.Id>): List<CharacterArcSection> {
                TODO("Not yet implemented")
            }

            override suspend fun removeArcSections(sections: List<CharacterArcSection>) {
            }

            override suspend fun updateCharacterArcSection(characterArcSection: CharacterArcSection) {

            }

            override suspend fun listThemesInProject(projectId: Project.Id): List<Theme> {
                TODO("Not yet implemented")
            }

            override suspend fun removeCharacterArc(themeId: Theme.Id, characterId: Character.Id) {

            }

            override suspend fun deleteTheme(theme: Theme) {

            }

            override suspend fun getCharacterArcSectionsForCharacter(characterId: Character.Id): List<CharacterArcSection> = emptyList()
            override suspend fun getCharacterArcSectionsForTheme(themeId: Theme.Id): List<CharacterArcSection> {
                TODO("Not yet implemented")
            }

            override suspend fun getCharacterArcSectionsForCharacterInTheme(
                characterId: Character.Id,
                themeId: Theme.Id
            ): List<CharacterArcSection> {
                return emptyList()
            }
        }
        val useCase: com.soyle.stories.characterarc.usecases.planNewCharacterArc.PlanNewCharacterArc =
            com.soyle.stories.characterarc.usecases.planNewCharacterArc.PlanNewCharacterArcUseCase(
                repo,
                repo,
                repo,
                PromoteMinorCharacterUseCase(repo, repo, repo)
            )
        val output = object : com.soyle.stories.characterarc.usecases.planNewCharacterArc.PlanNewCharacterArc.OutputPort {
            var result: Either<Exception, *>? = null
            override fun receivePlanNewCharacterArcFailure(failure: Exception) {
                result = failure.left()
            }

            override fun receivePlanNewCharacterArcResponse(response: com.soyle.stories.characterarc.usecases.listAllCharacterArcs.CharacterArcItem) {
                result = response.right()
            }
        }
        return { uuid, name ->
            runBlocking {
                useCase.invoke(uuid, name, output)
            }
            output.result!!
        }
    }

    val characterId = UUID.randomUUID()
    val characterArcName = "First character arc for character"

    @Nested
    inner class NonExistentCharacter {

        val useCase = given(emptyList())

        @Test
        fun `should fail with character does not exist error`() {
            val (result) = useCase(characterId, characterArcName) as Either.Left
            result as CharacterDoesNotExist
            assertEquals(characterId, result.characterId)
        }

    }

    @Nested
    inner class WhenCharacterExists {

        val characterName = "Bob the Builder"
        val character = Character(
            Character.Id(characterId), projectId, characterName
        )

        @Test
        fun `new theme is created`() {
            var addedTheme: Theme? = null
            val useCase = given(listOf(character), addNewTheme = {
                addedTheme = it
            })
            useCase.invoke(characterId, characterArcName)
            assertNotNull(addedTheme)
        }

        @Test
        fun `character arc is persisted`() {
            var addedArc: CharacterArc? = null
            val useCase = given(listOf(character), addNewCharacterArc = {
                addedArc = it
            })
            useCase.invoke(characterId, characterArcName)
            assertNotNull(addedArc)
        }

        @Test
        fun `character is major character in theme`() {
            var addedTheme: Theme? = null
            val useCase = given(listOf(character), addNewTheme = {
                addedTheme = it
            })
            useCase.invoke(characterId, characterArcName)
            addedTheme!!.getMajorCharacterById(Character.Id(characterId))!!
        }

        @Test
        fun `character in theme has name of character`() {
            var addedTheme: Theme? = null
            val useCase = given(listOf(character), addNewTheme = {
                addedTheme = it
            })
            useCase.invoke(characterId, characterArcName)
            assertEquals(characterName, addedTheme!!.getIncludedCharacterById(Character.Id(characterId))!!.name)
        }

        @Test
        fun `character arc has name provided`() {
            var addedArc: CharacterArc? = null
            val useCase = given(listOf(character), addNewCharacterArc = {
                addedArc = it
            })
            useCase.invoke(characterId, characterArcName)
            assertEquals(characterArcName, addedArc!!.name)
        }

        @Test
        fun `character arc should have required story sections and thematic sections`() {
            var addedArc: CharacterArc? = null
            var addedTheme: Theme? = null
            val addedArcSections = mutableListOf<CharacterArcSection>()
            val useCase = given(listOf(character), addNewCharacterArc = {
                addedArc = it
            }, addNewTheme = {
                addedTheme = it
            }, addNewCharacterArcSections = {
                addedArcSections.addAll(it)
            })
            useCase.invoke(characterId, characterArcName)
            val fullTemplate =
                (addedArc!!.template.sections.map { it.id } + addedTheme!!.thematicTemplate.sections.map { it.characterArcTemplateSectionId }).toSet()
            assertEquals(fullTemplate.size, addedArcSections.size)
        }

        @Test
        fun `output should have name provided`() {
            val (result) = given(listOf(character)).invoke(characterId, characterArcName) as Either.Right
            result as com.soyle.stories.characterarc.usecases.listAllCharacterArcs.CharacterArcItem
            assertEquals(characterArcName, result.characterArcName)
        }

        @Test
        fun `output should have id of created theme`() {
            var addedTheme: Theme? = null
            val useCase = given(listOf(character), addNewTheme = {
                addedTheme = it
            })
            val (result) = useCase.invoke(characterId, characterArcName) as Either.Right
            result as com.soyle.stories.characterarc.usecases.listAllCharacterArcs.CharacterArcItem
            assertEquals(addedTheme!!.id.uuid, result.themeId)
        }
    }

}