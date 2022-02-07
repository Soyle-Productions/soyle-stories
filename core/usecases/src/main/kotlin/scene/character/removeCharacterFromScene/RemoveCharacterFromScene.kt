package com.soyle.stories.usecase.scene.character.removeCharacterFromScene

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.SceneLocale
import com.soyle.stories.domain.scene.character.events.CharacterRemovedFromScene
import java.util.*

interface RemoveCharacterFromScene {

	suspend operator fun invoke(sceneId: Scene.Id, characterId: Character.Id, output: OutputPort)

	class ResponseModel(val characterRemoved: CharacterRemovedFromScene)

	fun interface OutputPort {
		suspend fun characterRemovedFromScene(response: ResponseModel)
	}
}