package com.soyle.stories.usecase.scene.charactersInScene.coverCharacterArcSectionsInScene

import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.usecase.character.planNewCharacterArc.CreatedCharacterArc
import com.soyle.stories.usecase.theme.createTheme.CreatedTheme
import java.util.*

interface CreateCharacterArcAndCoverSectionsInScene {

    suspend fun listCharacterArcSectionTypesForNewArc(output: OutputPort)

    class RequestModel(
        val characterId: UUID,
        val sceneId: UUID,
        val name: NonBlankString,
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