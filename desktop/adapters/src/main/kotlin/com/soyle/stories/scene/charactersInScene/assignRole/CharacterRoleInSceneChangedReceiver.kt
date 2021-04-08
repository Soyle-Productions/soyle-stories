package com.soyle.stories.scene.charactersInScene.assignRole

import com.soyle.stories.domain.scene.events.CharacterRoleInSceneChanged
import com.soyle.stories.domain.scene.events.CompoundEvent

interface CharacterRoleInSceneChangedReceiver {
    suspend fun receiveCharacterRolesInSceneChanged(event: CompoundEvent<CharacterRoleInSceneChanged>)
}