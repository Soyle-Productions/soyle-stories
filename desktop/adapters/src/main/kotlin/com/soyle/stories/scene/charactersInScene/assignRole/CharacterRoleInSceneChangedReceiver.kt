package com.soyle.stories.scene.charactersInScene.assignRole

import com.soyle.stories.domain.scene.events.CharacterRoleInSceneChanged

interface CharacterRoleInSceneChangedReceiver {
    suspend fun receiveCharacterRoleInSceneChanged(event: CharacterRoleInSceneChanged)
}