package com.soyle.stories.scene.usecases.coverCharacterArcSectionsInScene

import java.util.*

interface CreateCharacterArcSectionAndCoverInScene {

    class RequestModel(
        val themeId: UUID,
        val characterId: UUID,
        val sceneId: UUID,
        val sectionTemplateId: UUID,
        val value: String
    )

    suspend operator fun invoke(request: RequestModel, output: OutputPort)

    class ResponseModel(
        val createdCharacterArcSection: CreatedCharacterArcSection,
        val characterArcSectionCoveredByScene: CharacterArcSectionCoveredByScene
    )

    interface OutputPort {
        suspend fun characterArcCreatedAndCoveredInScene(response: ResponseModel)
    }

}