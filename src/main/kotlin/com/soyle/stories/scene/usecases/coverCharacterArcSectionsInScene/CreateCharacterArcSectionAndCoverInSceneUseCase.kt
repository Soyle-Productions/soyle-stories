package com.soyle.stories.scene.usecases.coverCharacterArcSectionsInScene

import com.soyle.stories.characterarc.CharacterArcDoesNotExist
import com.soyle.stories.characterarc.repositories.getCharacterArcOrError
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.CharacterArc
import com.soyle.stories.entities.CharacterArcTemplateSection
import com.soyle.stories.entities.Theme
import com.soyle.stories.theme.repositories.CharacterArcRepository
import java.util.*

class CreateCharacterArcSectionAndCoverInSceneUseCase(
    private val characterArcRepository: CharacterArcRepository
) : CreateCharacterArcSectionAndCoverInScene {

    override suspend fun listAvailableCharacterArcSectionTypesForCharacterArc(
        themeId: UUID,
        characterId: UUID,
        output: CreateCharacterArcSectionAndCoverInScene.OutputPort
    ) {
        val characterArc = characterArcRepository.getCharacterArcOrError(characterId, themeId)

        output.receiveAvailableCharacterArcSectionTypesForCharacterArc(
            AvailableCharacterArcSectionTypesForCharacterArc(
                characterId,
                themeId,
                characterArc.template.sections
                    .takeAvailableSectionTemplates(characterArc)
                    .map { AvailableCharacterArcSectionType(it.id.uuid, it.name, it.allowsMultiple) }
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
        TODO("Not yet implemented")
    }

}