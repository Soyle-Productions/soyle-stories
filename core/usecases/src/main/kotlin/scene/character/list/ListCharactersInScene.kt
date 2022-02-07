package com.soyle.stories.usecase.scene.character.list

import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.usecase.scene.common.IncludedCharacterInScene

interface ListCharactersInScene {
    suspend operator fun invoke(sceneId: Scene.Id, output: OutputPort)

    fun interface OutputPort {
        suspend fun receiveCharactersInScene(charactersInScene: CharactersInScene)
    }
}