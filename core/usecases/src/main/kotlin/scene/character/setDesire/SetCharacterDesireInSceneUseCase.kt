package com.soyle.stories.usecase.scene.character.setDesire

import com.soyle.stories.domain.scene.Updated
import com.soyle.stories.usecase.scene.SceneRepository

class SetCharacterDesireInSceneUseCase(
    private val sceneRepository: SceneRepository
) : SetCharacterDesireInScene {
    override suspend fun invoke(
        request: SetCharacterDesireInScene.RequestModel,
        output: SetCharacterDesireInScene.OutputPort
    ) {
        val scene = sceneRepository.getSceneOrError(request.sceneId.uuid)
        val sceneUpdate = scene.withDesireForCharacter(request.characterId, request.desire)
        val response: SetCharacterDesireInScene.ResponseModel
        if (sceneUpdate is Updated) {
            sceneRepository.updateScene(sceneUpdate.scene)
            response = SetCharacterDesireInScene.ResponseModel(sceneUpdate.event)
        } else {
            response = SetCharacterDesireInScene.ResponseModel(null)
        }
        output.receiveSetCharacterDesireInSceneResponse(response)
    }
}