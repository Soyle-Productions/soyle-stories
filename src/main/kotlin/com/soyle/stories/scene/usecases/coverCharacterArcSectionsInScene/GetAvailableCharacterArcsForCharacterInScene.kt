package com.soyle.stories.scene.usecases.coverCharacterArcSectionsInScene

import java.util.*

interface GetAvailableCharacterArcsForCharacterInScene {

    suspend fun invoke(sceneId: UUID, characterId: UUID, output: OutputPort)

    interface OutputPort {
        suspend fun availableCharacterArcSectionsForCharacterInSceneListed(response: AvailableCharacterArcSectionsForCharacterInScene)
    }

}