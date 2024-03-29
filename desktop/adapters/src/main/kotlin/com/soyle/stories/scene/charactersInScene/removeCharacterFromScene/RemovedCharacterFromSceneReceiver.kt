package com.soyle.stories.scene.charactersInScene.removeCharacterFromScene

import com.soyle.stories.usecase.scene.character.removeCharacterFromScene.RemoveCharacterFromScene

interface RemovedCharacterFromSceneReceiver {
    suspend fun receiveRemovedCharacterFromScene(removedCharacterFromScene: RemoveCharacterFromScene.ResponseModel)
}