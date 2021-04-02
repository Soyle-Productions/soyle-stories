package com.soyle.stories.usecase.character.arc.section.addSectionToArc

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.CharacterArcTemplateSection
import com.soyle.stories.domain.character.TemplateSectionIsNotPartOfArcTemplate
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.usecase.character.CharacterArcDoesNotExist
import com.soyle.stories.usecase.character.CharacterArcRepository
import com.soyle.stories.usecase.character.CharacterArcTemplateSectionDoesNotExist
import com.soyle.stories.usecase.character.arc.section.addCharacterArcSectionToMoralArgument.ArcSectionAddedToCharacterArc
import java.util.*

class AddSectionToCharacterArcUseCase(
    private val characterArcRepository: CharacterArcRepository
) : AddSectionToCharacterArc {
    override suspend fun invoke(
        request: AddSectionToCharacterArc.RequestModel,
        output: AddSectionToCharacterArc.OutputPort
    ) {
        val characterArc = characterArcRepository.getCharacterArcOrError(request.characterId.uuid, request.themeId.uuid)
        val sectionTemplate = characterArc.template.getSectionById(request.sectionTemplateId)
            ?: throw TemplateSectionIsNotPartOfArcTemplate(
                characterArc.id.uuid,
                characterArc.characterId.uuid,
                characterArc.themeId.uuid,
                request.sectionTemplateId.uuid
            )
        val newArc = characterArc.withArcSection(sectionTemplate)
        val previousArcSectionIds = characterArc.arcSections.associateBy { it.id }.keys
        characterArcRepository.updateCharacterArcs(setOf(newArc))
        output.receiveAddSectionToCharacterArcResponse(
            AddSectionToCharacterArc.ResponseModel(
            ArcSectionAddedToCharacterArc(
                newArc,
                newArc.arcSections.single { it.id !in previousArcSectionIds }
            )
        ))
    }
}