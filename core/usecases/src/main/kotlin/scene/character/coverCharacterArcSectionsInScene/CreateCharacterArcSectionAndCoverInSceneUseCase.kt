package com.soyle.stories.usecase.scene.character.coverCharacterArcSectionsInScene

import com.soyle.stories.domain.character.CharacterArc
import com.soyle.stories.domain.character.CharacterArcTemplateSection
import com.soyle.stories.usecase.character.CharacterArcRepository
import com.soyle.stories.usecase.character.CharacterArcTemplateSectionDoesNotExist
import com.soyle.stories.usecase.character.arc.section.addCharacterArcSectionToMoralArgument.ArcSectionAddedToCharacterArc
import com.soyle.stories.usecase.scene.SceneRepository
import java.util.*

class CreateCharacterArcSectionAndCoverInSceneUseCase(
    private val characterArcRepository: CharacterArcRepository,
    private val sceneRepository: SceneRepository
) : CreateCharacterArcSectionAndCoverInScene, GetAvailableCharacterArcSectionTypesForCharacterArc {

    override suspend fun invoke(
        themeId: UUID,
        characterId: UUID,
        output: GetAvailableCharacterArcSectionTypesForCharacterArc.OutputPort
    ) {
        val characterArc = characterArcRepository.getCharacterArcOrError(characterId, themeId)
        val arcSectionsByTemplateId = characterArc.arcSections.associateBy { it.template.id }

        output.receiveAvailableCharacterArcSectionTypesForCharacterArc(
            AvailableCharacterArcSectionTypesForCharacterArc(
                characterId,
                themeId,
                characterArc.template.sections
                    .map { template ->
                        AvailableCharacterArcSectionType(template.id.uuid, template.name, template.allowsMultiple,
                            arcSectionsByTemplateId[template.id]?.let {
                                it.id.uuid to it.value
                            }
                        )
                    }
            )
        )
    }

    private fun List<CharacterArcTemplateSection>.takeAvailableSectionTemplates(
        characterArc: CharacterArc
    ): List<CharacterArcTemplateSection> {
        val usedTemplateSectionIds = characterArc.arcSections
            .map { it.template.id }
            .toSet()
        return filter { it.allowsMultiple || it.id !in usedTemplateSectionIds }
    }

    override suspend fun invoke(
        request: CreateCharacterArcSectionAndCoverInScene.RequestModel,
        output: CreateCharacterArcSectionAndCoverInScene.OutputPort
    ) {
        val arc = characterArcRepository.getCharacterArcOrError(request.characterId, request.themeId)

        // find requested template section in this arc's template
        val templateSection = arc.template.sections.find { it.id.uuid == request.sectionTemplateId }
            ?: throw CharacterArcTemplateSectionDoesNotExist(request.sectionTemplateId)

        // create new arc section with template and provided value
        val updatedArc = arc.withArcSection(templateSection, value = request.value)

        // find the only arc section in the updated arc that isn't in the original
        val newSection = updatedArc.arcSections.single { it !in arc.arcSections }

        // cover the new section in the scene and save the scene
        val scene = sceneRepository.getSceneOrError(request.sceneId)
        sceneRepository.updateScene(scene.withCharacterArcSectionCovered(newSection))

        characterArcRepository.replaceCharacterArcs(updatedArc)

        output.characterArcCreatedAndCoveredInScene(
            CreateCharacterArcSectionAndCoverInScene.ResponseModel(
                ArcSectionAddedToCharacterArc(arc, newSection),
                CharacterArcSectionCoveredByScene(
                    scene.id.uuid,
                    arc.characterId.uuid,
                    arc.themeId.uuid,
                    arc.id.uuid,
                    newSection.id.uuid,
                    newSection.template.name,
                    newSection.value,
                    arc.name,
                    newSection.template.allowsMultiple
                )
            )
        )
    }

}