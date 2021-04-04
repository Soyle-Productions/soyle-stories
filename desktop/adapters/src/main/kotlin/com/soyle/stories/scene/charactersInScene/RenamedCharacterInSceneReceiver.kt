package com.soyle.stories.scene.charactersInScene

import com.soyle.stories.domain.scene.events.RenamedCharacterInScene

interface RenamedCharacterInSceneReceiver {

    suspend fun receiveRenamedCharacterInScene(renamedCharacterInScene: RenamedCharacterInScene)

}