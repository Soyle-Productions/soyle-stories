package com.soyle.stories.characterarc.usecases.removeCharacterArcSectionFromMoralArgument

import com.soyle.stories.characterarc.usecases.moveCharacterArcSectionInMoralArgument.CharacterArcSectionMovedInMoralArgument
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