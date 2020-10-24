package com.soyle.stories.characterarc.usecases.addCharacterArcSectionToMoralArgument

import com.soyle.stories.characterarc.ArcTemplateSectionIsNotMoral
import com.soyle.stories.characterarc.CharacterArcAlreadyContainsMaximumNumberOfTemplateSection
import com.soyle.stories.characterarc.CharacterArcDoesNotExist
import com.soyle.stories.characterarc.TemplateSectionIsNotPartOfArcTemplate
import com.soyle.stories.characterarc.repositories.getCharacterArcOrError
import com.soyle.stories.common.template
import com.soyle.stories.theme.repositories.CharacterArcRepository
import java.util.*

class AddCharacterArcSectionToMoralArgumentUseCase(
    private val characterArcRepository: CharacterArcRepository
) : ListAvailableArcSectionTypesToAddToMoralArgument, AddCharacterArcSectionToMoralArgument {

    /**
     * ListAvailableArcSectionTypesToAddToMoralArgument
     */
    override suspend fun invoke(
        themeId: UUID,
        characterId: UUID,
        output: ListAvailableArcSectionTypesToAddToMoralArgument.OutputPort
    ) {
        val characterArc = characterArcRepository.getCharacterArcOrError(characterId, themeId)
        val arcSectionsByTemplateId by lazy { characterArc.arcSections.associateBy { it.template.id } }

        output.receiveAvailableArcSectionTypesToAddToMoralArgument(
            ListAvailableArcSectionTypesToAddToMoralArgument.ResponseModel(
                themeId,
                characterId,
                characterArc.template.sections.filter { it.isMoral }.map { sectionTemplate ->
                    val existingArcSection = if (!sectionTemplate.allowsMultiple)
                        arcSectionsByTemplateId[sectionTemplate.id]
                    else
                        null
                    ListAvailableArcSectionTypesToAddToMoralArgument.AvailableArcSectionType(
                        sectionTemplate.id.uuid,
                        sectionTemplate.name,
                        existingArcSection?.id?.uuid,
                        existingArcSection?.let { characterArc.indexInMoralArgument(it.id) }
                    )
                }
            )
        )
    }

    /**
     * AddCharacterArcSectionToMoralArgument
     */
    override suspend fun invoke(
        request: AddCharacterArcSectionToMoralArgument.RequestModel,
        output: AddCharacterArcSectionToMoralArgument.OutputPort
    ) {
        val characterArc = characterArcRepository.getCharacterArcOrError(request.characterId, request.themeId)
        val templateSection = characterArc.template.sections.find { it.id.uuid == request.templateSectionId }
            ?: throw TemplateSectionIsNotPartOfArcTemplate(
                characterArc.id.uuid,
                characterArc.characterId.uuid,
                characterArc.themeId.uuid,
                request.templateSectionId
            )
        if (!templateSection.isMoral) {
            throw ArcTemplateSectionIsNotMoral(
                characterArc.id.uuid,
                characterArc.characterId.uuid,
                characterArc.themeId.uuid,
                request.templateSectionId
            )
        }
        val newCharacterArc = characterArc.moralArgument().withArcSection(templateSection, index = request.indexInMoralArgument)
        val previousCharacterArcSectionIds = characterArc.arcSections.map { it.id }.toSet()
        val newSection = newCharacterArc.arcSections.find { it.id !in previousCharacterArcSectionIds }!!
        characterArcRepository.replaceCharacterArcs(
            newCharacterArc
        )
        output.characterArcSectionAddedToMoralArgument(
            AddCharacterArcSectionToMoralArgument.ResponseModel(
                ArcSectionAddedToCharacterArc(
                    newCharacterArc, newSection
                )
            )
        )
    }

}