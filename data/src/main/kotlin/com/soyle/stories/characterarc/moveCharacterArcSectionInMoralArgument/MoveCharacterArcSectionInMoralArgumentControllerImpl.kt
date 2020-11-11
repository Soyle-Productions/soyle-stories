package com.soyle.stories.characterarc.moveCharacterArcSectionInMoralArgument

import com.soyle.stories.characterarc.usecases.moveCharacterArcSectionInMoralArgument.MoveCharacterArcSectionInMoralArgument
import com.soyle.stories.common.ThreadTransformer
import java.util.*

class MoveCharacterArcSectionInMoralArgumentControllerImpl(
    private val threadTransformer: ThreadTransformer,
    private val moveCharacterArcSectionInMoralArgument: MoveCharacterArcSectionInMoralArgument,
    private val moveCharacterArcSectionInMoralArgumentOutput: MoveCharacterArcSectionInMoralArgument.OutputPort
) : MoveCharacterArcSectionInMoralArgumentController {

    override fun moveSectionInMoralArgument(sectionId: String, themeId: String, characterId: String, index: Int) {
        val request = MoveCharacterArcSectionInMoralArgument.RequestModel(
            UUID.fromString(themeId),
            UUID.fromString(characterId),
            UUID.fromString(sectionId),
            index
        )
        threadTransformer.async {
            moveCharacterArcSectionInMoralArgument.invoke(
                request,
                moveCharacterArcSectionInMoralArgumentOutput
            )
        }
    }

}