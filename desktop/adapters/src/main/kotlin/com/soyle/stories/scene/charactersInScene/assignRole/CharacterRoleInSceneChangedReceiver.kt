package com.soyle.stories.scene.charactersInScene.assignRole

import com.soyle.stories.domain.scene.character.events.CharacterRoleInSceneChanged
import com.soyle.stories.domain.scene.events.CompoundEvent

fun interface CharacterRoleInSceneChangedReceiver {
    suspend fun receiveCharacterRolesInSceneChanged(event: CompoundEvent<CharacterRoleInSceneChanged>)
}