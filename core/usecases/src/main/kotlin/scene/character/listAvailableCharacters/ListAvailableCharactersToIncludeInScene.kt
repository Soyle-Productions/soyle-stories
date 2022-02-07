package com.soyle.stories.usecase.scene.character.listAvailableCharacters

import com.soyle.stories.domain.scene.Scene
import java.util.*

interface ListAvailableCharactersToIncludeInScene {

    suspend operator fun invoke(sceneId: Scene.Id, output: OutputPort): Result<Unit>

    fun interface OutputPort {
        suspend fun receiveAvailableCharactersToAddToScene(response: AvailableCharactersToAddToScene)
    }

}