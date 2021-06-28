package com.soyle.stories.scene.charactersInScene.includeCharacterInScene

import com.soyle.stories.usecase.scene.common.IncludedCharacterInScene

interface IncludedCharacterInSceneReceiver {

    suspend fun receiveIncludedCharacterInScene(includedCharacterInScene: IncludedCharacterInScene)

}