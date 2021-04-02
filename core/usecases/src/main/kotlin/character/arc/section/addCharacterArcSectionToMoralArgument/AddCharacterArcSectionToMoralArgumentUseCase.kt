package com.soyle.stories.usecase.character.arc.section.addCharacterArcSectionToMoralArgument

import com.soyle.stories.domain.character.TemplateSectionIsNotPartOfArcTemplate
import com.soyle.stories.usecase.character.CharacterArcRepository
import java.util.*

class AddCharacterArcSectionToMoralArgumentUseCase(
    private val characterArcRepository: CharacterArcRepository
) : ListAvailableArcSectionTypesToAddToMoralArgument, AddCharacterArcSectionToMoralArgument {

    /**
     * List Available Arc Section Types to Add to Moral Argument
     */
    override suspend fun invoke(
        themeId: UUID,
        characterId: UUID,
        output: ListAvailableArcSectionTypesToAddToMoralArgument.OutputPort
    ) {
        val characterArc = characterArcRepository.getCharacterArcOrError(characterId, themeId)
        val arcSectionsByTemplateId by lazy { characterArc.arcSections.groupBy { it.template.id } }

        output.receiveAvailableArcSectionTypesToAddToMoralArgument(
            ListAvailableArcSectionTypesToAddToMoralArgument.ResponseModel(
                themeId,
                characterId,
                characterArc.template.sections.filter { it.isMoral }.map { sectionTemplate ->
                    val existingArcSection = if (!sectionTemplate.allowsMultiple)
                        arcSectionsByTemplateId[sectionTemplate.id]?.first()
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
     * Add Character Arc Section to Moral Argument
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