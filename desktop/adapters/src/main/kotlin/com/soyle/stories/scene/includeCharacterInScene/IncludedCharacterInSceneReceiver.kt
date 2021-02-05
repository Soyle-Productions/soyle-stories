package com.soyle.stories.scene.includeCharacterInScene

import com.soyle.stories.scene.usecases.common.IncludedCharacterInScene

interface IncludedCharacterInSceneReceiver {

    suspend fun receiveIncludedCharacterInScene(includedCharacterInScene: IncludedCharacterInScene)

}