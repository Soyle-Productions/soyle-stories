package com.soyle.stories.scene.setMotivationForCharacterInScene

interface SetMotivationForCharacterInSceneController {

	fun clearMotivationForCharacter(sceneId: String, characterId: String)
	fun setMotivationForCharacter(sceneId: String, characterId: String, motivation: String)
}