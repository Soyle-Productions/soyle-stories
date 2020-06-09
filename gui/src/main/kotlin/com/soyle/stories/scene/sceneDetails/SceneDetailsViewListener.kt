package com.soyle.stories.scene.sceneDetails

interface SceneDetailsViewListener {

	fun getValidState()
	fun addCharacter(storyEventId: String, characterId: String)

}