package com.soyle.stories.scene.usecases.coverCharacterArcSectionsInScene

import java.util.*

interface CreateCharacterArcSectionAndCoverInScene {

    suspend fun listAvailableCharacterArcSectionTypesForCharacterArc(themeId: UUID, characterId: UUID, output: OutputPort)

    class RequestModel(
        val themeId: UUID,
        val characterId: UUID,
        val sectionTemplateId: UUID,
        val value: String
    )

    suspend operator fun invoke(request: RequestModel, output: OutputPort)

    interface OutputPort {
        suspend fun receiveAvailableCharacterArcSectionTypesForCharacterArc(response: AvailableCharacterArcSectionTypesForCharacterArc)
    }

}