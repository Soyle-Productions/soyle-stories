package com.soyle.stories.scene.charactersInScene.involve

import com.soyle.stories.usecase.scene.character.involve.CharacterInvolvedInScene

fun interface CharacterInvolvedInSceneReceiver {
    suspend fun receiveCharacterInvolvedInScene(event: CharacterInvolvedInScene)
}