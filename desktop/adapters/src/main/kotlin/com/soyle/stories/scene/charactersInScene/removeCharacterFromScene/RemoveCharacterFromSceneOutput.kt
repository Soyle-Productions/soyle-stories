package com.soyle.stories.scene.charactersInScene.removeCharacterFromScene

import com.soyle.stories.usecase.scene.charactersInScene.removeCharacterFromScene.RemoveCharacterFromScene

class RemoveCharacterFromSceneOutput(
    private val removedCharacterFromSceneReceiver: RemovedCharacterFromSceneReceiver
) : RemoveCharacterFromScene.OutputPort {
    override suspend fun characterRemovedFromScene(response: RemoveCharacterFromScene.ResponseModel) {
        removedCharacterFromSceneReceiver.receiveRemovedCharacterFromScene(response)
    }

    override fun failedToRemoveCharacterFromScene(failure: Exception) {
        throw failure
    }
}