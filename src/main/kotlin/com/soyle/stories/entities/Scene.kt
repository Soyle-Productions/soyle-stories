package com.soyle.stories.entities

import com.soyle.stories.common.Entity
import java.util.*

class Scene(
  override val id: Id,
  val projectId: Project.Id,
  val name: String,
  val storyEventId: StoryEvent.Id,
  private val characterMotivations: Map<Character.Id, String?>
) : Entity<Scene.Id> {

	constructor(projectId: Project.Id, name: String, storyEventId: StoryEvent.Id) : this(Id(), projectId, name, storyEventId, mapOf())

	fun includesCharacter(characterId: Character.Id): Boolean
	{
		return characterMotivations.containsKey(characterId)
	}

	fun getMotivationForCharacter(characterId: Character.Id): String?
	{
		return characterMotivations[characterId]
	}

	private fun copy(
	  name: String = this.name,
	  characterMotivations: Map<Character.Id, String?> = this.characterMotivations
	) = Scene(id, projectId, name, storyEventId, characterMotivations)

	fun withName(newName: String) = copy(name = newName)
	fun withCharacterIncluded(characterId: Character.Id) = copy(characterMotivations = characterMotivations + (characterId to null))

	data class Id(val uuid: UUID = UUID.randomUUID()) {
		override fun toString(): String = "Scene($uuid)"
	}
}