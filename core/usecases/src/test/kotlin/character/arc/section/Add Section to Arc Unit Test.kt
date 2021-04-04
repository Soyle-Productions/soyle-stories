package com.soyle.stories.usecase.character.arc.section

import com.soyle.stories.domain.character.*
import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.theme.makeTheme
import com.soyle.stories.usecase.character.CharacterArcDoesNotExist
import com.soyle.stories.usecase.character.CharacterArcTemplateSectionDoesNotExist
import com.soyle.stories.usecase.character.arc.section.addSectionToArc.AddSectionToCharacterArc
import com.soyle.stories.usecase.character.arc.section.addSectionToArc.AddSectionToCharacterArcUseCase
import com.soyle.stories.usecase.repositories.CharacterArcRepositoryDouble
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.fail

class `Add Section to Arc Unit Test` {

    private val character = makeCharacter()
    private val theme = makeTheme()
    private val characterArc = makeCharacterArc(character.id, theme.id)
    private val sectionTemplate = Drive

    private var updatedCharacterArc: CharacterArc? = null
    private var result: AddSectionToCharacterArc.ResponseModel? = null

    private val characterArcRepository = CharacterArcRepositoryDouble(onUpdateCharacterArc = ::updatedCharacterArc::set)

    @Test
    fun `character arc does not exist`() {
        val error = assertThrows<CharacterArcDoesNotExist> {
            addSectionToArc()
        }
        error.characterId.mustEqual(character.id.uuid)
        error.themeId.mustEqual(theme.id.uuid)
    }

    @Nested
    inner class `Given Character Arc Exists`
    {

        init {
            characterArcRepository.givenCharacterArc(characterArc)
        }

        @Test
        fun `section template not in character arc template`() {
            val failingId = CharacterArcTemplateSection.Id()
            val error = assertThrows<TemplateSectionIsNotPartOfArcTemplate> {
                addSectionToArc(failingId)
            }
            error.arcId.mustEqual(characterArc.id.uuid)
            error.characterId.mustEqual(character.id.uuid)
            error.themeId.mustEqual(theme.id.uuid)
            error.templateSectionId.mustEqual(failingId.uuid)
        }

        @Test
        fun `should update character arc`() {
            addSectionToArc()
            updatedCharacterArc!!.arcSections.single { it.template === sectionTemplate }
        }

        @Test
        fun `should output event`() {
            addSectionToArc()
            result!!.sectionAddedToCharacterArc
        }

    }

    private fun addSectionToArc(sectionTemplateId: CharacterArcTemplateSection.Id = sectionTemplate.id) {
        val useCase: AddSectionToCharacterArc = AddSectionToCharacterArcUseCase(characterArcRepository)
        val output = object : AddSectionToCharacterArc.OutputPort {
            override suspend fun receiveAddSectionToCharacterArcResponse(response: AddSectionToCharacterArc.ResponseModel) {
                result = response
            }
        }
        val request = AddSectionToCharacterArc.RequestModel(character.id, theme.id, sectionTemplateId)
        runBlocking {
            useCase.invoke(request, output)
        }
    }

}