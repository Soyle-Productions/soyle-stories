package com.soyle.stories.usecase.scene.character.coverCharacterArcSectionsInScene

import java.util.*

interface GetAvailableCharacterArcsForCharacterInScene {

    suspend fun invoke(sceneId: UUID, characterId: UUID, output: OutputPort)

    interface OutputPort {
        suspend fun availableCharacterArcSectionsForCharacterInSceneListed(response: AvailableCharacterArcSectionsForCharacterInScene)
    }

}