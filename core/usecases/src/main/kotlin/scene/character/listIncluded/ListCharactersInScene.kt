package com.soyle.stories.usecase.scene.character.listIncluded

import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.usecase.scene.common.IncludedCharacterInScene

interface ListCharactersInScene {
    suspend operator fun invoke(sceneId: Scene.Id, output: OutputPort)

    class ResponseModel(
        val sceneId: Scene.Id,
        val charactersInScene: List<IncludedCharacterInScene>
    )

    interface OutputPort {
        suspend fun receiveCharactersInScene(response: ResponseModel)
    }
}