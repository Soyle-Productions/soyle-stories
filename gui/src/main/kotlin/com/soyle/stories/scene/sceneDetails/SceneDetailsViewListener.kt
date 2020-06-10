package com.soyle.stories.scene.sceneDetails

interface SceneDetailsViewListener {

	fun getValidState()
	fun linkLocation(locationId: String)
	fun addCharacter(storyEventId: String, characterId: String)
	fun removeCharacter(storyEventId: String, characterId: String)

}