package com.soyle.stories.characterarc.usecases

import com.soyle.stories.character.CharacterDoesNotExist
import com.soyle.stories.doubles.CharacterRepositoryDouble
import com.soyle.stories.character.makeCharacter
import com.soyle.stories.characterarc.usecases.planNewCharacterArc.PlanNewCharacterArc
import com.soyle.stories.characterarc.usecases.planNewCharacterArc.PlanNewCharacterArcUseCase
import com.soyle.stories.common.shouldBe
import com.soyle.stories.common.str
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.CharacterArcSection
import com.soyle.stories.entities.Project
import com.soyle.stories.entities.Theme
import com.soyle.stories.storyevent.characterDoesNotExist
import com.soyle.stories.doubles.CharacterArcSectionRepositoryDouble
import com.soyle.stories.doubles.ThemeRepositoryDouble
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class PlanNewCharacterArcTest {

    // provided data
    private val projectId = Project.Id()
    private val characterId = Character.Id()
    private val characterArcName = "Character Arc ${str()}"

    // persisted data
    private var createdTheme: Theme? = null
    private var updatedCharacter: Character? = null
    private var createdArcSections: List<CharacterArcSection>? = null

    // output data
    private var result: PlanNewCharacterArc.ResponseModel? = null

    @Test
    fun `character doesn't exist`() {
        assertThrows<CharacterDoesNotExist> {
            planNewCharacterArc()
        } shouldBe characterDoesNotExist(characterId.uuid)
        assertNull(createdTheme)
        assertNull(updatedCharacter)
        assertNull(createdArcSections)
        assertNull(result)
    }

    @Nested
    inner class WhenCharacterExists {

        val characterName = "Bob the Builder"
        val character = makeCharacter(
            characterId, projectId, characterName
        )

        init {
            givenCharacter(character)
        }

        @Test
        fun `new theme is created`() {
            planNewCharacterArc()
            createdTheme!! shouldBe {
                assertEquals(projectId, it.projectId)
                assertEquals(characterArcName, it.name)
            }
        }

        @Test
        fun `character is major character in theme`() {
            planNewCharacterArc()
            createdTheme!!.getMajorCharacterById(characterId)!!
        }

        @Test
        fun `character arc has name provided`() {
            planNewCharacterArc()
            createdTheme!!.getMajorCharacterById(characterId)!!.characterArc shouldBe {
                assertEquals(characterArcName, it.name)
            }
        }

        @Test
        fun `character in theme has name of character`() {
            planNewCharacterArc()
            assertEquals(characterName, createdTheme!!.getIncludedCharacterById(characterId)!!.name)
        }

        @Test
        fun `character arc should have required story sections and thematic sections`() {
            planNewCharacterArc()
            val addedArc = createdTheme!!.getMajorCharacterById(characterId)!!.characterArc
            val fullTemplate =
                (addedArc.template.sections.map { it.id } + createdTheme!!.thematicTemplate.sections.map { it.characterArcTemplateSectionId }).toSet()
            assertEquals(fullTemplate.size, createdArcSections!!.size)
        }

        @Test
        fun `output should have name provided`() {
            planNewCharacterArc()
            assertEquals(characterArcName, result!!.createdCharacterArc.characterArcName)
        }

        @Test
        fun `output should have id of created theme`() {
            planNewCharacterArc()
            assertEquals(createdTheme!!.id.uuid, result!!.createdCharacterArc.themeId)
        }
    }

    private val themeRepository = ThemeRepositoryDouble(onAddTheme = {
        createdTheme = it
    })
    private val characterRepository =
        CharacterRepositoryDouble(onUpdateCharacter = {
            updatedCharacter = it
        })
    private val characterArcSectionRepositoryDouble = CharacterArcSectionRepositoryDouble(onAddNewCharacterArcSections = {
        createdArcSections = it
    })

    private fun givenCharacter(character: Character) {
        characterRepository.characters[character.id] = character
    }

    private fun planNewCharacterArc() {
        val useCase: PlanNewCharacterArc = PlanNewCharacterArcUseCase(
            characterRepository,
            themeRepository,
            characterArcSectionRepositoryDouble
        )
        val output = object : PlanNewCharacterArc.OutputPort {
            override suspend fun characterArcPlanned(response: PlanNewCharacterArc.ResponseModel) {
                result = response
            }
        }
        runBlocking {
            useCase.invoke(
                characterId.uuid,
                characterArcName,
                output
            )
        }
    }
}