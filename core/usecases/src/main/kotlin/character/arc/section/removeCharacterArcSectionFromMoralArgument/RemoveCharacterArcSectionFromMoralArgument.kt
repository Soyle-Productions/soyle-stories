package com.soyle.stories.usecase.character.arc.section.removeCharacterArcSectionFromMoralArgument

import com.soyle.stories.usecase.character.arc.section.moveCharacterArcSectionInMoralArgument.CharacterArcSectionMovedInMoralArgument
import java.util.*

interface RemoveCharacterArcSectionFromMoralArgument {

    suspend operator fun invoke(arcSectionId: UUID, output: OutputPort)

    class ResponseModel(
        val removedSection: CharacterArcSectionRemoved,
        val movedSections: List<CharacterArcSectionMovedInMoralArgument>
    )

    interface OutputPort {
        suspend fun removedCharacterArcSectionFromMoralArgument(response: ResponseModel)
    }

}