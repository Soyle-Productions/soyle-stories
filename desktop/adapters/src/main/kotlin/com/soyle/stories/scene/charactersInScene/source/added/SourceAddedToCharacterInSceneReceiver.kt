package com.soyle.stories.scene.charactersInScene.source.added

import com.soyle.stories.usecase.scene.character.involve.SourceAddedToCharacterInScene

fun interface SourceAddedToCharacterInSceneReceiver {
    suspend fun receiverSourceAddedToCharacterInScene(event: SourceAddedToCharacterInScene)
}