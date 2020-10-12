package com.soyle.stories.scene.usecases.coverCharacterArcSectionsInScene

import com.soyle.stories.characterarc.usecases.changeCharacterArcSectionValue.ChangedCharacterArcSectionValue
import com.soyle.stories.scene.usecases.getSceneDetails.CoveredArcSectionInScene
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
        val characterArcSectionCoveredByScene: CoveredArcSectionInScene
    )

    interface OutputPort {
        suspend fun characterArcSectionValueChangedAndAddedToScene(response: ResponseModel)
    }

}