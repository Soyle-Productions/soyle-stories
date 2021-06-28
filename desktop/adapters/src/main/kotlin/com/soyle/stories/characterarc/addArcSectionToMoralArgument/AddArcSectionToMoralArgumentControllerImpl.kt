package com.soyle.stories.characterarc.addArcSectionToMoralArgument

import com.soyle.stories.usecase.character.arc.section.addCharacterArcSectionToMoralArgument.AddCharacterArcSectionToMoralArgument
import com.soyle.stories.common.ThreadTransformer
import java.util.*

class AddArcSectionToMoralArgumentControllerImpl(
    private val threadTransformer: ThreadTransformer,
    private val addCharacterArcSectionToMoralArgument: AddCharacterArcSectionToMoralArgument,
    private val addCharacterArcSectionToMoralArgumentOutput: AddCharacterArcSectionToMoralArgument.OutputPort
) : AddArcSectionToMoralArgumentController {

    override fun addCharacterArcSectionToMoralArgument(
        themeId: String,
        characterId: String,
        templateSectionId: String,
        indexInMoralArgument: Int?
    ) {
        val request = AddCharacterArcSectionToMoralArgument.RequestModel(
            UUID.fromString(themeId),
            UUID.fromString(characterId),
            UUID.fromString(templateSectionId),
            indexInMoralArgument
        )
        threadTransformer.async {
            addCharacterArcSectionToMoralArgument.invoke(
                request,
                addCharacterArcSectionToMoralArgumentOutput
            )
        }
    }

}