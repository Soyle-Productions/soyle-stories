package com.soyle.stories.scene.charactersInScene.removeCharacterFromScene

import com.soyle.stories.common.Notifier
import com.soyle.stories.common.Receiver
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.domain.scene.character.events.CharacterRemovedFromScene
import com.soyle.stories.usecase.scene.character.removeCharacterFromScene.RemoveCharacterFromScene

class RemovedCharacterFromSceneNotifier : Notifier<Receiver<CharacterRemovedFromScene>>(), Receiver<CharacterRemovedFromScene> {

	override suspend fun receiveEvent(event: CharacterRemovedFromScene) {
		notifyAll { it.receiveEvent(event) }
	}

}