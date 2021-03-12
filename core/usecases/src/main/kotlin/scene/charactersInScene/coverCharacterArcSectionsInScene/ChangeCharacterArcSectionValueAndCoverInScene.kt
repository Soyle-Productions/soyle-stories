package com.soyle.stories.usecase.scene.charactersInScene.coverCharacterArcSectionsInScene

import com.soyle.stories.usecase.character.changeCharacterArcSectionValue.ChangedCharacterArcSectionValue
import java.util.*

interface ChangeCharacterArcSectionValueAndCoverInScene {

    class RequestModel(
        val themeId: UUID,
        val characterId: UUID,
        val arcSectionId: UUID,
        val sceneId: UUID,
        val value: String
    )

    suspend operator fun invoke(request: RequestModel, output: OutputPort)

    class ResponseModel(
        val changedCharacterArcSectionValue: ChangedCharacterArcSectionValue,
        val characterArcSectionCoveredByScene: CharacterArcSectionCoveredByScene
    )

    interface OutputPort {
        suspend fun characterArcSectionValueChangedAndAddedToScene(response: ResponseModel)
    }

}