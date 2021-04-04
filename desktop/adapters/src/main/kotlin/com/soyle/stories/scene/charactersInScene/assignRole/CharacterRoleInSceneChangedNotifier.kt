package com.soyle.stories.scene.charactersInScene.assignRole

import com.soyle.stories.common.Notifier
import com.soyle.stories.domain.scene.events.CharacterRoleInSceneChanged

class CharacterRoleInSceneChangedNotifier : Notifier<CharacterRoleInSceneChangedReceiver>(),
    CharacterRoleInSceneChangedReceiver {
    override suspend fun receiveCharacterRoleInSceneChanged(event: CharacterRoleInSceneChanged) {
        notifyAll { it.receiveCharacterRoleInSceneChanged(event) }
    }
}