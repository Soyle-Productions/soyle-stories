package com.soyle.stories.scene.charactersInScene.setDesire

import com.soyle.stories.domain.scene.events.CharacterDesireInSceneChanged

interface CharacterDesireInSceneChangedReceiver {
    suspend fun receiveCharacterDesireInSceneChanged(event: CharacterDesireInSceneChanged)
}