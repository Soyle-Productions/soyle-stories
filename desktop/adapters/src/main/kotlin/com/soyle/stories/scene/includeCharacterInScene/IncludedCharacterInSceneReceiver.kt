package com.soyle.stories.scene.includeCharacterInScene

import com.soyle.stories.usecase.scene.common.IncludedCharacterInScene

interface IncludedCharacterInSceneReceiver {

    suspend fun receiveIncludedCharacterInScene(includedCharacterInScene: IncludedCharacterInScene)

}