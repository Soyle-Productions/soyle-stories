package com.soyle.stories.storyevent.storyEventDetails

interface StoryEventDetailsViewListener {

	fun getValidState()
	fun deselectLocation()
	fun selectLocation(locationId: String)
	fun addCharacter(characterId: String)
	fun removeCharacter(characterId: String)

}