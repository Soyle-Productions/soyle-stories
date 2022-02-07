package com.soyle.stories.scene.charactersInScene.setMotivationForCharacterInScene

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.scene.Scene

interface SetMotivationForCharacterInSceneController {

	fun clearMotivationForCharacter(sceneId: Scene.Id, characterId: Character.Id)
	fun setMotivationForCharacter(sceneId: Scene.Id, characterId: Character.Id, motivation: String)
}