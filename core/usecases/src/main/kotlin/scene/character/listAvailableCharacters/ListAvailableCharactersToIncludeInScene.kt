package com.soyle.stories.usecase.scene.character.listAvailableCharacters

import java.util.*

interface ListAvailableCharactersToIncludeInScene {

    suspend operator fun invoke(sceneId: UUID, output: OutputPort)

    interface OutputPort {
        suspend fun receiveAvailableCharactersToAddToScene(response: AvailableCharactersToAddToScene)
    }

}