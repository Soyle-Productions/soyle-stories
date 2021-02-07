package com.soyle.stories.scene.usecases.includeCharacterInScene

import java.util.*

interface GetAvailableCharactersToAddToScene {

    suspend operator fun invoke(sceneId: UUID, output: OutputPort)

    interface OutputPort {
        suspend fun receiveAvailableCharactersToAddToScene(response: AvailableCharactersToAddToScene)
    }

}