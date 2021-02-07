package com.soyle.stories.usecase.character.removeCharacterArcSectionFromMoralArgument

import com.soyle.stories.domain.character.CharacterArc
import com.soyle.stories.domain.character.CharacterArcSection
import com.soyle.stories.usecase.character.CharacterArcRepository
import com.soyle.stories.usecase.character.moveCharacterArcSectionInMoralArgument.CharacterArcSectionMovedInMoralArgument
import com.soyle.stories.usecase.character.removeCharacterArcSectionFromMoralArgument.RemoveCharacterArcSectionFromMoralArgument.OutputPort
import com.soyle.stories.usecase.character.removeCharacterArcSectionFromMoralArgument.RemoveCharacterArcSectionFromMoralArgument.ResponseModel
import java.util.*

class RemoveCharacterArcSectionFromMoralArgumentUseCase(
    private val characterArcRepository: CharacterArcRepository
) : RemoveCharacterArcSectionFromMoralArgument {

    override suspend fun invoke(arcSectionId: UUID, output: OutputPort) {
        val arc = characterArcRepository.getCharacterArcWithArcSectionOrError(CharacterArcSection.Id(arcSectionId))
        val steps = UseCaseSteps(arcSectionId, arc)
        characterArcRepository.replaceCharacterArcs(steps.arcWithoutSection)

        output.removedCharacterArcSectionFromMoralArgument(
            ResponseModel(
                steps.characterArcSectionRemovedEvent,
                steps.movedMoralSectionEvents
            )
        )
    }

    private class UseCaseSteps(
        val arcSectionId: UUID,
        val arc: CharacterArc
    ) {

        private val section = arc.arcSections.find { it.id.uuid == arcSectionId }!!
        private val indexInMoralArgument = arc.indexInMoralArgument(section.id)

        val arcWithoutSection: CharacterArc = arc.withoutArcSection(section.id)

        val characterArcSectionRemovedEvent: CharacterArcSectionRemoved by lazy {
            CharacterArcSectionRemoved(arcSectionId, arc.themeId.uuid, arc.characterId.uuid, arc.id.uuid)
        }

        val movedMoralSectionEvents: List<CharacterArcSectionMovedInMoralArgument> by lazy {
            if (indexInMoralArgument != null) {
                arcWithoutSection.moralArgument()
                    .getSectionsAfter(indexInMoralArgument)
                    .map { it.getMovedInMoralArgumentEvent() }
            } else {
                listOf()
            }
        }

        private fun CharacterArc.MoralArgument.getSectionsAfter(index: Int): List<CharacterArcSection> =
            arcSections.subList(index, arcSections.size)

        private fun CharacterArcSection.getMovedInMoralArgumentEvent(): CharacterArcSectionMovedInMoralArgument {
            return CharacterArcSectionMovedInMoralArgument(
                id.uuid,
                arc.indexInMoralArgument(id)!!,
                arcWithoutSection.indexInMoralArgument(id)!!
            )
        }

    }

}