package com.soyle.stories.usecase.scene.character.setDesire

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.events.CharacterDesireInSceneChanged

interface SetCharacterDesireInScene {

    class RequestModel(
        val sceneId: Scene.Id,
        val characterId: Character.Id,
        val desire: String
    )

    suspend operator fun invoke(request: RequestModel, output: OutputPort)

    class ResponseModel(
        val characterDesireInSceneChanged: CharacterDesireInSceneChanged?
    )

    interface OutputPort {
        suspend fun receiveSetCharacterDesireInSceneResponse(response: ResponseModel)
    }

}