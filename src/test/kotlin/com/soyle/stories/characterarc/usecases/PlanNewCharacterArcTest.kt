package com.soyle.stories.characterarc.usecases

import com.soyle.stories.character.CharacterDoesNotExist
import com.soyle.stories.character.doubles.CharacterRepositoryDouble
import com.soyle.stories.character.makeCharacter
import com.soyle.stories.characterarc.usecases.listAllCharacterArcs.CharacterArcItem
import com.soyle.stories.characterarc.usecases.planNewCharacterArc.PlanNewCharacterArc
import com.soyle.stories.characterarc.usecases.planNewCharacterArc.PlanNewCharacterArcUseCase
import com.soyle.stories.common.shouldBe
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.CharacterArcSection
import com.soyle.stories.entities.Project
import com.soyle.stories.entities.Theme
import com.soyle.stories.storyevent.characterDoesNotExist
import com.soyle.stories.theme.doubles.ThemeRepositoryDouble
import com.soyle.stories.theme.repositories.CharacterArcSectionRepository
import com.soyle.stories.theme.usecases.createTheme.CreatedTheme
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

class PlanNewCharacterArcTest {

    val projectId = Project.Id()

    val characterId = UUID.randomUUID()
    val characterArcName = "First character arc for character"

    private var addedTheme: Theme? = null
    private var updatedCharacter: Character? = null

    private var plannedCharacterArcEvent: CharacterArcItem? = null
    private var notedThemeEvent: CreatedTheme? = null

    @Test
    fun `character doesn't exist`() {
        assertThrows<CharacterDoesNotExist> {
            given(emptyList()).invoke(characterId, characterArcName)
        } shouldBe characterDoesNotExist(characterId)
    }

    @Nested
    inner class WhenCharacterExists {

        val characterName = "Bob the Builder"
        val character = makeCharacter(
            Character.Id(characterId), projectId, characterName
        )

        @Test
        fun `new theme is created`() {
            val useCase = given(listOf(character))
            useCase.invoke(characterId, characterArcName)
            addedTheme shouldBe {
                it as Theme
                assertEquals(projectId, it.projectId)
                assertEquals(characterArcName, it.name)
            }
        }

        @Test
        fun `character arc is persisted`() {
            val useCase = given(listOf(character))
            useCase.invoke(characterId, characterArcName)
            updatedCharacter!!.characterArcs.single()
        }

        @Test
        fun `character is major character in theme`() {
            val useCase = given(listOf(character))
            useCase.invoke(characterId, characterArcName)
            addedTheme!!.getMajorCharacterById(Character.Id(characterId))!!
        }

        @Test
        fun `character in theme has name of character`() {
            val useCase = given(listOf(character))
            useCase.invoke(characterId, characterArcName)
            assertEquals(characterName, addedTheme!!.getIncludedCharacterById(Character.Id(characterId))!!.name)
        }

        @Test
        fun `character arc has name provided`() {
            val useCase = given(listOf(character))
            useCase.invoke(characterId, characterArcName)
            assertEquals(characterArcName, updatedCharacter!!.characterArcs.single().name)
        }

        @Test
        fun `character arc should have required story sections and thematic sections`() {
            val addedArcSections = mutableListOf<CharacterArcSection>()
            val useCase = given(listOf(character), addNewCharacterArcSections = {
                addedArcSections.addAll(it)
            })
            useCase.invoke(characterId, characterArcName)
            val addedArc = updatedCharacter!!.characterArcs.single()
            val fullTemplate =
                (addedArc.template.sections.map { it.id } + addedTheme!!.thematicTemplate.sections.map { it.characterArcTemplateSectionId }).toSet()
            assertEquals(fullTemplate.size, addedArcSections.size)
        }

        @Test
        fun `output should have name provided`() {
            given(listOf(character)).invoke(characterId, characterArcName)
            val result = plannedCharacterArcEvent as CharacterArcItem
            assertEquals(characterArcName, result.characterArcName)
        }

        @Test
        fun `output should have id of created theme`() {
            val useCase = given(listOf(character))
            useCase.invoke(characterId, characterArcName)
            val result = plannedCharacterArcEvent as CharacterArcItem
            assertEquals(addedTheme!!.id.uuid, result.themeId)
        }
    }

    fun given(
        characters: List<Character>,
        addNewCharacterArcSections: (List<CharacterArcSection>) -> Unit = {}
    ): (UUID, String) -> Unit {
        val characterRepo = CharacterRepositoryDouble(initialCharacters = characters, onUpdateCharacter = {
            updatedCharacter = it
        })
        val themeRepo = ThemeRepositoryDouble(onAddTheme = { addedTheme = it })
        val repo = object : CharacterArcSectionRepository {
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
        val useCase: PlanNewCharacterArc = PlanNewCharacterArcUseCase(
            characterRepo,
            themeRepo,
            repo
        )
        val output = object : PlanNewCharacterArc.OutputPort {
            override suspend fun characterArcPlanned(response: CharacterArcItem) {
                plannedCharacterArcEvent = response
            }

            override suspend fun themeNoted(response: CreatedTheme) {
                notedThemeEvent = response
            }
        }
        return { uuid, name ->
            runBlocking {
                useCase.invoke(uuid, name, output)
            }
        }
    }

}