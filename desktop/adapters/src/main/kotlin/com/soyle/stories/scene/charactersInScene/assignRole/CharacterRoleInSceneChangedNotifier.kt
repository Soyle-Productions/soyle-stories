package com.soyle.stories.scene.charactersInScene.assignRole

import com.soyle.stories.common.Notifier
import com.soyle.stories.domain.scene.events.CharacterRoleInSceneChanged
import com.soyle.stories.domain.scene.events.CompoundEvent

class CharacterRoleInSceneChangedNotifier : Notifier<CharacterRoleInSceneChangedReceiver>(),
    CharacterRoleInSceneChangedReceiver {
    override suspend fun receiveCharacterRolesInSceneChanged(event: CompoundEvent<CharacterRoleInSceneChanged>) {
        notifyAll { it.receiveCharacterRolesInSceneChanged(event) }
    }
}