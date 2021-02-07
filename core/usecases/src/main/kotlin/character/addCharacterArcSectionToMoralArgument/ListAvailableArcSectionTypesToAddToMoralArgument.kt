package com.soyle.stories.usecase.character.addCharacterArcSectionToMoralArgument

import java.util.*

interface ListAvailableArcSectionTypesToAddToMoralArgument {

    suspend operator fun invoke(themeId: UUID, characterId: UUID, output: OutputPort)

    class ResponseModel(
        val themeId: UUID,
        val characterId: UUID,
        availableArcSectionTypes: List<AvailableArcSectionType>
    ) : List<AvailableArcSectionType> by availableArcSectionTypes

    class AvailableArcSectionType(
        val sectionTemplateId: UUID,
        val sectionTemplateName: String,
        val existingSectionId: UUID?,
        val indexInMoralArgument: Int?
    ) {
        val canBeCreated: Boolean
            get() = existingSectionId == null
    }

    interface OutputPort {
        suspend fun receiveAvailableArcSectionTypesToAddToMoralArgument(response: ResponseModel)
    }

}