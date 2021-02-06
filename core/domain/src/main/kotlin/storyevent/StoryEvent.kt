package com.soyle.stories.domain.storyevent

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.project.Project
import java.util.*

class StoryEvent(
	val id: Id,
	val name: String,
	val projectId: Project.Id,
	val previousStoryEventId: Id?,
	val nextStoryEventId: Id?,
	val linkedLocationId: Location.Id?,
	val includedCharacterIds: List<Character.Id>
) {

	constructor(name: String, projectId: Project.Id) : this(Id(), name, projectId, null, null, null, emptyList())

	private fun copy(
	  name: String = this.name,
	  previousStoryEventId: Id? = this.previousStoryEventId,
	  nextStoryEventId: Id? = this.nextStoryEventId,
	  linkedLocationId: Location.Id? = this.linkedLocationId,
	  includedCharacterIds: List<Character.Id> = this.includedCharacterIds
	) = StoryEvent(id, name, projectId, previousStoryEventId, nextStoryEventId, linkedLocationId, includedCharacterIds)

	fun withName(newName: String) = copy(name = newName)
	fun withPreviousId(storyEventId: Id?) = copy(previousStoryEventId = storyEventId)
	fun withNextId(storyEventId: Id?) = copy(nextStoryEventId = storyEventId)
	fun withLocationId(locationId: Location.Id?) = copy(linkedLocationId = locationId)
	fun withIncludedCharacterId(characterId: Character.Id) = copy(includedCharacterIds = includedCharacterIds + characterId)
	fun withoutCharacterId(characterId: Character.Id) = copy(includedCharacterIds = includedCharacterIds - characterId)

	data class Id(val uuid: UUID = UUID.randomUUID())
	{
		override fun toString(): String = "StoryEvent($uuid)"
	}

}