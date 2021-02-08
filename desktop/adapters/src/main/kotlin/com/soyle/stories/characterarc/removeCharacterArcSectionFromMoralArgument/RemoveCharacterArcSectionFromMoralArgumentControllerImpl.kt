package com.soyle.stories.characterarc.removeCharacterArcSectionFromMoralArgument

import com.soyle.stories.usecase.character.removeCharacterArcSectionFromMoralArgument.RemoveCharacterArcSectionFromMoralArgument
import com.soyle.stories.common.ThreadTransformer
import java.util.*

class RemoveCharacterArcSectionFromMoralArgumentControllerImpl(
    private val threadTransformer: ThreadTransformer,
    private val removeCharacterArcSectionFromMoralArgument: RemoveCharacterArcSectionFromMoralArgument,
    private val removeCharacterArcSectionFromMoralArgumentOutput: RemoveCharacterArcSectionFromMoralArgument.OutputPort
) : RemoveCharacterArcSectionFromMoralArgumentController {

    override fun removeSectionFromMoralArgument(arcSectionId: String) {
        val preparedArcSectionId = UUID.fromString(arcSectionId)
        threadTransformer.async {
            removeCharacterArcSectionFromMoralArgument.invoke(
                preparedArcSectionId,
                removeCharacterArcSectionFromMoralArgumentOutput
            )
        }
    }
}