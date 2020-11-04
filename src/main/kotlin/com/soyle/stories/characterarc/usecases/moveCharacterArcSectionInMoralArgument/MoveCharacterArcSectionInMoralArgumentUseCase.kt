package com.soyle.stories.characterarc.usecases.moveCharacterArcSectionInMoralArgument

import com.soyle.stories.characterarc.CharacterArcSectionAlreadyInPosition
import com.soyle.stories.characterarc.CharacterArcSectionDoesNotExist
import com.soyle.stories.characterarc.CharacterArcSectionNotInMoralArgument
import com.soyle.stories.characterarc.repositories.getCharacterArcOrError
import com.soyle.stories.theme.repositories.CharacterArcRepository
import com.soyle.stories.characterarc.usecases.moveCharacterArcSectionInMoralArgument.MoveCharacterArcSectionInMoralArgument.*
import com.soyle.stories.entities.CharacterArc
import com.soyle.stories.entities.CharacterArcSection
import java.util.*

class MoveCharacterArcSectionInMoralArgumentUseCase(
    private val characterArcRepository: CharacterArcRepository
) : MoveCharacterArcSectionInMoralArgument {

    override suspend fun invoke(request: RequestModel, output: OutputPort) {
        val arc = characterArcRepository.getCharacterArcOrError(request.characterId, request.themeId)
        val arcSection = arc.getArcSectionOrError(request.arcSectionId)

        ensureSectionNotAlreadyAtIndex(arc, arcSection, request.index)

        val newArc = arc.moralArgument().withSectionMovedTo(arcSection.id, request.index)
        characterArcRepository.replaceCharacterArcs(newArc)

        val response = ResponseModel(
            arc.themeId.uuid,
            arc.characterId.uuid,
            arc.id.uuid,
            newArc.getMoralSectionsThatHaveMovedFromPositionsInArc(arc)
                .map {
                    CharacterArcSectionMovedInMoralArgument(
                        it.id.uuid,
                        arc.indexInMoralArgument(it.id)!!,
                        newArc.indexInMoralArgument(it.id)!!
                    )
                }
                .toList()
        )
        output.receiveMoveCharacterArcSectionInMoralArgumentResponse(response)
    }

    private fun CharacterArc.getArcSectionOrError(
        arcSectionId: UUID
    ): CharacterArcSection {
        return getArcSection(CharacterArcSection.Id(arcSectionId))
            ?: throw CharacterArcSectionDoesNotExist(arcSectionId)
    }

    private fun ensureSectionNotAlreadyAtIndex(
        arc: CharacterArc,
        arcSection: CharacterArcSection,
        index: Int
    ) {
        val currentIndex = arc.getIndexInMoralArgumentOrError(arcSection)

        if (index == currentIndex) throw CharacterArcSectionAlreadyInPosition(arcSection.id.uuid, index)
    }

    private fun CharacterArc.getIndexInMoralArgumentOrError(
        arcSection: CharacterArcSection
    ): Int {
        return indexInMoralArgument(arcSection.id)
            ?: throw CharacterArcSectionNotInMoralArgument(
                arcSection.id.uuid,
                characterId.uuid,
                themeId.uuid,
                id.uuid
            )
    }

    private fun CharacterArc.getMoralSectionsThatHaveMovedFromPositionsInArc(originalArc: CharacterArc): Sequence<CharacterArcSection>
    {
        return moralArgument().arcSections
            .asSequence()
            .filter { originalArc.indexInMoralArgument(it.id)!! != indexInMoralArgument(it.id)!! }
    }
}