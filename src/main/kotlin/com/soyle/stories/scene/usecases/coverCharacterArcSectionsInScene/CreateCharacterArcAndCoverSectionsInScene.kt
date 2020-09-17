package com.soyle.stories.scene.usecases.coverCharacterArcSectionsInScene

import com.soyle.stories.characterarc.usecases.planNewCharacterArc.CreatedCharacterArc
import com.soyle.stories.theme.usecases.createTheme.CreatedTheme
import java.util.*

interface CreateCharacterArcAndCoverSectionsInScene {

    suspend fun listCharacterArcSectionTypesForNewArc(output: OutputPort)

    class RequestModel(
        val characterId: UUID,
        val sceneId: UUID,
        val name: String,
        val coverSectionsWithTemplateIds: List<UUID>
    )

    suspend operator fun invoke(request: RequestModel, output: OutputPort)

    class ResponseModel(
        val createdTheme: CreatedTheme,
        val createdCharacterArc: CreatedCharacterArc,
        val sectionsCoveredByScene: List<CharacterArcSectionCoveredByScene>
    )

    interface OutputPort {

        suspend fun receiveCharacterArcSectionTypes(response: CharacterArcSectionTypes)
        suspend fun characterArcCreatedAndSectionsCovered(response: ResponseModel)

    }

}